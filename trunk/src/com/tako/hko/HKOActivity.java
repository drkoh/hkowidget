package com.tako.hko;

import java.util.ArrayList;

import com.tako.hko.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class HKOActivity extends TabActivity {
	private static final String URI_SMALL_HEADER = "hkosmallwidget://widget/id";
	private static final String URI_LARGE_HEADER = "hkolargewidget://widget/id";
	private final int MENU_REFRESH = 0;
	private final int MENU_OPTION = 1;
	private final int MENU_ABOUT  = 2;
	
	private static final String TAG = "HKOActivity";
	private static final String PREFS_NAME = "HKOPrefs";
	private static int language = -1;
	private int widgetId = -1;
	private final int LOADING_DIALOG = 10;
	
	// Tab 1 UI

	private HKOConnect dataMine = null;
	private Warnings warningOutput = null;
	private Warnings typhoonOutput = null;
	private CurrentWeatherWrapper warningIcon = null;
	private ImageView tab1IconFire = null;
	private ImageView tab1IconTemp = null;
	private ImageView tab1IconTyphoon = null;
	private ImageView tab1IconOther = null;
	private String forecastOutput = "";
	private int todayIcon;
	private TextView tab1Warning = null;
	private TextView tab1Display = null;
	private ImageView tab1Image = null;
	private Context context = null;
	private ImageView typhoonPos = null;
	private Bitmap typhoonPosImg = null;
	private TextView typhoonDesc = null;
	
	// Tab 2 UI
	private String[] forecastText = null;
	private int[] forecastIcons = null;
	private TextView summary = null;
	private TextView tab2Day1 = null;
	private TextView tab2Day2 = null;
	private TextView tab2Day3 = null;
	private TextView tab2Day4 = null;
	private TextView tab2Day5 = null;
	private TextView tab2Day6 = null;
	private TextView tab2Day7 = null;
	private ImageView tab2Icon1 = null;
	private ImageView tab2Icon2 = null;
	private ImageView tab2Icon3 = null;
	private ImageView tab2Icon4 = null;
	private ImageView tab2Icon5 = null;
	private ImageView tab2Icon6 = null;
	private ImageView tab2Icon7 = null;
	
	// Tab 3 UI
	CurrentWeatherWrapper wrapper = null;
	ScrollView sv = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
    	language = settings.getInt("language", 0);
    	
    	widgetId = this.getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
    	Log.e(TAG, "Widget ID = " + widgetId);
    	
    	String tab1Text = null, tab2Text = null, tab3Text = null, tab4Text = null;
    	if (language == Misc.CHINESE) {
       		tab1Text = this.getApplicationContext().getString(R.string.tab1_chi);
       		tab2Text = this.getApplicationContext().getString(R.string.tab2_chi);
       		tab3Text = this.getApplicationContext().getString(R.string.tab3_chi);  
       		tab4Text = this.getApplicationContext().getString(R.string.tab4_chi);
       		//tab5Text = this.getApplicationContext().getString(R.string.tab5_chi);
    	}
    	else {
    		tab1Text = this.getApplicationContext().getString(R.string.tab1_eng);
    		tab2Text = this.getApplicationContext().getString(R.string.tab2_eng);
    		tab3Text = this.getApplicationContext().getString(R.string.tab3_eng);
    		tab4Text = this.getApplicationContext().getString(R.string.tab4_eng);
       		//tab5Text = this.getApplicationContext().getString(R.string.tab5_eng);
    	}
    	
        TabHost mTabHost = getTabHost();
     	
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(tab1Text).setContent(R.id.todaytab));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(tab2Text).setContent(R.id.sevendaytab));
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator(tab3Text).setContent(R.id.localtab));
        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator(tab4Text).setContent(R.id.airtab));
        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(
        		new TabHost.OnTabChangeListener() {
        			public void onTabChanged(String tabId) {
        		    	Log.e(TAG, "Showed");

        				if (tabId.equals("tab1")) getData(0);
        				else if (tabId.equals("tab2")) getData(1);
        				else if (tabId.equals("tab3")) getData(2);
        				else if (tabId.equals("tab4")) getData(3);
        				Log.e(TAG, "Dismiss");
        				//pBar.dismiss();
        			}
        		}
		);
        this.getData(0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
    	
    	if (resultCode == 0) {
    		SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        	language = settings.getInt("language", 0);

    		String tab1Text = null, tab2Text = null, tab3Text = null, tab4Text = null;
    		if (language == Misc.CHINESE) {
    			tab1Text = this.getApplicationContext().getString(R.string.tab1_chi);
    			tab2Text = this.getApplicationContext().getString(R.string.tab2_chi);
    			tab3Text = this.getApplicationContext().getString(R.string.tab3_chi);
    			tab4Text = this.getApplicationContext().getString(R.string.tab4_chi);
    		}
    		else {
    			tab1Text = this.getApplicationContext().getString(R.string.tab1_eng);
    			tab2Text = this.getApplicationContext().getString(R.string.tab2_eng);
    			tab3Text = this.getApplicationContext().getString(R.string.tab3_eng);
    			tab4Text = this.getApplicationContext().getString(R.string.tab4_eng);
    		}

    		TabHost mTabHost = getTabHost();
    		int currentTab = mTabHost.getCurrentTab();
    		
    		//  Update the text of the tabs!!
    		// http://www.anddev.org/viewtopic.php?p=11984
            ArrayList<View> views = mTabHost.getTabWidget().getChildAt(0).getTouchables();
            RelativeLayout relLayout = (RelativeLayout) views.get(0);
            TextView tv = (TextView)relLayout.getChildAt(1);
            tv.setText(tab1Text);
            
            views = mTabHost.getTabWidget().getChildAt(1).getTouchables();
            relLayout = (RelativeLayout) views.get(0);
            tv = (TextView) relLayout.getChildAt(1);
            tv.setText(tab2Text);
            
            views = mTabHost.getTabWidget().getChildAt(2).getTouchables();
            relLayout = (RelativeLayout) views.get(0);
            tv = (TextView) relLayout.getChildAt(1);
            tv.setText(tab3Text);
            
            views = mTabHost.getTabWidget().getChildAt(3).getTouchables();
            relLayout = (RelativeLayout) views.get(0);
            tv = (TextView) relLayout.getChildAt(1);
            tv.setText(tab4Text);
            
            //  Try to update the widget!
            this.sendWidget(true);
            
            this.getData(currentTab);
    	}
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_REFRESH, 0, R.string.refresh_chi).setIcon(android.R.drawable.ic_menu_rotate);
    	menu.add(0, MENU_OPTION, 1, R.string.option_chi).setIcon(android.R.drawable.ic_menu_preferences);
    	menu.add(0, MENU_ABOUT, 2, R.string.about_chi).setIcon(android.R.drawable.btn_star_big_on);
    	return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	if (language == Misc.CHINESE) {
    	   	menu.add(0, MENU_REFRESH, 0, R.string.refresh_chi).setIcon(android.R.drawable.ic_menu_rotate);
        	menu.add(0, MENU_OPTION, 1, R.string.option_chi).setIcon(android.R.drawable.ic_menu_preferences);
        	menu.add(0, MENU_ABOUT, 2, R.string.about_chi).setIcon(android.R.drawable.btn_star_big_on);	
    	} else {
    	   	menu.add(0, MENU_REFRESH, 0, R.string.refresh_eng).setIcon(android.R.drawable.ic_menu_rotate);
        	menu.add(0, MENU_OPTION, 1, R.string.option_eng).setIcon(android.R.drawable.ic_menu_preferences);
        	menu.add(0, MENU_ABOUT, 2, R.string.about_eng).setIcon(android.R.drawable.btn_star_big_on);	
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
   // public void  setOnTabChangedListener (TabHost.OnTabChangeListener l)
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	switch(item.getItemId()) {
    		case MENU_REFRESH:
    			String updateString = null;
    			if (language == Misc.CHINESE)
    				updateString = this.getString(R.string.updated_chi);
    			else
    				updateString = this.getString(R.string.updated_eng);
    			
    			this.getData(this.getTabHost().getCurrentTab());
    			
    			//  Update widget information
    			//this.sendWidget(true);
    			
    			Log.e(TAG, "Refreshed");
    			Toast.makeText(this.getApplicationContext(), updateString, Toast.LENGTH_LONG).show();
    			break;
    		case MENU_OPTION:
    			Intent optionIntent = new Intent(HKOActivity.this, HKOOption.class);
    			this.startActivityForResult(optionIntent, 0);
    			break;
    		case MENU_ABOUT:
    			this.showAboutDialog();
    			break;
    	}
    	return true;
    }
    
    private Handler tab1Handler = new Handler() {
    	public void handleMessage(Message msg) {
    		if (forecastOutput == null) {
    			tab1Image.setImageResource(R.drawable.empty);
    			if (language == Misc.CHINESE)
    				tab1Display.setText(getString(R.string.cannot_connect_chi));
    			else
    				tab1Display.setText(getString(R.string.cannot_connect_eng));
    		} else {
    			Log.e(TAG, "warning is null ? " + (warningIcon == null));
    			Log.e(TAG, "Icon is null ? " + (tab1IconFire == null));
    			Log.e(TAG, "Fire : " + warningIcon.getFireWarning());
    			Log.e(TAG, "Other : " + warningIcon.getOther());
//    			tab1IconFire.setMaxHeight(20);
    			tab1IconFire.setImageResource(warningIcon.getFireWarning());
    			tab1IconTemp.setImageResource(warningIcon.getExtremeTemp());
    			tab1IconTyphoon.setImageResource(warningIcon.getTyphoon());
    			tab1IconOther.setImageResource(warningIcon.getOther());
    			
    			tab1Warning.setText(warningOutput.getOtherWarning());
    			tab1Display.setText(forecastOutput);
    			tab1Image.setImageResource(todayIcon);
    		}
    		
    	}
    };
   
    private Handler tab2Handler = new Handler() {
    	public void handleMessage(Message msg) {
    		if (forecastText == null) {
    			if (language == Misc.CHINESE)
    				summary.setText(getString(R.string.cannot_connect_chi));
    			else 
    				summary.setText(getString(R.string.cannot_connect_eng));
    			return;
    		}
    		if (forecastText.length >= 8) {
    			summary.setText(forecastText[0]);

    			tab2Day1.setText(forecastText[1]);
    			tab2Day2.setText(forecastText[2]);
    			tab2Day3.setText(forecastText[3]);
    			tab2Day4.setText(forecastText[4]);
    			tab2Day5.setText(forecastText[5]);
    			tab2Day6.setText(forecastText[6]);
    			tab2Day7.setText(forecastText[7] + "\n\n");
    			tab2Icon1.setImageResource(forecastIcons[0]);
    			tab2Icon2.setImageResource(forecastIcons[1]);
    			tab2Icon3.setImageResource(forecastIcons[2]);
    			tab2Icon4.setImageResource(forecastIcons[3]);
    			tab2Icon5.setImageResource(forecastIcons[4]);
    			tab2Icon6.setImageResource(forecastIcons[5]);
    			tab2Icon7.setImageResource(forecastIcons[6]);
        	} else {
        		summary.setText(forecastText[0]);
        		tab2Day1.setText("");
    			tab2Day2.setText("");
    			tab2Day3.setText("");
    			tab2Day4.setText("");
    			tab2Day5.setText("");
    			tab2Day6.setText("");
    			tab2Day7.setText("");
    			tab2Icon1.setImageResource(R.drawable.empty);
    			tab2Icon2.setImageResource(R.drawable.empty);
    			tab2Icon3.setImageResource(R.drawable.empty);
    			tab2Icon4.setImageResource(R.drawable.empty);
    			tab2Icon5.setImageResource(R.drawable.empty);
    			tab2Icon6.setImageResource(R.drawable.empty);
    			tab2Icon7.setImageResource(R.drawable.empty);
            }
    	}
    };
    
    private Handler tab3Handler = new Handler() {
    	public void handleMessage(Message msg) {
    		sv.removeAllViews();
    		
    		if (wrapper == null) {
    			TextView error = new TextView(context);
    			if (language == Misc.CHINESE)
    				error.setText(getString(R.string.cannot_connect_chi));
    			else 
    				error.setText(getString(R.string.cannot_connect_eng));
    			sv.addView(error);
    			return;
    		}
    		
    		String[] areas = wrapper.getAreas();
    		TableLayout tl = new TableLayout(context);
    		TableRow tr = null;
    		TextView tv1 = null, tv2 = null;
    		for (String area : areas) {
    			tr = new TableRow(context);
    			tv1 = new TextView(context);
    			tv2 = new TextView(context);
    			tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
    			tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
    			if (language == Misc.CHINESE)
    				tv1.setText(area.replaceAll(" ", "") + "\t");
    			else
    				tv1.setText(area + "\t");
    			tv2.setText(wrapper.getLocalTemperature(area.toString()));	
    			tr.addView(tv1);
    			tr.addView(tv2);
    			tl.addView(tr);
    			
    		}
    		sv.addView(tl);
    	}
    };
    
    private Handler tab4Handler = new Handler() {
    	public void handleMessage(Message msg) {
    		typhoonPos.setImageBitmap(typhoonPosImg);
    		typhoonDesc.setText(typhoonOutput.getTyphoonWarning());
    	}
    };
    
    private void getData(int tabNo) {
    	showDialog(LOADING_DIALOG);
    	
    	//Handler handler = new Handler();
    	context = this.getApplicationContext();
    	
    	
    	switch (tabNo) {
    	case 0:
    		tab1Display = (TextView) this.findViewById(R.id.weather_tab);
    		tab1Image = (ImageView) this.findViewById(R.id.tab1_image);
    		tab1Warning = (TextView) this.findViewById(R.id.warning_text);
    		tab1IconFire = (ImageView) this.findViewById(R.id.warning_fire);
    		tab1IconTemp = (ImageView) this.findViewById(R.id.warning_temp);
    		tab1IconTyphoon = (ImageView) this.findViewById(R.id.warning_typhoon);
    		tab1IconOther = (ImageView) this.findViewById(R.id.warning_other);
	    	
    		//String forecastOutput = dataMine.getWeatherForecast(this.getApplicationContext(), language);
    		
    		new Thread(){
    			public void run(){
    				dataMine = new HKOConnect();
    				warningOutput = dataMine.getWarning(context, language);
    				warningIcon = dataMine.getCurrentWeather(context, Misc.ENGLISH, true);
    				    				
    				if (warningOutput != null && !warningOutput.getOtherWarning().equals("")) {
    					warningOutput.setOtherWarning(warningOutput.getOtherWarning() + "\n\n" +
    							"------------------------------------------------------------");
    				}
    				
    				forecastOutput = dataMine.getWeatherForecast(context, language);
    				
    				forecastOutput += "\n\n" ;
    				forecastOutput += "------------------------------------------------------------\n\n";
    				Pollution pollution = dataMine.getAirPollution(context, language);
    				forecastOutput += pollution.getCurrent() + "\n";
    				forecastOutput += pollution.getForecast() + "\n\n\n";
    				
    				todayIcon = dataMine.getForecastIcon(context);
    				tab1Handler.sendEmptyMessage(0);
    				removeDialog(LOADING_DIALOG);
    			}
    		}.start();
        	
   			
    		break;
    	case 1:
    		summary = (TextView) this.findViewById(R.id.summary7days);
    		tab2Day1 = (TextView) this.findViewById(R.id.day1text);
    		tab2Day2 = (TextView) this.findViewById(R.id.day2text);
    		tab2Day3 = (TextView) this.findViewById(R.id.day3text);
    		tab2Day4 = (TextView) this.findViewById(R.id.day4text);
    		tab2Day5 = (TextView) this.findViewById(R.id.day5text);
    		tab2Day6 = (TextView) this.findViewById(R.id.day6text);
    		tab2Day7 = (TextView) this.findViewById(R.id.day7text);
    		
    		tab2Icon1 = (ImageView) this.findViewById(R.id.day1icon);
    		tab2Icon2 = (ImageView) this.findViewById(R.id.day2icon);
    		tab2Icon3 = (ImageView) this.findViewById(R.id.day3icon);
    		tab2Icon4 = (ImageView) this.findViewById(R.id.day4icon);
    		tab2Icon5 = (ImageView) this.findViewById(R.id.day5icon);
    		tab2Icon6 = (ImageView) this.findViewById(R.id.day6icon);
    		tab2Icon7 = (ImageView) this.findViewById(R.id.day7icon);
    		
    		new Thread(){
    			public void run(){
    				dataMine = new HKOConnect();
    				forecastText = dataMine.get7DaysForecast(context, language);
    	    		forecastIcons = dataMine.get7DaysIcon(context, language);
    	    		
    				tab2Handler.sendEmptyMessage(0);
    				removeDialog(LOADING_DIALOG);
    			}
    		}.start();
    		 
    		break;
    	case 2:
    		sv = (ScrollView) this.findViewById(R.id.localtab);
    		new Thread(){
    			public void run(){
    				dataMine = new HKOConnect();
    				wrapper = dataMine.getCurrentWeather(context, language, false);
    				
    				tab3Handler.sendEmptyMessage(0);
    				removeDialog(LOADING_DIALOG);
    			}
    		}.start();
    		
    		break;
    	case 3:
    		typhoonPos = (ImageView) this.findViewById(R.id.typhoon_pos);
    		typhoonDesc = (TextView) this.findViewById(R.id.typhoon_text);
    		
    		new Thread(){
    			public void run(){
    				dataMine = new HKOConnect();
    				CurrentWeatherWrapper isTyphoon = dataMine.getCurrentWeather(context, Misc.ENGLISH, true);

    				if (isTyphoon.getTyphoonNo() == 0) {
    	  				typhoonPosImg = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888);
    	  				if (language == Misc.CHINESE)
    	  					typhoonOutput = new Warnings("現時並無熱帶氣旋警告。", null);
    	  				else
    	  					typhoonOutput = new Warnings("There is no tropical cyclone warning signal.", null);
    				} else {
    					typhoonPosImg = dataMine.getTyphoonPos(context, language);
        				typhoonOutput = dataMine.getWarning(context, language);
    				}
    				 
    				tab4Handler.sendEmptyMessage(0);
    				removeDialog(LOADING_DIALOG);
    			}
    		}.start();
    		
    		break;
    	}

    }
    
    private void showAboutDialog() {
    	View view = View.inflate(HKOActivity.this, R.layout.about, null);   
    	TextView textView = (TextView) view.findViewById(R.id.aboutText); 
    	//PackageManager pm = getPackageManager();
    	textView.setText("HK Weather Widget 5.18\n" +
				   "Tako Au 2009\n" +
				   "\n" +
				   "Special Thanks:\n" +
				   "Chris_C已切, Maize\n" +
				   "Dedicate to Vikkilein");   
    	new AlertDialog.Builder(HKOActivity.this).setTitle(   
    	        R.string.about_eng).setView(view)   
    	        .setPositiveButton(android.R.string.ok, null)   
    	        .setIcon(R.drawable.logo).show();
    }
    
    //  Helper method
    private void sendWidget(boolean updateFlag) {
		SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
		Intent widgetUpdate = new Intent();
		widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{widgetId});
		widgetUpdate.putExtra("updateFlag", updateFlag);
		if (settings.getString("hkoid_" + widgetId, "small").equals("small"))
			widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_SMALL_HEADER), String.valueOf(widgetId)));
		else
			widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_LARGE_HEADER), String.valueOf(widgetId)));
		//PendingIntent newPending = PendingIntent.getBroadcast(this, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
		this.sendBroadcast(widgetUpdate);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	ProgressDialog loadingDialog = null;
    	if(id == LOADING_DIALOG) {
    		loadingDialog = new ProgressDialog(this);
    		loadingDialog.setMessage("Loading...");
    		loadingDialog.setIndeterminate(true);
    		loadingDialog.setCancelable(true);
    		return loadingDialog;
    	}
    	return super.onCreateDialog(id);
    }
    
    
}