package com.vachel.sudo.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.animation.OvershootInterpolator;

import com.vachel.sudo.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

import static com.vachel.sudo.utils.Constants.ERROR_RECT_WIDTH;

/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class BoardPresenter {
    private IAnimCallback mCallback;
    private int[][] mAnimStartOffset = new int[9][9];
    private float[] mInnerLines;
    private float[] mBoardLines;
    private boolean mStartCompleteAnim;
    private int mCompleteAnimIndex;
    private int mErrorAnimProgress = 0;
    private ValueAnimator mErrorAnim;
    private LinearGradient mGradient[]= new LinearGradient[4];

    public BoardPresenter(IAnimCallback cb) {
        mCallback = cb;
    }


    public LinearGradient[] getGradient() {
        return mGradient;
    }

    // 单元格分割线
    public float[] getInnerLines(float cellWidth) {
        if (mInnerLines == null) {
            mInnerLines = Utils.getInnerLines(cellWidth);
        }
        return mInnerLines;
    }

    // 大宫格分割线
    public float[] getBoardLines(float perWidth) {
        if (mBoardLines == null) {
            mBoardLines = new float[]{
                    0, perWidth, perWidth * 3, perWidth,
                    0, perWidth * 2, perWidth * 3, perWidth * 2,
                    perWidth, 0, perWidth, perWidth * 3,
                    perWidth * 2, 0, perWidth * 2, perWidth * 3
            };
        }
        return mBoardLines;
    }

    // 初始进入棋盘时，加载动画（数字随机跃入）
    public void doInflateAnim() {
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int startValue = random.nextInt(100);
                mAnimStartOffset[i][j] = startValue;
            }
        }
        ValueAnimator inflateAnim = ValueAnimator.ofInt(0, 200);
        inflateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mCallback.onInflateAnimProgress(progress);
            }
        });
        inflateAnim.setDuration(600);
        inflateAnim.start();
    }

    // 根据随机开始offset计算出实际动画进度
    public float getRelativeProgress(int i, int j, int progress) {
        int offset = mAnimStartOffset[i][j];
        if (progress > offset) {
            progress = progress - offset;
            if (progress > 100) {
                progress = 100;
            }
        } else {
            progress = 0;
        }
        return progress / 100f;
    }

    public void prepareStartCompleteAnimData(ArrayList<int[]> mRightStep, Integer[][] mTmpData) {
        if (mErrorAnim != null) {
            mErrorAnim.cancel();
        }
        ArrayList<int[]> extra = new ArrayList<>();
        for (int i = 0; i < mRightStep.size(); i++) {
            int[] step = mRightStep.get(i);
            if (mTmpData[step[0]][step[1]] != step[2]) {
                extra.add(step);
            }
        }
        mRightStep.removeAll(extra);
        mStartCompleteAnim = true;
        mCompleteAnimIndex = 0;
    }

    public boolean isStartCompleteAnim() {
        return mStartCompleteAnim;
    }

    public void setStartCompleteAnim(boolean b) {
        mStartCompleteAnim = b;
    }

    public int getCurrentAnimIndex() {
        return mCompleteAnimIndex++;
    }

    public int getErrorAnimProgress() {
        return mErrorAnimProgress;
    }

    public interface IAnimCallback {
        void onInflateAnimProgress(int value);

        void onBreathAnimProgress(int value);

        void onBreathAnimEnd();

        void onErrorAnimProgress(int value);
    }

    public void doBreathAnim() {
        ValueAnimator anim = ValueAnimator.ofInt(100, 20, 100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mCallback.onBreathAnimProgress(progress);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCallback.onBreathAnimEnd();
            }
        });
        anim.setDuration(450);
        anim.setInterpolator(new OvershootInterpolator());
        anim.start();
    }

    public void doErrorAnim(final int width, final int height) {
        if (mErrorAnim != null) {
            return;
        }
        mErrorAnim = ValueAnimator.ofInt(0, 100, 0, 100, 0);
        mErrorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mErrorAnimProgress = progress;
                int alpha = (int) (mErrorAnimProgress / 100f * 255);
                int startColor = Color.argb(alpha, 255, 102, 102);
                mGradient[0] = new LinearGradient(0, 0, ERROR_RECT_WIDTH, 0, startColor, 0, Shader.TileMode.MIRROR);
                mGradient[1] = new LinearGradient(0, 0, 0, ERROR_RECT_WIDTH, startColor, 0, Shader.TileMode.MIRROR);
                mGradient[2] = new LinearGradient(width, 0, width - ERROR_RECT_WIDTH, 0, startColor, 0, Shader.TileMode.MIRROR);
                mGradient[3] = new LinearGradient(0, height, 0, height - ERROR_RECT_WIDTH, startColor, 0, Shader.TileMode.MIRROR);
                mCallback.onErrorAnimProgress(progress);
            }
        });
        mErrorAnim.setDuration(800);
        mErrorAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mErrorAnimProgress = 0;
                mErrorAnim = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mErrorAnimProgress = 0;
                mErrorAnim = null;
            }
        });
        mErrorAnim.start();
    }

}
