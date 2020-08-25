package com.vachel.sudo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vachel.sudo.R;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.icon.LevelItem;

import java.util.List;


/**
 * Created by jianglixuan on 2020/8/25.
 * Describe:
 */
public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Examination> mData;
    private IOnItemClickListener mListener;

    public LevelAdapter(Context context, IOnItemClickListener listener,  @NonNull List<Examination> data) {
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
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
        levelItem.setLevelText((position + 1) + "");
        Examination examination = mData.get(position);
        long takeTime = examination.getTakeTime();
        if (takeTime > 0) {
            String take = Utils.parseTakeTime(takeTime, 0);
            levelItem.setText((position + 1) + "", take);
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
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LevelItem tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.level_text);
        }
    }

    public interface IOnItemClickListener {
        void onItemClick(int position);
    }
}
