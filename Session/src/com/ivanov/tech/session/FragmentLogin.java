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
import com.ivanov.tech.session.Session.CheckAuthorizationListener;
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentLogin extends DialogFragment implements OnClickListener {


    private static String TAG = FragmentLogin.class.getSimpleName();
    
    Button button_login,button_to_register;
    EditText edittext_login,edittext_password;
    
    CheckAuthorizationListener listener;
    ViewGroup container;
    
    public static FragmentLogin newInstance(CheckAuthorizationListener listener) {
    	FragmentLogin f = new FragmentLogin();
    	f.listener=listener;
    	
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
        view = inflater.inflate(R.layout.fragment_login, container, false);
        
        button_login = (Button) view.findViewById(R.id.fragment_login_button_login);
        button_login.setOnClickListener(this);
        
        button_to_register = (Button) view.findViewById(R.id.fragment_login_button_to_register);
        button_to_register.setOnClickListener(this);
        
        edittext_login=(EditText)view.findViewById(R.id.fragment_login_edittext_login);
        
        edittext_password=(EditText)view.findViewById(R.id.fragment_login_edittext_password);

        if(Session.getUserLogin()!=null){
        	edittext_login.setText(Session.getUserLogin());
        	
        	if(Session.getUserPassword()!=null){
        		edittext_password.setText(Session.getUserPassword());
        	}
        	
        }
        
        this.container=container;
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==button_login.getId()){
			
			final String login= edittext_login.getText().toString();
			final String password= edittext_password.getText().toString();
			
			Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new ProtocolListener(){

				@Override
				public void isCompleted() {
					Session.doLoginRequest(getActivity(),getFragmentManager(),R.id.main_container,login,password,new CheckAuthorizationListener(){

						@Override
						public void isAuthorized() {
							listener.isAuthorized();
						}

						@Override
						public void isLogedout() {
							Toast.makeText(getActivity(), "������������ ����� ��� ������", Toast.LENGTH_LONG).show();
						}
						
						@Override
						public boolean enableDialogs() {
							return true;
						}
					});
				}

				@Override
				public void onCanceled() {
					
				}
				
			});
			
			
		}
		if(v.getId()==button_to_register.getId()){
			
			Session.createSessionRegisterFirstFragment(getActivity(), getFragmentManager(), container.getId(), listener);
			
		}
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
