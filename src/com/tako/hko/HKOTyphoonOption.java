package com.tako.hko;

import java.util.ArrayList;

import com.tako.hko.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

public class HKOTyphoonOption extends ListActivity {
	private static final String PREFS_NAME = "HKOPrefs";
	private final String TAG = "HKOTyphoonOption";
	public static final int CUSTOMER_BACKGROUND = 504;
	private String[] options = null;
	private static int language = -1;
	private ListView list = null;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.generateList();
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
    }
    
    public void onStop() {
    	super.onStop();
    	LinearLayout list1 = (LinearLayout) list.getChildAt(0);
    	CheckBox check1 = (CheckBox) list1.findViewById(R.id.typhoon_check);
    	LinearLayout list2 = (LinearLayout) list.getChildAt(1);
    	CheckBox check2 = (CheckBox) list2.findViewById(R.id.typhoon_check);
    	LinearLayout list3 = (LinearLayout) list.getChildAt(2);
    	CheckBox check3 = (CheckBox) list3.findViewById(R.id.typhoon_check);
    	LinearLayout list4 = (LinearLayout) list.getChildAt(3);
    	CheckBox check4 = (CheckBox) list4.findViewById(R.id.typhoon_check);
    	
    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
    	editor.putBoolean("typhoon_noti", check1.isChecked());
    	editor.putBoolean("black_noti", check2.isChecked());
    	editor.putBoolean("typhoon_vibrate", check3.isChecked());
    	editor.putBoolean("sound", check4.isChecked());
    	//Log.e(TAG, "1 is " + check1.isChecked());
    	//Log.e(TAG, "2 is " + check2.isChecked());
    	//Log.e(TAG, "3 is " + check3.isChecked());
    	//Log.e(TAG, "4 is " + check4.isChecked());
        editor.commit();
    }
    
    private void generateList() {
    	ArrayList<TyphoonOption> aList = new ArrayList<TyphoonOption>();
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	language = settings.getInt("language", 0);
    	
    	if (language == Misc.CHINESE)
    		options = this.getResources().getStringArray(R.array.noti_menu_chi);
    	else
    		options = this.getResources().getStringArray(R.array.noti_menu_eng);
    	
    	//Log.e(TAG, "Loading Option : " + settings.getBoolean("typhoon_noti", true));
    	TyphoonOption option1 = new TyphoonOption(options[0],
    			settings.getBoolean("typhoon_noti", true),
    			null, View.VISIBLE);
    	aList.add(option1);
    	TyphoonOption option2 = new TyphoonOption(options[1],
    			settings.getBoolean("black_noti", false),
    			null, View.VISIBLE);
    	aList.add(option2);
    	TyphoonOption option3 = new TyphoonOption(options[2],
    			settings.getBoolean("typhoon_vibrate", true),
    			null, View.VISIBLE);
    	aList.add(option3);
    	TyphoonOption option4 = new TyphoonOption(options[3],
    			settings.getBoolean("sound", true),
    			null, View.VISIBLE);
    	aList.add(option4);
    	TyphoonOption option5 = new TyphoonOption(options[4],
    			false,
    			settings.getString("sound_path", null), View.INVISIBLE);
    	aList.add(option5);
    	TyphoonOption option6 = new TyphoonOption(options[5],
    			false,
    			null, View.INVISIBLE);
    	aList.add(option6);
    	
    	TyphoonAdapter aa = new TyphoonAdapter(this, R.layout.typhoon_adapter, aList);
    	Log.e(TAG, "Number in adapter : " + aa.getCount());
    	this.setListAdapter(aa);
    	list = this.getListView();
    	list.setTextFilterEnabled(false);
    	
    	list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	//this.setContentView(list);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (resultCode == RESULT_OK) {
    		//LinearLayout rl = (LinearLayout) list.getChildAt(4);
        	TyphoonAdapter newAdapter = (TyphoonAdapter) this.getListView().getAdapter();
        	TyphoonOption option = newAdapter.getItem(4);
        	
    		Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
	        SharedPreferences.Editor editor = settings.edit();

    		if (uri != null) {
    			editor.putString("sound_path", uri.toString());
    			option.setSound(uri.toString());
    		} else {
    			editor.putString("sound_path", null);
    			option.setSound(null);
    		}
    		editor.commit();
    	}
    	//this.generateList();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	LinearLayout rl = (LinearLayout) list.getChildAt(position);
    	CheckBox checkbox = (CheckBox) rl.getChildAt(0);
    	
    	TyphoonAdapter newAdapter = (TyphoonAdapter) l.getAdapter();
    	newAdapter.setItems(position, !checkbox.isChecked());
    	
    	TyphoonOption option = newAdapter.getItem(position);
		String label = option.getOptionName();
		if (label.equals("通知鈴聲") || label.equals("Notification Ringtone")) {
			String uri = null, title = null;
			uri = option.getSound();
			if (language == Misc.CHINESE)
				title = this.getResources().getString(R.string.sel_ringtone_chi);
			else
				title = this.getResources().getString(R.string.sel_ringtone_eng);
			
			Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE,
			RingtoneManager.TYPE_NOTIFICATION);
			intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, title);
			if( uri != null) {
				intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
				Uri.parse( uri));
			} else
				intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		
			this.startActivityForResult(intent, 0);
		}
		if (label.equals("更新速度") || label.equals("Refresh Rate"))
			this.showRefreshDialog();
    }
    
    private void showRefreshDialog() {
    	int defaultOption = 0;
    	String cancelButton = null;
    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	String refresh[] = null, refreshTitle = null;
    	
    	if (language == Misc.CHINESE) {
    		refresh = this.getResources().getStringArray(R.array.noti_refresh_chi);
    		refreshTitle = this.getResources().getString(R.string.sel_fresh_chi);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
    	} else {
    		refresh = this.getResources().getStringArray(R.array.noti_refresh_eng);
    		refreshTitle = this.getResources().getString(R.string.sel_fresh_eng);
    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
    	}
    	
    	int[] refreshValue = this.getResources().getIntArray(R.array.noti_refresh_value);
    	int currentValue = settings.getInt("noti_refresh", 900);
    	for (int i = 0; i < refreshValue.length; i++)
    		if (currentValue == refreshValue[i]) defaultOption = i;
    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
    	builder.setTitle(refreshTitle);
    	builder.setSingleChoiceItems(refresh, defaultOption, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        int[] refreshValue = getResources().getIntArray(R.array.noti_refresh_value);
    	        
    	        //  Start the service if from never to some intervals
    	        if (settings.getInt("noti_refresh", 900) == 0 &&
    	        		refreshValue[item] > 0) {
    	        	
    	        	Intent notiUpdate = new Intent();
    	        	Log.i(TAG, "Triggering service...");
					notiUpdate.setAction("com.tako.hko.NotiService");
					startService(notiUpdate);
    	        }
    	        //
    	        editor.putInt("noti_refresh", refreshValue[item]);
    	        editor.commit();
    	        
    	        dialog.dismiss();
    	    }
    	});
    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface i, int button)  {}
    	});
    	builder.show();
    } 
    
    
    
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        // TODO Auto-generated method stub
//        //record the selected item
//    	LinearLayout selectedItem = (LinearLayout) l.getChildAt(position);
//    	CheckBox check = (CheckBox) selectedItem.findViewById(R.id.typhoon_check);
//    	check.setChecked(!check.isChecked());
//    	if (position == 0) {
//        	LinearLayout list1 = (LinearLayout) l.getChildAt(0);
//        	CheckBox check1 = (CheckBox) list1.findViewById(R.id.typhoon_check);
//    		LinearLayout list2 = (LinearLayout) l.getChildAt(1);
//        	CheckBox check2 = (CheckBox) list2.findViewById(R.id.typhoon_check);
//        	check2.setEnabled(check1.isChecked());
//        	Log.e(TAG, "???");
//    	}
//    	Log.e(TAG, "???");
//    	super.onListItemClick (l, v, position, id);
//    }
      
