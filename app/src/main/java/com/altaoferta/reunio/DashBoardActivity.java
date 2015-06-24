package com.altaoferta.reunio;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.altaoferta.utils.ReusableClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity {

    EditText editTextDatePicker;
    ListView myListView;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        editTextDatePicker = (EditText) findViewById(R.id.editTextDatePicker);

        myListView = (ListView) findViewById(R.id.myListView);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Log.d("TAG", "value: " + data.get(position).get("shift"));
                Log.d("TAG", "value: " + data.get(position).get("shift_time"));

                Intent i = new Intent(DashBoardActivity.this, ConfirmShiftActivity.class);
                i.putExtra("shift", data.get(position).get("shift"));
                i.putExtra("shift_time", data.get(position).get("shift_time"));
                finish();
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //Date picker
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void datePicker(View v) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(DashBoardActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                String selectedmonthModified = (selectedmonth + 1) / 10 == 0 ? ("0" + (selectedmonth + 1)) : String.valueOf((selectedmonth + 1));

                editTextDatePicker.setText(new StringBuilder()
                        .append(selectedday).append("-")
                        .append(selectedmonthModified).append("-")
                        .append(selectedyear).append(" "));
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.show();
    }

    public void searchingShifts(View view) {
        if (editTextDatePicker.getText().toString().equalsIgnoreCase("Select date")) {
            Toast.makeText(this, "Please select a date first !!", Toast.LENGTH_LONG).show();
        } else {
            SQLiteDatabase db = ReusableClass.createAndOpenDb(this);
            Cursor cur = db.rawQuery("SELECT * FROM booked_shift_tbl WHERE shift_date = '" + editTextDatePicker.getText().toString() + "'", null);
            String shiftArray[] = new String[cur.getCount()];
            int j = 0;
            if (cur.moveToNext()) {
                do {
                    shiftArray[j] = cur.getString(2);
                    j++;
                } while (cur.moveToNext());
            }
            cur.close();
            db.close();

            if (cur.getCount() != 8)
                ((TextView) findViewById(R.id.textViewPlanTitle)).setVisibility(View.INVISIBLE);

            int time = 9;
            for (int i = 1; i < (9 - cur.getCount()); i++) {
                Map<String, String> datum = new HashMap<String, String>(2);

                datum.put("shift", "Shift -" + i);
                datum.put("shift_time", "Shift Timing: " + (time + 1) + ":00 HS");
                data.add(datum);
                time++;
            }

            SimpleAdapter adapter = new SimpleAdapter(this, data,
                    R.layout.simplerow,
                    new String[]{"shift", "shift_time"},
                    new int[]{R.id.rowTextView, R.id.rowTextView2});

            myListView.setAdapter(adapter);
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //END Date picker
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}
