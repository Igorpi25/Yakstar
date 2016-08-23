package com.ivanov.tech.session;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
import com.ivanov.tech.session.Session.RequestListener;
import com.ivanov.tech.session.demo.FragmentDemo;

import android.app.Activity;
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

    static private SharedPreferences preferences=null;
    
    //------------Prefs----------------
    
    public static final String PREF_USER_LOGIN="PREF_USER_LOGIN";
    public static final String PREF_USER_LOGIN_DEFAULT=null;
    
    public static final String PREF_USER_PASSWORD="PREF_USER_PASSWORD";
    public static final String PREF_USER_PASSWORD_DEFAULT=null;
    
    public static final String PREF_INFO_JSON="PREF_INFO";
    public static final String PREF_INFO_JSON_DEFAULT=null;

    public static final String PREF_COOKIES="PREF_COOKIES";
    public static final String PREF_COOKIES_DEFAULT=null;
    
    public static final String PREF_TARIFS_JSON="PREF_TARIFS";
    public static final String PREF_TARIFS_JSON_DEFAULT=null;
    
    public static final String PREF_REGISTER_JSON="PREF_REGISTERJSON";
    public static final String PREF_REGISTER_JSON_DEFAULT=null;

    public static final String PREF_REGISTERED_MESSAGE="PREF_REGISTEREDMESSAGE";
    public static final String PREF_REGISTERED_MESSAGE_DEFAULT=null;
    
    public static final String PREF_INTERNET_STATE="PREF_INTERNET_STATE";
    public static final boolean PREF_INTERNET_STATE_DEFAULT=false;
    
    public static final String PREF_REGDATA_JSON="PREF_REGDATA_JSON";
    public static final String PREF_REGDATA_JSON_DEFAULT=null;
    
    public static final String PREF_CHANGE_TARIF_STATUS_JSON="PREF_CHANGE_TARIF_STATUS_JSON";
    public static final String PREF_CHANGE_TARIF_STATUS_JSON_DEFAULT=null;
    
    public static final String PREF_RULES_JSON="PREF_RULES_JSON";
    public static final String PREF_RULES_JSON_DEFAULT=null;
    
    public static final String PREF_AGREEMENT_JSON="PREF_AGREEMENT_JSON";
    public static final String PREF_AGREEMENT_JSON_DEFAULT=null;
    
    //----------------URLs--------------------------------
    
    public static final String PREF_LOGOUT_URL="PREF_LOGOUTURL";
    public static final String PREF_CHECK_AUTORISATION_URL="PREF_CHECKAUTORISATIONURL";
    public static final String PREF_LOGIN_URL="PREF_LOGINURL";
    
    public static final String PREF_CHECK_INTERNET_URL="PREF_CHECK_INTERNETURL";
    public static final String PREF_SWITCH_INTERNET_URL="PREF_SWITCH_INTERNETURL";
    
    public static final String PREF_TARIF_URL="PREF_TARIFURL";
    public static final String PREF_CAPTCHA_URL="PREF_CAPTCHA_URL";
    
    public static final String PREF_RULES_URL="PREF_RULES_URL";
    public static final String PREF_AGREEMENT_URL="PREF_AGREEMENT_URL";
    
    public static final String PREF_REGISTER_URL="PREF_REGISTERURL";
    public static final String PREF_INFO_URL="PREF_INFOURL";
    public static final String PREF_PAYMENT_CARDACT_URL="PREF_PAYMENT_CARDACT_URL";
    public static final String PREF_PAYMENT_VISA_URL="PREF_PAYMENT_VISA_URL";
    public static final String PREF_CHANGE_REG_DATA_URL="PREF_CHANGE_REG_DATA_URL";
    public static final String PREF_CHANGE_REG_DATA_INIT_URL="PREF_CHANGE_REG_DATA_INIT_URL";    
    public static final String PREF_CHANGE_TARIF_URL="PREF_CHANGE_TARIF_URL";
    public static final String PREF_CHANGE_TARIF_STATUS_URL="PREF_CHANGE_TARIF_STATUS_URL";
    
    //---------------Headers-----------------------
    
    public static final String HEADERS_KEY_SETCOOKIES = "Set-Cookie";
    public static final String HEADERS_KEY_COOKIE = "Cookie";
    
    //---------------GetUrl------------------------------
    
    public static String getLogoutUrl(){
    	return preferences.getString(Session.PREF_LOGOUT_URL, null);    	
    }
    
    public static String getCheckAutorisationUrl(){
    	return preferences.getString(Session.PREF_CHECK_AUTORISATION_URL, null);    	
    }
    
    public static final String getLoginUrl(){
    	return preferences.getString(Session.PREF_LOGIN_URL, null);    	
    }
    
    public static final String getCheckInternetUrl(){
    	return preferences.getString(Session.PREF_CHECK_INTERNET_URL, null);    	
    }

    public static final String getSwitchInternetUrl(){
    	return preferences.getString(Session.PREF_SWITCH_INTERNET_URL, null);    	
    }
    
    public static final String getTarifUrl(){
    	return preferences.getString(Session.PREF_TARIF_URL, null);    	
    }

    public static final String getCaptchaUrl(){
    	return preferences.getString(Session.PREF_CAPTCHA_URL, null);    	
    }
    
    public static final String getRulesUrl(){
    	return preferences.getString(Session.PREF_RULES_URL, null);    	
    }
    
    public static final String getAgreementUrl(){
    	return preferences.getString(Session.PREF_AGREEMENT_URL, null);    	
    }

    public static final String getRegisterUrl(){
    	return preferences.getString(Session.PREF_REGISTER_URL, null);    	
    }
    
    public static final String getInfoUrl(){
    	return preferences.getString(Session.PREF_INFO_URL, null);    	
    }
    
    public static final String getPaymentCardactUrl(){
    	return preferences.getString(Session.PREF_PAYMENT_CARDACT_URL, null);    	
    }

    public static final String getPaymentVisaUrl(){
    	return preferences.getString(Session.PREF_PAYMENT_VISA_URL, null);    	
    }
    
    public static final String getChangeRegDataUrl(){
    	return preferences.getString(Session.PREF_CHANGE_REG_DATA_URL, null);    	
    }

    public static final String getChangeRegDataInitUrl(){
    	return preferences.getString(Session.PREF_CHANGE_REG_DATA_INIT_URL, null);    	
    }
    
    public static final String getChangeTarifUrl(){
    	return preferences.getString(Session.PREF_CHANGE_TARIF_URL, null);    	
    }
    
    public static final String getChangeTarifStatusUrl(){
    	return preferences.getString(Session.PREF_CHANGE_TARIF_STATUS_URL, null);    	
    }

    //--------------Set-Get Prefs-----------------
        
    public static String getUserLogin(){		
  		return preferences.getString(Session.PREF_USER_LOGIN, Session.PREF_USER_LOGIN_DEFAULT);
  	}
    
    public static void setUserLogin(String user_login){
  		
  		preferences.edit().putString(Session.PREF_USER_LOGIN, user_login).commit();
  		
  	}
    
    public static String getUserPassword(){		
  		return preferences.getString(Session.PREF_USER_PASSWORD, Session.PREF_USER_PASSWORD_DEFAULT);
  	}
    
    public static void setUserPassword(String user_password){
  		
  		preferences.edit().putString(Session.PREF_USER_PASSWORD, user_password).commit();
  		
  	}
  	
  	public static void removeLogin(){  		
  		preferences.edit().remove(Session.PREF_USER_LOGIN).commit();
  	}
  	
  	public static void removePassword(){  		
  		preferences.edit().remove(Session.PREF_USER_PASSWORD).commit();
  	}
  	
  	public static String getCookies(){		
  		return preferences.getString(Session.PREF_COOKIES, Session.PREF_COOKIES_DEFAULT);
  	}
  	
  	public static void removeCookies(){  		
  		preferences.edit().remove(Session.PREF_COOKIES).commit();
  	}
  	
  	public static boolean isCookieExists(){
  		return preferences.contains(Session.PREF_COOKIES);
  	}
  	
  	public static String getTarifsJson(){
  		return preferences.getString(Session.PREF_TARIFS_JSON, Session.PREF_TARIFS_JSON_DEFAULT);
  	}
  	
  	public static void setTarifsJson(String value){
  		
  		preferences.edit().putString(Session.PREF_TARIFS_JSON, value).commit();
  		
  	}
  	
  	public static void removeTarifsJson(){  		
  		preferences.edit().remove(Session.PREF_TARIFS_JSON).commit();
  	}

  	public static boolean isTarifsJsonExists(){
  		return preferences.contains(Session.PREF_TARIFS_JSON);
  	}
  	  	
  	public static String getRegisterJson(){
  		return preferences.getString(Session.PREF_REGISTER_JSON, Session.PREF_REGISTER_JSON_DEFAULT);
  	}
  	
  	public static void setRegisterJson(String value){
  		
  		preferences.edit().putString(Session.PREF_REGISTER_JSON, value).commit();
  		
  	}
  	
  	public static void removeRegisterJson(){  		
  		preferences.edit().remove(Session.PREF_REGISTER_JSON).commit();
  	}
  	
  	public static boolean isRegisterJsonExists(){
  		return preferences.contains(Session.PREF_REGISTER_JSON);
  	}
  	
  	public static String getInfoJson(){		
  		return preferences.getString(Session.PREF_INFO_JSON, Session.PREF_INFO_JSON_DEFAULT);
  	}
    
    public static void setInfoJson(String value){
  		preferences.edit().putString(Session.PREF_INFO_JSON, value).commit();  		
  	}

  	public static void removeInfoJson(){  		
  		preferences.edit().remove(Session.PREF_INFO_JSON).commit();
  	}
  	
  	public static boolean isInfoJsonExists(){
  		return preferences.contains(Session.PREF_INFO_JSON);
  	}
  	
  	public static boolean getInternetState(){
  		return preferences.getBoolean(Session.PREF_INTERNET_STATE,Session.PREF_INTERNET_STATE_DEFAULT);
  	}
  	
  	public static void setInternetState(boolean value){
  		preferences.edit().putBoolean(Session.PREF_INTERNET_STATE,value).commit();
  	}
  	
  	public static String getRegDataJson(){		
  		return preferences.getString(Session.PREF_REGDATA_JSON, Session.PREF_REGDATA_JSON_DEFAULT);
  	}
    
    public static void setRegDataJson(String value){
  		preferences.edit().putString(Session.PREF_REGDATA_JSON, value).commit();  		
  	}
    
    public static String getChangeTarifStatusJson(){		
  		return preferences.getString(Session.PREF_CHANGE_TARIF_STATUS_JSON, Session.PREF_CHANGE_TARIF_STATUS_JSON_DEFAULT);
  	}
    
    public static void setChangeTarifStatusJson(String value){
  		preferences.edit().putString(Session.PREF_CHANGE_TARIF_STATUS_JSON, value).commit();  		
  	}

    public static String getRulesJson(){		
  		return preferences.getString(Session.PREF_RULES_JSON, Session.PREF_RULES_JSON_DEFAULT);
  	}
    
    public static void setRulesJson(String value){
  		preferences.edit().putString(Session.PREF_RULES_JSON, value).commit();  		
  	}

    public static String getAgreementJson(){		
  		return preferences.getString(Session.PREF_AGREEMENT_JSON, Session.PREF_AGREEMENT_JSON_DEFAULT);
  	}
    
    public static void setAgreementJson(String value){
  		preferences.edit().putString(Session.PREF_AGREEMENT_JSON, value).commit();  		
  	}
    
  	//--------Message that reciedved after registration-------------
  	
  	public static String getRegisteredMessage(){
  		return preferences.getString(Session.PREF_REGISTERED_MESSAGE, Session.PREF_REGISTERED_MESSAGE_DEFAULT);
  	}

  	public static void setRegisteredMessage(String value){
  		
  		preferences.edit().putString(Session.PREF_REGISTERED_MESSAGE, value).commit();
  		
  	}
  	
  	public static void removeRegisteredMessage(){  		
  		preferences.edit().remove(Session.PREF_REGISTERED_MESSAGE).commit();
  	}
  	
  	public static boolean isRegisteredMessageExists(){
  		return preferences.contains(Session.PREF_REGISTERED_MESSAGE);
  	}
  	
  	//-----------------Methods-----------------------
  	
    public static void Initialize(Context context, 
    		String url_check_autorisation, 
    		String url_login,  
    		String url_logout,
    		String url_check_internet, 
    		String url_switch_internet,
    		String url_tarif,
    		String url_captcha,
    		String url_rules,
    		String url_agriment,
    		String url_register,
    		String url_info,
    		String url_payment_cardact,
    		String url_payment_visa,
    		String url_change_reg_data_init,
    		String url_change_reg_data,
    		String url_change_tarif,
    		String url_change_tarif_status){
    	if(preferences==null){
    		preferences=context.getApplicationContext().getSharedPreferences(PREF, 0);
    	}
    	preferences.edit().putString(Session.PREF_CHECK_AUTORISATION_URL, url_check_autorisation).commit();
    	preferences.edit().putString(Session.PREF_LOGIN_URL, url_login).commit();
    	preferences.edit().putString(Session.PREF_LOGOUT_URL, url_logout).commit();
    	
    	preferences.edit().putString(Session.PREF_CHECK_INTERNET_URL, url_check_internet).commit();
    	preferences.edit().putString(Session.PREF_SWITCH_INTERNET_URL, url_switch_internet).commit();
    	
    	
    	preferences.edit().putString(Session.PREF_TARIF_URL, url_tarif).commit();
    	preferences.edit().putString(Session.PREF_CAPTCHA_URL, url_captcha).commit();
    	preferences.edit().putString(Session.PREF_RULES_URL, url_rules).commit();
    	preferences.edit().putString(Session.PREF_AGREEMENT_URL, url_agriment).commit();
    	preferences.edit().putString(Session.PREF_REGISTER_URL, url_register).commit();
    	
    	preferences.edit().putString(Session.PREF_INFO_URL, url_info).commit();
    	preferences.edit().putString(Session.PREF_PAYMENT_CARDACT_URL, url_payment_cardact).commit();
    	preferences.edit().putString(Session.PREF_PAYMENT_VISA_URL, url_payment_visa).commit();
    	
    	preferences.edit().putString(Session.PREF_CHANGE_REG_DATA_INIT_URL, url_change_reg_data_init).commit();
    	preferences.edit().putString(Session.PREF_CHANGE_REG_DATA_URL, url_change_reg_data).commit();
    	
    	preferences.edit().putString(Session.PREF_CHANGE_TARIF_URL, url_change_tarif).commit();
    	preferences.edit().putString(Session.PREF_CHANGE_TARIF_STATUS_URL, url_change_tarif_status).commit();
    	
    }
   	
  	public static void checkAutorisation(final Context context, final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener listener){
  					
  		if(Session.isCookieExists()){
			doCheckAutorisationRequest(context,fragmentManager,container,listener);
		}else{
			doGetNewCookieRequest(context,fragmentManager,container,listener);
		}
		
  	}

    public static void Logout(final Context context, final FragmentManager fragmentManager, final int container){
    	Session.doLogoutRequest(context, fragmentManager, container, new RequestListener(){

			@Override
			public void onResponsed() {
				Log.d(TAG, "onClick logout onLogout");
				Session.removeCookies();
				Session.removeLogin();
				Session.removePassword();
				Session.removeRegisteredMessage();
				Session.removeRegisterJson();
				Session.removeTarifsJson();
				Session.removeRegisterJson();
				Session.removeRegisteredMessage();
				Session.removeInfoJson();
				
				Session.checkAutorisation(context, fragmentManager, container, new CheckAuthorizationListener() {
					
					@Override
					public void isAuthorized() {
						//Приложение не запустится, пока пользователь не будет авторизован
						fragmentManager.beginTransaction()
		                .replace(R.id.main_container, new FragmentDemo())
		                .commit();		
					}
					
					@Override
					public void isLogedout() {
				        Session.createSessionLoginFragment(context, fragmentManager, container, this);
					}

					
					@Override
					public boolean enableDialogs() {
						return true;
					}
									
				});
			}
			
		});
    }
    
    public static void killApp(){
    	System.runFinalizersOnExit(true);

        System.exit(0);
    }
  	
    //-----------------Fragments------------------------------
  	
  	public static FragmentLogin createSessionLoginFragment(final Context context,final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener listener){

        try{
            if(fragmentManager.findFragmentByTag("SessionLogin").isVisible()){
                return (FragmentLogin)fragmentManager.findFragmentByTag("SessionLogin");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

            FragmentLogin fragment = FragmentLogin.newInstance(listener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "SessionLogin");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fragmentTransaction.addToBackStack("SessionLogin");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegisterFirst createSessionRegisterFirstFragment(final Context context,final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("RegisterFirst").isVisible()){
                return (FragmentRegisterFirst)fragmentManager.findFragmentByTag("RegisterFirst");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

            FragmentRegisterFirst fragment = FragmentRegisterFirst.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "RegisterFirst");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("RegisterFirst");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegisterSecond createSessionRegisterSecondFragment(final Context context,final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("RegisterSecond").isVisible()){
                return (FragmentRegisterSecond)fragmentManager.findFragmentByTag("RegisterSecond");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentRegisterSecond fragment = FragmentRegisterSecond.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "RegisterSecond");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("RegisterSecond");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegisterLast createSessionRegisterLastFragment(final Context context,final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("RegisterLast").isVisible()){
                return (FragmentRegisterLast)fragmentManager.findFragmentByTag("RegisterLast");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentRegisterLast fragment = FragmentRegisterLast.newInstance(protocolListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "RegisterLast");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("RegisterLast");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegisterSuccess createSessionRegisterSuccessFragment(final Context context,final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener protocolListener){

        try{
            if(fragmentManager.findFragmentByTag("RegisterSuccess").isVisible()){
                return (FragmentRegisterSuccess)fragmentManager.findFragmentByTag("RegisterSuccess");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentRegisterSuccess fragment = FragmentRegisterSuccess.newInstance(protocolListener);
            
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "RegisterSuccess");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentPaymentCardact createPaymentCardactFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("PaymentCardact").isVisible()){
                return (FragmentPaymentCardact)fragmentManager.findFragmentByTag("PaymentCardact");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentPaymentCardact fragment = FragmentPaymentCardact.newInstance();
            
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "PaymentCardact");            
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("RegisterLast");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentPaymentVisa createPaymentVisaFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("PaymentVisa").isVisible()){
                return (FragmentPaymentVisa)fragmentManager.findFragmentByTag("PaymentVisa");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentPaymentVisa fragment = FragmentPaymentVisa.newInstance();
            
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "PaymentVisa");            
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("PaymentVisa");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentPaymentRoot createPaymentRootFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("PaymentRoot").isVisible()){
                return (FragmentPaymentRoot)fragmentManager.findFragmentByTag("PaymentRoot");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentPaymentRoot fragment = FragmentPaymentRoot.newInstance();
            
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "PaymentRoot");            
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("PaymentRoot");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static void popFullBackStack(final FragmentManager fragmentManager){
  		fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
  	}
  	
  	public static FragmentError createErrorFragment(final Context context,final FragmentManager fragmentManager, final int container, final int code, final int title, final int message, final CloseListener listener){

        try{
            if(fragmentManager.findFragmentByTag("Error").isVisible()){
                return (FragmentError)fragmentManager.findFragmentByTag("Error");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentError fragment = FragmentError.newInstance(code,title,message,listener);
            
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "Error");            
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("Error");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentError createErrorTerminateFragment(final Context context,final FragmentManager fragmentManager, final int container, final int code, final int title, final int message){

        try{
            if(fragmentManager.findFragmentByTag("Error").isVisible()){
                return (FragmentError)fragmentManager.findFragmentByTag("Error");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentError fragment = FragmentError.newInstance(code,title,message, new CloseListener(){

				@Override
				public void onClosed() {
					Session.killApp();
				}
        		
        	});
            
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "Error");            
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("Error");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegdata createAgreementFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("Agreement").isVisible()){
                return (FragmentRegdata)fragmentManager.findFragmentByTag("Agreement");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentRegdata fragment = FragmentRegdata.newInstance();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "Agreement");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("Agreement");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegdataEdit createAgreementEditFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("AgreementEdit").isVisible()){
                return (FragmentRegdataEdit)fragmentManager.findFragmentByTag("AgreementEdit");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentRegdataEdit fragment = FragmentRegdataEdit.newInstance();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "AgreementEdit");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("AgreementEdit");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentTarifChange createChangeTarifFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("AgreementEdit").isVisible()){
                return (FragmentTarifChange)fragmentManager.findFragmentByTag("AgreementEdit");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentTarifChange fragment = FragmentTarifChange.newInstance();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "AgreementEdit");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("AgreementEdit");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentPasswordChange createPasswordChangeFragment(final Context context,final FragmentManager fragmentManager, final int container){

        try{
            if(fragmentManager.findFragmentByTag("PasswordChange").isVisible()){
                return (FragmentPasswordChange)fragmentManager.findFragmentByTag("PasswordChange");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentPasswordChange fragment = FragmentPasswordChange.newInstance();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "PasswordChange");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("PasswordChange");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	
  	public static FragmentRegisterInfo createRegisterInfoFragment(final Context context,final FragmentManager fragmentManager, final int container, final String title, final String body){

        try{
            if(fragmentManager.findFragmentByTag("RegisterInfo").isVisible()){
                return (FragmentRegisterInfo)fragmentManager.findFragmentByTag("RegisterInfo");
            }else{
                throw (new NullPointerException());
            }
        }catch(NullPointerException e) {

        	FragmentRegisterInfo fragment = FragmentRegisterInfo.newInstance(title,body);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragment, "RegisterInfo");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("RegisterInfo");
            fragmentTransaction.commit();
            
            return fragment;
        }
    }
  	//------------------Cookies-------------------------------------
  	
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
  	
    public static void addCookiesToHeader(Map<String, String> headers) {
        
    	String cookies = preferences.getString(PREF_COOKIES, PREF_COOKIES_DEFAULT);
        
    	if ((cookies!=null)&&(cookies.length()>0)) {
            
            headers.put(HEADERS_KEY_COOKIE, cookies);
            Log.d(TAG, "addCookiesToHeader. Headers with added cookies:"+headers.toString());
        }else{
        	Log.d(TAG, "addCookiesToHeader. Not found cookie. Cookie not added");
        }
    	
    	
    }
    
    //------------------Xml Parsing---------------------
    
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

    //------------------Agreement and Rules Requests--------------------------
    
    public static void doRulesRequest(final Context context, final FragmentManager fragmentManager, final int container, final DialogRequestListener listener) {

      	final String tag = TAG+" doRulesRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Загрузка документа...");
    	pDialog.setCancelable(false);    	
    	if( (listener!=null)&&(listener.enableDialogs()) )pDialog.show();
    	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getRulesUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        pDialog.hide();
      	                        
      	                        if( (response==null)||(response.isEmpty()) ){
      	                        	
      	                        	if( (listener!=null)&&(listener.enableDialogs()) ){
      	                        		Session.createErrorFragment(context, fragmentManager, container, 61, R.string.error_611_title, R.string.error_611_message, null);
      	                        	}
      	                        	return;
      	                        }
      	                        
      	                        Session.setRulesJson(response);
      	                        
      	                        if(listener!=null)listener.onResponsed();
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        
      	                        if( (listener!=null)&&(listener.enableDialogs()) ){
	                        		Session.createErrorFragment(context, fragmentManager, container, 61, R.string.error_612_title, R.string.error_612_message, null);
	                        	}
      	                        
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
    
    public static void doAgreementRequest(final Context context, final FragmentManager fragmentManager, final int container, final DialogRequestListener listener) {

      	final String tag = TAG+" doAgreementRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Загрузка документа...");
    	pDialog.setCancelable(false);    	
    	if( (listener!=null)&&(listener.enableDialogs()) )pDialog.show();
    	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getAgreementUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        pDialog.hide();
      	                        
      	                        if( (response==null)||(response.isEmpty()) ){
      	                        	
      	                        	if( (listener!=null)&&(listener.enableDialogs()) ){
      	                        		Session.createErrorFragment(context, fragmentManager, container, 62, R.string.error_621_title, R.string.error_621_message, null);
      	                        	}
      	                        	return;
      	                        }
      	                        
      	                        Session.setAgreementJson(response);
      	                        
      	                        if(listener!=null)listener.onResponsed();
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        
      	                        if( (listener!=null)&&(listener.enableDialogs()) ){
	                        		Session.createErrorFragment(context, fragmentManager, container, 62, R.string.error_622_title, R.string.error_622_message, null);
	                        	}
      	                        
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
    
    //---------------------------Authorization Requests-------------------------
    
  	protected static boolean doCheckAutorisationRequest(final Context context, final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener listener) {

    	final String tag = TAG+" doCheckAutorisationRequest ";  
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Проверка активности сессии ...");
    	pDialog.setCancelable(false);
    	
    	if(listener.enableDialogs()){
    		pDialog.show();
    	}
    	    	
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
    	                        	Session.checkAutorisation(context, fragmentManager, container, listener);
    	                        	
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
									                	
									                	Session.removeRegisteredMessage();
									                	
									                	listener.isAuthorized();
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
    	                    	
    	                    	Session.createErrorFragment(context, fragmentManager, container, 31, R.string.error_31_title, R.string.error_31_message, new CloseListener(){

									@Override
									public void onClosed() {
										Session.killApp();
									}    	                    		
    	                    	});
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
  	
  	protected static boolean doGetNewCookieRequest(final Context context, final FragmentManager fragmentManager, final int container,final CheckAuthorizationListener listener) {

    	final String tag = TAG+" doGetNewCookieRequest ";  
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Получение новой сессии ...");
    	pDialog.setCancelable(false);    	
    	if(listener.enableDialogs()){
    		pDialog.show();
    	}
    	
    	//Cookie is not actual    	                        	
    	Session.removeCookies();
    	    	
    	StringRequest request = new StringRequest(Method.GET,
    			getCheckAutorisationUrl(),
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        pDialog.hide();
    	                        listener.isLogedout();
    	                        return;
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                    	Log.e(TAG,tag+"onErrorResponse "+error.toString()); 
    	                    	pDialog.hide();
    	                    	Session.createErrorFragment(context, fragmentManager, container, 32, R.string.error_32_title, R.string.error_32_message, new CloseListener(){

									@Override
									public void onClosed() {
										Session.killApp();
									}
    	                    		
    	                    	});
    	                    }
    	                    
    	                }){
    		
    		@Override
    	    protected Response<String> parseNetworkResponse(NetworkResponse response) {
    	        
    			
    			boolean iscookieexists=Session.parseCookies(response.headers);
    			
    			if(!iscookieexists){
    				Session.createErrorFragment(context, fragmentManager, container, 41, R.string.error_41_title, R.string.error_41_message, new CloseListener(){

						@Override
						public void onClosed() {
							Session.killApp();
						}
                		
                	});
    			}
    	            			
    	        return super.parseNetworkResponse(response);
    	    }
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

        return false;
    }
  	      	
  	public static void doLoginRequest(final Context context, final FragmentManager fragmentManager, final int container, final String login,final String password,final CheckAuthorizationListener listener) {

    	String tag = TAG+" doLoginRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	Session.setUserLogin(login);
    	Session.setUserPassword(password);    	
    	    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Отправка логина и пароля ...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
    	StringRequest request = new StringRequest(Request.Method.POST,
    			Session.getLoginUrl(),
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, "onResponse 1 " + response);
    	                        pDialog.hide();
    	                        Session.checkAutorisation(context, fragmentManager, R.id.main_container, listener);
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "1 Volley.onErrorResponser: " + error.getMessage());
    	                        pDialog.hide();
    	                    }
    	                    
    	                }){
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
    			
    			Log.d(TAG, "getHeaders");
    			
                HashMap<String, String> headers = new HashMap<String, String>();
                
                Session.addCookiesToHeader(headers);
                
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                
                return headers;
            }
    		
    		@Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
    			
                Map<String, String> params = new HashMap<String, String>();
                params.put("login", login);
                params.put("password", password);
                
                Log.d(TAG, "getParams = "+params.toString());
                
                return params;
            }
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
  	public static void doLogoutRequest(final Context context, final FragmentManager fragmentManager, final int container, final RequestListener logoutlistener) {

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
      	                        logoutlistener.onResponsed();
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
  	
  	//---------------------Check Internet Requests-----------------------
    
  	public static void doCheckInternetRequest(final Context context, final FragmentManager fragmentManager, final int container, final CheckInternetListener listener) {

      	final String tag = TAG+" doCheckInternetRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Проверка интернета ...");
    	pDialog.setCancelable(false);    	
    	//pDialog.show();
      	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getCheckInternetUrl(), 
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+"onResponse" + response);
      	                        
      	                        pDialog.hide();
      	                        
      	                        if((response==null) && response.isEmpty()){
      	                        	Session.createErrorFragment(context, fragmentManager, container, 451, R.string.error_451_title, R.string.error_451_message, null);
      	                        	listener.isUnknown();
      	                        	return;
      	                        }
      	                        
      	                        try {
									JSONObject json=new JSONObject(response);
									
									if(json.getBoolean("status")){										
										listener.isOnline();										
									}else{
										listener.isOffline();
									}
									
									Session.setInternetState(json.getBoolean("status"));
									
								} catch (JSONException e) {
									Log.e(TAG, tag+"onResponse JSONException e" + response);
									Session.createErrorFragment(context, fragmentManager, container, 452, R.string.error_452_title, R.string.error_452_message, null);
									listener.isUnknown();
								}
      	                        
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        
      	                        Session.createErrorFragment(context, fragmentManager, container, 37, R.string.error_37_title, R.string.error_37_message, null);
      	                        listener.isUnknown();
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
  	
  	public static void doSwitchInternetRequest(final Context context, final FragmentManager fragmentManager, final int container, final CheckInternetListener listener) {

      	final String tag = TAG+" doSwitchInternetRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Включение/Отключение интернета ...");
    	pDialog.setCancelable(false);    	
//    	pDialog.show();
      	
      	StringRequest request = new StringRequest(Method.POST,
      			Session.getSwitchInternetUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        
      	                        pDialog.hide();
      	                        
      	                        if((response==null) || response.isEmpty()){
      	                        	Log.d(TAG, tag+" onResponse response = empty or null");
      	                        	Session.createErrorFragment(context, fragmentManager, container, 461, R.string.error_461_title, R.string.error_461_message, null);
      	                        	listener.isUnknown();
      	                        	return;
      	                        }
      	                        
      	                        try {
									JSONObject json=new JSONObject(response);
									
									if(json.getBoolean("status")){
										listener.isOnline();										
									}else{
										listener.isOffline();
									}
									
									Session.setInternetState(json.getBoolean("status"));
									
								} catch (JSONException e) {
									Log.e(TAG, tag+" onResponse JSONException e" + response);
									Session.createErrorFragment(context, fragmentManager, container, 462, R.string.error_462_title, R.string.error_462_message, null);
									listener.isUnknown();
								}
      	                        
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        
      	                        Session.createErrorFragment(context, fragmentManager, container, 38, R.string.error_38_title, R.string.error_38_message, null);
      	                        listener.isUnknown();      	                        
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
  	  	
  	//-----------------------Operation Requests------------------------
  	
  	public static void doTarifRequest(final Context context, final FragmentManager fragmentManager, final int container, final RequestListener listener) {

      	final String tag = TAG+" doTarifRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Получение списка тарифов...");
    	pDialog.setCancelable(false);    	
    	pDialog.show();
    	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getTarifUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        pDialog.hide();
      	                        
      	                        Session.setTarifsJson(response);
      	                        listener.onResponsed();
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        
      	                        Session.createErrorTerminateFragment(context, fragmentManager, container, 33, R.string.error_33_title, R.string.error_33_message);
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
  	
  	public static void doInfoRequest(final Context context, final FragmentManager fragmentManager, final int container, final RequestListener listener) {

      	final String tag = TAG+" doInfoRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Запрос информации...");
    	pDialog.setCancelable(false);    	
    	//pDialog.show();
      	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getInfoUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);

      	                        pDialog.hide();
      	                        
      	                        if( (response==null)||(response.isEmpty()) ){
      	                        	Session.createErrorTerminateFragment(context,fragmentManager,441,R.string.error_441_title,R.string.error_441_message,container);
      	                        	return;
      	                        }
      	                              	                        
      	                        Session.setInfoJson(response);
      	                        
      	                        listener.onResponsed();
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        Session.createErrorTerminateFragment(context,fragmentManager,36,R.string.error_36_title,R.string.error_36_message,container);
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
  	
  	public static void doChangeRegDataInitRequest(final Context context, final FragmentManager fragmentManager, final int container, final DialogRequestListener listener) {

    	final String tag = TAG+" doChangeRegDataRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Получение данных пользователя");
    	pDialog.setCancelable(false);    
    	
    	if((listener!=null)&&(listener.enableDialogs()))pDialog.show();
    	
    	StringRequest request = new StringRequest(Method.GET,
    			Session.getChangeRegDataInitUrl(),
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        pDialog.hide();
    	                        
    	                        if( (response==null)||(response.isEmpty()) ){
      	                        	Session.createErrorFragment(context,fragmentManager,container,471,R.string.error_471_title,R.string.error_471_message,new CloseListener(){

										@Override
										public void onClosed() {
											
										}
      	                        		
      	                        	});
      	                        	return;
      	                        }
      	                              	                        
      	                        Session.setRegDataJson(response);
      	                        
      	                        listener.onResponsed();
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "Volley.onErrorResponser: " + error.getMessage());
    	                        pDialog.hide();
    	                        
    	                        Session.createErrorFragment(context,fragmentManager,container,39,R.string.error_39_title,R.string.error_39_message,new CloseListener(){

									@Override
									public void onClosed() {
										
									}
  	                        		
  	                        	});
    	                    }
    	                }){
    		
    		
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
  	
  	public static void doChangeRegDataRequest(final Context context, final FragmentManager fragmentManager, final int container, final HashMap<String, String> params, final RequestListener listener) {

    	final String tag = TAG+" doChangeRegDataRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Изменение данных пользователя...");
    	pDialog.setCancelable(false);    
    	
    	pDialog.show();
    	
    	
    
    	String uri=null;
		try {
			uri = String.format(Session.getChangeRegDataUrl()+"?surname=%1$s&name=%2$s&patronim=%3$s&email=%4$s&phone=%5$s&newPassword=%6$s&passwordRepeat=%7$s&verifiedPassword=%8$s",
					URLEncoder.encode(params.get("surname"), "utf-8"),
					URLEncoder.encode(params.get("name"), "utf-8"),
					URLEncoder.encode(params.get("patronim"), "utf-8"),
					URLEncoder.encode(params.get("email"), "utf-8"),
					URLEncoder.encode(params.get("phone"), "utf-8"),
					URLEncoder.encode(params.get("newPassword"), "utf-8"),
					URLEncoder.encode(params.get("passwordRepeat"), "utf-8"),
					URLEncoder.encode(params.get("verifiedPassword"), "utf-8") );
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
//    	String url_params="?";
//    	for(String key : params.keySet()){
//    		url_params+=key+"="+Charset.forName("UTF-8").encode(params.get(key))+"&";
//    	}    	
//    	url_params = url_params.substring(0, url_params.length()-1);    	
//    	String url=Session.getChangeRegDataUrl()+url_params;
    	
    	Log.d(TAG, tag+" url="+uri);
    	
    	StringRequest request = new StringRequest(Method.GET,
    			uri,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        pDialog.hide();
    	                        
    	                        if( (response==null)||(response.isEmpty()) ){
      	                        	Session.createErrorFragment(context,fragmentManager,container,471,R.string.error_471_title,R.string.error_471_message,null);
      	                        	return;
      	                        }
      	                        
    	                        try{
    	                        	JSONObject json=new JSONObject(response);
    	                        	
    	                        	if( (json.getString("message")!=null)&&(!json.getString("message").isEmpty()) ){
    	                        		Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
    	                        	}
    	                        	
    	                        	if(json.getBoolean("status")){
    	                        		//Session.setDataJson(response);
    	      	                        listener.onResponsed();
    	                        	}
    	                        	
    	                        }catch(JSONException e){
    	                        	Log.e(TAG,tag+" JSONException e="+e);
    	                        	Session.createErrorFragment(context,fragmentManager,container,472,R.string.error_472_title,R.string.error_472_message,null);
    	                        }
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "Volley.onErrorResponser: " + error.getMessage());
    	                        pDialog.hide();
    	                        
    	                        Session.createErrorFragment(context,fragmentManager,container,51,R.string.error_51_title,R.string.error_51_message,new CloseListener(){
									@Override
									public void onClosed() {
										
									}
  	                        	});
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
    	
    }
  	
  	public static void doChangeTarifRequest(final Context context, final FragmentManager fragmentManager, final int container, final String mode, final int tarid, final RequestListener listener) {

    	final String tag = TAG+" doChangeTarifRequest"; 
    	        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Отправление заявки на смену тарифа...");
    	pDialog.setCancelable(false);    
    	
    	pDialog.show();
    	
    	StringRequest request = new StringRequest(Method.GET,
    			Session.getChangeTarifUrl()+"?mode="+mode+"&tarid="+tarid,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+" onResponse " + response);
    	                        pDialog.hide();
    	                        
    	                        if( (response==null)||(response.isEmpty()) ){
      	                        	Session.createErrorFragment(context,fragmentManager,container,481,R.string.error_481_title,R.string.error_481_message,null);
      	                        	return;
      	                        }
      	                              	                        
      	                        try{
      	                        	JSONObject json=new JSONObject(response);
      	                        	
      	                        	//Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
      	                        	
      	                        	if(json.getBoolean("status")){
      	                        		//Заявка на изменение тарифа принята      	                        		
      	                        		Session.doChangeTarifStatusRequest(context, fragmentManager, container, new DialogRequestListener(){

											@Override
											public void onResponsed() {
												listener.onResponsed();
											}

											@Override
											public boolean enableDialogs() {
												// TODO Auto-generated method stub
												return true;
											}
      	                        			
      	                        		});
      	                        	}
      	                        }catch(JSONException e){
      	                        	Session.createErrorFragment(context,fragmentManager,container,482,R.string.error_482_title,R.string.error_482_message,null);
      	                        }
      	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, "Volley.onErrorResponser: " + error.getMessage());
    	                        pDialog.hide();
    	                        
    	                        Session.createErrorFragment(context,fragmentManager,container,52,R.string.error_52_title,R.string.error_52_message,null);
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
    	
    }
  	
  	public static void doChangeTarifStatusRequest(final Context context, final FragmentManager fragmentManager, final int container, final DialogRequestListener listener) {

      	final String tag = TAG+" doChangeTarifStatusRequest"; 
      	        
      	Log.e(TAG,tag);
      	
      	final ProgressDialog pDialog = new ProgressDialog(context);
    	pDialog.setMessage("Получение статуса текущего тарифа...");
    	pDialog.setCancelable(false);    	
    	if((listener!=null)&&(listener.enableDialogs()))pDialog.show();
    	
      	StringRequest request = new StringRequest(Method.GET,
      			Session.getChangeTarifStatusUrl(),
      	                new Response.Listener<String>() {
      	 
      	                    @Override
      	                    public void onResponse(String response) {
      	                        Log.d(TAG, tag+" onResponse " + response);
      	                        pDialog.hide();
      	                        
      	                        if( (response==null)||(response.isEmpty()) ){
    	                        	Session.createErrorFragment(context,fragmentManager,container,491,R.string.error_491_title,R.string.error_491_message,null);
    	                        	return;
    	                        }
      	                        
      	                        Session.setChangeTarifStatusJson(response);
      	                        listener.onResponsed();
      	                    }
      	                    
      	                }, new Response.ErrorListener() {
      	 
      	                    @Override
      	                    public void onErrorResponse(VolleyError error) {
      	                        Log.e(TAG, tag+"Volley.onErrorResponser: " + error.getMessage());
      	                        pDialog.hide();
      	                        
      	                        Session.createErrorTerminateFragment(context, fragmentManager, container, 53, R.string.error_53_title, R.string.error_53_message);
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
  	//-----------------Listeners------------------------
  	
  	public interface CheckAuthorizationListener{
        public void isAuthorized();
        public void isLogedout();
        public boolean enableDialogs();//If set true then dialogs will be showed, otherwise won't be
    }
  	
  	public interface RequestListener{
        public void onResponsed();
    }

  	public interface DialogRequestListener{
        public void onResponsed();
        public boolean enableDialogs();
    }
  	
  	public interface CheckInternetListener{
        public void isOnline();
        public void isOffline();
        public void isUnknown();
    }
  	
  	public interface CloseListener{
        public void onClosed();
    }

}
