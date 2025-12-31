package com.yundasys.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class OpenApiHttpUtils {
    public OpenApiHttpUtils() {
    }

    public static String doPostJson(String apiUrl, String jsonParams, String appKey, String appSecret) {
        StringBuffer sbResult = new StringBuffer();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            byte[] data = jsonParams.getBytes("UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("app-key", appKey);
            conn.setRequestProperty("req-time", String.valueOf(System.currentTimeMillis()));
            conn.setRequestProperty("sign", MD5(jsonParams + "_" + appSecret, "UTF-8"));
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(data);
            out.flush();
            out.close();
            if (200 == conn.getResponseCode()) {
                InputStream inputStream = conn.getInputStream();

                Exception e1;
                try {
                    e1 = null;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    String readLine;
                    while((readLine = responseReader.readLine()) != null) {
                        sbResult.append(readLine).append("\n");
                    }

                    responseReader.close();
                } catch (Exception var12) {
                    e1 = var12;
                    e1.printStackTrace();
                }
            }
        } catch (Exception var13) {
            Exception e = var13;
            e.printStackTrace();
        }

        return sbResult.toString();
    }

    private static String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);

        for(int i = 0; i < result.length; ++i) {
            int val = result[i] & 255;
            if (val <= 15) {
                sb.append("0");
            }

            sb.append(Integer.toHexString(val));
        }

        return sb.toString().toLowerCase();
    }
}
