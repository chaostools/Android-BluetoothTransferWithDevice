package com.bluetooth.transfertest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bluetooth.transfertest.BluetoothCheck;
import com.bluetooth.transfertest.BluetoothService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import android.text.ClipboardManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity{


	Context context;
	SharedPreferences prefs;
	Editor editor;
	Display display;
	
	static BluetoothDevice device;
	BluetoothService bluetooth_service;
	
	
	TextView topic_text;
	ImageView image;
	EditText editText;
	Button connect,copytext,send_data,cleartext,open_server;
	
	Bitmap bmp;

    
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.main);
		context = getApplicationContext();

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		editor = prefs.edit();
		
				

		Keys.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		
		
		
		image = (ImageView) findViewById(R.id.image);
		editText = (EditText) findViewById(R.id.editText);
		
		topic_text = (TextView) findViewById(R.id.text);
		if ( !getBluetoothMacAddress().equals( Keys.Server_Address )){
				topic_text.setText( "Client: " +getBluetoothMacAddress() );
		}else{
				topic_text.setText( "Server: " +getBluetoothMacAddress() );
		}
			
				
		
		
				
		
		copytext = (Button) findViewById(R.id.copytext);
		copytext.setOnClickListener(new View.OnClickListener() {
    	public void onClick(View view) {
		try{
			
			
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			clipboard.setText("" + editText.getText().toString() );
		    Toast.makeText( view.getContext(), "Copy ok", Toast.LENGTH_SHORT).show();
		    		
		    
		    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}   		
    	}		
    	});	
		

		cleartext = (Button) findViewById(R.id.cleartext);
		cleartext.setOnClickListener(new View.OnClickListener() {
    	public void onClick(View v) {
		try{
			
	         Main.this.runOnUiThread(new Runnable() {
             public void run() {
            	 
            	 editText.setText("");
             }
             });

		    		
		    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}   		
    	}		
    	});	
		
		


		
		
		
		connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new View.OnClickListener() {
    	public void onClick(View v) {
    	try{


    	    
    		if( bluetooth_service.getState() != BluetoothService.STATE_CONNECTED){
		            BluetoothDevice device = Keys.mBluetoothAdapter.getRemoteDevice( Keys.Server_Address );
		            bluetooth_service.connect(device);
    		}else{
    			
	    	        Main.this.runOnUiThread(new Runnable() {
	    	        public void run() {
	
	    	        	editText.setText("");
		    	        connect.setEnabled(false);

	    	        }
	    	        });
	    	        
	    	        
	    	        Thread t = new Thread(){
	    	        public void run(){
	    	        try{	
		    	    while(true){
		    	        	
		    	       
	    	        	Server_sendMessage();
	    	        	Thread.sleep(Keys.load_speeds);
	    	        	
	    	        	
		    	    }	
	    	    	} catch (Exception e) {
	    	    		e.printStackTrace();
	    	    	} 	
	    	        }
	    	        };
	    	        t.start();
	    	        



    		}
    		
    		

            
            
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 	
    	}		
    	});
		
	
		
		
		
}
	
	
	
public void onStart() {
super.onStart();


	        if (!Keys.mBluetoothAdapter.isEnabled()) {
	        	
				    Intent intent = new Intent();
				    intent.setClass( Main.this, BluetoothCheck.class);
				    startActivity(intent);
				    Main.this.finish();

	        }


	        if (bluetooth_service == null) {
	        	bluetooth_service = new BluetoothService(this, mHandler);
	        }


}
	
	
	 
	 
	 

//=============================================================================================
//============================================================================================    
//============================================================================================= 
	
	

static String DEVICE_NAME;
static String DEVICE_Address;
String mFilePath;
byte[] send;

boolean server_mode = false;
String[] server_array_md5;
String[] server_content;
int s = 0;

boolean receive_mode = false;
String[] receive_array_md5;
String client_content;
int c = 0;



private void display_name(String name, String address) {

	
	 if (!address.equals( Keys.Server_Address )){
		 connect.setText( "Send Photo" );
	 }else{
		 connect.setText( "Connected:" + DEVICE_NAME);
		 connect.setEnabled(false);
	 }
	 

}


