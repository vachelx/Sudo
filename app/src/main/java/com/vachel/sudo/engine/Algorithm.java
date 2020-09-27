package com.vachel.sudo.engine;

import com.vachel.sudo.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class Algorithm {
    public static Integer[][] randomSudo() {
        Integer[][] sudo;
        TreeSet<Integer> remainingNumbers = new TreeSet<>();
        for (int n = 1; n <= 9; n++) {
            remainingNumbers.add(n);
        }

        Random random = new Random();
        do {
            sudo = new Integer[9][9];
        } while (!performTraverse(sudo, remainingNumbers, random));

        return sudo;
    }

    private static boolean performTraverse(Integer[][] sudo, TreeSet<Integer> remainingNumbers, Random random) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; ) {
                if (System.currentTimeMillis() - start > 30) {
                    return false;
                }
                TreeSet<Integer> cellRemainNumbers = new TreeSet<>(remainingNumbers);
                for (int x = 0; x < j; x++) {
                    if (sudo[i][x] != null) {
                        cellRemainNumbers.remove(sudo[i][x]);
                    }
                }

                for (int y = 0; y < i; y++) {
                    if (sudo[y][j] != null) {
                        cellRemainNumbers.remove(sudo[y][j]);
                    }
                }

                int currentRow = i / 3;
                int currentColumn = j / 3;
                for (int rowX = currentRow * 3; rowX < currentRow * 3 + 3; rowX++) {
                    for (int columnY = currentColumn * 3; columnY < currentColumn * 3 + 3; columnY++) {
                        if (sudo[rowX][columnY] != null) {
                            cellRemainNumbers.remove(sudo[rowX][columnY]);
                        }
                    }
                }
                if (cellRemainNumbers.size() ==0){
                    j = 0;
                    for (int p = 0; p < 9;p++){
                        sudo[i][p] = null;
                    }
                } else {
                    List<Integer> lst = new ArrayList<>(cellRemainNumbers);
                    int index = random.nextInt(cellRemainNumbers.size());
                    Integer cell = lst.get(index);
                    sudo[i][j] = cell;
                    j++;
                }
            }
        }
        return true;
    }

    public static Integer[][] getExamSudo(int type){
        return getExamSudo(randomSudo(), type);
    }

    private static Integer[][] getExamSudo(Integer[][] sudo, int type) {
        Integer[][]  topicSudo = new Integer[9][9];
        int blankCount = getBlankCount(type);
        Random random = new Random();
        while(blankCount!=0){
            int randomNumber = random.nextInt(81);
            int row = randomNumber/9;
            int column = randomNumber%9;
            if (topicSudo[row][column]==null){
                topicSudo[row][column] = 0;
                blankCount--;
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (topicSudo[i][j] == null){
                    topicSudo[i][j] = sudo[i][j];
                }
            }
        }

        return topicSudo;
    }

    private static int getBlankCount(int type) {
        if (type == 0) {
            return BuildConfig.DEBUG ? 5 : 37; // 容易
        } else if (type == 1) {
            return 46; // 中等
        } else if (type == 2) {
            return 51; // 困难
        } else {
            return 55; // 专家
        }
    }

    public static boolean checkSudoEqually(Integer[][] sudo, Integer[][] targetSudo) {
        if (sudo == null && targetSudo == null) {
            return true;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!sudo[i][j].equals(targetSudo[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Integer[][] copySudo(Integer[][] sudo) {
        Integer[][] newSudo = new Integer[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(sudo[i], 0, newSudo[i], 0, 9);
        }
        return newSudo;
    }

    // 检查结果
    public static boolean checkResult(Integer[][] sudo){
        TreeSet<Integer> remainingNumbers = new TreeSet<>();
        for (int n = 1; n <= 9; n++) {
            remainingNumbers.add(n);
        }

        for (int i = 0; i < 9; i++) {
            TreeSet<Integer> rowRemainNumbers = new TreeSet<>(remainingNumbers);
            TreeSet<Integer> columnRemainNumbers = new TreeSet<>(remainingNumbers);
            TreeSet<Integer> gridRemainNumbers = new TreeSet<>(remainingNumbers);
            for (int j = 0; j < 9; j++) {
                if (!rowRemainNumbers.remove(sudo[i][j])) {
                    return false;
                }
                if (!columnRemainNumbers.remove(sudo[j][i])) {
                    return false;
                }
                if (!gridRemainNumbers.remove(sudo[i/3*3+j / 3][i%3*3+j % 3])) {
                    return false;
                }
            }
        }
        return true;
    }


    private static<T> void printSudo(T[][] sudo) {
        System.out.println("*******************************");
        for (int i = 0; i < 9; i++) {
            if (i%3==0){
                System.out.println("-------------------------------------");
            }
            for (int k = 0; k < 9; k++) {
                if (k%3==0){
                    System.out.print("|  ");
                }
                System.out.print(sudo[i][k] + "  ");
                if (k == 8) {
                    System.out.println();
                }
            }
        }
    }
}
