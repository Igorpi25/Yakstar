package com.ivanov.tech.session;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import org.json.JSONObject;

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
