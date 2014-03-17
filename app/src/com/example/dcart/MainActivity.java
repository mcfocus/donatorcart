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
	private TextView formatTxt;
	private TextView contentTxt;
	private TextView messageTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		messageTxt = (TextView)findViewById(R.id.scan_message); 
		
		scanBtn.setOnClickListener(this);
		

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
}
