package com.example.mdpapplication.ui.bluetooth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;
import com.example.mdpapplication.service.BluetoothService;

public class BluetoothFragment extends Fragment {

    private BluetoothViewModel bluetoothViewModel;
    private BluetoothService bluetoothService;

    private ListView myDevicesListView;
    private ListView otherDevicesListView;
    private Button enableBluetoothButton;
    private Button refreshMyDevicesButton;
    private Button refreshOtherDevicesButton;

    private ArrayAdapter<String> myDevicesArrayAdapter;
    private ArrayAdapter<String> otherDevicesArrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bluetoothViewModel =
                new ViewModelProvider(this).get(BluetoothViewModel.class);

        bluetoothService = new BluetoothService();

        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        myDevicesListView = root.findViewById(R.id.myDevicesListView);
        otherDevicesListView = root.findViewById(R.id.otherDevicesListView);
        enableBluetoothButton = root.findViewById(R.id.enableBluetoothButton);
        refreshMyDevicesButton = root.findViewById(R.id.refreshMyDevicesButton);
        refreshOtherDevicesButton = root.findViewById(R.id.refreshOtherDevicesButton);

        // Set adapter for myDevicesArrayAdapter
        myDevicesArrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.device_name);
        myDevicesListView.setAdapter(myDevicesArrayAdapter);
        // TODO: Remove dummy devices
        myDevicesArrayAdapter.add("Dummy Device 1");
        myDevicesArrayAdapter.add("Dummy Device 2");

        // Set adapter for myDevicesArrayAdapter
        otherDevicesArrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.device_name);
        otherDevicesListView.setAdapter(otherDevicesArrayAdapter);
        // TODO: Remove dummy devices
        otherDevicesArrayAdapter.add("Dummy Device A");
        otherDevicesArrayAdapter.add("Dummy Device B");

        enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Enable discovery by other bluetooth devices using BluetoothAdapter
            }
        });

        refreshMyDevicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Refresh my devices list
            }
        });

        refreshOtherDevicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Refresh other available devices list
            }
        });

        return root;
    }
}