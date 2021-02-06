package com.example.mdpapplication.ui.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;

public class CommunicationFragment extends Fragment {

    private CommunicationViewModel communicationViewModel;

    private static CommunicationFragment instance;

    private static final String PERSISTENT_STRING_KEY_1 = "persistent_string_1";
    private static final String PERSISTENT_STRING_KEY_2 = "persistent_string_2";
    private static final String PERSISTENT_STRING_DEFAULT_1 = "This is persistent text string 1";
    private static final String PERSISTENT_STRING_DEFAULT_2 = "This is persistent text string 2";
    private static final String RECEIVED_DATA_PLACEHOLDER = "Your received text strings will appear here";


    private String receivedStrings;

    private TextView textViewPersistentCommunicationString1;
    private TextView textViewPersistentCommunicationString2;
    private TextView textViewVolatileCommunicationString;
    private TextView textViewReceivedStrings;
    private Button persistentStringSendButton1;
    private Button persistentStringSendButton2;
    private Button volatileStringSendButton;
    private Button receivedDataClearButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        communicationViewModel =
                new ViewModelProvider(this).get(CommunicationViewModel.class);

        instance = this;

        receivedStrings = "";

        View root = inflater.inflate(R.layout.fragment_communication, container, false);
        textViewPersistentCommunicationString1 = root.findViewById(R.id.editTextCommunicationString1);
        textViewPersistentCommunicationString2 = root.findViewById(R.id.editTextCommunicationString2);
        textViewVolatileCommunicationString = root.findViewById(R.id.editTextCommunicationString);
        textViewReceivedStrings = root.findViewById(R.id.textViewReceivedStrings); // TODO: Update textViewReceivedStrings when new strings are received
        persistentStringSendButton1 = root.findViewById(R.id.stringSendButton1);
        persistentStringSendButton2 = root.findViewById(R.id.stringSendButton2);
        volatileStringSendButton = root.findViewById(R.id.stringSendButton);
        receivedDataClearButton = root.findViewById(R.id.receivedDataClearButton);

        textViewReceivedStrings.setMovementMethod(new ScrollingMovementMethod());

        persistentStringSendButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Send textViewPersistentCommunicationString1
            }
        });

        persistentStringSendButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Send textViewPersistentCommunicationString2
            }
        });

        volatileStringSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Send textViewVolatileCommunicationString
            }
        });

        receivedDataClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                textViewReceivedStrings.setText(RECEIVED_DATA_PLACEHOLDER);
                receivedStrings = "";
            }
        });

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
        System.out.println("Saved to shared preferences: " + textViewPersistentCommunicationString1.getText());
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

    public static CommunicationFragment getInstance() {
        return instance;
    }

    public void updateReceivedStrings(String newReceivedString) {
        receivedStrings = newReceivedString + "\n" + receivedStrings;
        textViewReceivedStrings.setText(receivedStrings);
    }
}
