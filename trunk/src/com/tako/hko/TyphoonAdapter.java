package com.tako.hko;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class TyphoonAdapter extends ArrayAdapter<TyphoonOption> {

	private ArrayList<TyphoonOption> items;
	private int rowId;
	private Context context;
	
	public TyphoonAdapter(Context context, int resource) {
		super(context, resource);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
    public TyphoonAdapter(Context context, int textViewResourceId,
    		ArrayList<TyphoonOption> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.rowId = textViewResourceId;
        this.context = context;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		LayoutInflater inflater = LayoutInflater.from(context);
		final TyphoonOption option = (TyphoonOption) items.get(position);
        
        if (convertView == null)
        	convertView = inflater.inflate(R.layout.typhoon_adapter, null);

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.typhoon_check);
        TextView text = (TextView) convertView.findViewById(R.id.typhoon_text);
        
        //checkbox.setText(option.getOptionName());
        text.setFocusable(false);
        text.setClickable(false);
        checkbox.setFocusable(false);
        checkbox.setClickable(false);
        text.setText(option.getOptionName());
        checkbox.setChecked(option.getOption());
        checkbox.setVisibility(option.getVisibility());
                
        Log.i("Adapter", "OptionName : " + option.getOptionName());
//        if (option.getOptionName().equals("通知鈴聲") ||
//        		option.getOptionName().equals("Notification Ringtone")) {
//        	checkbox.setVisibility(View.INVISIBLE);
//        	Log.i("Adapter", "Bingo!!!");
//        }
        return convertView;
	}
	
	public void setItems(int position, boolean newValue) {
		TyphoonOption oldNode = items.get(position);
		oldNode.setOption(newValue);
		items.set(position, oldNode);
	}
	
	public TyphoonOption getItem(int position) {
		return items.get(position);
	}

}