private void Server_sendMessage() {

	 
	 
    if ( bluetooth_service.getState() != BluetoothService.STATE_CONNECTED) {
        return;
    }

    if( server_mode ){
    	return;
    }
    
    
    server_mode = true;
    try{
    	
    		mFilePath = Keys.RandomImage();

		
		    File f = new File( mFilePath );
			if(f.exists()){
			        	
		
		        	
	        		bmp = BitmapFactory.decodeFile( f.getAbsolutePath() );
	        		byte[] mmsend = image_to_base64(bmp);
	        		String server_temp_message = Base64.encodeToString( mmsend, Base64.DEFAULT);
	        		//editText.setText(server_temp_message);
	        	    
	
	        	    if( server_temp_message.length() > 0) {
	        	    	
		        	        int num = (int) Math.ceil( server_temp_message.length()/Keys.wirte_size );
		        	        server_array_md5 = new String[num+1];
		        	        server_content = new String[num+1];
		        	        
	
	        	        	int start = 0;
	        	        	int end = 0;
	        	        	for(s = 0; s <= num; s++) {
	        	        	
			        	        	if( s ==0 ){
			        	        		start = 0;
			        	        		end   = Keys.wirte_size;
			        	        	}else{
			        	        		start = (s*Keys.wirte_size);
			        	        		end   = ((s+1)*Keys.wirte_size);
			        	        	}
			        	        	
	
			        	        	if( s == (num) ){
			        	        		end = (server_temp_message.length()-start)+start; 
			        	        	}
			        	        	
	
			        	        	server_array_md5[s] = getMD5EncryptedString(server_temp_message.substring( start, end));
			        	        	server_content[s] = server_temp_message.substring( start, end);
			        	        	//Log.v( "write"+  p + " to "+ num  , "send data " + start + " to " + end + " MD5=" + server_content[p] );
		
			        	    }
	        	        	
	
	        	        	String temp_md5_array = Arrays.toString(server_array_md5);
	        	        	temp_md5_array = Keys.start_md5 + temp_md5_array;
	
	        	        	send = temp_md5_array.getBytes();
	        	        	bluetooth_service.write(send);
	
	        	        	
			        }
		    
		        	    
		        	    
			}      
    }catch(Exception e){
        Log.e(Keys.log, e.toString());
    }
    
    
    
}	 







private void Client_Save_MD5(String receive_md5){

	    if( receive_mode ){
	    	return;
	    }	
	    

		receive_mode = true;
		receive_md5 = receive_md5.replaceAll( Keys.start_md5 ,"");

	
	 	
	 	receive_array_md5 = receive_md5.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
	 	client_content = "";
	 	c = 0;
	 	
	 	
	 	
	 	if(receive_array_md5.length > 0 ){
	 		
	     	send = ( Keys.receive_md5 + receive_array_md5[0] ).getBytes();
	     	bluetooth_service.write(send);
	     	
	 	}	
	
	
}




private void Server_sendImage(String md5_key) {
	md5_key = md5_key.replaceAll( Keys.receive_md5 ,"");
	
	
	for (int i = 0; i < server_array_md5.length; i++){
		
			String temp = server_array_md5[i];
			if(temp.equals(md5_key)){               
		
				
		    	send = ( Keys.send_content + server_content[i] ).getBytes();
		    	bluetooth_service.write(send);
		    	
		    }
			
	}
	

}




private void Client_CheckMD5_And_Save(String data) {
	
	
	data = data.replaceAll( Keys.send_content ,"");
	String temp_md5 = getMD5EncryptedString(data);
	
		
	
		if(receive_array_md5[c].equals(temp_md5)){
			
				

				client_content += data;
				
				c++;
				if( c < receive_array_md5.length ){
						
			     	send = ( Keys.receive_md5 + receive_array_md5[c] ).getBytes();
			     	bluetooth_service.write(send);
			     	
				}
				
		}else{
			

		     	send = ( Keys.receive_md5 + receive_array_md5[c] ).getBytes();
		     	bluetooth_service.write(send);
				
		}
		
	
		if( c >= receive_array_md5.length ){
			
				
				String temp = client_content;
				//editText.setText(temp);
	        	byte[] o = Base64.decode( temp,Base64.DEFAULT);
	        	Bitmap bitmap = BitmapFactory.decodeByteArray( o, 0, o.length);

	        	bitmap = resizeBitmap( bitmap, display.getWidth(), (int)(display.getWidth()*1.02) );
		      	image.setImageBitmap(bitmap);



		      	receive_mode = false;
		      	
		     	send = ( Keys.send_end  ).getBytes();
		     	bluetooth_service.write(send);
		     	
		}
	
	
	
	
}


