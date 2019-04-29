package com.smarthome.smartHomeArduinoProject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Connection extends Activity {

    String address = null;

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth;
    static BluetoothSocket btSocket;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static byte[] buffer = new byte[256];
    public static String EXTRA_ADDRESS = "device_address";

    static int bytes;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean ConnectSuccess = true; //if it's here, it's almost connected

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Connection.context = getApplicationContext();

        Intent newint = getIntent();
        address = newint.getStringExtra(Devicelist.EXTRA_ADDRESS); //receive the address of the bluetooth device

        new ConnectBT().execute(); //Call the class to connect
    }



    public static void turnOffLed() {
        if (btSocket != null) {
            try {
                byte[] message = "0".toString().getBytes();
                btSocket.getOutputStream().write(message);
                Toast.makeText(context, "0", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d("Error", String.valueOf(e));
            }
        }
    }

    public static void turnOnLed() {
        if (btSocket != null) {
            try {
                byte[] message = "1".toString().getBytes();
                btSocket.getOutputStream().write(message);
                Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d("Error", String.valueOf(e));
            }
        }
    }

    public static String getTemperature(){
        if (btSocket != null) {
            try {
                bytes = btSocket.getInputStream().read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                //return String.valueOf(bytes);
                return readMessage;
            } catch (IOException e) {
                Log.d("Error", String.valueOf(e));
            }
        }

        return "Null!";
    }

    public static void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                Log.d("Error", String.valueOf(e));
            }
        }


    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {

        progress = ProgressDialog.show(Connection.this, "Connecting...", "Please wait!!!");  //show a progress dialog
    }

    @Override
    protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
    {
        try {
            if (btSocket == null || !isBtConnected) {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection

            }
        } catch (IOException e) {
            ConnectSuccess = false;//if the try failed, you can check the exception here
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
        super.onPostExecute(result);

        if (!ConnectSuccess) {
            msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            finish();
        } else {
            msg("Connected.");
            isBtConnected = true;
            Intent i = new Intent(Connection.this, MainActivity.class);
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);

        }
        progress.dismiss();
    }


}
    // fast way to call Toast
    public void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


}