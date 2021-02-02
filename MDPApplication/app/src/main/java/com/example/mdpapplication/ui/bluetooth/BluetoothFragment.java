package com.example.mdpapplication.ui.bluetooth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;
import com.example.mdpapplication.service.BluetoothService;
import com.google.android.material.snackbar.Snackbar;

public class BluetoothFragment extends Fragment {

    private BluetoothViewModel bluetoothViewModel;
    private BluetoothService bluetoothService;

    // Snackbar messages
    private static final String CONNECTING_TO_DEVICE = "Connecting to device ";
    private static final String CONNECTED_TO_DEVICE = "Connected to device ";
    private static final String UNABLE_TO_CONNECT_TO_DEVICE = "Unable to connect to device ";

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
        myDevicesListView.setAdapter(myDevicesArrayAdapter); // TODO: Include MAC address for devices
        myDevicesListView.setOnItemClickListener(deviceListClickListener);
        // TODO: Remove dummy devices
        myDevicesArrayAdapter.add("Dummy Dummy Device 1");
        myDevicesArrayAdapter.add("Dummy Dummy Device 2");

        // Set adapter for myDevicesArrayAdapter
        otherDevicesArrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.device_name);
        otherDevicesListView.setAdapter(otherDevicesArrayAdapter); // TODO: Include MAC address for devices
        otherDevicesListView.setOnItemClickListener(deviceListClickListener);
        // TODO: Remove dummy devices
        otherDevicesArrayAdapter.add("Dummy Dummy Device A");
        otherDevicesArrayAdapter.add("Dummy Dummy Device B");

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

    private final AdapterView.OnItemClickListener deviceListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView adapterView, View view, int arg2, long arg3) {
            String deviceName = ((TextView) view).getText().toString();
            Snackbar.make(view, CONNECTING_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            // Get the device MAC address from device name (the last 17 chars)
            String macAddress = deviceName.substring(deviceName.length() - 17);

            // Call BluetoothService to connect with the selected device
            if (bluetoothService.connectToBluetoothDevice(macAddress)) {
                Snackbar.make(view, CONNECTED_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Snackbar.make(view, UNABLE_TO_CONNECT_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };
}