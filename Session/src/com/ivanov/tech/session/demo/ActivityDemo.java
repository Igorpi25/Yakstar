package com.ivanov.tech.session.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;

/**
 * Created by Igor on 15.01.15.
 */
public class ActivityDemo extends SherlockFragmentActivity {

	//Urls for Session-demo
	static final String url_testapikey="http://igorpi25.ru/v2/testapikey";
	static final String url_login="http://igorpi25.ru/v2/login";
	static final String url_register="http://igorpi25.ru/v2/register";
        	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        
        //Init app preferences
        Session.Initialize(getApplicationContext(),url_testapikey,url_login,url_register);
        
        showFragmentDemo();        
    }

    public void showFragmentDemo() {
    	
    	Session.checkApiKey(this, getSupportFragmentManager(), R.id.main_container, new Connection.ProtocolListener() {
			
			@Override
			public void onCanceled() {
				//Приложение не запустится, пока пользователь не будет авторизован
				finish();
			}
			
			@Override
			public void isCompleted() {
				FragmentManager fragmentManager = getSupportFragmentManager();
		        fragmentManager.beginTransaction()
		                .replace(R.id.main_container, new FragmentDemo())
		                .commit();		
			}
		});
    	
        
    }

}
