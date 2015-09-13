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

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerView;

public class ProgrammaticallyAddDemoFragment
        extends Fragment
        implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener {

    private CheckBox mCheckBoxForceUseCompatMode;
    private LinearLayout mItemsContainer;
    private Button mAddButton;
    private Button mClearButton;

    public ProgrammaticallyAddDemoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_programmatically_add_demo, container, false);

        mCheckBoxForceUseCompatMode = (CheckBox) (rootView.findViewById(R.id.checkbox_force_use_compat_mode));
        mItemsContainer = (LinearLayout) (rootView.findViewById(R.id.items_container));
        mAddButton = (Button) (rootView.findViewById(R.id.button_add));
        mClearButton = (Button) (rootView.findViewById(R.id.button_clear));

        mCheckBoxForceUseCompatMode.setOnCheckedChangeListener(this);
        mAddButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                addItem();
                break;
            case R.id.button_clear:
                clearItems();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkbox_force_use_compat_mode:
                setForceCompatMode(isChecked);
                break;
        }
    }

    private void addItem() {
        LinearLayout parent = mItemsContainer;
        Context context = parent.getContext();
        float density = context.getResources().getDisplayMetrics().density;

        AppCompatTextView itemView = new AppCompatTextView(context);

        // Need to specify type of the shadow. Specify one of the following to the fourth parameter.
        //
        // - R.style.ms9_DefaultShadowStyle[Z6|Z9|Z18]
        // - R.style.ms9_DefaultShadowStyle[Z6|Z9|Z18]CompatOnly
        // - R.style.ms9_NoDisplayedPositionAffectShadowStyle[Z6|Z9|Z18]
        // - R.style.ms9_NoDisplayedPositionAffectShadowStyle[Z6|Z9|Z18]CompatOnly
        // - R.style.ms9_CompositeShadowStyle[Z6|Z9|Z18]
        // - R.style.ms9_CompositeShadowStyle[Z6|Z9|Z18]CompatOnly
        MaterialShadowContainerView shadowView = new MaterialShadowContainerView(
                context, null, 0, R.style.ms9_DefaultShadowStyle);


        // Setup the itemView
        {
            MaterialShadowContainerView.LayoutParams layoutParams =
                    new MaterialShadowContainerView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, (int) (density * 56));

            // Need to set background color and margins
            itemView.setBackgroundColor(Color.WHITE);
            layoutParams.setMargins(
                    (int) (8 * density), (int) (8 * density),
                    (int) (8 * density), (int) (8 * density));
            itemView.setLayoutParams(layoutParams);

            itemView.setText("Item " + (parent.getChildCount() + 1));
            itemView.setGravity(Gravity.CENTER);

        }

        // Setup the shadowView
        {
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            shadowView.setLayoutParams(layoutParams);

            shadowView.setShadowElevation(density * 4);
            shadowView.setForceUseCompatShadow(mCheckBoxForceUseCompatMode.isChecked());
        }

        // Add views
        shadowView.addView(itemView);
        parent.addView(shadowView);
    }

    private void clearItems() {
        mItemsContainer.removeAllViews();
    }

    public void setForceCompatMode(boolean forceCompatMode) {
        int n = mItemsContainer.getChildCount();

        for (int i = 0; i < n; i++) {
            MaterialShadowContainerView v = (MaterialShadowContainerView) mItemsContainer.getChildAt(i);
            v.setForceUseCompatShadow(forceCompatMode);
        }
    }
}
