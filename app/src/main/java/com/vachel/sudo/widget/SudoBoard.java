package com.vachel.sudo.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vachel.sudo.R;
import com.vachel.sudo.bean.CellHistoryBean;
import com.vachel.sudo.presenter.BoardPresenter;
import com.vachel.sudo.utils.Arithmetic;
import com.vachel.sudo.utils.ToastUtil;
import com.vachel.sudo.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import static com.vachel.sudo.utils.Constants.ERROR_RECT_WIDTH;

public class SudoBoard extends View implements InputLayout.IOnTextClickListener, BoardPresenter.IAnimCallback {
    private Integer[][] mExamData;
    private Integer[][] mTmpData;
    private TreeSet<Integer>[][] mMarkInfo = new TreeSet[9][9];
    private LinkedList<ArrayList<CellHistoryBean>> mHistoryList = new LinkedList<>();
    private ArrayList<int[]> mRightStep = new ArrayList<>(); // 记录正确步骤用于做动画

    private Paint mBoardPaint;
    private Paint mInnerPaint;
    private float mCellWidth;
    private float mTextOffsetY;
    private Paint mTextPaint;
    private Integer mSelectValue;
    private int mTouchI;
    private int mTouchJ;
    private Paint mBlankBgPaint;
    private boolean mIsMark;
    private Paint mMarkPaint;
    private float mMarkTextOffsetY;
    private IBoardListener mBoardListener;
    private int mAnimProgress = 200;
    private BoardPresenter mPresenter;
    private boolean mHasPopComplete;
    private int mBreathProgress = -1; // 默认值-1；complete前， completeAnim中为大于0；completeAnim完成后为-2
    private Paint mGradientPaint;
    private int mColorBlue;

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
        Resources resources = getContext().getResources();
        mColorBlue = resources.getColor(R.color.main_blue_dark);
        mBoardPaint = new Paint();
        mBoardPaint.setAntiAlias(true);
        mBoardPaint.setColor(mColorBlue);
        mBoardPaint.setStrokeWidth(6);

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(Color.LTGRAY);

