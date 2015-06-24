package com.altaoferta.reunio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.altaoferta.utils.ReusableClass;

public class RegistrationActivity extends AppCompatActivity {

    EditText EditTextFirstName;
    EditText EditTextLastName;
    EditText EditTextMobile;
    EditText EditTextUsername;
    EditText EditTextPassword;
    EditText EditTextConfirmPassword;

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
                ReusableClass.saveInPreference("username", EditTextUsername.getText().toString(), RegistrationActivity.this);
                ReusableClass.saveInPreference("password", EditTextPassword.getText().toString(), RegistrationActivity.this);
                ReusableClass.saveInPreference("name", EditTextFirstName.getText().toString() + " "
                        + EditTextLastName.getText().toString(), RegistrationActivity.this);
                ReusableClass.saveInPreference("mobile_no", EditTextMobile.getText().toString(), RegistrationActivity.this);

                Toast.makeText(this, "Thanks for registering !!", Toast.LENGTH_LONG).show();
                onBackPressed();
            } else {
                Toast.makeText(this, "Password and confirm password mismatched !!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "All fields are mandatory !!", Toast.LENGTH_LONG).show();
        }
    }
}
