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
        Record record = new Record(examKey, useTime, completeTime,cruxKey[Constants.DIFFICULTY], cruxKey[Constants.INDEX]);
        RecordDataManager.getInstance().addOrUpdateRecord(record);

        Examination examination = new Examination(examKey, Utils.sudoToString(examSudo),cruxKey[Constants.DIFFICULTY], cruxKey[Constants.INDEX], 3,
                useTime, completeTime, cruxKey[Constants.MODE], cruxKey[Constants.VERSION]);
        ExamDataManager.getInstance().addOrUpdateExamination(examination);
    }
}
