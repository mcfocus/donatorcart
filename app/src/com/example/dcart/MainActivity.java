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


public class MainActivity extends Activity implements OnClickListener {

	private Button scanBtn;
	private TextView formatTxt, contentTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		
		scanBtn.setOnClickListener(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		}
		else {
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Item not found in our database :(", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
