package com.altaoferta.reunio;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.altaoferta.utils.CustomAdapter;
import com.altaoferta.utils.CustomObject;
import com.altaoferta.utils.ReusableClass;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DashBoardActivity extends AppCompatActivity {

    EditText editTextDatePicker;
    ListView myListView;
    ArrayList<CustomObject> objects = new ArrayList<CustomObject>();
    private String[] shiftArray = null;
    private ProgressDialog dialog;
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


                Calendar c = Calendar.getInstance();
                String date = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
                String month = String.format("%02d", (c.get(Calendar.MONTH) + 1));
                String year = c.get(Calendar.YEAR) + "";
                String Hr24 = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
                String Min = String.format("%02d", c.get(Calendar.MINUTE));

                long current = Long.parseLong("" + year + month + date + Hr24 + Min);
                Log.e("TAG", "VALUE: " + editTextDatePicker.getText().toString());
                long shiftTime = Long.parseLong(editTextDatePicker.getText().toString().substring(6, 10) +
                        editTextDatePicker.getText().toString().substring(3, 5) +
                        editTextDatePicker.getText().toString().substring(0, 2) +
                        objects.get(position).getprop2().substring(14, 16) +
                        objects.get(position).getprop2().substring(17, 19));

                Log.d("TAG", "current Time:" + current);
                Log.d("TAG", "shift Time:" + shiftTime);

                if (shiftTime > current) {
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
                } else {
                    Toast.makeText(DashBoardActivity.this, "Sorry shift is already over !!", Toast.LENGTH_LONG).show();
                }
            }
        });
        searchAllShifts();

        editTextDatePicker.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Log.i("TAG", "changed");
                searchAllShifts();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

                editTextDatePicker.setText(new StringBuilder()
                        .append(String.format("%02d", selectedday)).append("-")
                        .append(String.format("%02d", selectedmonth + 1)).append("-")
                        .append(selectedyear).append(" "));
            }
        }, mYear, mMonth, mDay);

        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    private void searchAllShifts() {
        if (editTextDatePicker.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please select a date first !!", Toast.LENGTH_LONG).show();
        } else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please Wait ...");
            dialog.show();

            new GetAllShiftTask().execute(ReusableClass.getFromPreference("user_id", this), editTextDatePicker.getText().toString());
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //END Date picker
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    private class GetAllShiftTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReusableClass.baseUrl + "reunio/get_all_shift.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user_id", values[0]));
                nameValuePairs.add(new BasicNameValuePair("shift_date", values[1]));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);

                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    responseBody = EntityUtils.toString(response.getEntity());
                    Log.d("TAG", "value: " + responseBody);
                }
            } catch (Exception t) {
                Log.e("TAG", "Error: " + t);
            }
            return responseBody;
        }

        protected void onPostExecute(String result) {
            Log.d("TAG", "value: " + result);
            if (!result.equalsIgnoreCase("NO")) {
                try {
                    JSONArray jsonarray = new JSONArray(result);
                    shiftArray = new String[jsonarray.length()];
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        shiftArray[i] = jsonobject.getString("shift_name");
                        Log.d("TAG", "Shift name: " + shiftArray[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DashBoardActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                shiftArray = new String[0];
                // Do nothing
            }

            int time = 8;
            objects.clear();
            for (int i = 1; i < 14; i++) {
                if (Arrays.asList(shiftArray).contains("Shift -" + i))
                    objects.add(new CustomObject("Shift -" + i, "Shift Timing: " +
                            ((((time + 1) + "").length() == 1) ? "0" + (time + 1) : (time + 1)) + ":00 HS",
                            ((((time + 1) + "").length() == 1) ? "0" + (time + 1) : (time + 1)) + ":00 - Not Available !"));
                else
                    objects.add(new CustomObject("Shift -" + i, "Shift Timing: " +
                            ((((time + 1) + "").length() == 1) ? "0" + (time + 1) : (time + 1)) + ":00 HS",
                            ((((time + 1) + "").length() == 1) ? "0" + (time + 1) : (time + 1)) + ":00 - Available !"));

                time++;
            }

            CustomAdapter customAdapter = new CustomAdapter(DashBoardActivity.this, objects, shiftArray);
            myListView.setAdapter(customAdapter);

            dialog.dismiss();
        }
    }
}
