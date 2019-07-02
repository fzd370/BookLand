package com.example.bookland;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    private Context mContext;
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0,books);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView= LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_bookadapter,parent,false);
        }
        Book currentbook=getItem(position);
        ImageView imageView= listItemView.findViewById(R.id.book_cover);
        if(!currentbook.getmImageUrl().toString().isEmpty())
        {
            Glide.with(mContext).load(currentbook.getmImageUrl()).into(imageView);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.no_connection).into(imageView);
        }
        TextView authorname = listItemView.findViewById(R.id.author);
        authorname.setText(currentbook.getmAuthor());
        TextView publisheddate= listItemView.findViewById(R.id.pub_date);
        publisheddate.setText(currentbook.getmPubDate());
        TextView learnmore=listItemView.findViewById(R.id.learn_more);
        learnmore.setText(Html.fromHtml("<a href=\""+currentbook.getmInfoLink()+"\">Learn More</a> "));
        learnmore.setMovementMethod(LinkMovementMethod.getInstance());

        return listItemView;
    }
}
