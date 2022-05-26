package com.app.pethouse.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.pethouse.R;
import com.app.pethouse.callback.StringCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.controller.UploadController;
import com.app.pethouse.controller.UserController;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.annotations.NotNull;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener{
    @NotNull
    @NotEmpty
    @Length(min = 3)
    TextInputEditText  name;

    @NotNull
    @NotEmpty
    @Length(min = 8)
    TextInputEditText phone;

    @NotNull
    @NotEmpty
    @Length(min = 6)
    TextInputEditText password;

    @NotNull
    @NotEmpty
    @Email
    TextInputEditText email;

    private boolean isEditing = false;
    private UserModel user = new UserModel();
    Button login,register;
    LoadingHelper loadingHelper;
    private Validator validator;
    private static final int PICK_IMAGE = 55;
    private SharedPreferences sharedPref;

    ImageView avatar;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        isEditing = getIntent().getExtras().getBoolean("isEditing", false);
        sharedPref = this.getSharedPreferences(SharedData.PREF_KEY, Context.MODE_PRIVATE);

        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        loadingHelper = new LoadingHelper(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        register.setOnClickListener(v -> {
            validator.validate();
        });

        login.setOnClickListener(v -> onBackPressed());

        avatar.setOnClickListener(v -> {
            if (checkReadPermission()) {
                pickImage();
            }
        });

        setData();
    }

    @SuppressLint("DefaultLocale")
    private void setData() {
        if (isEditing) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("User Profile");
            register.setText("Update");
            login.setVisibility(View.GONE);
            assert SharedData.currentUser!= null;
            user = SharedData.currentUser;
            name.setText(SharedData.currentUser.getName());
            phone.setText(SharedData.currentUser.getPhone());
            password.setText(SharedData.currentUser.getPass());
            email.setText(SharedData.currentUser.getEmail());
            if (!TextUtils.isEmpty(SharedData.currentUser.getProfileImage())) {
                Picasso.get()
                        .load(SharedData.currentUser.getProfileImage())
                        .into(avatar);
            } else {
                user = new UserModel();
                user.setState(1);
            }
        }

    }

    private boolean checkReadPermission() {
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            avatar.setImageURI(imageUri);}
    }

    @Override
    public void onValidationSucceeded() {
        user.setName(name.getText().toString());
        user.setPhone(phone.getText().toString());
        user.setPass(password.getText().toString());
        user.setEmail(email.getText().toString());
        user.setState(1);
        if(imageUri != null) {
            loadingHelper.showLoading("");
            new UploadController().uploadImage(imageUri, new StringCallback() {
                @Override
                public void onSuccess(String text) {
                    loadingHelper.dismissLoading();
                    user.setProfileImage(text);
                    SharedData.currentUser = user;
                    saveUser();
                }

                @Override
                public void onFail(String error) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }else if(isEditing) {
            SharedData.currentUser = user;
            saveUser();
        } else {
            Toast.makeText(RegisterActivity.this, "Pick your profile picture!", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveUser() {
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
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

    }
}