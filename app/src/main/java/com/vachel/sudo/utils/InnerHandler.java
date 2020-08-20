package com.vachel.sudo.utils;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Created by jianglixuan on 2020/8/20.
 * Describe: 避免内存泄漏的Handler类
 * <p>
 * 普通对象用IMessageHandler
 * 有生命周期的对象用ILifeCycleMessageHandler（特指Activity）
 * <p>
 * （在基类中例如BaseActivity中使用需要注意子类可能复写handleMessage的可能）
 */
public class InnerHandler extends Handler {
    private WeakReference<IMessageHandler> mReference;

    public InnerHandler(IMessageHandler handler) {
        mReference = new WeakReference<>(handler);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        IMessageHandler handler = mReference.get();
        if (handler == null) {
            return;
        }
        if (handler instanceof ILifeCycleMessageHandler && ((ILifeCycleMessageHandler) handler).isFinishing()) {
            return;
        }
        handler.handleMessage(msg);
    }

    public interface IMessageHandler {
        void handleMessage(Message msg);
    }

    public interface ILifeCycleMessageHandler extends IMessageHandler {
        void handleMessage(Message msg);

        boolean isFinishing();
    }
}
