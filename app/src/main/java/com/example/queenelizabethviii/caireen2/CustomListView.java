package com.example.queenelizabethviii.caireen2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomListView extends ArrayAdapter<String> {

    private String[] title;
    private String[] link;
    private String[] description;
    private Activity context;

    public CustomListView(Activity context, String[] title, String[] link, String[] description) {
        super(context, R.layout.articles_age,title);

        this.context=context;
        this.title= title;
        this.link = link;
        this.description = description;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r==null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.articles_age, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.tvw1.setText(title[position]);
        viewHolder.tvw2.setText(link[position]);
        viewHolder.tvw3.setText(description[position]);
        return r;

      //  return super.getView(position, convertView, parent);
    }

    class ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;

        ViewHolder(View v){
            tvw1 = (TextView) v.findViewById(R.id.article_name);
            tvw2 = (TextView) v.findViewById(R.id.link);
            tvw3 = (TextView) v.findViewById(R.id.description);
        }
    }
}
