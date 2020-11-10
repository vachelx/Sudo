package com.vachel.sudo.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.vachel.sudo.MyApplication;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.engine.Algorithm;
import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.service.ExamsCreateService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

public class Utils {
    public static final String MEDIA_SAVE_PATH = Environment.DIRECTORY_DCIM + "/sudo/";

    public static final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

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

    public static String getExamKey(int mode, int difficulty, int version, int index) {
        return mode + "_" + difficulty + "_" + version + "_" + index;
    }

    public static String getExamKey(int[] cruxKeys) {
        return cruxKeys[0] + "_" + cruxKeys[1] + "_" + cruxKeys[2] + "_" + cruxKeys[3];
    }

    // 存档的key 随机模式不需要使用version和index进行组合
    public static String getArchiveKey(int[] cruxKeys) {
        if (cruxKeys[0] == 0) {
            return cruxKeys[0] + "_" + cruxKeys[1] + "_" + 0 + "_" + 0;
        }
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

    public static String getlevelItemShowTime(long takeTime) {
        takeTime /= 1000;
        int hour = (int) (takeTime / 60 / 60);
        int minute = (int) (takeTime / 60 % 60);
        int second = (int) (takeTime % 60);
        StringBuilder text = new StringBuilder();
        if (hour >= 100) {
            return text.append(hour).append("h").toString();
        }
        if (hour > 0) {
            return text.append(hour).append("h").append(minute).append("m").toString();
        }
        return text.append(minute).append(":").append(second).append("\"").toString();
    }

    public static String parseDate(long time) {
        Date date = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static String sudoMarksToString(TreeSet<Integer>[][] marks) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (marks[i][j] != null) {
                    builder.append(i).append(":").append(j).append(":");
                    TreeSet<Integer> values = marks[i][j];
                    for (Integer mark: values) {
                        builder.append(mark);
                    }
                    builder.append(";");
                }
            }
        }
        return builder.toString();
    }

    public static TreeSet<Integer>[][] parseMarks(@NonNull String marks) {
        TreeSet<Integer>[][] result = new TreeSet[9][9];
        String[] split = marks.split(";");
        for (String s : split) {
            if (!TextUtils.isEmpty(s)) {
                String[] info = s.split(":");
                if (info.length == 3) {
                    TreeSet<Integer> mark = new TreeSet<>();
                    for (int j = 0; j < info[2].length(); j++) {
                        mark.add(Integer.valueOf(info[2].substring(j, j + 1)));
                    }
                    result[Integer.valueOf(info[0])][Integer.valueOf(info[1])] = mark;
                }
            }
        }
        return result;
    }

    public static int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static synchronized boolean checkNetworkAvailable(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getSaveFilePath(String fileName) {
        File sdCard = Environment.getExternalStorageDirectory();
        File outputDirectory = new File(sdCard.getAbsolutePath() + File.separator + "sudo");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        return outputDirectory.getAbsolutePath() + File.separator + fileName;
    }

    public static boolean saveBitmapFile(Bitmap bitmap, String filePath) {
        if (bitmap == null) { //
            return false;
        }
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean saveBitmapWithAndroidQ(Bitmap bitmap, String displayName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, MEDIA_SAVE_PATH);
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = MyApplication.getContext().getContentResolver();
        Uri insertUri = contentResolver.insert(uri, contentValues);
        if (insertUri != null) {
            try (OutputStream outputStream = contentResolver.openOutputStream(insertUri)){
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void initCreateAllExams(final ExamsCreateService.ICreateExamsCallback callback) {
        if (PreferencesUtils.getBooleanPreference(MyApplication.getInstance(), Constants.HAS_CREATE_ALL_EXAMS, false)) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            int count = 999;
            for (int j = 0; j < count; j++) {
                Integer[][] sudo = Algorithm.getExamSudo(i);
                String examKey = Utils.getExamKey(1, i, 0, j);
                Examination examination = new Examination(examKey, Utils.sudoToString(sudo), i, j, 3,
                        0, 0, 1, 0);
                final int currentIndex = j;
                ThreadPoolX.getThreadPool().execute(() -> {
                    ExamDataManager.getInstance().addOrUpdateExamination(examination);
                    callback.onCreateIndex(currentIndex);
                });
            }
        }
        PreferencesUtils.setBooleanPreference(MyApplication.getInstance(), Constants.HAS_CREATE_ALL_EXAMS, true);
    }
}
