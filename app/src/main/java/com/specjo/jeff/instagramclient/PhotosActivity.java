package com.specjo.jeff.instagramclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "82f6972779074bdba988718be0ce65b4";

    public static final String POPULAR_MEDIA_ENDPOINT = "https://api.instagram.com/v1/media/popular";

    public static final int DEFAULT_CAPTION_LINES_TO_SHOW = 3;

    ListView lvPhotos;

    private List<InstagramPhoto> photos = new ArrayList<>();
    private InstagramPhotosAdapter aPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        aPhotos = new InstagramPhotosAdapter(this, photos);
        lvPhotos.setAdapter(aPhotos);

        fetchPopularPhotos();
    }

    // Trigger API request
    public void fetchPopularPhotos() {
        final String popularMediaUrl = POPULAR_MEDIA_ENDPOINT + "?client_id=" + CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = null;

        client.get(popularMediaUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data");

                    for (int i = 0 ; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        boolean isImage = (photoJSON.getString("type").equals("image"));

                        if (!isImage) {
                            continue;
                        }

                        InstagramPhoto photo = new InstagramPhoto();

                        if (!photoJSON.isNull("user")) {
                            photo.username = photoJSON.getJSONObject("user").getString("username");
                            photo.profilePictureURL = photoJSON.getJSONObject("user").getString("profile_picture");
                        }

                        if (!photoJSON.isNull("caption")) {
                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        }

                        if (!photoJSON.isNull("images")) {
                            photo.imageURL = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                            photo.imageWidth = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");
                            photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        }

                        if (!photoJSON.isNull("likes")) {
                            photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        }

                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("DEBUG", responseString);
                Toast.makeText(getApplicationContext(), "Couldn't get new photos :(", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onCaptionClick(View view) {
        TextView footer = (TextView) view;

        Integer photoPosition = lvPhotos.getPositionForView(view);
        InstagramPhoto photo = photos.get(photoPosition);

        if (photo.expandedCaption) {
            footer.setMaxLines(DEFAULT_CAPTION_LINES_TO_SHOW);
            footer.setEllipsize(TextUtils.TruncateAt.END);

            photo.expandedCaption = false;
        } else {
            footer.setMaxLines(Integer.MAX_VALUE);
            footer.setEllipsize(null);

            photo.expandedCaption = true;
        }
    }
}
