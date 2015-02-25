package marmara.facebook.bwlwi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    public SharedPreferences sp;
    public SharedPreferences.Editor Ed;
    public EditText ogrencino;
    public EditText password;
    public Button submit;
    public Button kayit_ol;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        //strict mode main thread üzerinde senkronize http çağrılarını aktifleştirmek için
        StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        //internet varsa devam et
        if (isNetworkAvailable()) {
            setContentView(R.layout.login);
            sp=getSharedPreferences("Login", MODE_WORLD_READABLE);

            //klasik View definisyonları
            submit =(Button)findViewById(R.id.button2);
            ogrencino =(EditText)findViewById(R.id.editText);
            password =(EditText)findViewById(R.id.editText2);
            kayit_ol = (Button)findViewById(R.id.button);
            // shared preferences saklı değişkenlerini çek
            String ogrenci_no = sp.getString("ogrenci_no",null);
            String password_ = sp.getString("password",null);

            //giriş bilgilerini kaydet
            if(ogrenci_no != null && password_ != null) {
                ogrencino.setText(ogrenci_no);
                password.setText(password_);
            }
            //butona onclick ataması
            submit.setOnClickListener(this);
            kayit_ol.setOnClickListener(this);
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

    //giriş yap onclick metod
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button2: {
                    String ogrenci_no_ = ogrencino.getText().toString();
                    String ogrenci_pass_ = password.getText().toString();
                    if (isNetworkAvailable()) {
                        Ion.with(getApplicationContext())
                                .load("http://www.bilmemnesitesi.com/login.php")
                                .setBodyParameter("ogrencino", ogrenci_no_)
                                .setBodyParameter("sifre", ogrenci_pass_)
                                .asString()
                                .withResponse()
                                .setCallback(new FutureCallback<Response<String>>() {
                                    @Override
                                    public void onCompleted(Exception e, Response<String> result) {
                                        login_kontrol(result.getResult());
                                    }
                                });
                        //shared preferences atamaları
                        Ed = sp.edit();
                        Ed.putString("ogrenci_no", ogrenci_no_);
                        Ed.putString("password", ogrenci_pass_);
                        Ed.commit();
                    }
                    break;
                }
                case R.id.button:{
                    Intent myIntent = new Intent(getApplicationContext(), kayit_ol.class);
                    startActivityForResult(myIntent, 0);
                    break;
            }
            }
        }
    public void login_kontrol(String response){

        //login kontrol parametreleri
        if(response.equals("hata")) {
            login_uyari_goster("Giriş başarısız!");
        }else if(response.equals("ok")){
            Intent myIntent = new Intent(getApplicationContext(), iletiler.class);
            startActivityForResult(myIntent, 0);
        }
    }
    // hakkında bilgisi dialog
    public void login_uyari_goster(String mesaj) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Hata");
        builder1.setMessage(mesaj);
        builder1.setCancelable(true);
        builder1.setNegativeButton("Kapat",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}