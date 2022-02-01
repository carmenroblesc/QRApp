package com.learntodroid.androidqrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.Serializable;

import org.json.JSONObject;

public class Imprimirdatos extends AppCompatActivity implements Serializable{
    private String arrayobjeto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprimirdatos);
        Intent intent = getIntent();
        arrayobjeto = intent.getStringExtra("jsonArray");
        /*try {
            JSONObject obj = new JSONObject(getIntent().getStringExtra("json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        /*try { JSONArray array = new JSONArray(arrayobjeto);
            System.out.println(array.toString(2));
        }
        catch (JSONException e) { e.printStackTrace();
        }*/


        /*try {
            String myJsonString = objeto.getString("ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


    }

}