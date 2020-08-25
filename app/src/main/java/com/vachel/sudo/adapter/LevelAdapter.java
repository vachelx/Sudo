package com.vachel.sudo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vachel.sudo.R;
import com.vachel.sudo.dao.ArchiveBean;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.manager.ArchiveDataManager;
import com.vachel.sudo.utils.InnerHandler;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.icon.LevelItem;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by jianglixuan on 2020/8/25.
 * Describe:
 */
public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> implements InnerHandler.IMessageHandler {
    private final LayoutInflater mLayoutInflater;
    private List<Examination> mData;
    private IOnItemClickListener mListener;
    private final InnerHandler mHandler;

    public LevelAdapter(Context context, IOnItemClickListener listener, @NonNull List<Examination> data) {
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;

        mHandler = new InnerHandler(this);
    }

    public void updateExamination(Examination examination) {
        mData.set(examination.getSortIndex(), examination);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_level, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        LevelItem levelItem = holder.tvName;
        levelItem.resetText((position + 1) + "");
        Examination examination = mData.get(position);
        long takeTime = examination.getTakeTime();
        if (takeTime > 0) {
            String take = Utils.parseTakeTime(takeTime, 0);
            levelItem.resetText((position + 1) + "", take);
            levelItem.setClickable(true);
            levelItem.setOnClickListener(v -> mListener.onItemClick(position));
            levelItem.setSelected(false);
        } else if (position == 0 || mData.get(position - 1).getTakeTime() > 0) {
            // 当前最新关卡
            levelItem.setClickable(true);
            levelItem.setOnClickListener(v -> mListener.onItemClick(position));
            levelItem.setSelected(true);
        } else {
            levelItem.setClickable(false);
            levelItem.setSelected(false);
        }

        levelItem.setHasArchive(false);
        String examKey = mData.get(position).getKey();
        levelItem.setTag(examKey);
        ThreadPoolX.getThreadPool().execute(new ArchiveRunnable(mHandler, levelItem, examKey));
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            WeakReference<LevelItem> item = (WeakReference<LevelItem>) msg.obj;
            LevelItem levelItem = item.get();
            if (levelItem != null) {
                Bundle data = msg.getData();
                String key = data.getString("key", "");
                if (key.equals(levelItem.getTag())) {
                    levelItem.setHasArchive(true);
                    if (levelItem.hasNullTakeTime()) { // 没有通过记录时间时才展示存档已用时间
                        long takeTime = data.getLong("take_time", 0);
                        levelItem.updateTakeTime(Utils.parseTakeTime(takeTime, 0));
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LevelItem tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.level_text);
        }
    }

    public interface IOnItemClickListener {
        void onItemClick(int position);
    }

    static class ArchiveRunnable implements Runnable {
        WeakReference<LevelItem> reference;
        String examKey;
        Handler handler;

        ArchiveRunnable(Handler handler, LevelItem view, String key) {
            reference = new WeakReference<>(view);
            examKey = key;
            this.handler = handler;
        }

        @Override
        public void run() {
            ArchiveBean archive = ArchiveDataManager.getInstance().getArchive(examKey);
            if (archive!=null) {
                Message msg = Message.obtain();
                msg.obj = reference;
                Bundle bundle = new Bundle();
                bundle.putString("key", examKey);
                bundle.putLong("take_time", archive.getTakeTime());
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    }
}
