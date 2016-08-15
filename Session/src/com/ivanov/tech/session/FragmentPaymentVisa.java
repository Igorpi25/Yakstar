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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class FragmentPaymentVisa extends DialogFragment implements OnClickListener {


    private static String TAG = FragmentPaymentVisa.class.getSimpleName();
    
    Button button_pay,button_back,button_close;
    EditText edittext_amount;
    
    View layout_amount,layout_webview;
    WebView webview;
    
    ViewGroup container;

    public static FragmentPaymentVisa newInstance() {
    	FragmentPaymentVisa f = new FragmentPaymentVisa();
    	
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
        view = inflater.inflate(R.layout.fragment_payment_visa, container, false);
        
        button_pay = (Button) view.findViewById(R.id.fragment_payment_visa_button_pay);
        button_pay.setOnClickListener(this);
        
        button_back = (Button) view.findViewById(R.id.fragment_payment_visa_button_back);
        button_back.setOnClickListener(this);
        
        button_close = (Button) view.findViewById(R.id.fragment_payment_visa_button_close);
        button_close.setOnClickListener(this);
        
        edittext_amount=(EditText)view.findViewById(R.id.fragment_payment_visa_edittext_amount);
        
        layout_amount=view.findViewById(R.id.fragment_payment_visa_layout_amount);
        
        //My cookies
//        final HashMap<String, String> additional_headers = new HashMap<String, String>();        
//        Session.addCookiesToHeader(additional_headers);
        
        layout_webview=view.findViewById(R.id.fragment_payment_visa_layout_webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "onCreateView webview.shouldOverrideUrlLoading url="+url);
//            	view.loadUrl(url, additional_headers);
            	
                return false;
            }
        });
        
        this.container=container;
        
        showAmount();
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==button_pay.getId()){
			
			final String amount= edittext_amount.getText().toString();
			
			Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new ProtocolListener(){

				@Override
				public void isCompleted() {
					doPaymentVisaRequest(getActivity(),amount);
				}

				@Override
				public void onCanceled() {
					
				}
				
			});
			
		}
		
		if( (v.getId()==button_back.getId()) || (v.getId()==button_close.getId()) ){
			
			getFragmentManager().popBackStack();
			
		}
		
	}
		
	void doPaymentVisaRequest(final Context context,final String amount) {

    	final String tag = TAG+" doPaymentCardactRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Подготовка оплаты ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
    	StringRequest request = new StringRequest(Request.Method.GET,
    			Session.getPaymentVisaUrl()+"?paysystem=https://secure.payonlinesystem.com/ru/payment/&b-payonline__amount="+amount,
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
	                        		final JSONObject json=new JSONObject(response);
	                        		
	                        		boolean status=json.getBoolean("status");
	                        		String message=json.getString("message");
	                        		
	                        		if(status==false){
	                        			Log.e(TAG, tag+"onResponse status=false message=" + message);
	                        			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	                        		}else{
	                        			Log.d(TAG, tag+"onResponse status=true message=" + message);
	                        			showWebview(json);	                        			
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
	
	void showWebview(JSONObject json){
		hideKeyboard();
		
		
		
		layout_webview.setVisibility(View.VISIBLE);
		layout_amount.setVisibility(View.GONE);
		
		button_close.setVisibility(View.GONE);
	}
	
	void showAmount(){
		layout_webview.setVisibility(View.GONE);
		layout_amount.setVisibility(View.VISIBLE);
		
		button_close.setVisibility(View.GONE);
	}
	
	void success(){
		button_close.setVisibility(View.VISIBLE);
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
