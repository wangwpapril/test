package com.example.wwang.movie;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;


public class Common {

	public static ProgressDialog dialogLoading;
	public static Context context;

	public static void showHintDialog(String title, String content) {

		if (context != null) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder
					.setTitle(title)
					.setMessage(content)
					.setCancelable(true)
					.setNegativeButton("Confirmed",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
		}
	}

	public static void showLoading() {
		if (dialogLoading != null)
			return;
		if (context == null)
			return;

		dialogLoading = new ProgressDialog(context);

		dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		dialogLoading.setTitle("Please wait");

		dialogLoading.setMessage("Reading...");

		dialogLoading.setIndeterminate(false);

		dialogLoading.setCancelable(true);

		dialogLoading.setOnDismissListener(listenerDismissLoading);

		dialogLoading.show();
	}

	public static void showLoading(String str) {
		if (dialogLoading != null)
			return;
		if (context == null)
			return;

		dialogLoading = new ProgressDialog(context);

		dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		dialogLoading.setMessage(str);

		dialogLoading.setIndeterminate(false);

		dialogLoading.setCancelable(true);

		dialogLoading.setOnDismissListener(listenerDismissLoading);

		dialogLoading.show();
	}

	static OnDismissListener listenerDismissLoading = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {

		}
	};

	public static void cancelLoading() {
		if (dialogLoading != null) {
			if (dialogLoading.isShowing())
				dialogLoading.cancel();
			dialogLoading = null;
		}
	}

}