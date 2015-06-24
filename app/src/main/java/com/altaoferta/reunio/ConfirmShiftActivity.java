package com.altaoferta.reunio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.altaoferta.utils.ReusableClass;

public class ConfirmShiftActivity extends AppCompatActivity {

    String shift;
    String shift_time;
    TextView TextViewNameValue;
    TextView TextViewMobileValue;
    TextView TextViewShiftNameValue;
    TextView TextViewShiftTimeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_shift);

        shift = getIntent().getStringExtra("shift");
        shift_time = getIntent().getStringExtra("shift_time");

        TextViewNameValue = (TextView) findViewById(R.id.TextViewNameValue);
        TextViewMobileValue = (TextView) findViewById(R.id.TextViewMobileValue);
        TextViewShiftNameValue = (TextView) findViewById(R.id.TextViewShiftNameValue);
        TextViewShiftTimeValue = (TextView) findViewById(R.id.TextViewShiftTimeValue);

        TextViewNameValue.setText(ReusableClass.getFromPreference("name", this));
        TextViewMobileValue.setText(ReusableClass.getFromPreference("mobile_no", this));
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
    }
}
