package marmara.facebook.bwlwi;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<Items> {

    private final Context context;
    private final ArrayList<Items> itemsArrayList;

    public MyAdapter(Context context, ArrayList<Items> itemsArrayList) {

        super(context, R.layout.row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row, parent, false);

        TextView labelView = (TextView) rowView.findViewById(R.id.label);
        TextView valueView = (TextView) rowView.findViewById(R.id.value);
        TextView tarihView = (TextView) rowView.findViewById(R.id.tarihtext);
        TextView comments = (TextView) rowView.findViewById(R.id.comments);
        TextView post_id = (TextView) rowView.findViewById(R.id.post_id);

        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());
        tarihView.setText(itemsArrayList.get(position).getTarih());
        comments.setText(itemsArrayList.get(position).getYorumsay());
        post_id.setText(itemsArrayList.get(position).getPostID());

        return rowView;
    }
}
