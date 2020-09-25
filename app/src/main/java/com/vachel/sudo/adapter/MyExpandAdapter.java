package com.vachel.sudo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vachel.sudo.R;
import com.vachel.sudo.bean.SlideBean;
import com.vachel.sudo.bean.SlideItem;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class MyExpandAdapter extends BaseExpandableListAdapter {
    private List<SlideBean> mData;
    private final LayoutInflater mInflater;

    public MyExpandAdapter(@NonNull Context context) {
        mData = new ArrayList<>();
        SlideBean slideBean = new SlideBean();
        slideBean.setTitle("通用设置");

        ArrayList<SlideItem> childList = new ArrayList<>();
        SlideItem slideItem = new SlideItem();
        slideItem.setItemText("显示数独计时器");
        slideItem.setChecked(true);
        childList.add(slideItem);

        SlideItem slideItem1 = new SlideItem();
        slideItem1.setItemText("显示数独内1-9数字剩余可用次数");
        slideItem.setChecked(true);
        childList.add(slideItem1);

        SlideItem slideItem2 = new SlideItem();
        slideItem2.setItemText("闯关玩法关卡列表读取存档提示弹窗");
        slideItem.setChecked(true);
        childList.add(slideItem2);

        SlideItem slideItem3 = new SlideItem();
        slideItem3.setItemText("退出关卡时弹出未保存提示");
        slideItem3.setChecked(true);
        childList.add(slideItem3);

        slideBean.setSlideItems(childList);
        mData.add(slideBean);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).getSlideItems().size();
    }

    @Override
    public SlideBean getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public SlideItem getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getSlideItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.slide_expand_title, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.item_title);
        textView.setText(getGroup(groupPosition).getTitle());
        ImageView parentImageViw = convertView.findViewById(R.id.item_arrow);
        if (isExpanded) {
            parentImageViw.setBackgroundResource(R.mipmap.angle_down);
        } else {
            parentImageViw.setBackgroundResource(R.mipmap.angle_right);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.slide_expand_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.item_text);
        textView.setText(getChild(groupPosition, childPosition).getItemText());
        final ImageView checkBox = convertView.findViewById(R.id.item_checkbox);
        String itemKey = getItemKey(childPosition);
        boolean initValue = PreferencesUtils.getBooleanPreference(parent.getContext(), itemKey, getItemKeyDefaultValue(childPosition));
        checkBox.setSelected(initValue);
        convertView.setOnClickListener(v -> {
            boolean selected = !checkBox.isSelected();
            checkBox.setSelected(selected);
            onItemClick(parent.getContext(), childPosition, selected);

        });
        return convertView;
    }

    private void onItemClick(Context context, int position, boolean selected) {
        String key = getItemKey(position);
        if (!TextUtils.isEmpty(key)) {
            PreferencesUtils.setBooleanPreference(context, key, selected);
        }
    }

    private String getItemKey(int position) {
        String key = null;
        if (position == 0) {
            key = Constants.SHOW_TIMER;
        } else if (position == 1) {
            key = Constants.REMAINING_USEFUL_COUNTS;
        } else if (position == 2) {
            key = Constants.SHOW_RESUME_ARCHIVE_TIPS;
        } else if (position == 3) {
            key = Constants.SAVE_TIPS_WHILE_EXIT;
        }
        return key;
    }

    private boolean getItemKeyDefaultValue(int position) {
        boolean value = false;
        if (position == 0) {
            value = true;
        } else if (position == 1) {
            value = true;
        } else if (position == 2) {
            value = true;
        } else if (position == 3) {
            value = false;
        }
        return value;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
