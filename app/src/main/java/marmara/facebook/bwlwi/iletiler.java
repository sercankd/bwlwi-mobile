package marmara.facebook.bwlwi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class iletiler extends ActionBarActivity {
    ListView listView;
    public static Version VERSION_2_2;
    //buradaki accesstoken gruba yetkisi olan birisinin
    public String accessToken = "FACEBOOKTAN ACCESS TOKEN AL";
    public FacebookClient facebookClient = new DefaultFacebookClient(accessToken, VERSION_2_2);
    private String until;
    public MyAdapter adapter;
    private ArrayList< Items > items;
    //facebook graph api tarih şeklini okunabilir şekle sokma
    public Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    //bildirim kontrolü için tarih manipüle
    public SimpleDateFormat formatterNM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //strict mode main thread üzerinde senkronize http çağrılarını aktifleştirmek için
        StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //cihaz internete bağlıysa postları çekmeye başla
        if(isNetworkAvailable()) {
            listView = (ListView) findViewById(R.id.listView);
            Button btnLoadMore = new Button(this);
            btnLoadMore.setText("Daha fazla göster..");
            listView.addFooterView(btnLoadMore);

            /*
                save instance state geri butonuna bastığımızda scrollbar
                ve benzeri view objelerinin eski yerlerinde kalmaları için gerekli
            */
            Parcelable state = listView.onSaveInstanceState();
            items = new ArrayList<Items>();
            adapter = new MyAdapter(this, items);
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(state);

            //postları çekmeye başla
            fbpost_initialize();

            //daha fazla göster butonu için onclick listener
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    fbPost_sonrakisayfa();
                }
            });

            //listview elementlerine onclick listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String post_id = ((TextView) view.findViewById(R.id.post_id)).getText().toString();
                    Intent Yorumlarshow = new Intent(iletiler.this, yorumlar.class);
                    Yorumlarshow.putExtra("post_id", post_id);
                    startActivity(Yorumlarshow);
                }
            });
        }else{
            //internet yoksa uyarı ver
            setContentView(R.layout.yorumlar_dialog);
        }
    }@Override

    //3 nokta menüsü ekranın sağ üst köşesinde.
     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //menü elementlerini seçim işlemi sonrası method
    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                hakkindaGoster();
                return true;
            case R.id.action_yucel:
                Intent intent = new Intent(this, yucel_hoca.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // hakkında bilgisi dialog
    public void hakkindaGoster() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Hazırlayan");
        builder1.setMessage("Sercan Küçükdemirci © 2015");
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

    //ilk açılış postlarını çeken fonksiyon
    public void fbpost_initialize() {

        Connection< Post > rPosts = facebookClient.fetchConnection("272161786181478/feed",
                Post.class,
                Parameter.with("limit", 25),
                Parameter.with("fields", "from,message,created_time,comments.summary(true)"));
        if (rPosts.hasNext()) {
            Uri nextpage = Uri.parse(rPosts.getNextPageUrl());
            this.until = nextpage.getQueryParameter("until");
        }
        for (int i = 0; i < rPosts.getData().size() - 1; i++) {
            Post post = rPosts.getData().get(i);

            items.add(new Items(post.getFrom().getName(), post.getMessage(),formatter.format(post.getCreatedTime()),Long.toString(post.getCommentsCount())+" Yorum",post.getId()));
        }
        //shared preferences ile en son yollanan postun tarihini bildirim için saklıyoruz
        SharedPreferences.Editor editor = getSharedPreferences("LAST_POST_DATE", MODE_PRIVATE).edit();
        formatterNM.setTimeZone(TimeZone.getTimeZone("GMT"));
        editor.putString("last_post", formatterNM.format(rPosts.getData().get(0).getCreatedTime()));
        editor.commit();

        //listview adaptörüne günceleme mesajı yolla, yeni eklenen bişeyler varsa aktar
        this.adapter.notifyDataSetChanged();

        //bildirim servisini başlat
        startService(new Intent(this, BWLWiService.class));

    }

    //daha fazla göster butonuna atanmış olan fonksiyon
    public void fbPost_sonrakisayfa() {
        Connection < Post > rPosts = facebookClient.fetchConnection("272161786181478/feed",
                Post.class,
                Parameter.with("limit", 25),
                Parameter.with("fields", "from,message,created_time,comments.summary(true)"),
                Parameter.with("until", this.until));
        if (rPosts.hasNext()) {
            Uri nextpage = Uri.parse(rPosts.getNextPageUrl());
            this.until = nextpage.getQueryParameter("until");
        }

        for (int i = 0; i < rPosts.getData().size() - 1; i++) {
            Post post = rPosts.getData().get(i);
            items.add(new Items(post.getFrom().getName(), post.getMessage(),formatter.format(post.getCreatedTime()),Long.toString(post.getCommentsCount())+" Yorum",post.getId()));
        }

        this.adapter.notifyDataSetChanged();
    }

    //cihaz internete bağlımı kontrol metodu
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}