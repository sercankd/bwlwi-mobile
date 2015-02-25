package marmara.facebook.bwlwi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ListView;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Comment;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class yorumlar extends ActionBarActivity {
    ListView listView2;
    public static Version VERSION_2_2;
    public String accessToken = "FACEBOOKTAN ACCESS TOKEN AL";
    public FacebookClient facebookClient = new DefaultFacebookClient(accessToken, VERSION_2_2);
    public MyAdapter adapter;
    private ArrayList< Items > items;
    public Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorumlar);

        Intent myIntent = getIntent();
        String post_id = myIntent.getStringExtra("post_id");
        listView2 = (ListView) findViewById(R.id.listView2);
        items = new ArrayList < Items > ();
        adapter = new MyAdapter(this, items);
        listView2.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        fb_comments(post_id);
    }
    public void fb_comments(String post_id) {

        Connection<Comment> rPosts = facebookClient.fetchConnection(post_id+"/comments",
                Comment.class,
                Parameter.with("limit", 250));
        for (int i = 0; i < rPosts.getData().size(); i++) {
            Comment post = rPosts.getData().get(i);
            items.add(new Items(post.getFrom().getName(), post.getMessage(),formatter.format(post.getCreatedTime()),"",""));
        }
        this.adapter.notifyDataSetChanged();

    }
}
