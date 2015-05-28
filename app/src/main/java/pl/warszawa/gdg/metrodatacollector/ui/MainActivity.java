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
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.R;
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
        if(!FlagsLocal.runBackground) {
            AppMetroDataCollector.unregisterToCellEvent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppMetroDataCollector.registerToCellEvent();
    }

    @OnClick(R.id.switchBackgroundState)
    public void changeBackgroundState() {
        PhoneCellListener.reset();
        if(!FlagsLocal.runBackground) {
            AppMetroDataCollector.registerToCellEvent();
        } else {
            AppMetroDataCollector.unregisterToCellEvent();
        }
        FlagsLocal.runBackground = !FlagsLocal.runBackground;
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
}
