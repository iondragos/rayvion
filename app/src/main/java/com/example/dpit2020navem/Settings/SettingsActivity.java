package com.example.dpit2020navem.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dpit2020navem.AddAnObject.Activity.ObjectTypeMenuActivity;
import com.example.dpit2020navem.Database.OwnedObjectsDatabase;
import com.example.dpit2020navem.Help.HelpActivity;
import com.example.dpit2020navem.HomePage.MainActivity;
import com.example.dpit2020navem.ObjectTypeDetailes.ObjectTypeDetailesActivity;
import com.example.dpit2020navem.OwnedObjectsList.OwnedObjectsListActivity;
import com.example.dpit2020navem.R;
import com.example.dpit2020navem.UvcInfo.UvcInfoActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {

    Button buttonSideMenu;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView sideMenu;
    Button buttonDeleteAllObjects;
    OwnedObjectsDatabase database;
    TextView buttonConectToBluetoothDevice;
    ListView bluetoothDeviceList;
    ConstraintLayout layoutBluetoothDevices;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    ProgressDialog progress;
    boolean isBtConnected = false;
    String address;
    static String EXTRA_ADDRESS = "device_address";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layoutBluetoothDevices = findViewById(R.id.layoutBluetoothDevices);
        layoutBluetoothDevices.setVisibility(View.INVISIBLE);


        setUpSideMenu();
        openSideMenu();
        cleanOwnedObjectsList();
        bluetoothConection();

    }

    private void setUpSideMenu(){
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        sideMenu = (NavigationView)findViewById(R.id.sideMenu);
        sideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public  boolean onNavigationItemSelected(@NonNull MenuItem item){
                int id = item.getItemId();

                if(id == R.id.homePage){
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id == R.id.addAnObject){
                    Intent intent = new Intent(SettingsActivity.this, ObjectTypeMenuActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id == R.id.ownedObjectList) {
                    Intent intent = new Intent(SettingsActivity.this, OwnedObjectsListActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id == R.id.objectTypeDetailes) {
                    Intent intent = new Intent(SettingsActivity.this, ObjectTypeDetailesActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id == R.id.UVCinfo) {
                    Intent intent = new Intent(SettingsActivity.this, UvcInfoActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id == R.id.settings) {
                    drawerLayout.closeDrawer(sideMenu);
                }else if(id == R.id.help) {
                    Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void openSideMenu(){
        buttonSideMenu = findViewById(R.id.buttonSideMenu);
        buttonSideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(sideMenu);
            }
        });
    }

    private void cleanOwnedObjectsList(){
        database = new OwnedObjectsDatabase(this);

        buttonDeleteAllObjects = findViewById(R.id.buttonDeleteAllObjects);

        buttonDeleteAllObjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.cleanOwnedObjectsDatabase();
            }
        });
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void bluetoothConection(){
        buttonConectToBluetoothDevice = findViewById(R.id.conectToBluetoothDevice);
        bluetoothDeviceList = findViewById(R.id.bluetoothDevicesList);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        buttonConectToBluetoothDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });
    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList bluetoothDevicesList = new ArrayList();

        if (pairedDevices.size() > 0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                bluetoothDevicesList.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, bluetoothDevicesList);
        bluetoothDeviceList.setAdapter(adapter);
        bluetoothDeviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

        layoutBluetoothDevices.setVisibility(View.VISIBLE);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            layoutBluetoothDevices.setVisibility(View.INVISIBLE);
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);

            new ConnectBT().execute(); //Call the class to connect
        }
    };

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(SettingsActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

}