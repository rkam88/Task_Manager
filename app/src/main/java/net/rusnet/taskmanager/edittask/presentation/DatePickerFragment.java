package net.rusnet.taskmanager.edittask.presentation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_CALENDAR = "ARG_CALENDAR";

    private OnDatePickerDialogResultListener mCallback;

    public interface OnDatePickerDialogResultListener extends DatePickerDialog.OnDateSetListener {
        void onDatePickerDialogCancelClick();
    }

    public static DatePickerFragment newInstance() {
        return newInstance(null);
    }

    public static DatePickerFragment newInstance(@Nullable Calendar calendar) {
        Bundle args = new Bundle();
        if (calendar != null) args.putSerializable(ARG_CALENDAR, calendar);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickerDialogResultListener) {
            mCallback = (OnDatePickerDialogResultListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement DatePickerFragment.OnDatePickerDialogResultListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        Calendar calendar = null;
        if (args != null) {
            calendar = (Calendar) args.getSerializable(ARG_CALENDAR);
        }
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    mCallback.onDatePickerDialogCancelClick();
                }
            }
        });
        return dialog;
    }

}
