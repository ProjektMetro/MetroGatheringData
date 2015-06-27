package pl.warszawa.gdg.metrodatacollector.test;


import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;


import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.ui.ActivityAddNewPoint;


@RunWith(AndroidJUnit4.class)
public class ActivityAddNewPointTest
        extends ActivityInstrumentationTestCase2<ActivityAddNewPoint> {

    private ActivityAddNewPoint mActivityAddNewPoint;
    private AutoCompleteTextView mSelectStation;
    private Instrumentation mInstrumentation;
    private CheckBox mOutside;

    public ActivityAddNewPointTest(){
        super(ActivityAddNewPoint.class);

    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivityAddNewPoint = getActivity();
        mSelectStation = (AutoCompleteTextView) mActivityAddNewPoint.findViewById(R.id.textViewSelectStation);
        mInstrumentation =  getInstrumentation();
        mOutside = (CheckBox) mActivityAddNewPoint.findViewById(R.id.checkBoxOutside);


    }

    @Test
    public void test_preconditions() {
        assertNotNull(mActivityAddNewPoint);
        assertNotNull(mSelectStation);
        assertNotNull(mInstrumentation);
        assertNotNull(mOutside);
    }

    @Test
    public void test_check_select_station() {

        final Bundle outState = new Bundle();
        mActivityAddNewPoint.runOnUiThread(new Runnable(){

            public void run(){
                mSelectStation.setAdapter(null);
                mSelectStation.setText("GOOD");
                mInstrumentation.callActivityOnSaveInstanceState(mActivityAddNewPoint, outState);
                mSelectStation.setText("WRONG");
                mInstrumentation.callActivityOnRestoreInstanceState(mActivityAddNewPoint, outState);
            }
        });

        mInstrumentation.waitForIdleSync();

        assertEquals("GOOD", mSelectStation.getText().toString());

    }

    @Test
    public void test_check_outside() {
/* Dlaczego ten test przechodzi czy dla checkboxów jest jakieś wbudowane wsparcie ???*/
        final Bundle outState = new Bundle();
        mActivityAddNewPoint.runOnUiThread(new Runnable(){

            public void run(){

                mOutside.setChecked(true);
                mInstrumentation.callActivityOnSaveInstanceState(mActivityAddNewPoint, outState);
                mOutside.setChecked(false);
                mInstrumentation.callActivityOnRestoreInstanceState(mActivityAddNewPoint, outState);
            }
        });

        mInstrumentation.waitForIdleSync();
        assertTrue(mOutside.isChecked());

    }
}
