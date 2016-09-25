package myapp.rxapp;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscription = getGistObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Gist>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onNext(Gist gist) {
                TextView textView = (TextView) findViewById(R.id.textView);
                if(gist == null){
                    textView.setText(R.string.issues);
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    for(Map.Entry<String, GistFile> entry : gist.files.entrySet()){
                        sb.append(entry.getKey());
                        sb.append(" - ");
                        sb.append("Length of the file ");
                        sb.append(entry.getValue().content.length());
                        sb.append("\n");
                    }
                    textView.setText(sb.toString());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

    @Nullable
    private Gist getGist() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/gists/ae590aa0e060ae6ea4a283ff9e668196")
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
            return gist;
        }
        return null;
    }

    public Observable<Gist> getGistObservable(){
        return Observable.defer(new Func0<Observable<Gist>>() {
            @Override
            public Observable<Gist> call() {
                try{
                    return Observable.just(getGist());
                }
                catch (IOException e){
                    return null;
                }
            }
        });
    }
}
