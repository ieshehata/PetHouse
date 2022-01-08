package com.app.pethouse.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pethouse.R;

public class RateDialog extends AppCompatDialogFragment {
    private RatingBar ratingBar;
    private EditText commentText;
    private RateDialogListener listener;
    boolean fromFragment;

    public RateDialog(boolean fromFragment) {
        this.fromFragment = fromFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rate, null);

        builder.setView(view)
                .setNegativeButton("Cancel", (dialogInterface, i) -> listener.onError("Canceled!"))
                .setPositiveButton("Send", (dialogInterface, i) -> {
                    String comment = "";
                    if(!TextUtils.isEmpty(commentText.getText().toString())) {
                        comment = commentText.getText().toString();
                    }
                    listener.getData(ratingBar.getRating(), comment);
                });
        ratingBar = view.findViewById(R.id.rate);
        commentText = view.findViewById(R.id.comment);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(!fromFragment) {
                listener = (RateDialogListener) context ;
            }else {
                listener = (RateDialogListener) getParentFragment();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement RequestProductDialogListener");

        }
    }

    public interface RateDialogListener {
        void getData(float rate, String commentText);
        void onError(String error);
    }
}
