package com.example.mdpapplication.ui.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.MainActivity;
import com.example.mdpapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class CommunicationFragment extends Fragment {

    private static final String COMMUNICATION_FRAGMENT_TAG = "CommunicationFragment";

    private CommunicationViewModel communicationViewModel;

    private static final String PERSISTENT_STRING_KEY_1 = "persistent_string_1";
    private static final String PERSISTENT_STRING_KEY_2 = "persistent_string_2";
    private static final String PERSISTENT_STRING_DEFAULT_1 = "This is persistent text string 1";
    private static final String PERSISTENT_STRING_DEFAULT_2 = "This is persistent text string 2";
    private static final String RECEIVED_DATA_PLACEHOLDER = "Your received text strings will appear here";

    private Timer timer;
    private UpdateReceivedDataTimerTask updateReceivedDataTimerTask;
    private static final int RECEIVE_DATA_UPDATE_DELAY = 0;
    private static final int RECEIVE_DATA_UPDATE_INTERVAL = 500;

    private TextView textViewPersistentCommunicationString1;
    private TextView textViewPersistentCommunicationString2;
    private TextView textViewVolatileCommunicationString;
    private TextView textViewReceivedStrings;
    private Button persistentStringSendButton1;
    private Button persistentStringSendButton2;
    private Button volatileStringSendButton;
    private Button receivedDataClearButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        communicationViewModel =
                new ViewModelProvider(this).get(CommunicationViewModel.class);

        // Update receive data
        timer = new Timer();
        updateReceivedDataTimerTask = new UpdateReceivedDataTimerTask();

        View root = inflater.inflate(R.layout.fragment_communication, container, false);
        textViewPersistentCommunicationString1 = root.findViewById(R.id.editTextCommunicationString1);
        textViewPersistentCommunicationString2 = root.findViewById(R.id.editTextCommunicationString2);
        textViewVolatileCommunicationString = root.findViewById(R.id.editTextCommunicationString);
        textViewReceivedStrings = root.findViewById(R.id.textViewReceivedStrings);
        persistentStringSendButton1 = root.findViewById(R.id.stringSendButton1);
        persistentStringSendButton2 = root.findViewById(R.id.stringSendButton2);
        volatileStringSendButton = root.findViewById(R.id.stringSendButton);
        receivedDataClearButton = root.findViewById(R.id.receivedDataClearButton);

        textViewReceivedStrings.setMovementMethod(new ScrollingMovementMethod());
        textViewReceivedStrings.setText(RECEIVED_DATA_PLACEHOLDER);

        persistentStringSendButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendCommunicationMessage(textViewPersistentCommunicationString1.getText().toString());
            }
        });
        persistentStringSendButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendCommunicationMessage(textViewPersistentCommunicationString2.getText().toString());
            }
        });

        volatileStringSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendCommunicationMessage(textViewVolatileCommunicationString.getText().toString());
            }
        });

        receivedDataClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.resetReceivedTextStrings();
                textViewReceivedStrings.setText(RECEIVED_DATA_PLACEHOLDER);
                Log.d(COMMUNICATION_FRAGMENT_TAG, "Reset received data: " + textViewReceivedStrings.getText());
            }
        });

        timer.schedule(updateReceivedDataTimerTask, RECEIVE_DATA_UPDATE_DELAY, RECEIVE_DATA_UPDATE_INTERVAL);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Write persistent strings in SharedPreferences
        // https://stackoverflow.com/questions/21720089/how-do-i-use-shared-preferences-in-a-fragment-on-android
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSISTENT_STRING_KEY_1, "" + textViewPersistentCommunicationString1.getText());
        editor.putString(PERSISTENT_STRING_KEY_2, "" + textViewPersistentCommunicationString2.getText());
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get persistent strings from SharedPreferences
        // https://stackoverflow.com/questions/21720089/how-do-i-use-shared-preferences-in-a-fragment-on-android
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String communicationStringValue1 = sharedPreferences.getString(PERSISTENT_STRING_KEY_1, PERSISTENT_STRING_DEFAULT_1);
        String communicationStringValue2 = sharedPreferences.getString(PERSISTENT_STRING_KEY_2, PERSISTENT_STRING_DEFAULT_2);
        textViewPersistentCommunicationString1.setText(communicationStringValue1);
        textViewPersistentCommunicationString2.setText(communicationStringValue2);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////           Update Received Data           ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class UpdateReceivedDataTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!MainActivity.getReceivedTextStrings().isEmpty()) {
                            textViewReceivedStrings.setText(MainActivity.getReceivedTextStrings());
                        }
                    }
                });
            } catch (NullPointerException ignored) {

            } catch (Exception exception) {
                Log.d(COMMUNICATION_FRAGMENT_TAG, exception.getLocalizedMessage());
            }
        }
    }
}
