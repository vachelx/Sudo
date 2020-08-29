package com.vachel.sudo.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.view.animation.OvershootInterpolator;

import com.vachel.sudo.bean.CellTouchBean;
import com.vachel.sudo.helper.SudoIterator;
import com.vachel.sudo.render.BoardLinesRender;
import com.vachel.sudo.render.EdgeGradientRender;
import com.vachel.sudo.render.InnerLinesRender;
import com.vachel.sudo.render.cell.BaseCellRender;
import com.vachel.sudo.render.cell.CellRender;
import com.vachel.sudo.render.cell.CommonParams;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class BoardPresenter implements BaseCellRender.ICellCallback {

    private ISudoView mView;
    private boolean mStartCompleteAnim;
    private int mErrorAnimProgress = 0;
    private ValueAnimator mErrorAnim;
    private int mBreathProgress = -1;  // 默认值-1；complete前， completeAnim中为大于0；completeAnim完成后为-2
    private int mInflateProgress = 200;
    private final CommonParams mCommonParams;
    private final InnerLinesRender mInnerLinesRender;
    private final BoardLinesRender mBoardLinesRender;
    private final EdgeGradientRender mEdgeGradientRender;
    private CellRender[][] mCellRenders;
    private int mCompletePopProgress;

    public CellRender[][] getCellRenders() {
        return mCellRenders;
    }

    public BoardPresenter(ISudoView cb) {
        mView = cb;
        mCommonParams = new CommonParams();
        mCellRenders = new CellRender[9][9];
        mInnerLinesRender = new InnerLinesRender();
        mBoardLinesRender = new BoardLinesRender();
        mEdgeGradientRender = new EdgeGradientRender();
        SudoIterator.execute(mCellRenders, (i, j, value) -> mCellRenders[i][j] = new CellRender(i, j, mCommonParams, this));
    }

    public void initCellsData(Integer[][] data) {
        SudoIterator.execute(mCellRenders, (i, j, value) -> value.initDataByExam(data[i][j]));
    }

    public void initCellWidth(float cellWidth) {
        mCommonParams.cellWidth = cellWidth;
        SudoIterator.execute(mCellRenders, (i, j, value) -> value.initLocation(cellWidth));
    }

    // 初始进入棋盘时，加载动画（数字随机跃入）
    public void doInflateAnim() {
        final Random random = new Random();
        SudoIterator.execute(mCellRenders, (i, j, value) -> {
            value.mStartAnimOffset = random.nextInt(100);
        });
        ValueAnimator inflateAnim = ValueAnimator.ofInt(0, 200);
        inflateAnim.addUpdateListener(animation -> {
            mInflateProgress = (int) animation.getAnimatedValue();
            mView.invalidate();
        });
        inflateAnim.setDuration(600);
        inflateAnim.start();
    }

    public void startCompleteAnim(ArrayList<int[]> rightStep) {
        if (mErrorAnim != null) {
            mErrorAnim.cancel();
        }
        mBreathProgress = -1;
        ArrayList<int[]> extra = new ArrayList<>();
        for (int i = 0; i < rightStep.size(); i++) {
            int[] step = rightStep.get(i);
            if (mCellRenders[step[0]][step[1]].mCellValue != step[2]) {
                extra.add(step);
            }
        }
        rightStep.removeAll(extra);

        int size = rightStep.size();
        int duration = size < 15 ? 300 : size * 20;
        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
        anim.addUpdateListener(animation -> {
            mCompletePopProgress = (int) animation.getAnimatedValue();
            mView.invalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mStartCompleteAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mView.onBreathAnimEnd();
               doBreathAnim();
            }
        });
        anim.setDuration(duration);
        anim.start();
    }

    public boolean isStartCompleteAnim() {
        return mStartCompleteAnim;
    }

    public void setStartCompleteAnim(boolean b) {
        mStartCompleteAnim = b;
    }

    public int getErrorAnimProgress() {
        return mErrorAnimProgress;
    }

    public void drawErrorAnimIfNeed(Canvas canvas, int width, int height) {
        if (getErrorAnimProgress() != 0) {
            mEdgeGradientRender.drawEdge(canvas, width, height);
        }
    }

    public void drawCompleteAnim(Canvas canvas, CellTouchBean touchCell, ArrayList<int[]> rightStep) {
        SudoIterator.execute(getCellRenders(), (i, j, value) -> {
            if (value.mIsImmutable) {
                value.onDraw(canvas, touchCell);
            }
        });
        int showSize = (int) (mCompletePopProgress / 100f * rightStep.size());
        for (int i = 0; i < showSize; i++) {
            int[] step = rightStep.get(i);
            mCellRenders[step[0]][step[1]].drawCompleteProgress(canvas, mBreathProgress);
        }
    }

    public void drawLines(Canvas canvas) {
        mInnerLinesRender.drawInnerLines(canvas, mCommonParams.cellWidth);
        mBoardLinesRender.drawLines(canvas, mCommonParams.cellWidth, mCommonParams.circlePaint);
    }

    public interface ISudoView {
        void onBreathAnimEnd();

        void invalidate();
    }

    public void doBreathAnim() {
        ValueAnimator anim = ValueAnimator.ofInt(100, 20, 100);
        anim.addUpdateListener(animation -> {
            mBreathProgress = (int) animation.getAnimatedValue();
            mView.invalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mView.onBreathAnimEnd();
                mBreathProgress = -2;
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
        mErrorAnim.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            mErrorAnimProgress = progress;
            mEdgeGradientRender.updateGradient(width, height, progress);
            mView.invalidate();
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

    public CellRender getCell(int x, int y) {
        return mCellRenders[x][y];
    }

    public Integer[][] getTmpData() {
        Integer[][] tmp = new Integer[9][9];
        SudoIterator.execute(mCellRenders, (i, j, value) -> tmp[i][j] = value.mCellValue);
        return tmp;
    }

    public boolean hasNoFilledData() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                CellRender cellRender = mCellRenders[i][j];
                if (!cellRender.mIsImmutable) {
                    if (cellRender.mCellValue != 0 || cellRender.mMarkValues != null && cellRender.mMarkValues.size() > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean checkInputFinish() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (mCellRenders[i][j].mCellValue == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void resetState() {
        mBreathProgress = -1;
        mInflateProgress = 200;
        SudoIterator.execute(mCellRenders, (i, j, value) -> {
            if (!value.mIsImmutable) {
                value.mMarkValues = null;
                value.mCellValue = 0;
            }
        });
    }

    public void resetMarks() {
        SudoIterator.execute(mCellRenders, (i, j, value) -> {
            if (!value.mIsImmutable) {
                value.mMarkValues = null;
            }
        });
    }

    public boolean isBreathAimEnd() {
        return mBreathProgress == -2;
    }

    @Override
    public int getInflateAnimProgress() {
        return mInflateProgress;
    }
}
