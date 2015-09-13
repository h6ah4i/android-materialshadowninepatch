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

package com.h6ah4i.android.materialshadowninepatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

public class MaterialShadowContainerView extends FrameLayout {
    private static final String TAG = "ShadowContainerView";

    private static final float SPOT_SHADOW_X_TRANSLATION_AMOUNT_COEFFICIENT = 0.0002f;
    private static final float SPOT_SHADOW_Y_TRANSLATION_AMOUNT_COEFFICIENT = 0.002f;
    private static final float NON_POSITION_AWARE_SPOT_SHADOW_Y_TRANSLATION_AMOUNT_COEFFICIENT = 0.2f;

    private float mDisplayDensity;
    private float mInvDisplayDensity;
    private int mLightPositionX;
    private int mLightPositionY;
    private int mSpotShadowTranslationX;
    private int mSpotShadowTranslationY;

    private float mShadowTranslationZ = 0;
    private float mShadowElevation = 0;
    private boolean mAffectsDisplayedPosition = true;
    private boolean mForceUseCompatShadow = false;

    private boolean mUseAmbientShadow = true;
    private boolean mUseSpotShadow = true;

    private int[] mSpotShadowResourcesIdList;
    private int[] mAmbientShadowResourcesIdList;

    private int mMaxSpotShadowLevel;
    private int mMaxAmbientShadowLevel;

    private int mCurrentSpotShadowDrawable1ResId;
    private NinePatchDrawable mCurrentSpotShadowDrawable1;
    private int mCurrentSpotShadowDrawable2ResId;
    private NinePatchDrawable mCurrentSpotShadowDrawable2;
    private int mCurrentAmbientShadowDrawable1ResId;
    private NinePatchDrawable mCurrentAmbientShadowDrawable1;
    private int mCurrentAmbientShadowDrawable2ResId;
    private NinePatchDrawable mCurrentAmbientShadowDrawable2;

    private Rect mTempRect = new Rect();
    private int[] mTmpLocations = new int[2];

    public MaterialShadowContainerView(Context context) {
        this(context, null, 0);
    }

