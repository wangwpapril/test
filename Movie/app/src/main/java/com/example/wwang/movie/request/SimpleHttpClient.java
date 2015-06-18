package com.example.wwang.movie.request;


import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;



public class SimpleHttpClient {
		
	private static final int OK = 200;// OK: Success!
	private static final int NOT_MODIFIED = 304;
	private static final int BAD_REQUEST = 400;
	private static final int NOT_AUTHORIZED = 401;
	private static final int FORBIDDEN = 403;
	private static final int NOT_FOUND = 404;
	private static final int NOT_ACCEPTABLE = 406;
	private static final int INTERNAL_SERVER_ERROR = 500;
	private static final int BAD_GATEWAY = 502;
	private static final int SERVICE_UNAVAILABLE = 503;
	private static final int NETWORK_DISABLED=601;

	private static int retryCount = 1;

	
	public static String doPost(PostParameter[] postParams,String connectionUrl,int connectTimeout) throws Exception{
/*		Enums.NetStatus netStatus = MyApplication.getNetStatus();
		if(netStatus == Enums.NetStatus.Disable){
			return String.valueOf(NETWORK_DISABLED);
		}*/
		
		int retriedCount = 0;
		Response response = null;

		for (retriedCount = 0; retriedCount < retryCount; retriedCount++) {

			int responseCode = -1;
			HttpURLConnection connection = null;
			OutputStream os = null;
			try {
				connection = (HttpURLConnection) new URL(connectionUrl).openConnection();
				if(connectTimeout !=0){
					connection.setConnectTimeout(connectTimeout);
				}
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				connection.setDoOutput(true);
				
				String postParam = "";
				if (postParams != null) {
					postParam = encodeParameters(postParams);
				}
				byte[] bytes = postParam.getBytes("UTF-8");
				connection.setRequestProperty("Content-Length",Integer.toString(bytes.length));
				os = connection.getOutputStream();
				os.write(bytes);
				os.flush();
				os.close();
				response = new Response(connection);
				responseCode = response.getStatusCode();

				if (responseCode != OK) {
					if (responseCode < INTERNAL_SERVER_ERROR || retriedCount == retryCount)
						throw new Exception(getCause(responseCode));
				} else {break;}				
			}catch(ConnectTimeoutException e){
				throw new Exception("ConnectionTimeout",e);
			}catch(InterruptedIOException e){	
				throw new Exception("ConnectionTimeout",e);
			}catch (Exception e) {
				throw new Exception(e.getMessage(), e);
			}
		}
		return response.asString();
	}

	public static String doGet(String connectionUrl,int connectTimeout) throws Exception{
/*		Enums.NetStatus netStatus = MyApplication.getNetStatus();
		if(netStatus == Enums.NetStatus.Disable){
			return String.valueOf(NETWORK_DISABLED);
		}*/
	
		int retriedCount = 0;
		Response response = null;

		for(retriedCount = 0; retriedCount < retryCount; retriedCount++){
			
			int responseCode = -1;
			try {
				HttpURLConnection connection = null;
				try {

					connection = (HttpURLConnection) new URL(connectionUrl).openConnection();
					if(connectTimeout !=0){
						connection.setConnectTimeout(connectTimeout);
					}
					response = new Response(connection);
					responseCode = response.getStatusCode();

					if (responseCode != OK) {
						if (responseCode < INTERNAL_SERVER_ERROR || retriedCount == retryCount)
							throw new Exception(getCause(responseCode));
					} else {
						break;
					}

				} finally {
					
				}
			}catch(ConnectTimeoutException e){
				throw new Exception("ConnectionTimeout",e);
			}catch(InterruptedIOException e){	
				throw new Exception("ConnectionTimeout",e);
			}catch (Exception e) {
				throw new Exception(e.getMessage(), e);
			}
		}
		
		return response.asString();
	}

