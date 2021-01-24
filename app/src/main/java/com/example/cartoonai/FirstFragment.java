package com.example.cartoonai;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;


import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.content.pm.PackageManager;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FirstFragment extends Fragment  implements PictureCallback {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICTURE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final int REQUEST_CODE = 1;
    private final static String DEBUG_TAG = "MakePhotoActivity";
    public TextView info;
    public Button button_1;
    public Button button_2;
    public Button button_3;
    public ProgressBar spinner;
    String currentPhotoPath;
    private Bitmap bitmap;
    private ImageView imageView;
    private Camera camera;
    private int cameraId = 0;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyContext.first = this;
        imageView = view.findViewById(R.id.CameraImage);
        info = view.findViewById(R.id.textview_info);

        button_1 = view.findViewById(R.id.button_first);
        button_2 = view.findViewById(R.id.button_first_2);
        button_3= view.findViewById(R.id.button_first_3);
        spinner = view.findViewById(R.id.progressBar1);

        spinner.setVisibility(View.GONE);
        button_3.setVisibility(View.GONE);
        button_2.setEnabled(false);
        button_3.setEnabled(false);

        info.setText("take photo (selfie camera)");

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MyContext.context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyContext.main, new String[]{android.Manifest.permission.CAMERA}, 50);
                }
                if (ContextCompat.checkSelfPermission(MyContext.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyContext.main, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(MyContext.context.getPackageManager()) != null) {

                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }


                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(MyContext.context,
                                "com.example.android.fileprovider",
                                photoFile);


                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });



        view.findViewById(R.id.button_first_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        view.findViewById(R.id.button_first_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MyContext.context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyContext.main, new String[]{android.Manifest.permission.INTERNET}, 50);
                }

                button_1.setEnabled(false);
                button_2.setEnabled(false);
                button_3.setEnabled(false);
                button_2.setVisibility(View.GONE);
                button_3.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                info.setText("Uploading to server and triggering AI flow");
                PostFileAPI a = new PostFileAPI(imageView);
                a.execute(MyContext.upload_url + "/upload",currentPhotoPath);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE_CAPTURE){ // && resultCode == RESULT_OK) {
            File imgFile = new  File(currentPhotoPath);
            if(imgFile.exists())            {
                imageView.setImageURI(Uri.fromFile(imgFile));
                button_2.setEnabled(true);
                button_3.setEnabled(false);
            }
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;

        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            //if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                // break;
            //}
        }
        return cameraId;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File storageDir = MyContext.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

@RequiresApi(api = Build.VERSION_CODES.O)
public void onEnd() {

    File file = Environment.getExternalStorageDirectory();
    File dir = new File(file.getAbsolutePath() + "/MyPics");
    dir.mkdirs();

    File outFile = new File(dir, String.valueOf(MyContext.fileToSave.toPath().getFileName()));

    try {
        Files.copy(MyContext.fileToSave.toPath(),outFile.toPath());
    } catch (IOException e) {
        e.printStackTrace();
    }

    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    Uri contentUri = Uri.fromFile(outFile);
    mediaScanIntent.setData(contentUri);
    MyContext.main.sendBroadcast(mediaScanIntent);
    info.setText("File saved to gallery");
}


}