package com.yamin.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yamin.reader.R;

/**
 * 
 * @ClassName: CustomDialog
 * @Description:自定义DIALOg
 * @author ymcao
 * @date 2013-6-23 下午2:14:01
 * 
 */
public class LoadingDialog extends Dialog {
	private static int default_width = ViewGroup.LayoutParams.WRAP_CONTENT;; // 默认宽度
	private static int default_height = ViewGroup.LayoutParams.WRAP_CONTENT;// 默认高度
	private static LoadingDialog customProgressDialog = null; 
    private Context context = null;
    private ImageView spaceshipImage ;
	public LoadingDialog(Context context, String msg) {
		this(context, default_width, default_height, R.style.Theme_dialog, msg);
	}

	public LoadingDialog(Context context, int width, int height, int style,
			String msg) {
		super(context, style);
		this.context=context;
		// set content
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		spaceshipImage = (ImageView) v.findViewById(R.id.loadingimg);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		tipTextView.setText(msg);// 设置加载信息
		setContentView(v);
		// set window params
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// set width,height by density and gravity
		float density = getDensity(context);
		params.width = (int) (width * density);
		params.height = (int) (height * density);
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}
	 public void onWindowFocusChanged(boolean hasFocus){  
	        if (customProgressDialog == null){  
	            return;  
	        }  
	    	// 加载动画
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					context, R.anim.loading_anim);
			// 使用ImageView显示动画
			spaceshipImage.startAnimation(hyperspaceJumpAnimation);
	    }  
	public static LoadingDialog createDialog(Context context,String msg){  
        customProgressDialog = new LoadingDialog(context,msg);  
        return customProgressDialog;  
    }  
	
	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

}