//    private void showLanguageDialog() {
//    	int dialogTitle = -1;
//    	String cancelButton = null;
//    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
//    	int currentValue = settings.getInt("language", 0);
//    	
//    	if (language == Misc.CHINESE) {
//    		dialogTitle = R.string.sel_lang_chi;
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
//    	} else {
//    		dialogTitle = R.string.sel_lang_eng;
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
//    	}
//    	
//    	String[] langs = this.getResources().getStringArray(R.array.language);
//    	//int[] langValue = this.getResources().getIntArray(R.array.lang_value);
//    	
//    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
//    	builder.setTitle(dialogTitle);
//    	builder.setSingleChoiceItems(langs, currentValue, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int item) {
//				// TODO Auto-generated method stub
//				SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
//    	        SharedPreferences.Editor editor = settings.edit();
//    	        //int[] langValue = getResources().getIntArray(R.array.lang_value);
//    	    	editor.putInt("language", item);
//    	        editor.commit();
//    	        
//    	        language = item;
//    	    	if (item == Misc.CHINESE)
//    	    		options = getResources().getStringArray(R.array.main_chi);
//    	    	else
//    	    		options = getResources().getStringArray(R.array.main_eng);
//    	    	
//    	        setListAdapter(new ArrayAdapter<String>(HKOTyphoonOption.this, android.R.layout.simple_list_item_1, options));
//    	        dialog.dismiss();
//			}
//		});
//    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
//    				public void onClick(DialogInterface i, int button)  {}
//    	});
//    	builder.show();
//    }
//
//    private void showRefreshDialog() {
//    	int defaultOption = 0;
//    	String cancelButton = null;
//    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
//    	String refresh[] = null, refreshTitle = null;
//    	
//    	if (language == Misc.CHINESE) {
//    		refresh = this.getResources().getStringArray(R.array.refresh_chi);
//    		refreshTitle = this.getResources().getString(R.string.sel_fresh_chi);
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
//    	} else {
//    		refresh = this.getResources().getStringArray(R.array.refresh_eng);
//    		refreshTitle = this.getResources().getString(R.string.sel_fresh_eng);
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
//    	}
//    	
//    	int[] refreshValue = this.getResources().getIntArray(R.array.refresh_value);
//    	int currentValue = settings.getInt("refresh", 1800);
//    	for (int i = 0; i < refreshValue.length; i++)
//    		if (currentValue == refreshValue[i]) defaultOption = i;
//    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
//    	builder.setTitle(refreshTitle);
//    	builder.setSingleChoiceItems(refresh, defaultOption, new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int item) {
//    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
//    	        SharedPreferences.Editor editor = settings.edit();
//    	        int[] refreshValue = getResources().getIntArray(R.array.refresh_value);
//    	    	editor.putInt("refresh", refreshValue[item]);
//    	        editor.commit();
//    	        dialog.dismiss();
//    	    }
//    	});
//    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface i, int button)  {}
//    	});
//    	builder.show();
//    } 
//    
//    private void showRetryDialog() {
//    	int defaultOption = 0;
//    	String cancelButton = null;
//    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
//    	String retry[] = null, retryTitle = null;
//    	
//    	int currentValue = settings.getInt("retry", 600);
//    	
//    	if (language == Misc.CHINESE) {
//    		retry = this.getResources().getStringArray(R.array.retry_chi);
//    		retryTitle = this.getResources().getString(R.string.sel_retry_chi);
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
//    	} else {
//    		retry = this.getResources().getStringArray(R.array.retry_eng);
//    		retryTitle = this.getResources().getString(R.string.sel_retry_eng);
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
//    	}
//    	
//    	int[] retryValue = this.getResources().getIntArray(R.array.retry_value);
//    	for (int i = 0; i < retryValue.length; i++)
//    		if (currentValue == retryValue[i]) defaultOption = i;
//    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
//    	builder.setTitle(retryTitle);
//    	builder.setSingleChoiceItems(retry, defaultOption, new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int item) {
//    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
//    	        SharedPreferences.Editor editor = settings.edit();
//    	        int[] retryValue = getResources().getIntArray(R.array.retry_value);
//    	    	editor.putInt("retry", retryValue[item]);
//    	        editor.commit();
//    	        dialog.dismiss();
//    	    }
//    	});
//    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface i, int button)  {}
//    	});
//    	builder.show();
//    }
//    
//    private void showColorDialog() {
//    	String cancelButton = null;
//    	SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
//    	String color[] = null, colorTitle = null;
//    	
//    	int currentValue = settings.getInt("color", 0);
//    	
//    	if (language == Misc.CHINESE) {
//    		color = this.getResources().getStringArray(R.array.color_chi);
//    		colorTitle = this.getResources().getString(R.string.sel_color_chi);
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_chi);
//    	} else {
//    		color = this.getResources().getStringArray(R.array.color_eng);
//    		colorTitle = this.getResources().getString(R.string.sel_color_eng);
//    		cancelButton = this.getResources().getString(R.string.btn_cancel_eng);
//    	}
//    	
//    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
//    	builder.setTitle(colorTitle);
//    	builder.setSingleChoiceItems(color, currentValue, new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int item) {
//    	    	SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
//    	        SharedPreferences.Editor editor = settings.edit();
//    	    	editor.putInt("color", item);
//    	        editor.commit();
//    	        if (item == 3) {
//    	        	showCustomBackground();
//    	        }
//    	        dialog.dismiss();
//    	    }
//    	    private void showCustomBackground() {
//    	    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
//    	    	final View view = SeekbarFactory.getInstance(HKOTyphoonOption.this, R.layout.color_diag, HKOTyphoonOption.this, language, SeekbarFactory.BACK_OPTION);
//    	    	builder.setView(view);
//    	    	String chooseText = null, okText = null, cancelText = null;
//    	    	if (language == Misc.CHINESE) {
//    	    		chooseText = getString(R.string.choose_col_chi);
//    	    		okText = getString(R.string.btn_ok_chi);
//    	    		cancelText = getString(R.string.btn_cancel_chi);
//    	    	} else {
//    	    		chooseText = getString(R.string.choose_col_eng);
//    	    		okText = getString(R.string.btn_ok_eng);
//    	    		cancelText = getString(R.string.btn_cancel_eng);
//    	    	}
//    	    	builder.setTitle(chooseText);
//    	    	builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
//    				
//    				@Override
//    				public void onClick(DialogInterface dialog, int which) {
//    					SeekBar red = (SeekBar) view.findViewById(R.id.red);
//    					SeekBar blue = (SeekBar) view.findViewById(R.id.blue);
//    					SeekBar green = (SeekBar) view.findViewById(R.id.green);
//    					SeekBar alpha = (SeekBar) view.findViewById(R.id.alpha);
//    					CheckBox greyScale = (CheckBox) view.findViewById(R.id.grey);
//    					// TODO Auto-generated method stub
//    					SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
//    	    	        SharedPreferences.Editor editor = settings.edit();
//    	    	        int shadowColor = (alpha.getProgress() / 2) << 24 | 0x3d << 16 |
//    	    	        					0x3d << 8 | 0x3d;
//    	    	    	
//    	    	        Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.testback);
//    					float r = red.getProgress() / 255f;
//    					float g = green.getProgress() / 255f;
//    					float b = blue.getProgress() / 255f;
//    					float a = alpha.getProgress() / 255f;
//    	    	        ColorMatrix cm = new ColorMatrix(new float[] {  
//    	    	        		r , 0f, 0f, 0f, 0f,  
//    	    	        		0f, g , 0f, 0f, 0f,  
//    	    	        		0f, 0f, b , 0f, 0f,  
//    	    	        		0f, 0f, 0f, a , 0f});
//
//    	    	        Bitmap newMap = Bitmap.createBitmap(map.getWidth(), map.getHeight(), Bitmap.Config.ARGB_8888);
//    	    	        Canvas canvas = new Canvas(newMap);
//    	    	        Paint paint = new Paint();
//    	    	        //paint.setAlpha(0);
//    	    	        //paint.setShadowLayer(4.0f, 1.5f, 1.5f, shadowColor);
//    	    	        
//    	    	        if (greyScale.isChecked()) {
//    	    	        	paint.setColorFilter(new PorterDuffColorFilter(shadowColor, PorterDuff.Mode.SRC_IN)); 
//    	    	        	canvas.translate(5, 5);
//    	    	        	canvas.drawBitmap(map, 0f, 0f, paint);
//    	    	        	canvas.save();
//    	    	        	canvas.translate(-5, -5);
//    	    	        }
//    	    	        paint.setColorFilter(new ColorMatrixColorFilter(cm));
//    	    	        canvas.drawBitmap(map, 0f, 0f, paint);
//
//    	    	        //canvas.restore();
//						ObjectHandler oh = new ObjectHandler(getApplicationContext(), language);
//						oh.writeBackground(newMap);
//    	    	        
//    	    	        
//    	    	    	editor.putInt("backRed", red.getProgress());
//    	    	    	editor.putInt("backGreen", green.getProgress());
//    	    	    	editor.putInt("backBlue", blue.getProgress());
//    	    	    	editor.putInt("backAlpha", alpha.getProgress());
//    	    	    	editor.putBoolean("backGreyScale", greyScale.isChecked());
//    	    	        editor.commit();
//    	    	        dialog.dismiss();
//    				}
//    			});
//    	    	builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
//    				@Override
//    				public void onClick(DialogInterface dialog, int which) {}
//    			});
//    	    	builder.show();
//    	    }
//    	});
//    	builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface i, int button)  {}
//    	});
//    	builder.show();
//    }
//    
//    private void showTextDialog() {
//    	AlertDialog.Builder builder = new AlertDialog.Builder(HKOTyphoonOption.this);
//    	final View view = SeekbarFactory.getInstance(HKOTyphoonOption.this, R.layout.color_diag, this, language, SeekbarFactory.FONT_OPTION);
//    	builder.setView(view);
//    	
//    	String chooseText = null, okText = null, cancelText = null;
//    	if (language == Misc.CHINESE) {
//    		chooseText = this.getString(R.string.choose_col_chi);
//    		okText = this.getString(R.string.btn_ok_chi);
//    		cancelText = this.getString(R.string.btn_cancel_chi);
//    	} else {
//    		chooseText = this.getString(R.string.choose_col_eng);
//    		okText = this.getString(R.string.btn_ok_eng);
//    		cancelText = this.getString(R.string.btn_cancel_eng);
//    	}
//    	builder.setTitle(chooseText);
//    	builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				SeekBar red = (SeekBar) view.findViewById(R.id.red);
//				SeekBar blue = (SeekBar) view.findViewById(R.id.blue);
//				SeekBar green = (SeekBar) view.findViewById(R.id.green);
//				SeekBar alpha = (SeekBar) view.findViewById(R.id.alpha);
//				CheckBox greyScale = (CheckBox) view.findViewById(R.id.grey);
//				// TODO Auto-generated method stub
//				SharedPreferences settings = getApplicationContext().getSharedPreferences(HKOTyphoonOption.PREFS_NAME, 0);
//    	        SharedPreferences.Editor editor = settings.edit();
//    	        int color = alpha.getProgress() << 24 | red.getProgress() << 16 | green.getProgress() << 8 | blue.getProgress();
//    	    	editor.putInt("fontColor", color);
//    	    	editor.putInt("fontRed", red.getProgress());
//    	    	editor.putInt("fontGreen", green.getProgress());
//    	    	editor.putInt("fontBlue", blue.getProgress());
//    	    	editor.putInt("fontAlpha", alpha.getProgress());
//    	    	editor.putBoolean("fontGreyScale", greyScale.isChecked());
//    	        editor.commit();
//    	        dialog.dismiss();
//			}
//		});
//    	builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {}
//		});
//    	builder.show();
//    }

}