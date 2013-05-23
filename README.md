## Android-Bluetooth-Transfer-Test ##

![github](https://raw.github.com/hkmung/Android-Bluetooth-Transfer-Test/master/display.jpg)


This is a android bluetooth transfer test,
Bluetooth server phone continues to send photos 
to clients phone




## Usage ##
Keys.java


		protected static final String Server_Address = "";

Server_Address is a server phone bluetooth address



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


Change your photo 


