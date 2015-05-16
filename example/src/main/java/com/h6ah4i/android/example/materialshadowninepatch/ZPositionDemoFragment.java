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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerView;

public class ZPositionDemoFragment
        extends Fragment
        implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private static final int SEEKBAR_MAX = 1000;
    private static final float MAX_ELEVATION = 9;

    private static final int[] mNativeShadowItemIds = new int[]{
            R.id.native_shadow_item_z0,
            R.id.native_shadow_item_z1,
            R.id.native_shadow_item_z2,
            R.id.native_shadow_item_z3,
            R.id.native_shadow_item_z4,
            R.id.native_shadow_item_z5,
            R.id.native_shadow_item_z6,
            R.id.native_shadow_item_z7,
            R.id.native_shadow_item_z8,
            R.id.native_shadow_item_z9,
    };
    private static final int[] mCompatShadowItemIds = new int[]{
            R.id.compat_shadow_item_z0,
            R.id.compat_shadow_item_z1,
            R.id.compat_shadow_item_z2,
            R.id.compat_shadow_item_z3,
            R.id.compat_shadow_item_z4,
            R.id.compat_shadow_item_z5,
            R.id.compat_shadow_item_z6,
            R.id.compat_shadow_item_z7,
            R.id.compat_shadow_item_z8,
            R.id.compat_shadow_item_z9,
    };
    private static final int[] mCompatShadowItemContainerIds = new int[]{
            R.id.compat_shadow_item_container_z0,
            R.id.compat_shadow_item_container_z1,
            R.id.compat_shadow_item_container_z2,
            R.id.compat_shadow_item_container_z3,
            R.id.compat_shadow_item_container_z4,
            R.id.compat_shadow_item_container_z5,
            R.id.compat_shadow_item_container_z6,
            R.id.compat_shadow_item_container_z7,
            R.id.compat_shadow_item_container_z8,
            R.id.compat_shadow_item_container_z9,
    };

    private View[] mNativeShadowItems;
    private View[] mCompatShadowItems;
    private MaterialShadowContainerView[] mCompatShadowItemContainers;
    private SeekBar mSeekBarElevation;
    private CheckBox mCheckBoxForceUseCompatMode;
    private float mDisplayDensity;

    public ZPositionDemoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_z_position_demo, container, false);

        mDisplayDensity = getResources().getDisplayMetrics().density;

        mSeekBarElevation = (SeekBar) rootView.findViewById(R.id.seekbar_elevation);
        mSeekBarElevation.setOnSeekBarChangeListener(this);
        mSeekBarElevation.setMax(SEEKBAR_MAX);

        mCheckBoxForceUseCompatMode = (CheckBox) (rootView.findViewById(R.id.checkbox_force_use_compat_mode));
        mCheckBoxForceUseCompatMode.setOnCheckedChangeListener(this);

        mNativeShadowItems = new View[mNativeShadowItemIds.length];
        for (int i = 0; i < mNativeShadowItemIds.length; i++) {
            mNativeShadowItems[i] = rootView.findViewById(mNativeShadowItemIds[i]);
        }

        mCompatShadowItems = new View[mCompatShadowItemIds.length];
        for (int i = 0; i < mCompatShadowItemIds.length; i++) {
            mCompatShadowItems[i] = rootView.findViewById(mCompatShadowItemIds[i]);
        }

        mCompatShadowItemContainers = new MaterialShadowContainerView[mCompatShadowItemContainerIds.length];
        for (int i = 0; i < mCompatShadowItemContainerIds.length; i++) {
            mCompatShadowItemContainers[i] = (MaterialShadowContainerView) rootView.findViewById(mCompatShadowItemContainerIds[i]);
        }

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        setForceCompatMode(mCheckBoxForceUseCompatMode.isChecked());
        setItemElevation(progressToElevation(mSeekBarElevation.getProgress()));
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkbox_force_use_compat_mode:
                setForceCompatMode(isChecked);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_elevation:
                if (fromUser) {
                    setItemElevation(progressToElevation(progress));
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private float progressToElevation(int progress) {
        return MAX_ELEVATION * mDisplayDensity * progress / SEEKBAR_MAX;
    }

    public void setItemElevation(float elevation) {
        for (View v : mNativeShadowItems) {
            ViewCompat.setElevation(v, elevation);
        }
        for (MaterialShadowContainerView v : mCompatShadowItemContainers) {
            v.setShadowElevation(elevation);
        }
    }

    public void setForceCompatMode(boolean forceCompatMode) {
        for (MaterialShadowContainerView v : mCompatShadowItemContainers) {
            v.setForceUseCompatShadow(forceCompatMode);
        }
    }
}
