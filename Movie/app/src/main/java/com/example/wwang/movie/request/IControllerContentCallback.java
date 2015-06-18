package com.example.wwang.movie.request;

import org.json.JSONException;

public interface IControllerContentCallback {
	public void handleSuccess(String content) throws JSONException;
	public void handleError(Exception e);
}
