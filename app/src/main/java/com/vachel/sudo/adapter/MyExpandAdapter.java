package com.vachel.sudo.adapter;

import android.content.Context;
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
            parentImageViw.setBackgroundResource(R.mipmap.checked_blue_bg);
        } else {
            parentImageViw.setBackgroundResource(R.mipmap.uncheck_grey_border);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.slide_expand_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.item_text);
        textView.setText(getChild(groupPosition, childPosition).getItemText());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
