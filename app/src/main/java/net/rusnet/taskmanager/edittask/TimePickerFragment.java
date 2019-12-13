package net.rusnet.taskmanager.edittask;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_CALENDAR = "ARG_CALENDAR";

    public static TimePickerFragment newInstance() {
        return newInstance(null);
    }

    public static TimePickerFragment newInstance(@Nullable Calendar calendar) {
        Bundle args = new Bundle();
        if (calendar != null) args.putSerializable(ARG_CALENDAR, calendar);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
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

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener listener = (TimePickerDialog.OnTimeSetListener) getActivity();

        return new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

}
