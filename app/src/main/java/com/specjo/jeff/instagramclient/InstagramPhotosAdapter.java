package com.specjo.jeff.instagramclient;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jemartinez on 10/25/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        drawImage(photo, convertView);
        drawFooter(photo, convertView);

        return convertView;
    }

    private String getUsername(InstagramPhoto photo) {
        String username = "";
        if (photo.username != null) {
            username = photo.username;
        }

        return username;
    }

    private String getCaption(InstagramPhoto photo) {
        String caption = "";
        if (photo.caption != null) {
            caption = photo.caption;
        }

        return caption;
    }

    private void drawFooter(InstagramPhoto photo, View convertView) {
        String username = getUsername(photo);
        String caption = getCaption(photo);

        StringBuilder footer = new StringBuilder();

        footer.append(username);

        if (caption != null) {
            footer.append(": ");
            footer.append(caption);
        }

        TextView tvFooter = (TextView) convertView.findViewById(R.id.tvFooter);
        tvFooter.setText(footer);

        if (photo.expandedCaption) {
            tvFooter.setMaxLines(Integer.MAX_VALUE);
            tvFooter.setEllipsize(null);
        } else {
            tvFooter.setMaxLines(PhotosActivity.DEFAULT_CAPTION_LINES_TO_SHOW);
            tvFooter.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    private void drawImage(InstagramPhoto photo, View convertView) {
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(photo.imageURL).into(ivPhoto);
    }
}
