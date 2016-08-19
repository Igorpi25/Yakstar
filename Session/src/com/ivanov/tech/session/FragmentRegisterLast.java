package com.ivanov.tech.session;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaderFactory;
import com.bumptech.glide.load.model.LazyHeaders;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.connection.Connection.ProtocolListener;
import com.ivanov.tech.session.Session.CheckAuthorizationListener;
import com.ivanov.tech.session.Session.CloseListener;
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentRegisterLast extends DialogFragment implements OnClickListener {
private static String TAG = FragmentRegisterLast.class.getSimpleName();
    
    Button button_submit,button_back;
    Spinner spinner_tarif;
    TextView textview_tarif_summary;
    
    CheckBox checkbox_one,checkbox_two;
    
    ImageView imageview_captcha;    
    Button button_captcha_update;
    EditText edittext_captcha_code;
    
    CheckAuthorizationListener protocollistener;
    ViewGroup container;
    
    int errorcounter=0;
    int maxerrornum=4;
    
    boolean success=false;    

    public static FragmentRegisterLast newInstance(CheckAuthorizationListener listener) {
    	FragmentRegisterLast f = new FragmentRegisterLast();
    	f.protocollistener=listener;
    	
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_register_last, container, false);
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        
        button_submit = (Button) view.findViewById(R.id.fragment_register_button_submit);
        button_submit.setOnClickListener(this);
        
        button_back = (Button) view.findViewById(R.id.fragment_register_button_back_second);
        button_back.setOnClickListener(this);
        
        checkbox_one = (CheckBox)view.findViewById(R.id.fragment_register_checkbox_one);
        checkbox_two = (CheckBox)view.findViewById(R.id.fragment_register_checkbox_two);
        
        imageview_captcha=(ImageView)view.findViewById(R.id.fragment_register_imageview_captcha);
        button_captcha_update = (Button)view.findViewById(R.id.fragment_register_button_captcha_update);
        button_captcha_update.setOnClickListener(this);
        edittext_captcha_code=(EditText)view.findViewById(R.id.fragment_register_edittext_captcha_code);
        
        this.container=container;
        
        doCapchaRequest(getActivity(),false);
        
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
    	edittext_captcha_code.setText("");
    	
    	hideKeyboard();
    }
    
    public void onResume(){
    	super.onResume();   
    	
    }
	
    @Override
	public void onClick(View v) {
		
		if(v.getId()==button_submit.getId()){
			
			if(checkConfirms()&&checkCaptcha()){
				saveRegisterJson();
				
				doRegisterRequest(getActivity(),getFragmentManager(),R.id.main_container);
			}
		}
			
		if(v.getId()==button_back.getId()){
			getFragmentManager().popBackStack();			
		}
		
		if(v.getId()==button_captcha_update.getId()){
			//updateCaptcha();
			doCapchaRequest(getActivity(),true);
		}
			
	}
	
	boolean checkConfirms(){
		if ( !(checkbox_one.isChecked()&&checkbox_two.isChecked()) ){
			showWarningCheck(R.string.register_check_confirm);
		}
		
		return checkbox_one.isChecked()&&checkbox_two.isChecked();
	}
	
	boolean checkCaptcha(){
		if(edittext_captcha_code.getText().length()==0)showWarningCheck(R.string.register_check_captcha);
		return (edittext_captcha_code.getText().length()>0);
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

	void doRegisterRequest(final Context context, final FragmentManager fragmentManager, final int container) {

    	final String tag = TAG+" doRegisterRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Регистрация...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
    	StringRequest request = new StringRequest(Method.POST,
    			Session.getRegisterUrl(),
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, "onResponse doRegisterRequest " + response);
    	                        pDialog.hide();
    	                        
    	                        JSONObject json;
    	                        
    	                        try{
    	                        	json=new JSONObject(response);
    	                        	
    	                        	if(json.getBoolean("status")==true){

    	                                Session.removeRegisterJson();
    	                                
    	                        		String message=json.getString("message");
    	                        		
    	                        		Session.setRegisteredMessage(message);

    	                                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); 

    	                                
    	                        		Session.createSessionRegisterSuccessFragment(getActivity(), getFragmentManager(), R.id.main_container, protocollistener);
    	                        		
    	                        	}else{
    	                        		showWarningNotRegistered(json.getString("message"));
    	                        	}
    	                        	
    	                        }catch(JSONException e){
    	                        	Log.e(TAG,"doRegisterRequest " +e.toString());
    	                        	Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 432, R.string.error_432_title, R.string.error_432_message, null);
    	                        }
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "Volley.onErrorResponser: " + error.getMessage());
    	                        pDialog.hide();
    	                        
    	                        Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 35, R.string.error_35_title, R.string.error_35_message, null);
    	                    }
    	                }){
    		
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                Session.addCookiesToHeader(headers);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                
                return headers;
            }
    		
    		@Override
            protected Map<String, String> getParams() {
    			
                Map<String, String> params = new HashMap<String, String>();
                
                try{
                	
	                JSONObject json=new JSONObject(Session.getRegisterJson());
	                
	                
	                params.put("surname", json.getString("surname"));
	                params.put("name", json.getString("name"));
	                params.put("patr", json.getString("patr"));
	                
	                params.put("email", json.getString("email"));
	                params.put("phone", json.getString("phone"));
	                params.put("passport_num", json.getString("passport_num"));
	                params.put("passport_who", json.getString("passport_who"));
	                
	                params.put("tarif", json.getString("tarif_id"));
	                params.put("confirm_one", "on");
	                params.put("confirm_two", "on");
	               
	                params.put("vftext", json.getString("captcha_code"));
	                
	                Log.d(TAG, params.toString());
                
                }catch(JSONException e){
                	Log.e(TAG, "doRegisterRequest getParams JSONException e="+e);
                }
                
                return params;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);
    	
    }
	
	void doCapchaRequest(final Context context, final boolean enabledialog) {

    	final String tag = TAG+" doCapchaRequest"; 
    	
    	Log.e(TAG,tag);
    	edittext_captcha_code.setText("");
    	imageview_captcha.setImageResource(R.color.color_white);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Обновление кода безопасности...");
    	pDialog.setCancelable(false);
    	if(enabledialog)pDialog.show();
    	
    	ImageRequest request = new ImageRequest(Session.getCaptchaUrl(),
    		    new Response.Listener<Bitmap>() {
    		        @Override
    		        public void onResponse(Bitmap bitmap) {
    		        	pDialog.hide();
    		            imageview_captcha.setImageBitmap(bitmap);
    		            edittext_captcha_code.setText("");
    		        }
    		    }, 0, 0, null,
    		    new Response.ErrorListener() {
    		        public void onErrorResponse(VolleyError error) {
    		        	pDialog.hide();
    		        	imageview_captcha.setImageResource(R.color.color_white);
    		        	edittext_captcha_code.setText("");
    		        	
    		        	//final FragmentManager fragmentmanager=getFragmentManager();    		        	
    		        	Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 34, R.string.error_34_title, R.string.error_34_message, new CloseListener(){

							@Override
							public void onClosed() {
								Session.killApp();
							}
    		        		
    		        	});
    		        }
    		    }){
    			
    		@Override
    	    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
    	        
    			//Session.parseCookies(response.headers);
    	            			
    	        return super.parseNetworkResponse(response);
    	    }
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                Session.addCookiesToHeader(headers);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                
                
                return headers;
            }
    		
    	};

    	
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);
    	
    }

	void saveRegisterJson(){
		
		try {
			JSONObject json;
			
			if(Session.isRegisterJsonExists()){
				json=new JSONObject(Session.getRegisterJson());
			}else{
				json=new JSONObject();
			}
			
			json.put("confirm_one", checkbox_one.isChecked());
			json.put("confirm_two", checkbox_two.isChecked());
			
			json.put("captcha_code", edittext_captcha_code.getText().toString());
			
			Session.setRegisterJson(json.toString());
			
		} catch (JSONException e) {e.printStackTrace();}
				
	}
	
	void loadRegisterJson(){
		if(!Session.isRegisterJsonExists())return;
		
		try {
			JSONObject json=new JSONObject(Session.getRegisterJson());
			
			checkbox_one.setChecked(json.getBoolean("confirm_one"));
			checkbox_two.setChecked(json.getBoolean("confirm_two"));
			
			edittext_captcha_code.setText("");
			
		} catch (JSONException e) {e.printStackTrace();}
			
		
	}
	
	void showWarningCheck(int stringid){
		Toast.makeText(getActivity(), stringid, Toast.LENGTH_LONG).show();
	}
	
	void showWarningNotRegistered(String message){
		errorcounter++;
		
		if(errorcounter<maxerrornum){
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
			doCapchaRequest(getActivity(),false);
		}else{
			errorcounter=0;
			Session.createErrorFragment(getActivity(), getFragmentManager(), R.id.main_container, 431, R.string.error_431_title, R.string.error_431_message,null);
		}
		
	}
	
}
