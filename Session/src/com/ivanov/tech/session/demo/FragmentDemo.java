package com.ivanov.tech.session.demo;


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
import com.ivanov.tech.session.Session.RequestListener;
import com.ivanov.tech.session.adapter.ItemHolderText;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentDemo extends DialogFragment implements OnClickListener ,CheckInternetListener, OnCheckedChangeListener{

    public static final String TAG = FragmentDemo.class
            .getSimpleName();    
	
    protected static final int TYPE_TEXT = 0;
    protected static final int TYPE_TEXT_BALANCE = 1;
    
    protected SubMenu menuSession;
	protected MenuItem menuLogout;
    
    TextView textview_gov_link,textview_internet_value;
    SwitchCompat switch_internet;
    View layout_internet,layout_info;
    
    RecyclerView recyclerview_info;
    CursorMultipleTypesAdapter adapter_info;
    
    //Button button_signout;

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
        
        Log.d(TAG, "onResume");
        
        Session.doInfoRequest(getActivity(),getFragmentManager(), R.id.main_container, new RequestListener(){

			@Override
			public void onResponsed() {
		    	updateInfo();		    	
			}	
        });
    
        Session.doCheckInternetRequest(getActivity(), getFragmentManager(), R.id.main_container, this);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_demo, container, false);
        
        Log.d(TAG, "onCreateView");
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().show();
		
		((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name); 
		
		textview_gov_link = (TextView)view.findViewById(R.id.fragment_demo_textview_gov_link);
		textview_gov_link.setClickable(true);
		textview_gov_link.setMovementMethod(LinkMovementMethod.getInstance());
		
        layout_internet = view.findViewById(R.id.fragment_demo_layout_internet);        
        layout_info = view.findViewById(R.id.fragment_demo_layout_info);
        
        switch_internet = (SwitchCompat)view.findViewById(R.id.fragment_demo_switch_internet);
        switch_internet.setSwitchTextAppearance(getActivity(), R.style.SwitchInternetTheme);
        
        textview_internet_value = (TextView)view.findViewById(R.id.fragment_demo_textview_internet_value);
        
        recyclerview_info=(RecyclerView)view.findViewById(R.id.fragment_demo_recyclerview_info);
        recyclerview_info.setLayoutManager(new LinearLayoutManager(recyclerview_info.getContext()));
        
        adapter_info=new CursorMultipleTypesAdapter(getActivity(),null);        
        adapter_info.addItemHolder(TYPE_TEXT, new ItemHolderText(getActivity(),null));
        adapter_info.addItemHolder(TYPE_TEXT_BALANCE, new ItemHolderText(getActivity(),R.layout.itemholder_text_clickable,new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick TYPE_TEXT_BALANCE");
				Session.createPaymentRootFragment(getActivity(), getFragmentManager(), R.id.main_container);				
			}
        	
        }));
        
        recyclerview_info.setAdapter(adapter_info);
        
        //Take info from preferences
        updateInfo();
        
        //Set status to waiting
        internetWaiting();
        
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        
		menu.clear();
				
		menuSession = menu.addSubMenu(R.id.fragment_demo_menu_session, Menu.NONE, 1, R.string.fragment_demo_menu_session).setIcon(R.drawable.ic_menu_logout_white);
		menuSession.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menuLogout=menuSession.add(Menu.NONE, R.id.fragment_demo_menu_logout, Menu.NONE,R.string.fragment_demo_menu_logout);
		menuLogout.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menuLogout.setIcon(R.drawable.ic_menu_logout_dark);
		menuLogout.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.d(TAG, "onMenuItemClick signout");
				Session.Logout(getActivity(), getFragmentManager(), R.id.main_container);
				return true;
			}
			
		});
    }
    
    @Override
	public void onClick(View v) {
		
	}

    @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	Log.d(TAG, "onCheckedChanged switch_internet.checked="+switch_internet.isChecked()+" isChecked="+isChecked);
    	
    	if(isChecked){
    		textview_internet_value.setText(R.string.fragment_demo_textview_internet_enabling);
		}else{
			textview_internet_value.setText(R.string.fragment_demo_textview_internet_disabling);
		}
    	
    	Session.doSwitchInternetRequest(getActivity(),getFragmentManager(),R.id.main_container,this);
    	switch_internet.setEnabled(false);    	
    	
	}
    
    void updateInfo(){
    	
    	try {
    		
    		JSONObject json_info=new JSONObject(Session.getInfoJson());
    	
    		String servicestatusdata=json_info.getJSONObject("serviceStatus").getString("data");
    		
			if(servicestatusdata.equals("Активна")){						
				adapter_info.changeCursor(getAdapterInfo(json_info));				
			}else if(servicestatusdata.equals("Заблокирована")){					
				adapter_info.changeCursor(getAdapterInfo(json_info));				
			}else{				
				Log.e(TAG, "updateInfo unknown service.status value="+servicestatusdata);	
				//Try to parse it anyway
				adapter_info.changeCursor(getAdapterInfo(json_info));				
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "onStart doInfoRequest onResponsed JSONException e="+e);
			Session.createErrorTerminateFragment(getActivity(), getFragmentManager(), R.id.main_container, 442, R.string.error_442_title, R.string.error_442_message);
		}
    }
    
    void internetWaiting(){
    	if(Session.getInternetState()){
			textview_internet_value.setText(R.string.fragment_demo_textview_internet_on);
		}else{
			textview_internet_value.setText(R.string.fragment_demo_textview_internet_off);
		}
    	
    	switch_internet.setEnabled(false);
    	
    	switch_internet.setOnCheckedChangeListener(null);
    	switch_internet.setChecked(Session.getInternetState());
    	switch_internet.setOnCheckedChangeListener(this);
    }
    
    //------------Preparing cursor----------------------------
	
    protected MatrixCursor getAdapterInfo(JSONObject json_info){

    	MatrixCursor matrixcursor=new MatrixCursor(new String[]{adapter_info.COLUMN_ID, adapter_info.COLUMN_TYPE, adapter_info.COLUMN_KEY, adapter_info.COLUMN_VALUE});    	
    	
    	int _id=-1;    	
    	
    	JSONObject json_item=null;   
    	JSONObject json_object=null; 
    	
    	try{
    		json_object=json_info.getJSONObject("agreement");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getString("data")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("balance");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getDouble("data")+" руб'} }"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT_BALANCE,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("toPay");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getDouble("data")+" руб'} }"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("debet");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getDouble("data")+" руб'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("tariffName");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getString("data")+"'} }"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("levelname");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getString("data")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		JSONArray json_array=json_info.getJSONArray("trafic");
    		json_object=json_array.getJSONObject(0);
    		json_item=new JSONObject("{key:{text:'Трафик'},value:{text:'"+new DecimalFormat("###.#").format(json_object.getDouble("restbytes")/1048576)+"/"+Math.round(json_object.getDouble("maxbytes")/1048576)+" МБ'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	try{
    		json_object=json_info.getJSONObject("serviceStatus");
    		json_item=new JSONObject("{key:{text:'"+json_object.getString("title")+"'},value:{text:'"+json_object.getString("data")+"'}}"); 
    		matrixcursor.addRow(new Object[]{++_id,TYPE_TEXT,_id,json_item.toString()});
    		
    	}catch(JSONException e){
    		Log.e(TAG, "getAdapterInfo JSONException e="+e);
    	}
    	
    	return matrixcursor;   
    	
    }
    
    //-----------------CheckInternetListener-----------------------------
    
    @Override
	public void isOnline() {
    	textview_internet_value.setText(R.string.fragment_demo_textview_internet_on);
    	
    	switch_internet.setEnabled(true); 
    	
    	switch_internet.setOnCheckedChangeListener(null);
    	switch_internet.setChecked(true);
    	switch_internet.setOnCheckedChangeListener(this);
	}

	@Override
	public void isOffline() {
		textview_internet_value.setText(R.string.fragment_demo_textview_internet_off);
    	
    	switch_internet.setEnabled(true);
    	
    	switch_internet.setOnCheckedChangeListener(null);
    	switch_internet.setChecked(false);
    	switch_internet.setOnCheckedChangeListener(this);
	}
	

	@Override
	public void isUnknown() {
		textview_internet_value.setText(R.string.fragment_demo_textview_internet_error);
    	switch_internet.setEnabled(false);
    	
    	switch_internet.setOnCheckedChangeListener(null);
    	switch_internet.setChecked(Session.getInternetState());
    	switch_internet.setOnCheckedChangeListener(this);
	}

	
}
