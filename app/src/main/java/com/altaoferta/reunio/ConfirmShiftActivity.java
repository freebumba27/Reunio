package com.altaoferta.reunio;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.altaoferta.utils.ReusableClass;

public class ConfirmShiftActivity extends AppCompatActivity {

    String shift;
    String shift_time;
    String shift_date;
    TextView TextViewNameValue;
    TextView TextViewMobileValue;
    TextView TextViewShiftNameValue;
    TextView TextViewShiftDateValue;
    TextView TextViewShiftTimeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_shift);

        shift = getIntent().getStringExtra("shift");
        shift_time = getIntent().getStringExtra("shift_time");
        shift_date = getIntent().getStringExtra("shift_date");

        TextViewNameValue      = (TextView) findViewById(R.id.TextViewNameValue);
        TextViewMobileValue    = (TextView) findViewById(R.id.TextViewMobileValue);
        TextViewShiftNameValue = (TextView) findViewById(R.id.TextViewShiftNameValue);
        TextViewShiftDateValue = (TextView) findViewById(R.id.TextViewShiftDateValue);
        TextViewShiftTimeValue = (TextView) findViewById(R.id.TextViewShiftTimeValue);

        TextViewNameValue.setText(ReusableClass.getFromPreference("name", this));
        TextViewMobileValue.setText(ReusableClass.getFromPreference("mobile_no", this));
        TextViewShiftDateValue.setText(shift_date);
        TextViewShiftNameValue.setText(shift);
        TextViewShiftTimeValue.setText(shift_time.substring(14, shift_time.length()));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, DashBoardActivity.class);
        finish();
        startActivity(i);
    }

    public void confirmingShift(View view) {
        SQLiteDatabase db = ReusableClass.createAndOpenDb(ConfirmShiftActivity.this);
        try {
            String sql = "insert into booked_shift_tbl (shift_name, shift_date, shift_time) values ('" + TextViewShiftNameValue.getText().toString()+"','" +
                    TextViewShiftDateValue.getText().toString()+"','" + TextViewShiftTimeValue.getText().toString()+"');";
            Log.i("TAG", sql);
            db.execSQL(sql);

            Toast.makeText(this, "Thanks for confirming you shift!!", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong while saving the data!!", Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }

        Intent i = new Intent(this, DashBoardActivity.class);
        finish();
        startActivity(i);
    }
}
