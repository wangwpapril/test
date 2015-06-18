package com.example.wwang.movie.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

public class Response {

	private int statusCode;
	private InputStream is;
	private HttpURLConnection con;
	private String responseAsString=null;
	private boolean streamConsumed=false;
	public Response(){
	}
	public Response(HttpURLConnection connection) throws Exception{
		this.con=connection;
		try{
			this.statusCode=connection.getResponseCode();
		}catch (Exception e) {
			if(e==null||e.toString().trim().equals("java.lang.NullPointerException")){
				throw new Exception("URL is incorrect, please check!");
			}
		}
		if(null==(is=connection.getErrorStream())){
			is=connection.getInputStream();
		}
		if(null!=is&&"gzip".equals(con.getContentEncoding())){
			is=new GZIPInputStream(is);
		}
	}
	
	public InputStream asStream(){
		if(streamConsumed){
			throw new IllegalStateException("Stream has already been consumed.");
		}
		
		return is;
	}
	
	public String asString() throws Exception{
		
		if(null==responseAsString){
			BufferedReader bufferedReader;
			try {
				InputStream stream=asStream();
				if(null==stream){
					return null;
				}
				
				bufferedReader=new BufferedReader(new InputStreamReader(stream,"UTF-8"));
				StringBuffer buffer=new StringBuffer();
				String line;
				while(null!=(line=bufferedReader.readLine())){
					buffer.append(line).append("\n");
				}
				
				this.responseAsString=buffer.toString();
				stream.close();
				con.disconnect();
				streamConsumed=true;
				
			} catch (NullPointerException e) {
				throw new Exception(e.getMessage(),e);
			} catch(IOException ioe){
				throw new Exception(ioe.getMessage(),ioe);
			}
		}
		
		return responseAsString;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
