package com.tencent.map.sdk.samples.tools.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

/**
 * 用户可以继承这个类,实现自己的地图 IOverlay 的动画。这是一个 AnimatorSet 动画集合, 由
 * 用户传入的一系列动画关键值生成。(可以理解为 keyframe)
 *
 * @author wangxiaokun on 2016/9/23.
 */
public abstract class OverlayAnimator {

    private Object mObject;

    /**
     * 动画持续时间, 单位:ms
     */
    private long mDuration;

    /**
     * 动画集合
     */
    private AnimatorSet mAnimatorSet;

    protected OverlayAnimator(Object object, long duration) {
        mObject = object;
        mDuration = duration;
        mAnimatorSet = new AnimatorSet();
    }

    /**
     * 获取动画持续时间, 单位:ms
     *
     * @return
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * 设置动画持续时间, 单位 ms
     *
     * @param duration
     */
    public void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * 获取要执行动画的地图 IOverlay 对象
     *
     * @return
     */
    public Object getObject() {
        return mObject;
    }

    /**
     * 设置要执行动画的地图 object 对象
     *
     * @param object
     */
    public void setObject(Object object) {
        mObject = object;
    }

    /**
     * 获取动画集合
     *
     * @return
     */
    public AnimatorSet getAnimatorSet() {
        return mAnimatorSet;
    }

    /**
     * 设置动画集合
     *
     * @param animatorSet
     */
    protected void setAnimatorSet(AnimatorSet animatorSet) {
        mAnimatorSet = animatorSet;
    }

    /**
     * 开始动画, 子类构造完成后,通过这个方法开始动画
     */
    public void startAnimation() {
        synchronized (this) {
            if (!mAnimatorSet.isRunning()) {
                mAnimatorSet.start();
            }
        }
    }

    /**
     * 取消动画, 被应用动画的对象停止在当前状态
     */
    public void cancelAnimation() {
        synchronized (this) {
            mAnimatorSet.cancel();
        }
    }

    /**
     * 结束动画, 被应用动画的对象状态直接跳到动画终点状态
     */
    public void endAnimation() {
        synchronized (this) {
            mAnimatorSet.end();
        }
    }

    /**
     * 设置整个 animatorSet 的每段动画, 子类通过重写这个方法设置应用到 IOverlay 的动画。
     */
    protected abstract ValueAnimator createSegmentAnimator(int segmentIndex);

    /**
     * 设置动画回调
     *
     * @param listener
     */
    public void addAnimatorListener(Animator.AnimatorListener listener) {
        mAnimatorSet.addListener(listener);
    }
}
