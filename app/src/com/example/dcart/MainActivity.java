package com.example.dcart;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
  
  
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity implements OnClickListener {

	private Button scanBtn;
	private Button donateBtn;
	private TextView formatTxt;
	private TextView contentTxt;
	private TextView messageTxt;
	private double counter = 0.0;
	private double percent;
	private String dataSent;
	
	private static final String TAG = "bluetooth1";
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	
	// SPP UUID service 
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	  
	// MAC-address of Bluetooth module (arduino)
	private static String address = "00:06:66:01:5E:DB";
	
	// previous scanned barcode
	private String lastBarcode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		scanBtn = (Button)findViewById(R.id.scan_button);
		donateBtn = (Button)findViewById(R.id.donate_button);
		
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		messageTxt = (TextView)findViewById(R.id.scan_message);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	    checkBTState();
		
		scanBtn.setOnClickListener(this);

		
	    donateBtn.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	//counter += 1;
	        	//percent = (counter * 0.1) ;
	        	//dataSent = Double.toString(counter);
	        	if ((lastBarcode.equals("00502610") || lastBarcode.equals("00504485") || lastBarcode.equals("00508476"))) {
	        		sendData("1");
	        		Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
	        	} else if (lastBarcode.equals("026000003049")) {
	        		sendData("0");
	        		Toast.makeText(getBaseContext(), "This item isn't edible!", Toast.LENGTH_LONG).show();
	        	}
	        }});
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	public void onClick(View v) {
		if (v.getId() == R.id.scan_button) {
			//scanning
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}
	}
	
	// Retrieve Scan Results
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null) {
			//show scan results
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			
			// set lastBarcode as the barcode of what was just scanned
			lastBarcode = scanContent;
			
			
			//formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("BARCODE NUMBER: " + scanContent);
			//messageTxt.setText("Hello");
			if (scanContent.equals("00502610")) {
				messageTxt.setText("These Garbanzo beans can feed 2 people. \nPrice: $1.99");
			}
			if (scanContent.equals("00504485")) {
				messageTxt.setText("These oats can supply multiple meals. \nPrice: $3.99");
			}
			if (scanContent.equals("00508476")) {
				messageTxt.setText("This can of black bean soup is a great donation item " +
						"because you can eat it straight out of the can.  \nPrice: $1.99");
			}
			if (scanContent.equals("026000003049")) {
				messageTxt.setText("This item is not edible. But you can " +
						"donate it to the local school supply bin! " +
						"  \nPrice: $1.99");
			}
			

		}
		else {
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Item not found in our database :(", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	// Bluetooth

	  private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
	      if(Build.VERSION.SDK_INT >= 10){
	          try {
	              final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
	              return (BluetoothSocket) m.invoke(device, MY_UUID);
	          } catch (Exception e) {
	              Log.e(TAG, "Could not create Insecure RFComm Connection",e);
	          }
	      }
	      return  device.createRfcommSocketToServiceRecord(MY_UUID);
	  }
	   
	  @Override
	  public void onResume() {
	    super.onResume();
	 
	    Log.d(TAG, "...onResume - try connect...");
	   
	    // Set up a pointer to the remote node using it's address.
	    BluetoothDevice device = btAdapter.getRemoteDevice(address);
	   
	    // Two things are needed to make a connection:
	    //   A MAC address, which we got above.
	    //   A Service ID or UUID.  In this case we are using the
	    //     UUID for SPP.
	   
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e1) {
			errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
		}
	    
	    /*try {
	      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
	    } catch (IOException e) {
	      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
	    }*/
	   
	    // Discovery is resource intensive.  Make sure it isn't going on
	    // when you attempt to connect and pass your message.
	    btAdapter.cancelDiscovery();
	   
	    // Establish the connection.  This will block until it connects.
	    Log.d(TAG, "...Connecting...");
	    try {
	      btSocket.connect();
	      Log.d(TAG, "...Connection ok...");
	    } catch (IOException e) {
	      try {
	        btSocket.close();
	      } catch (IOException e2) {
	        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
	      }
	    }
	     
	    // Create a data stream so we can talk to server.
	    Log.d(TAG, "...Create Socket...");
	 
	    try {
	      outStream = btSocket.getOutputStream();
	    } catch (IOException e) {
	      errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
	    }
	  }
	 
	  @Override
	  public void onPause() {
	    super.onPause();
	 
	    Log.d(TAG, "...In onPause()...");
	 
	    if (outStream != null) {
	      try {
	        outStream.flush();
	      } catch (IOException e) {
	        errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
	      }
	    }
	 
	    try     {
	      btSocket.close();
	    } catch (IOException e2) {
	      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
	    }
	  }
	   
	  private void checkBTState() {
	    // Check for Bluetooth support and then check to make sure it is turned on
	    // Emulator doesn't support Bluetooth and will return null
	    if(btAdapter==null) { 
	      errorExit("Fatal Error", "Bluetooth not supported");
	    } else {
	      if (btAdapter.isEnabled()) {
	        Log.d(TAG, "...Bluetooth ON...");
	      } else {
	        //Prompt user to turn on Bluetooth
	        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableBtIntent, 1);
	      }
	    }
	  }
	 
	  private void errorExit(String title, String message){
	    Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
	    finish();
	  }
	 
	  private void sendData(String message) {
	    byte[] msgBuffer = message.getBytes();
	 
	    Log.d(TAG, "...Send data: " + message + "...");
	 
	    try {
	      outStream.write(msgBuffer);
	    } catch (IOException e) {
	      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
	      if (address.equals("00:00:00:00:00:00")) 
	        msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
	      	msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
	       
	      	errorExit("Fatal Error", msg);       
	    }
	  }
	
	
	
}
