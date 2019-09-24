package com.tencent.map.sdk.samples.tools.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.tencent.map.lib.basemap.data.DoublePoint;
import com.tencent.map.sdk.samples.tools.SphericalMercatorProjection;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * marker 移动动画, 可以根据一系列坐标点动画移动 marker
 *
 * @author wangxiaokun on 2016/9/26.
 */

public class MarkerTranslateAnimator extends OverlayAnimator {

    /**
     * marker 移动路线点串
     */
    private LatLng[] mLatLngs;

    /**
     * 点串相邻点间的距离
     */
    private double[] mDistances;

    /**
     * 路线总距离
     */
    private double mSumDistance;

    /**
     * 允许旋转 marker
     */
    private boolean mRotateEnabled;

    /**
     * 旋转动画集合
     */
    private AnimatorSet mRotateAnimatorSet;

    private SphericalMercatorProjection mEarthMercatorProjection;

    private volatile boolean isRotationAnimationPlaying = false;

    /**
     * Marker 移动动画, 并不会根据线路旋转 marker
     *
     * @param marker   要添加移动动画的 marker
     * @param duration 动画的持续时间
     * @param latLngs  marker 移动过程经过的坐标点
     */
    public MarkerTranslateAnimator(Marker marker, long duration, LatLng[] latLngs) {
        this(marker, duration, latLngs, false);
    }

    /**
     * Marker 移动动画
     *
     * @param marker        要添加移动动画的 marker
     * @param duration      动画的持续时间
     * @param latLngs       marker 移动过程经过的坐标点
     * @param rotateEnabled marker 移动过程中是否旋转
     */
    public MarkerTranslateAnimator(Marker marker, long duration,
                                   LatLng[] latLngs, boolean rotateEnabled) {
        super(marker, duration);
        if (latLngs == null) {
            return;
        }
        mLatLngs = latLngs;
        mDistances = new double[latLngs.length - 1];
        mEarthMercatorProjection = new SphericalMercatorProjection();
        for (int i = 0; i < latLngs.length - 1; i++) {
            mDistances[i] = mEarthMercatorProjection.distanceBetween(latLngs[i], latLngs[i + 1]);
            mSumDistance += mDistances[i];
        }
        List<Animator> animators = new ArrayList<>();
        for (int i = 0; i < latLngs.length - 1; i++) {
            animators.add(createSegmentAnimator(i));
        }
        getAnimatorSet().playSequentially(animators);

        mRotateEnabled = rotateEnabled;
        if (rotateEnabled) {
            setRotateAnimatorSet();
        }
    }

