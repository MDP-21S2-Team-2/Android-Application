package com.example.mdpapplication.ui.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private static final String PERSISTENT_STRING_KEY_1 = "persistent_string_1";
    private static final String PERSISTENT_STRING_KEY_2 = "persistent_string_2";
    private static final String PERSISTENT_STRING_DEFAULT_1 = "This is persistent text string 1";
    private static final String PERSISTENT_STRING_DEFAULT_2 = "This is persistent text string 2";

    private CommunicationViewModel communicationViewModel;
    private TextView textViewCommunicationString1;
    private TextView textViewCommunicationString2;
    private Button stringSendButton1;
    private Button stringSendButton2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        communicationViewModel =
                new ViewModelProvider(this).get(CommunicationViewModel.class);

        View root = inflater.inflate(R.layout.fragment_communication, container, false);
        textViewCommunicationString1 = root.findViewById(R.id.editTextCommunicationString1);
        textViewCommunicationString2 = root.findViewById(R.id.editTextCommunicationString2);
        stringSendButton1 = root.findViewById(R.id.stringSendButton1);
        stringSendButton2 = root.findViewById(R.id.stringSendButton2);

        stringSendButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: Send textViewCommunicationString1
            }
        });

        stringSendButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: Send textViewCommunicationString2
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
        editor.putString(PERSISTENT_STRING_KEY_1, "" + textViewCommunicationString1.getText());
        System.out.println("Saved to shared preferences: " + textViewCommunicationString1.getText());
        editor.putString(PERSISTENT_STRING_KEY_2, "" + textViewCommunicationString2.getText());
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
        textViewCommunicationString1.setText(communicationStringValue1);
        textViewCommunicationString2.setText(communicationStringValue2);
    }
}
