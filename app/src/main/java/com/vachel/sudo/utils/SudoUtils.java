package com.vachel.sudo.utils;

import java.util.ArrayList;

public class SudoUtils {
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
}
