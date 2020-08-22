package com.vachel.sudo.utils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    // 检查填写完整度
    public static boolean checkInputFinish(Integer[][] sudo) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudo[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static float[] getInnerLines(float cellWidth) {
        ArrayList<Float> lines = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i % 3 != 0) {
                    lines.add(cellWidth * j);
                    lines.add(cellWidth * i);
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * i);
                }

                if (i % 3 != 2) {
                    lines.add(cellWidth * j);
                    lines.add(cellWidth * (i + 1));
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * (i + 1));
                }

                if (j % 3 != 0) {
                    lines.add(cellWidth * j);
                    lines.add(cellWidth * (i));
                    lines.add(cellWidth * (j));
                    lines.add(cellWidth * (i + 1));
                }

                if (j % 3 != 2) {
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * (i));
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * (i + 1));
                }
            }
        }
        float[] innerLines = new float[lines.size()];
        for (int k = 0; k < lines.size(); k++) {
            innerLines[k] = lines.get(k);
        }
        return innerLines;
    }

    public static String getExamKey(int mode, int difficulty, int version, int index) {
        return mode + "_" + difficulty + "_" + version + "_" + index;
    }

    public static String getExamKey(int[] cruxKeys) {
        return cruxKeys[0] + "_" + cruxKeys[1] + "_" + cruxKeys[2] + "_" + cruxKeys[3];
    }

    public static int getNextExamIndex(@NonNull String currentExamKey) {
        String[] split = currentExamKey.split("-");
        return Integer.valueOf(split[3]);
    }

    public static String sudoToString(Integer[][] sudo) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                builder.append(sudo[i][j]);
            }
        }
        return builder.toString();
    }

    public static Integer[][] parseSudo(@NonNull String sudo) {
        if (sudo.length() != 81) {
            throw new RuntimeException("parse sudo error! sudo = " + sudo);
        }
        Integer[][] result = new Integer[9][9];
        for (int i = 0; i < sudo.length(); i++) {
            result[i / 9][i % 9] = Integer.valueOf(sudo.substring(i, i + 1));
        }
        return result;
    }

    /*  type:
        0   冒号；
        1   h：min：s
        2   时分秒 */
    public static String parseTakeTime(long takeTime, int type) {
        takeTime /= 1000;
        int hour = (int) (takeTime / 60 / 60);
        int minute = (int) (takeTime / 60 % 60);
        int second = (int) (takeTime % 60);
        StringBuilder text = new StringBuilder();
        if (hour > 0) {
            text.append(hour).append(type == 0 ? ":" : "h");
        }
        if (minute < 10) {
            text.append(0);
        }
        text.append(minute).append(type == 0 ? ":" : "min");
        if (second < 10) {
            text.append(0);
        }
        text.append(second);
        text.append(type == 0 ? "\"" : "s");
        return text.toString();
    }

    public static String parseDate(long time) {
        Date date = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}