package com.watanabemjk.todokeep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class  EditDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if(activity == null){
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialog_view = inflater.inflate(R.layout.edit_dialog, null);
        final EditText titleEdit = dialog_view.findViewById(R.id.titleText);
        final EditText detailEdit = dialog_view.findViewById(R.id.detailText);
        final RadioGroup colorRadio = dialog_view.findViewById(R.id.radioGroup);

        builder.setView(dialog_view);
        builder.setMessage(R.string.make_plan);
        builder.setPositiveButton(R.string.make, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String title = titleEdit.getText().toString();
                String detail = detailEdit.getText().toString();
                int radioId = colorRadio.getCheckedRadioButtonId();
                Fragment target = getTargetFragment();
                if (target == null) { return; }
                Intent data = new Intent();
                data.putExtra("title", title);
                data.putExtra("detail", detail);
                int backColorId;
                switch (radioId){
                    case R.id.first:
                        backColorId = R.color.listview_back_white;
                        break;
                    case R.id.second:
                        backColorId = R.color.listview_back_red;
                        break;
                    case R.id.third:
                        backColorId = R.color.listview_back_blue;
                        break;
                    case R.id.fourth:
                        backColorId = R.color.listview_back_orange;
                        break;
                    case R.id.fifth:
                        backColorId = R.color.listview_back_green;
                        break;
                    case R.id.sixth:
                        backColorId = R.color.listview_back_pink;
                        break;
                    case R.id.seventh:
                        backColorId = R.color.listview_back_yellow;
                        break;
                    default:
                        Log.d("デフォルト","入力なし");
                        backColorId = R.color.listview_back_selected;
                        break;
                }
                data.putExtra("backColor",backColorId);
                target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}