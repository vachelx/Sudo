package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.vachel.sudo.R;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.utils.Constants;

import java.util.List;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class LevelActivity extends BaseActivity {
    @Override
    int getLayoutId() {
        return R.layout.activity_level;
    }

    @Override
    void init() {
        final int difficulty = getIntent().getIntExtra(Constants.KEY_DIFFICULTY, 0);
        final ListView listView = findViewById(R.id.level_list);
        List<Examination> exams = ExamDataManager.getInstance().getExamsByDiff(1, difficulty);
        listView.setAdapter(new ListAdapter(exams));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LevelActivity.this, SudoActivity.class);
                int[] nextKey = new int[4];
                nextKey[0] = 1;
                nextKey[1] = difficulty;
                nextKey[2] = 0;
                nextKey[3] = position;
                intent.putExtra(Constants.KEY_EXAM, nextKey);
                startActivity(intent);
            }
        });
    }

    static class ListAdapter extends BaseAdapter{
        List<Examination> data;
        ListAdapter(List<Examination> exams){
            data = exams;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Examination getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setText("第"+(getItem(position).getSortIndex()+1)+"关");
            return textView;
        }
    }
}
