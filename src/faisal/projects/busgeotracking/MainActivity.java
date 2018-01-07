package faisal.projects.busgeotracking;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import faisal.projects.busgeotracking.model.FirebaseAccess;
import faisal.projects.busgeotracking.model.LocalData;

public class MainActivity extends Activity {

	private TextView log;
	private Button btn;
	private EditText bussid;
	
	private Handler handler2 = new Handler() {
	    @Override
	    public void handleMessage(Message msg) 
	    {
	    	Bundle bundle = msg.getData();
	    	
	    	String CloudLog = bundle.getString("CloudLog");
	    	String busExists = bundle.getString("BUS_EXISTS");
	    	
	    	if (CloudLog != null) {
	    		if(CloudLog.equals("OK"))
	    		{
	    			String key = bundle.getString("fbkey");
	    			LocalData.setKey(MainActivity.this, "FirebaseAppId", key);
	    			if(LocalData.keyExists(MainActivity.this, "FirebaseAppId"))
	    				Toast.makeText(MainActivity.this, "Bus Id saved successfully!", Toast.LENGTH_LONG).show();
	    			else
	    				Toast.makeText(MainActivity.this, "Error saving bus!", Toast.LENGTH_LONG).show();
	    			
	    			FirebaseAccess.busHasLocation(key, handler2);
	    			
	    			Intent intent = new Intent(MainActivity.this, Home.class);
	    			startActivity(intent);
	    		}
	    		else
	    			Toast.makeText(MainActivity.this,CloudLog, Toast.LENGTH_SHORT).show();			
	    	}
	    	if(busExists != null)
	    	{
	    		if(busExists.equals("true")){
	    		}
	    		else if(busExists.equals("false")){
	    			String key = bundle.getString("BUS_KEY"); 
	    			FirebaseAccess.saveBusLocation(key);
	    		}
	    		else{
	    			
	    			Toast.makeText(MainActivity.this, bundle.getString("BUS_EXISTS"), Toast.LENGTH_SHORT).show();			
	    	    }
	    	}
	    	
	    }
	};
	private void initComponents(){
		
		log = (TextView) findViewById(R.id.main_log);
		btn = (Button) findViewById(R.id.main_bussid_btn);
		bussid = (EditText) findViewById(R.id.main_bussid_box);
	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initComponents();
		
		FirebaseAccess.initFirebase(this);
		
		if(LocalData.keyExists(this, "FirebaseAppId"))
		{
			Intent intent = new Intent(MainActivity.this,Home.class);
			startActivity(intent);
		}
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!bussid.getText().toString().equals(""))
				{
					FirebaseAccess.validateBus( bussid.getText().toString(), handler2 );				
					FirebaseAccess.busHasLocation(bussid.getText().toString(), handler2);
				}
			}
		});
	}
	
	
	
}