private void Server_Send_Again(String data) {
	
	
	data = data.replaceAll( Keys.send_end ,"");
	String temp = getMD5EncryptedString(data);
	server_mode = false;
	
	
}

//=============================================================================================
//============================================================================================    
//============================================================================================= 



public static final int MESSAGE_DEVICE_NAME = 1;
public static final int READ_MESSAGE = 2;


private final Handler mHandler = new Handler() {
public void handleMessage(Message msg) {
switch (msg.what) {


case MESSAGE_DEVICE_NAME:
	
	DEVICE_NAME = msg.getData().getString( DEVICE_NAME );
	DEVICE_Address = msg.getData().getString( DEVICE_Address );
	display_name( DEVICE_NAME, DEVICE_Address);

break;


case READ_MESSAGE:
    	 
	
     byte[] readBuf = (byte[]) msg.obj;
     String receive_Message = new String(readBuf, 0, msg.arg1);



		 if(receive_Message.contains(Keys.start_md5)){

			 	Client_Save_MD5(receive_Message);
 	
		 }

		 if(receive_Message.contains(Keys.receive_md5)){

			 	Server_sendImage(receive_Message);
			 	
		 }
	 
		 
		 if(receive_Message.contains(Keys.send_content)){

			 Client_CheckMD5_And_Save(receive_Message);
 
		 }
		 
		 if(receive_Message.contains(Keys.send_end)){

			 Server_Send_Again(receive_Message);
 
		 }		 
		 
		 
break;
}
}
};
	




//=============================================================================================
//============================================================================================    
//============================================================================================= 



public synchronized void onResume() {
    super.onResume();

    if (bluetooth_service != null) {
        if (bluetooth_service.getState() == BluetoothService.STATE_NONE) {
        	bluetooth_service.start();
        }
    }
}


public void onDestroy() {
    super.onDestroy();
    if (bluetooth_service != null) {
    	bluetooth_service.stop();
    }
}



//=============================================================================================
//============================================================================================    
//============================================================================================= 



public static byte[] image_to_base64(Bitmap bmp) {
	
	Bitmap immage = bmp;
	
    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    immage.compress(Bitmap.CompressFormat.JPEG, 60, baos);
    byte[] b = baos.toByteArray();
    
	

	return b;
}



//=============================================================================================
//============================================================================================    
//============================================================================================= 


public Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight){


	
	int width = bitmap.getWidth();
    int height = bitmap.getHeight();


    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;


    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);



    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	



    return  bitmap;

}




//=============================================================================================
//============================================================================================    
//============================================================================================= 



public static String getMD5EncryptedString(String encTarget){
    MessageDigest mdEnc = null;
    try{
        mdEnc = MessageDigest.getInstance("MD5");
        
    }catch (NoSuchAlgorithmException e){
        System.out.println("Exception while encrypting to md5");
        e.printStackTrace();
    }
    
    mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
    String md5 = new BigInteger(1, mdEnc.digest()).toString(16) ;
    
    
    return md5;
}




//=============================================================================================
//============================================================================================    
//============================================================================================= 




public String getBluetoothMacAddress() {
	

    if (!Keys.mBluetoothAdapter.isEnabled()) {
		Intent intent = new Intent();
		intent.setClass( Main.this, BluetoothCheck.class);
		startActivity(intent);
		Main.this.finish();
    }
    Log.i(Keys.log , "Address:" + Keys.mBluetoothAdapter.getAddress() );
    
    
    
    return Keys.mBluetoothAdapter.getAddress();
}   
    	
	
	
	
}
