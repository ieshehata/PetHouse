package com.app.pethouse.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pethouse.R;
import com.app.pethouse.model.GovernorateModel;
import com.app.pethouse.utils.SharedData;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class CityDialog extends AppCompatDialogFragment {
    private Spinner spinner;
    private EditText name;
    private DataDialogListener listener;
    private String oldName = "";
    private GovernorateModel governorate = null;
    private String oldGovernorate = null;

    public CityDialog() {
    }

    public CityDialog(String old, String governorate) {
        this.oldName = old;
        this.oldGovernorate = governorate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_city, null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onError("Canceled!");
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!TextUtils.isEmpty(name.getText().toString()) && governorate != null) {
                            listener.getData(name.getText().toString(), governorate);
                        }else {
                            listener.onError("You need to write the name and choose the governorate!");
                        }
                    }
                });

        name = view.findViewById(R.id.name);
        spinner = view.findViewById(R.id.governorates_spinner);

        ArrayList<String> governoatesStringList = new ArrayList<>();
        for (GovernorateModel governorateModel : SharedData.allGovernorates) {
            governoatesStringList.add(governorateModel.getName());
        }

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, governoatesStringList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                governorate = SharedData.allGovernorates.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if(!TextUtils.isEmpty(oldName)) {
            name.setText(oldName);
        }

        if(oldGovernorate != null) {
            for(int i = 0; i < SharedData.allGovernorates.size(); i++) {
                if(SharedData.allGovernorates.get(i).getKey().equals(oldGovernorate)) {
                    spinner.setSelection(i);
                }
            }
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        try {
            if(oldName.equals("")) {
                listener = (DataDialogListener) context ;
            }else {
                listener = (DataDialogListener) getParentFragment();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement RequestProductDialogListener");

        }
    }

    public interface DataDialogListener {
        void getData(String name, GovernorateModel governorate);
        void onError(String error);
    }
}
