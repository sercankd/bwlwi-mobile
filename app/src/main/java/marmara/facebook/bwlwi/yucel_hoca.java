package marmara.facebook.bwlwi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

/**
 * Created by sercan on 22.02.2015.
 */
public class yucel_hoca extends ActionBarActivity {
    public WebView yucel_web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //strict mode main thread üzerinde senkronize http çağrılarını aktifleştirmek için
        StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        //internet varsa devam et
        if (isNetworkAvailable()) {
            setContentView(R.layout.yucel_hoca);

            yucel_web = (WebView) findViewById(R.id.webView);
            yucel_web.getSettings().setJavaScriptEnabled(true);
            yucel_web.loadUrl("http://www.bilmemnesitesi.com/yucel_tweet/");
        } else {
            //internet yoksa internet yok uyarısı göster
            setContentView(R.layout.yorumlar_dialog);
        }
    }
    // cihaz internete bağlımı kontrol metodu
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
