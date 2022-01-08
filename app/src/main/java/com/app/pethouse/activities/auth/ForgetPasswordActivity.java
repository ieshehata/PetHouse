package com.app.pethouse.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.app.pethouse.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText phone;
    private String enteredPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        phone = findViewById(R.id.phone);

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    enteredPhone = phone.getText().toString();
                    Intent intent = new Intent(ForgetPasswordActivity.this, OTPActivity.class);
                    intent.putExtra("from", 2);
                    intent.putExtra("phone", enteredPhone);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validate() {
        boolean validData = true;
        if(TextUtils.isEmpty(phone.getText().toString())){
            validData = false;
            phone.setError("required field!");
        }
        if(phone.getText().toString().length() < 10){
            validData = false;
            phone.setError("Phone is not valid");
        }
        return validData;
    }
}