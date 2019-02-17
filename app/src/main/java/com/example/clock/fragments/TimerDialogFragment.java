package com.example.clock.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;

import com.example.clock.R;

public class TimerDialogFragment extends DialogFragment {

    private OnPositiveButtonClickListener mOnPositiveButtonClickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        int h = (int) bundle.getLong("timer", 600_000) / 1_000 / 60 / 60 % 24;
        int m = (int) bundle.getLong("timer", 600_000) / 1_000 / 60 % 60;
        int s = (int) bundle.getLong("timer", 600_000) / 1_000 % 60;

        final View numberPickerView =
                View.inflate(getContext(), R.layout.partial_timer_picker, null);
        final NumberPicker hours =
                numberPickerView.findViewById(R.id.number_picker_hours);
        final NumberPicker minutes =
                numberPickerView.findViewById(R.id.number_picker_minutes);
        final NumberPicker seconds =
                numberPickerView.findViewById(R.id.number_picker_seconds);

        hours.setMaxValue(23);
        hours.setMinValue(0);
        hours.setValue(h);

        minutes.setMaxValue(59);
        minutes.setMinValue(0);
        minutes.setValue(m);

        seconds.setMaxValue(59);
        seconds.setMinValue(0);
        seconds.setValue(s);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_timer_title)
                .setView(numberPickerView)
                .setPositiveButton(
                        getString(R.string.dialog_set),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long fullTime =
                                (hours.getValue()*60*60*1_000) +
                                (minutes.getValue()*60*1_000) +
                                (seconds.getValue()*1_000);

                        mOnPositiveButtonClickListener.onPositiveClickListener(fullTime);
                    }
                })
                .setNegativeButton(
                        getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    public void setPositiveButtonClickListener(OnPositiveButtonClickListener onPositiveButtonClickListener) {
        mOnPositiveButtonClickListener = onPositiveButtonClickListener;
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveClickListener(long fullTime);
    }

}
