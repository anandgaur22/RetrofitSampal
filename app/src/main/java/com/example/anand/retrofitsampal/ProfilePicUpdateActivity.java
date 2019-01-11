package com.example.anand.retrofitsampal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.anand.retrofitsampal.retrofit.ApiUtils;
import com.example.anand.retrofitsampal.retrofit.RetrofitInterfaces;
import com.example.anand.retrofitsampal.model.ProfileModel.ProfilePicUpdateModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePicUpdateActivity extends AppCompatActivity {

    String imagepath;
    private int GALLERY = 1, CAMERA = 2;
    ImageView profile_image,imgViewEditProfile;

    private static final String IMAGE_DIRECTORY = "/demonuts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);

        profile_image=findViewById(R.id.profile_image);
        imgViewEditProfile=findViewById(R.id.imgViewEditProfile);

        imgViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();
            }
        });


    }



    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(ProfilePicUpdateActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(ProfilePicUpdateActivity.this,
                Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(ProfilePicUpdateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);
        } else {
            requestWritePermission(ProfilePicUpdateActivity.this);

        }
    }

    private void takePhotoFromCamera() {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(ProfilePicUpdateActivity.this,
                Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(ProfilePicUpdateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        } else {

            requestWritePermission(ProfilePicUpdateActivity.this);
        }
    }

    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            new android.app.AlertDialog.Builder(context)
                    .setMessage("This app needs permission to use The phone Camera in order to activate the Scanner")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    }).show();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(context)
                    .setMessage("This app needs permission to use storage to save the clicked Image")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfilePicUpdateActivity.this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);

                    profile_image.setImageBitmap(bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                    Log.d("", "onActivityResult: " + bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfilePicUpdateActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            profile_image.setImageBitmap(thumbnail);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            saveImage(thumbnail);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(ProfilePicUpdateActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::--->" + f.getAbsolutePath());
            imagepath = f.getAbsolutePath();

            updateprofile();
           // saveProfileAccount();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void updateprofile() {
        Log.e("imgpath====", "" + imagepath);

        File file = new File(imagepath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_picture", file.getName(), requestFile);
        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), "45");//user_id
        RequestBody Update_Profile_Picture = RequestBody.create(MediaType.parse("multipart/form-data"), "Update_Profile_Picture");

        RetrofitInterfaces iRestInterfaces = ApiUtils.getAPIService();
        Call<ProfilePicUpdateModel> profilePicUpdateModelCall = iRestInterfaces.profileUpdateModle(id,Update_Profile_Picture, body);
        profilePicUpdateModelCall.enqueue(new Callback<ProfilePicUpdateModel>() {
            @Override
            public void onResponse(Call<ProfilePicUpdateModel> call, Response<ProfilePicUpdateModel> response) {
                if (response.isSuccessful()) {

                    String status=response.body().getResult().get(0).getStatus().toString();

                    Toast.makeText(ProfilePicUpdateActivity.this, ""+response.body().getResult().get(0).getMsg(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ProfilePicUpdateModel> call, Throwable t) {


            }
        });
    }

}
