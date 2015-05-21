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

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerView;
import com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerViewProperties;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ZPositionAnimationDemoFragment
        extends Fragment
        implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener {

    private float mDisplayDensity;
    private View mCompatShadowItem;
    private MaterialShadowContainerView mCompatShadowItemContainer;
    private CheckBox mCheckBoxForceUseCompatMode;

    public ZPositionAnimationDemoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_z_position_animation_demo, container, false);

        mDisplayDensity = getResources().getDisplayMetrics().density;

        mCompatShadowItem = rootView.findViewById(R.id.compat_shadow_item);
        mCompatShadowItem.setOnClickListener(this);

        mCompatShadowItemContainer = (MaterialShadowContainerView) (rootView.findViewById(R.id.compat_shadow_item_container));

        mCheckBoxForceUseCompatMode = (CheckBox) (rootView.findViewById(R.id.checkbox_force_use_compat_mode));
        mCheckBoxForceUseCompatMode.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == mCompatShadowItem) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    mCompatShadowItemContainer,
                    MaterialShadowContainerViewProperties.SHADOW_TRANSLATION_Z,
                    mDisplayDensity * 0.0f, mDisplayDensity * 8.0f);
            animator.setDuration(500);
            animator.setInterpolator(new CycleInterpolator(0.5f));
            animator.start();
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

    public void setForceCompatMode(boolean forceCompatMode) {
        mCompatShadowItemContainer.setForceUseCompatShadow(forceCompatMode);
    }
}
