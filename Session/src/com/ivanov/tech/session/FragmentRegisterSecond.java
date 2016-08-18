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

public class FragmentRegisterSecond extends DialogFragment implements OnClickListener {
private static String TAG = FragmentRegisterSecond.class.getSimpleName();
    
    Button button_next,button_back;
    Spinner spinner_tarif;
    TextView textview_tarif_summary;
    
    CheckAuthorizationListener protocollistener;
    ViewGroup container;
        
    boolean success=false;

    public static FragmentRegisterSecond newInstance(CheckAuthorizationListener listener) {
    	FragmentRegisterSecond f = new FragmentRegisterSecond();
    	f.protocollistener=listener;
    	
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_register_second, container, false);
        
        textview_tarif_summary=(TextView)view.findViewById(R.id.fragment_register_textview_tarif_summary);
        
        if(getAdapterStrings()==null){
        	Log.e(TAG, "onCreateView getAdapterStrings()==null");
        	return view;
        }
        
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getAdapterStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         
        spinner_tarif = (Spinner) view.findViewById(R.id.fragment_register_spinner_tarif_list);
        spinner_tarif.setAdapter(adapter);
                
        // устанавливаем обработчик нажатия
        spinner_tarif.setOnItemSelectedListener(new OnItemSelectedListener() {
	      
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view,
		          int position, long id) {			        
        		setSummary(getTarifIdByPosition(position));				
		    }
		      
		    @Override
		    public void onNothingSelected(AdapterView<?> arg0) {
		    	textview_tarif_summary.setText("");
		    }
		      
	    });
                
        button_next = (Button) view.findViewById(R.id.fragment_register_button_next_last);
        button_next.setOnClickListener(this);
        
        button_back = (Button) view.findViewById(R.id.fragment_register_button_back_first);
        button_back.setOnClickListener(this);
        
        this.container=container;
        
        return view;
    }

    public void onStart(){
    	super.onStart();
    	loadRegisterJson();
    	hideKeyboard();
    }
    
    public void onStop(){
    	super.onStop();
    	int position=spinner_tarif.getSelectedItemPosition();
    	saveRegisterJson( getTarifIdByPosition(position) );
    	
    }
    
	@Override
	public void onClick(View v) {
		
		if(v.getId()==button_next.getId()){
			Session.createSessionRegisterLastFragment(getActivity(), getFragmentManager(), container.getId(), protocollistener);
		}
		
		if(v.getId()==button_back.getId()){
			getFragmentManager().popBackStack();			
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

	void saveRegisterJson(int id){
		
		try {
			JSONObject json;
			
			if(Session.isRegisterJsonExists()){
				json=new JSONObject(Session.getRegisterJson());
			}else{
				json=new JSONObject();
			}
			
			json.put("tarif_id", id);
			
			Session.setRegisterJson(json.toString());
			
		} catch (JSONException e) {e.printStackTrace();}
				
	}
	
	void loadRegisterJson(){
		if ( !(Session.isRegisterJsonExists()&&(Session.isTarifsJsonExists())) )return;
		
		try {
			JSONObject json=new JSONObject(Session.getRegisterJson());
			
			int tarif_id=json.getInt("tarif_id");
			
			if(getTarifPositionById(tarif_id)==-1){
				Log.e(TAG, "loadRegisterJson getTarifPositionById("+tarif_id+")==-1");
				throw new JSONException("loadRegisterJson getTarifPositionById("+tarif_id+")==-1");				
			}
			
			spinner_tarif.setSelection(getTarifPositionById(tarif_id));
			setSummary(tarif_id);
			
		} catch (JSONException e) {			
			e.printStackTrace();
			spinner_tarif.setSelection(0);
			setSummary(getTarifIdByPosition(0));
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
	    
		textview_tarif_summary.setText(summary);
	}
}
