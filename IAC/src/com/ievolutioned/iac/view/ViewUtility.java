package com.ievolutioned.iac.view;

import com.ievolutioned.iac.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

@SuppressLint("InflateParams")
public class ViewUtility {
	/**
	 * Create a loading screen, using an {@link AlertDialog} class
	 * 
	 * @param context the context the control will be shown
	 * @return AlertDialog with an animation
	 * @see 
	 * 		http://developer.android.com/guide/topics/ui/dialogs.html
	 * <br />
	 * <br />
	 * 		Avoid using Dialog class directly. If a more complex dialog is needed use
	 * 		http://developer.android.com/guide/topics/ui/dialogs.html#FullscreenDialog
	 */
	public static AlertDialog getLoadingScreen(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflayer = LayoutInflater.from(context);
		View view = inflayer.inflate(R.layout.loading, null);
		ImageView image_big = (ImageView)view.findViewById(R.id.iac_loading_big);
		ImageView image_little = (ImageView)view.findViewById(R.id.iac_loading_little);
		Animation animation_to_right = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.rotate_to_right);
		Animation animation_to_left = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.rotate_to_left);
		image_big.setAnimation(animation_to_right);
		image_little.setAnimation(animation_to_left);
		builder.setView(view);
		builder.setCancelable(false);
		AlertDialog alert = builder.create();
		alert.getWindow().setBackgroundDrawableResource(R.drawable.ic_mapcross_dummy);
		return alert;
	}
	
}
