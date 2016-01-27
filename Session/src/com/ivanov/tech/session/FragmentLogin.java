package com.ivanov.tech.session;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.connection.Connection.ProtocolListener;

public class FragmentLogin extends SherlockDialogFragment implements OnClickListener {


    private static String TAG = FragmentLogin.class.getSimpleName();
    
    Button button_login,button_to_register;
    EditText edittext_email,edittext_password;
    
    
    
    
    Connection.ProtocolListener protocollistener;
    ViewGroup container;
    

    public static FragmentLogin newInstance(Connection.ProtocolListener listener) {
    	FragmentLogin f = new FragmentLogin();
    	f.protocollistener=listener;
    	
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    
    @Override
    public void onDetach(){
    	super.onDetach();
    	
    		
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_login, container, false);
        
        button_login = (Button) view.findViewById(R.id.fragment_login_button_login);
        button_login.setOnClickListener(this);
        
        button_to_register = (Button) view.findViewById(R.id.fragment_login_button_to_register);
        button_to_register.setOnClickListener(this);
        
        edittext_email=(EditText)view.findViewById(R.id.fragment_login_edittext_email);
        
        edittext_password=(EditText)view.findViewById(R.id.fragment_login_edittext_password);

        
        if((getArguments()!=null)&&(getArguments().containsKey("email"))){
        	edittext_email.setText(getArguments().getString("email"));
        }
        
        if((getArguments()!=null)&&(getArguments().containsKey("password"))){
        	edittext_password.setText(getArguments().getString("password"));
        }
        
        this.container=container;
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==button_login.getId()){
			
			final String email= edittext_email.getText().toString();
			final String password= edittext_password.getText().toString();
			
			Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new ProtocolListener(){

				@Override
				public void isCompleted() {
					doLoginRequest(getActivity().getApplicationContext(),email,password);
				}

				@Override
				public void onCanceled() {
					
				}
				
			});
			
			
		}
		if(v.getId()==button_to_register.getId()){
			
			Session.createSessionRegisterFragment(getActivity(), getFragmentManager(), container.getId(), protocollistener);
			
		}
	}
	
	
	
	void doLoginRequest(Context context,final String email,final String password) {

    	String tag = TAG+" doLoginRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	StringRequest request = new StringRequest(Method.POST,
    			Session.loginUrl,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, "onResponse" + response);
    	                        
    	                        JSONObject json;
    	                        
    	                        try{
    	                        	json=new JSONObject(response);
    	                        
    	                        	if(!json.isNull("apiKey")){	    	                        	
    	                        		
    	                        		Session.setApiKey(json.getString("apiKey"));
    	                        		getFragmentManager().beginTransaction().remove(FragmentLogin.this).commit();
    	                        		protocollistener.isCompleted();
	    	                        }
    	                        	
    	                        	if(!json.isNull("user_id")){	    	                        	
    	                        		
    	                        		Session.setUserId(json.getInt("user_id"));
    	                        		getFragmentManager().beginTransaction().remove(FragmentLogin.this).commit();
    	                        		protocollistener.isCompleted();
	    	                        }
	    	                        
    	                        }catch(JSONException e){
    	                        	Log.e(TAG,"doLoginRequest "+e.toString());
    	                        }
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "Volley.onErrorResponser: " + error.getMessage());
    	                        
    	                    }
    	                }){
    		
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                
                return headers;
            }
    		
    		@Override
            protected Map<String, String> getParams() {
    			
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                
                return params;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
	

}