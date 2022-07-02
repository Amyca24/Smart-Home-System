package com.thesis.arduino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {


    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private BluetoothDevice mDevice;

    private boolean mIsUserInitiatedDisconnect = false;

    private boolean mIsBluetoothConnected = false;
    private ProgressDialog progressDialog;


    final static String livingLedOn = "49";
    final static String livingLedOff = "48";
    final static String bedroomLedOn = "5";
    final static String bedroomLedOff = "6";
    final static String livingWindowOpen = "90";
    final static String livingWindowClosed = "0";
    final static String doorOpen = "1";
    final static String doorClosed = "2";
    Button livingOn, livingOff, bedroomOn, bedroomOff,  livingOpen, livingClosed, dO, dC;
    ImageView light1, light2, window1, window2;
    TextView temp, humidity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);


        livingOn = (Button) findViewById(R.id.livingOn);
        livingOff = (Button) findViewById(R.id.livingOff);
        light1 = (ImageView) findViewById(R.id.light1);

        bedroomOn = (Button) findViewById(R.id.bedroomOn);
        bedroomOff = (Button) findViewById(R.id.bedroomOff);
        light2 = (ImageView) findViewById(R.id.light2);

        livingOpen = (Button) findViewById(R.id.livingOpen);
        livingClosed = (Button) findViewById(R.id.livingClosed);
        window1 = (ImageView) findViewById(R.id.window);
        window2 = (ImageView) findViewById(R.id.window2);
        temp = (TextView) findViewById(R.id.temp);
        humidity = (TextView) findViewById(R.id.humidity);
        dO = (Button) findViewById(R.id.doorOpen);
        dC = (Button) findViewById(R.id.doorClosed);



        //TURN ON LIGHT LIVINGROOM
        livingOn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(livingLedOn.getBytes());
                    light1.setImageResource(R.drawable.ic_light_on);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });

        //TURN OFF LIGHT LIVING ROOM
        livingOff.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(livingLedOff.getBytes());
                    light1.setImageResource(R.drawable.ic_light_off);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });



        //TURN ON LIGHT BEDROOM
        bedroomOn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(bedroomLedOn.getBytes());
                    light2.setImageResource(R.drawable.ic_light_on);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });

        //TURN OFF LIGHT BEDROOM
        bedroomOff.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(bedroomLedOff.getBytes());
                    light2.setImageResource(R.drawable.ic_light_off);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });



        //OPEN WINDOW LIVING ROOM
        livingOpen.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(livingWindowOpen.getBytes());
                    window1.setImageResource(R.drawable.ic_w_open);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });

        //CLOSE WINDOW LIVING ROOM
        livingClosed.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(livingWindowClosed.getBytes());
                    window1.setImageResource(R.drawable.ic_w_closed);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });



        //OPEN DOOR
        dO.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(doorOpen.getBytes());
                    window2.setImageResource(R.drawable.ic_w_open);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });

        //CLOSE DOOR
        dC.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {


                try {
                    mBTSocket.getOutputStream().write(doorClosed.getBytes());
                    window2.setImageResource(R.drawable.ic_w_closed);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });



    }



    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }


        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;

                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        //TEMPERATURE INFO FROM ARDUINO
                        temp.post(new Runnable() {
                            @Override
                            public void run() {
                                temp.setText("");
                                temp.append(strInput);

                                int txtLength = temp.getEditableText().length();
                                if (txtLength > mMaxChars) {
                                    temp.getEditableText().delete(0, txtLength - mMaxChars);
                                }
                            }
                        });


                        //Humidity INFO FROM ARDUINO
                        humidity.post(new Runnable() {
                            @Override
                            public void run() {
                                humidity.setText("");
                                humidity.append(strInput);

                                int txtLength = humidity.getEditableText().length();
                                if (txtLength > mMaxChars) {
                                    humidity.getEditableText().delete(0, txtLength - mMaxChars);
                                }
                            }
                        });


                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(MainActivity2.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}