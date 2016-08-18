package com.ivanov.tech.session;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSplashScreen extends DialogFragment{


    private static String TAG = FragmentSplashScreen.class.getSimpleName();
    
    ViewGroup container;

    public static FragmentSplashScreen newInstance() {
    	FragmentSplashScreen f = new FragmentSplashScreen();
    	
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
        view = inflater.inflate(R.layout.fragment_splashscreen, container, false);
        
        this.container=container;
        
        return view;
    }

}
