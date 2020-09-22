package com.vachel.sudo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vachel.sudo.bean.CellHistoryBean;
import com.vachel.sudo.bean.CellTouchBean;
import com.vachel.sudo.helper.SudoIterator;
import com.vachel.sudo.presenter.BoardPresenter;
import com.vachel.sudo.engine.Algorithm;
import com.vachel.sudo.render.cell.CellRender;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;


public class SudoBoard extends View implements InputLayout.IOnTextClickListener, BoardPresenter.ISudoView {
    private LinkedList<ArrayList<CellHistoryBean>> mHistoryList = new LinkedList<>();
    private ArrayList<int[]> mRightStep = new ArrayList<>(); // 记录正确步骤用于做动画
    private float mCellWidth;
    private boolean mIsMark;
    private IBoardListener mBoardListener;
    private BoardPresenter mPresenter;
    private boolean mHasPopComplete;
    private boolean mHasLoadData;
    private CellTouchBean mTouchCell;
    private boolean mUpdateRemainingCounts;

    public SudoBoard(Context context) {
        this(context, null);
    }

    public SudoBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SudoBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPresenter = new BoardPresenter(this);
        mTouchCell = new CellTouchBean();
        mUpdateRemainingCounts = PreferencesUtils.getBooleanPreference(getContext(), Constants.REMAINING_USEFUL_COUNTS, true);
    }

    public void initData(@NonNull Integer[][] data) {
        mPresenter.initCellsData(data);
        mHasLoadData = true;
        mPresenter.doInflateAnim();
        updateCounts();
    }

    public void resumeData(Integer[][] data, Integer[][] mResumeTmpSudo, TreeSet<Integer>[][] mResumeMarks) {
        resumeCellsData(data, mResumeTmpSudo, mResumeMarks);
        mHasLoadData = true;
        mPresenter.doInflateAnim();
        updateCounts();
    }

    private void resumeCellsData(Integer[][] data, Integer[][] mResumeTmpSudo, TreeSet<Integer>[][] resumeMarks) {
        SudoIterator.execute(mPresenter.getCellRenders(), (i, j, value) -> {
            value.initDataByExam(data[i][j]);
            if (!value.mIsImmutable) {
                value.mMarkValues = resumeMarks[i][j];
                Integer resumeValue = mResumeTmpSudo[i][j];
                if(resumeValue!=0){
                    value.mCellValue = mResumeTmpSudo[i][j];
                    mRightStep.add(new int[]{i, j, resumeValue});
                }
            }
        });
    }

    private void updateCounts() {
        if (mBoardListener != null && mUpdateRemainingCounts) {
            mBoardListener.onTextChanged(mPresenter.getTmpData());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int measuredWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initCellWidthIfNeed();
        if (mPresenter.isStartCompleteAnim()) {
            mPresenter.drawCompleteAnim(canvas, mTouchCell, mRightStep);
        } else {
            SudoIterator.execute(mPresenter.getCellRenders(), (i, j, value) -> value.onDraw(canvas, mTouchCell));
        }

        mPresenter.drawErrorAnimIfNeed(canvas, getMeasuredWidth(), getMeasuredHeight());
        mPresenter.drawLines(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!mHasLoadData) {
                return false;
            }
            if (mPresenter.isStartCompleteAnim()) {
                if (mPresenter.isBreathAimEnd()) {
                    mPresenter.setStartCompleteAnim(false);
                } else { // 未结束动画前不可点击
                    return true;
                }
            }
            float x = event.getX();
            float y = event.getY();
            int touchJ = (int) (x / mCellWidth);
            int touchI = (int) (y / mCellWidth);

            Integer selectValue = mPresenter.getCell(touchI, touchJ).mCellValue;
            if (touchI == mTouchCell.x && touchJ == mTouchCell.y) {
                mTouchCell.selectValue = mTouchCell.selectValue == null ? selectValue : null;
            } else {
                mTouchCell.selectValue = selectValue;
            }
            mTouchCell.x = touchI;
            mTouchCell.y = touchJ;
            invalidate();
        }
        return true;
    }

    private float initCellWidthIfNeed() {
        if (mCellWidth == 0) {
            mCellWidth = getMeasuredWidth() / 3f / 3f;
            mPresenter.initCellWidth(mCellWidth);
        }
        return mCellWidth;
    }

    @Override
    public void onTextClick(Integer value) {
        if (!mHasLoadData || mTouchCell.selectValue == null || mPresenter.isStartCompleteAnim() || mPresenter.isBreathAimEnd()) {
            return;
        }
        CellRender cell = mPresenter.getCell(mTouchCell.x, mTouchCell.y);
        if (cell.mIsImmutable) {
            return;
        }

        if (!mIsMark || value == 0) {//改变cell值或清除mark值
            Integer lastValue = cell.mCellValue;
            boolean willChanged = !lastValue.equals(value) || cell.mMarkValues != null;
            if (willChanged) {
                onValueChanged(value);
                cell.mCellValue = value;
                mTouchCell.selectValue = value;
                invalidate();
            }
            if (mPresenter.checkInputFinish()) {
                boolean success = Algorithm.checkResult(mPresenter.getTmpData());
                if (success) {
                    if (mBoardListener != null) {
                        mBoardListener.onSolved();
                    }
                    startCompleteAnim();
                } else {
                    mPresenter.doErrorAnim(getMeasuredWidth(), getMeasuredHeight());
                    ToastUtil.showShortToast(getContext(), "还有错误喔！");
                }
            }
        } else { // 增减mark标记
            onCellMark(value);
            invalidate();
        }
        updateCounts();
    }

    // 填充成功后动画
    public void startCompleteAnim() {
        mTouchCell.selectValue = null;
        mPresenter.startCompleteAnim(mRightStep);
    }

    @Override
    public void onPreStepClick() {
        ArrayList<CellHistoryBean> changes = mHistoryList.pollLast();
        if (changes != null) {
            // 撤销变化
            for (CellHistoryBean bean : changes) {
                CellRender cell = mPresenter.getCell(bean.getX(), bean.getY());
                cell.mMarkValues = bean.getMarkValue();
                cell.mCellValue = bean.getMarkValue() != null ? 0 : bean.getValue();
            }

            // 选中上一步的cell
            if (mHistoryList.size() > 0) {
                ArrayList<CellHistoryBean> last = mHistoryList.getLast();
                CellHistoryBean prevEditCell = last.get(0);
                mTouchCell.x = prevEditCell.getX();
                mTouchCell.y = prevEditCell.getY();
                mTouchCell.selectValue = mPresenter.getCell(mTouchCell.x, mTouchCell.y).mCellValue;
            } else {
                mTouchCell.reset();
                mPresenter.resetMarks();
            }
            invalidate();
            updateCounts();
        }
    }

    @Override
    public void onSaveClick() {
        if (!mHasLoadData) {
            return;
        }
        if (hasNoFilledData()) {
            ToastUtil.showShortToast(getContext(), "没有可保存的数据");
            return;
        }

        if (mBoardListener != null) {
            Integer[][] examData = new Integer[9][9];
            Integer[][] tmpData = new Integer[9][9];
            TreeSet<Integer>[][] markInfo = new TreeSet[9][9];
            SudoIterator.execute(mPresenter.getCellRenders(), (i, j, value) -> {
                if (value.mIsImmutable) {
                    examData[i][j] = value.mCellValue;
                } else {
                    examData[i][j] = 0;
                    if (value.mMarkValues != null) {
                        markInfo[i][j] = value.mMarkValues;
                    }
                }
                tmpData[i][j] = value.mCellValue;
            });
            mBoardListener.saveArchive(examData, tmpData, markInfo);
        }
    }

    public boolean hasNoFilledData() {
        return mPresenter.hasNoFilledData();
    }

    private void onCellMark(Integer value) {
        CellRender cell = mPresenter.getCell(mTouchCell.x, mTouchCell.y);
        // 存操作记录
        ArrayList<CellHistoryBean> historyStep = new ArrayList<>();
        if (cell.mMarkValues == null) {
            historyStep.add(new CellHistoryBean(mTouchCell.x, mTouchCell.y, cell.mCellValue));
        } else {
            historyStep.add(new CellHistoryBean(mTouchCell.x, mTouchCell.y, new TreeSet<>(cell.mMarkValues)));
        }
        mHistoryList.add(historyStep);

        // 标记cell的可用值
        mTouchCell.selectValue = 0;
        cell.performMarkValue(value);
    }

    private void onValueChanged(Integer value) {
        if (value != 0) {
            mRightStep.add(new int[]{mTouchCell.x, mTouchCell.y, value});
        }

        ArrayList<CellHistoryBean> historyStep = new ArrayList<>();
        CellRender cell = mPresenter.getCell(mTouchCell.x, mTouchCell.y);

        // 设置值时清空标注
        if (cell.mMarkValues != null) {
            historyStep.add(new CellHistoryBean(mTouchCell.x, mTouchCell.y, new TreeSet<>(cell.mMarkValues)));
            cell.mMarkValues = null;
        } else {
            historyStep.add(new CellHistoryBean(mTouchCell.x, mTouchCell.y, cell.mCellValue));
        }

        // 减少标记
        if (value != 0) {
            SudoIterator.execute(mPresenter.getCellRenders(), (i, j, render) -> {
                if (render.mMarkValues != null && (
                        i == mTouchCell.x || j == mTouchCell.y
                                || i >= mTouchCell.x / 3 * 3 && i < mTouchCell.x / 3 * 3 + 3 && j >= mTouchCell.y / 3 * 3 && j < mTouchCell.y / 3 * 3 + 3)) {
                    historyStep.add(new CellHistoryBean(i, j, new TreeSet<>(render.mMarkValues)));
                    render.mMarkValues.remove(value);
                    if (render.mMarkValues.size() == 0) {
                        render.mMarkValues = null;
                    }
                }
            });
        }

        mHistoryList.add(historyStep);
    }

    @Override
    public void onResetClick() {
        if (hasNoFilledData()) {
            return;
        }

        new BaseAlertDialog()
                .initDialog(null, "确认清除所有输入数据，恢复到初始状态？")
                .setNegativeTextDefault()
                .setPositiveTextDefault()
                .setListener(new BaseAlertDialog.IDialogListener() {
                    @Override
                    public void onPositiveClick() {
                        resetAll(true);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                })
                .show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "");
    }

    public void resetAll(boolean needTimer) {
        mTouchCell.reset();
        mHistoryList.clear();

        mPresenter.resetState();
        mHasPopComplete = false;
        mRightStep.clear();
        mPresenter.setStartCompleteAnim(false);
        if (mBoardListener != null) {
            mBoardListener.onReset(needTimer);
            mBoardListener.onTextChanged(mPresenter.getTmpData());
        }
        invalidate();
    }

    @Override
    public void onMarkClick(boolean mark) {
        mIsMark = mark;
    }

    public void setBoardListener(IBoardListener listener) {
        mBoardListener = listener;
    }

    @Override
    public void onBreathAnimEnd() {
        invalidate();
        // 第一次完成才跳转
        if (!mHasPopComplete && mBoardListener != null) {
            mBoardListener.completeExam();
            mHasPopComplete = true;
        }
    }

    public interface IBoardListener {
        void onSolved();

        void completeExam();

        void onReset(boolean needTimer);

        void onTextChanged(Integer[][] sudo);

        void saveArchive(Integer[][] examData, Integer[][] tmpData, TreeSet<Integer>[][] marks);
    }
}
