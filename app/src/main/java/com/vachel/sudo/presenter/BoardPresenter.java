package com.vachel.sudo.presenter;

import android.animation.ValueAnimator;

import com.vachel.sudo.utils.Utils;

import java.util.Random;

/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class BoardPresenter {
    private IBoardPresenter mCallback;
    private int[][] mAnimStartOffset = new int[9][9];
    private float[] mInnerLines;
    private float[] mBoardLines;

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

    public BoardPresenter(IBoardPresenter cb) {
        mCallback = cb;
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
                int mAnimProgress = (int) animation.getAnimatedValue();
                mCallback.onInflateAnimProgress(mAnimProgress);
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

    public interface IBoardPresenter {
        void onInflateAnimProgress(int value);
    }

}
