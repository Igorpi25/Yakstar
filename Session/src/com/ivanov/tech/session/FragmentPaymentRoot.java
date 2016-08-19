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
import android.support.v7.app.AppCompatActivity;
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
import com.ivanov.tech.session.Session.RequestListener;

public class FragmentPaymentRoot extends DialogFragment implements OnClickListener {


    private static String TAG = FragmentPaymentRoot.class.getSimpleName();
    
    Button button_back;
    ImageButton imagebutton_cardact,imagebutton_visa;
   
    ViewGroup container;

    public static FragmentPaymentRoot newInstance() {
    	FragmentPaymentRoot f = new FragmentPaymentRoot();
    	
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
        view = inflater.inflate(R.layout.fragment_payment_root, container, false);
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().show();
        
        button_back = (Button) view.findViewById(R.id.fragment_payment_root_button_back);
        button_back.setOnClickListener(this);
        
        imagebutton_cardact = (ImageButton)view.findViewById(R.id.fragment_payment_root_imagebutton_cardact);
        imagebutton_cardact.setOnClickListener(this);
        
        imagebutton_visa = (ImageButton)view.findViewById(R.id.fragment_payment_root_imagebutton_visa);
        imagebutton_visa.setOnClickListener(this);
        
        this.container=container;
        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==imagebutton_cardact.getId()){
			Session.createPaymentCardactFragment(getActivity(), getFragmentManager(), R.id.main_container);
		}
		
		if(v.getId()==imagebutton_visa.getId()){
			Session.createPaymentVisaFragment(getActivity(), getFragmentManager(), R.id.main_container);
		}
		
		if(v.getId()==button_back.getId()){			
			getFragmentManager().popBackStack();			
		}
		
	}
	
}
