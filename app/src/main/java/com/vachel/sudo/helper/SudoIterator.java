package com.vachel.sudo.helper;

import androidx.annotation.NonNull;

public class SudoIterator {
    public static <T> void execute(@NonNull T[][] sudo, @NonNull IFoundCallback<T> callback) {
        for (int i = 0; i < sudo.length; i++) {
            T[] row = sudo[i];
            for (int j = 0; j < row.length; j++) {
                callback.onNext(i, j, sudo[i][j]);
            }
        }
    }

    public interface IFoundCallback<T> {
        void onNext(int i, int j, T value);
    }
}
