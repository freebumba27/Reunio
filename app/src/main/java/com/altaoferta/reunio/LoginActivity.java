package com.altaoferta.reunio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000;
    EditText EditTextUsername;
    EditText EditTextPassword;
    private long mBackPressed;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditTextUsername = (EditText) findViewById(R.id.EditTextUserName);
        EditTextPassword = (EditText) findViewById(R.id.EditTextPass);

        EditTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    ((Button)findViewById(R.id.buttonLogin)).performClick();
                }
                return false;
            }
        });
    }

    public void login(View view) {
        if (EditTextUsername.getText().toString().equalsIgnoreCase("") || EditTextPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "All fields are mandatory !!", Toast.LENGTH_LONG).show();
        } else {

            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Please wait ...");
            dialog.show();

            new LoginTask().execute(EditTextUsername.getText().toString(), EditTextPassword.getText().toString());

//            if (ReusableClass.getFromPreference("username", LoginActivity.this).equalsIgnoreCase(EditTextUsername.getText().toString()) &&
//                    ReusableClass.getFromPreference("password", LoginActivity.this).equalsIgnoreCase(EditTextPassword.getText().toString())) {
//                Intent i = new Intent(this, DashBoardActivity.class);
//                finish();
//                startActivity(i);
//            } else {
//                Toast.makeText(this, "Please check your credentials !!", Toast.LENGTH_LONG).show();
//            }
        }
    }

    public void registering(View view) {
        if (ReusableClass.getFromPreference("username", LoginActivity.this).length() == 0) {
            Intent i = new Intent(this, RegistrationActivity.class);
            finish();
            startActivity(i);
        } else {
            Toast.makeText(this, "For a single device single user allowed !!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), R.string.double_back_pressed_error, Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    private class LoginTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReusableClass.baseUrl + "reunio/login_user.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", values[0]));
                nameValuePairs.add(new BasicNameValuePair("password", values[1]));

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
            if (result.contains("user_id")) {
                Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
                finish();
                startActivity(i);
            } else if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(LoginActivity.this, "Please check your credentials !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }
}
