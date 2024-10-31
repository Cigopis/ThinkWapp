package com.wongcoco.thinkwapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class PanduanExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private static final int ROTATE_DURATION = 300;
    private boolean[] groupExpanded; // Array untuk menyimpan status grup yang diperluas

    public PanduanExpandableListAdapter(Context context, List<String> listDataHeader,
                                        HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.groupExpanded = new boolean[listDataHeader.size()]; // Inisialisasi status grup
    }

    public boolean isGroupExpanded(int groupPosition) {
        return groupExpanded[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
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
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }

        TextView lblListHeader = convertView.findViewById(R.id.lblListItem);
        lblListHeader.setText(headerTitle);

        // Setup ImageView dan rotasi animasi
        ImageView dropdownIcon = convertView.findViewById(R.id.dropdownIcon);

        // Set rotasi berdasarkan status ekspansi saat ini
        dropdownIcon.clearAnimation();
        float rotationAngle = isExpanded ? 180 : 0;
        dropdownIcon.setRotation(rotationAngle);

        // Cek dan atur status grup
        if (groupExpanded[groupPosition] != isExpanded) {
            groupExpanded[groupPosition] = isExpanded;

            // Rotasi animasi
            RotateAnimation rotateAnimation = new RotateAnimation(
                    isExpanded ? 0 : 180, // Dari degree
                    isExpanded ? 180 : 0, // Ke degree
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(ROTATE_DURATION);
            rotateAnimation.setFillAfter(true);
            dropdownIcon.startAnimation(rotateAnimation);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
