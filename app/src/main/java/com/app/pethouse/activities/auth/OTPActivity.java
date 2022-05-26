package com.app.pethouse.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.pethouse.R;
import com.app.pethouse.activities.supplier.SupplierMainActivity;
import com.app.pethouse.activities.owener.OwenerMainActivity;
import com.app.pethouse.callback.SupplierReqCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.controller.SupplierReqController;
import com.app.pethouse.controller.UserController;
import com.app.pethouse.model.SuppliersReqModel;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.app.pethouse.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    private EditText otp;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "PhoneAuthActivity";
    private int from = 0;
    private String phone = "";
    private SharedPreferences sharedPref;
    private LoadingHelper loadingHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        sharedPref = this.getSharedPreferences(SharedData.PREF_KEY, Context.MODE_PRIVATE);
        loadingHelper = new LoadingHelper(this);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        from = intent.getIntExtra("from", 0);
        mAuth = FirebaseAuth.getInstance();
        otp = findViewById(R.id.otp);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Invalid phone number.",
                            Snackbar.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
            }
        };
        //Send sms
        sendVerificationCode();
        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp.getText().toString().trim();
                if(!TextUtils.isEmpty(code) && code.length() == 6) {
                    checkCode();
                }else {
                    Toast.makeText(OTPActivity.this, "Enter the code!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkCode() {
        String code = otp.getText().toString();
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Utils.hideKeyboard(OTPActivity.this);
        loadingHelper.showLoading("");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if(from == 1) { //from register
                            if(SharedData.userType == 2) { //supplier
                                new UserController().newSupplier(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> suppliers) {
                                        if(suppliers.size() > 0) {
                                            new SupplierReqController().newRequest(suppliers.get(0), new SupplierReqCallback() {
                                                @Override
                                                public void onSuccess(ArrayList<SuppliersReqModel> requests) {
                                                    loadingHelper.dismissLoading();
                                                    Toast.makeText(OTPActivity.this, "Registered, wait admin review!", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(OTPActivity.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                                @Override
                                                public void onFail(String error) {
                                                    loadingHelper.dismissLoading();
                                                    Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });

                            }else if(SharedData.userType == 3) { //Owener
                                new UserController().newOwner(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> oweners) {
                                        if(oweners.size() > 0) {
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putBoolean(SharedData.IS_USER_SAVED, true);
                                            editor.putString(SharedData.PHONE, oweners.get(0).getPhone());
                                            editor.putString(SharedData.PASS, oweners.get(0).getPass());
                                            editor.putInt(SharedData.USER_TYPE, SharedData.userType);
                                            editor.apply();
                                            loadingHelper.dismissLoading();
                                            Intent intent = new Intent(OTPActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }else if(from == 2) { //from forget
                            if(SharedData.userType == 2) {
                                new UserController().getUserByPhone(phone, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> suppliers) {
                                        if(suppliers.size() > 0) {
                                            SharedData.currentUser = suppliers.get(0);
                                            loadingHelper.dismissLoading();
                                            Intent intent = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(OTPActivity.this, "user not registered", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else if(SharedData.userType == 3) {
                                new UserController().getUserByPhone(phone, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> oweners) {
                                        if(oweners.size() > 0) {
                                            SharedData.currentUser = oweners.get(0);
                                            loadingHelper.dismissLoading();
                                            Intent intent = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(OTPActivity.this, "user not registered", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }else if(from == 3) { //updating
                            if(SharedData.userType == 2) {
                                new UserController().save(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> suppliers) {
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean(SharedData.IS_USER_SAVED, true);
                                        editor.putString(SharedData.PHONE, suppliers.get(0).getPhone());
                                        editor.putString(SharedData.PASS, suppliers.get(0).getPass());
                                        editor.putInt(SharedData.USER_TYPE, SharedData.userType);
                                        editor.apply();

                                        loadingHelper.dismissLoading();
                                        Intent intent = new Intent(OTPActivity.this, SupplierMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else if(SharedData.userType == 3) {
                                new UserController().save(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> oweners) {
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean(SharedData.IS_USER_SAVED, true);
                                        editor.putString(SharedData.PHONE, oweners.get(0).getPhone());
                                        editor.putString(SharedData.PASS, oweners.get(0).getPass());
                                        editor.putInt(SharedData.USER_TYPE, SharedData.userType);

                                        editor.apply();
                                        loadingHelper.dismissLoading();
                                        Intent intent = new Intent(OTPActivity.this, OwenerMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    } else {
                        loadingHelper.dismissLoading();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(OTPActivity.this,
                                    "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(){
        if(phone == null) {
            phone = "";
        }
        if(phone.isEmpty()){
            Toast.makeText(OTPActivity.this, "Error while sending code!", Toast.LENGTH_LONG).show();
            return;
        }
        if(phone.length() < 11 ){
            Toast.makeText(OTPActivity.this, "phone is not valid!", Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}