	public static String post(String entity, String connectionUrl,
			int connectTimeout) throws Exception{
		int retriedCount = 0;
		Response response = null;


		for (retriedCount = 0; retriedCount < retryCount; retriedCount++) {

			int responseCode = -1;
			HttpURLConnection connection = null;
			OutputStream os = null;
			try {
				connection = (HttpURLConnection) new URL(connectionUrl)
						.openConnection();
				if (connectTimeout != 0) {
					connection.setConnectTimeout(connectTimeout);
				}
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type",
						"application/json");
				connection.setRequestProperty("Accept", "application/json");

				connection.setDoOutput(true);

				String postParam = "";

				if (entity != null) {
					postParam = entity;
				}

				byte[] bytes = postParam.getBytes("UTF-8");
				connection.setRequestProperty("Content-Length",
						Integer.toString(bytes.length));
				os = connection.getOutputStream();
				os.write(bytes);
				os.flush();
				os.close();
				response = new Response(connection);
				responseCode = response.getStatusCode();

                if (responseCode == NOT_FOUND)
                    return response.asString();

				if (responseCode != OK) {
					if (responseCode < INTERNAL_SERVER_ERROR
							|| retriedCount == retryCount)
					throw new Exception(getCause(responseCode));
				} else {
					break;
				}
			} catch (ConnectTimeoutException e) {
				throw new Exception("ConnectionTimeout", e);
			} catch (InterruptedIOException e) {
				throw new Exception("ConnectionTimeout", e);
			} catch (Exception e) {
				throw new Exception(e.getMessage(), e);
			}
		}

		return response.asString();
	}

    public static String put(String entity, String connectionUrl,
                              int connectTimeout) throws Exception{
        int retriedCount = 0;
        Response response = null;


        for (retriedCount = 0; retriedCount < retryCount; retriedCount++) {

            int responseCode = -1;
            HttpURLConnection connection = null;
            OutputStream os = null;
            try {
                connection = (HttpURLConnection) new URL(connectionUrl)
                        .openConnection();
                if (connectTimeout != 0) {
                    connection.setConnectTimeout(connectTimeout);
                }
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setRequestProperty("Accept", "application/json");

                connection.setDoOutput(true);
                connection.setDoInput(true);

                String postParam = "";

                if (entity != null) {
                    postParam = entity;
                }


                byte[] bytes = postParam.getBytes("UTF-8");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(bytes.length));
                os = connection.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
                response = new Response(connection);
                responseCode = response.getStatusCode();

                if (responseCode == NOT_FOUND)
                    return response.asString();

                if (responseCode != OK) {
                    if (responseCode < INTERNAL_SERVER_ERROR
                            || retriedCount == retryCount)
                        throw new Exception(getCause(responseCode));
                } else {
                    break;
                }
            } catch (ConnectTimeoutException e) {
                throw new Exception("ConnectionTimeout", e);
            } catch (InterruptedIOException e) {
                throw new Exception("ConnectionTimeout", e);
            } catch (Exception e) {
                throw new Exception(e.getMessage(), e);
            }
        }

        return response.asString();
    }


    private static String encodeParameters(PostParameter[] postParams) throws Exception {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < postParams.length; i++) {
			if (i != 0){
				buffer.append("&");
			}
			buffer.append(URLEncoder.encode(postParams[i].getName(), "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(postParams[i].getObject().toString(), "UTF-8"));
		}

		return buffer.toString();
	}

	private static String getCause(int statusCode) {
		String cause = null;
		switch (statusCode) {
		case NOT_MODIFIED:
			break;
		case BAD_REQUEST:
			cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
			break;
		case NOT_AUTHORIZED:
			cause = "Authentication credentials were missing or incorrect.";
			break;
		case FORBIDDEN:
			cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
			break;
		case NOT_FOUND:
			cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
			break;
		case NOT_ACCEPTABLE:
			cause = "Returned by the Search API when an invalid format is specified in the request.";
			break;
		case INTERNAL_SERVER_ERROR:
			cause = "Something is broken.  Please post to the group so the Weibo team can investigate.";
			break;
		case BAD_GATEWAY:
			cause = "Weibo is down or being upgraded.";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable: The Weibo servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}


}
