package com.ivanov.tech.session.tester;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.session.R;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.session.Session.Status;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentSessionTester extends SherlockDialogFragment implements OnClickListener {

    public static final String TAG = FragmentSessionTester.class
            .getSimpleName();    
	
    TextView textview_info;
    Button button_logout;

    public static FragmentSessionTester newInstance() {
    	FragmentSessionTester f = new FragmentSessionTester();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        showInfo();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_tester, container, false);
                
        button_logout = (Button) view.findViewById(R.id.fragment_tester_button_logout);
        button_logout.setOnClickListener(this);
       
        textview_info=(TextView)view.findViewById(R.id.fragment_tester_textview_info);
           
        return view;
    }

    @Override
	public void onClick(View v) {
		textview_info.setText("Info: (asdads)");
		
		if (v.getId()==button_logout.getId()){
			Session.removeApiKey();			
			((MainActivity)getActivity()).showTester();
		}
	}
    
    void showInfo(){
    	textview_info.setText("Api-Key:\n"+Session.getApiKey());
    }
	
}
