/**
 * 
 */
package faisal.projects.busgeotracking;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import faisal.projects.busgeotracking.model.FirebaseAccess;
import faisal.projects.busgeotracking.model.LocalData;

/**
 * @author Faisal
 *
 */
public class Home extends Activity {
	
	private Spinner from;
	private Spinner to;
	private Button go;
	private TextView title;
	
	private LocationManager lm;
	private LocationListener ll;
	
	private Handler handler2 = new Handler() {
	    @Override
	    public void handleMessage(Message msg) 
	    {
	    	Bundle bundle = msg.getData();
	    	String busTerminals = bundle.getString("BUS_TERMINALS");
	    	String exception = bundle.getString("CLOUD_EXCEPTION");
	    	
	    	if(busTerminals != null)
	    	{
	    		if(busTerminals.equals("TRUE")){
	    			String temp = "";
	    			List<String> tlist = bundle.getStringArrayList("TERMINAL_LIST");
	    			for(String terminal : tlist){
	    				temp += terminal+"\n";
	    			}
	    			ArrayAdapter<String> list = new ArrayAdapter<String>(Home.this, android.R.layout.simple_spinner_item ,tlist);
	    			from.setAdapter(list);
	    			to.setAdapter(list);
	    		}
	    		else
	    			Toast.makeText(Home.this, bundle.getString("BUS_TERMINALS"), Toast.LENGTH_SHORT).show();
	    	}
	    	if(exception != null)
	    	{
	    		Toast.makeText(Home.this, "Error : "+exception, Toast.LENGTH_SHORT).show();
	    	}
	    }
	};
	
	private void attachListeners(){
		
		go.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(from.getSelectedItem().toString().equals(to.getSelectedItem().toString()))
					Toast.makeText(Home.this , "Source and Destination can't be the same!" , Toast.LENGTH_SHORT).show();
				else {
					lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
					ll = new BusLocationListener();
					lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
				}
			}
		});
		
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		from = (Spinner) findViewById(R.id.busapp_from_box);
		to = (Spinner) findViewById(R.id.busapp_to_box);
		go = (Button) findViewById(R.id.busapp_go_btn);
		
		attachListeners();
		
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		title = (TextView) findViewById(R.id.busapp_title);
		title.setText("App ID : "+LocalData.getKey(Home.this, "FirebaseAppId"));
		
		FirebaseAccess.getTerminals(handler2);
	}

	
	private class BusLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			FirebaseAccess.updateBusLocaiton(
					 LocalData.getKey(Home.this,"FirebaseAppId")
					,from.getSelectedItem().toString()
					,to.getSelectedItem().toString()
					,Double.toString(location.getLatitude())
					,Double.toString(location.getLongitude())
					,handler2);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
		
	}
	
}
