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
public class FragmentRegdata extends DialogFragment implements OnClickListener {

    public static final String TAG = FragmentRegdata.class
            .getSimpleName();    
	
    protected static final int TYPE_TEXT = 0;
    protected static final int TYPE_HEADER = 1;
    
    Button button_edit,button_password_change, button_back;
    
    RecyclerView recyclerview;
    CursorMultipleTypesAdapter adapter;
    
    View layout;
    
    String response_data=null;
    
    public static FragmentRegdata newInstance() {
    	FragmentRegdata f = new FragmentRegdata();
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        Log.d(TAG, "onStart");  
        
        Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new Connection.ProtocolListener(){
        				
			@Override
			public void onCanceled() {}
			
			@Override
			public void isCompleted() {
				Session.doChangeRegDataInitRequest(getActivity(), getFragmentManager(), R.id.main_container, new DialogRequestListener(){

					@Override
					public void onResponsed() {
						showList();
					}

					@Override
					public boolean enableDialogs() {
						// TODO Auto-generated method stub
						return false;
					}
					
				});
			}
		});
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_regdata, container, false);
        
        Log.d(TAG, "onCreateView");
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().show();
						
		layout=view.findViewById(R.id.fragment_regdata_layout);
		
		button_edit=(Button)view.findViewById(R.id.fragment_regdata_button_edit);
		button_edit.setOnClickListener(this);
		
		button_password_change=(Button)view.findViewById(R.id.fragment_regdata_button_password_change);
		button_password_change.setOnClickListener(this);
		
		button_back=(Button)view.findViewById(R.id.fragment_regdata_button_back);
		button_back.setOnClickListener(this);
		
        recyclerview=(RecyclerView)view.findViewById(R.id.fragment_regdata_recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(recyclerview.getContext()));
        
        adapter=new CursorMultipleTypesAdapter(getActivity(),null);        
        adapter.addItemHolder(TYPE_TEXT, new ItemHolderText(getActivity(),null));
        adapter.addItemHolder(TYPE_HEADER, new ItemHolderText(getActivity(),R.layout.itemholder_header,null));
        
        recyclerview.setAdapter(adapter);
        
        showList();
        
        return view;
    }
    
    @Override
	public void onClick(View v) {
    	
    	Log.d(TAG, "onClick");
    	
    	if(v.getId()==button_edit.getId()){
    		Session.createAgreementEditFragment(getActivity(), getFragmentManager(), R.id.main_container);
    		return;
    	}
    	
    	if(v.getId()==button_password_change.getId()){
    		Session.createPasswordChangeFragment(getActivity(), getFragmentManager(), R.id.main_container);
    		return;
    	}
    	
    	if(v.getId()==button_back.getId()){
    		getFragmentManager().popBackStack();
    		return;
    	}
    	
	}
    
    void showList(){
    	JSONObject json_info=null;
    	JSONObject json_data=null;

    	//Prepare json_info
		try {
			if(Session.getInfoJson()==null)throw new JSONException("getInfoJson is empty");
			json_info = new JSONObject(Session.getInfoJson());		
		} catch (JSONException e) {
			Log.e(TAG, "getAdapterInfo JSONException e="+e);
			Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 442, R.string.error_442_title, R.string.error_442_message, new CloseListener(){
				@Override
				public void onClosed() {
					Session.popFullBackStack(getFragmentManager());
				}				
			});
		}
		
		//Prepare json_data
		try {
			if(Session.getRegDataJson()==null)throw new JSONException("getDataJson is empty");
			json_data = new JSONObject(Session.getRegDataJson());		
		}catch (JSONException e) {
			Log.e(TAG, "getAdapterInfo JSONException e="+e);
			Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 472, R.string.error_472_title, R.string.error_472_message, new CloseListener(){
				@Override
				public void onClosed() {
					Session.popFullBackStack(getFragmentManager());
				}				
			});
		} catch (NullPointerException e) {
			Log.e(TAG, "getAdapterInfo NullPointerException e="+e);
			Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 471, R.string.error_471_title, R.string.error_471_message, new CloseListener(){
				@Override
				public void onClosed() {
					Session.popFullBackStack(getFragmentManager());
				}				
			});
			
		}
		
		if((json_info!=null)&&(json_data!=null)){
			adapter.changeCursor(getAdapterCursor(json_info,json_data));
		}
    }
    
    
    //------------Preparing cursor----------------------------
	
    protected MatrixCursor getAdapterCursor(JSONObject json_info,JSONObject json_data){

		MatrixCursor matrixcursor=new MatrixCursor(new String[]{adapter.COLUMN_ID, adapter.COLUMN_TYPE, adapter.COLUMN_KEY, adapter.COLUMN_VALUE});   	
    	
    	int _id=-1;    	
    	
    	JSONObject json_item=null;   
    	JSONObject json_object=null; 
    	
    	//------------------------Agreement----------------------------
    	try{    		
    		json_item=new JSONObject("{key:{text:'Логин'},value:{text:'"+Session.getUserLogin()+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	    	
    	//------------------------Person Data----------------------
//    	try{    		
//    		json_item=new JSONObject("{key:{text:'"+getString(R.string.fragment_agreement_header_person)+"'}, value:{visibility:false}}"); 
//    		matrixcursor.addRow(new Object[]{++_id,TYPE_HEADER,0,json_item.toString()});
//    		
//    	}catch(JSONException e){
//    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
//    	}
    	
    	try{    		
    		json_item=new JSONObject("{key:{text:'Фамилия'},value:{text:'"+json_data.getString("surname")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{    		
    		json_item=new JSONObject("{key:{text:'Имя'},value:{text:'"+json_data.getString("name")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{    		
    		json_item=new JSONObject("{key:{text:'Отчество'},value:{text:'"+json_data.getString("patronim")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("agreement");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getString("data")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	//------------------------Contacts----------------------
//    	try{    		
//    		json_item=new JSONObject("{key:{text:'"+getString(R.string.fragment_agreement_header_contact)+"'}, value:{visibility:false}}"); 
//    		matrixcursor.addRow(new Object[]{++_id,TYPE_HEADER,0,json_item.toString()});
//    		
//    	}catch(JSONException e){
//    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
//    	}
    	
    	
    	try{
    		if(!json_data.getString("email").isEmpty()){
	    		json_item=new JSONObject("{key:{text:'e-mail'},value:{text:'"+json_data.getString("email")+"'}}"); 
	    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});
    		}
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{    
    		if(!json_data.getString("phone").isEmpty()){
	    		json_item=new JSONObject("{key:{text:'Телефон'},value:{text:'"+json_data.getString("phone")+"'}}"); 
	    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,0,json_item.toString()});
    		}
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	    	
    	
    	return matrixcursor;   
    	
    }
    
}
