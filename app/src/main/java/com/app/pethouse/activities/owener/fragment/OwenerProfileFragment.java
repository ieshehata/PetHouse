package com.app.pethouse.activities.owener.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import static android.app.Activity.RESULT_OK;

public class OwenerProfileFragment extends Fragment implements Validator.ValidationListener{

    @NotNull
    @NotEmpty
    @Length(min = 3)
    TextInputEditText petName, name;

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

    @NotNull
    @NotEmpty
    @Length(min = 3)
    TextInputEditText petKind;

    @NotNull
    @NotEmpty
    @Length(min = 3)
    TextInputEditText petGander;

    private UserModel owner = new UserModel();
    Button register;
    LoadingHelper loadingHelper;
    private Validator validator;
    private static final int PICK_IMAGE = 55;
    private static final int PICK_PET_IMAGE = 99;

    ImageView avatar,imagePet;
    Uri imageUri, petUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owener_profile, container, false);
        avatar = view.findViewById(R.id.avatar);
        imagePet = view.findViewById(R.id.pet_image);
        petName = view.findViewById(R.id.pet_name);
        petKind = view.findViewById(R.id.pet_kind);
        petGander = view.findViewById(R.id.pet_gander);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        phone = view.findViewById(R.id.phone);
        register = view.findViewById(R.id.register);
        loadingHelper = new LoadingHelper(getActivity());
        validator = new Validator(this);
        validator.setValidationListener(this);

        avatar.setOnClickListener(v -> {
            if (checkReadPermission()) {
                pickImage();
            }
        });

        imagePet.setOnClickListener(v -> {
            if (checkReadPermission()) {
                pickPetImage();
            }
        });

        register.setOnClickListener(v -> {
            validator.validate();
        });
        setData();
        return view;
    }

    @SuppressLint("DefaultLocale")
    private void setData() {
        register.setText("Update");
        assert SharedData.currentUser != null;
        owner = SharedData.currentUser;
        name.setText(SharedData.currentUser.getName());
        phone.setText(SharedData.currentUser.getPhone());
        password.setText(SharedData.currentUser.getPass());
        email.setText(SharedData.currentUser.getEmail());
        petName.setText(SharedData.currentUser.getPetName());
        petGander.setText(SharedData.currentUser.getPetGander());
        petKind.setText(SharedData.currentUser.getPetKind());

        if (!TextUtils.isEmpty(SharedData.currentUser.getProfileImage())) {
            Picasso.get()
                    .load(SharedData.currentUser.getProfileImage())
                    .into(avatar);
        }

        if (!TextUtils.isEmpty(SharedData.currentUser.getImagePet())) {
            Picasso.get()
                    .load(SharedData.currentUser.getImagePet())
                    .into(imagePet);
        }

    }

    private boolean checkReadPermission() {
        int permissionWriteExternal = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
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

    private void pickPetImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_PET_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            avatar.setImageURI(imageUri);}
        else if(resultCode == RESULT_OK && requestCode == PICK_PET_IMAGE){
            petUri = data.getData();
            imagePet.setImageURI(petUri);
        }
    }

    @Override
    public void onValidationSucceeded() {
        owner.setName(name.getText().toString());
        owner.setPhone(phone.getText().toString());
        owner.setPass(password.getText().toString());
        owner.setEmail(email.getText().toString());
        owner.setPetName(petName.getText().toString());
        owner.setPetGander(petGander.getText().toString());
        owner.setPetKind(petKind.getText().toString());
        SharedData.currentUser = owner;

        if(imageUri != null) {
            loadingHelper.showLoading("");
            new UploadController().uploadImage(imageUri, new StringCallback() {
                @Override
                public void onSuccess(String text) {
                    SharedData.currentUser.setProfileImage(text);
                    if(petUri != null) {
                        new UploadController().uploadImage(petUri, new StringCallback() {
                            @Override
                            public void onSuccess(String text) {
                                SharedData.currentUser.setImagePet(text);
                                new UserController().save(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<UserModel> users) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        loadingHelper.dismissLoading();
                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFail(String error) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        new UserController().save(SharedData.currentUser, new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<UserModel> users) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(String error) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFail(String error) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if(petUri != null) {
                loadingHelper.showLoading("");
                new UploadController().uploadImage(petUri, new StringCallback() {
                    @Override
                    public void onSuccess(String text) {
                        SharedData.currentUser.setImagePet(text);
                        new UserController().save(SharedData.currentUser, new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<UserModel> users) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(String error) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFail(String error) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loadingHelper.showLoading("");
                new UserController().save(SharedData.currentUser, new UserCallback() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> users) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String error) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }

    }
}