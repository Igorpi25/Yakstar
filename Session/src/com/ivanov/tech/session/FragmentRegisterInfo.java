package com.ivanov.tech.session;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class FragmentRegisterInfo extends DialogFragment implements OnClickListener {
private static String TAG = FragmentRegisterInfo.class.getSimpleName();
    
    Button button_close;
    TextView textview_body,textview_title;
    
    String title,body;
    
    ViewGroup container;
        
    boolean success=false;

    public static FragmentRegisterInfo newInstance(String title, String body) {
    	FragmentRegisterInfo f = new FragmentRegisterInfo();
    	f.title=title;
    	f.body=body;
    	
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_register_info, container, false);
        
        setHasOptionsMenu(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        
		textview_title=(TextView)view.findViewById(R.id.fragment_register_info_textview_title);
		textview_title.setText(title);
		
		textview_body=(TextView)view.findViewById(R.id.fragment_register_info_textview_body);
		textview_body.setText(body);
		
        button_close = (Button) view.findViewById(R.id.fragment_register_info_button_close);
        button_close.setOnClickListener(this);
        
        this.container=container;
        
        return view;
    }

    public void onStart(){
    	super.onStart();
    	hideKeyboard();
    }
    
    public void onStop(){
    	super.onStop();
    }
    
	@Override
	public void onClick(View v) {
		
		if(v.getId()==button_close.getId()){
			getFragmentManager().popBackStack();			
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
