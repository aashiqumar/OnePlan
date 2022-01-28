package com.store.oneplan.Dashboard;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.store.oneplan.R;

public class NotificationCounter {

    private TextView nNumber;
    private final int MAX_NUMBER = 99;
    private int n_number_counter = 1;

    public NotificationCounter(View view)
    {
        nNumber= view.findViewById(R.id.summary);

    }



    public void increaseNumber ()
    {
        n_number_counter++;

        if (n_number_counter> MAX_NUMBER)
        {
            Log.d("Counter", "Maximum Number Reached");

        }

        else
        {
            nNumber.setText(String.valueOf(n_number_counter));
        }
    }
}
