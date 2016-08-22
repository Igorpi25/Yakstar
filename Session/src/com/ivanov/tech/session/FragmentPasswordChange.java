package com.ivanov.tech.session;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.multipletypesadapter.cursoradapter_recyclerview.CursorItemHolder;
import com.ivanov.tech.multipletypesadapter.cursoradapter_recyclerview.CursorMultipleTypesAdapter;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.session.Session.CheckInternetListener;
import com.ivanov.tech.session.Session.CloseListener;
import com.ivanov.tech.session.Session.DialogRequestListener;
import com.ivanov.tech.session.Session.RequestListener;
import com.ivanov.tech.session.adapter.ItemHolderText;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentPasswordChange extends DialogFragment implements OnClickListener {

    public static final String TAG = FragmentPasswordChange.class
            .getSimpleName();    
	
    Button button_send, button_cancel;
    
    EditText edittext_newpassword, edittext_passwordrepeat, edittext_currentpassword;
    
    JSONObject json_data;
    
    public static FragmentPasswordChange newInstance() {
    	FragmentPasswordChange f = new FragmentPasswordChange();
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        Log.d(TAG, "onStart");  
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_password_change, container, false);
        
        Log.d(TAG, "onCreateView");
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().show();
				
		((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name); 
		
		button_send=(Button)view.findViewById(R.id.fragment_password_change_button_send);
		button_send.setOnClickListener(this);
		
		button_cancel=(Button)view.findViewById(R.id.fragment_password_change_button_cancel);
		button_cancel.setOnClickListener(this);
		
		edittext_newpassword=(EditText)view.findViewById(R.id.fragment_password_change_edittext_newpassword);
		edittext_passwordrepeat=(EditText)view.findViewById(R.id.fragment_password_change_edittext_passwordrepeat);
		edittext_currentpassword=(EditText)view.findViewById(R.id.fragment_password_change_edittext_currentpassword);
		
        return view;
    }
    
    @Override
	public void onClick(View v) {
    	
    	Log.d(TAG, "onClick");
    	
    	if(v.getId()==button_send.getId()){
    		
    		if( !(checkNewPassword()&&checkPasswordRepeat()&&checkCurrentPassword()&&checkPasswordsEquality()) )return;
			
    		Connection.protocolConnection(getActivity(), getFragmentManager(), R.id.main_container, new Connection.ProtocolListener(){

				@Override
				public void isCompleted() {		
										
					Session.doChangeRegDataRequest(getActivity(), getFragmentManager(), R.id.main_container, getData(), new RequestListener(){

						@Override
						public void onResponsed() {
							//Parse response
							getFragmentManager().popBackStack();
						}
						
					});
				}

				@Override
				public void onCanceled() {}
    			
    		});
    		
    		return;
    	}
    	
    	if(v.getId()==button_cancel.getId()){
    		getFragmentManager().popBackStack();
    		return;
    	}
    	
	}

    HashMap<String, String> getData(){
    	HashMap<String, String> params = new HashMap<String, String>();
    	
    	
    	params.put("newPassword",edittext_newpassword.getText().toString());
    	params.put("passwordRepeat",edittext_passwordrepeat.getText().toString());
    	
    	params.put("surname", "");
    	params.put("name", "");
    	params.put("patronim", "");
    	params.put("email", "");
    	params.put("phone", "");    	
    	
    	params.put("verifiedPassword", edittext_currentpassword.getText().toString());
    	
    	return params;
    }
    
    boolean checkNewPassword(){
    	if(edittext_newpassword.getText().length()==0){
    		Toast.makeText(getActivity(), "Вы должны ввести новый пароль", Toast.LENGTH_LONG).show();
    	}
    	return edittext_newpassword.getText().length()>0;
    }
    
    boolean checkPasswordRepeat(){
    	if(edittext_passwordrepeat.getText().length()==0){
    		Toast.makeText(getActivity(), "Вы должны повторить пароль", Toast.LENGTH_LONG).show();
    	}
    	return edittext_passwordrepeat.getText().length()>0;
    }
    
    boolean checkCurrentPassword(){
    	if(edittext_currentpassword.getText().length()==0){
    		Toast.makeText(getActivity(), "Вы должны ввести текущий пароль", Toast.LENGTH_LONG).show();
    	}
    	
    	return edittext_currentpassword.getText().length()>0;
    }
    
    boolean checkPasswordsEquality(){
    	
    	String newpassword=edittext_newpassword.getText().toString();
    	String passwordrepeat=edittext_passwordrepeat.getText().toString();
    	
    	if(!newpassword.equals(passwordrepeat)){
    		Toast.makeText(getActivity(), "Повтор пароля неверный", Toast.LENGTH_LONG).show();
    	}
    	
    	return newpassword.equals(passwordrepeat);
    }
}
