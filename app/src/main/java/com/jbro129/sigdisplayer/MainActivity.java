package com.jbro129.sigdisplayer;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
	
	public static String[] digests;
	
	private int initCheck = 0;
	
	private Spinner digestSpinner;
	private EditText signature;
	private EditText hash;
	private Button clearBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		signature = (EditText)findViewById(R.id.signature);
		hash = (EditText)findViewById(R.id.hash);
		digestSpinner = (Spinner)findViewById(R.id.digestType);
		clearBtn = (Button) findViewById(R.id.clear);
		
		digests = getResources().getStringArray(R.array.digestTypes);
		
		digestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				if(++initCheck > 1) // avoid setOnItemSelectedListener calls during initialization
				{
					try {
						MessageDigest messageDigest = MessageDigest.getInstance(digests[position]);
						
						@SuppressLint("PackageManagerGetSignatures")
						Signature[] signatures = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).signatures;
						
						for (Signature sig : signatures)
						{
							
							messageDigest.update(sig.toByteArray());
							
							signature.setText(bytesToHex(messageDigest.digest()));
							hash.setText(String.valueOf(sig.hashCode()));
						}
						
					} catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		});
		
		clearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				digestSpinner.setSelection(0);
				signature.setText("");
				hash.setText("");
			}
		});
	}
	
	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
}