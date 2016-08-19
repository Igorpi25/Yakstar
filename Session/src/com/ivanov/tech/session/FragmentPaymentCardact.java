package com.ivanov.tech.session;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.connection.Connection.ProtocolListener;
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentPaymentCardact extends DialogFragment implements OnClickListener {


    private static String TAG = FragmentPaymentCardact.class.getSimpleName();
    
    Button button_activate,button_back,button_close;
    EditText edittext_number,edittext_code_one,edittext_code_two,edittext_code_three,edittext_code_four;
    
    TextView textview_success_message;
    
    View layout_form,layout_success;
    
    ViewGroup container;

    public static FragmentPaymentCardact newInstance() {
    	FragmentPaymentCardact f = new FragmentPaymentCardact();
    	
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
    
    public void onStop(){
    	super.onStop();    	
    	hideKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_payment_cardact, container, false);
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().show();
        
        button_activate = (Button) view.findViewById(R.id.fragment_payment_cardact_button_activate);
        button_activate.setOnClickListener(this);
        
        button_back = (Button) view.findViewById(R.id.fragment_payment_cardact_button_back);
        button_back.setOnClickListener(this);
        
        button_close = (Button) view.findViewById(R.id.fragment_payment_cardact_button_close);
        button_close.setOnClickListener(this);
        
        edittext_number=(EditText)view.findViewById(R.id.fragment_payment_cardact_edittext_number);
        
        edittext_code_one=(EditText)view.findViewById(R.id.fragment_payment_cardact_edittext_code_one);
        edittext_code_two=(EditText)view.findViewById(R.id.fragment_payment_cardact_edittext_code_two);
        edittext_code_three=(EditText)view.findViewById(R.id.fragment_payment_cardact_edittext_code_three);
        edittext_code_four=(EditText)view.findViewById(R.id.fragment_payment_cardact_edittext_code_four);
                
        layout_form=view.findViewById(R.id.fragment_payment_cardact_layout_form);
        layout_success=view.findViewById(R.id.fragment_payment_cardact_layout_success);
        
        textview_success_message=(TextView)view.findViewById(R.id.fragment_payment_cardact_textview_success_message);
        
        this.container=container;
        
        showForm();
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==button_activate.getId()){
			
			final String number= edittext_number.getText().toString();
			final String code= getCompiledCode();
			
			Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new ProtocolListener(){

				@Override
				public void isCompleted() {
					doPaymentCardactRequest(getActivity(),number,code);
				}

				@Override
				public void onCanceled() {
					
				}
				
			});
			
		}
		
		if( (v.getId()==button_back.getId()) ){
			
			getFragmentManager().popBackStack();
			
		}
		
		if( (v.getId()==button_close.getId()) ){
			
			Session.popFullBackStack(getFragmentManager());
			
		}
		
	}
		
	void doPaymentCardactRequest(final Context context,final String number,final String code) {

    	final String tag = TAG+" doPaymentCardactRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Активация карты ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
    	StringRequest request = new StringRequest(Request.Method.GET,
    			Session.getPaymentCardactUrl()+"?b-cardact__number="+number+"&b-cardact__code="+code,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, "onResponse 1 " + response);
    	                        pDialog.hide();
    	                        
    	                        
	                        	if( (response==null)||(response.isEmpty()) ){
	                        		Log.e(TAG, tag+"onResponse empty response=");
	                        		Toast.makeText(context, "Ошибка активации", Toast.LENGTH_LONG).show();
	                        		return;
	                        	}
	                        	
	                        	
	                        	try{
	                        		JSONObject json=new JSONObject(response);
	                        		
	                        		boolean status=json.getBoolean("status");
	                        		String message=json.getString("message");
	                        		
	                        		if(status==false){
	                        			Log.e(TAG, tag+"onResponse status=false message=" + message);
	                        			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	                        		}else{
	                        			Log.d(TAG, tag+"onResponse status=true message=" + message);
	                        			showSuccess(message);	                        			
	                        		}
	                        			                        		
	                        	}catch(JSONException e){
	                        		Log.e(TAG, tag+"onResponse JSONException e=" + e.getMessage());
	                        	}
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "1 Volley.onErrorResponser: " + error.getMessage());
    	                        pDialog.hide();
    	                        
    	                        Toast.makeText(context, "Ошибка соединения к сети", Toast.LENGTH_LONG).show();
    	                    }
    	                }){
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
    			
    			Log.d(TAG, "getHeaders");
    			
                HashMap<String, String> headers = new HashMap<String, String>();
                
                Session.addCookiesToHeader(headers);
                
                return headers;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
	
	String getCompiledCode(){
		return
		edittext_code_one.getText().toString()+
		"-"+
		edittext_code_two.getText().toString()+
		"-"+
		edittext_code_three.getText().toString()+
		"-"+
		edittext_code_four.getText().toString();
	}
	
	void showSuccess(String message){
		layout_success.setVisibility(View.VISIBLE);
		layout_form.setVisibility(View.GONE);
		
		textview_success_message.setText(message);
	}
	
	void showForm(){
		layout_success.setVisibility(View.GONE);
		layout_form.setVisibility(View.VISIBLE);
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
	
}
