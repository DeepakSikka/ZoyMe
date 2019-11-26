package com.androidgeeks.hp.zoyme.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMeBussinesDetailActivity extends AppCompatActivity {
    private EditText edt_bussines_name;
    private EditText edt_gst;
    private EditText edt_tan;
    private EditText edt_cin;
    private EditText flat_no;
    private EditText area;
    private EditText city;
    private EditText state;
    private Button cancel, save_btn;
    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    private ImageView toolbar_iv_back;
    private TextView attach_image;
    private ImageView img_signature;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 2;
    private static final int REQUEST_CODE_PERMISSION_CAMERA_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int mUserChoose = 0;
    private Boolean mFromDirectPermission = false;
    private static int CAMERA_PIC_REQUEST = 0;
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath;
    private Uri fileUri;
    String image, signature_image;
    InputStream inputStream = null;
    String encodedString;

    @Override
    protected void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_bussines_detail);
        edt_bussines_name = (EditText) findViewById(R.id.edt_bussines_name);
        edt_gst = (EditText) findViewById(R.id.edt_gst);
        edt_tan = (EditText) findViewById(R.id.edt_tan);
        edt_cin = (EditText) findViewById(R.id.edt_cin);
        flat_no = (EditText) findViewById(R.id.flat_no);
        area = (EditText) findViewById(R.id.area);

        state = (EditText) findViewById(R.id.state);
        cancel = (Button) findViewById(R.id.cancel);
        save_btn = (Button) findViewById(R.id.save_btn);
        city = (EditText) findViewById(R.id.city);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        attach_image = (TextView) findViewById(R.id.attach_image);
        attach_image.setPaintFlags(attach_image.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        img_signature = (ImageView) findViewById(R.id.img_signature);
        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeBussinesDetailActivity.this);
        progressDialog = new ProgressDialog(ZoyMeBussinesDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");


        attach_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageDailog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFormsFields();
            }
        });

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Utils.getConnectivityStatusVal(ZoyMeBussinesDetailActivity.this)) {
            progressDialog.show();
            makeJsonObjBusinessDetail(UrlConstants.ZOY_ME_BUSINESS);
        } else {
            CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
        }
    }

    private void validateFormsFields() {

        if (edt_bussines_name.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter business name");
            edt_bussines_name.requestFocus();
        } else if ((edt_gst.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter Gst id");
            edt_gst.requestFocus();
        } else if ((edt_tan.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter tan");
            edt_tan.requestFocus();
        } else if ((edt_cin.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter Cin number");
            edt_cin.requestFocus();
        } else if ((flat_no.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter flat number");
            flat_no.requestFocus();
        } else if ((area.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter locality or area");
            area.requestFocus();
        } else if ((city.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter city");
            city.requestFocus();
        } else if ((state.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBussinesDetailActivity.this, getString(R.string.app_name), "Please enter state");
            state.requestFocus();
        } else {
            if (Utils.getConnectivityStatusVal(ZoyMeBussinesDetailActivity.this)) {
                progressDialog.show();
                makeJsonObjBusinessDetail(UrlConstants.ZOY_ME_UPDATE_BUSINESS);
            } else {
                CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
            }
        }


    }

    /**
     * Image code from gallery and camera
     */
    public void uploadImageDailog() {
        //Dailog for selection upload image option
        android.support.v7.app.AlertDialog.Builder getImageFrom = new android.support.v7.app.AlertDialog.Builder(this);
        getImageFrom.setTitle("Select:");
        final CharSequence[] opsChars = {"Camera", "Gallery"};
        // final CharSequence[] opsChars = {"Open Gallery"};
        getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //checking camera and storage permission
                    // showCamera();

                    mUserChoose = 1;
                    if (Build.VERSION.SDK_INT >= 23) {
                        verifyCameraPermissions();
                    } else {
                        intentToOpenCamera();
                    }

                } else if (which == 1) {
                    //checking storage permission
                    //showGallery();
                    mUserChoose = 2;
                    if (Build.VERSION.SDK_INT >= 23) {
                        verifyStoragePermissions();
                    } else {
                        intentToOpenGallery();
                    }

                }

                dialog.dismiss();
            }
        });
        getImageFrom.show();

    }

    public void verifyCameraPermissions() {
        int permission = ActivityCompat.checkSelfPermission(ZoyMeBussinesDetailActivity.this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "permission is not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(ZoyMeBussinesDetailActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        } else {
            //Toast.makeText(this, "permission is already granted_write_your_code_here", Toast.LENGTH_SHORT).show();
            //loadAcitivtyData();
            int permissionForStorage = ActivityCompat.checkSelfPermission(ZoyMeBussinesDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionForStorage != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "permission is not granted", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(ZoyMeBussinesDetailActivity.this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION_CAMERA_STORAGE);
            } else {
                intentToOpenCamera();
//                Toast.makeText(this, "permission is already granted Storage", Toast.LENGTH_SHORT).show();
            }
            //intentToOpenCamera();
//            Toast.makeText(this, "permission is already granted Camera", Toast.LENGTH_SHORT).show();
        }
    }

    public void intentToOpenCamera() {
        mFromDirectPermission = false;
//        mUserChoose = 0;
        //If user has granted permission or already had permission open gallery app
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(ZoyMeBussinesDetailActivity.this.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
    }

    public void intentToOpenGallery() {
//        mUserChoose = 0;
        mFromDirectPermission = false;
        //If user has granted permission or already had permission open gallery app
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(ZoyMeBussinesDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "permission is not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(ZoyMeBussinesDetailActivity.this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION);
        } else {
            //Toast.makeText(this, "permission is already granted_write_your_code_here", Toast.LENGTH_SHORT).show();
            //loadAcitivtyData();
            intentToOpenGallery();
//            Toast.makeText(this, "permission is already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            mFromDirectPermission = false;
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            signature_image = cursor.getString(columnIndex);
            image = signature_image;
            fileUri = getImageUri(getApplicationContext(), BitmapFactory.decodeFile(signature_image));
            img_signature.setImageBitmap(BitmapFactory.decodeFile(image));


            try {
                inputStream = new FileInputStream(signature_image);
                byte[] bytes;
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = output.toByteArray();
                encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                Log.d("image _url", "" + encodedString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && null != data) {
            mFromDirectPermission = false;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            try {
                fileUri = getImageUri(getApplicationContext(), bitmap);
                signature_image = getPath(getApplicationContext(), fileUri);
                image = signature_image;
                img_signature.setImageBitmap(BitmapFactory.decodeFile(image));


            } catch (NullPointerException ne) {
                saveBitmap(bitmap);
            }


            try {
                inputStream = new FileInputStream(signature_image);
                byte[] bytes;
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = output.toByteArray();
                encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                Log.d("image _url", "" + encodedString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 168) {
            mFromDirectPermission = true;
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void saveBitmap(Bitmap bmp) {
        String _time = "";
        Calendar cal = Calendar.getInstance();
        int millisecond = cal.get(Calendar.MILLISECOND);
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        _time = "image_" + hourofday + "" + minute + "" + second + ""
                + millisecond + ".png";
        picturePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/pictures";
        try {
            File dir = new File(picturePath);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, _time);
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
//            Log.e("error in saving image", e.getMessage());
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    private void makeJsonObjBusinessDetail(String url) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("name", edt_bussines_name.getText().toString());
        params.put("gst", edt_gst.getText().toString());
        params.put("tan", edt_tan.getText().toString());
        params.put("cin", edt_cin.getText().toString());
        params.put("flat_no", flat_no.getText().toString());
        params.put("locality", area.getText().toString());
        params.put("city", city.getText().toString());
        params.put("state", state.getText().toString());
        if (encodedString == null) {
            params.put("signature", "");
        } else {
            params.put("signature", encodedString);
        }
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, url, params, businessResponseListener, businessResponseError), "tag_business_req");
    }


    Response.Listener<JSONObject> businessResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");

                    JSONObject data = response.getJSONObject("data");
                    edt_bussines_name.setText(data.getString("name"));
                    edt_gst.setText(data.getString("gst"));
                    edt_tan.setText(data.getString("tan"));
                    edt_cin.setText(data.getString("cin"));
                    data.getString("signature");
                    flat_no.setText(data.getString("flat_no"));
                    city.setText(data.getString("city"));
                    state.setText(data.getString("state"));
                    area.setText(data.getString("locality"));

                    if (message.equalsIgnoreCase("Successfully Updated")) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBussinesDetailActivity.this).create();
                        alertDialog.setTitle(R.string.app_name);
                        alertDialog.setCancelable(false);

                        alertDialog.setMessage(response.getString("message"));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBussinesDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                                //     dialog.dismiss();

                            }
                        });
                        if (!((Activity) ZoyMeBussinesDetailActivity.this).isFinishing()) {

                            alertDialog.show();
                        }
                    } else {

                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBussinesDetailActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBussinesDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBussinesDetailActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    Response.ErrorListener businessResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

}
