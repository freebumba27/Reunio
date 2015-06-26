package com.altaoferta.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.altaoferta.reunio.R;

import java.util.ArrayList;

/**
 * Created by Dream on 25-Jun-15.
 */
public class CustomAdapter extends BaseAdapter {

    Context con;
    String[] shiftValueArray;
    private LayoutInflater inflater;
    private ArrayList<CustomObject> objects;

    public CustomAdapter(Context context, ArrayList<CustomObject> objects, String[] shiftArray) {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
        con = context;
        shiftValueArray = shiftArray;
    }

    public int getCount() {
        return objects.size();
    }

    public CustomObject getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.simplerow, null);
            holder.textView1 = (TextView) convertView.findViewById(R.id.rowTextView1);
            holder.textView2 = (TextView) convertView.findViewById(R.id.rowTextView2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        for (int i = 1; i < 14; i++) {
            ((RelativeLayout) convertView.findViewById(R.id.RelativeLayoutSingleRow)).setBackgroundResource(R.drawable.green_bg);
        }

        for (int i = 0; i < shiftValueArray.length; i++) {
            if (shiftValueArray[i].equalsIgnoreCase(objects.get(position).getprop1()))
                ((RelativeLayout) convertView.findViewById(R.id.RelativeLayoutSingleRow)).setBackgroundResource(R.drawable.red_bg);
        }

        holder.textView1.setText(objects.get(position).getprop3());
        holder.textView2.setText(objects.get(position).getprop2());
        holder.textView2.setVisibility(View.GONE);
        return convertView;
    }

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
    }
}