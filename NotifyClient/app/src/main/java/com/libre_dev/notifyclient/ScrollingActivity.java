package com.libre_dev.notifyclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.chainfire.libsuperuser.Shell;

public class ScrollingActivity extends AppCompatActivity implements AsyncListener {

    public static final int STATUS_FREE = 0;        // ready for a trip
    public static final int STATUS_OCCUPIED = 1;    // on a trip, with the meter running
    public static final int STATUS_ENROUTE = 2;     // dispatched to a passenger, but passenger not in vehicle yet
    public static final int STATUS_DISTRESS = 3;    // panic button
    public static final int STATUS_BREAK = 4;       // driver on break, but shift not over yet
    public static final int STATUS_OFFDUTY = 5;     // driver is not active

    public static final int RESULT_DISCARD = 0;
    public static final int RESULT_LOGIN = 1;
    public static final int RESULT_SHIFTIN = 2;
    public static final int RESULT_SHIFTOUT = 3;
    public static final int RESULT_FAREHAILED = 4;
    public static final int RESULT_FAREACCEPT = 5;
    public static final int RESULT_TRIPSTART = 6;
    public static final int RESULT_TRIPCOMPLETE = 7;
    public static final int RESULT_TRIPPAUSE = 8;
    public static final int RESULT_TRIPUNPAUSE = 9;
    public static final int RESULT_PANIC = 10;

    public String tripId = "123";
    public float lat = 19;
    public float lng = -99;
    //public String driverId = "drv1455745972297275574";
    public String driverId = "drv1455813112363511158";
    public String taxiId = "taxi_789";
    public int status = STATUS_OFFDUTY;
    public int eta = 0;

    private EditText editText = null;
    private TextView textView = null;

    private void trusting()
    {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    }

    private String getEventsFromAnXML()
            throws XmlPullParserException, IOException
    {
        StringBuffer stringBuffer = new StringBuffer();
        Resources res = getResources();
        XmlResourceParser xpp = res.getXml(R.xml.appconfig);
        xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_DOCUMENT)
            {
                stringBuffer.append("--- Start XML ---");
            }
            else if(eventType == XmlPullParser.START_TAG)
            {
                stringBuffer.append("\nSTART_TAG: "+xpp.getName());
            }
            else if(eventType == XmlPullParser.END_TAG)
            {
                stringBuffer.append("\nEND_TAG: "+xpp.getName());
            }
            else if(eventType == XmlPullParser.TEXT)
            {
                stringBuffer.append("\nTEXT: "+xpp.getText());
            }
            eventType = xpp.next();
        }
        stringBuffer.append("\n--- End XML ---");
        return stringBuffer.toString();
    }

public class TrafficRecord{
    public long tx; public long rx;
    String tag;
    public TrafficRecord(int uid, String tag){
        tx = TrafficStats.getUidTxBytes(uid);
        rx = TrafficStats.getUidRxBytes(uid);
        this.tag = tag;
    }
}
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean doIHavePermission(){


        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());

        return !queryUsageStats.isEmpty();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        try {
            String x = getEventsFromAnXML();

            Class c;
            c = Class.forName("android.net.TrafficStats");
            Method m = c.getMethod("getUidTxBytes", new Class[] {String.class, int.class});
            Object o = m.invoke(null, new Object[]{-5});
            Method m1 = c.getMethod("getUidTxBytes", new Class[] {String.class, int.class});
            Object o2 = m1.invoke(null, new Object[]{-5});
        } catch (Exception e) {

        }


