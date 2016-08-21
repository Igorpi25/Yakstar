package com.ivanov.tech.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.connection.Connection.ProtocolListener;
import com.ivanov.tech.session.Session.CheckAuthorizationListener;
import com.ivanov.tech.session.Session.CloseListener;
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentTarifChange extends DialogFragment implements OnClickListener {
private static String TAG = FragmentTarifChange.class.getSimpleName();
    
    Button button_send,button_cancel;
    Spinner spinner;
    TextView textview_summary,textview_status,textview_cancel_link;
    ScrollView scrollview;
    
    ViewGroup container;
        
    public static FragmentTarifChange newInstance() {
    	FragmentTarifChange f = new FragmentTarifChange();
    	
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_tarif_change, container, false);
        
        setHasOptionsMenu(true);		        
        
        textview_summary=(TextView)view.findViewById(R.id.fragment_tarif_change_textview_summary);
        textview_status=(TextView)view.findViewById(R.id.fragment_tarif_change_textview_status);
        
        textview_cancel_link=(TextView)view.findViewById(R.id.fragment_tarif_change_textview_cancel_link);
        textview_cancel_link.setOnClickListener(this);
        
        scrollview=(ScrollView)view.findViewById(R.id.fragment_tarif_change_scrollview);
        
        if(getAdapterStrings()==null){
        	Log.e(TAG, "onCreateView getAdapterStrings()==null");
        	return view;
        }
        
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getAdapterStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         
        spinner = (Spinner) view.findViewById(R.id.fragment_tarif_change_spinner);
        spinner.setAdapter(adapter);
                
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	      
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view,
		          int position, long id) {			        
        		setSummary(getTarifIdByPosition(position));				
		    }
		      
		    @Override
		    public void onNothingSelected(AdapterView<?> arg0) {
		    	textview_summary.setText("");
		    }
		      
	    });
                
        button_send = (Button) view.findViewById(R.id.fragment_tarif_change_button_send);
        button_send.setOnClickListener(this);
        
        button_cancel = (Button) view.findViewById(R.id.fragment_tarif_change_button_cancel);
        button_cancel.setOnClickListener(this);
        
        this.container=container;
        
        return view;
    }

    public void onStart(){
    	super.onStart();
    	((AppCompatActivity)getActivity()).getSupportActionBar().show();
    	
    	refreshChangeTarifStatus();
    	
    	hideKeyboard();
    }
    
    public void onStop(){
    	super.onStop();    	
    }
    
	@Override
	public void onClick(View v) {
		
		if(v.getId()==button_send.getId()){
			Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new Connection.ProtocolListener(){

				@Override
				public void isCompleted() {
					
					int position=spinner.getSelectedItemPosition();
			    	
					Session.doChangeTarifRequest(getActivity(), getFragmentManager(), R.id.main_container, "change", getTarifIdByPosition(position),new RequestListener(){

						@Override
						public void onResponsed() {
							refreshChangeTarifStatus();
						}
						
					});
				}

				@Override
				public void onCanceled() {}
				
			});
		}
		
		if(v.getId()==button_cancel.getId()){
			getFragmentManager().popBackStack();			
		}
		
		if(v.getId()==textview_cancel_link.getId()){
			Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new Connection.ProtocolListener(){

				@Override
				public void isCompleted() {
										
					Session.doChangeTarifRequest(getActivity(), getFragmentManager(), R.id.main_container, "cancel", 0,new RequestListener(){

						@Override
						public void onResponsed() {
							refreshChangeTarifStatus();
						}
						
					});
				}

				@Override
				public void onCanceled() {}
				
			});			
		}
		
	}
	
	void hideKeyboard(){
    	try {
            InputMethodManager input = (InputMethodManager) getActivity()
                    .getSystemService(getActivity().INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

	int getTarifIdByPosition(int position){
		try {
			JSONObject tarifs_json=new JSONObject(Session.getTarifsJson());			
			JSONArray tarifs_json_array=tarifs_json.getJSONArray("optionList");
			
			//Tarif at position
			JSONObject tarif=tarifs_json_array.getJSONObject(position);
			
			return tarif.getInt("id");
			
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		} 
		
	}
	
	int getTarifPositionById(int id){
		try {
			JSONObject json=new JSONObject(Session.getTarifsJson());			
			JSONArray json_array=json.getJSONArray("optionList");
			
			for(int position=0;position<json_array.length();position++){
				JSONObject object=json_array.getJSONObject(position);
				if(object.getInt("id")==id)return position;
			}
			
			return -1;
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		} 
		
	}
	
	JSONObject getTarif(int id){
		try {
			JSONObject json=new JSONObject(Session.getTarifsJson());			
			JSONArray json_array=json.getJSONArray("optionList");
			
			for(int position=0;position<json_array.length();position++){
				JSONObject object=json_array.getJSONObject(position);
				if(object.getInt("id")==id)return object;
			}
			
			return null;
		} catch (JSONException e) {
			Log.e(TAG, "getTarif JSONException e="+e);
			return null;
		} 
		
	}
	
	ArrayList<String> getAdapterStrings(){
		
		try{
		
			JSONObject json=new JSONObject(Session.getTarifsJson());
			
			JSONArray json_array=json.getJSONArray("optionList");
			
			ArrayList<String> strings=new ArrayList<String>();
			
			for(int i=0;i<json_array.length();i++){
				JSONObject tarif=json_array.getJSONObject(i);
				
				String value=tarif.getString("descr")+" ("+tarif.getString("rent")+" руб.)";
				strings.add(value);
			}
			
			return strings;
		
		}catch(JSONException e){
			
			Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 42, R.string.error_42_title, R.string.error_42_message, new CloseListener(){

				@Override
				public void onClosed() {
					Session.killApp();
				}
              	
              });
			
			return null;
		}
	}
	
	void setSummary(int id){
		JSONObject tarif=getTarif(id);
		
		String summary="";
		try {
			summary= tarif.getString("ext_descr");
		} catch (JSONException e) {e.printStackTrace();}    
	    
		textview_summary.setText(summary);
	}
	
	void refreshChangeTarifStatus(){
		
		scrollview.fullScroll(View.FOCUS_UP);
		
		if(Session.getChangeTarifStatusJson()==null){
			
			textview_status.setVisibility(View.GONE);
			textview_cancel_link.setVisibility(View.GONE);
			
			return;
		}
		
		try{
			JSONObject json=new JSONObject(Session.getChangeTarifStatusJson());
			if(json.getBoolean("status")){
				textview_cancel_link.setVisibility(View.VISIBLE);
			}else{
				textview_cancel_link.setVisibility(View.GONE);
			}
			
			textview_status.setVisibility(View.VISIBLE);
			textview_status.setText(json.getString("message"));
		}catch(JSONException e){
			Session.createErrorFragment(getActivity(),getFragmentManager(),R.id.main_container,492,R.string.error_492_title,R.string.error_492_message,null);
		}
	}
}
