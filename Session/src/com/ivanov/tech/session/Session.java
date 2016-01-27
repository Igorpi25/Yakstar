package com.ivanov.tech.session;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.connection.FragmentNoConnection;
import com.ivanov.tech.connection.FragmentNoServerResponding;
import com.ivanov.tech.connection.Connection.ProtocolListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class Session {
	
    private static String TAG = Session.class.getSimpleName();
     
    private static final String PREF = "Session";
    
    public static final String PREF_API_KEY="PREF_API_KEY";
    public static final String PREF_API_KEY_DEFAULT=" ";
    
    public static final String PREF_USER_ID="PREF_USER_ID";
    public static final int PREF_USER_ID_DEFAULT=0;

        
    public final static String testApiKeyUrl = "http://"+YourDomenOrIp+"/igorserver/v1/testapikey";
    public static final String loginUrl="http://"+YourDomenOrIp+"/igorserver/v1/login";
    public static final String registerUrl="http://"+YourDomenOrIp+"/igorserver/v1/register";


    static private SharedPreferences preferences=null;
    
    public static void Initialize(Context context){
    	if(preferences==null){
    		preferences=context.getApplicationContext().getSharedPreferences(PREF, 0);
    	}
    }
 
    public static String getApiKey(){		
  		return preferences.getString(Session.PREF_API_KEY, Session.PREF_API_KEY_DEFAULT);
  	}
  	
  	public static void setApiKey(String apikey){
  		if(!getApiKey().equals(apikey)){
  			preferences.edit().putString(Session.PREF_API_KEY, apikey).commit();
  		}
  	}
  	
  	public static int getUserId(){		
  		return preferences.getInt(Session.PREF_USER_ID, Session.PREF_USER_ID_DEFAULT);
  	}
    
    public static void setUserId(int user_id){
  		if(getUserId()!=user_id){
  			preferences.edit().putInt(Session.PREF_USER_ID, user_id).commit();
  		}
  	}
  	
  	public static void removeApiKey(){
  		
  		preferences.edit().remove(Session.PREF_API_KEY).commit();
  		preferences.edit().remove(Session.PREF_USER_ID).commit();
  	}
  	
  	public static boolean isApiKeyExist(){
  		return preferences.contains(Session.PREF_API_KEY);
  	}
  	
  	public static void checkApiKey(final Context context, final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener){
  					
  		
  		Connection.protocolConnection(context, fragmentManager, container, new Connection.ProtocolListener() {
			
			@Override
			public void onCanceled() {
				
			}
			
			@Override
			public void isCompleted() {
								
				if(preferences.contains(PREF_API_KEY)) { 		
		  			doCheckApiKeyRequest(context,fragmentManager,container,protocolListener);
		  			return;		  			
		  		}else{
		  			createSessionRegisterFragment(context,fragmentManager,container,protocolListener); 		  			
		  		}
			}	
		});
  		
  		 		
  	}
  	
  	public static boolean doCheckApiKeyRequest(final Context context, final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener) {

    	final String tag = TAG+" doTestApiKeyRequest ";  
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Login ...");
    	pDialog.setCancelable(false);
    	
    	pDialog.show();
    	
    	StringRequest request = new StringRequest(Method.POST,
    			testApiKeyUrl,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        
    	                        JSONObject json;
    	                        
    	                        try{
    	                        	json=new JSONObject(response);
    	                        
    	                        	if(!json.isNull("success")){	    	                        	
    	                        		if(json.getInt("success")==1)protocolListener.isCompleted();	
    	                        		else createSessionLoginFragment(context,fragmentManager,container,protocolListener);
	    	                        }else throw (new JSONException("success=null"));
	    	                        
    	                        }catch(JSONException e){
    	                        	Log.e(TAG,tag+"onResponse JSONException "+e.toString());
    	                        	createSessionLoginFragment(context,fragmentManager,container,protocolListener);
    	                        }finally{
    	                        	pDialog.hide();
    	                        }
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                    	Log.e(TAG,tag+"onErrorResponse "+error.toString());
    	                        pDialog.hide();
    	                        createSessionLoginFragment(context,fragmentManager,container,protocolListener);
    	                    }
    	                }){
    		
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Api-Key", Session.getApiKey());
                
                return headers;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

        return false;
    }
  	
  	public static FragmentLogin createSessionLoginFragment(final Context context,final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("SessionLogin").isVisible()){
                return (FragmentLogin)fragmentManager.findFragmentByTag("SessionLogin");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

            FragmentLogin sessionloginfragment = FragmentLogin.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, sessionloginfragment, "SessionLogin");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fragmentTransaction.addToBackStack("SessionLogin");
            fragmentTransaction.commit();
            
            return sessionloginfragment;
        }
    }
  	
  	public static FragmentRegister createSessionRegisterFragment(final Context context,final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("SessionRegister").isVisible()){
                return (FragmentRegister)fragmentManager.findFragmentByTag("SessionRegister");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

            FragmentRegister sessionloginfragment = FragmentRegister.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, sessionloginfragment, "SessionRegister");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fragmentTransaction.addToBackStack("SessionRegister");
            fragmentTransaction.commit();
            
            return sessionloginfragment;
        }
    }
  	  	
}
