package pl.warszawa.gdg.metrodatacollector.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.location.PhoneCellListener;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;
import pl.warszawa.gdg.metrodatacollector.subway.Station;

public class ActivityAddNewPoint extends AppCompatActivity {
    private static final String TAG = ActivityAddNewPoint.class.getSimpleName();

    @InjectView(R.id.textViewSelectStation)
    AutoCompleteTextView selectStation;

    @InjectView(R.id.listViewNeighboringCells)
    ListView listViewNeighboringCells;

    @InjectView(R.id.currentCell)
    TextView currentCell;

    private String selectedStation;
    private List<String> stationList;
    private TelephonyManager telephonyManager;

    public static final String STOP_LISTENING = "Stop_listening";
    private Station stationToAdd;//Object that user fills information about

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_point);
        NotificationHelper.hideNotificationNewPlace(ActivityAddNewPoint.this);

        ButterKnife.inject(this);
        onNewIntent(getIntent());
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        setupMetroStationList();
        setupCurrentCell();
        setupNeighboringCellList();
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
     * @param towerInfo
     */
    public void onEvent(TowerInfo towerInfo) {
        //TODO Popup and clean views
        Log.d(TAG, "onEvent ");
    }

    private void setupMetroStationList() {
        ParseHelper.getAllStations(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<String> stationList = new ArrayList<String>();
                for (ParseObject parseObject : list) {
                    stationList.add(ParseHelper.getStation(parseObject).getName());
                }
                setupStationList(stationList);
            }
        });
    }

    private void setupCurrentCell() {
        GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
        currentCell.setText("" + cellLocation.getCid());
    }

    private void setupStationList(List<String> stationList) {
        this.stationList = stationList;
        selectStation.setAdapter(new StationAdapter(ActivityAddNewPoint.this, stationList));

        selectStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stationItemClicked(position);
            }
        });
    }

    private void stationItemClicked(int position) {
        selectedStation = stationList.get(position);
        selectStation.setError(null);
    }

    private void setupNeighboringCellList() {
        TextView header = new TextView(this);
        header.setGravity(Gravity.CENTER);
        header.setText("Neighboring Cells:");
        listViewNeighboringCells.addHeaderView(header);

        List<String> neighborStringList = getNeighbouringCellList();
        listViewNeighboringCells.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, neighborStringList));
    }

    private List<String> getNeighbouringCellList() {
        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
        if (allCellInfo != null && !allCellInfo.isEmpty()) {
            return transformAllCellInfo(allCellInfo);
        } else {
            return transformNeighboringCellInfo(telephonyManager.getNeighboringCellInfo());
        }
    }

    private List<String> transformAllCellInfo(List<CellInfo> allCellInfo) {
        return Lists.transform(allCellInfo, new Function<CellInfo, String>() {
            @Override
            public String apply(CellInfo input) {
                return getCellDescription(input);
            }
        });
    }

    private String getCellDescription(CellInfo ci) {
        if (ci instanceof CellInfoLte) {
            CellIdentityLte cellIdentity = ((CellInfoLte) ci).getCellIdentity();
            return "LTE" + "\n" +
                    "Ci: " + cellIdentity.getCi() + "\n" +
                    "Mnc: " + cellIdentity.getMnc() + "\n" +
                    "Pci: " + cellIdentity.getPci() + "\n" +
                    "Tac: " + cellIdentity.getTac();
        } else if (ci instanceof CellInfoGsm) {
            CellIdentityGsm cellIdentity = ((CellInfoGsm) ci).getCellIdentity();
            return "GSM" + "\n" +
                    "Cid: " + cellIdentity.getCid() + "\n" +
                    "Mnc: " + cellIdentity.getMnc() + "\n" +
                    "Lac: " + cellIdentity.getLac();
        } else if (ci instanceof CellInfoCdma) {
            CellIdentityCdma cellIdentity = ((CellInfoCdma) ci).getCellIdentity();
            return "CDMA" + "\n" +
                    "BaseId: " + cellIdentity.getBasestationId() + "\n" +
                    "NetworkId: " + cellIdentity.getNetworkId() + "\n" +
                    "SystemId: " + cellIdentity.getSystemId() + "\n" +
                    "Latitude: " + cellIdentity.getLatitude() + "\n" +
                    "Longitude: " + cellIdentity.getLongitude();
        } else if (ci instanceof CellInfoWcdma) {
            if (Build.VERSION.SDK_INT > 18) {
                CellIdentityWcdma cellIdentity = ((CellInfoWcdma) ci).getCellIdentity();
                return "WCDMA" + "\n" +
                        "Cid: " + cellIdentity.getCid() + "\n" +
                        "Mnc: " + cellIdentity.getMnc() + "\n" +
                        "Lac: " + cellIdentity.getLac();
            }
        }
        return "";
    }

    private List<String> transformNeighboringCellInfo(List<NeighboringCellInfo> neighboringCellInfo) {
        return Lists.transform(neighboringCellInfo, new Function<NeighboringCellInfo, String>() {
            @Override
            public String apply(NeighboringCellInfo input) {
                return "Cid: " + input.getCid() + "\n" +
                        "Type: " + input.getNetworkType() + "\n" +
                        "Psc: " + input.getPsc() + "\n" +
                        "Rssi: " + input.getRssi();
            }
        });
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
            if (Strings.isNullOrEmpty(selectedStation)) {
                selectStation.setError("Select station from list.");
            } else {
                //lets try to send it
                stationToAdd = new Station(selectedStation);
                if(stationToAdd != null && stationToAdd.getName() != null) {//TODO check if one BTS is set
                    //stationToAdd.updateParse();//and only then send it to Parse
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            @InjectView(android.R.id.text1)
            TextView text1;

            final private View view;

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
    public void onNewIntent(Intent intent){
        //TODO change this to Broadcast receiver
        String action = intent.getAction();
        if(action != null){
            if(STOP_LISTENING.equals(action)) {
                PhoneCellListener phoneCellListener = new PhoneCellListener(ActivityAddNewPoint.this);
                AppMetroDataCollector.telephonyManager.listen(phoneCellListener, PhoneStateListener.LISTEN_NONE);
                NotificationHelper.hideRunningNotification(ActivityAddNewPoint.this);
                System.exit(0);//No no pattern
            }
        }
    }
}
