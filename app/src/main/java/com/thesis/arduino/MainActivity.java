package com.thesis.arduino;


import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;
    //   private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;


    private BluetoothAdapter mBTAdapter;
    private static final int BT_ENABLE_REQUEST = 10;
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mBufferSize = 50000; //Default
    public static final String DEVICE_EXTRA = "com.thesis.arduino.SOCKET";
    public static final String DEVICE_UUID = "com.thesis.arduino.uuid";
    private static final String DEVICE_LIST = "com.thesis.arduino.devicelist";
    private static final String DEVICE_LIST_SELECTED = "com.thesis.arduino.devicelistselected";
    public static final String BUFFER_SIZE = "com.thesis.arduino.buffersize";
    //   private static final String TAG = "BlueTest5-MainActivity";
    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;
    private boolean mIsUserInitiatedDisconnect = false;

    private boolean mIsBluetoothConnected = false;
    private ProgressDialog progressDialog;





    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISABLE_BT = 1;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView Bstatus, pairedDevices;
    ListView discoveredDevices;
    ImageView Bicon;
    Button onBtn, offBtn, discoBtn, pairedBtn, devicesBtn;

    private static final String appName = "thesis";
    private static final UUID uuid = UUID.randomUUID();

    BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Bluetooth config
        Bstatus = findViewById(R.id.Bstatus);
        pairedDevices = findViewById(R.id.pairedDevices);
        Bicon = findViewById(R.id.Bicon);
        onBtn = findViewById(R.id.onBtn);
        offBtn = findViewById(R.id.offBtn);
        discoBtn = findViewById(R.id.discoBtn);
        pairedBtn = findViewById(R.id.pairedBtn);
        discoveredDevices = findViewById(R.id.discoveredDevices);
        devicesBtn = findViewById(R.id.connect);


        //adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
            if (list != null) {
                initList(list);
                MyAdapter adapter = (MyAdapter) discoveredDevices.getAdapter();
                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    devicesBtn.setEnabled(true);
                }
            } else {
                initList(new ArrayList<BluetoothDevice>());
            }
        } else {
            initList(new ArrayList<BluetoothDevice>());
        }


        //checks if bt is available
        if (bluetoothAdapter == null) {
            Bstatus.setText("Bluetooth is not available");

        } else {
            Bstatus.setText("Bluetooth is available");
        }


        //set image if bt is on/off

        if (bluetoothAdapter.isEnabled()) {
            Bicon.setImageResource(R.drawable.ic_action_bon);
        } else {
            Bicon.setImageResource(R.drawable.ic_action_boff);
        }


        //on btn click
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bluetoothAdapter.isEnabled()) {
                    showToast("Turning On Bluetooth...");
                    //intent  to bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    showToast("Bluetooth is already on!");
                }
            }
        });


        //off btn click
    /*b    offBtn.setOnClickListener(new View.OnClickListener() {
       /*     @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    bluetoothAdapter.disable();
                    showToast("Turning Bluetooth Off...");
                    Bicon.setImageResource(R.drawable.ic_action_boff);
                } else {
                    showToast("Bluetooth is already off");
                }

            }

            }); */


        //disco btn click
        discoBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (!bluetoothAdapter.isDiscovering()) {
                    showToast("Making your device discoverable..");
                }
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, REQUEST_DISCOVER_BT);

            }
        }));


        //paired devices btn click
        pairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBTAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBTAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
                } else if (!mBTAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);
                } else {
                    new SearchDevices().execute();
                }
            }
        });


        //discovered devices btn click
        devicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                BluetoothDevice device = ((MyAdapter) (discoveredDevices.getAdapter())).getSelectedItem();
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                intent.putExtra(DEVICE_EXTRA, device);
                intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
                intent.putExtra(BUFFER_SIZE, mBufferSize);
                startActivity(intent);

            }
        });

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                //  discoveredDevices.append("\n " + device.getName() + "," + device);
                showToast("Found device " + device.getName());
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    //BLUETOOTH ON
                    Bicon.setImageResource(R.drawable.ic_action_bon);
                    showToast("Bluetooth is on!");

                }else{
                    //user denied to turn on bluetooth
                    showToast("COULD NOT TURN BLUETOOTH ON!");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //toast msg function
    private void showToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    //Bletooth devices list
    private void initList(List<BluetoothDevice> objects) {
        final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_item, R.id.lstContent, objects);
        discoveredDevices.setAdapter(adapter);
        discoveredDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                devicesBtn.setEnabled(true);
            }
        });
    }


    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                listDevices.add(device);
            }
            return listDevices;

        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                MyAdapter adapter = (MyAdapter) discoveredDevices.getAdapter();
                adapter.replaceItems(listDevices);
            } else {
                // msg("No paired devices found, please pair your serial BT device and try again");
            }
        }

    }

    private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
        private int selectedIndex;
        private Context context;
        private int selectedColor = Color.parseColor("#abcdef");
        private List<BluetoothDevice> myList;

        public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(ctx, resource, textViewResourceId, objects);
            context = ctx;
            myList = objects;
            selectedIndex = -1;
        }

        public void setSelectedIndex(int position) {
            selectedIndex = position;
            notifyDataSetChanged();
        }

        public BluetoothDevice getSelectedItem() {
            return myList.get(selectedIndex);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView tv;
        }

        public void replaceItems(List<BluetoothDevice> list) {
            myList = list;
            notifyDataSetChanged();
        }

        public List<BluetoothDevice> getEntireList() {
            return myList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;
            if (convertView == null) {
                vi = LayoutInflater.from(context).inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.tv = (TextView) vi.findViewById(R.id.lstContent);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                holder.tv.setBackgroundColor(selectedColor);
            } else {
                holder.tv.setBackgroundColor(Color.WHITE);
            }
            BluetoothDevice device = myList.get(position);
            holder.tv.setText(device.getName() + "\n " + device.getAddress());

            return vi;
        }

    }


}