//        long ulBytes1 = TrafficStats.getUidTxBytes(TrafficStats.UID_TETHERING);
//        long dlBytes1 = TrafficStats.getUidRxBytes(TrafficStats.UID_TETHERING);
        doIHavePermission();

        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        HashMap appNames = new HashMap();
        HashMap apps = new HashMap();
        for(ApplicationInfo app : getPackageManager().getInstalledApplications(0))
        {
            appNames.put(app.uid, app.packageName);
            TrafficRecord rc = new TrafficRecord(app.uid, (String)appNames.get(app.uid));
            if(rc.tx + rc.rx > 4000000)
                apps.put(app.uid, rc);
        }

        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> packages = pm.queryIntentActivities(main, 0);

        ArrayList<String> app_name_list = new ArrayList<String>();
        ArrayList<String> app_package_list = new ArrayList<String>();

        for(ResolveInfo resolve_info : packages) {
            try {
                String package_name = resolve_info.activityInfo.packageName;
                String app_name = (String)pm.getApplicationLabel(
                        pm.getApplicationInfo(package_name
                                , PackageManager.GET_META_DATA));
                boolean same = false;
                for(int i = 0 ; i < app_name_list.size() ; i++) {
                    if(package_name.equals(app_package_list.get(i)))
                        same = true;
                }
                if(!same) {
                    app_name_list.add(app_name);
                    app_package_list.add(package_name);
                }
                //Log.i("Check", "package = <" + package_name + "> name = <" + app_name + ">");
            } catch(Exception e) { }
        }


        try {
            PackageManager pm1 = getPackageManager();
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                String appName;
                try {
                    appName = pm1.getApplicationInfo(service.process, 0).loadLabel(pm1).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    appName = null;
                }

                int uid = service.uid;

                long ulBytes = TrafficStats.getUidTxBytes(uid);
                long dlBytes = TrafficStats.getUidRxBytes(uid);
            }

            List<String> stdout = Shell.SH.run("ps");
            List<String> packages1 = new ArrayList<>();
            for (String line : stdout) {
                // Get the process-name. It is the last column.
                String[] arr = line.split("\\s+");
                String processName = arr[arr.length - 1].split(":")[0];
                packages1.add(processName);
            }

// Get a list of all installed apps on the device.
            List<ApplicationInfo> apps1 = pm1.getInstalledApplications(0);

// Remove apps which are not running.
            for (Iterator<ApplicationInfo> it = apps1.iterator(); it.hasNext(); ) {
                if (!packages.contains(it.next().packageName)) {
                    it.remove();
                }
            }

            for (ApplicationInfo app : apps1) {
                String appName = app.loadLabel(pm1).toString();
                int uid = app.uid;
                long ulBytes = TrafficStats.getUidTxBytes(uid);
                long dlBytes = TrafficStats.getUidRxBytes(uid);
    /* do your stuff */
            }


            ApplicationInfo app = getPackageManager().getApplicationInfo("com.sec.tetheringprovision", 0);
            {
                int uid = app.uid;
                long ulBytes = TrafficStats.getUidTxBytes(uid);
                long dlBytes = TrafficStats.getUidRxBytes(uid);
            }
