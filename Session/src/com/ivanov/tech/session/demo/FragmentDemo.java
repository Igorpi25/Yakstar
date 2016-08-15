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

import org.json.JSONException;
import org.json.JSONObject;

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
import com.ivanov.tech.session.Session.RequestListener;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentDemo extends DialogFragment implements OnClickListener ,CheckInternetListener{

    public static final String TAG = FragmentDemo.class
            .getSimpleName();    
	
    TextView textview_login_value,
    	textview_service_status_disabled,textview_service_status_active,
    	textview_internet_on,textview_internet_off,
    	textview_balance_value,textview_agriment,textview_clientname,textview_tariffname,textview_topay,textview_levelname,textview_debet;
    
    
    View layout_internet,layout_info;
    
    Button button_signout,button_connect,button_disconnect,button_balance_charge;

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
        
        Session.doInfoRequest(getActivity(), new RequestListener(){

			@Override
			public void onResponsed() {

		    	if(!Session.isInfoJsonExists())Toast.makeText(getActivity(), "Ошибка! Данные пользователя пусты", Toast.LENGTH_LONG).show();
		    	
		    	try {
		    		
		    		JSONObject json_info=new JSONObject(Session.getInfoJson());
		    	
		    		String servicestatusdata=json_info.getJSONObject("serviceStatus").getString("data");
		    		
					if(servicestatusdata.equals("Активна")){
						showActive();				
						updateInfo(json_info);
					}else if(servicestatusdata.equals("Заблокирована")){
						
						showDisabled();
						updateInfo(json_info);
					}else{
						Log.e(TAG, "onStart doInfoRequest onResponsed serviceStatus.data unknown value="+servicestatusdata);
						Toast.makeText(getActivity(), "Ошибка! Неизвестный статус услуги: "+servicestatusdata, Toast.LENGTH_LONG).show();
						showDisabled();
						cleanInfo();
					}
					
				} catch (JSONException e) {
					Log.e(TAG, "onStart doInfoRequest onResponsed JSONException e="+e);
				}
			}
			
        	
        });
    
        Session.doCheckInternetRequest(getActivity(), this);
        
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
        
        button_balance_charge = (Button) view.findViewById(R.id.fragment_demo_button_balance_charge);
        button_balance_charge.setOnClickListener(this);
        
        textview_login_value = (TextView) view.findViewById(R.id.fragment_demo_textview_login_value);
        textview_login_value.setText(Session.getUserLogin());
        
        textview_service_status_active = (TextView) view.findViewById(R.id.fragment_demo_textview_service_status_active);
        textview_service_status_disabled = (TextView) view.findViewById(R.id.fragment_demo_textview_service_status_disabled);
        
        textview_internet_on = (TextView) view.findViewById(R.id.fragment_demo_textview_internet_on);
        textview_internet_off = (TextView) view.findViewById(R.id.fragment_demo_textview_internet_off);
                
//        progressbar_wait = (ProgressBar) view.findViewById(R.id.fragment_demo_progressbar_wait);
        layout_internet = view.findViewById(R.id.fragment_demo_layout_internet);
        
        layout_info = view.findViewById(R.id.fragment_demo_layout_info);
        
        textview_balance_value=(TextView) view.findViewById(R.id.fragment_demo_textview_balance_value);
        textview_agriment=(TextView) view.findViewById(R.id.fragment_demo_textview_agriment);
        textview_clientname=(TextView) view.findViewById(R.id.fragment_demo_textview_clientname);
        textview_tariffname=(TextView) view.findViewById(R.id.fragment_demo_textview_tariffname);
        textview_topay=(TextView) view.findViewById(R.id.fragment_demo_textview_topay);
        textview_levelname=(TextView) view.findViewById(R.id.fragment_demo_textview_levelname);
        textview_debet=(TextView) view.findViewById(R.id.fragment_demo_textview_debet);
        
        
        showDisabled();
            
        return view;
    }

    @Override
	public void onClick(View v) {
		textview_login_value.setText(Session.getUserLogin());
		Log.d(TAG, "onClick logout");
		if (v.getId()==button_signout.getId()){
			
			Session.Logout(getActivity(), getFragmentManager(), R.id.main_container);
			return;
		}
		
		if (v.getId()==button_balance_charge.getId()){
			
			Session.createPaymentCardactFragment(getActivity(), getFragmentManager(), R.id.main_container);
			return;
		}
		
		if ( (v.getId()==button_connect.getId())||(v.getId()==button_disconnect.getId()) ){
			Session.doSwitchInternetRequest(getActivity(),this);
			return;
		}
		
		
		
	}
        
    void showActive(){
    	layout_internet.setVisibility(View.VISIBLE);
    	
    	textview_service_status_active.setVisibility(View.VISIBLE);
    	textview_service_status_disabled.setVisibility(View.GONE);
    	
    }
    
    void showDisabled(){
    	layout_internet.setVisibility(View.GONE);
    	
    	textview_service_status_active.setVisibility(View.GONE);
    	textview_service_status_disabled.setVisibility(View.VISIBLE);
    	
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
	
    void updateInfo(JSONObject json){ 
    	
    	try {
    		layout_info.setVisibility(View.VISIBLE);	
    		
			textview_balance_value.setText(json.getJSONObject("balance").getString("data"));			
			textview_clientname.setText(json.getJSONObject("clientName").getString("data"));			
			textview_agriment.setText("Договор : "+json.getJSONObject("agreement").getString("data"));
			
			textview_tariffname.setText(getInfoString(json,"tariffName"));
			textview_topay.setText(getInfoString(json,"toPay")+ " руб");
			textview_levelname.setText(getInfoString(json,"levelname"));
			textview_debet.setText(getInfoString(json,"debet")+ " руб");
						
		} catch (JSONException e) {e.printStackTrace();}
    }
    
    void cleanInfo(){ 
    	
    	layout_info.setVisibility(View.INVISIBLE);	
    }
    
    String getInfoString(JSONObject json_info, String name) throws JSONException{
    	
    	JSONObject object=json_info.getJSONObject(name);
    	
    	String title=object.getString("title");
    	String data=object.getString("data");
    	
    	return title+" : "+data;
    }
    
    //-----------------CheckInternetListener-----------------------------
    
    @Override
	public void isOnline() {
		showInternetOn();
	}

	@Override
	public void isOffline() {
		showInternetOff();
	}
	
}
