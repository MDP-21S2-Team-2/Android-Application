package com.example.mdpapplication.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.mdpapplication.MainActivity;

public class BluetoothService {

    private static final String BLUETOOTH_SERVICE_HANDLER_TAG = "BluetoothService Handler";
    private static final String BLUETOOTH_SERVICE_TAG = "BluetoothService";

    private String connectedDeviceName;
    private boolean isConnected;

    private static BluetoothCommunicationService bluetoothCommunicationService;
    private static BluetoothAdapter bluetoothAdapter;

    public BluetoothService() {
        bluetoothCommunicationService = new BluetoothCommunicationService(handler);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Public Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: Test this method on the tablet
    /**
     * Connect to a Bluetooth device.
     *
     * @param macAddress MAC address of the Bluetooth device to be connected
     * @return boolean value indicating whether connection is successful
     */
    public boolean connectToBluetoothDevice(String macAddress) {
        Log.d(BLUETOOTH_SERVICE_TAG, "Connecting to device with MAC address: " + macAddress);

        final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        bluetoothCommunicationService = new BluetoothCommunicationService(handler);
        bluetoothCommunicationService.connect(bluetoothDevice, false);

        return isConnected;
    }

    /**
     * Return Bluetooth connection status
     *
     * @return boolean value indicating whether the application is connected to another Bluetooth device
     */
    public boolean isConnectedToBluetoothDevice() {
        return isConnected;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////                  Handler                 ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // This method is adopted from The Android Open Source Project by Google
    /**
     * This handler handles messages from the BluetoothCommunicationService
     */
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothCommunicationService.STATE_CONNECTED: // bluetooth service has connected to a device
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_CONNECTED");
                            isConnected = true; // TODO: Set to false when disconnected (to receive message via LocalBroadcastManager)
                            MainActivity.updateBluetoothStatusFloatingActionButtonDisplay();
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                        case BluetoothCommunicationService.STATE_CONNECTING: // bluetooth service is connecting to a device
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_CONNECTING");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                        case BluetoothCommunicationService.STATE_LISTEN: // bluetooth service is listening for devices
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_LISTEN");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                        case BluetoothCommunicationService.STATE_NONE:
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_NONE");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // Construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    // TODO: Send received message to respective Fragment / MainActivity via LocalBroadcastManager
                    Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "MESSAGE_READ - " + readMessage);
                case Constants.MESSAGE_DEVICE_NAME:
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "MESSAGE_DEVICE_NAME - " + connectedDeviceName);
                    // TODO: Send bluetooth device name to MainActivity via LocalBroadcastManager
                    // TODO: Update bluetooth connection status to MainActivity via LocalBroadcastManager
                    break;
                case Constants.MESSAGE_TOAST:
                    Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "MESSAGE_TOAST");
                    // TODO: Check if received message is "Device connection was lost", update bluetooth connection status
                    // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                    break;
            }
        }
    };
}