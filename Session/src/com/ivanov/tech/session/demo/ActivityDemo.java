package com.ivanov.tech.session.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;

/**
 * Created by Igor on 15.01.15.
 */
public class ActivityDemo extends AppCompatActivity {

	//Urls for Session-demo
	static final String host="https://lk.amtelcom.ru/";	
	
	static final String url_check_autorisation=host+"b-/b-autorisation/b-autorisation_check-user.php";
	
	static final String url_login=host;
	static final String url_register=host;
	
	static final String url_check_internet=host+"b-/b-switch-inet/b-switch-inet_status.php";
	static final String url_switch_internet=host+"b-/b-switch-inet/b-switch-inet.php";
	
	static final String url_logout=host+"/?action=logout";
        	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        
        //Init app preferences
        Session.Initialize(getApplicationContext(),url_check_autorisation,url_login,url_register,url_check_internet,url_switch_internet,url_logout);
        
        showFragmentDemo();        
    }

    public void showFragmentDemo() {
    	
    	Session.checkAutorisation(this, getSupportFragmentManager(), R.id.main_container, new Connection.ProtocolListener() {
			
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
