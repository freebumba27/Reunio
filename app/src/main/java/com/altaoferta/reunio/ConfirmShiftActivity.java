package com.altaoferta.reunio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.altaoferta.utils.ReusableClass;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConfirmShiftActivity extends AppCompatActivity {

    String shift;
    String shift_time;
    String shift_date;
    TextView TextViewNameValue;
    TextView TextViewMobileValue;
    TextView TextViewShiftNameValue;
    TextView TextViewShiftDateValue;
    TextView TextViewShiftTimeValue;

    private ProgressDialog dialog2;

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
        dialog2 = new ProgressDialog(this);
        dialog2.setMessage("Please wait ...");
        dialog2.show();

        new ConfirmShiftTask().execute(TextViewShiftNameValue.getText().toString(), TextViewShiftDateValue.getText().toString()
                , TextViewShiftTimeValue.getText().toString(), ReusableClass.getFromPreference("user_id", ConfirmShiftActivity.this));

//            String sql = "insert into booked_shift_tbl (shift_name, shift_date, shift_time) values ('" + TextViewShiftNameValue.getText().toString()+"','" +
//                    TextViewShiftDateValue.getText().toString()+"','" + TextViewShiftTimeValue.getText().toString()+"');";
//            Log.i("TAG", sql);
//            db.execSQL(sql);

    }

    private class ConfirmShiftTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReusableClass.baseUrl + "reunio/book_shift.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("shift_name", values[0]));
                nameValuePairs.add(new BasicNameValuePair("shift_date", values[1]));
                nameValuePairs.add(new BasicNameValuePair("shift_time", values[2]));
                nameValuePairs.add(new BasicNameValuePair("user_id", values[3]));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);

                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    responseBody = EntityUtils.toString(response.getEntity());
                }
            } catch (Exception t) {
                Log.e("TAG", "Error: " + t);
            }
            return responseBody;
        }

        protected void onPostExecute(String result) {
            dialog2.dismiss();
            Log.d("TAG", "value: " + result);
            if (result.equalsIgnoreCase("YES")) {

//            Calendar cal = Calendar.getInstance();
//            cal.setTimeInMillis(System.currentTimeMillis());
//            cal.clear();
//            cal.set(2015,5,27,13,00);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.clear();
                cal.set(Integer.parseInt(TextViewShiftDateValue.getText().toString().substring(6, 10)),
                        (Integer.parseInt(TextViewShiftDateValue.getText().toString().substring(3, 5)) - 1),
                        Integer.parseInt(TextViewShiftDateValue.getText().toString().substring(0, 2)),
                        Integer.parseInt(TextViewShiftTimeValue.getText().toString().substring(0, 2)),
                        Integer.parseInt(TextViewShiftTimeValue.getText().toString().substring(3, 5)));

                Intent myIntent1 = new Intent(ConfirmShiftActivity.this, AlarmBroadCustReciver.class);
                myIntent1.putExtra("time", TextViewShiftTimeValue.getText().toString());
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(ConfirmShiftActivity.this, 1253, myIntent1,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent1);

                Toast.makeText(ConfirmShiftActivity.this, "Thanks for confirming you shift!!", Toast.LENGTH_LONG).show();

                Intent i = new Intent(ConfirmShiftActivity.this, DashBoardActivity.class);
                finish();
                startActivity(i);
            } else if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(ConfirmShiftActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("EXISTS")) {
                Toast.makeText(ConfirmShiftActivity.this, R.string.shift_already_booked_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ConfirmShiftActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
