package com.example.drilldesign;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class dialogFragmentAbout extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.aboutTitle))
                .setMessage(getString(R.string.aboutMessage))
                .setPositiveButton(getString(R.string.aboutClose), (dialog, id) -> dialog.cancel());
        return builder.create();
    }

}
