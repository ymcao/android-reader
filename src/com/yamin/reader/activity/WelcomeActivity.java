package com.yamin.reader.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.yamin.reader.R;
public class WelcomeActivity  extends Activity{
	   //延迟2.0秒 
		private final long DISPLAY_DURATION = 2000; 
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //取消标题栏
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //全屏
	        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
	                      WindowManager.LayoutParams. FLAG_FULLSCREEN);
	        setContentView(R.layout.book_welcome);	
		
	        new Handler().postDelayed(new Runnable(){   
	                
	            public void run() {   
	                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
	                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                WelcomeActivity.this.startActivity(intent);  
	                WelcomeActivity.this.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
	                WelcomeActivity.this.finish();   
	            }           
	        }, DISPLAY_DURATION);   
			
	    }
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
		}
		
}
