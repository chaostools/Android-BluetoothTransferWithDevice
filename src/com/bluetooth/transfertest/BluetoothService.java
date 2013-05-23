package com.bluetooth.transfertest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;







public class BluetoothService {

	
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
	

	
    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }
	
    
    public synchronized void start() {

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    
    }
    
    
    

//=============================================================================================
//============================================================================================    
//============================================================================================= 

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        
        public AcceptThread() {
            	BluetoothServerSocket tmp = null;

	            try {
	                tmp = mAdapter.listenUsingRfcommWithServiceRecord( Keys.uuname, Keys.uuid);
	            } catch (IOException e) {
	                Log.e(Keys.log, "listen() failed", e);
	            }
	            mmServerSocket = tmp;
        }

        public void run() {

            setName("AcceptThread");
            BluetoothSocket socket = null;


            while (mState != STATE_CONNECTED) {
                try{

                    socket = mmServerSocket.accept();
                    
                }catch(IOException e){
                    Log.e(Keys.log, "accept() failed", e);
                    break;
                }


                if (socket != null) {
                synchronized (BluetoothService.this) {
                	
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:

                            connected(socket, socket.getRemoteDevice());
                            break;
                            
                        case STATE_NONE:
                        case STATE_CONNECTED:

                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(Keys.log, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                        
                }
                }
            }

        }

        public void cancel() {
            try{
                mmServerSocket.close();
            }catch (IOException e){
                Log.e(Keys.log, "close() of server failed", e);
            }
        }
        
    }


    
//=============================================================================================
//============================================================================================    
//============================================================================================= 
  
    
	
    public synchronized void connect(BluetoothDevice device) {
    	

        
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }



    
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;


            try {
                tmp = device.createRfcommSocketToServiceRecord(Keys.uuid);
            } catch (IOException e) {
                Log.e(Keys.log, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(Keys.log, "BEGIN mConnectThread");
            setName("ConnectThread");


            mAdapter.cancelDiscovery();


            try {
                mmSocket.connect();
                
            } catch (IOException e) {
            	
	                connectionFailed();
	                try {
	                    mmSocket.close();
	                } catch (IOException e2) {
	                    Log.e(Keys.log, "unable to close() socket during connection failure", e2);
	                }
	                BluetoothService.this.start();
	                return;
                
            }


            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(Keys.log, "close() of connect socket failed", e);
            }
        }
    }
    
    

    
    
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(Keys.log, "connected");

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        
        Message msg = mHandler.obtainMessage( Main.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString( Main.DEVICE_NAME, device.getName());
        bundle.putString( Main.DEVICE_Address, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
        
    }


//=============================================================================================
//============================================================================================    
//============================================================================================= 
    

    int m_bytes;
    String temp;
    String total_text;
    String total_image;
    Boolean read = false;

private class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    
    
    public ConnectedThread(BluetoothSocket socket) {
        Log.d(Keys.log, "create ConnectedThread");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            connectionLost(e);

        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    
    
    public void run() {
        Log.i(Keys.log, "BEGIN mConnectedThread");
        final byte[] buffer = new byte[Keys.read_size];
        int bytes;

        while(true){
        try{

            	bytes = mmInStream.read(buffer);
            	mHandler.obtainMessage( Main.READ_MESSAGE, bytes, -1, buffer).sendToTarget();


        } catch (Exception e) {
            connectionLost(e);
            break;
        }
        }
    }


    
    public void write(byte[] buffer) {
    try{
        	
            mmOutStream.write(buffer);
            //mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            //Log.e("wirte","run");
        
    } catch (IOException e) {
        	Log.e(Keys.log, "Exception during write", e);
    }
    }

    
    public void cancel() {
    try {
        	mmSocket.close();
    } catch (IOException e) {
        	Log.e(Keys.log, "close() of connect socket failed", e);
    }
    }
    
}


    
    
    

    
    

    
    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }


    private static final boolean D = true;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    
    
    
    
    
    public synchronized int getState() {
        return mState;
    }
    
    
    
    private synchronized void setState(int state) {
    	
        if (D) Log.d(Keys.log, "setState() " + mState + " -> " + state);
        mState = state;
        //mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        
    }    
    
    private void connectionFailed() {
        setState(STATE_LISTEN);
        /*
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */
    }


    private void connectionLost(Exception e) {
        setState(STATE_LISTEN);

        Keys.mBluetoothAdapter = null;

        // Send a failure message back to the Activity

        //Message msg = mHandler.obtainMessage(openapp.MESSAGE_TOAST);
        //Bundle bundle = new Bundle();
        //bundle.putString(openapp.TOAST, "Device connection was lost");
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);

    }

    
    public synchronized void stop() {
        if (D) Log.d(Keys.log, "stop");
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }
    
    
    
    
    
    



    
    
    
    
	
	
	
	
	
}
