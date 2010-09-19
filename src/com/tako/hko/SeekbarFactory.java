package com.tako.hko;

import com.tako.hko.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekbarFactory {
	public static final int FONT_OPTION = 74;
	public static final int BACK_OPTION = 87;
	private static final String PREFS_NAME = "HKOPrefs";
	private static final String TAG = "SeekbarFactory";
	private static boolean greyScale = false;

	public static View getInstance(Context context, int layoutId, Activity activity, int language, int option) {
		SeekbarFactory sb = new SeekbarFactory();
		return sb.makeSeekbar(context, layoutId, activity, language, option);
	}
	
	protected SeekbarFactory() {}
	
	protected View makeSeekbar(Context context, int layoutId, Activity activity, int language, int option) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, (ViewGroup) activity.findViewById(R.id.layout_root));

		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		int color = -1, redValue = -1, greenValue = -1, blueValue = -1, alphaValue = -1;
		boolean greyValue = false;
		if (option == SeekbarFactory.FONT_OPTION) {
			color = settings.getInt("fontColor", 0xffd3d3d9);
			redValue = settings.getInt("fontRed", 211);
			greenValue = settings.getInt("fontGreen", 211);
			blueValue = settings.getInt("fontBlue", 211);
			alphaValue = settings.getInt("fontAlpha", 255);
			greyValue = settings.getBoolean("fontGreyScale", false);
		} else {
			color = settings.getInt("backColor", 0xffd3d3d9);
			redValue = settings.getInt("backRed", 211);
			greenValue = settings.getInt("backGreen", 211);
			blueValue = settings.getInt("backBlue", 211);
			alphaValue = settings.getInt("backAlpha", 255);
			greyValue = settings.getBoolean("backGreyScale", false);
		}
		
		String redLabel = null, greenLabel = null, blueLabel = null, alphaLabel = null, greyLabel = null;
		if (language == Misc.CHINESE) {
			redLabel = context.getString(R.string.red_chi);
			greenLabel = context.getString(R.string.green_chi);
			blueLabel = context.getString(R.string.blue_chi);
			alphaLabel = context.getString(R.string.alpha_chi);
			if (option == SeekbarFactory.BACK_OPTION)
				greyLabel = context.getString(R.string.shadow_chi);
			else
				greyLabel = context.getString(R.string.grey_chi);
		} else {
			redLabel = context.getString(R.string.red_eng);
			greenLabel = context.getString(R.string.green_eng);
			blueLabel = context.getString(R.string.blue_eng);
			alphaLabel = context.getString(R.string.alpha_eng);
			if (option == SeekbarFactory.BACK_OPTION)
				greyLabel = context.getString(R.string.shadow_eng);
			else
				greyLabel = context.getString(R.string.grey_eng);
		}
		
		OnSeekBarChangeListener fontSeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				showFontColor(seekBar);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				showFontColor(seekBar);
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				showFontColor(seekBar);
			}
		};
		
		OnSeekBarChangeListener backSeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				showBackColor(seekBar);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				showBackColor(seekBar);
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				showBackColor(seekBar);
			}
		};
		
		TextView redText = (TextView) layout.findViewById(R.id.redLabel);
		redText.setText(redLabel);
		TextView greenText = (TextView) layout.findViewById(R.id.greenLabel);
		greenText.setText(greenLabel);
		TextView blueText = (TextView) layout.findViewById(R.id.blueLabel);
		blueText.setText(blueLabel);
		TextView alphaText = (TextView) layout.findViewById(R.id.alphaLabel);
		alphaText.setText(alphaLabel);
		TextView sample = (TextView) layout.findViewById(R.id.sample);
		ImageView sampleBack = (ImageView) layout.findViewById(R.id.sample_back);
		if (option == SeekbarFactory.FONT_OPTION)
			sampleBack.setVisibility(View.GONE);
		else
			sample.setVisibility(View.GONE);
		
		SeekBar red = (SeekBar) layout.findViewById(R.id.red);
		red.setMax(255);
		red.setProgress(redValue);

		SeekBar green = (SeekBar) layout.findViewById(R.id.green);
		green.setMax(255);
		green.setProgress(greenValue);
		SeekBar blue = (SeekBar) layout.findViewById(R.id.blue);
		blue.setMax(255);
		blue.setProgress(blueValue);
		SeekBar alpha = (SeekBar) layout.findViewById(R.id.alpha);
		alpha.setMax(255);
		alpha.setProgress(alphaValue);

		if (option == SeekbarFactory.FONT_OPTION) {
			red.setOnSeekBarChangeListener(fontSeekBarListener);
			green.setOnSeekBarChangeListener(fontSeekBarListener);
			blue.setOnSeekBarChangeListener(fontSeekBarListener);
			alpha.setOnSeekBarChangeListener(fontSeekBarListener);
		} else {
			red.setOnSeekBarChangeListener(backSeekBarListener);
			green.setOnSeekBarChangeListener(backSeekBarListener);
			blue.setOnSeekBarChangeListener(backSeekBarListener);
			alpha.setOnSeekBarChangeListener(backSeekBarListener);
		}
		
		CheckBox grey = (CheckBox) layout.findViewById(R.id.grey);
		grey.setChecked(greyValue);
		grey.setText(greyLabel);
		grey.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				Log.e(TAG, "Clicked : " + greyScale);
				greyScale = isChecked;					
			}
		});
		

		if (option == SeekbarFactory.BACK_OPTION) {
			ColorMatrix cm = new ColorMatrix(new float[] {  
					redValue / 255f , 0f, 0f, 0f, 0f,  
					0f, greenValue / 255f, 0f, 0f, 0f,  
					0f, 0f, blueValue / 255f, 0f, 0f,  
					0f, 0f, 0f, alphaValue / 255f, 0f});
			
			sampleBack.setColorFilter(new ColorMatrixColorFilter(cm));
		} else
			sample.setTextColor(color);
		
		return layout;
	}
	
	private void showFontColor(SeekBar seekBar) {
		LinearLayout layout = (LinearLayout) seekBar.getParent();
		SeekBar blue = (SeekBar) layout.findViewById(R.id.blue);
		SeekBar green = (SeekBar) layout.findViewById(R.id.green);
		SeekBar red = (SeekBar) layout.findViewById(R.id.red);
		SeekBar alpha = (SeekBar) layout.findViewById(R.id.alpha);
		TextView sample = (TextView) layout.findViewById(R.id.sample);
		
		//Log.e(TAG, "GreyScale : " + greyScale);
		if (greyScale && seekBar.getId() != R.id.alpha) {
			int greyValue = seekBar.getProgress();
			blue.setProgress(greyValue);
			red.setProgress(greyValue);
			green.setProgress(greyValue);
		}
		int color = alpha.getProgress() << 24 | red.getProgress() << 16 | green.getProgress() << 8 | blue.getProgress();
		sample.setTextColor(color);
		
	}
	
	private void showBackColor(SeekBar seekBar) {
		LinearLayout layout = (LinearLayout) seekBar.getParent();
		SeekBar blue = (SeekBar) layout.findViewById(R.id.blue);
		SeekBar green = (SeekBar) layout.findViewById(R.id.green);
		SeekBar red = (SeekBar) layout.findViewById(R.id.red);
		SeekBar alpha = (SeekBar) layout.findViewById(R.id.alpha);
		ImageView sample = (ImageView) layout.findViewById(R.id.sample_back);
		
		//Log.e(TAG, "GreyScale : " + greyScale);
//		if (greyScale && seekBar.getId() != R.id.alpha) {
//			int greyValue = seekBar.getProgress();
//			blue.setProgress(greyValue);
//			red.setProgress(greyValue);
//			green.setProgress(greyValue);
//		}
		//int color = alpha.getProgress() << 24 | red.getProgress() << 16 | green.getProgress() << 8 | blue.getProgress();
		float a = alpha.getProgress() / 255f;
		float r = red.getProgress() / 255f;
		float g = green.getProgress() / 255f;
		float b = blue.getProgress() / 255f;
	//	BitmapDrawable
		ColorMatrix cm = new ColorMatrix(new float[] {  
				r , 0f, 0f, 0f, 0f,  
				0f, g , 0f, 0f, 0f,  
				0f, 0f, b , 0f, 0f,  
				0f, 0f, 0f, a , 0f});
		
		sample.setColorFilter(new ColorMatrixColorFilter(cm));
		//sample.setTextColor(color);
		
	}
}
