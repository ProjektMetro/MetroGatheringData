package pl.warszawa.gdg.metrodatacollector.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.location.CellMonitorReceiver;
import pl.warszawa.gdg.metrodatacollector.location.NetworkLocation;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;
import pl.warszawa.gdg.metrodatacollector.subway.Station;

public class ActivityAddNewPoint extends AppCompatActivity {
    public static final String STOP_LISTENING = "Stop_listening";
    private static final String TAG = ActivityAddNewPoint.class.getSimpleName();

    @InjectView(R.id.textViewSelectStation)
    AutoCompleteTextView selectStation;

    @InjectView(R.id.checkBoxOutside)
    CheckBox outside;

    private String selectedStation;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_point);
        NotificationHelper.hideNotificationNewPlace(ActivityAddNewPoint.this);

        ButterKnife.inject(this);
        onNewIntent(getIntent());

        setupMetroStationView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    /**
     * GSM Tower has changed
     *
     * @param towerInfo
     */
    public void onEvent(TowerInfo towerInfo) {
        if (this.alert != null) {
            this.alert.dismiss();
        }
        this.alert = getOnTowerInfoEventAlertDialog();
        this.alert.show();
    }

    private AlertDialog getOnTowerInfoEventAlertDialog() {
        return new AlertDialog.Builder(this)
                .setMessage("Zmieniłeś swoją pozycję.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedStation = "";
                        selectStation.setText("");
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                })
                .create();
    }

    private void setupMetroStationView() {
        ParseHelper.getAllStations(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<String> stationList = new ArrayList<String>();
                for (ParseObject parseObject : list) {
                    stationList.add(ParseHelper.getStation(parseObject).getName());
                }
                setupMetroStationView(stationList);
            }
        });
    }

    private void setupMetroStationView(List<String> stationList) {
        selectStation.setAdapter(new StationAdapter(ActivityAddNewPoint.this, stationList));

        selectStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stationListItemClicked(((TextView) view).getText().toString());
            }
        });
    }

    private void stationListItemClicked(String station) {
        selectedStation = station;
        selectStation.setError(null);
    }

    @OnTextChanged(R.id.textViewSelectStation)
    public void onStationNameTextChanged() {
        selectedStation = null;
        selectStation.setError(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_add_new_point, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_save) {
            onSaveOptionSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSaveOptionSelected() {
        if (Strings.isNullOrEmpty(selectedStation)) {
            selectStation.setError("Select station from list.");
        } else {
            final TowerInfo towerInfo = NetworkLocation.getCurrentTower(ActivityAddNewPoint.this);
            if (towerInfo == null) {
                Toast.makeText(ActivityAddNewPoint.this, "Problem with GSM module", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseHelper.getStation(towerInfo.getUniqueId(), towerInfo.getMnc(), new FindCallback<ParseObject>() {
                @Override
                public void done(List list, ParseException e) {
                    if (list != null && list.size() > 0) {
                        //there is station already
                        Log.d(TAG, "done station exists");
                    } else {
                        final Station.Builder builder = new Station.Builder(selectedStation);
                        builder.gsm(towerInfo, outside.isChecked());

                        final Station station = builder.build();
                        station.updateParse(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(ActivityAddNewPoint.this, "Saved " + station.getName(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "save station, error: " + e.getLocalizedMessage());
                                    Toast.makeText(ActivityAddNewPoint.this, "Not saved: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        //TODO change this to Broadcast receiver
        String action = intent.getAction();
        if (action != null) {
            if (STOP_LISTENING.equals(action)) {
                CellMonitorReceiver.stopGsmMonitor(ActivityAddNewPoint.this);
                System.exit(0);//No no pattern
            }
        }
    }

    static class StationAdapter extends BaseAdapter implements Filterable {
        private Context context;
        private List<String> stations;
        private List<String> filtered;
        private StationFilter stationFilter;

        public StationAdapter(Context context, List<String> stations) {
            this.context = context;
            this.stations = stations;
            this.filtered = Lists.newArrayList();
            this.stationFilter = new StationFilter();
        }

        @Override
        public Filter getFilter() {
            return stationFilter;
        }

        @Override
        public int getCount() {
            return filtered.size();
        }

        @Override
        public String getItem(int position) {
            return filtered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = getViewHolder(convertView, parent);
            holder.text1.setText(getItem(position));
            return holder.getView();
        }

        private ViewHolder getViewHolder(View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(android.R.layout.simple_spinner_item, parent, false);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
                return holder;
            } else {
                return (ViewHolder) convertView.getTag();
            }
        }

        static class ViewHolder {
            final private View view;

            @InjectView(android.R.id.text1)
            TextView text1;

            public ViewHolder(View view) {
                this.view = view;
                ButterKnife.inject(this, view);
            }

            public View getView() {
                return view;
            }
        }

        private class StationFilter extends Filter {

            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {

                List<String> filtered = Lists.newArrayList(Iterables.filter(stations, new Predicate<String>() {
                    @Override
                    public boolean apply(String input) {
                        return input.toLowerCase().startsWith(constraint.toString().toLowerCase());
                    }
                }));

                FilterResults results = new FilterResults();
                results.values = filtered;
                results.count = filtered.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filtered.clear();
                if (results.values != null) {
                    filtered.addAll((List) results.values);
                }
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("STATION", selectStation.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        selectStation.setText(savedInstanceState.getString("STATION"));

    }
}
