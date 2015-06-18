package com.example.wwang.movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;


public class StringUtil {
	
	public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str);
    }

    public static String getValueOrDefault(String str, String defaultValue) {
        return StringUtil.isEmpty(str) ? defaultValue : str;
    }

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String join(Object[] elements, CharSequence separator) {
        return join(Arrays.asList(elements), separator);
    }

    public static String join(Iterable<? extends Object> elements, CharSequence separator) {
        StringBuilder builder = new StringBuilder();

        if (elements != null) {
            Iterator<? extends Object> iter = elements.iterator();
            if (iter.hasNext()) {
                builder.append(String.valueOf(iter.next()));
                while (iter.hasNext()) {
                    builder.append(separator).append(String.valueOf(iter.next()));
                }
            }
        }

        return builder.toString();
    }

    public static String inputStreamToString(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }

    public static String getFromStream(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[40960];
        int read;
        while ((read = br.read(buffer, 0, buffer.length)) > 0) {
            sb.append(buffer, 0, read);
        }
        br.close();
        return sb.toString();
    }

    public static String fromBytes(byte[] bytes) {
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < bytes.length; offset++) {
            int i = bytes[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    public static JSONObject parseJSON(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
//            Logg.e(e);
            return new JSONObject();
        }
    }

    public static Map<String, Object> parseJSONToHash(String json) {
        JSONObject jo = parseJSON(json);
        Iterator<String> iter = jo.keys();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key, jo.get(key));
            }
        } catch (JSONException e) {
//            Logg.e(e);
        }
        return map;
    }

    public static Map<String, Object> parseHttpParamsToHash(String params) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] arr = params.split("&");
        for (String kv : arr) {
            if (kv.indexOf("=") > 0) {
                String[] kvArr = kv.split("=");
                map.put(kvArr[0], kvArr[1]);
            }
        }
        return map;
    }

    public static String simpleFormat(String str, Object... replacements) {
        String[] parts = str.split("%s");
        if (parts.length < 2) return str;
        StringBuilder sb = new StringBuilder();
        int rl = replacements.length;
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i < rl)
                sb.append(replacements[i]);
        }
        return sb.toString();
    }

    public static boolean isEmail(String email){
        String str="[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void showAlertDialog(String strTitle, String strMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(strMessage);
        builder.setTitle(strTitle);
        builder.setPositiveButton(R.string.ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.setCancelable(false);

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        alertDialog.show();
    }
}