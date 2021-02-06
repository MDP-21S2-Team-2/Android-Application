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
    private static final String DEVICE_CONNECTION_WAS_LOST = "device connection was lost";

    private boolean isConnected;
    private String connectedDeviceName;

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

    /**
     * Return connected Bluetooth device name
     *
     * @return Name of the connected Bluetooth device
     */
    public String getConnectedDeviceName() {
        return connectedDeviceName;
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
        public void handleMessage(Message message) {
            switch (message.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (message.arg1) {
                        case BluetoothCommunicationService.STATE_CONNECTED:
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_CONNECTED");
                            updateIsConnected(true);
                            break;
                        case BluetoothCommunicationService.STATE_CONNECTING:
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_CONNECTING");
                            updateIsConnected(false);
                            break;
                        case BluetoothCommunicationService.STATE_LISTEN:
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_LISTEN");
                            updateIsConnected(false);
                            break;
                        case BluetoothCommunicationService.STATE_NONE:
                            Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_NONE");
                            updateIsConnected(false);
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) message.obj;
                    // Construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, message.arg1);
                    // TODO: Send received message to respective Fragment / MainActivity via LocalBroadcastManager
                    Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "MESSAGE_READ - " + readMessage);
                case Constants.MESSAGE_DEVICE_NAME:
                    updateIsConnected(true);
                    connectedDeviceName = message.getData().getString(Constants.DEVICE_NAME);
                    Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "MESSAGE_DEVICE_NAME - " + connectedDeviceName);
                    break;
                case Constants.MESSAGE_TOAST:
                    String toastMessage = message.getData().getString(Constants.TOAST);
                    Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "MESSAGE_TOAST - " + toastMessage);
                    if (toastMessage.equalsIgnoreCase(DEVICE_CONNECTION_WAS_LOST)) {
                        isConnected = false;
                        connectedDeviceName = ""; // set name to empty string since connection was lost
                        MainActivity.updateBluetoothStatusFloatingActionButtonDisplay();
                    }
                    break;
            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Helper Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateIsConnected(boolean isConnectedValue) {
        isConnected = isConnectedValue;
        MainActivity.updateBluetoothStatusFloatingActionButtonDisplay();
    }
}