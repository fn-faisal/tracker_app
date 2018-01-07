package faisal.projects.busgeotracking.model;

import java.util.ArrayList;
import java.util.List;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import faisal.projects.busgeotracking.Home;

public class FirebaseAccess {

	private static Firebase fbase;
	
	public static void initFirebase(Context context){
		Firebase.setAndroidContext(context);
		fbase = new Firebase("http://bustracking-busloc.firebaseIO.com");
	}
	
	public static boolean validateBus(
						 final String appId
						,final Handler log ){
		Query q = fbase.child("BusRegister").orderByChild("app_id");
		q.addListenerForSingleValueEvent(new ValueEventListener() {
			
			private Message msg = new Message();
			private Bundle bun = new Bundle();
			
			@Override
			public void onDataChange(DataSnapshot ds) {
				boolean exists = false;
				if(ds.exists())
					for(DataSnapshot tempds : ds.getChildren()){
						if(tempds.getKey().toString().equals(appId)){
							bun.putString("CloudLog", "OK");
							bun.putString("fbkey", appId);
							exists = true;
						}	
					}
				if (!exists) {
					bun.putString("CloudLog", "Invalid App Id");
				}
				
				msg.setData(bun);
				log.sendMessage(msg);
			}
			
			@Override
			public void onCancelled(FirebaseError e) {
				bun.putString("CloudLog", "Error : "+e.getMessage());
				msg.setData(bun);
				log.sendMessage(msg);
			}
		});
		
		return false;
	}
	
	public static void busHasLocation(final String appId
							,final Handler log){
		
		Query q = fbase.child("BusLocation").orderByValue();
		q.addListenerForSingleValueEvent(new ValueEventListener() {
			
			private Message msg = new Message();
			private Bundle bun = new Bundle();
			
			@Override
			public void onDataChange(DataSnapshot data) {
				boolean found = false;
				if(data.exists()){
					if(data.hasChild(appId)){
						bun.putString("BUS_EXISTS", "true");
					}
					else{
						bun.putString("BUS_EXISTS", "false");
						bun.putString("BUS_KEY", appId);
					}
				}
				msg.setData(bun);
				log.sendMessage(msg);
			}
			
			@Override
			public void onCancelled(FirebaseError e) {
				bun.putString("BUS_EXISTS", "Error : "+e.getMessage());
				msg.setData(bun);
				log.sendMessage(msg);
			}
		});
	}
	
	public static void saveBusLocation(String appId){
		fbase.child("BusLocation").child(appId).child("from").setValue("lhr");
		fbase.child("BusLocation").child(appId).child("to").setValue("fsd");
		fbase.child("BusLocation").child(appId).child("longitude").setValue("0");
		fbase.child("BusLocation").child(appId).child("latitude").setValue("0");
		
	}
	
	public static void getTerminals(final Handler log){
		Query q = fbase.child("Terminals").orderByKey();
		q.addListenerForSingleValueEvent(new ValueEventListener() {
			
			private Message msg = new Message();
			private Bundle bun = new Bundle();
			
			@Override
			public void onDataChange(DataSnapshot data) {
				
				List<String> list = new ArrayList<String>();
				
				if(data.exists())
				{
					if(data.hasChildren())
					{
						for(DataSnapshot temp : data.getChildren()){
							list.add(temp.getKey());
						}
						bun.putString("BUS_TERMINALS", "TRUE");
						bun.putStringArrayList("TERMINAL_LIST", (ArrayList<String>) list);
					}
					else
					{
						bun.putString("BUS_TERMINALS", "No Terminals registered");
					}
					msg.setData(bun);
					log.sendMessage(msg);
				}
				
			}
			
			@Override
			public void onCancelled(FirebaseError e) {
				
			}
		});
	}
	
	public static void updateBusLocaiton(
							 final String appId
							,final String from
							,final String to
							,final String lat
							,final String lng
							,Handler log){
		try{
			fbase.child("BusLocation").child(appId).child("from").setValue(from);
			fbase.child("BusLocation").child(appId).child("to").setValue(to);
			fbase.child("BusLocation").child(appId).child("latitude").setValue(lat);
			fbase.child("BusLocation").child(appId).child("longitude").setValue(lng);
		}catch(FirebaseException ex){
			Bundle bun = new Bundle();
			Message msg = new Message();
			
			bun.putString("CLOUD_EXCEPTION", ex.getMessage());
			msg.setData(bun);
			log.sendMessage(msg);
			
		}
		
	}
	
}
