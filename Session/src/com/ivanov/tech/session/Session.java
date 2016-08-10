package com.ivanov.tech.session;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.connection.FragmentNoConnection;
import com.ivanov.tech.connection.FragmentNoServerResponding;
import com.ivanov.tech.connection.Connection.ProtocolListener;
import com.ivanov.tech.session.Session.LogoutListener;
import com.ivanov.tech.session.demo.FragmentDemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class Session {
	
    private static String TAG = Session.class.getSimpleName();
     
    private static final String PREF = "Session";
        
    public static final String PREF_USER_LOGIN="PREF_USER_LOGIN";
    public static final String PREF_USER_LOGIN_DEFAULT="(login)";
    
    public static final String PREF_USER_PASSWORD="PREF_USER_PASSWORD";
    public static final String PREF_USER_PASSWORD_DEFAULT="(password)";

    public static final String PREF_LOGOUT_URL="PREF_LOGOUTURL";
    public static final String PREF_CHECK_AUTORISATION_URL="PREF_CHECKAUTORISATIONURL";
    public static final String PREF_LOGIN_URL="PREF_LOGINURL";
    public static final String PREF_REGISTER_URL="PREF_REGISTERURL";
    public static final String PREF_CHECK_INTERNET_URL="PREF_CHECK_INTERNETURL";
    public static final String PREF_SWITCH_INTERNET_URL="PREF_SWITCH_INTERNETURL";
    
    public static final String PREF_COOKIES="PREF_COOKIES";
    public static final String PREF_COOKIES_DEFAULT=null;

    private static final String HEADERS_KEY_SETCOOKIES = "Set-Cookie";
    private static final String HEADERS_KEY_COOKIE = "Cookie";
    
    private static final String COOKIE_LOGIN = "login";
    private static final String COOKIE_PASSWORD = "password";
    
    public static void Logout(final Context context, final FragmentManager fragmentManager, final int container){
    	Session.doLogoutRequest(context, fragmentManager, container, new LogoutListener(){

			@Override
			public void onLogout() {
				Log.d(TAG, "onClick logout onLogout");
				Session.removeCookies();
				Session.removeLogin();
				Session.removePassword();
				
				Session.checkAutorisation(context, fragmentManager, container, new Connection.ProtocolListener() {
					
					@Override
					public void onCanceled() {
						//Приложение не запустится, пока пользователь не будет авторизован
					}
					
					@Override
					public void isCompleted() {
				        fragmentManager.beginTransaction()
				                .replace(R.id.main_container, new FragmentDemo())
				                .commit();		
					}
				});
			}
			
		});
    }
    
    public static String getLogoutUrl(){
    	return preferences.getString(Session.PREF_LOGOUT_URL, null);    	
    }
        
    public static String getCheckAutorisationUrl(){
    	return preferences.getString(Session.PREF_CHECK_AUTORISATION_URL, null);    	
    }
    
    public static final String getLoginUrl(){
    	return preferences.getString(Session.PREF_LOGIN_URL, null);    	
    }
    
    public static final String getRegisterUrl(){
    	return preferences.getString(Session.PREF_REGISTER_URL, null);    	
    }

    public static final String getCheckInternetUrl(){
    	return preferences.getString(Session.PREF_CHECK_INTERNET_URL, null);    	
    }

    public static final String getSwitchInternetUrl(){
    	return preferences.getString(Session.PREF_SWITCH_INTERNET_URL, null);    	
    }

    static private SharedPreferences preferences=null;
    
    public static void Initialize(Context context, String url_check_autorisation, String url_login, String url_register, String url_check_internet, String url_switch_internet, String url_logout){
    	if(preferences==null){
    		preferences=context.getApplicationContext().getSharedPreferences(PREF, 0);
    	}
    	preferences.edit().putString(Session.PREF_CHECK_AUTORISATION_URL, url_check_autorisation).commit();
    	preferences.edit().putString(Session.PREF_LOGIN_URL, url_login).commit();
    	preferences.edit().putString(Session.PREF_REGISTER_URL, url_register).commit();
    	preferences.edit().putString(Session.PREF_CHECK_INTERNET_URL, url_check_internet).commit();
    	preferences.edit().putString(Session.PREF_SWITCH_INTERNET_URL, url_switch_internet).commit();
    	preferences.edit().putString(Session.PREF_LOGOUT_URL, url_logout).commit();
    }
 
    public static String getUserLogin(){		
  		return preferences.getString(Session.PREF_USER_LOGIN, Session.PREF_USER_LOGIN_DEFAULT);
  	}
    
    public static void setUserLogin(String user_login){
  		if(!getUserLogin().equals(user_login)){
  			preferences.edit().putString(Session.PREF_USER_LOGIN, user_login).commit();
  		}
  	}
    
    public static String getUserPassword(){		
  		return preferences.getString(Session.PREF_USER_PASSWORD, Session.PREF_USER_PASSWORD_DEFAULT);
  	}
    
    public static void setUserPassword(String user_password){
  		if(!getUserPassword().equals(user_password)){
  			preferences.edit().putString(Session.PREF_USER_PASSWORD, user_password).commit();
  		}
  	}
  	
  	public static void removeLogin(){  		
  		preferences.edit().remove(Session.PREF_USER_LOGIN).commit();
  	}
  	
  	public static void removePassword(){  		
  		preferences.edit().remove(Session.PREF_USER_PASSWORD).commit();
  	}
  	
  	public static void removeCookies(){  		
  		preferences.edit().remove(Session.PREF_COOKIES).commit();
  	}
  	
  	public static boolean isCookieExists(){
  		return preferences.contains(Session.PREF_COOKIES);
  	}
  	
  	public static void checkAutorisation(final Context context, final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener){
  					
  		Connection.protocolConnection(context, fragmentManager, container, new Connection.ProtocolListener() {
			
			@Override
			public void onCanceled() {
				
			}
			
			@Override
			public void isCompleted() {
				
				if(Session.isCookieExists()){
					doCheckAutorisationRequest(context,fragmentManager,container,protocolListener);
				}else{
					doGetNewCookieRequest(context,fragmentManager,container,protocolListener);
				}
		  		
			}	
		});
  		
  		 		
  	}
  	
  	protected static boolean doCheckAutorisationRequest(final Context context, final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener) {

    	final String tag = TAG+" doCheckAutorisationRequest ";  
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Проверка активности сессии ...");
    	pDialog.setCancelable(false);
    	
    	pDialog.show();
    	    	
    	StringRequest request = new StringRequest(Method.GET,
    			getCheckAutorisationUrl(),
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                    	
    	                    	pDialog.hide();
    	                    	
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        
    	                        if( (response==null)||(response.isEmpty())){
    	                        	
    	                        	//Cookie is not actual    	                        	
    	                        	Session.removeCookies();
    	                        		
    	                        	//Request without cookies, to get new cookie
    	                        	Session.checkAutorisation(context, fragmentManager, container, protocolListener);
    	                        	
    	                        	return;
    	                        }
    	                        
    	                        try {
									XmlPullParser xpp = Session.prepareXpp(response);
									
									while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
										Log.d(TAG, "while xpp.type="+xpp.getEventType()+" xpp.name="+xpp.getName() );
										if((xpp.getEventType()==XmlPullParser.START_TAG)&&(xpp.getName().equals("div"))){
											Log.d(TAG, "<div> found");
											for (int i = 0; i < xpp.getAttributeCount(); i++) {
												Log.d(TAG, "for attr["+i+"].name="+xpp.getAttributeName(i)+" attr["+i+"].value="+xpp.getAttributeValue(i));
									            if( (xpp.getAttributeName(i).equals("class"))&&(xpp.getAttributeValue(i).equals("b-autorisation__user-login-label")) ){
									            	Log.d(TAG, "<div class=\"b-autorisation__user-login-label\"> found");
									            	xpp.next();
									                if(xpp.getEventType()==XmlPullParser.TEXT){
									                	Log.d(TAG, "onResponse login_text="+xpp.getText());
									                	
									                	protocolListener.isCompleted();
									                	return;
									                	
									                }else{
									                	Log.e(TAG, "onResponse No text inside <div class=autorisation__user-login-label>");									                	
									                }
									                
									            }
									        }
											
										}
										
										xpp.next();
									}
									
									Log.e(TAG, "onResponse <div class=\"autorisation__user-login-label\"> not found");
									Log.e(TAG, "onResponse явная хрень. Такого не должно быть. Сервер изменил ответ. Нужно создать новый парсер");
									
									Toast.makeText(context, "Парсер не нашел искомое значение. Значит сервер изменил ответ. Нужно создать новый парсер", Toast.LENGTH_LONG).show();
									
								} catch (XmlPullParserException e) {									
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                    	Log.e(TAG,tag+"onErrorResponse "+error.toString());
    	                    	
    	                    	pDialog.hide();
    	                    	
    	                        //createSessionLoginFragment(context,fragmentManager,container,protocolListener);
    	                    	Toast.makeText(context, "Volley error. Сервер не найден, или проблемы с сеть", Toast.LENGTH_LONG).show();
    	                    }
    	                }){
    		
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                Session.addCookiesToHeader(headers);
                
                return headers;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

        return false;
    }
  	
  	protected static boolean doGetNewCookieRequest(final Context context, final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener) {

    	final String tag = TAG+" doGetNewCookieRequest ";  
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Получение новой сессии ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
    	//Cookie is not actual    	                        	
    	Session.removeCookies();
    	    	
    	StringRequest request = new StringRequest(Method.GET,
    			getCheckAutorisationUrl(),
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        pDialog.hide();
    	                        Session.createSessionLoginFragment(context, fragmentManager, container, protocolListener);
    	                        return;
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                    	Log.e(TAG,tag+"onErrorResponse "+error.toString()); 
    	                    	pDialog.hide();
    	                    	Toast.makeText(context, "Volley error. Сервер не найден, или проблемы с сеть", Toast.LENGTH_LONG).show();
    	                    }
    	                }){
    		
    		@Override
    	    protected Response<String> parseNetworkResponse(NetworkResponse response) {
    	        
    			Session.parseCookies(response.headers);
    	            			
    	        return super.parseNetworkResponse(response);
    	    }
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

        return false;
    }
  	
  	public static FragmentLogin createSessionLoginFragment(final Context context,final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("SessionLogin").isVisible()){
                return (FragmentLogin)fragmentManager.findFragmentByTag("SessionLogin");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

            FragmentLogin sessionloginfragment = FragmentLogin.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, sessionloginfragment, "SessionLogin");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fragmentTransaction.addToBackStack("SessionLogin");
            fragmentTransaction.commit();
            
            return sessionloginfragment;
        }
    }
  	
  	public static FragmentRegister createSessionRegisterFragment(final Context context,final FragmentManager fragmentManager, final int container,final Connection.ProtocolListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("SessionRegister").isVisible()){
                return (FragmentRegister)fragmentManager.findFragmentByTag("SessionRegister");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

            FragmentRegister sessionloginfragment = FragmentRegister.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, sessionloginfragment, "SessionRegister");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fragmentTransaction.addToBackStack("SessionRegister");
            fragmentTransaction.commit();
            
            return sessionloginfragment;
        }
    }
  	
  	//--------------------Cookies-------------------------------------
  	
  	public static boolean parseCookies(Map<String, String> headers) {
  		
        if ( headers.containsKey(HEADERS_KEY_SETCOOKIES) ) {
        	
	        	//Save all cookies in preferences
	            Log.d(TAG, "parseCookies Set-Cookie found. All cookies saved in preferences");
	            preferences.edit().putString(PREF_COOKIES, headers.get(HEADERS_KEY_SETCOOKIES)).commit();
        		
	            return true;
	            
	            /*//Try to get login and password from cookies
        		String cookies_string = headers.get(HEADERS_KEY_SETCOOKIES);
                //Take name and password if consists
                if (cookies_string.length() > 0) {
                    String[]  cookies_array = cookies_string.split(";");
                                        
                    for(String cookie_string: cookies_array ){
                    	String[] cookie_pair = cookie_string.split("=");
                    	
                    	if(cookie_pair[0].equals(COOKIE_LOGIN)&&(!cookie_pair[1].equals("+"))){
                    		Log.d(TAG, "parseCookies login="+cookie_pair[1]);                    		
                    		Session.setUserLogin(cookie_pair[1]);
                    		login=true;
                    	}
                    	
                    	
                    	if(cookie_pair[0].equals(COOKIE_PASSWORD)&&(!cookie_pair[1].equals("+"))){
                    		Log.d(TAG, "parseCookies password="+cookie_pair[1]);
                    		Session.setUserPassword(cookie_pair[1]);
                    		password=true;
                    	}                    	
                    }
                }*/
                               
         }else{
        	 Log.d(TAG, "parseCookies Set-Cookie not found");
        	 
        	 return false;
         }
        
    }

    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public static void addCookiesToHeader(Map<String, String> headers) {
        
    	String cookies = preferences.getString(PREF_COOKIES, PREF_COOKIES_DEFAULT);
        
    	if ((cookies!=null)&&(cookies.length()>0)) {
            
            headers.put(HEADERS_KEY_COOKIE, cookies);
            Log.d(TAG, "addCookiesToHeader. Headers with added cookies:"+headers.toString());
        }else{
        	Log.d(TAG, "addCookiesToHeader. Not found cookie. Cookie not added");
        }
    	
    	
    }
    
    //------------------------XmlParsing---------------------
    
    public static XmlPullParser prepareXpp(String string) throws XmlPullParserException {
		  
		  // получаем фабрику
		  XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		  
		  // включаем поддержку namespace (по умолчанию выключена)
		  factory.setNamespaceAware(true);
		  // создаем парсер
		  XmlPullParser xpp = factory.newPullParser();
		  // даем парсеру на вход Reader
		  xpp.setInput( new StringReader(string) );
		  
		  return xpp;
	}

  //---------------------Check Internet Requests-----------------------
    
  	public static void doCheckInternetRequest(Context context, final CheckInternetListener listener) {

      	final String tag = TAG+" doCheckInternetRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Проверка интернета ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
      	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getCheckInternetUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+"onResponse" + response);
      	                        
      	                        pDialog.hide();
      	                        
      	                        if((response==null) && response.isEmpty()){
      	                        	listener.isOffline();
      	                        	return;
      	                        }
      	                        
      	                        try {
									JSONObject json=new JSONObject(response);
									
									if(json.has("status") && json.getBoolean("status")){
										listener.isOnline();										
									}else{
										listener.isOffline();
									}
									
								} catch (JSONException e) {e.printStackTrace();}
      	                        
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        listener.isOffline();      	                        
      	                    }
      	                }){
      		
      		
      		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                  HashMap<String, String> headers = new HashMap<String, String>();
                  
                  headers.put("Content-Type", "application/x-www-form-urlencoded");
                  Session.addCookiesToHeader(headers);
                  
                  return headers;
            }
      		
      	};
      	 
      	request.setTag(tag);
      	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
  	
  	public interface CheckInternetListener{
        public void isOnline();
        public void isOffline();
    }
  	
  	//---------------------Switch Internet Requests-----------------------
    
  	public static void doSwitchInternetRequest(Context context, final CheckInternetListener listener) {

      	final String tag = TAG+" doSwitchInternetRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Включение/Отключение интернета ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
      	
      	StringRequest request = new StringRequest(Method.POST,
      			Session.getSwitchInternetUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        
      	                        pDialog.hide();
      	                        
      	                        if((response==null) || response.isEmpty()){
      	                        	Log.d(TAG, tag+" onResponse response = empty or null");
      	                        	listener.isOffline();
      	                        	return;
      	                        }
      	                        
      	                        try {
									JSONObject json=new JSONObject(response);
									
									if(json.has("status") && json.getBoolean("status")){
										listener.isOnline();										
									}else{
										listener.isOffline();
									}
									
								} catch (JSONException e) {e.printStackTrace();}
      	                        
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        listener.isOffline();      	                        
      	                    }
      	                }){
      		
      		
      		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                  HashMap<String, String> headers = new HashMap<String, String>();
                  
                  headers.put("Content-Type", "application/x-www-form-urlencoded");
                  Session.addCookiesToHeader(headers);
                  
                  return headers;
            }
      		
      	};
      	 
      	request.setTag(tag);
      	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
  	
//---------------------Logout Requests-----------------------
    
  	public static void doLogoutRequest(final Context context, final FragmentManager fragmentManager, final int container, final LogoutListener logoutlistener) {

      	final String tag = TAG+" doLogoutRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Закрытие сессии ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getLogoutUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        pDialog.hide();
      	                        logoutlistener.onLogout();
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                    }
      	                }){
      		
      		
      		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                  HashMap<String, String> headers = new HashMap<String, String>();
                  
                  headers.put("Content-Type", "application/x-www-form-urlencoded");
                  Session.addCookiesToHeader(headers);
                  
                  return headers;
            }
      		
      	};
      	 
      	request.setTag(tag);
      	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
  	
  	public interface LogoutListener{
        public void onLogout();
    }
  	
  	
}
