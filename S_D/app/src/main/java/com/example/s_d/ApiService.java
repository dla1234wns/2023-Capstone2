package com.example.s_d;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    private final OkHttpClient client = new OkHttpClient();

    private String Result = new String();
    private JSONObject jsonObject = new JSONObject();
    //상태값 초기화
    private int status = 0;

    public String getValue(String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getKey(String key) {
        if (jsonObject.has(key)) {
            return key;
        } else {
            return null;
        }
    }

    public int getStatus(){return status;}

    public void arrayToJson(String result){
        String responseString = result;

        String string = responseString;
        string = string.replace("[", "").replace("]", "").replace("{", "").replace("}", "");

        String[] stringArray = string.split(",");
        String string1 = new String();
        for (int i = 0; i < stringArray.length - 1; i++){
            if (string1.isEmpty()){
                string1 = stringArray[i];
            }else{
                string1 = string1 + "," + stringArray[i];
            }
        }

        try {
            jsonObject = new JSONObject(result);
            String statusStr = jsonObject.getString("SUCCESS");
            status = Integer.parseInt(statusStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("json", jsonObject.toString());
        Log.d("status", Integer.toString(status));
    }

    public void getUrl(String url) {
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.d("output values", "responseBody: " + result);
                    return result;
                } else {
                    Log.e("error message", "response is not successful: " + response.code());
                    throw new IOException("response is not successful: " + response.code());
                }
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();

        try {
            Result = futureTask.get();
        }catch (Exception e){
            Log.e("error", e.toString());
        }

        arrayToJson(Result);
    }

    public void postUrl(String url) {
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(null, new byte[0]))
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.d("output values", "responseBody: " + result);
                    return result;
                } else {
                    Log.e("error message", "response is not successful: " + response.code());
                    throw new IOException("response is not successful: " + response.code());
                }
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();

        try {
            Result = futureTask.get();
        }catch (Exception e){
            Log.e("error", e.toString());
        }

        arrayToJson(Result);
    }
    public void putUrl(String url) {
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .put(RequestBody.create(null, new byte[0]))
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.d("output values", "responseBody: " + result);
                    return result;
                } else {
                    Log.e("error message", "response is not successful: " + response.code());
                    throw new IOException("response is not successful: " + response.code());
                }
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();

        try {
            Result = futureTask.get();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        arrayToJson(Result);
    }
    public void deleteUrl(String url) {
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .delete()
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.d("output values", "responseBody: " + result);
                    return result;
                } else {
                    Log.e("error message", "response is not successful: " + response.code());
                    throw new IOException("response is not successful: " + response.code());
                }
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();

        try {
            Result = futureTask.get();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        arrayToJson(Result);
    }
}