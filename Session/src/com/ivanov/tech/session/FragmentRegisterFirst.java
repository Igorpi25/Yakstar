package com.ivanov.tech.session;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentRegisterFirst extends DialogFragment implements OnClickListener {
private static String TAG = FragmentRegisterFirst.class.getSimpleName();
    
    Button button_next,button_cancel;
    
    EditText 
    	edittext_surname,edittext_name,edittext_patr,edittext_,
    	edittext_email,edittext_phone,
    	edittext_passport_num,edittext_passport_who;
    
    CheckAuthorizationListener protocollistener;
    ViewGroup container;
        
    boolean success=false;

    public static FragmentRegisterFirst newInstance(CheckAuthorizationListener listener) {
    	FragmentRegisterFirst f = new FragmentRegisterFirst();
    	f.protocollistener=listener;
    	
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = null;
        view = inflater.inflate(R.layout.fragment_register_first, container, false);
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        
        edittext_surname=(EditText)view.findViewById(R.id.fragment_register_edittext_surname);
        edittext_name=(EditText)view.findViewById(R.id.fragment_register_edittext_name);
        edittext_patr=(EditText)view.findViewById(R.id.fragment_register_edittext_patr);
        
        edittext_email=(EditText)view.findViewById(R.id.fragment_register_edittext_email);        
        edittext_phone=(EditText)view.findViewById(R.id.fragment_register_edittext_phone);
        
        edittext_passport_num=(EditText)view.findViewById(R.id.fragment_register_edittext_passport_num);
        edittext_passport_who=(EditText)view.findViewById(R.id.fragment_register_edittext_passport_who);

        button_next = (Button) view.findViewById(R.id.fragment_register_button_next_second);
        button_next.setOnClickListener(this);
        
        button_cancel = (Button) view.findViewById(R.id.fragment_register_button_cancel);
        button_cancel.setOnClickListener(this);
        
//        //������� ���������(� ��������) � ���� Rules � Agreements, ������� ����������� �� FargmentRegisterLast
//        if(Session.getRulesJson()==null){
//        	Session.doRulesRequest(getActivity(), getFragmentManager(), R.id.main_container, null);
//        }        
//        if(Session.getAgreementJson()==null){
//        	Session.doAgreementRequest(getActivity(), getFragmentManager(), R.id.main_container, null);
//        }
        
        this.container=container;
        
        return view;
    }

    public void onStop(){
    	super.onStop();
    	
    	saveRegisterJson();
    	
    	hideKeyboard();
    }
    
    public void onStart(){
    	super.onStart();   
    	
    	loadRegisterJson();
    	
    	hideKeyboard();
    }
    
	@Override
	public void onClick(View v) {
		
		if(v.getId()==button_next.getId()){
			if(validate()){
				
				Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new Connection.ProtocolListener(){ 
					
					@Override
					public void onCanceled() {
						
					}
					
					@Override
					public void isCompleted() {
						Session.doTarifRequest(getActivity(), getFragmentManager(), R.id.main_container, new RequestListener(){

							@Override
							public void onResponsed() {
								Session.createSessionRegisterSecondFragment(getActivity(), getFragmentManager(), container.getId(), protocollistener);
							}
							
						});
					}
				});
								
			}
		}
		
		if(v.getId()==button_cancel.getId()){
			getFragmentManager().popBackStack();			
		}
			
	}
	
	boolean validate(){
		
		if( edittext_passport_who.getText().toString()==null || (edittext_passport_who.getText().toString().isEmpty()) ){			
			showWarning(R.string.register_check_passport_who);
			return false;
		}
			
		return 
		checkRegex("^[�-�].[�-�]{0,70}$",edittext_surname.getText().toString(),R.string.register_check_surname) &&
		checkRegex("^[�-�].[�-�]{0,70}$",edittext_name.getText().toString(),R.string.register_check_name) &&
		checkRegex("^[�-�].[�-�]{0,70}$",edittext_patr.getText().toString(),R.string.register_check_patr) &&		
		checkRegex("^[0-9]{10}$",edittext_passport_num.getText().toString(),R.string.register_check_passport_num);
		
	}
	
	boolean checkRegex(String regex,String value,int warning){  
        Pattern p = Pattern.compile(regex);  
        Matcher m = p.matcher(value);  
        
        if(!m.matches())showWarning(warning);
        
        return m.matches();  
    }  
	
	void showWarning(int res){	
		Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
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
	
	void saveRegisterJson(){
		
		try {
			JSONObject json;
			
			if(Session.isRegisterJsonExists()){
				json=new JSONObject(Session.getRegisterJson());
			}else{
				json=new JSONObject();
			}
			
			json.put("surname", edittext_surname.getText().toString());
			
			json.put("name", edittext_name.getText().toString());
			json.put("patr", edittext_patr.getText().toString());
			
			json.put("email", edittext_email.getText().toString());
			json.put("phone", edittext_phone.getText().toString());
			
			json.put("passport_num", edittext_passport_num.getText().toString());
			json.put("passport_who", edittext_passport_who.getText().toString());

			Session.setRegisterJson(json.toString());
		
		} catch (JSONException e) {e.printStackTrace();}
				
	}
	
	void loadRegisterJson(){
		if(!Session.isRegisterJsonExists())return;
		
		try {
			JSONObject json=new JSONObject(Session.getRegisterJson());
			
			edittext_surname.setText(json.getString("surname"));
			edittext_name.setText(json.getString("name"));
			edittext_patr.setText(json.getString("patr"));
			
			edittext_email.setText(json.getString("email"));
			edittext_phone.setText(json.getString("phone"));
			
			edittext_passport_num.setText(json.getString("passport_num"));
			edittext_passport_who.setText(json.getString("passport_who"));
			
		} catch (JSONException e) {e.printStackTrace();}
			
		
	}

}