//            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
//            String[] d = cm.getTetheredIfaces();
//            Drawable icon = packageManager.getApplicationIcon(app);
//            String name = PackageManager.getApplicationLabel(app);
        } catch (PackageManager.NameNotFoundException e) {
            Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
//        for(Integer uid: appNames.keySet())
//        {
//            apps.put(uid, new TrafficRecord(uid, appNames.get(uid)));
//        }

        trusting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            serverGet("/gps-ingestion/1.0.0/gps/city001_taxi_19");
            return true;
        } else if (id == R.id.action_login) {
            doLogin();
        } else if (id == R.id.action_shiftin) {
            doShiftIn();
        } else if (id == R.id.action_shiftout) {
            doShiftOut();
        } else if (id == R.id.action_begin_hailed) {
            doBeginHailed();
        } else if (id == R.id.action_accept_fare) {
            doAcceptFare();
        } else if (id == R.id.action_begin_trip) {
            doBeginTrip();
        } else if (id == R.id.action_end_trip) {
            doEndTrip();
        } else if (id == R.id.action_break) {
            doBreak();
        } else if (id == R.id.action_ready) {
            doReady();
        } else if (id == R.id.action_pause) {
            doPause();
        } else if (id == R.id.action_end_pause) {
            doEndPause();
        } else if (id == R.id.action_panic) {
            doPanic();
        } else if (id == R.id.button) {
            editText.setText("");
        } else {
            return super.onOptionsItemSelected(item);
        }

       // updateGps();
        return true;
    }

    public void setStatus(int newStatus)
    {
        status = newStatus;
    }

    public void clearTripId()
    {
        tripId="123";
    }

    public void doLogin() {
        serverPost(RESULT_LOGIN,"/driver/1.0.0/login","{ \"driverId\": \""+driverId+"\", \"password\": \"password\" }");
        setStatus(STATUS_BREAK);
        eta = 0;
    }

    public void doShiftIn()
    {
        serverPost(RESULT_SHIFTIN,"/driver/1.0.0/shift/" + driverId + "/in","{ \"taxiId\": \""+taxiId+"\", \"startTime\": "+getTimestamp()+" }");
        setStatus(STATUS_FREE);
        eta = 0;
    }

    public void doShiftOut() {
        serverPost(RESULT_SHIFTOUT,"/driver/1.0.0/shift/"+ driverId +"/out","{ \"endTime\": "+getTimestamp()+" }");
        // status 5 means unavailable.
        setStatus(STATUS_OFFDUTY);
        eta = 0;
    }

    private void doBeginHailed() {
        //serverPost(RESULT_FAREHAILED,"/dispatch/1.0.0/fare/"+taxiId+"/hailed", "{ 'data': {  'driverid': '" + driverId + "', 'startTimestamp': "+getTimestamp()+" }  }");
        serverPost(RESULT_FAREHAILED,"/trip-manager/1.0.0/trip/",
                "{  \"taxiId\": \"" + taxiId + "\", \"driverId\": \"" + driverId + "\", \"startTimestamp\": "+getTimestamp()+" }");
        // status 0 means occupied
        setStatus(STATUS_OCCUPIED);
        eta = 2500;  // measured in m
    }

    private void doAcceptFare() {
        serverPut(RESULT_FAREACCEPT,"/dispatch/1.0.0/fare/" + taxiId + "/accept/" + tripId, "{\"driverId\": \"" + driverId + "\" }");
        // status 2 means on enroute to pick up the passenger
        setStatus(STATUS_ENROUTE);
        eta = 500;  // measured in m
    }

    private void doBeginTrip() {
        serverPut(RESULT_TRIPSTART,"/trip-manager/1.0.0/trip/" + tripId + "/start", "{ \"startTimestamp\": " + getTimestamp() + "}");
        // status 1 means occupiaied
        setStatus(STATUS_OCCUPIED);
        eta = 1000; // measured in m
    }

    private void doEndTrip() {
        serverPut(RESULT_TRIPCOMPLETE,"/trip-manager/1.0.0/trip/" + tripId + "/complete", "{ \"endTimestamp\": "+getTimestamp()+"}");
        // status 4 means on break
        setStatus(STATUS_BREAK);
        clearTripId();  // although in real life we'd want to complete payment first.
        eta = 0;
    }

    private void doPause() {
        serverPut(RESULT_TRIPPAUSE,"/trip-manager/1.0.0/trip/" + tripId + "/pause", "{ \"timestamp\": " + getTimestamp() + "  }");
        // status doesn't change.
    }

    private void doEndPause() {
        serverPut(RESULT_TRIPUNPAUSE,"/trip-manager/1.0.0/trip/" + tripId + "/unpause", "{ \"timestamp\": " + getTimestamp() + " }");
    }

    private void doPanic() {
        serverPost(RESULT_PANIC,"/notification/1.0.0/panic/", "{ \"tripId\": \"" + tripId + "\", \"timestamp\": " + getTimestamp() + " }");
        setStatus(STATUS_DISTRESS);
    }

    private void doBreak() {
        setStatus(STATUS_BREAK);
        eta=0;
    }

    private void doReady() {
        setStatus(STATUS_FREE);
        eta=0;
    }

    private String getTimestamp() {
        return Long.toString(new java.util.Date().getTime());
    }

    private void updateGps() {
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            json.accumulate("data",data);
            data.accumulate("taxiId",taxiId);
            data.accumulate("lat",lat);
            data.accumulate("lng",lng);
            data.accumulate("timestamp", getTimestamp());
            data.accumulate("eta", Integer.toString(eta));
            data.accumulate("tripId",tripId);
            data.accumulate("status", Integer.toString(status));
//            data.accumulate("type","sedan");
            serverPost("/gps-ingestion/1.0.0/gps",json.toString());
        } catch(Exception e) {
            Log.e("updateGps", "Exception while forming GPS message.");
        }
    }


    private void serverGet(String api) {
        String [] params = new String[2];
        params[0] = api;
        params[1] = "";
        textView.setText("GET API: "+api+"\n---\n");
        new ServerGet().execute(params);
    }

    private void serverPost(String api,String payload) {
        String [] params = new String[2];
        params[0] = api;
        params[1] = payload;
        textView.setText(textView.getText()+"\n***\nPOST API: "+api+"\n\nPayload: "+payload);
        new ServerPost().execute(params);
    }

    private void serverPost(int type,String api,String payload) {
        String [] params = new String[2];
        params[0] = api;
        params[1] = payload;
        textView.setText("POST API: "+api+"\n\nPayload: "+payload+"\n---\n");
        new ServerPost().setListener(this).setType(type).execute(params);
    }

    private void serverPut(String api,String payload) {
        String [] params = new String[2];
        params[0] = api;
        params[1] = payload;
        textView.setText("PUT API: "+api+"\n\nPayload: "+payload+"\n---\n");
        new ServerPut().execute(params);
    }

    private void serverPut(int type,String api,String payload) {
        String [] params = new String[2];
        params[0] = api;
        params[1] = payload;
        textView.setText("PUT API: " + api + "\n\nPayload: " + payload + "\n---\n");
        new ServerPut().setListener(this).setType(type).execute(params);
    }

    @Override
    public void asyncResult(int type,int status, JSONObject json)
    {
        if(type == RESULT_DISCARD) return;
        if(type == RESULT_LOGIN) {
            try {
                JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    String token = result.getString("token");
                    Log.i("ScrollingActity","Login Token is "+token);

                }
            } catch(JSONException e) {
                Log.e("ScrollingActivity","json exception while parsing the result of type login");
                Log.e("ScrollingActivity",e.getMessage());
            }
        } else if(type == RESULT_FAREHAILED) {
            // we have a trip id assigned.
            try {
                JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    tripId = result.getString("tripId");
                    Log.i("ScrollingActity","Trip ID is "+tripId);
                }
            } catch(JSONException e) {
                Log.e("asyncResult","json exception while parsing the result of type hailed");
            }
        }
        if(json != null) {
            textView.setText(textView.getText() + "\n---\nResult for id " + type +
                    "\nStatus " +  status + "\n" +
                    json.toString());
        } else {
            textView.setText(textView.getText() + "\n---\nNo result for id " + type +
                    "\nStatus " + status);
        }
    }

    private class ServerPost extends AsyncTask<String [],Integer, JSONObject> {
        public AsyncListener asyncListener=null;
        public int type=0;      // enumeration for response handler type.
        public int status=0;    // status code for the http request.

        public ServerPost setListener(AsyncListener listener) {
            asyncListener=listener;

            return this;
        }

        public ServerPost setType(int asyncType) {
            type=asyncType;
            return this;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String []... params) {
            String[] request = params[0];
            String api = request[0];
            String payload = request[1];
            //String server = "http://192.168.0.140:3000";
            String server = "https://dev-tools.libre-dev.com/api";


            Log.i("ScrollingActivity","requesting "+server + api);
            Log.i("ScrollingActivity","payload "+payload);

            JSONObject result=null;
            try {
                URL url = new URL(server + api);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                //HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer 5f43e2f1c34ba8daa84243d04ea3d72d");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                out.writeBytes(payload);
                out.flush();
                out.close();

                int statusCode = urlConnection.getResponseCode();
                Log.i("ScrollingActivity","Got status code "+statusCode);
                status = statusCode;
                InputStream stream = urlConnection.getErrorStream();
                if (stream == null) {
                    stream = urlConnection.getInputStream();
                }
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.i("ScrollingActivity",responseStrBuilder.toString());
                result = new JSONObject(responseStrBuilder.toString());
            } catch(IOException e) {
                Log.e("ScrollingActivity","During server post operation, an io exception occured.");
                Log.e("ScrollingActivity",e.getMessage());
            } catch(JSONException e) {
                Log.e("ScrollingActivity","During server post operation, a json exception occured.");
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // update the main thread gui with our progress in percent.
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (asyncListener != null) {
                asyncListener.asyncResult(type, status, result);
            }
        }
    }

    private class ServerPut extends AsyncTask<String [],Integer, JSONObject> {
        public AsyncListener asyncListener=null;
        public int type=0;
        public int status=0;    // status code for the http request.

        public ServerPut setListener(AsyncListener listener) {
            asyncListener=listener;

            return this;
        }

        public ServerPut setType(int asyncType) {
            type=asyncType;
            return this;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String []... params) {
            String[] request = params[0];
            String api = request[0];
            String payload = request[1];
            //String server = "http://192.168.0.140:3000";
            String server = "https://dev-tools.libre-dev.com/api";


            Log.i("ScrollingActivity","requesting "+server + api);
            Log.i("ScrollingActivity","payload "+payload);

            JSONObject result=null;
            try {
                URL url = new URL(server + api);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                //HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Authorization", "Bearer 5f43e2f1c34ba8daa84243d04ea3d72d");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //urlConnection.connect();

                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                out.writeBytes(payload);
                out.flush();
                out.close();

                int statusCode = urlConnection.getResponseCode();
                status=statusCode;
                Log.i("ScrollingActivity","Got status code "+statusCode);
                InputStream stream = urlConnection.getErrorStream();
                if (stream == null) {
                    stream = urlConnection.getInputStream();
                }
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.i("ScrollingActivity",responseStrBuilder.toString());
                result = new JSONObject(responseStrBuilder.toString());
                //}
            } catch(IOException e) {
                Log.e("ScrollingActivity","During server post operation, an io exception occured.");
                Log.e("ScrollingActivity",e.getMessage());
            } catch(JSONException e) {
                Log.e("ScrollingActivity","During server post operation, a json exception occured.");
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // update the main thread gui with our progress in percent.
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (asyncListener != null) {
                asyncListener.asyncResult(type, status, result);
            }
        }
    }

    private class ServerGet extends AsyncTask<String [],Integer, JSONObject> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String []... params) {
            String[] request = params[0];
            String api = request[0];
            String payload = request[1];
            //String server = "http://192.168.0.140:4080";
            String server = "https://dev-tools.libre-dev.com/api";


            Log.i("ScrollingActivity","requesting GET "+server + api);

            JSONObject result=null;
            try {
                URL url = new URL(server + api);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer 5f43e2f1c34ba8daa84243d04ea3d72d");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();
                Log.i("ScrollingActivity","Got status code "+statusCode);
                if(statusCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);

                    Log.i("ScrollingActivity",responseStrBuilder.toString());
                    result = new JSONObject(responseStrBuilder.toString());
                }
            } catch(IOException e) {
                Log.e("ScrollingActivity","During server post operation, an io exception occured.");
                Log.e("ScrollingActivity",e.getMessage());
            } catch(JSONException e) {
                Log.e("ScrollingActivity","During server post operation, a json exception occured.");
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // update the main thread gui with our progress in percent.
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
        }
    }
    public WifiConfiguration getHotspotConfiguration(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
//            wifiConfig.SSID;
//            wifiConfig.preSharedKey;

            return wifiConfig;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
