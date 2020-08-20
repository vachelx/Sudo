package com.vachel.sudo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class Arithmetic {
    public static Integer[][] randomSudo() {
        Integer[][] sudo = new Integer[9][9];
        TreeSet<Integer> remainingNumbers = new TreeSet<>();
        for (int n = 1; n <= 9; n++) {
            remainingNumbers.add(n);
        }

        Random random = new Random();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; ) {
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
        return sudo;
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

        // 结果不唯一时，减少一个空白格
//        while (!checkSolutionSole(topicSudo, sudo)) {
//            int row;
//            int column;
//            do {
//                int randomNumber = random.nextInt(81);
//                row = randomNumber / 9;
//                column = randomNumber % 9;
//            } while (topicSudo[row][column] != 0);
//            topicSudo[row][column] = sudo[row][column];
//        }

        return topicSudo;
    }

    private static int getBlankCount(int type) {
        if (type == 0) {
            return 37; // 容易
        } else if (type == 1) {
            return 46; // 中等
        } else if (type == 2) {
            return 51; // 困难
        } else {
            return 55; // 专家
        }
    }

    private static boolean checkSolutionSole(Integer[][] sudo, Integer[][] targetSudo) {
        int randomCount = 3;
        while (randomCount != 0) {
            Integer[][] randomSolution = getRandomSolution(copySudo(sudo));
            boolean equally = checkSudoEqually(randomSolution, targetSudo);
            if (!equally) {
                return false;
            }
            randomCount--;
        }
        return true;
    }

    static class MarkKnife{
        int markI;
        int markJ;
        List<Integer> markRemaining;

        MarkKnife(int i, int j, TreeSet<Integer> mark){
            markI = i;
            markJ = j;
            markRemaining = new ArrayList<>(mark);
        }
    }

    private static Integer[][] getRandomSolution(Integer[][] sudo){
        Integer[][] sourceSudo = copySudo(sudo);
        Random random = new Random();

        MarkKnife markKnife = getMarkKnife(sourceSudo);
        if (markKnife == null){

        }
        return sudo;
    }

    private static MarkKnife getMarkKnife(Integer[][] sudo){
        TreeSet<Integer> remainingNumbers = new TreeSet<>();
        for (int n = 1; n <= 9; n++) {
            remainingNumbers.add(n);
        }

        TreeSet<Integer>[][] cellRemainNumbers = new TreeSet[9][9];
        for (int c = 0; c < 9; c++) {
            for (int k = 0; k < 9; k++) {
                cellRemainNumbers[c][k] = new TreeSet<>(remainingNumbers);
            }
        }

        int markI = -1;
        int markJ = -1;
        int minSize = 10;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudo[i][j] != 0){
                    continue;
                }
                for (int x = 0; x < 9; x++) {
                    if (sudo[i][x] != 0) {
                        cellRemainNumbers[i][j].remove(sudo[i][x]);
                    }
                }
                for (int y = 0; y < 9; y++) {
                    if (sudo[y][j] != 0) {
                        cellRemainNumbers[i][j].remove(sudo[y][j]);
                    }
                }
                int currentRow = i / 3;
                int currentColumn = j / 3;
                for (int rowX = currentRow * 3; rowX < currentRow * 3 + 3; rowX++) {
                    for (int columnY = currentColumn * 3; columnY < currentColumn * 3 + 3; columnY++) {
                        if (sudo[rowX][columnY] != 0) {
                            cellRemainNumbers[i][j].remove(sudo[rowX][columnY]);
                        }
                    }
                }

                int size = cellRemainNumbers[i][j].size();
                if (size==0){
                    return null;
                } else if (cellRemainNumbers[i][j].size() == 1) {
                    List<Integer> lst = new ArrayList<>(cellRemainNumbers[i][j]);
                    sudo[i][j] = lst.get(0);
                } else if (cellRemainNumbers[i][j].size() < minSize){
                    minSize=cellRemainNumbers[i][j].size();
                    markI = i;
                    markJ = j;
                }
            }
        }
        // markI为-1处理
        return new MarkKnife(markI, markJ, cellRemainNumbers[markI][markJ]);
    }


    public static boolean checkSudoEqually(Integer[][] sudo, Integer[][] targetSudo) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!sudo[i][j].equals(targetSudo[i][j])){
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
