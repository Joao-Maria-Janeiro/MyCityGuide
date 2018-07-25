package com.mycityguide.joaomjaneiro.restfulwithspring;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    Button btcCallRestApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btcCallRestApi = (Button) findViewById(R.id.btnCallRestApi);

        btcCallRestApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call Rest API
                new HttpReqTask().execute();
            }
        });

    }


    private class HttpReqTask extends AsyncTask<Void, Void, Product[]>{

        @Override
        protected Product[] doInBackground(Void... voids) {

            try {
                String apiUri = "http://10.0.2.2:1980/api/v1/product/show/1";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Product[] products = restTemplate.getForObject(apiUri, Product[].class);

                return products;
            }catch (Exception ex){
                Log.e("", ex.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Product[] products){
            super.onPostExecute(products);

            for(Product u : products){
                Log.i("Product", "#######");
                Log.i("Product_id", String.valueOf(u.getId()));
                Log.i("Product_name", u.getName());
                Log.i("Product_description", u.getDescription());
            }
        }
    }
}
