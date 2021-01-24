package com.example.cartoonai;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


        import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
        import java.io.BufferedOutputStream;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusFile extends AsyncTask<String, String, String> {

    String url_inn = "";
    long id;
    ImageView imageView;



    public StatusFile(long id,ImageView imageView){
        this.id = id;
        this.imageView = imageView;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        StatusResp msg = new Gson().fromJson(s, StatusResp.class);
        System.out.println(msg);

        String status = msg.getStatus();
        if(status.contentEquals("pending")){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StatusFile st = new StatusFile(id,imageView);
                    st.execute(url_inn);
                }
            }, 3000);
        }

        if(status.contentEquals("complete")){

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_.jpg";
            File storageDir = MyContext.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String fullPath = storageDir + "/" + imageFileName;
            DownloadFileFromURL d = new DownloadFileFromURL(imageView);
            d.execute(MyContext.upload_url + "/file/res/" + id + "/out.jpg",fullPath);
        }

    }


    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        OutputStream out = null;

        url_inn = urlString;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            out = new BufferedOutputStream(urlConnection.getOutputStream());
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//            writer.write(data);
//            writer.flush();
//            writer.close();
//            out.close();
//            urlConnection.connect();
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            readStream(in);


            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            Integer count = 0;
            while ((line = br.readLine()) != null) {
                if(count>0){
                    sb.append("\n"+line);
                }else{
                    sb.append(line);
                }
                count += 1;
            }
            br.close();


            if(urlConnection != null) // Make sure the connection is not null.
                urlConnection.disconnect();
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void readStream(InputStream in) {
        try {
            System.out.println(in.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



class StatusResp {
//    private long id;
    private String status;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

//    public String getMessage() {
//        return message;
//    }
//    public void setMessage(String message) {
//        this.message = message;
//    }
}