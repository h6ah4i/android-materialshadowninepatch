package com.h6ah4i.android.example.materialshadowninepatch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by hasegawa on 9/13/15.
 */
public class ProgrammaticallyAddDemoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ProgrammaticallyAddDemoFragment())
                    .commit();
        }
    }
}