    public MaterialShadowContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialShadowContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaterialShadowContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ms9_MaterialShadowContainerView, defStyleAttr, defStyleRes);
        final float shadowTranslationZ = ta.getDimension(R.styleable.ms9_MaterialShadowContainerView_ms9_shadowTranslationZ, mShadowTranslationZ);
        final float shadowElevation = ta.getDimension(R.styleable.ms9_MaterialShadowContainerView_ms9_shadowElevation, mShadowElevation);
        final int spotShadowLevelListResId = ta.getResourceId(R.styleable.ms9_MaterialShadowContainerView_ms9_spotShadowDrawablesList, 0);
        final int ambientShadowLevelListResId = ta.getResourceId(R.styleable.ms9_MaterialShadowContainerView_ms9_ambientShadowDrawablesList, 0);
        final boolean forceUseCompatShadow = ta.getBoolean(R.styleable.ms9_MaterialShadowContainerView_ms9_forceUseCompatShadow, mForceUseCompatShadow);
        final boolean affectsXYPosition = ta.getBoolean(R.styleable.ms9_MaterialShadowContainerView_ms9_affectsDisplayedPosition, mAffectsDisplayedPosition);
        final boolean useAmbientShadow = ta.getBoolean(R.styleable.ms9_MaterialShadowContainerView_ms9_useAmbientShadow, mUseAmbientShadow);
        final boolean useSpotShadow = ta.getBoolean(R.styleable.ms9_MaterialShadowContainerView_ms9_useSpotShadow, mUseSpotShadow);
        ta.recycle();

        mSpotShadowResourcesIdList = getResourceIdArray(getResources(), spotShadowLevelListResId);
        mAmbientShadowResourcesIdList = getResourceIdArray(getResources(), ambientShadowLevelListResId);

        mMaxSpotShadowLevel = getMaxShadowLevel(mSpotShadowResourcesIdList);
        mMaxAmbientShadowLevel = getMaxShadowLevel(mAmbientShadowResourcesIdList);

        mDisplayDensity = getResources().getDisplayMetrics().density;
        mInvDisplayDensity = 1.0f / mDisplayDensity;
        mShadowTranslationZ = shadowTranslationZ;
        mShadowElevation = shadowElevation;
        mForceUseCompatShadow = forceUseCompatShadow;
        mAffectsDisplayedPosition = affectsXYPosition;
        mUseAmbientShadow = useAmbientShadow;
        mUseSpotShadow = useSpotShadow;

        updateShadowLevel(true);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState s = new SavedState(superState);

        s.shadowElevation = mShadowElevation;
        s.shadowTranslationZ = mShadowTranslationZ;
        s.affectsDisplayedPosition = mAffectsDisplayedPosition;
        s.forceUseCompatShadow = mForceUseCompatShadow;
        s.useAmbientShadow = mUseAmbientShadow;
        s.useSpotShadow = mUseSpotShadow;

        return s;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState s = (SavedState) state;

        super.onRestoreInstanceState(s.getSuperState());

        mShadowElevation = s.shadowElevation;
        mShadowTranslationZ = s.shadowTranslationZ;
        mAffectsDisplayedPosition = s.affectsDisplayedPosition;
        mForceUseCompatShadow = s.forceUseCompatShadow;
        mUseAmbientShadow = s.useAmbientShadow;
        mUseSpotShadow = s.useSpotShadow;

        updateShadowLevel(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((getChildCount() > 0) && (getChildAt(0).getVisibility() == View.VISIBLE)) {
            if (mUseAmbientShadow) {
                if (mCurrentAmbientShadowDrawable1 != null) {
                    mCurrentAmbientShadowDrawable1.draw(canvas);
                }
                if (mCurrentAmbientShadowDrawable2 != null) {
                    mCurrentAmbientShadowDrawable2.draw(canvas);
                }
            }

            if (mUseSpotShadow && (mCurrentSpotShadowDrawable1 != null || mCurrentSpotShadowDrawable2 != null)) {
                final int savedCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);

                canvas.translate(mSpotShadowTranslationX, mSpotShadowTranslationY);

                if (mCurrentSpotShadowDrawable1 != null) {
                    mCurrentSpotShadowDrawable1.draw(canvas);
                }

                if (mCurrentSpotShadowDrawable2 != null) {
                    mCurrentSpotShadowDrawable2.draw(canvas);
                }

                canvas.restoreToCount(savedCount);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateShadowDrawableBounds();
        updateSpotShadowPosition();
    }

    public void setShadowTranslationZ(float translationZ) {
        if (mShadowTranslationZ == translationZ) {
            return;
        }

        mShadowTranslationZ = translationZ;

        updateShadowLevel(false);
    }

    public float getShadowTranslationZ() {
        return mShadowTranslationZ;
    }

    public void setShadowElevation(float elevation) {
        if (mShadowElevation == elevation) {
            return;
        }

        mShadowElevation = elevation;

        updateShadowLevel(false);
    }

    public float getShadowElevation() {
        return mShadowElevation;
    }

    public void setDisplayedPositionAffectionEnabled(boolean enabled) {
        if (mAffectsDisplayedPosition == enabled) {
            return;
        }
        mAffectsDisplayedPosition = enabled;
        if (useCompatShadow()) {
            updateShadowLevel(true);
        }
    }

    public boolean isDisplayedPositionAffectionEnabled() {
        return mAffectsDisplayedPosition;
    }

    public void setForceUseCompatShadow(boolean forceUseCompatShadow) {
        if (mForceUseCompatShadow == forceUseCompatShadow) {
            return;
        }

        final boolean prevUseCompatShadow = useCompatShadow();

        mForceUseCompatShadow = forceUseCompatShadow;

        final boolean curUseCompatShadow = useCompatShadow();

        if (prevUseCompatShadow != curUseCompatShadow) {
            // disable native shadow
            if (curUseCompatShadow && supportsNativeShadow()) {
                updateShadowLevelNative(0.0f, 0.0f, true);
            }

            // apply
            updateShadowLevel(true);
        }
    }

    public boolean useCompatShadow() {
        if (!supportsNativeShadow()) {
            return true;
        } else {
            return mForceUseCompatShadow;
        }
    }

    public boolean useAmbientShadow() {
        return mUseAmbientShadow;
    }

    public void setUseAmbientShadow(boolean useAmbientShadow) {
        if (mUseAmbientShadow == useAmbientShadow) {
            return;
        }

        mUseAmbientShadow = useAmbientShadow;

        // invalidate
        if (!updateWillNotDraw()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean useSpotShadow() {
        return mUseSpotShadow;
    }

    public void setUseSpotShadow(boolean useSpotShadow) {
        if (mUseSpotShadow == useSpotShadow) {
            return;
        }

        mUseSpotShadow = useSpotShadow;

        // invalidate
        if (!updateWillNotDraw()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public static boolean supportsNativeShadow() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    private static int getMaxShadowLevel(int[] shadowDrawableResIds) {
        return (shadowDrawableResIds != null) ? Math.max(0, shadowDrawableResIds.length - 1) : 0;
    }

    private NinePatchDrawable getNinePatchDrawableFromResource(int resId) {
        final Drawable drawable = (resId != 0) ? getResources().getDrawable(resId) : null;

        if (drawable instanceof NinePatchDrawable) {
            return (NinePatchDrawable) drawable;
        } else {
            return null;
        }
    }

    private void updateShadowLevelCompat(float translationZ, float elevation, boolean force) {
        final float floatLevel = Math.max((translationZ + elevation) * mInvDisplayDensity, 0.0f);
        final int intLevel = (int) floatLevel;
        final int spotLevel1 = Math.min(intLevel, mMaxSpotShadowLevel);
        final int spotLevel2 = Math.min(intLevel + 1, mMaxSpotShadowLevel);
        final int ambientLevel1 = Math.min(intLevel, mMaxAmbientShadowLevel);
        final int ambientLevel2 = Math.min(intLevel + 1, mMaxAmbientShadowLevel);

        // update drawable
        final int spotShadow1ResId = (mSpotShadowResourcesIdList != null) ? mSpotShadowResourcesIdList[spotLevel1] : 0;
        final int spotShadow2ResId = (mSpotShadowResourcesIdList != null) ? mSpotShadowResourcesIdList[spotLevel2] : 0;
        final int ambientShadow1ResId = (mAmbientShadowResourcesIdList != null) ? mAmbientShadowResourcesIdList[ambientLevel1] : 0;
        final int ambientShadow2ResId = (mAmbientShadowResourcesIdList != null) ? mAmbientShadowResourcesIdList[ambientLevel2] : 0;

        if (force ||
                spotShadow1ResId != mCurrentSpotShadowDrawable1ResId ||
                spotShadow2ResId != mCurrentSpotShadowDrawable2ResId ||
                ambientShadow1ResId != mCurrentAmbientShadowDrawable1ResId ||
                ambientShadow2ResId != mCurrentAmbientShadowDrawable2ResId) {

            if (spotShadow1ResId != mCurrentSpotShadowDrawable1ResId) {
                mCurrentSpotShadowDrawable1 = getNinePatchDrawableFromResource(spotShadow1ResId);
                mCurrentSpotShadowDrawable1ResId = spotShadow1ResId;
            }

            if (spotShadow2ResId != mCurrentSpotShadowDrawable2ResId) {
                mCurrentSpotShadowDrawable2 = (spotShadow2ResId == spotShadow1ResId) ? null : getNinePatchDrawableFromResource(spotShadow2ResId);
                mCurrentSpotShadowDrawable2ResId = (spotShadow2ResId == spotShadow1ResId) ? 0 : spotShadow2ResId;
            }

            if (ambientShadow1ResId != mCurrentAmbientShadowDrawable1ResId) {
                mCurrentAmbientShadowDrawable1 = getNinePatchDrawableFromResource(ambientShadow1ResId);
                mCurrentAmbientShadowDrawable1ResId = ambientShadow1ResId;
            }

            if (ambientShadow2ResId != mCurrentAmbientShadowDrawable2ResId) {
                mCurrentAmbientShadowDrawable2 = (ambientShadow2ResId == ambientShadow1ResId) ? null : getNinePatchDrawableFromResource(ambientShadow2ResId);
                mCurrentAmbientShadowDrawable2ResId = (ambientShadow2ResId == ambientShadow1ResId) ? 0 : ambientShadow2ResId;
            }
            updateShadowDrawableBounds();
            updateSpotShadowPosition();

            updateWillNotDraw();
        }

        // update alpha
        final int alpha1 = 255 - Math.min(Math.max((int) ((floatLevel - intLevel) * 255 + 0.5f), 0), 255);
        final int alpha2 = 255 - alpha1;

        if (mCurrentSpotShadowDrawable1 != null) {
            if (mCurrentSpotShadowDrawable2 != null) {
                mCurrentSpotShadowDrawable1.setAlpha(alpha1);
            } else {
                mCurrentSpotShadowDrawable1.setAlpha(255);
            }
        }

        if (mCurrentSpotShadowDrawable2 != null) {
            mCurrentSpotShadowDrawable2.setAlpha(alpha2);
        }

        if (mCurrentAmbientShadowDrawable1 != null) {
            if (mCurrentAmbientShadowDrawable2 != null) {
                mCurrentAmbientShadowDrawable1.setAlpha(alpha1);
            } else {
                mCurrentAmbientShadowDrawable1.setAlpha(255);
            }
        }

        if (mCurrentAmbientShadowDrawable2 != null) {
            mCurrentAmbientShadowDrawable2.setAlpha(alpha2);
        }

        // invalidate
        if (!willNotDraw()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void updateShadowLevel(boolean force) {
        if (useCompatShadow()) {
            updateShadowLevelCompat(mShadowTranslationZ, mShadowElevation, force);
        } else {
            updateShadowLevelNative(mShadowTranslationZ, mShadowElevation, force);
        }
    }

    private void updateShadowLevelNative(float translationZ, float elevation, boolean force) {
        if (force) {
            mCurrentSpotShadowDrawable1 = null;
            mCurrentSpotShadowDrawable1ResId = 0;
            mCurrentSpotShadowDrawable2 = null;
            mCurrentSpotShadowDrawable2ResId = 0;
            mCurrentAmbientShadowDrawable1 = null;
            mCurrentAmbientShadowDrawable1ResId = 0;
            mCurrentAmbientShadowDrawable2 = null;
            mCurrentAmbientShadowDrawable2ResId = 0;
            updateWillNotDraw();
        }

        final View childView = (getChildCount() > 0) ? getChildAt(0) : null;

        if (childView != null) {
            ViewCompat.setTranslationZ(childView, translationZ);
            ViewCompat.setElevation(childView, elevation);
        }
    }

    private boolean updateWillNotDraw() {
        boolean drawAmbientShadow = (mUseAmbientShadow && (mCurrentAmbientShadowDrawable1 != null || mCurrentAmbientShadowDrawable2 != null));
        boolean drawSpotShadow = (mUseSpotShadow && (mCurrentSpotShadowDrawable1 != null || mCurrentSpotShadowDrawable2 != null));
        boolean willNotDraw =
                !drawAmbientShadow &&
                        !drawSpotShadow &&
                        (getBackground() == null) &&
                        (getForeground() == null);
        setWillNotDraw(willNotDraw);

        return willNotDraw;
    }

    private void updateShadowDrawableBounds() {
        if (getChildCount() <= 0) {
            return;
        }

        final View childView = getChildAt(0);

        final int childLeft = childView.getLeft();
        final int childTop = childView.getTop();
        final int childRight = childView.getRight();
        final int childBottom = childView.getBottom();

        updateNinePatchBounds(mCurrentSpotShadowDrawable1, childLeft, childTop, childRight, childBottom);
        if (mCurrentAmbientShadowDrawable1 != mCurrentSpotShadowDrawable2) {
            updateNinePatchBounds(mCurrentSpotShadowDrawable2, childLeft, childTop, childRight, childBottom);
        }

        updateNinePatchBounds(mCurrentAmbientShadowDrawable1, childLeft, childTop, childRight, childBottom);
        if (mCurrentAmbientShadowDrawable1 != mCurrentAmbientShadowDrawable2) {
            updateNinePatchBounds(mCurrentAmbientShadowDrawable2, childLeft, childTop, childRight, childBottom);
        }
    }

    private void updateNinePatchBounds(NinePatchDrawable ninePatch, int childLeft, int childTop, int childRight, int childBottom) {
        if (ninePatch == null) {
            return;
        }

        final Rect t = mTempRect;
        ninePatch.getPadding(t);
        ninePatch.setBounds(
                childLeft - t.left, childTop - t.top,
                childRight + t.right, childBottom + t.bottom);
    }

    private void updateSpotShadowPosition() {
        if (getChildCount() < 1) {
            return;
        }

        final View childView = getChildAt(0);

        childView.getWindowVisibleDisplayFrame(mTempRect);

        mLightPositionX = mTempRect.width() / 2;
        mLightPositionY = 0;

        childView.getLocationInWindow(mTmpLocations);

        final float zPosition = (mShadowTranslationZ + mShadowElevation);
        final float tx = ViewCompat.getTranslationX(childView);
        final float ty = ViewCompat.getTranslationY(childView);

        final float positionRelatedTranslationX;
        final float positionRelatedTranslationY;

        if (mAffectsDisplayedPosition) {
            final int childWidth = childView.getWidth();
            final int childHeight = childView.getHeight();

            final int childCenterPosX = mTmpLocations[0] + (childWidth / 2);
            final int childCenterPosY = mTmpLocations[1] + (childHeight / 2);

            positionRelatedTranslationX = (float) Math.sqrt((childCenterPosX - mLightPositionX) * mInvDisplayDensity * SPOT_SHADOW_X_TRANSLATION_AMOUNT_COEFFICIENT) * zPosition;
            positionRelatedTranslationY = (float) Math.sqrt((childCenterPosY - mLightPositionY) * mInvDisplayDensity * SPOT_SHADOW_Y_TRANSLATION_AMOUNT_COEFFICIENT) * zPosition;
        } else {
            positionRelatedTranslationX = 0;
            positionRelatedTranslationY = mDisplayDensity * NON_POSITION_AWARE_SPOT_SHADOW_Y_TRANSLATION_AMOUNT_COEFFICIENT * zPosition;
        }

        mSpotShadowTranslationX = (int) (positionRelatedTranslationX + tx + 0.5f);
        mSpotShadowTranslationY = (int) (positionRelatedTranslationY + ty + 0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        updateShadowDrawableBounds();
        updateSpotShadowPosition();

        if (requiresChildViewLayoutFix()) {
            fixChildViewGravity();
        }

        if (!useCompatShadow()) {
            updateShadowLevelNative(mShadowTranslationZ, mShadowElevation, true);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (requiresChildViewLayoutFix()) {
            onMeasureCompat(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean requiresChildViewLayoutFix() {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) && (!isInEditMode());
    }

    @SuppressLint("RtlHardcoded")
    private void fixChildViewGravity() {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            LayoutParams params = (LayoutParams) childView.getLayoutParams();

            if (params.gravity == -1) {
                params.gravity = Gravity.TOP | Gravity.LEFT;
            }

            childView.setLayoutParams(params);
        }
    }

    private void onMeasureCompat(int widthMeasureSpec, int heightMeasureSpec) {
        int count = Math.min(1, getChildCount());

        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;

        View matchParentChildren = null;

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);

                childState |= ViewCompat.getMeasuredState(child);

                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                            lp.height == LayoutParams.MATCH_PARENT) {
                        matchParentChildren = child;
                    }
                }
            }
        }

        final int paddingH = getPaddingLeft() + getPaddingRight();
        final int paddingV = getPaddingTop() + getPaddingBottom();

        maxWidth += paddingH;
        maxHeight += paddingV;

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        final Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }

        setMeasuredDimension(
                ViewCompat.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                ViewCompat.resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << ViewCompat.MEASURED_HEIGHT_STATE_SHIFT));

        if (matchParentChildren != null) {
            final View child = matchParentChildren;

            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidthMeasureSpec;
            int childHeightMeasureSpec;

            if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth() - paddingH - lp.leftMargin - lp.rightMargin,
                        MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        paddingH + lp.leftMargin + lp.rightMargin,
                        lp.width);
            }

            if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight() - paddingV - lp.topMargin - lp.bottomMargin,
                        MeasureSpec.EXACTLY);
            } else {
                childHeightMeasureSpec = getChildMeasureSpec(
                        heightMeasureSpec,
                        paddingV + lp.topMargin + lp.bottomMargin,
                        lp.height);
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    private int[] getResourceIdArray(Resources resources, int id) {
        if (id == 0) {
            return null;
        }
        if (isInEditMode()) {
            return null;
        }

        TypedArray ta = resources.obtainTypedArray(id);
        int[] array = new int[ta.length()];

        for (int i = 0; i < array.length; i++) {
            array[i] = ta.getResourceId(i, 0);
        }

        ta.recycle();

        return array;
    }

    private static class SavedState extends BaseSavedState implements Parcelable {
        float shadowTranslationZ;
        float shadowElevation;
        boolean affectsDisplayedPosition;
        boolean forceUseCompatShadow;
        private boolean useAmbientShadow;
        private boolean useSpotShadow;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            shadowTranslationZ = source.readFloat();
            shadowElevation = source.readFloat();
            affectsDisplayedPosition = source.readByte() != 0;
            forceUseCompatShadow = source.readByte() != 0;
            useAmbientShadow = source.readByte() != 0;
            useSpotShadow = source.readByte() != 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(shadowTranslationZ);
            dest.writeFloat(shadowElevation);
            dest.writeByte((byte) (affectsDisplayedPosition ? 1 : 0));
            dest.writeByte((byte) (forceUseCompatShadow ? 1 : 0));
            dest.writeByte((byte) (useAmbientShadow ? 1 : 0));
            dest.writeByte((byte) (useSpotShadow ? 1 : 0));
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
