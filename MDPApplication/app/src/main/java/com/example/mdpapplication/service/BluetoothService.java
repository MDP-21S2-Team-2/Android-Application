package com.example.mdpapplication.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BluetoothService {

    private static final String HANDLER_LOG = "BluetoothService Handler Log: ";

    private String connectedDeviceName;
    private boolean isConnected;

    private static BluetoothCommunicationService bluetoothCommunicationService;

    public BluetoothService() {
        bluetoothCommunicationService = new BluetoothCommunicationService(handler);
    }

    // This method is adopted from The Android Open Source Project by Google
    /**
     * The Handler that gets information back from the BluetoothCommunicationService
     */
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothCommunicationService.STATE_CONNECTED: // bluetooth service has connected to a device
                            Log.d(HANDLER_LOG, "STATE_CONNECTED");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                        case BluetoothCommunicationService.STATE_CONNECTING: // bluetooth service is connecting to a device
                            Log.d(HANDLER_LOG, "STATE_CONNECTING");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                        case BluetoothCommunicationService.STATE_LISTEN: // bluetooth service is listening for devices
                            Log.d(HANDLER_LOG, "STATE_LISTEN");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                        case BluetoothCommunicationService.STATE_NONE:
                            Log.d(HANDLER_LOG, "STATE_NONE");
                            // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // Construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    // TODO: Send received message to respective Fragment / MainActivity via LocalBroadcastManager
                    Log.d(HANDLER_LOG, "MESSAGE_READ - " + readMessage);
                case Constants.MESSAGE_DEVICE_NAME:
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.d(HANDLER_LOG, "MESSAGE_DEVICE_NAME - " + connectedDeviceName);
                    // TODO: Send bluetooth device name to BluetoothFragment via LocalBroadcastManager
                    // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                    break;
                case Constants.MESSAGE_TOAST:
                    Log.d(HANDLER_LOG, "MESSAGE_TOAST");
                    // TODO: Check if received message is "Device connection was lost", update bluetooth connection status
                    // TODO: Update bluetooth connection status to BluetoothFragment and MainActivity via LocalBroadcastManager
                    break;
            }
        }
    };
}