package com.tako.hko;

import com.tako.hko.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;

public class HKOOption extends ListActivity {
	private static final String PREFS_NAME = "HKOPrefs";
	public static final int CUSTOMER_BACKGROUND = 504;
	private String[] options = null;
	private static int language = -1;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	language = settings.getInt("language", 0);
    	
    	if (language == Misc.CHINESE)
    		options = this.getResources().getStringArray(R.array.main_option_chi);
    	else
    		options = this.getResources().getStringArray(R.array.main_option_eng);
    	setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
    	ListView list = getListView();
    	list.setTextFilterEnabled(true);
    	//list.setOnClickListener(new OnClickListener());
    }
    
    public void onStop() {
    	
    	//Bundle b = new Bundle();
    	//b.putInt("language", language);
    	//Intent i = this.getIntent();
    	//i.putExtras(b);
    	this.setResult(RESULT_OK);
    	super.onStop();
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        //record the selected item
        switch (position) {
        case 0:
        	this.showLanguageDialog();
        	break;
        case 1:
        	this.showRefreshDialog();
        	break;
        case 2:
        	this.showRetryDialog();
        	break;
        case 3:
        	this.showColorDialog();
        	break;
        case 4:
        	this.showTextDialog();
        	break;
        case 5:
        	Intent typhoonIntent = new Intent(HKOOption.this, HKOTyphoonOption.class);
        	this.startActivity(typhoonIntent);
        }
    }
      
    private void showLanguageDialog() {
    	int dialogTitle = -1;
    	String cancelButton = null;
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	int currentValue = settings.getInt("language", 0);
    	
    	if (language == Misc.CHINESE) {
    		dialogTitle = R.string.sel_lang_chi;
    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
    	} else {
    		dialogTitle = R.string.sel_lang_eng;
    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
    	}
    	
    	String[] langs = this.getResources().getStringArray(R.array.language);
    	//int[] langValue = this.getResources().getIntArray(R.array.lang_value);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOOption.this);
    	builder.setTitle(dialogTitle);
    	builder.setSingleChoiceItems(langs, currentValue, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOOption.PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        //int[] langValue = getResources().getIntArray(R.array.lang_value);
    	    	editor.putInt("language", item);
    	        editor.commit();
    	        
    	        language = item;
    	    	if (item == Misc.CHINESE)
    	    		options = getResources().getStringArray(R.array.main_option_chi);
    	    	else
    	    		options = getResources().getStringArray(R.array.main_option_eng);
    	    	
    	        setListAdapter(new ArrayAdapter<String>(HKOOption.this, android.R.layout.simple_list_item_1, options));
    	        dialog.dismiss();
			}
		});
    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface i, int button)  {}
    	});
    	builder.show();
    }

    private void showRefreshDialog() {
    	int defaultOption = 0;
    	String cancelButton = null;
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	String refresh[] = null, refreshTitle = null;
    	
    	if (language == Misc.CHINESE) {
    		refresh = this.getResources().getStringArray(R.array.refresh_chi);
    		refreshTitle = this.getResources().getString(R.string.sel_fresh_chi);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
    	} else {
    		refresh = this.getResources().getStringArray(R.array.refresh_eng);
    		refreshTitle = this.getResources().getString(R.string.sel_fresh_eng);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
    	}
    	
    	int[] refreshValue = this.getResources().getIntArray(R.array.refresh_value);
    	int currentValue = settings.getInt("refresh", 1800);
    	for (int i = 0; i < refreshValue.length; i++)
    		if (currentValue == refreshValue[i]) defaultOption = i;
    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOOption.this);
    	builder.setTitle(refreshTitle);
    	builder.setSingleChoiceItems(refresh, defaultOption, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOOption.PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        int[] refreshValue = getResources().getIntArray(R.array.refresh_value);
    	    	editor.putInt("refresh", refreshValue[item]);
    	        editor.commit();
    	        dialog.dismiss();
    	    }
    	});
    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface i, int button)  {}
    	});
    	builder.show();
    } 
    
    private void showRetryDialog() {
    	int defaultOption = 0;
    	String cancelButton = null;
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	String retry[] = null, retryTitle = null;
    	
    	int currentValue = settings.getInt("retry", 600);
    	
    	if (language == Misc.CHINESE) {
    		retry = this.getResources().getStringArray(R.array.retry_chi);
    		retryTitle = this.getResources().getString(R.string.sel_retry_chi);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
    	} else {
    		retry = this.getResources().getStringArray(R.array.retry_eng);
    		retryTitle = this.getResources().getString(R.string.sel_retry_eng);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
    	}
    	
    	int[] retryValue = this.getResources().getIntArray(R.array.retry_value);
    	for (int i = 0; i < retryValue.length; i++)
    		if (currentValue == retryValue[i]) defaultOption = i;
    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOOption.this);
    	builder.setTitle(retryTitle);
    	builder.setSingleChoiceItems(retry, defaultOption, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOOption.PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        int[] retryValue = getResources().getIntArray(R.array.retry_value);
    	    	editor.putInt("retry", retryValue[item]);
    	        editor.commit();
    	        dialog.dismiss();
    	    }
    	});
    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface i, int button)  {}
    	});
    	builder.show();
    }
    
    private void showColorDialog() {
    	String cancelButton = null;
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	String color[] = null, colorTitle = null;
    	
    	int currentValue = settings.getInt("color", 0);
    	
    	if (language == Misc.CHINESE) {
    		color = this.getResources().getStringArray(R.array.color_chi);
    		colorTitle = this.getResources().getString(R.string.sel_color_chi);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
    	} else {
    		color = this.getResources().getStringArray(R.array.color_eng);
    		colorTitle = this.getResources().getString(R.string.sel_color_eng);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
    	}
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOOption.this);
    	builder.setTitle(colorTitle);
    	builder.setSingleChoiceItems(color, currentValue, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOOption.PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        if (settings.getInt("color", 0) != item) editor.putBoolean("color_change", true);
    	    	editor.putInt("color", item);
    	        editor.commit();
    	        if (item == 2) {
    	        	showCustomBackground();
    	        }
    	        dialog.dismiss();
    	    }
    	    private void showCustomBackground() {
    	    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOOption.this);
    	    	final View view = SeekbarFactory.getInstance(HKOOption.this, R.layout.color_diag, HKOOption.this, language, SeekbarFactory.BACK_OPTION);
    	    	builder.setView(view);
    	    	String chooseText = null, okText = null, cancelText = null;
    	    	if (language == Misc.CHINESE) {
    	    		chooseText = getString(R.string.choose_col_chi);
    	    		okText = getString(R.string.btn_ok_chi);
    	    		cancelText = getString(R.string.btn_cancel_chi);
    	    	} else {
    	    		chooseText = getString(R.string.choose_col_eng);
    	    		okText = getString(R.string.btn_ok_eng);
    	    		cancelText = getString(R.string.btn_cancel_eng);
    	    	}
    	    	builder.setTitle(chooseText);
    	    	builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					SeekBar red = (SeekBar) view.findViewById(R.id.red);
    					SeekBar blue = (SeekBar) view.findViewById(R.id.blue);
    					SeekBar green = (SeekBar) view.findViewById(R.id.green);
    					SeekBar alpha = (SeekBar) view.findViewById(R.id.alpha);
    					CheckBox greyScale = (CheckBox) view.findViewById(R.id.grey);
    					// TODO Auto-generated method stub
    					SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOOption.PREFS_NAME, 0);
    	    	        SharedPreferences.Editor editor = settings.edit();
    	    	        int shadowColor = (int) (alpha.getProgress() * 0.4) << 24 | 0x3d << 16 |
    	    	        					0x3d << 8 | 0x3d;
    	    	    	
    	    	        Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.testback);
    					float r = red.getProgress() / 255f;
    					float g = green.getProgress() / 255f;
    					float b = blue.getProgress() / 255f;
    					float a = alpha.getProgress() / 255f;
    	    	        ColorMatrix cm = new ColorMatrix(new float[] {  
    	    	        		r , 0f, 0f, 0f, 0f,  
    	    	        		0f, g , 0f, 0f, 0f,  
    	    	        		0f, 0f, b , 0f, 0f,  
    	    	        		0f, 0f, 0f, a , 0f});

    	    	        Bitmap newMap = Bitmap.createBitmap(map.getWidth(), map.getHeight(), Bitmap.Config.ARGB_8888);
    	    	        Canvas canvas = new Canvas(newMap);
    	    	        Paint paint = new Paint();
    	    	        //paint.setAlpha(0);
    	    	        //paint.setShadowLayer(4.0f, 1.5f, 1.5f, shadowColor);
    	    	        
    	    	        if (greyScale.isChecked()) {
    	    	        	paint.setColorFilter(new PorterDuffColorFilter(shadowColor, PorterDuff.Mode.SRC_IN)); 
    	    	        	canvas.translate(2f, 2f);
    	    	        	canvas.drawBitmap(map, 0f, 0f, paint);
    	    	        	canvas.save();
    	    	        	canvas.translate(-2f, -2f);
    	    	        }
    	    	        paint.setColorFilter(new ColorMatrixColorFilter(cm));
    	    	        canvas.drawBitmap(map, 0f, 0f, paint);

    	    	        //canvas.restore();
						ObjectHandler oh = new ObjectHandler(getApplicationContext(), language);
						oh.writeBackground(newMap);
    	    	        
    	    	        
    	    	    	editor.putInt("backRed", red.getProgress());
    	    	    	editor.putInt("backGreen", green.getProgress());
    	    	    	editor.putInt("backBlue", blue.getProgress());
    	    	    	editor.putInt("backAlpha", alpha.getProgress());
    	    	    	editor.putBoolean("backGreyScale", greyScale.isChecked());
    	    	    	editor.putBoolean("color_change", true);
    	    	        editor.commit();
    	    	        dialog.dismiss();
    				}
    			});
    	    	builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {}
    			});
    	    	builder.show();
    	    }
    	});
    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface i, int button)  {}
    	});
    	builder.show();
    }
    
    private void showTextDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOOption.this);
    	final View view = SeekbarFactory.getInstance(HKOOption.this, R.layout.color_diag, this, language, SeekbarFactory.FONT_OPTION);
    	builder.setView(view);
    	
    	String chooseText = null, okText = null, cancelText = null;
    	if (language == Misc.CHINESE) {
    		chooseText = this.getString(R.string.choose_col_chi);
    		okText = this.getString(R.string.btn_ok_chi);
    		cancelText = this.getString(R.string.btn_cancel_chi);
    	} else {
    		chooseText = this.getString(R.string.choose_col_eng);
    		okText = this.getString(R.string.btn_ok_eng);
    		cancelText = this.getString(R.string.btn_cancel_eng);
    	}
    	builder.setTitle(chooseText);
    	builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SeekBar red = (SeekBar) view.findViewById(R.id.red);
				SeekBar blue = (SeekBar) view.findViewById(R.id.blue);
				SeekBar green = (SeekBar) view.findViewById(R.id.green);
				SeekBar alpha = (SeekBar) view.findViewById(R.id.alpha);
				CheckBox greyScale = (CheckBox) view.findViewById(R.id.grey);
				// TODO Auto-generated method stub
				SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOOption.PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        int color = alpha.getProgress() << 24 | red.getProgress() << 16 | green.getProgress() << 8 | blue.getProgress();
    	    	editor.putInt("fontColor", color);
    	    	editor.putInt("fontRed", red.getProgress());
    	    	editor.putInt("fontGreen", green.getProgress());
    	    	editor.putInt("fontBlue", blue.getProgress());
    	    	editor.putInt("fontAlpha", alpha.getProgress());
    	    	editor.putBoolean("fontGreyScale", greyScale.isChecked());
    	        editor.commit();
    	        dialog.dismiss();
			}
		});
    	builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	builder.show();
    }

}