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
import android.widget.ImageButton;
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
import com.ivanov.tech.session.Session.CloseListener;
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentError extends DialogFragment implements OnClickListener {


    private static String TAG = FragmentError.class.getSimpleName();
    
    Button button_close;
    TextView textview_title,textview_message,textview_code;
   
    CloseListener listener;
    String title,message;
    int code;
    
    ViewGroup container;

    public static FragmentError newInstance(int code,String title,String message, CloseListener listener) {
    	FragmentError f = new FragmentError();
    	
    	f.title=title;
    	f.message=message;
    	f.code=code;
    	
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_error, container, false);
        
        button_close = (Button) view.findViewById(R.id.fragment_error_button_close);
        button_close.setOnClickListener(this);
        
        textview_title = (TextView) view.findViewById(R.id.fragment_error_textview_title);
        textview_message = (TextView) view.findViewById(R.id.fragment_error_textview_message);
        textview_code = (TextView) view.findViewById(R.id.fragment_error_textview_code);
        
        this.container=container;
        
        return view;
    }

	@Override
	public void onClick(View v) {
		
		if(v.getId()==button_close.getId()){			
			getFragmentManager().popBackStack();
			listener.onClosed();
		}
		
	}
	
}
