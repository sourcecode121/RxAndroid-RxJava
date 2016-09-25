package myapp.rxapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String resp;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://gist.github.com/sourcecode121/ae590aa0e060ae6ea4a283ff9e668196")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    resp = response.body().string();
                    Log.d("RESPONSE", resp);
                    return resp;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute();
    }
}
