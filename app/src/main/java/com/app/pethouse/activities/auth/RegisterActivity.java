package com.app.pethouse.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
    ImageView avatar;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        isEditing = getIntent().getExtras().getBoolean("isEditing", false);
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
            assert SharedData.owner!= null;
            user = SharedData.owner;
            name.setText(SharedData.owner.getName());
            phone.setText(SharedData.owner.getPhone());
            password.setText(SharedData.owner.getPass());
            email.setText(SharedData.owner.getEmail());
            if (!TextUtils.isEmpty(SharedData.owner.getProfileImage())) {
                Picasso.get()
                        .load(SharedData.owner.getProfileImage())
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
                    SharedData.owner = user;
                    Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                    intent.putExtra("from", 1);
                    intent.putExtra("phone", user.getPhone());
                    startActivity(intent);
                }

                @Override
                public void onFail(String error) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }else if(isEditing) {
            SharedData.owner = user;
            Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
            intent.putExtra("from", 3);
            intent.putExtra("phone", user.getPhone());
            startActivity(intent);
        } else {
            Toast.makeText(RegisterActivity.this, "Pick your profile picture!", Toast.LENGTH_SHORT).show();
        }

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