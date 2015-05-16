package pl.warszawa.gdg.metrodatacollector.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

import com.parse.ParseAnalytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.location.PhoneCellListener;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;


public class MainActivity extends AppCompatActivity {
    private PhoneCellListener phoneCellListener;

    @InjectView(R.id.switchBackgroundState)
    Switch switchBackgroundState;

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
        phoneCellListener = new PhoneCellListener(MainActivity.this);
        NotificationHelper.showNotificationNewPlace("adsasd", MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        AppMetroDataCollector.telephonyManager.listen(phoneCellListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    /**
     * Tower has changed, update UI?
     * @param towerInfo
     */
    public void onEvent(TowerInfo towerInfo) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!FlagsLocal.runBackground) {
            unregisterToCellEvent();
        }
    }

    private void registerToCellEvent() {
        AppMetroDataCollector.telephonyManager.listen(phoneCellListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void unregisterToCellEvent() {
        AppMetroDataCollector.telephonyManager.listen(phoneCellListener, PhoneStateListener.LISTEN_NONE);
    }

    @OnClick(R.id.switchBackgroundState)
    public void changeBackgroundState() {
        FlagsLocal.runBackground = !FlagsLocal.runBackground;
        if(!FlagsLocal.runBackground) {
            registerToCellEvent();
        } else {
            unregisterToCellEvent();
        }
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
}
