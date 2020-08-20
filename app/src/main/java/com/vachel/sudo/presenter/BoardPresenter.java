package com.vachel.sudo.presenter;

import android.animation.Animator;
import android.animation.ValueAnimator;

import java.util.Random;

/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class BoardPresenter {
    private IBoardPresenter mCallback;
    private int[][] mAnimStartOffset = new int[9][9];

    public BoardPresenter(IBoardPresenter cb) {
        mCallback = cb;
    }

    public void doInflateAnim() {
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int startValue = random.nextInt(70);
                mAnimStartOffset[i][j] = startValue;
            }
        }
        ValueAnimator inflateAnim = ValueAnimator.ofInt(0, 70, 95, 105, 100);
        inflateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int mAnimProgress = (int) animation.getAnimatedValue();
                mCallback.onInflateAnimProgress(mAnimProgress);
            }
        });
        inflateAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCallback.onInflateAnimProgress(100);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inflateAnim.setDuration(600);
        inflateAnim.start();
    }

    public int getAnimStartOffset(int i, int j){
        return mAnimStartOffset[i][j];
    }

    public interface IBoardPresenter {
        void onInflateAnimProgress(int value);
    }

}
