package com.ivanov.tech.session;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SessionFragmentRegister extends SherlockDialogFragment implements OnClickListener {
private static String TAG = SessionFragmentRegister.class.getSimpleName();
    
    Button button_register,button_to_login;
    EditText edittext_name,edittext_email,edittext_password;
    
    
    public static final String registerUrl="http://"+YourDomen+"/v1/register";
    
    Session.Status statuslistener;
    ViewGroup container;
    
    boolean success=false;

    public static SessionFragmentRegister newInstance(Session.Status listener) {
    	SessionFragmentRegister f = new SessionFragmentRegister();
    	f.statuslistener=listener;
    	
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
        view = inflater.inflate(R.layout.fragment_register, container, false);
        
        button_register = (Button) view.findViewById(R.id.fragment_register_button_register);
        button_register.setOnClickListener(this);
        
        button_to_login = (Button) view.findViewById(R.id.fragment_register_button_to_login);
        button_to_login.setOnClickListener(this);
        
        edittext_name=(EditText)view.findViewById(R.id.fragment_register_edittext_name);
        
        edittext_email=(EditText)view.findViewById(R.id.fragment_register_edittext_email);
        
        edittext_password=(EditText)view.findViewById(R.id.fragment_register_edittext_password);

        this.container=container;
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==button_register.getId()){
			
			final String name= edittext_name.getText().toString();
			final String email= edittext_email.getText().toString();
			final String password= edittext_password.getText().toString();
			
			doRegisterRequest(getActivity().getApplicationContext(),name,email,password);
		}
		
		if(v.getId()==button_to_login.getId()){
			Session.createSessionLoginFragment(getActivity(), getFragmentManager(), container.getId(), statuslistener);
			
		}
			
	}
	
	
	
	void doRegisterRequest(Context context,final String name,final String email,final String password) {

    	final String tag = TAG+" doRegisterRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	StringRequest request = new StringRequest(Method.POST,
    			registerUrl,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, "onResponse doRegisterRequest " + response);
    	                        
    	                        JSONObject json;
    	                        
    	                        try{
    	                        	json=new JSONObject(response);
    	                        
    	                        	if(!json.isNull("success")){	    	                        	
    	                        		if(json.getInt("success")==1){
    	                        			
    	                        			SessionFragmentLogin f=Session.createSessionLoginFragment(getActivity(), getFragmentManager(), container.getId(), statuslistener);
    	                        			
    	                        			Bundle arguments=new Bundle();
    	                        			arguments.putString("email", email);
    	                        			arguments.putString("password", password);
    	                        			
    	                        			f.setArguments(arguments);
    	                        			
    	                        		}else throw(new JSONException("success==0"));
	    	                        }
	    	                        
    	                        }catch(JSONException e){
    	                        	Log.e(TAG,"doRegisterRequest " +e.toString());
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
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                
                return params;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
	
}
