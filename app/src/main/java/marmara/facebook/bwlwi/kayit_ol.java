package marmara.facebook.bwlwi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

/**
 * Created by sercan on 21.02.2015.
 */
public class kayit_ol extends ActionBarActivity implements View.OnClickListener {
    public SharedPreferences sp;
    public SharedPreferences.Editor Ed;
    public EditText reg_fullname;
    public EditText reg_no;
    public EditText reg_password;
    public TextView link_to_login;
    public Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //strict mode main thread üzerinde senkronize http çağrılarını aktifleştirmek için
        StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        //internet varsa devam et
        if (isNetworkAvailable()) {
            setContentView(R.layout.kayit_ol);
            sp=getSharedPreferences("Login", MODE_WORLD_READABLE);
            //klasik View definisyonları
            submit =(Button)findViewById(R.id.btnRegister);
            reg_fullname =(EditText)findViewById(R.id.reg_fullname);
            reg_no =(EditText)findViewById(R.id.reg_no);
            reg_password =(EditText)findViewById(R.id.reg_password);
            link_to_login = (TextView)findViewById(R.id.link_to_login);
            submit.setOnClickListener(this);
            link_to_login.setOnClickListener(this);
        } else {
            //internet yoksa internet yok uyarısı göster
            setContentView(R.layout.yorumlar_dialog);
        }
    }
    //giriş yap onclick metod
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister: {
                String ogrenci_no_ = reg_no.getText().toString();
                String ogrenci_ad_soyad = reg_fullname.getText().toString();
                String ogrenci_sifre = reg_password.getText().toString();
                if (isNetworkAvailable()) {
                    Ion.with(getApplicationContext())
                            .load("http://www.bilmemnesitesi.com/kayitol.php")
                            .setBodyParameter("ogrencino", ogrenci_no_)
                            .setBodyParameter("sifre", ogrenci_sifre)
                            .setBodyParameter("adsoyad", ogrenci_ad_soyad)
                            .asString()
                            .withResponse()
                            .setCallback(new FutureCallback<Response<String>>() {
                                @Override
                                public void onCompleted(Exception e, Response<String> result) {
                                    kayit_kontrol(result.getResult());
                                }
                            });
                    //shared preferences atamaları
                    Ed = sp.edit();
                    Ed.putString("ogrenci_no", ogrenci_no_);
                    Ed.putString("password", ogrenci_sifre);
                    Ed.commit();
                }
                break;
            }
            case R.id.link_to_login:{
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                break;
            }
        }
    }
    public void kayit_kontrol(String response){

        //login kontrol parametreleri
        if(response.equals("hata")) {
            kayit_uyari_goster("Kayıt başarısız!");
        }else if(response.equals("ok")){
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
        }
    }
    public void kayit_uyari_goster(String mesaj) {
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
    // cihaz internete bağlımı kontrol metodu
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
