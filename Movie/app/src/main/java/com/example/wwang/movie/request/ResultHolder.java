package com.example.wwang.movie.request;

public class ResultHolder {
	private boolean isSuccess;
	private String result;
	public boolean isSuccess(){
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess){
		this.isSuccess = isSuccess;
	}
	public String getResult(){
		return result;
	}
	public void setResult(String result){
		this.result = result;
	}
}
