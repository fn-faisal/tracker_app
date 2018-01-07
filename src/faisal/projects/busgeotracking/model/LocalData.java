package faisal.projects.busgeotracking.model;


import android.content.Context;
import android.content.SharedPreferences;
import faisal.projects.busgeotracking.R;

public class LocalData {

	private static Context context;
	private static SharedPreferences sp;
	
	public static boolean checkKey(Context con, String keyName, String keyValue){
		
		if(sp == null)
		{
			context = con;
			sp = context.getSharedPreferences(context.getString(R.string.app_id), Context.MODE_PRIVATE);
		}
		
		if(!sp.contains(keyName)) return false;
		
		String storedValue = sp.getString(keyName, null);
		
		if(storedValue == null) return false;
		
		if(storedValue.equals(""))	return false;
		
		if(storedValue.equals(keyValue))	return true;
		
		return false;
	}
	
	public static String getKey(Context con, String keyName){
		String keyValue = null;
		
		if(sp == null)
		{
			context = con;
			sp = context.getSharedPreferences(context.getString(R.string.app_id), Context.MODE_PRIVATE);
		}
		if(sp.contains(keyName))
			keyValue = sp.getString(keyName, null);
		
		return keyValue;
	}
	
	public static boolean removeKey(Context con, String key){
		if(sp == null)
		{
			context = con;
			sp = context.getSharedPreferences(context.getString(R.string.app_id), Context.MODE_PRIVATE);
		}
		
		if(keyExists(context, key))
		{
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
		}
		else return false;
		return true;
	}
	
	public static boolean keyExists(Context con,String key){
		
		if(sp == null)
		{
			context = con;
			sp = context.getSharedPreferences(context.getString(R.string.app_id), Context.MODE_PRIVATE);
		}
		
		if(sp.contains(key)) return true;
		
		return false;
	}
	
	public static void setKey(Context con, String keyName, String keyValue){
		
		if(sp == null)
		{
			context = con;
			sp = context.getSharedPreferences(context.getString(R.string.app_id), Context.MODE_PRIVATE);
		}
		
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(keyName, keyValue);
		editor.commit();
	}
	
}
