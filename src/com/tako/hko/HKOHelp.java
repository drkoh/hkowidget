package com.tako.hko;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HKOHelp extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = (TextView) this.findViewById(R.id.instruction);
		tv.setText("Thanks for using HK Weather Widget!\n\n" +
				"I am sorry that you cannot open this widget as it is a pure widget.  " +
				"Instead you can long-press the empty area in home screen then select 'Widget'->'Hong Kong Weather'." +
				"That is!!\n\n " +
				"After the widget shows up you can click the circled area to update the weather instantly, " +
				"or click the rest area to get the weather detail and the setup screen and more!!");
		Button b1 = (Button) this.findViewById(R.id.closeButton);
		b1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		this.setContentView(R.layout.help_layout);
		
	}

}
