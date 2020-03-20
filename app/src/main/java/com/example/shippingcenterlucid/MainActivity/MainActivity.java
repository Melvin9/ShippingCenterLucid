package com.example.shippingcenterlucid.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shippingcenterlucid.Login.ActivityLogin;
import com.example.shippingcenterlucid.R;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class MainActivity extends AppCompatActivity {
    String TAG = "GenerateQRCode";
    ImageView qrImage;
    EditText batch,vehicle,item,wt,aadhar;
    public static Button date;
    Button start, save;
    Button fid;
    String inputValue="ABCD";//batchID should be generated

    String savePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).toString();
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    String aid;
    int c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrImage = findViewById(R.id.qr);
        start = findViewById(R.id.generate);
        batch=findViewById(R.id.batch);
        save = findViewById(R.id.save);
        date=findViewById(R.id.pickDate);
        vehicle=findViewById(R.id.vehicle);
        item=findViewById(R.id.item);
        fid=findViewById(R.id.fid);
        wt=findViewById(R.id.wt);
        aadhar=findViewById(R.id.aadhar);
        fid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Getting ID!!!");
                progressDialog.show();
                aid=aadhar.getText().toString();
                final StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://caressing-compensat.000webhostapp.com/farmer_details.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("details");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject o=jsonArray.getJSONObject(i);
                                if(o.getString("adhaar").equals(aid)){
                                    fid.setText(o.getString("fid"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
                RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);


            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DateFragment();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Getting Batch Code!!!");
                progressDialog.show();
                final StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://caressing-compensat.000webhostapp.com/current_status_shipping.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("details");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject o=jsonArray.getJSONObject(i);
                                c=o.getInt("sno");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
                RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);
                inputValue="PC"+(c+1);
                if (inputValue.length() > 0) {
                    batch.setText(inputValue);
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(
                            inputValue, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.v(TAG, e.toString());
                    }
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        boolean save = new QRGSaver().save(savePath, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                        String result = save ? "Image Saved" : "Image Not Saved";
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Entering Details!!!");
                progressDialog.show();
                RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
                StringRequest request=new StringRequest(Request.Method.POST, "https://caressing-compensat.000webhostapp.com/insert_shipping_details.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String,String> hashMap= new HashMap<>();
                        hashMap.put("id",aid);
                        hashMap.put("bno","PC0001");
                        hashMap.put("sno",c+1+"");
                        hashMap.put("item",item.getText().toString());
                        hashMap.put("date","2020-02-27");
                        hashMap.put("status","S");
                        hashMap.put("vno",vehicle.getText().toString());
                        hashMap.put("qty",wt.getText().toString());

                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });
    }
}
