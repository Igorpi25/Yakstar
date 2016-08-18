package com.ivanov.tech.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings.PluginState;
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
    TextView textview_success_summary;
    
    View layout_amount,layout_webview,layout_success;
    WebView webview;
    
    ViewGroup container;    
    
    ProgressDialog dialogloading;    
    Handler handlerloading;
    Timer timerloading;

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

        this.container=container;
        
        button_pay = (Button) view.findViewById(R.id.fragment_payment_visa_button_pay);
        button_pay.setOnClickListener(this);
        
        button_back = (Button) view.findViewById(R.id.fragment_payment_visa_button_back);
        button_back.setOnClickListener(this);
        
        button_close = (Button) view.findViewById(R.id.fragment_payment_visa_button_close);
        button_close.setOnClickListener(this);
        
        edittext_amount=(EditText)view.findViewById(R.id.fragment_payment_visa_edittext_amount);
        
        textview_success_summary=(TextView)view.findViewById(R.id.fragment_payment_visa_textview_success_summary);
        
        layout_amount=view.findViewById(R.id.fragment_payment_visa_layout_amount);
        
        layout_webview=view.findViewById(R.id.fragment_payment_visa_layout_webview);
        
        layout_success=view.findViewById(R.id.fragment_payment_visa_layout_success);
        
        webview = (WebView)view.findViewById(R.id.fragment_payment_visa_webview);
        
        webview.getSettings().setJavaScriptEnabled(true);
        
        handlerloading =  new Handler() {
            public void handleMessage(Message msg) {
            	dialogloading.hide();
            }
        };
        
        dialogloading = new ProgressDialog(getActivity());
    	dialogloading.setCancelable(false);
    	
    	timerloading= new Timer();
        
        showAmount();
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==button_pay.getId()){
			
			//Prepare URL
	        final String amount= edittext_amount.getText().toString();
	        
			if(checkMinAmount(amount)){			
				Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new ProtocolListener(){
	
					@Override
					public void isCompleted() {											
						doPaymentVisaRequest(getActivity(), amount);
					}
	
					@Override
					public void onCanceled() {
						
					}
					
				});
			}
		}
		
		if( (v.getId()==button_back.getId()) ){
			
			getFragmentManager().popBackStack();
			
		}
		
		if( (v.getId()==button_close.getId()) ){
			
			Session.popFullBackStack(getFragmentManager());
			
		}
		
	}
		
	void doPaymentVisaRequest(final Context context,final String amount) {

    	final String tag = TAG+" doPaymentCardactRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Подготовка оплаты ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
    	final String url=Session.getPaymentVisaUrl()+"?paysystem=https://secure.payonlinesystem.com/ru/payment/&b-payonline__amount="+amount;
        
    	StringRequest request = new StringRequest(Request.Method.GET,url,    			
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, "onResponse 1 " + response);
    	                        pDialog.hide();
    	                        
    	                        
	                        	if( (response==null)||(response.isEmpty()) ){
	                        		Log.e(TAG, tag+"onResponse empty response=");
	                        		Toast.makeText(context, "Ошибка! Данные из сервера не получены", Toast.LENGTH_LONG).show();
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
	                        			
	                        			showWebview(json.getJSONObject("payData"),amount);
	                        			
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
	
	void showWebview(JSONObject json,final String amount){
		
        try{
        	String postData=
	        	"paysystem=https://secure.payonlinesystem.com/ru/payment/"
		        +"&b-payonline__amount="+amount
		        +"&MerchantId="+json.getString("MerchantId")
		        +"&OrderId="+json.getString("OrderId")
		        +"&Amount="+json.getString("Amount")
		        +"&Currency="+json.getString("Currency")
		        +"&ValidUntil="+json.getString("ValidUntil")
		        +"&SecurityKey="+json.getString("SecurityKey")
		        +"&ReturnUrl="+json.getString("ReturnUrl")
		        +"&FailUrl="+json.getString("FailUrl")
		        +"&ClientId="+json.getString("ClientId")
		        +"&url="+json.getString("url");
        	
        	hideKeyboard();
        	
        	layout_amount.setVisibility(View.GONE);		
    		layout_webview.setVisibility(View.VISIBLE);
    		layout_success.setVisibility(View.GONE);
    		
			webview.setWebViewClient(new WebViewClient() {        	
		            @Override
		            public boolean shouldOverrideUrlLoading(WebView view, String url) {
		                Log.d(TAG, "onCreateView webview.shouldOverrideUrlLoading url="+url);        
		                
		                return false;
		            }
		            
		            @Override
		            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
		            	Log.d(TAG, "onCreateView webview.onReceivedSslError"); 
		            	handler.proceed() ;
		            }
		            
		            @Override
		            public void onPageStarted(WebView view, String url, Bitmap favicon) {
		            	Log.d(TAG, "onCreateView webview.onPageStarted url="+url);
		            	
		            	if(isOkUrl(url)){
		            		success(amount);
		            	}
		            	
		            	showLoading("Подготовка оплаты ..");
		            }
	
		            @Override
		            public void onPageFinished(WebView view, String url) {
		            	Log.d(TAG, "onCreateView webview.onPageFinished url="+url);
		            	if(isOkUrl(url)){
		            		success(amount);
		            	}
		            	
		            }
		            
		            @Override
		            public void onLoadResource(WebView view, String url) {
		            	Log.d(TAG, "onCreateView webview.onLoadResource url="+url);
		            	if(isOkUrl(url)){
		            		success(amount);
		            	}
	
		            	showLoading("Подготовка оплаты ...");
		            }
		            
		    	});
    		
        	webview.postUrl("https://secure.payonlinesystem.com/ru/payment/", EncodingUtils.getBytes(postData, "utf-8"));
        	//webview.postUrl("https://lk.amtelcom.ru/", EncodingUtils.getBytes(postData, "utf-8"));
        	
        }catch(JSONException e){
        	Log.e(TAG, "showWebview postData JSONException e="+e);
        	Toast.makeText(getActivity(),"Ошибка парсинга json-ответа сервера",Toast.LENGTH_LONG).show();
        }
        
	}
	
	void showAmount(){
		layout_amount.setVisibility(View.VISIBLE);		
		layout_webview.setVisibility(View.GONE);
		layout_success.setVisibility(View.GONE);
	}
	
	void success(String amount){
		layout_amount.setVisibility(View.GONE);		
		layout_webview.setVisibility(View.GONE);
		layout_success.setVisibility(View.VISIBLE);
		
		//Prevent clicking on links
		webview.setOnTouchListener(new View.OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		       return true;
		    }
		});
		
		textview_success_summary.setText("Сумма оплаты "+amount+" руб");
		
		dialogloading.hide();
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
	
	boolean isOkUrl(String url){
		ArrayList<String> array=new ArrayList<String>();
		
		array.add("https://form.payonlinesystem.com/default/draft/ok");
		array.add("https://form.payonlinesystem.com/default/draft/rel/img/mobile/ok-page/ok-x2.png");
		array.add("https://form.payonlinesystem.com/default/draft/rel/css/desktop.control.ok-page.css");
		array.add("https://form.payonlinesystem.com/default/draft/rel/css/mobile.control.ok-page.css");
		
		for(String item : array){
			if(url.equals(item)){
				return true;
			}
		}
		
		return false;
	}

	boolean checkMinAmount(String amount){
		
		int value=-1;
		
		try{
			value=Integer.parseInt(amount);
		}catch(NumberFormatException e){
			Log.e(TAG, "checkMinAmount e="+e);
			Toast.makeText(getActivity(), "Неправильный формат числа", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if (value<100){
			Log.d(TAG, "checkMinAmount значение не входит в диапазон value="+value);
			Toast.makeText(getActivity(), "Минимальная сумма оплаты 100 руб", Toast.LENGTH_LONG).show();
			return false;
		}else {			
			return true;
		}
	}
	
	void showLoading(String message){
		
		dialogloading.setMessage(message);
		dialogloading.show();
		
		timerloading.cancel();
		timerloading.purge();
		
		timerloading=new Timer();
		
		timerloading.schedule(new TimerTask(){

			@Override
			public void run() {
				handlerloading.obtainMessage(1).sendToTarget();
			}
			
		}, 6000);
		
	}
}
