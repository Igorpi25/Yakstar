package com.ivanov.tech.session.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import com.ivanov.tech.multipletypesadapter.BinderImageView;
import com.ivanov.tech.multipletypesadapter.BinderTextView;
import com.ivanov.tech.multipletypesadapter.cursoradapter_recyclerview.CursorItemHolder;
import com.ivanov.tech.multipletypesadapter.cursoradapter_recyclerview.CursorMultipleTypesAdapter;
import com.ivanov.tech.session.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemHolderText extends CursorItemHolder{
	
	private static final String TAG = ItemHolderText.class.getSimpleName();
	
	TextView textview_key,textview_value;
	ImageView imageview_icon;
	
	OnClickListener onclicklistener;
	int layout=R.layout.itemholder_text;
	
	protected ItemHolderText(View itemView,Context context, OnClickListener onclicklistener) {
		super(itemView);
		
		this.context=context;
		this.onclicklistener=onclicklistener;
		
	}
	
	public ItemHolderText(Context context, OnClickListener onclicklistener) {
		super(new View(context));
		
		this.context=context;
		this.onclicklistener=onclicklistener;		
	}
	
	public ItemHolderText(Context context, int layout, OnClickListener onclicklistener) {
		super(new View(context));
		
		
		this.context=context;
		this.layout=layout;
		this.onclicklistener=onclicklistener;		
	}
	
	@Override
	public CursorItemHolder createClone(ViewGroup parent) {	
		
		Log.d(TAG, "createClone");
		
		View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
		
		ItemHolderText itemholder=new ItemHolderText(view,context,onclicklistener);
		
		itemholder.textview_key = (TextView) view.findViewById(R.id.itemholder_text_textview_key);
		itemholder.textview_value = (TextView) view.findViewById(R.id.itemholder_text_textview_value);
		itemholder.imageview_icon = (ImageView) view.findViewById(R.id.itemholder_text_imageview_icon);
		
		if(itemholder.onclicklistener!=null){
			view.setOnClickListener(itemholder.onclicklistener);
		}
		
		return itemholder;
	}

	@Override
	public void bindView(Cursor cursor) {
		JSONObject json;
		try {
			json = new JSONObject(CursorMultipleTypesAdapter.getValue(cursor));
			
			Log.d(TAG, "bindView json="+json);
						
			new BinderTextView(context).bindText(textview_key, json.getJSONObject("key"));			
			new BinderTextView(context).bindText(textview_value, json.getJSONObject("value"));
			
			if(json.has("icon")){
				new BinderImageView(context).bind(imageview_icon, json.getJSONObject("icon"));
			}
			
			itemView.setTag(layout, CursorMultipleTypesAdapter.getKey(cursor));				
									
		} catch (JSONException e) {
			Log.e(TAG, "bindView JSONException e="+e);
		}
	}


}
