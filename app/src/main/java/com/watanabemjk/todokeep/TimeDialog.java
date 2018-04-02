package com.watanabemjk.todokeep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

public class TimeDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if(activity == null){
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialog_view = inflater.inflate(R.layout.time_dialog, null);
        final TimePicker timePicker = dialog_view.findViewById(R.id.timePicker);
        //CAUTION:builderするまえにsetIs24HourViewを記述しなければならない
        timePicker.setIs24HourView(true);
        builder.setView(dialog_view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                PlanListFragment instance = new PlanListFragment();
                instance.newTimeInstance(hour,minute);

                DialogFragment newFragment = new EditDialog();
                newFragment.setTargetFragment(getTargetFragment(),1);
                newFragment.show(getFragmentManager(), "test");
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}