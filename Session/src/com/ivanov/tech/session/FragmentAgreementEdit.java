package com.ivanov.tech.session;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
import com.ivanov.tech.multipletypesadapter.cursoradapter_recyclerview.CursorItemHolder;
import com.ivanov.tech.multipletypesadapter.cursoradapter_recyclerview.CursorMultipleTypesAdapter;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.session.Session.CheckInternetListener;
import com.ivanov.tech.session.Session.CloseListener;
import com.ivanov.tech.session.Session.DialogRequestListener;
import com.ivanov.tech.session.Session.RequestListener;
import com.ivanov.tech.session.adapter.ItemHolderText;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentAgreementEdit extends DialogFragment implements OnClickListener {

    public static final String TAG = FragmentAgreementEdit.class
            .getSimpleName();    
	
    Button button_send, button_cancel;
    
    EditText edittext_surname,edittext_name, edittext_patronim, edittext_email, edittext_phone, edittext_password;
    
    View layout_phone;
    
    JSONObject json_data;
    
    public static FragmentAgreementEdit newInstance() {
    	FragmentAgreementEdit f = new FragmentAgreementEdit();
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        Log.d(TAG, "onStart");  
        
        refreshData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_agreement_edit, container, false);
        
        Log.d(TAG, "onCreateView");
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().show();
				
		((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name); 
		
		button_send=(Button)view.findViewById(R.id.fragment_agreement_edit_button_send);
		button_send.setOnClickListener(this);
		
		button_cancel=(Button)view.findViewById(R.id.fragment_agreement_edit_button_cancel);
		button_cancel.setOnClickListener(this);
		
		edittext_surname=(EditText)view.findViewById(R.id.fragment_agreement_edit_edittext_surname);
		edittext_name=(EditText)view.findViewById(R.id.fragment_agreement_edit_edittext_name);
		edittext_patronim=(EditText)view.findViewById(R.id.fragment_agreement_edit_edittext_patr);
		
		edittext_email=(EditText)view.findViewById(R.id.fragment_agreement_edit_edittext_email);
		edittext_phone=(EditText)view.findViewById(R.id.fragment_agreement_edit_edittext_phone);
		
		layout_phone = view.findViewById(R.id.fragment_agreement_edit_layout_phone);
		
		edittext_password=(EditText)view.findViewById(R.id.fragment_agreement_edit_edittext_password);
		
        return view;
    }
    
    @Override
	public void onClick(View v) {
    	
    	Log.d(TAG, "onClick");
    	
    	if(v.getId()==button_send.getId()){
    		
    		if( !(checkChanges()&&checkPassword()) )return;
			
    		Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new Connection.ProtocolListener(){

				@Override
				public void isCompleted() {					
					
					Session.doChangeRegDataRequest(getActivity(), getFragmentManager(), R.id.main_container, getData(), new RequestListener(){

						@Override
						public void onResponsed() {
							//Parse response
							getFragmentManager().popBackStack();
						}
						
					});
				}

				@Override
				public void onCanceled() {}
    			
    		});
    		
    		return;
    	}
    	
    	if(v.getId()==button_cancel.getId()){
    		getFragmentManager().popBackStack();
    		return;
    	}
    	
	}

    HashMap<String, String> getData(){
    	HashMap<String, String> params = new HashMap<String, String>();
    	
    	processData("surname",edittext_surname.getText().toString(),params);
    	processData("name",edittext_name.getText().toString(),params);
    	processData("patronim",edittext_patronim.getText().toString(),params);
    	
    	processData("email",edittext_email.getText().toString(),params);
    	processData("phone", edittext_phone.getText().toString(),params);
    	
    	params.put("newPassword", "");
    	params.put("passwordRepeat", "");
    	
    	params.put("verifiedPassword", edittext_password.getText().toString());
    	
    	return params;
    }
    
    void processData(String key,String value,HashMap<String, String> params){
    	try{
    		boolean result=json_data.has(key)&&( !json_data.getString(key).equals(value) );
    		
    		if(result){
    			params.put(key, value);
    		}else{
    			params.put(key, "");
    		}
    		
    	}catch(JSONException e){
    		Log.e(TAG, "JSONException e="+e);
    	}
    }
    
    boolean bindData(String key,EditText edittext,JSONObject json_data){
    	try{
    		edittext.setText(json_data.getString(key));
    		return true;
    	}catch(JSONException e){
    		Log.e(TAG, "JSONException e="+e);
    		return false;
    	}
    }

    void refreshData(){
    	if(Session.getDataJson()==null){
        	Session.popFullBackStack(getFragmentManager());
        	return;
        }
        
        try {
			json_data=new JSONObject(Session.getDataJson());
			
			bindData("surname",edittext_surname,json_data);
			bindData("name",edittext_name,json_data);
			bindData("patronim",edittext_patronim,json_data);
			
			bindData("email",edittext_email,json_data);
			if(bindData("phone",edittext_phone,json_data)==false){
				layout_phone.setVisibility(View.GONE);
			}else{
				layout_phone.setVisibility(View.VISIBLE);
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "refreshData JSONException e="+e);
			
			Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 472, R.string.error_472_title, R.string.error_472_message, new CloseListener(){

				@Override
				public void onClosed() {
					getFragmentManager().popBackStack();
				}
				
			});
		}
    }
    
    boolean checkChanges(){
    	if(getData().size()==0){
    		Toast.makeText(getActivity(), "Изменений не обнаружено", Toast.LENGTH_LONG).show();
    	}
    	return getData().size()>0;
    }
    
    boolean checkPassword(){
    	if(!getData().containsKey("verifiedPassword")){
    		Toast.makeText(getActivity(), "Вы должны ввести пароль", Toast.LENGTH_LONG).show();
    	}
    	
    	return getData().containsKey("verifiedPassword");
    }
}
