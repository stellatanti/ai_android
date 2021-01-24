package com.example.cartoonai;


import android.os.AsyncTask;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


public class PostFileAPI  extends AsyncTask<String, String, String> {

    String upLoadServerUri = null;

    /**********  File Path *************/
//    final String uploadFilePath = "/mnt/sdcard/";
//    final String uploadFileName = "service_lifecycle.png";


    int serverResponseCode = 0;

    ImageView imageView;

    public PostFileAPI(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        UploadResp msg = new Gson().fromJson(s, UploadResp.class);
        System.out.println(msg);

        StatusFile st = new StatusFile(msg.getId(),imageView);
        st.execute(MyContext.upload_url + "/status/" + msg.getId());
    }

    @Override
    protected String doInBackground(String... params) {

        String urlString = params[0]; // URL to call
        String sourceFileUri = params[1];

        String[] tmp = params[1].split("/");
        String uploadFileName = tmp[tmp.length-1];




        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);


        if (!sourceFile.isFile()) {
            return null;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(urlString);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", uploadFileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + uploadFileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

//                    runOnUiThread(new Runnable() {
//                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;



//                            messageText.setText(msg);
//                            Toast.makeText(UploadToServer.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

//                InputStream in = new BufferedInputStream(conn.getInputStream());
//            readStream(in);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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

                if(conn != null) // Make sure the connection is not null.
                    conn.disconnect();

                return sb.toString();





            } catch (MalformedURLException ex) {

//                dialog.dismiss();
                ex.printStackTrace();

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("MalformedURLException Exception : check script url.");
//                        Toast.makeText(UploadToServer.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

//                dialog.dismiss();
                e.printStackTrace();

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("Got Exception : see logcat ");
//                        Toast.makeText(UploadToServer.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
                Log.e("Upload  Exception", "Exception : "
                        + e.getMessage(), e);
            }
//            dialog.dismiss();
//            return serverResponseCode;

        } // End else block


        return null;
    }
}






//
//public class PostFileAPI {
//
//    public PostFileAPI(){
//        //set context variables if required
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//        String urlString = params[0]; // URL to call
//        String data = params[1]; //data to post
//        OutputStream out = null;
//
//
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//
//
//
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("POST");
//
//            urlConnection.setRequestProperty("Connection", "Keep-Alive");
//            urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
//            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            urlConnection.setRequestProperty("uploaded_file", fileName);
//
//
//            out = new BufferedOutputStream(urlConnection.getOutputStream());
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//            writer.write(data);
//            writer.flush();
//            writer.close();
//            out.close();
//            urlConnection.connect();
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            readStream(in);
//            if(urlConnection != null) // Make sure the connection is not null.
//                urlConnection.disconnect();
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        return "";
//    }
//
//    private void readStream(InputStream in) {
//        try {
//            System.out.println(in.read());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}


class UploadResp {
    private long id;
//    private String message;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

//    public String getMessage() {
//        return message;
//    }
//    public void setMessage(String message) {
//        this.message = message;
//    }
}