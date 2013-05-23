package com.bluetooth.transfertest;

import java.util.Random;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;

public class Keys {


	protected static final String Server_Address = "";
	
	
	public static String RandomImage() {

	    Random random = new Random(); 
	    String mFilePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/testphoto/";
	
	
	    
	    switch (random.nextInt(5)) {
	        case 0 : mFilePath += "20130416082218.jpg"   ; break;
	        case 1 : mFilePath += "20130416090542.jpg"   ; break;
	        case 2 : mFilePath += "20130416091146.jpg"   ; break;
	        case 3 : mFilePath += "20130416091546.jpg"   ; break;
	        case 4 : mFilePath += "20130416091738.jpg"   ; break;
	        case 5 : mFilePath += "20130416092721.jpg"   ; break;
	        case 6 : mFilePath += "20130507042349.jpg"   ; break;
	        
	        default: mFilePath += "20130507044407.jpg"   ; break;
	    }
		

		return mFilePath;
	}
	
	
	
	
	
	
	
	public static final String mPerfName = "com.bluetooth.transfertest";
	public static final String log = "com.bluetooth.transfertest";
	
	public static final String uuname = "bluetooth_transfertest_conn";
	public static final UUID uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
	public static BluetoothAdapter mBluetoothAdapter;

	public static int wirte_size = 512;
	public static int read_size = 1024;
	protected static long load_speeds = 200;

	public static final String start_md5 = "<md5>";
	public static final String receive_md5 = "<receive_md5>";
	public static final String send_content = "<send_content>";
	public static final String end_content = "<end_content>";
	public static final String send_end = "<send_end>";
	
	
	
	
	

	
	
	
	
}
