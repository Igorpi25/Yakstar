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

public class FragmentRegisterSuccess extends DialogFragment implements OnClickListener {


    private static String TAG = FragmentRegisterSuccess.class.getSimpleName();
    
    Button button_login;
    EditText edittext_login,edittext_password;
    
    TextView textview_response;
    
    CheckAuthorizationListener protocollistener;
    ViewGroup container;
    

    public static FragmentRegisterSuccess newInstance(CheckAuthorizationListener listener) {
    	FragmentRegisterSuccess f = new FragmentRegisterSuccess();
    	f.protocollistener=listener;
    	
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
        view = inflater.inflate(R.layout.fragment_register_success, container, false);
        
        textview_response=(TextView)view.findViewById(R.id.fragment_register_textview_response);
        textview_response.setText(Session.getRegisteredMessage());
        
        button_login = (Button) view.findViewById(R.id.fragment_register_button_login);
        button_login.setOnClickListener(this);
        
        edittext_login=(EditText)view.findViewById(R.id.fragment_register_edittext_login);        
        edittext_password=(EditText)view.findViewById(R.id.fragment_register_edittext_password);
        
        if((getArguments()!=null)&&(getArguments().containsKey("email"))){
        	edittext_login.setText(getArguments().getString("email"));
        }
        
        if((getArguments()!=null)&&(getArguments().containsKey("password"))){
        	edittext_password.setText(getArguments().getString("password"));
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
					Session.doLoginRequest(getActivity(), getFragmentManager(), R.id.main_container, login, password, new CheckAuthorizationListener(){

						@Override
						public void isAuthorized() {
							protocollistener.isAuthorized();
						}

						@Override
						public void isLogedout() {
							Toast.makeText(getActivity(), "Неправильный логин или пароль", Toast.LENGTH_LONG).show();
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
