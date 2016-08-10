package com.ivanov.tech.session.demo;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.session.Session.CheckInternetListener;
import com.ivanov.tech.session.Session.LogoutListener;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentDemo extends DialogFragment implements OnClickListener ,CheckInternetListener{

    public static final String TAG = FragmentDemo.class
            .getSimpleName();    
	
    TextView 
    	textview_login_value,
    	textview_service_status_disabled,textview_service_status_active,textview_service_status_wait,
    	textview_internet_on,textview_internet_off;
    
    //ProgressBar progressbar_wait;
    
    View layout_internet;
    
    Button button_signout,button_connect,button_disconnect;

    public static FragmentDemo newInstance() {
    	FragmentDemo f = new FragmentDemo();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_demo, container, false);
                
        button_signout = (Button) view.findViewById(R.id.fragment_demo_button_signout);
        button_signout.setOnClickListener(this);
        
        button_connect = (Button) view.findViewById(R.id.fragment_demo_button_connect);
        button_connect.setOnClickListener(this);
        
        button_disconnect = (Button) view.findViewById(R.id.fragment_demo_button_disconnect);
        button_disconnect.setOnClickListener(this);
       
        textview_login_value = (TextView) view.findViewById(R.id.fragment_demo_textview_login_value);
        textview_login_value.setText(Session.getUserLogin());
        
        textview_service_status_active = (TextView) view.findViewById(R.id.fragment_demo_textview_service_status_active);
        textview_service_status_disabled = (TextView) view.findViewById(R.id.fragment_demo_textview_service_status_disabled);
        textview_service_status_wait = (TextView) view.findViewById(R.id.fragment_demo_textview_service_status_wait);
        
        textview_internet_on = (TextView) view.findViewById(R.id.fragment_demo_textview_internet_on);
        textview_internet_off = (TextView) view.findViewById(R.id.fragment_demo_textview_internet_off);
               
//        progressbar_wait = (ProgressBar) view.findViewById(R.id.fragment_demo_progressbar_wait);
        layout_internet = view.findViewById(R.id.fragment_demo_layout_internet);
        
        showWait();              
        
        
        Session.doCheckInternetRequest(getActivity(), this);
                
        return view;
    }

    @Override
	public void onClick(View v) {
		textview_login_value.setText(Session.getUserLogin());
		Log.d(TAG, "onClick logout");
		if (v.getId()==button_signout.getId()){
			
			Session.Logout(getActivity(), getFragmentManager(), R.id.main_container);
			
		}
		
		if ( (v.getId()==button_connect.getId())||(v.getId()==button_disconnect.getId()) ){
			Session.doSwitchInternetRequest(getActivity(),this);
		}
		
	}
        
    void showWait(){
    	//progressbar_wait.setVisibility(View.VISIBLE);
    	layout_internet.setVisibility(View.GONE);
    	
    	textview_service_status_active.setVisibility(View.GONE);
    	textview_service_status_disabled.setVisibility(View.GONE);
    	textview_service_status_wait.setVisibility(View.VISIBLE);
    }
    
    void showActive(){
    	//progressbar_wait.setVisibility(View.GONE);
    	layout_internet.setVisibility(View.VISIBLE);
    	
    	textview_service_status_active.setVisibility(View.VISIBLE);
    	textview_service_status_disabled.setVisibility(View.GONE);
    	textview_service_status_wait.setVisibility(View.GONE);
    	
    }
    
    void showDisabled(){
    	//progressbar_wait.setVisibility(View.GONE);
    	layout_internet.setVisibility(View.GONE);
    	
    	textview_service_status_active.setVisibility(View.GONE);
    	textview_service_status_disabled.setVisibility(View.VISIBLE);
    	textview_service_status_wait.setVisibility(View.GONE);
    	
    }
    
    void showInternetOn(){
    	textview_internet_on.setVisibility(View.VISIBLE);
    	textview_internet_off.setVisibility(View.GONE);
    	
    	button_connect.setVisibility(View.GONE);
    	button_disconnect.setVisibility(View.VISIBLE);
    }
    
    void showInternetOff(){
    	textview_internet_on.setVisibility(View.GONE);
    	textview_internet_off.setVisibility(View.VISIBLE);
    	
    	button_connect.setVisibility(View.VISIBLE);
    	button_disconnect.setVisibility(View.GONE);
    }
	
    //-----------------CheckInternetListener-----------------------------
    
    @Override
	public void isOnline() {
		showInternetOn();
		showActive();
	}

	@Override
	public void isOffline() {
		showInternetOff();
		showActive();
	}
	
}
