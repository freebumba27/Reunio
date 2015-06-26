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
import android.widget.TextView;
import android.widget.Toast;

import com.altaoferta.utils.CustomAdapter;
import com.altaoferta.utils.CustomObject;
import com.altaoferta.utils.ReusableClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DashBoardActivity extends AppCompatActivity {

    EditText editTextDatePicker;
    ListView myListView;
    ArrayList<CustomObject> objects = new ArrayList<CustomObject>();
    private String[] shiftArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        editTextDatePicker = (EditText) findViewById(R.id.editTextDatePicker);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy ");
        editTextDatePicker.setText(sdf.format(c.getTime()));

        myListView = (ListView) findViewById(R.id.myListView);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Log.d("TAG", "value: " + objects.get(position).getprop1());
                Log.d("TAG", "value: " + objects.get(position).getprop2());

                if (Arrays.asList(shiftArray).contains(objects.get(position).getprop1())) {
                    Toast.makeText(DashBoardActivity.this, "Sorry this shift is not available !!", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(DashBoardActivity.this, ConfirmShiftActivity.class);
                    i.putExtra("shift", objects.get(position).getprop1());
                    i.putExtra("shift_time", objects.get(position).getprop2());
                    i.putExtra("shift_date", editTextDatePicker.getText().toString());
                    finish();
                    startActivity(i);
                }
            }
        });
        searchAllShifts();
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

        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    public void searchingShifts(View view) {
        searchAllShifts();
    }

    private void searchAllShifts() {
        if (editTextDatePicker.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please select a date first !!", Toast.LENGTH_LONG).show();
        } else {
            SQLiteDatabase db = ReusableClass.createAndOpenDb(DashBoardActivity.this);
            String sql = "SELECT * FROM booked_shift_tbl WHERE shift_date = '" + editTextDatePicker.getText().toString() + "'";
            Log.i("TAG", sql);
            Cursor cur = db.rawQuery(sql, null);
            shiftArray = new String[cur.getCount()];
            int j = 0;
            if (cur.moveToNext()) {
                do {
                    shiftArray[j] = cur.getString(1);
                    j++;
                } while (cur.moveToNext());
            }

            if (cur.getCount() < 9)
                ((TextView) findViewById(R.id.textViewPlanTitle)).setVisibility(View.GONE);

            int time = 9;
            objects.clear();
            for (int i = 1; i < 9 ; i++) {
                objects.add(new CustomObject("Shift -" + i, "Shift Timing: " + (time + 1) + ":00 HS"));
                time++;
            }

            CustomAdapter customAdapter = new CustomAdapter(this, objects, shiftArray);
            myListView.setAdapter(customAdapter);

            cur.close();
            db.close();
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //END Date picker
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}
