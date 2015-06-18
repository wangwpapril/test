package com.example.wwang.movie.request;

import android.os.AsyncTask;
import android.os.Handler;

import com.example.wwang.movie.utils.Common;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ControllerContentTask extends
		AsyncTask<String, Void, ResultHolder> {

	private IControllerContentCallback icc;
	private String url;
	private Enums.ConnMethod connMethod;
	private boolean isHideLoading;
	private static int NORMAL_TIMEOUT = 6000;				


	public ControllerContentTask(String url, IControllerContentCallback icc,
			Enums.ConnMethod connMethod,boolean isHideLoading){
		this.icc = icc;
		this.url = url;
		this.connMethod = connMethod;
		this.isHideLoading = isHideLoading;
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(!isHideLoading){
			new Handler().post(new Runnable() {
				public void run() {
						Common.showLoading("Loading");
				}
			});
		}
	}

	private List<NameValuePair> getParams(IContentParms[] params){
		List<NameValuePair> connParams = null;
		try {
			if (params != null && params.length > 0) {
				connParams = new ArrayList<NameValuePair>();
				connParams.add(new BasicNameValuePair("json", params[0].getparmStr()));
			}
		} catch (Exception e) {
			return null;
		}
		return connParams;
	}

	protected ResultHolder doInBackground(String... params){

		ResultHolder rh = new ResultHolder();
		String json = null;
		if(params[0] != null)
			json = params[0];
		
		try {
			switch (connMethod) {
			case GET:
				rh.setResult(SimpleHttpClient.doGet(url, NORMAL_TIMEOUT));
				break;
			case POST:
//				rh.setResult(SimpleHttpClient.post(url, getParams(params)));
				rh.setResult(SimpleHttpClient.post(json, url, NORMAL_TIMEOUT));
					
				break;
            case PUT:
                rh.setResult(SimpleHttpClient.put(json, url, NORMAL_TIMEOUT));


            }
			rh.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			rh.setSuccess(false);
			rh.setResult(e.getMessage());
		}
		return rh;
	}

	protected void onPostExecute(ResultHolder result){
		super.onPostExecute(result);
		Common.cancelLoading();
		if (result.isSuccess()) {
            try {
                icc.handleSuccess(result.getResult());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
			icc.handleError(new Exception(result.getResult()));
		}
	}
	
	
}
