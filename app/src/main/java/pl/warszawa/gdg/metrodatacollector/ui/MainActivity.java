package pl.warszawa.gdg.metrodatacollector.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

import com.parse.ParseAnalytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.location.NetworkLocation;
import pl.warszawa.gdg.metrodatacollector.location.PhoneCellListener;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;

public class MainActivity extends AppCompatActivity {

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
        PhoneCellListener.reset();
        FlagsLocal.runBackground = !switchBackgroundState.isChecked();
        if(!FlagsLocal.runBackground) {
            NetworkLocation.registerToCellEvent();
        } else {
            NetworkLocation.unregisterToCellEvent();
        }
    }

    @OnClick(R.id.switchNewPlace)
    public void changeNewPlaceState() {
        FlagsLocal.showNotificationInfo = !FlagsLocal.showNotificationInfo;
    }

    @OnClick(R.id.buttonAddPlace)
    public void addPlaceClicked() {
        startActivity(new Intent(this, ActivityAddNewPoint.class));
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
