package com.yamin.reader.activity;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.yamin.reader.R;
import com.yamin.reader.view.SwitchButton;
public class BookSettingsActivity  extends Activity implements OnCheckedChangeListener{

	//private CusttomSlipSwitch slipswitch_MSL;
	private SwitchButton main_myslipswitch;
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.book_settings);
	        ActionBar bar=this.getActionBar();
	        bar.setDisplayHomeAsUpEnabled(true);
	        bar.setDisplayShowTitleEnabled(true);
	        bar.setTitle(R.string.book_read_settings);
	        main_myslipswitch=(SwitchButton)findViewById(R.id.main_myslipswitch);
	        main_myslipswitch.setOnCheckedChangeListener(this);
		}
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				Toast.makeText(this, "checked", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "unchecked", Toast.LENGTH_SHORT).show();
			}
		}
}
