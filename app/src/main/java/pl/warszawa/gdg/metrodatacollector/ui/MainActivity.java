package pl.warszawa.gdg.metrodatacollector.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.data.busEvents.BackgroundServiceStop;
import pl.warszawa.gdg.metrodatacollector.location.CellMonitorReceiver;
import pl.warszawa.gdg.metrodatacollector.location.CellMonitorService;
import pl.warszawa.gdg.metrodatacollector.location.NetworkLocation;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;
import pl.warszawa.gdg.metrodatacollector.subway.Station;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.switchBackgroundState)
    Switch switchBackgroundState;

    @InjectView(R.id.buttonCheckLocation)
    Button buttonCheckLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Track events to Parse
        if(FlagsLocal.parseEnabled) {
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        if(savedInstanceState != null) {
            //TODO restore previous state
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Tower has changed, update UI?
     * @param towerInfo
     */
    public void onEvent(TowerInfo towerInfo) {

    }

    /**
     * CallMonitor receiver and service has stopped - no more background updates
     * */
    public void onEvent(BackgroundServiceStop backgroundServiceStop) {
        if(switchBackgroundState != null && switchBackgroundState.isChecked()) {
            switchBackgroundState.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FlagsLocal.save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlagsLocal.read();
    }

    @OnClick(R.id.switchBackgroundState)
    public void changeBackgroundState() {
        CellMonitorService.reset();
        FlagsLocal.runBackground = !switchBackgroundState.isChecked();
        if(!FlagsLocal.runBackground) {
            CellMonitorReceiver.scheduleGsmMonitor(MainActivity.this);
        } else {
            CellMonitorReceiver.stopGsmMonitor(MainActivity.this);
        }
    }

    @OnClick(R.id.buttonCheckLocation)
    public void checkLocation() {
        TowerInfo tower = NetworkLocation.getCurrentTower(MainActivity.this);
        if(tower == null) {
            Toast.makeText(MainActivity.this, "Problem with GSM", Toast.LENGTH_SHORT).show();
            return;
        }
        buttonCheckLocation.setEnabled(false);
        buttonCheckLocation.setText("Checking...");
        ParseHelper.getStation(tower.getUniqueId(), tower.getMnc(), new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(buttonCheckLocation != null) {
                    buttonCheckLocation.setEnabled(true);
                    buttonCheckLocation.setText("CHECK LOCATION");
                }
                if (list != null && list.size() > 0 && list.get(0) != null) {
                    Station station = ParseHelper.getStation(list.get(0));
                    if (station != null && station.getName() != null) {
                        //check if place has actually changed - as can be 2g->3g in same place
                        Log.d(TAG, "checkLocation :" + station);
                        Toast.makeText(MainActivity.this, "Station: " + station.getName(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "checkLocation station is null!");
                        Toast.makeText(MainActivity.this, "Station not know", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "checkLocation station is null!");
                    Toast.makeText(MainActivity.this, "Station not know", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.switchNewPlace)
    public void changeNewPlaceState() {
        FlagsLocal.showNotificationInfo = !FlagsLocal.showNotificationInfo;
        NotificationHelper.hideNotificationNewPlace(MainActivity.this);
    }

    @OnClick(R.id.switchAutorun)
    public void changeAutorunState() {
        //Turn on/off geofencing
        //TODO
    }

    @OnClick(R.id.buttonAddPlace)
    public void addPlaceClicked() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        startActivity(new Intent(MainActivity.this, ActivityAddNewPoint.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you inside metro?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //TODO save values
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //TODO read values
    }
}
