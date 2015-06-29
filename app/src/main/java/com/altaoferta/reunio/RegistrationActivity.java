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

public class RegistrationActivity extends AppCompatActivity {

    EditText EditTextFirstName;
    EditText EditTextLastName;
    EditText EditTextMobile;
    EditText EditTextUsername;
    EditText EditTextPassword;
    EditText EditTextConfirmPassword;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditTextFirstName = (EditText) findViewById(R.id.EditTextFirstName);
        EditTextLastName = (EditText) findViewById(R.id.EditTextLastName);
        EditTextMobile = (EditText) findViewById(R.id.EditTextMobile);
        EditTextUsername = (EditText) findViewById(R.id.EditTextUsername);
        EditTextPassword = (EditText) findViewById(R.id.EditTextPass);
        EditTextConfirmPassword = (EditText) findViewById(R.id.EditTextConfirmPassword);

        EditTextConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    ((Button)findViewById(R.id.buttonRegister)).performClick();
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    public void registering(View view) {

        if (EditTextFirstName.getText().toString().length() > 0 && EditTextLastName.getText().toString().length() > 0 &&
                EditTextPassword.getText().toString().length() > 0 && EditTextMobile.getText().toString().length() > 0 &&
                EditTextConfirmPassword.getText().toString().length() > 0 && EditTextUsername.getText().toString().length() > 0) {

            if (EditTextPassword.getText().toString().equalsIgnoreCase(EditTextConfirmPassword.getText().toString())) {

                dialog = new ProgressDialog(RegistrationActivity.this);
                dialog.setMessage("Please wait registering ...");
                dialog.show();

                new UserRegistrationTask().execute(EditTextUsername.getText().toString(), EditTextPassword.getText().toString(),
                        EditTextFirstName.getText().toString(), EditTextMobile.getText().toString());


//                ReusableClass.saveInPreference("username", EditTextUsername.getText().toString(), RegistrationActivity.this);
//                ReusableClass.saveInPreference("password", EditTextPassword.getText().toString(), RegistrationActivity.this);
//                ReusableClass.saveInPreference("name", EditTextFirstName.getText().toString() + " "
//                        + EditTextLastName.getText().toString(), RegistrationActivity.this);
//                ReusableClass.saveInPreference("mobile_no", EditTextMobile.getText().toString(), RegistrationActivity.this);
//
//                Toast.makeText(this, "Thanks for registering !!", Toast.LENGTH_LONG).show();
//                onBackPressed();
            } else {
                Toast.makeText(this, "Password and confirm password mismatched !!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "All fields are mandatory !!", Toast.LENGTH_LONG).show();
        }
    }

    private class UserRegistrationTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReusableClass.baseUrl + "reunio/register_user.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", values[0]));
                nameValuePairs.add(new BasicNameValuePair("password", values[1]));
                nameValuePairs.add(new BasicNameValuePair("name", values[2]));
                nameValuePairs.add(new BasicNameValuePair("mobile_no", values[3]));

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
            Log.d("TAG", "value: " + result);
            if (result.equalsIgnoreCase("YES")) {
                Toast.makeText(RegistrationActivity.this, "Thanks for registering !!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(RegistrationActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("EXISTS")) {
                Toast.makeText(RegistrationActivity.this, R.string.user_exists_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegistrationActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }
}