    @Override
    protected ValueAnimator createSegmentAnimator(final int segmentIndex) {
        final DoublePoint fromMercator = mEarthMercatorProjection.toPoint(mLatLngs[segmentIndex]);
        final DoublePoint toMercator = mEarthMercatorProjection.toPoint(mLatLngs[segmentIndex + 1]);

        ValueAnimator animator = new ValueAnimator();
        animator.setDuration((long) ((double) getDuration() * mDistances[segmentIndex] / mSumDistance));
        animator.setInterpolator(new LinearInterpolator());
        animator.setFloatValues((float) mDistances[segmentIndex]);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (fromMercator.equals(toMercator)) {
                    //两个坐标点一致,直接返回
                    return;
                }
                double currentDistance = Double.parseDouble(String.valueOf(animation.getAnimatedValue()));
                double x = fromMercator.x +
                        (toMercator.x - fromMercator.x) *
                                currentDistance / mDistances[segmentIndex];
                double y = fromMercator.y +
                        (toMercator.y - fromMercator.y) *
                                currentDistance / mDistances[segmentIndex];
                if (getObject() == null) {
                    return;
                }
                ((Marker) getObject()).setPosition(mEarthMercatorProjection.toLatLng(new DoublePoint(x, y)));
            }
        });
        return animator;
    }

    private void setRotateAnimatorSet() {
        mRotateAnimatorSet = new AnimatorSet();
        mRotateAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRotationAnimationPlaying = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isRotationAnimationPlaying = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        List<Animator> mRotateAnimators = new ArrayList<>();
        //设置固定的转弯半径, 这里设置6m
        double turnRadius = 6;
        long duration = 0;
        long delay = 0;
        //路线段起点和终点在点串中的序号
        int startIndex = 0;
        int endIndex = 0;
        //上次旋转的坐标序号
        int lastTurnIndex = 0;
        float lastAngle = 0f;
        float deltaAngle = 0f;
        for (int i = 1; i < mLatLngs.length; i++) {
            if (mLatLngs[startIndex].equals(mLatLngs[i])) {
                continue;
            } else {
                endIndex = i;
                DoublePoint m0 = mEarthMercatorProjection.toPoint(mLatLngs[lastTurnIndex]);
                DoublePoint m1 = mEarthMercatorProjection.toPoint(mLatLngs[startIndex]);
                DoublePoint m2 = mEarthMercatorProjection.toPoint(mLatLngs[endIndex]);
                //The positive direction of y axis down.
                deltaAngle = (float) calculateAngle(
                        m1.x - m0.x, m0.y - m1.y,
                        m2.x - m1.x, m1.y - m2.y);
                if (mRotateAnimators.size() == 0) {
                    // 如果还没有旋转动画加入到集合, 将 marker 初始旋转角度
                    // 作为上次旋转角度的结果, 并设置动画时间和延迟都为0,即立刻执行动画
                    if (getObject() == null) {
                        return;
                    }
                    lastAngle = ((Marker) getObject()).getRotation();
                    // 初始两个坐标与正北方夹角
                    //The positive direction of y axis down.
                    deltaAngle = (float) calculateAngle(
                            0, 1,
                            m2.x - m1.x, m1.y - m2.y);
                    delay = 0;
                    duration = 0;
                } else {
                    //根据弧长公式计算动画持续时间
                    double turnDistance = Math.abs(deltaAngle) * Math.PI * turnRadius / 180;
                    duration = (long) ((double) getDuration() * turnDistance / mSumDistance);
                    //在当前转角前延迟直行所需动画时间并减去转角动画时间的一半
                    delay = calculateDelay(lastTurnIndex, startIndex) - duration / 2;
                }
                mRotateAnimators.add(createRotateAnimator(lastAngle, lastAngle += deltaAngle, duration, delay));
                lastTurnIndex = startIndex;
                startIndex = endIndex;
            }
        }
        mRotateAnimatorSet.playSequentially(mRotateAnimators);
    }

    /**
     * 计算向量 a(x1, y1), b(x2, y2) 间的夹角
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 向量 a, b 的夹角,范围[180°, -180°]
     */
    private double calculateAngle(double x1, double y1, double x2, double y2) {
        double dotProduct = x1 * x2 + y1 * y2;
        double cosAlpha = dotProduct /
                (Math.sqrt((x1 * x1 + y1 * y1)) * Math.sqrt((x2 * x2 + y2 * y2)));
        if (Double.isNaN(cosAlpha)) {
            return 0;
        }
        //精度损失，可能计算出的 cos 值超出 [-1,1] 区间
        if (cosAlpha < -1) {
            cosAlpha = -1;
        }
        if (cosAlpha > 1) {
            cosAlpha = 1;
        }
        double angle = Math.acos(cosAlpha) * 180 / Math.PI;
        double crossProduc = x1 * y2 - y1 * x2;
        if (crossProduc > 0) {
            angle = -angle;
        }
        return (float) angle;
    }

    /**
     * 根据两次旋转坐标在点串中的序号计算旋转动画的延时
     *
     * @param lastTurnIndex    上次旋转坐标的序号
     * @param currentTurnIndex 本次旋转坐标的序号
     * @return 动画的延迟时间, 单位:ms
     */
    private long calculateDelay(int lastTurnIndex, int currentTurnIndex) {
        double d = 0;
        for (int i = lastTurnIndex; i < currentTurnIndex; i++) {
            d += mDistances[i];
        }
        return (long) ((double) getDuration() * d / mSumDistance);
    }

    private ValueAnimator createRotateAnimator(final float startAngle, final float endAngle,
                                               long duration, long delay) {
        ValueAnimator animator = ValueAnimator.ofFloat(startAngle, endAngle);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            double currentAngle = Double.parseDouble(String.valueOf(animation.getAnimatedValue()));
            if (getObject() == null) {
                return;
            }
            ((Marker) getObject()).setRotation((float) currentAngle);
        });
        return animator;
    }

    @Override
    public void startAnimation() {
        super.startAnimation();
        synchronized (this) {
            if (mRotateEnabled && mRotateAnimatorSet != null
                    && !isRotationAnimationPlaying) {
                isRotationAnimationPlaying = true;
                mRotateAnimatorSet.start();
            }
        }
    }

    @Override
    public void cancelAnimation() {
        super.cancelAnimation();
        synchronized (this) {
            if (mRotateEnabled && mRotateAnimatorSet != null) {
                mRotateAnimatorSet.cancel();
            }
        }
    }

    @Override
    public void endAnimation() {
        super.endAnimation();
        synchronized (this) {
            if (mRotateEnabled && mRotateAnimatorSet != null) {
                mRotateAnimatorSet.end();
            }
        }
    }
}
