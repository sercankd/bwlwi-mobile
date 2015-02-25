package marmara.facebook.bwlwi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class BWLWiService extends Service {
    public static Version VERSION_2_2;
    public String accessToken = "FACEBOOKTAN ACCESS TOKEN AL";
    public FacebookClient facebookClient = new DefaultFacebookClient(accessToken, VERSION_2_2);
    Handler handler;
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                super.handleMessage(msg);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                SharedPreferences prefs = getSharedPreferences("LAST_POST_DATE", MODE_PRIVATE);
                SharedPreferences.Editor editor = getSharedPreferences("LAST_POST_DATE", MODE_PRIVATE).edit();
                String last_posttarih = prefs.getString("last_post", null);
                Connection< Post > rPosts = facebookClient.fetchConnection("272161786181478/feed",
                        Post.class,
                        Parameter.with("fields", "from,message"),
                        Parameter.with("since", last_posttarih));

                int total_sayi = rPosts.getData().size();
                    if (total_sayi > 0) {
                        Pushgonder();
                        editor.putString("last_post", dateFormat.format(date));
                        editor.commit();
                }
            }

        };
        new Thread(new Runnable(){
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
        return START_STICKY;
    }

    public void Pushgonder(){
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("ticker")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Bwl-Wi")
                .setContentText("Yeni birşeyler var");
        Notification n = builder.getNotification();
        nm.notify(1554, n);
    }
}
