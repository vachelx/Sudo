package com.vachel.sudo.presenter;

import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.dao.Record;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.manager.RecordDataManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.Utils;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class SudoPresenter {
    public void saveSolvedRecord(int[] cruxKey, long useTime, long completeTime, Integer[][] examSudo) {
        String examKey = Utils.getExamKey(cruxKey);
        Record newRecord = new Record(examKey, useTime, completeTime, cruxKey[Constants.DIFFICULTY], cruxKey[Constants.MODE]);
        RecordDataManager.getInstance().addOrUpdateRecord(newRecord);
        Examination examination = new Examination(examKey, Utils.sudoToString(examSudo), cruxKey[Constants.DIFFICULTY], cruxKey[Constants.INDEX], 3,
                useTime, completeTime, cruxKey[Constants.MODE], cruxKey[Constants.VERSION]);
        ExamDataManager.getInstance().addOrUpdateExamination(examination);
    }

    public long checkRecord(int[] cruxKey, long completeTime) {
        String examKey = Utils.getExamKey(cruxKey);
        Record record = RecordDataManager.getInstance().getRecord(examKey);
        long result = 0; // 0为闯关成功，无历史记录，-1表示刷新本题的记录，大于0表示未刷新记录，返回值即为最好记录
        if (record != null) {
            result = completeTime < record.getTakeTime() ? -1 : record.getTakeTime();
        }
        return result;
    }
}
