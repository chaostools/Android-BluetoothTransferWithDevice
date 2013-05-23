package com.bluetooth.transfertest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;





public class BluetoothCheck extends Activity {

  
	Context context;
	SharedPreferences prefs;

	Builder nofounddialog;
	private boolean p;
	
	int REQUEST_ENABLE_BT = 1000;
	
	
	
	
	
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.bluetoothcheck); 
	
			context = getApplicationContext();
			prefs = PreferenceManager.getDefaultSharedPreferences(context); 

    }
    
    

	private void TurnPage(){
		
    	p = false;
		Intent intent = new Intent();
		intent.setClass(BluetoothCheck.this, Main.class);
		startActivity(intent);
		BluetoothCheck.this.finish();
		
	}
	
	
	
	
	
	
	
	
    
	protected void onStart() {
	super.onStart();
	
			p = true;

		    Keys.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    if(Keys.mBluetoothAdapter == null) {
		        
		    	nofound_dialog();
		        return;
		    }
		            

		    if (Keys.mBluetoothAdapter.isEnabled()) {
		    			TurnPage();
		    	
		    }else{	
		    	
		    		try{

		    			
			            if (Keys.mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
			                Keys.mBluetoothAdapter.enable();
			            }
					    

					} catch (Exception e) {
						e.printStackTrace();
					}	
					
					check_blue_tooth_open();

		    }
	}
    
	


	
	
	
	private void check_blue_tooth_open() {
		
			
			Thread m = new Thread() {
			public void run() {
			while (p){
				
				try{
	
						Thread.sleep(500);
					    if (Keys.mBluetoothAdapter.isEnabled()) {
					    			TurnPage();
					    }
						
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			}
			};
			m.start();
			
			
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
			public void run() {
			if(p==true){
				
		            Intent mIntentOpenBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		            startActivityForResult(mIntentOpenBT, REQUEST_ENABLE_BT);
		            
			}
			}
			}, 8000 );
			
			
	}




	private void nofound_dialog() {

		
        nofounddialog = new AlertDialog.Builder(this);
        nofounddialog.setCancelable(false);
        nofounddialog.setMessage("No Found Bluetooth");
        nofounddialog.setPositiveButton( "OK" , new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
     	   
            	BluetoothCheck.this.finish();
        }
        });
    	BluetoothCheck.this.runOnUiThread(new Runnable() {
        public void run() {
        		nofounddialog.show();
    	}
    	});
        
	}
    
	
	
	
    

	
	
	
	
	
	
	
	
	
	
	
	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	super.onActivityResult(requestCode, resultCode, data); 
				

			if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK ) {  
			try{	
				
					if (Keys.mBluetoothAdapter.isEnabled()) {
						TurnPage();
					}
  
			}catch(Exception e){   e.printStackTrace();  }      
			}  

	}
	
    
	
	
    
	
	
	
	
	
}