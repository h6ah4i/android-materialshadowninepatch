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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerView;

public class XYPositionDemoFragment
        extends Fragment
        implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private static final int SEEKBAR_MAX = 1000;
    private static final float MAX_Z_TRANSLATION = 9;

    private static final int[] mCompatShadowItemIds = new int[]{
            R.id.compat_shadow_item_top_left,
            R.id.compat_shadow_item_top_center,
            R.id.compat_shadow_item_top_right,
            R.id.compat_shadow_item_center_left,
            R.id.compat_shadow_item_center_center,
            R.id.compat_shadow_item_center_right,
            R.id.compat_shadow_item_bottom_left,
            R.id.compat_shadow_item_bottom_center,
            R.id.compat_shadow_item_bottom_right,
    };
    private static final int[] mCompatShadowItemContainerIds = new int[]{
            R.id.compat_shadow_item_container_top_left,
            R.id.compat_shadow_item_container_top_center,
            R.id.compat_shadow_item_container_top_right,
            R.id.compat_shadow_item_container_center_left,
            R.id.compat_shadow_item_container_center_center,
            R.id.compat_shadow_item_container_center_right,
            R.id.compat_shadow_item_container_bottom_left,
            R.id.compat_shadow_item_container_bottom_center,
            R.id.compat_shadow_item_container_bottom_right,
    };

    private View[] mCompatShadowItems;
    private MaterialShadowContainerView[] mCompatShadowItemContainers;
    private SeekBar mSeekBarTranslationZ;
    private CheckBox mCheckBoxForceUseCompatMode;
    private CheckBox mCheckBoxEnableDisplayedPositionAffection;
    private float mDisplayDensity;

    public XYPositionDemoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_xy_position_demo, container, false);

        mDisplayDensity = getResources().getDisplayMetrics().density;

        mSeekBarTranslationZ = (SeekBar) rootView.findViewById(R.id.seekbar_translation_z);
        mSeekBarTranslationZ.setOnSeekBarChangeListener(this);
        mSeekBarTranslationZ.setMax(SEEKBAR_MAX);

        mCheckBoxForceUseCompatMode = (CheckBox) (rootView.findViewById(R.id.checkbox_force_use_compat_mode));
        mCheckBoxForceUseCompatMode.setOnCheckedChangeListener(this);

        mCheckBoxEnableDisplayedPositionAffection = (CheckBox) (rootView.findViewById(R.id.checkbox_displayed_position_affection));
        mCheckBoxEnableDisplayedPositionAffection.setOnCheckedChangeListener(this);

        mCompatShadowItems = new View[mCompatShadowItemIds.length];
        for (int i = 0; i < mCompatShadowItemIds.length; i++) {
            mCompatShadowItems[i] = rootView.findViewById(mCompatShadowItemIds[i]);
        }

        mCompatShadowItemContainers = new MaterialShadowContainerView[mCompatShadowItemContainerIds.length];
        for (int i = 0; i < mCompatShadowItemContainerIds.length; i++) {
            mCompatShadowItemContainers[i] = (MaterialShadowContainerView) rootView.findViewById(mCompatShadowItemContainerIds[i]);
        }

        if (savedInstanceState == null) {
            mCheckBoxForceUseCompatMode.setChecked(true);
            mCheckBoxEnableDisplayedPositionAffection.setChecked(true);
            mSeekBarTranslationZ.setProgress(500);

            setForceCompatMode(mCheckBoxForceUseCompatMode.isChecked());
            setDisplayedPositionAffection(mCheckBoxEnableDisplayedPositionAffection.isChecked());
            setItemsTranslationZ(progressToTranslationZAmount(mSeekBarTranslationZ.getProgress()));
        }

        return rootView;
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
            case R.id.checkbox_displayed_position_affection:
                setDisplayedPositionAffection(isChecked);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_translation_z:
                if (fromUser) {
                    setItemsTranslationZ(progressToTranslationZAmount(progress));
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

    private float progressToTranslationZAmount(int progress) {
        return MAX_Z_TRANSLATION * mDisplayDensity * progress / SEEKBAR_MAX;
    }

    public void setItemsTranslationZ(float amount) {
        for (MaterialShadowContainerView v : mCompatShadowItemContainers) {
            v.setShadowTranslationZ(amount);
        }
    }

    public void setForceCompatMode(boolean forceCompatMode) {
        for (MaterialShadowContainerView v : mCompatShadowItemContainers) {
            v.setForceUseCompatShadow(forceCompatMode);
        }
    }

    public void setDisplayedPositionAffection(boolean enabled) {
        for (MaterialShadowContainerView v : mCompatShadowItemContainers) {
            v.setDisplayedPositionAffectionEnabled(enabled);
        }
    }
}
