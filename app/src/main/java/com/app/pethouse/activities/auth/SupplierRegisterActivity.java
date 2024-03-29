package com.app.pethouse.activities.auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.pethouse.R;
import com.app.pethouse.activities.supplier.PlacePickerActivity;
import com.app.pethouse.callback.StringCallback;
import com.app.pethouse.callback.SupplierReqCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.controller.SupplierReqController;
import com.app.pethouse.controller.UploadController;
import com.app.pethouse.controller.UserController;
import com.app.pethouse.model.CityModel;
import com.app.pethouse.model.GovernorateModel;
import com.app.pethouse.model.SuppliersReqModel;
import com.app.pethouse.model.TypeModel;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.annotations.NotNull;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SupplierRegisterActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotNull
    @NotEmpty
    @Length(min = 3)
    TextInputEditText name, description;

    @NotNull
    @NotEmpty
    @Length(min = 8)
    TextInputEditText phone;

    @NotNull
    @NotEmpty
    @Email
    TextInputEditText email;

    @NotNull
    @NotEmpty
    @Length(min = 6)
    TextInputEditText password;

    @NotNull
    @NotEmpty
    @DecimalMin(0.1)
    TextInputEditText price;
    private static final int PICK_IMAGE = 55;
    private boolean isEditing = false;
    private UserModel supplier = new UserModel();
    private LinearLayout governorateLayout, cityLayout;
    private AutoCompleteTextView governorate, city ,type;
    private ArrayList<String> governoratesNames = new ArrayList<>();
    private ArrayList<String> citiesNames = new ArrayList<>();
    private ArrayList<CityModel> cities = new ArrayList<>();
    private TypeModel chosenType;
    private GovernorateModel chosenGovernorate;
    private CityModel chosenCity;
    private SharedPreferences sharedPref;

    Uri imageUri;
    int gender = 1;  // 0->Female, 1->Male
    SimpleDateFormat sdf = new SimpleDateFormat(SharedData.formatDate, Locale.US);
    ImageView avatar;
    Button register, location;
    MaterialButton male, female;
    LoadingHelper loadingHelper;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_register);
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        sharedPref = this.getSharedPreferences(SharedData.PREF_KEY, Context.MODE_PRIVATE);

        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        governorateLayout = findViewById(R.id.governorate_layout);
        cityLayout = findViewById(R.id.city_layout);
        type = findViewById(R.id.type);
        governorate = findViewById(R.id.governorate);
        city = findViewById(R.id.city);
        male = findViewById(R.id.male_button);
        female = findViewById(R.id.female_button);
        location = findViewById(R.id.place_location);
        register = findViewById(R.id.register);

        loadingHelper = new LoadingHelper(this);
        validator = new Validator(this);
        validator.setValidationListener(this);

        register.setText(SharedData.userType == 2 ? "Apply" : "Register");

        avatar.setOnClickListener(v -> {
            if(checkReadPermission()){
                pickImage();
            }
        });

        male.setOnClickListener(v -> {
            gender = 1;
            male.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryMidDark)));
            male.setTextColor(ContextCompat.getColor(this, R.color.whiteCardColor));
            male.setStrokeWidth(0);

            female.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            female.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
            female.setStrokeWidth(2);
        });

        female.setOnClickListener(v -> {
            gender = 0;
            female.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryMidDark)));
            female.setTextColor(ContextCompat.getColor(this, R.color.whiteCardColor));
            female.setStrokeWidth(0);

            male.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            male.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
            male.setStrokeWidth(2);
        });

        location.setOnClickListener(v -> {
            Intent intent = new Intent(SupplierRegisterActivity.this, PlacePickerActivity.class);
            SharedData.latitude = SharedData.currentUser.getLatitude() == null ? 29.282478 : SharedData.currentUser.getLatitude();
            SharedData.longitude = SharedData.currentUser.getLongitude() == null ? 47.912792 : SharedData.currentUser.getLongitude();
            startActivityForResult(intent,2);
        });

        register.setOnClickListener(v -> {
            validator.validate();
        });

        setData();
    }


    @SuppressLint("DefaultLocale")
    private void setData() {
        setAdapters();
        if (isEditing) {
            supplier = SharedData.currentUser;

            Objects.requireNonNull(getSupportActionBar()).setTitle("Supplier Profile");
            register.setText("Update");
            name.setText(SharedData.currentUser.getName());
            phone.setText(SharedData.currentUser.getPhone());
            email.setText(SharedData.currentUser.getEmail());
            password.setText(SharedData.currentUser.getPass());
            description.setText(SharedData.currentUser.getDescription());
            price.setText(String.format("%.3f",SharedData.currentUser.getPrice()));

            if(!TextUtils.isEmpty(SharedData.currentUser.getProfileImage())) {
                Picasso.get()
                        .load(SharedData.currentUser.getProfileImage())
                        .into(avatar);
            }

            if(SharedData.currentUser.getType().getKey() != null) {
                for(int i = 0; i < SharedData.allTypes.size(); i++) {
                    if(SharedData.allTypes.get(i).getName().equals(SharedData.currentUser.getType().getName())) {
                        type.getOnItemClickListener().onItemClick(null, null, i, i);
                        type.setText(SharedData.allTypes.get(i).getName());
                    }
                }
            }

            if(SharedData.currentUser.getGovernorate().getKey() != null) {
                for(int i = 0; i < SharedData.allGovernorates.size(); i++) {
                    if(SharedData.allGovernorates.get(i).getName().equals(SharedData.currentUser.getGovernorate().getName())) {
                        governorate.getOnItemClickListener().onItemClick(null, null, i, i);
                        governorate.setText(SharedData.allGovernorates.get(i).getName());
                    }
                }
            }

            if(SharedData.currentUser.getCity().getKey() != null) {
                city.setVisibility(VISIBLE);
                for(int i = 0; i < SharedData.allCities.size(); i++) {
                    if(SharedData.allCities.get(i).getName().equals(SharedData.currentUser.getCity().getName())) {
                        city.getOnItemClickListener().onItemClick(null, null, i, i);
                        city.setText(SharedData.allCities.get(i).getName());
                    }
                }
            }

            genderButtonsRefresh();
        } else {
            SharedData.currentUser = new UserModel();
        }
    }

    private void setAdapters() {
        for(GovernorateModel governorateModel: SharedData.allGovernorates) {
            governoratesNames.add(governorateModel.getName());
        }

        ArrayAdapter categoriesAdapter = new ArrayAdapter<>(this, R.layout.list_item, governoratesNames);
        governorate.setAdapter(categoriesAdapter);

        governorate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenGovernorate = SharedData.allGovernorates.get(position);
                citiesNames = new ArrayList<>();
                cities = new ArrayList<>();
                city.setText("");
                for(CityModel cityModel: SharedData.allCities) {
                    if(cityModel.getGovernorateKey().equals(chosenGovernorate.getKey())) {
                        cities.add(cityModel);
                        citiesNames.add(cityModel.getName());
                    }
                }
                if(citiesNames.size() > 0) {
                    cityLayout.setVisibility(VISIBLE);
                    ArrayAdapter adapter = new ArrayAdapter<>(SupplierRegisterActivity.this, R.layout.list_item, citiesNames);
                    city.setAdapter(adapter);
                }else {
                    cityLayout.setVisibility(GONE);
                }
            }
        });

        city.setOnItemClickListener((parent, view, position, id) -> {
            if(cities.size() > position)
                chosenCity = cities.get(position);
        });

        /*
        for(TypeModel typeModel: SharedData.allTypes) {
            typesNames.add(typeModel.getName());
        }

        ArrayAdapter kindsAdapter = new ArrayAdapter<>(this, R.layout.list_item, typesNames);
        type.setAdapter(kindsAdapter);

        type.setOnItemClickListener((parent, view, position, id) -> chosenType = SharedData.allTypes.get(position));
         */
    }

    private void genderButtonsRefresh() {
        switch (gender) {
            case 0: //female
                female.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryMidDark)));
                female.setTextColor(ContextCompat.getColor(this, R.color.whiteCardColor));
                female.setStrokeWidth(0);

                male.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                male.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
                male.setStrokeWidth(2);
                break;
            case 1: //male
                male.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryMidDark)));
                male.setTextColor(ContextCompat.getColor(this, R.color.whiteCardColor));
                male.setStrokeWidth(0);

                female.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                female.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
                female.setStrokeWidth(2);
                break;
        }
    }

    private boolean checkReadPermission(){
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            return false;
        }else{
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        }
    }

    private void pickImage(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2){
            supplier.setLatitude(SharedData.currentLatLng.getLatitude());
            supplier.setLongitude(SharedData.currentLatLng.getLongitude());
        }else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            avatar.setImageURI(imageUri);
        }
    }

    @Override
    public void onValidationSucceeded() {
        if(supplier.getLatitude() == null || supplier.getLongitude() == null) {
            Toast.makeText(SupplierRegisterActivity.this, "place your location first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chosenGovernorate == null && chosenCity == null) {
            Toast.makeText(SupplierRegisterActivity.this, "Select Govenorate and City", Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        if (chosenType == null ) {
            Toast.makeText(SupplierRegisterActivity.this, "Select Type ", Toast.LENGTH_SHORT).show();
            return;
        }*/

        supplier.setName(name.getText().toString());
        supplier.setPhone(phone.getText().toString());
        supplier.setEmail(email.getText().toString());
        supplier.setPass(password.getText().toString());
        supplier.setDescription(description.getText().toString());
        supplier.setPrice(Double.parseDouble(price.getText().toString()));
        supplier.setGender(gender);
        supplier.setNationality("");
        supplier.setType(chosenType);
        supplier.setGovernorate(chosenGovernorate);
        supplier.setCity(chosenCity);
        supplier.setUserType(2);
        supplier.setActivated(1);
        supplier.setState(0);

        if(!isEditing)
            supplier.setSupplierType(SharedData.supplierType);

        SharedData.currentUser = supplier;

        if (imageUri != null) {
            loadingHelper.showLoading("");
            new UploadController().uploadImage(imageUri, new StringCallback() {
                @Override
                public void onSuccess(String text) {
                    loadingHelper.dismissLoading();
                    supplier.setProfileImage(text);
                    SharedData.currentUser = supplier;
                    saveUser();
                }

                @Override
                public void onFail(String error) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(SupplierRegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        } else if(isEditing) {
            loadingHelper.showLoading("");
            new UserController().save(supplier, new UserCallback() {
                @Override
                public void onSuccess(ArrayList<UserModel> users) {
                    loadingHelper.dismissLoading();
                    onBackPressed();
                }

                @Override
                public void onFail(String error) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(SupplierRegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SupplierRegisterActivity.this, "Pick your profile picture!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUser() {
        new UserController().newSupplier(SharedData.currentUser, new UserCallback() {
            @Override
            public void onSuccess(ArrayList<UserModel> suppliers) {
                if(suppliers.size() > 0) {
                    new SupplierReqController().newRequest(suppliers.get(0), new SupplierReqCallback() {
                        @Override
                        public void onSuccess(ArrayList<SuppliersReqModel> requests) {
                            loadingHelper.dismissLoading();
                            Toast.makeText(SupplierRegisterActivity.this, "Registered, wait admin review!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SupplierRegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        @Override
                        public void onFail(String error) {
                            loadingHelper.dismissLoading();
                            Toast.makeText(SupplierRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(SupplierRegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for(ValidationError error: errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if( view instanceof EditText){
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }}