package com.app.pethouse.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pethouse.R;

public class DataDialog extends AppCompatDialogFragment {
    private EditText name;
    private DataDialogListener listener;
    private String oldName = "";

    public DataDialog() {
    }

    public DataDialog(String old) {
        this.oldName = old;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_data_name, null);

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
                        if(!TextUtils.isEmpty(name.getText().toString())) {
                            listener.getData(name.getText().toString());
                        }else {
                            listener.onError("You need to write the name!");
                        }
                    }
                });
        name = view.findViewById(R.id.name);
        if(!TextUtils.isEmpty(oldName)) {
            name.setText(oldName);
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
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
        void getData(String name);
        void onError(String error);
    }
}
