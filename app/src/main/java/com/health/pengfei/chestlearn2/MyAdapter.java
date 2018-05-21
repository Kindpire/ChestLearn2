package com.health.pengfei.chestlearn2;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;


    public MyAdapter(Context context,ArrayList<HashMap<String, Object>> data) {

        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * get count of data
     */
    public int getCount() {
        return data.size();
    }
    /**
     * get data object
     */
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     * get id
     */
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        Row row = null;
        if(convertView==null){
            row = new Row();
            // generate layout
            convertView = layoutInflater.inflate(R.layout.history, null);
            row.imageView = (ImageView) convertView.findViewById(R.id.image);
            row.titleView = (TextView) convertView.findViewById(R.id.title);
            row.infoView = (TextView) convertView.findViewById(R.id.info);
            row.button = (Button) convertView.findViewById(R.id.view_btn);
            // store in tag
            convertView.setTag(row);
        }
        else {
            row = (Row) convertView.getTag();
        }
        // binding data
        row.imageView.setBackgroundResource((Integer) data.get(position).get("image"));
        row.titleView.setText((String)data.get(position).get("name"));
        row.infoView.setText("1st:" + String.valueOf(data.get(position).get("first picture")) + " " +
                "2nd:" + String.valueOf(data.get(position).get("second picture")) + " " +
                "3rd:" + String.valueOf(data.get(position).get("third picture")));
        row.button.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                //TODO
                // Start new activity for photo review and retake
            }

        });
        return convertView;
    }

}
