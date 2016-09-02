package com.ivanov.tech.session.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.session.FragmentSplashScreen;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.session.Session.CheckAuthorizationListener;

/**
 * Created by Igor on 15.01.15.
 */
public class ActivityDemo extends AppCompatActivity {

	//Urls for Session-demo
	static final String host="https://lk.amtelcom.ru/";	
	
	static final String url_check_autorisation=host+"b-/b-autorisation/b-autorisation_check-user.php";	
	static final String url_login=host;
	static final String url_logout=host+"/?action=logout";
		
	static final String url_check_internet=host+"b-/b-switch-inet/b-switch-inet_status.php";
	static final String url_switch_internet=host+"b-/b-switch-inet/b-switch-inet.php";
	
	static final String url_tarif=host+"p-/b-registration/b-registration__get-tarifs.php";
	static final String url_captcha=host+"p-/b-registration/b-registration__capcha.php";
	static final String url_rules=host+"b-/b-documents/__content/b-documents__content_type_rules.html";
	static final String url_agriment=host+"b-/b-documents/__content/b-documents__content_type_agriment.html";
	static final String url_register=host+"p-/b-registration/b-registration.php";
	
	static final String url_info=host+"p-/b-welcom/b-welcom.php";
	
	static final String url_payment_cardact=host+"p-/b-cardact/b-cardact.php";
	static final String url_payment_visa=host+"p-/b-payonline/b-payonline.php";
    
	static final String url_changeregdata_init=host+"p-/b-change-reg-data/b-change-reg-data_initialize.php";
	static final String url_changeregdata=host+"p-/b-change-reg-data/b-change-reg-data.php";
	
	static final String url_changetarif=host+"p-/b-change-tarif/b-change-tarif.php";
	static final String url_changetarif_status=host+"p-/b-change-tarif/b-change-tarif_get-status.php";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        setContentView(R.layout.activity_demo);
        
        getSupportActionBar().hide();
                
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setTitle(R.string.app_logo); 
      
        //Init app preferences
        Session.Initialize(getApplicationContext(),
        		url_check_autorisation,
        		url_login,
        		url_logout,
        		url_check_internet,
        		url_switch_internet,
        		url_tarif,
        		url_captcha,
        		url_rules,
        		url_agriment,
        		url_register,
        		url_info,
        		url_payment_cardact,
        		url_payment_visa,
        		url_changeregdata_init,
        		url_changeregdata,
        		url_changetarif,
        		url_changetarif_status);
        
        showFragmentSplashScreen();
        
        Session.checkAutorisation(this, getSupportFragmentManager(), R.id.main_container, new CheckAuthorizationListener() {
			    		
			@Override
			public void isAuthorized() {
				showFragmentDemo();
			}			
			
			@Override
			public void isLogedout() {
				Session.createSessionLoginFragment(ActivityDemo.this, getSupportFragmentManager(), R.id.main_container, this);
			}

			@Override
			public boolean enableDialogs() {
				return false;
			}
				
		});
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
        
    public void showFragmentSplashScreen() {
    	
    	FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, new FragmentSplashScreen())
                .commit();	        
    }    	
    
    public void showFragmentDemo() {
    	
    	FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, new FragmentDemo())
                .commit();        
                
    }

}