        mBlankBgPaint = new Paint();
        mBlankBgPaint.setAntiAlias(true);
        mBlankBgPaint.setColor(Color.LTGRAY);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mColorBlue);

        mMarkPaint = new Paint();
        mMarkPaint.setAntiAlias(true);
        mMarkPaint.setColor(Color.BLACK);
        mMarkPaint.setTextSize(22f);
        Paint.FontMetrics metrics = mMarkPaint.getFontMetrics();
        mMarkTextOffsetY = (metrics.descent - metrics.ascent) / 2 - metrics.descent;

        mGradientPaint = new Paint();
        mGradientPaint.setAntiAlias(true);
        mGradientPaint.setStyle(Paint.Style.FILL);

        mPresenter = new BoardPresenter(this);
    }

    private void resetTextSize(int x, int y, int progress) {
        mTextPaint.setTextSize(70f * mPresenter.getRelativeProgress(x, y, progress));
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    public void initData(@NonNull Integer[][] data) {
        mExamData = data;
        mTmpData = Arithmetic.copySudo(mExamData);
        mPresenter.doInflateAnim();
        updateCounts();
    }

    private void updateCounts() {
        if (mBoardListener != null) {
            mBoardListener.onTextChanged(mTmpData);
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
        if (mPresenter.isStartCompleteAnim()) {
            drawCompleteAnim(canvas);
        } else if (mTmpData != null) {
            drawCells(canvas, mTmpData);
        }
        if (mPresenter.getErrorAnimProgress() != 0) {
            LinearGradient[] gradient = mPresenter.getGradient();
            mGradientPaint.setShader(gradient[0]);
            canvas.drawRect(0, 0, ERROR_RECT_WIDTH, getMeasuredHeight(), mGradientPaint);
            mGradientPaint.setShader(gradient[1]);
            canvas.drawRect(0, 0, getMeasuredWidth(), ERROR_RECT_WIDTH, mGradientPaint);
            mGradientPaint.setShader(gradient[2]);
            canvas.drawRect(getMeasuredWidth() - ERROR_RECT_WIDTH, 0, getMeasuredWidth(), getMeasuredHeight(), mGradientPaint);
            mGradientPaint.setShader(gradient[3]);
            canvas.drawRect(0, getMeasuredHeight() - ERROR_RECT_WIDTH, getMeasuredWidth(), getMeasuredHeight(), mGradientPaint);
        }
        canvas.drawLines(mPresenter.getInnerLines(getCellWidth()), mInnerPaint);
        canvas.drawLines(mPresenter.getBoardLines(getCellWidth() * 3), mBoardPaint);
    }

    private void drawCompleteAnim(Canvas canvas) {
        drawCells(canvas, mExamData);
        int animIndex = mPresenter.getCurrentAnimIndex();
        boolean completeAnimEnd = false;
        if (animIndex >= mRightStep.size()) {
            animIndex = mRightStep.size() - 1;
            completeAnimEnd = true;
        }
        mTextPaint.setColor(Color.WHITE);
        float radius = (mCellWidth - 20) / 2;
        if (mBreathProgress > 0) {
            int alpha = (int) (mBreathProgress * 1.0f / 100 * 255);
            mTextPaint.setAlpha(alpha);
            mTextPaint.setTextSize(70f * mBreathProgress / 100);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
            radius = radius * mBreathProgress / 100f;
            mBoardPaint.setAlpha(alpha);
        }
        for (int i = 0; i <= animIndex; i++) {
            int[] step = mRightStep.get(i);
            String text = step[2] + "";
            float offsetX = mTextPaint.measureText(text) / 2;
            float centerX = mCellWidth * step[1] + mCellWidth / 2;
            float centerY = mCellWidth * step[0] + mCellWidth / 2;
            canvas.drawCircle(centerX, centerY, radius, mBoardPaint);
            canvas.drawText(text, centerX - offsetX, centerY + mTextOffsetY, mTextPaint);
        }
        if (completeAnimEnd) {
            if (mBreathProgress == -1) {
                mPresenter.doBreathAnim();
            }
        } else {
            mBoardListener.handleNextFrame(mRightStep.size());
        }
    }

    private void drawCells(Canvas canvas, Integer[][] data) {
        getCellWidth();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Integer currentValue = data[i][j];
                if (currentValue != 0) {
                    resetTextSize(i, j, mAnimProgress);
                    String text = currentValue.toString();
                    float offsetX = mTextPaint.measureText(text) / 2;
                    float centerX = mCellWidth * j + mCellWidth / 2;
                    float centerY = mCellWidth * i + mCellWidth / 2;
                    if (currentValue.equals(mSelectValue)) {
                        if (mTouchI == i && mTouchJ == j) {
                            canvas.drawRect(mCellWidth * mTouchJ, mCellWidth * mTouchI, mCellWidth * (mTouchJ + 1), mCellWidth * (mTouchI + 1), mBlankBgPaint);
                        }
                        canvas.drawCircle(centerX, centerY, (mCellWidth - 20) / 2, mBoardPaint);
                    }
                    mTextPaint.setColor(mExamData[i][j] == 0 ? currentValue.equals(mSelectValue) ? Color.WHITE : mColorBlue : Color.BLACK);
                    canvas.drawText(text, centerX - offsetX, centerY + mTextOffsetY, mTextPaint);
                } else {
                    if (currentValue.equals(mSelectValue) && i == mTouchI && j == mTouchJ) {
                        canvas.drawRect(mCellWidth * j, mCellWidth * i, mCellWidth * (j + 1), mCellWidth * (i + 1), mBlankBgPaint);
                    }
                    TreeSet<Integer> currentMarks = mMarkInfo[i][j];
                    if (currentMarks != null && currentMarks.size() > 0) {
                        float baseX = mCellWidth * j;
                        float baseY = mCellWidth * i;
                        for (Integer mark : currentMarks) {
                            float markTextCenterX = baseX + ((mark - 1) % 3 + 0.5f) * mCellWidth / 3;
                            float markTextCenterY = baseY + ((mark - 1) / 3 + 0.5f) * mCellWidth / 3;
                            float offsetX = mMarkPaint.measureText(mark.toString()) / 2;
                            canvas.drawText(mark.toString(), markTextCenterX - offsetX, markTextCenterY + mMarkTextOffsetY, mMarkPaint);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mTmpData == null) {
                return false;
            }
            if (mPresenter.isStartCompleteAnim()) {
                if (mBreathProgress > -2) {
                    return true;
                } else {
                    mPresenter.setStartCompleteAnim(false);
                }
            }
            float x = event.getX();
            float y = event.getY();
            int touchJ = (int) (x / mCellWidth);
            int touchI = (int) (y / mCellWidth);
            Integer selectValue = mTmpData[touchI][touchJ];
            if (touchI == mTouchI && touchJ == mTouchJ) {
                mSelectValue = mSelectValue == null ? selectValue : null;
            } else {
                mSelectValue = selectValue;
            }
            mTouchI = touchI;
            mTouchJ = touchJ;
            invalidate();
        }
        return true;
    }

    private float getCellWidth() {
        if (mCellWidth == 0) {
            mCellWidth = getMeasuredWidth() / 3f / 3f;
        }
        return mCellWidth;
    }

    @Override
    public void onTextClick(Integer value) {
        if (mTmpData == null || mSelectValue == null || mPresenter.isStartCompleteAnim() || mBreathProgress == -2) {
            return;
        }
        if (!mExamData[mTouchI][mTouchJ].equals(0)) {
            return;
        }

        if (!mIsMark || value == 0) {//改变cell值或清除mark值
            Integer lastValue = mTmpData[mTouchI][mTouchJ];
            boolean willChanged = !lastValue.equals(value) || mMarkInfo[mTouchI][mTouchJ] != null;
            if (willChanged) {
                onValueChanged(value);
                mTmpData[mTouchI][mTouchJ] = value;
                mSelectValue = value;
                invalidate();
            }
            if (Utils.checkInputFinish(mTmpData)) {
                boolean success = Arithmetic.checkResult(mTmpData);
                if (success) {
                    startCompleteAnim();
                    if (mBoardListener != null) {
                        mBoardListener.onSolved();
                    }
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
        mSelectValue = null;
        mBreathProgress = -1;
        mPresenter.prepareStartCompleteAnimData(mRightStep, mTmpData);
        invalidate();
    }

    @Override
    public void onPreStepClick() {
        ArrayList<CellHistoryBean> changes = mHistoryList.pollLast();
        if (changes != null) {
            // 撤销变化
            for (CellHistoryBean bean : changes) {
                mMarkInfo[bean.getX()][bean.getY()] = bean.getMarkValue();
                mTmpData[bean.getX()][bean.getY()] = bean.getMarkValue() != null ? 0 : bean.getValue();
            }

            // 选中上一步的cell
            if (mHistoryList.size() > 0) {
                ArrayList<CellHistoryBean> last = mHistoryList.getLast();
                CellHistoryBean prevEditCell = last.get(0);
                mTouchI = prevEditCell.getX();
                mTouchJ = prevEditCell.getY();
                mSelectValue = mTmpData[mTouchI][mTouchJ];
            } else {
                mSelectValue = null;
                mTouchI = 0;
                mTouchJ = 0;
                mMarkInfo = new TreeSet[9][9];
            }
            invalidate();
            updateCounts();
        }
    }

    @Override
    public void onSaveClick() {
        if (mExamData == null) {
            return;
        }
        if (Arithmetic.checkSudoEqually(mExamData, mTmpData) && isMarkEmpty()) {
            ToastUtil.showShortToast(getContext(), "没有可保存的数据");
            return;
        }

        if (mBoardListener!=null){
            mBoardListener.saveArchive(mExamData, mTmpData);
        }
    }

    private void onCellMark(Integer value) {
        // 存操作记录
        ArrayList<CellHistoryBean> historyStep = new ArrayList<>();
        if (mMarkInfo[mTouchI][mTouchJ] == null) {
            historyStep.add(new CellHistoryBean(mTouchI, mTouchJ, mTmpData[mTouchI][mTouchJ]));
        } else {
            historyStep.add(new CellHistoryBean(mTouchI, mTouchJ, new TreeSet<>(mMarkInfo[mTouchI][mTouchJ])));
        }
        mHistoryList.add(historyStep);

        // 标记cell的可用值
        mSelectValue = 0;
        mTmpData[mTouchI][mTouchJ] = 0;
        if (mMarkInfo[mTouchI][mTouchJ] == null) {
            mMarkInfo[mTouchI][mTouchJ] = new TreeSet<>();
        }
        if (mMarkInfo[mTouchI][mTouchJ].contains(value)) {
            mMarkInfo[mTouchI][mTouchJ].remove(value);
        } else {
            mMarkInfo[mTouchI][mTouchJ].add(value);
        }
    }

    private void onValueChanged(Integer value) {
        if (value != 0) {
            mRightStep.add(new int[]{mTouchI, mTouchJ, value});
        }

        ArrayList<CellHistoryBean> historyStep = new ArrayList<>();

        // 设置值时清空标注
        if (mMarkInfo[mTouchI][mTouchJ] != null) {
            historyStep.add(new CellHistoryBean(mTouchI, mTouchJ, new TreeSet<>(mMarkInfo[mTouchI][mTouchJ])));
            mMarkInfo[mTouchI][mTouchJ] = null;
        } else {
            historyStep.add(new CellHistoryBean(mTouchI, mTouchJ, mTmpData[mTouchI][mTouchJ]));
        }

        // 减少标记
        if (value != 0) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (mMarkInfo[i][j] != null && (
                            i == mTouchI || j == mTouchJ
                                    || i >= mTouchI / 3 * 3 && i < mTouchI / 3 * 3 + 3 && j >= mTouchJ / 3 * 3 && j < mTouchJ / 3 * 3 + 3)) {
                        historyStep.add(new CellHistoryBean(i, j, new TreeSet<>(mMarkInfo[i][j])));
                        mMarkInfo[i][j].remove(value);
                    }
                }
            }
        }

        mHistoryList.add(historyStep);
    }

    @Override
    public void onResetClick() {
        if (Arithmetic.checkSudoEqually(mTmpData, mExamData) && isMarkEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("确认清除所有输入数据，恢复到初始状态？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(true);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetAll();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void resetAll() {
        mTmpData = Arithmetic.copySudo(mExamData);
        mSelectValue = null;
        mTouchI = 0;
        mTouchJ = 0;
        mMarkInfo = new TreeSet[9][9];
        mHistoryList.clear();

        mBreathProgress = -1;
        mHasPopComplete = false;
        mAnimProgress = 200;
        mRightStep.clear();
        mPresenter.setStartCompleteAnim(false);
        if (mBoardListener != null) {
            mBoardListener.onReset();
            mBoardListener.onTextChanged(mTmpData);
        }
        invalidate();
    }

    private boolean isMarkEmpty() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (mMarkInfo[i][j] != null && mMarkInfo[i][j].size() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onMarkClick(boolean mark) {
        mIsMark = mark;
    }

    public void setBoardListener(IBoardListener listener) {
        mBoardListener = listener;
    }

    @Override
    public void onInflateAnimProgress(int value) {
        mAnimProgress = value;
        invalidate();
    }

    @Override
    public void onBreathAnimProgress(int value) {
        mBreathProgress = value;
        invalidate();
    }

    @Override
    public void onBreathAnimEnd() {
        invalidate();
        mBreathProgress = -2;
        // 第一次完成才跳转
        if (!mHasPopComplete && mBoardListener != null) {
            mBoardListener.jumpNext();
            mHasPopComplete = true;
        }
    }

    @Override
    public void onErrorAnimProgress(int value) {
        invalidate();
    }

    public interface IBoardListener {
        void onSolved();

        void handleNextFrame(int size);

        void jumpNext();

        void onReset();

        void onTextChanged(Integer[][] sudo);

        void saveArchive(Integer[][] examData, Integer[][] tmpData);
    }
}
