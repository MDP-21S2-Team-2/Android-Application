package com.example.mdpapplication.ui.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.MainActivity;
import com.example.mdpapplication.R;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class BluetoothFragment extends Fragment {

    private static final String BLUETOOTH_FRAGMENT_TAG = "BluetoothFragment";

    private BluetoothViewModel bluetoothViewModel;
    private BluetoothAdapter bluetoothAdapter;

    // Snackbar messages
    private static final String CONNECTING_TO_DEVICE = "Connecting to ";
    private static final String CONNECTED_TO_DEVICE = "Connected to ";
    private static final String UNABLE_TO_CONNECT_TO_DEVICE = "Unable to connect to ";
    private static final String DEVICE_IS_DISCOVERABLE = "Your device is now discoverable via Bluetooth";

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        myDevicesListView = root.findViewById(R.id.myDevicesListView);
        otherDevicesListView = root.findViewById(R.id.otherDevicesListView);
        enableBluetoothButton = root.findViewById(R.id.enableBluetoothButton);
        refreshMyDevicesButton = root.findViewById(R.id.refreshMyDevicesButton);
        refreshOtherDevicesButton = root.findViewById(R.id.refreshOtherDevicesButton);

        // Initialize my devices list
        myDevicesArrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.device_name);
        myDevicesListView.setAdapter(myDevicesArrayAdapter);
        myDevicesListView.setOnItemClickListener(deviceListClickListener);
        refreshMyDevicesList();

        // Initialize other devices list
        otherDevicesArrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.device_name);
        otherDevicesListView.setAdapter(otherDevicesArrayAdapter);
        otherDevicesListView.setOnItemClickListener(deviceListClickListener);
        refreshOtherDeviceList();

        enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                enableBluetoothDiscovery(view);
            }
        });

        refreshMyDevicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                refreshMyDevicesList();
            }
        });

        refreshOtherDevicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                refreshOtherDeviceList();
            }
        });

        registerBluetoothBroadcastReceiver();

        return root;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////               Methods for UI             ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void enableBluetoothDiscovery(View view) {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
            Log.d(BLUETOOTH_FRAGMENT_TAG, "Enabled bluetooth discoverable");
        } else {
            Log.d(BLUETOOTH_FRAGMENT_TAG, "Bluetooth discoverable already enabled");
        }

        Snackbar.make(view, DEVICE_IS_DISCOVERABLE, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void refreshMyDevicesList() {
        myDevicesArrayAdapter.clear();

        for (BluetoothDevice bluetoothDevice : bluetoothAdapter.getBondedDevices()) {
            myDevicesArrayAdapter.add(getDeviceNameWithMacAddress(bluetoothDevice));
        }
    }

    private void refreshOtherDeviceList() {
        otherDevicesArrayAdapter.clear();

        // Cancel current discovery session (if any)
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Start new discovery session
        checkAndRequestBluetoothPermissions();
        bluetoothAdapter.startDiscovery();
        getActivity().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private final AdapterView.OnItemClickListener deviceListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView adapterView, View view, int arg2, long arg3) {
            String deviceName = ((TextView) view).getText().toString();
            Snackbar.make(view, CONNECTING_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            // Get the device MAC address from device name (the last 17 chars)
            String macAddress = deviceName.substring(deviceName.length() - 19, deviceName.length() - 2);

            // Call BluetoothService to connect with the selected device
            if (MainActivity.getBluetoothService().connectToBluetoothDevice(macAddress)) {
                Snackbar.make(view, CONNECTED_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Snackbar.make(view, UNABLE_TO_CONNECT_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////       Methods for BroadcastReceiver      ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void registerBluetoothBroadcastReceiver() {
        getActivity().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        getActivity().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        getActivity().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));

        getActivity().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        getActivity().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(BLUETOOTH_FRAGMENT_TAG, "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_FOUND");
                // Add new found device to other devices list
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String newDevice = getDeviceNameWithMacAddress(bluetoothDevice);
                otherDevicesArrayAdapter.add(newDevice);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.d(BLUETOOTH_FRAGMENT_TAG, "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_BOND_STATE_CHANGED");
                refreshMyDevicesList();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Log.d(BLUETOOTH_FRAGMENT_TAG, "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d(BLUETOOTH_FRAGMENT_TAG, "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_ACL_DISCONNECTED");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(BLUETOOTH_FRAGMENT_TAG, "bluetoothBroadcastReceiver: BluetoothAdapter.ACTION_DISCOVERY_FINISHED");
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.d(BLUETOOTH_FRAGMENT_TAG, "bluetoothBroadcastReceiver: BluetoothAdapter.ACTION_STATE_CHANGED");
            }
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Helper Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkAndRequestBluetoothPermissions() {
        int permissionCheck = ActivityCompat.checkSelfPermission(getContext(), "Manifest.permission.ACCESS_FINE_LOCATION")
                + ActivityCompat.checkSelfPermission(getContext(), "Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        } else {
            Log.d(BLUETOOTH_FRAGMENT_TAG, "Bluetooth permissions already ");
        }
    }

    @NotNull
    private String getDeviceNameWithMacAddress(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice.getName() == null) {
            return "Unnamed Device" + "\n| MAC Address: " + bluetoothDevice.getAddress() + " |";
        } else {
            return bluetoothDevice.getName() + "\n| MAC Address: " + bluetoothDevice.getAddress() + " |";
        }
    }
}
