package com.androidgeeks.hp.zoyme.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;

import com.androidgeeks.hp.zoyme.adapter.ColorPopAdapter;
import com.androidgeeks.hp.zoyme.adapter.SizePopAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.BuyerProductImage;
import com.androidgeeks.hp.zoyme.model.BuyerProductModel;
import com.androidgeeks.hp.zoyme.model.CategoryModel;
import com.androidgeeks.hp.zoyme.model.ColorModel;
import com.androidgeeks.hp.zoyme.model.SizeModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.BetterSpinner;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMeBuyerProductCreateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_display_name;
    private EditText edt_brand;
    private EditText edt_material;
    private EditText edt_description;
    private EditText edt_quantity;
    private EditText edt_product_price;
    private Button save, update;
    private ImageView toolbar_iv_back;
    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    String selectedImagePath;
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
    String image = "", image1 = "", image2 = "", image3 = "", image4 = "", signature_image;
    InputStream inputStream = null;
    String encodedString = "", encodedString1 = "", encodedString2 = "", encodedString3 = "", encodedString4 = "";
    ArrayList<ColorModel> colorModels;
    ArrayList<CategoryModel> categoryModels;
    private ArrayList<SizeModel> sizeModels;
    private ArrayList<String> categoryList;
    private BetterSpinner fragment_category;
    private TextView fragment_size_drop;
    private TextView fragment_color_drop;
    private ImageView upload_product0;
    private ImageView upload_product1;
    private ImageView upload_product2;
    private ImageView upload_product3;
    private ImageView upload_product4;
    private String categoryId = "";
    private ArrayList<String> colorlist;
    private ArrayList<String> colorlistId;
    private ArrayList<String> sizeList;
    private ArrayList<String> sizeListId;
    public static boolean[] checkSelected;
    int categoryid;
    String id, total_txt, addressid, edit_address;
    String position;
    String[] words;
    String description, brand, material;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_buyer_productcreate);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        edt_display_name = (EditText) findViewById(R.id.edt_display_name);
        edt_brand = (EditText) findViewById(R.id.edt_brand);
        edt_material = (EditText) findViewById(R.id.edt_material);
        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_quantity = (EditText) findViewById(R.id.edt_quantity);
        edt_product_price = (EditText) findViewById(R.id.edt_product_price);
        save = (Button) findViewById(R.id.save);
        update = (Button) findViewById(R.id.update);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        upload_product0 = (ImageView) findViewById(R.id.upload_product0);
        upload_product1 = (ImageView) findViewById(R.id.upload_product1);
        upload_product2 = (ImageView) findViewById(R.id.upload_product2);
        upload_product3 = (ImageView) findViewById(R.id.upload_product3);
        upload_product4 = (ImageView) findViewById(R.id.upload_product4);


        fragment_category = (BetterSpinner) findViewById(R.id.fragment_category);
        fragment_size_drop = (TextView) findViewById(R.id.fragment_size_drop);
        fragment_color_drop = (TextView) findViewById(R.id.fragment_color_drop);

        colorModels = new ArrayList<ColorModel>();
        sizeModels = new ArrayList<SizeModel>();
        categoryModels = new ArrayList<CategoryModel>();
        categoryList = new ArrayList<>();
        colorlist = new ArrayList<String>();
        colorlistId = new ArrayList<String>();
        sizeList = new ArrayList<String>();
        sizeListId = new ArrayList<String>();

        upload_product0.setOnClickListener(this);
        upload_product1.setOnClickListener(this);
        upload_product2.setOnClickListener(this);
        upload_product3.setOnClickListener(this);
        upload_product4.setOnClickListener(this);
        fragment_size_drop.setOnClickListener(this);
        fragment_color_drop.setOnClickListener(this);

        addressid = getIntent().getStringExtra("id");
        edit_address = getIntent().getStringExtra("edit_buyer");

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeBuyerProductCreateActivity.this);
        progressDialog = new ProgressDialog(ZoyMeBuyerProductCreateActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        if (addressid != null) {

            edt_display_name.setText(sharedPreferenceManager.getName());
            total_txt = sharedPreferenceManager.getDescription();

            words = total_txt.split("/");
            brand = words[0];
            material = words[1];
            description = words[2];

            edt_brand.setText(brand);
            edt_material.setText(material);
            edt_description.setText(description);

            edt_quantity.setText(sharedPreferenceManager.getQuantity());
            edt_product_price.setText(sharedPreferenceManager.getPrice());



            update.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog = new ProgressDialog(ZoyMeBuyerProductCreateActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Please wait...");
                    makeJsonObjBuyerUpdate();

                }
            });


        }

        if (Utils.getConnectivityStatusVal(ZoyMeBuyerProductCreateActivity.this)) {
            progressDialog.show();
            makeJsonObjBuyerCreate();
        } else {
            CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
        }


        fragment_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*isSizeSelected = true;*/
                categoryId = categoryModels.get(position).getId();
            }
        });


        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFormsFields();
            }
        });


    }

    private void validateFormsFields() {

        if (edt_display_name.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeBuyerProductCreateActivity.this, getString(R.string.app_name), "Please enter display name");
            edt_display_name.requestFocus();
        } else if ((edt_brand.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBuyerProductCreateActivity.this, getString(R.string.app_name), "Please enter brand");
            edt_brand.requestFocus();
        } else if ((edt_description.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBuyerProductCreateActivity.this, getString(R.string.app_name), "Please enter description");
            edt_description.requestFocus();
        } else if ((edt_quantity.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBuyerProductCreateActivity.this, getString(R.string.app_name), "Please enter quantity");
            edt_quantity.requestFocus();
        } else {
            if (Utils.getConnectivityStatusVal(ZoyMeBuyerProductCreateActivity.this)) {
                progressDialog.show();
                makeJsonObjBuyerStore();
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
        int permission = ActivityCompat.checkSelfPermission(ZoyMeBuyerProductCreateActivity.this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "permission is not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(ZoyMeBuyerProductCreateActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        } else {
            //Toast.makeText(this, "permission is already granted_write_your_code_here", Toast.LENGTH_SHORT).show();
            int permissionForStorage = ActivityCompat.checkSelfPermission(ZoyMeBuyerProductCreateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionForStorage != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "permission is not granted", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(ZoyMeBuyerProductCreateActivity.this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION_CAMERA_STORAGE);
            } else {
                intentToOpenCamera();
            }
        }
    }

    public void intentToOpenCamera() {
        mFromDirectPermission = false;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(ZoyMeBuyerProductCreateActivity.this.getPackageManager()) != null) {
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
        int permission = ActivityCompat.checkSelfPermission(ZoyMeBuyerProductCreateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ZoyMeBuyerProductCreateActivity.this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION);
        } else {
            intentToOpenGallery();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            mFromDirectPermission = false;
            Uri selectedImage = data.getData();
            selectedImagePath = getPath(selectedImage);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            signature_image = cursor.getString(columnIndex);
            image = signature_image;
            image1 = signature_image;
            image2 = signature_image;
            image3 = signature_image;
            image4 = signature_image;
            fileUri = getImageUri(getApplicationContext(), BitmapFactory.decodeFile(signature_image));

            if (categoryid == 0) {
                upload_product0.setImageBitmap(BitmapFactory.decodeFile(image));

                try {
                    inputStream = new FileInputStream(image);
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

            } else if (categoryid == 1) {
                upload_product1.setImageBitmap(BitmapFactory.decodeFile(image1));
                try {
                    inputStream = new FileInputStream(image1);
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
                    encodedString1 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    Log.d("image _url", "" + encodedString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            } else if (categoryid == 2) {
                upload_product2.setImageBitmap(BitmapFactory.decodeFile(image2));

                try {
                    inputStream = new FileInputStream(image2);
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
                    encodedString2 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    Log.d("image _url", "" + encodedString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            } else if (categoryid == 3) {
                upload_product3.setImageBitmap(BitmapFactory.decodeFile(image3));

                try {
                    inputStream = new FileInputStream(image3);
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
                    encodedString3 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    Log.d("image _url", "" + encodedString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            } else if (categoryid == 4) {
                upload_product4.setImageBitmap(BitmapFactory.decodeFile(image4));
                try {
                    inputStream = new FileInputStream(image4);
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
                    encodedString4 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    Log.d("image _url", "" + encodedString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && null != data) {
            mFromDirectPermission = false;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            try {
                fileUri = getImageUri(getApplicationContext(), bitmap);
                signature_image = getPath(getApplicationContext(), fileUri);
                image = signature_image;
                image1 = signature_image;
                image2 = signature_image;
                image3 = signature_image;
                image4 = signature_image;


                if (categoryid == 0) {
                    upload_product0.setImageBitmap(BitmapFactory.decodeFile(image));

                    try {
                        inputStream = new FileInputStream(image);
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


                } else if (categoryid == 1) {
                    upload_product1.setImageBitmap(BitmapFactory.decodeFile(image1));

                    try {
                        inputStream = new FileInputStream(image1);
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
                        encodedString1 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        Log.d("image _url", "" + encodedString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                } else if (categoryid == 2) {
                    upload_product2.setImageBitmap(BitmapFactory.decodeFile(image2));
                    try {
                        inputStream = new FileInputStream(image2);
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
                        encodedString2 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        Log.d("image _url", "" + encodedString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                } else if (categoryid == 3) {
                    upload_product3.setImageBitmap(BitmapFactory.decodeFile(image3));
                    try {
                        inputStream = new FileInputStream(image3);
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
                        encodedString3 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        Log.d("image _url", "" + encodedString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                } else if (categoryid == 4) {
                    upload_product4.setImageBitmap(BitmapFactory.decodeFile(image4));

                    try {
                        inputStream = new FileInputStream(image4);
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
                        encodedString4 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        Log.d("image _url", "" + encodedString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }

            } catch (NullPointerException ne) {
                saveBitmap(bitmap);
            }
        } else if (requestCode == 168) {
            mFromDirectPermission = true;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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


    private void makeJsonObjBuyerCreate() {
        Map<String, String> productParams = new LinkedHashMap<>();
        productParams.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_SELLER_CREATE, productParams, CreateReponseListener,
                CreateErrorListener), "tag_buyerCreate_req");

    }

    Response.Listener<JSONObject> CreateReponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            Log.d("response", response.toString());
            progressDialog.hide();
            try {
                if (response.getInt("status") == 200) {
                    JSONObject jsonObject = response.getJSONObject("data");


                    JSONArray jsonArrayCategories = jsonObject.getJSONArray("categories");

                    if (jsonObject.getJSONArray("categories").length() != 0) {

                        for (int i = 0; i < jsonArrayCategories.length(); i++) {
                            JSONObject resultJson = (JSONObject) jsonArrayCategories.get(i);

                            CategoryModel categoryPop = new CategoryModel();
                            categoryPop.setId(resultJson.getString("id"));
                            categoryPop.setName(resultJson.getString("name"));

                            categoryModels.add(categoryPop);

                            categoryList.add(resultJson.getString("name"));

                        }
                        fragment_category.setAdapter(new ArrayAdapter<String>(ZoyMeBuyerProductCreateActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                categoryList));
                    }

                    JSONArray jsonArrayColor = jsonObject.getJSONArray("colors");
                    if (jsonObject.getJSONArray("colors").length() != 0) {

                        for (int i = 0; i < jsonArrayColor.length(); i++) {
                            JSONObject resultJson = (JSONObject) jsonArrayColor.get(i);

                            ColorModel colorpop = new ColorModel();
                            colorpop.setId(resultJson.getString("id"));
                            colorpop.setName(resultJson.getString("name"));
                            colorpop.setCode(resultJson.getString("code"));

                            colorModels.add(colorpop);

                            colorlist.add(resultJson.getString("name"));
                            colorlistId.add(resultJson.getString("id"));

                        }


                    }
                    JSONArray jsonArraySizes = jsonObject.getJSONArray("sizes");
                    if (jsonObject.getJSONArray("sizes").length() != 0) {
                        for (int i = 0; i < jsonArraySizes.length(); i++) {
                            JSONObject resultJson = (JSONObject) jsonArraySizes.get(i);

                            SizeModel sizepop = new SizeModel();
                            sizepop.setId(resultJson.getString("id"));
                            sizepop.setName(resultJson.getString("name"));

                            sizeModels.add(sizepop);

                            sizeList.add(resultJson.getString("name"));
                            sizeListId.add(resultJson.getString("id"));

                        }
                    }

                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerProductCreateActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerProductCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBuyerProductCreateActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener CreateErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("response", error.toString());


        }
    };


    private void makeJsonObjBuyerUpdate() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("name", edt_display_name.getText().toString());
        params.put("description", edt_brand.getText().toString() + "/" + edt_material.getText().toString() + "/" + edt_quantity.getText().toString());
        params.put("quantity", edt_quantity.getText().toString());
        params.put("price", edt_product_price.getText().toString());
        params.put("category_id", categoryId);
        params.put("color", ColorPopAdapter.id);
        params.put("size", SizePopAdapter.id);
        params.put("image_1", "" + encodedString);
        params.put("image_2", "" + encodedString1);
        params.put("image_3", "" + encodedString2);
        params.put("image_4", "" + encodedString3);
        params.put("image_5", "" + encodedString4);
        params.put("id", addressid);

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_PRODUCT_UPDATE, params, storeResponseListener, storeResponseError), "tag_store_req");

    }


    private void makeJsonObjBuyerStore() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("name", edt_display_name.getText().toString());
        params.put("description", edt_brand.getText().toString() + "/" + edt_material.getText().toString() + "/" + edt_quantity.getText().toString());
        params.put("quantity", edt_quantity.getText().toString());
        params.put("price", edt_product_price.getText().toString());
        params.put("category_id", categoryId);
        params.put("color", ColorPopAdapter.id);
        params.put("size", SizePopAdapter.id);
        params.put("image_1", "" + encodedString);
        params.put("image_2", "" + encodedString1);
        params.put("image_3", "" + encodedString2);
        params.put("image_4", "" + encodedString3);
        params.put("image_5", "" + encodedString4);

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_PRODUCT_STORE, params, storeResponseListener, storeResponseError), "tag_store_req");
    }


    Response.Listener<JSONObject> storeResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());

            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");

                    JSONObject jsonObject = response.getJSONObject("data");
                    BuyerProductModel buyerProductModel = new BuyerProductModel();

                    buyerProductModel.setId(jsonObject.getString("id"));
                    buyerProductModel.setName(jsonObject.getString("name"));
                    buyerProductModel.setDescription(jsonObject.getString("description"));
                    buyerProductModel.setCategory_name(jsonObject.getString("category_name"));
                    buyerProductModel.setPrice(jsonObject.getString("price"));

                    sharedPreferenceManager.setId(jsonObject.getString("id"));
                    sharedPreferenceManager.setName(jsonObject.getString("name"));
                    sharedPreferenceManager.setDescription(jsonObject.getString("description"));
                    sharedPreferenceManager.setQuantity(jsonObject.getString("quantity"));
                    sharedPreferenceManager.setPrice(jsonObject.getString("price"));
                    sharedPreferenceManager.setCategoryname(jsonObject.getString("category_name"));


                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerProductCreateActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerProductCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent loginIntent = new Intent(ZoyMeBuyerProductCreateActivity.this, ZoyMeBuyerProductDetailActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBuyerProductCreateActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerProductCreateActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerProductCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBuyerProductCreateActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    Response.ErrorListener storeResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    private void initiatePopUpSize() {
        final Dialog dialog2 = new Dialog(ZoyMeBuyerProductCreateActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog2.setContentView(R.layout.pop_up_window);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ListView txt = (ListView) dialog2.findViewById(R.id.dropDownList);
        TextView texttop = (TextView) dialog2.findViewById(R.id.texttop);
        texttop.setText("Select Size");
        Button btn = (Button) dialog2.findViewById(R.id.ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                fragment_size_drop.setText(SizePopAdapter.value);
                SizePopAdapter.value = "";

            }
        });


        SizePopAdapter adapter = new SizePopAdapter(ZoyMeBuyerProductCreateActivity.this, sizeList, sizeListId);
        txt.setAdapter(adapter);
        Log.e("size", "" + txt);


        dialog2.show();
        dialog2.getWindow().setAttributes(lp);
    }

    private void initiatePopUp() {

        final Dialog dialog2 = new Dialog(ZoyMeBuyerProductCreateActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog2.setContentView(R.layout.pop_up_window);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ListView txt = (ListView) dialog2.findViewById(R.id.dropDownList);

        Button btn = (Button) dialog2.findViewById(R.id.ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                fragment_color_drop.setText(ColorPopAdapter.value);
                ColorPopAdapter.value = "";


            }
        });


        ColorPopAdapter adapter = new ColorPopAdapter(ZoyMeBuyerProductCreateActivity.this, colorlist, colorlistId);
        txt.setAdapter(adapter);
        Log.e("size", "" + txt);


        dialog2.show();
        dialog2.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_product0:
                categoryid = 0;

                uploadImageDailog();

                break;
            case R.id.upload_product1:
                categoryid = 1;
                uploadImageDailog();

                break;
            case R.id.upload_product2:
                categoryid = 2;
                uploadImageDailog();
                break;
            case R.id.upload_product3:
                categoryid = 3;
                uploadImageDailog();
                break;
            case R.id.upload_product4:
                categoryid = 4;
                uploadImageDailog();
                break;
            case R.id.fragment_size_drop:
                SizePopAdapter.id = "";
                initiatePopUpSize();
                break;
            case R.id.fragment_color_drop:
                ColorPopAdapter.id = "";
                initiatePopUp();
                break;
        }
    }
}
