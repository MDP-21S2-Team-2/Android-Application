package com.example.mdpapplication.ui.bluetooth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;

public class BluetoothFragment extends Fragment {

    private BluetoothViewModel bluetoothViewModel;
    private ListView myDevicesListView;

    private ArrayAdapter<String> myDevicesArrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bluetoothViewModel =
                new ViewModelProvider(this).get(BluetoothViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        myDevicesListView = root.findViewById(R.id.myDevicesListView);

        // Set adapter for myDevicesArrayAdapter
        myDevicesArrayAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.device_name);
        myDevicesListView.setAdapter(myDevicesArrayAdapter);
//        myDevicesArrayAdapter.add("Test Device 1");
//        myDevicesArrayAdapter.add("Test Device 2");

        return root;
    }
}