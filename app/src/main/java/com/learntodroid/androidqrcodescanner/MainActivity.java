package com.learntodroid.androidqrcodescanner;

import
        androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Movie;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Button qrCodeFoundButton;
    private String qrCode;
    private JSONObject objeto;
    private JSONArray arrayobjeto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("myTag", "onCreate funciona");

        previewView = findViewById(R.id.activity_main_previewView);
        qrCode = "61505e4e7e438da72105168d";
        obtenerDatosVolley(qrCode);


        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton);
        qrCodeFoundButton.setVisibility(View.INVISIBLE);
        qrCodeFoundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_SHORT).show();

                Log.i(MainActivity.class.getSimpleName(), "QR Code Found: " + qrCode);
                obtenerDatosVolley(qrCode);
           //     try {
           //          String myJsonString = objeto.getString("ID");
           //     } catch (JSONException e) {
           //      e.printStackTrace();
           //     }

                Intent intent =new Intent(MainActivity.this, Imprimirdatos.class);
                //intent.putExtra("json", arrayobjeto.toString());
                //intent.putExtra("json", objeto.toString());

                startActivity(intent);
            }
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();


    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void obtenerDatosVolley(String _qrCode) {
        String url = "https://marketplace.gisai.geoide.upm.es/identifier/identifierJSON?id=" +_qrCode;
        JSONObject dato= new JSONObject();
        Log.i("myTag", "función funciona");

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);  //@Carmen: te faltaba esto

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("myTag", "response funciona");
                try {
                    JSONObject productJSON =response.getJSONObject("product");
                    JSONArray products = productJSON.getJSONArray("products");

                    for(int i=0;i<products.length();i++) {
                        String product_id= products.getString(i);
                        Log.i("myTag", "producto "+i+":"+product_id);
                    }

                    String name = productJSON.getString("name");
                    Log.i("myTag", "Nombre: "+name);

                    String company = productJSON.getString("company");
                    Log.i("myTag", "Empresa: "+company);

                    String nutri_info = productJSON.getString("nutritionalInformation");
                    Log.i("myTag", "Inform. nutricional: "+nutri_info);

                    double pollution = productJSON.getDouble("pollutionGenerated");
                    Log.i("myTag", "Contaminación generada: "+pollution);

                    JSONObject shipmentJSON =response.getJSONObject("shipment_status");
                    JSONArray status_array = shipmentJSON.getJSONArray("status");

                    for(int i=0;i<status_array.length();i++) {
                        JSONObject itemJSON= status_array.getJSONObject(i);
                        String location_item = itemJSON.getJSONArray("currentLocation").toString();
                        Log.i("myTag", "localización "+i+", coordenadas: "+location_item);

                        String status_item = itemJSON.getString ("status");
                        Log.i("myTag", "localización "+i+", estado: "+status_item);

                        double distance_item = itemJSON.getDouble ("distance");
                        Log.i("myTag", "localización "+i+", distancia: "+distance_item);

                        double pollution_item = itemJSON.getDouble("pollutionGenerated");
                        Log.i("myTag", "localización "+i+", contaminación generada: "+pollution_item);

                        String registry_date_item = itemJSON.getString ("updatedAt");
                        Log.i("myTag", "localización "+i+", fecha registro: "+registry_date_item);

                    }




                    //jsonarray=response.getJSONArray(_qrCode);
                    asignarobjeto(dato);
                    //asignarArray(jsonarray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },

        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Access the RequestQueue through your singleton class.
        requestQueue.add(request); //@Carmen: te faltaba esto


    }






    private void asignarobjeto(JSONObject d){
       objeto=d;
    }
    private void asignarArray(JSONArray a){
        arrayobjeto=a;
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                qrCodeFoundButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void qrCodeNotFound() {
                qrCodeFoundButton.setVisibility(View.INVISIBLE);
            }
        }));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }
}