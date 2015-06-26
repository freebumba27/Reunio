package com.altaoferta.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.altaoferta.reunio.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dream on 25-Jun-15.
 */
public class CustomAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<CustomObject> objects;
    Context con;
    String[] shiftValueArray;

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
    }

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
        View v = convertView;
        if(v==null){
            v = inflater.inflate(R.layout.simplerow, null);
        }

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

        int time = 9;
        for (int i = 1; i < 9 ; i++) {
            if(Arrays.asList(shiftValueArray).contains("Shift -" + (position + i))) {
                Log.i("TAG", "matched"+(position + i));
                v.setBackgroundResource(R.drawable.red_bg);
            }
            else
            {
                Log.i("TAG", "not matched"+(position + i));
                v.setBackgroundResource(R.drawable.green_bg);
            }
            time++;
        }

        holder.textView1.setText(objects.get(position).getprop1());
        holder.textView2.setText(objects.get(position).getprop2());
        return convertView;
    }
}