package com.starrtc.demo.utils;

import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import com.starrtc.demo.demo.MLOC;
import com.starrtc.starrtcsdk.core.utils.StringUtils;

public class StarHttpUtil extends AsyncTask<Bundle, Object, Bundle> {

    ICallback icallback;
    private static int TIMEOUT = 8000;
    private static final String CHARSET = "utf-8"; // 设置编码
    private static String requestMethod = "";

    public static String URL = "url";
    public static String DATA = "data";
    public static String REQUEST_METHOD_POST = "POST";
    public static String REQUEST_METHOD_GET = "GET";

    public void addListener(ICallback _icallback){
        icallback = _icallback;
    }
    private final String TEXTTAG = "StarHttpUtil";
    private String token;

    public StarHttpUtil(String method){
        requestMethod = method;
    }

    @Override
    protected Bundle doInBackground(Bundle... params) {
        Bundle bundle = null;

        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        HttpURLConnection postConn = null;
        StringBuffer responseResult = new StringBuffer();
        try {
            String serverUrl = params[0].getString(URL);
            String paramData = params[0].getString(DATA);
            StringBuilder postData = new StringBuilder();
            if(StringUtils.isNotEmpty(paramData)){
                JSONObject jsonObject = new JSONObject(paramData);
                Iterator iterator = jsonObject.keys();
                while(iterator.hasNext()){
                    String key = (String) iterator.next();
                    String value = jsonObject.getString(key);
                    if(postData.length()!=0){
                        postData.append("&");
                    }
                    postData.append(key).append("=").append(value);
                }
            }
            postConn = (HttpURLConnection)(new URL(serverUrl)).openConnection();
            postConn.setConnectTimeout(TIMEOUT);

            if(requestMethod.equals(REQUEST_METHOD_GET)){
                MLOC.d(TEXTTAG, "====== start request url======" + params[0].getString("url"));
                postConn.setRequestMethod(requestMethod);
                postConn.connect();
            }else if(requestMethod.equals(REQUEST_METHOD_POST)){
                MLOC.d(TEXTTAG, "====== start request url======" + params[0].getString("url")+"  "+requestMethod+":"+params[0].getString("data"));
                //byte[] data = postData.toString().getBytes();
                postConn.setRequestMethod(requestMethod);
//                postConn.setRequestProperty("Content-Type", "application/json");
//                postConn.setRequestProperty("Content-Length", data.length+"");
                postConn.setDoOutput(true);
                postConn.setDoInput(true);
                postConn.connect();
                // 获取URLConnection对象对应的输出流
                printWriter = new PrintWriter(postConn.getOutputStream());
                // 发送请求参数
                printWriter.print(postData.toString());
                // flush输出流的缓冲
                printWriter.flush();
            }

            // 根据ResponseCode判断连接是否成功
            int responseCode = postConn.getResponseCode();
            if (responseCode != 200) {
                bundle = new Bundle();
                bundle.putBoolean("result",false);
                bundle.putString("content", String.valueOf(responseCode));
            } else {

                bufferedReader = new BufferedReader(new InputStreamReader(
                        postConn.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseResult.append(line);
                }
                bundle = new Bundle();
                bundle.putBoolean("result",true);
                bundle.putString("content",responseResult.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            bundle = new Bundle();
            bundle.putBoolean("result", false);
            bundle.putString("content", e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            bundle = new Bundle();
            bundle.putBoolean("result", false);
            bundle.putString("content", e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            postConn.disconnect();
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bundle;
    }



    @Override
    protected void onPostExecute(Bundle resultBundle) {
        super.onPostExecute(resultBundle);
        if(resultBundle == null) return;
        boolean result = resultBundle.getBoolean("result");
        String content = resultBundle.getString("content");

        MLOC.d(TEXTTAG,"====== post request result ======"+result+"||"+content);
        if(result) {
            String statusCode;
            try {
                JSONObject jsonObject = new JSONObject(content);
                statusCode = jsonObject.getString("status");
                String retData = null;
                if(jsonObject.has("data")){
                    retData = jsonObject.getString("data");
                }
                icallback.callback(true, statusCode, retData);
            } catch (JSONException e) {
                e.printStackTrace();
                icallback.callback(false, "", content);
            }
        }else{
            icallback.callback(false, "", content);
        }

    }
}
