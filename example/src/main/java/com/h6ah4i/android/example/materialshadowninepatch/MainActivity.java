/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.example.materialshadowninepatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button_z_position_demo).setOnClickListener(this);
        findViewById(R.id.button_xy_position_demo).setOnClickListener(this);
        findViewById(R.id.button_z_position_animation_demo).setOnClickListener(this);
        findViewById(R.id.button_shadow_styles_demo).setOnClickListener(this);
        findViewById(R.id.button_programmatically_add_demo).setOnClickListener(this);

        /** {@link android.util.Property} is only available on API level 14 or later */
        findViewById(R.id.button_z_position_animation_demo).setEnabled(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_z_position_demo:
                launchActivity(ZPositionDemoActivity.class);
                break;
            case R.id.button_xy_position_demo:
                launchActivity(XYPositionDemoActivity.class);
                break;
            case R.id.button_z_position_animation_demo:
                launchActivity(ZPositionAnimationDemoActivity.class);
                break;
            case R.id.button_shadow_styles_demo:
                launchActivity(ShadowStylesDemoActivity.class);
                break;
            case R.id.button_programmatically_add_demo:
                launchActivity(ProgrammaticallyAddDemoActivity.class);
                break;
            default:
                break;
        }
    }

    private void launchActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
