package com.newpage.hack2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WaysActivity extends AppCompatActivity {
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class MyCardView extends CardView {
        public int id;
        MyCardView(Context context, int id) {
            super(context);
            this.id = id;
        }
    }

    protected ArrayList<MyCardView> cardViews = new ArrayList<>();

    class CheckRoutesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            MediaType type = MediaType.parse("application/x-www-form-urlencoded");
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create("key=" + strings[0], type);
            Request request = new Request.Builder().url("https://newpage.ddns.net/tangle/api/way.php")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String ans = response.body().string();
                if (ans.equals("false")) {
                    return null;
                } else {
                    JSONObject array = new JSONObject(ans);
                    return array;
                }
            } catch (IOException ex) {
                Log.e("ea", "ead");
            } catch (JSONException jex) {
                Log.e("es", "eas");
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject array) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 20, 20, 20);
            LinearLayout layout = WaysActivity.this.findViewById(R.id.layout);

            LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            for (int i = 1; i <= array.length(); i++) {
                MyCardView cardView = new MyCardView(WaysActivity.this, i);
                cardViews.add(cardView);
                cardView.setLayoutParams(layoutParams);

                LinearLayout innerLayout = new LinearLayout(WaysActivity.this);
                innerLayout.setLayoutParams(innerParams);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout innerInnerLayout = new LinearLayout(WaysActivity.this);
                innerInnerLayout.setOrientation(LinearLayout.VERTICAL);


                ImageView imageView = new ImageView(WaysActivity.this);
                try {
                    new DownloadImageTask(imageView).execute(
                            (array.getJSONObject(String.valueOf(i)).getString("img_src"))
                    );
                } catch (JSONException jex) {
                    jex.printStackTrace();
                }

                cardView.setCardElevation(8f);
                TextView label = new TextView(WaysActivity.this);
                TextView rate = new TextView(WaysActivity.this);

                innerInnerLayout.addView(label);
                innerInnerLayout.addView(rate);
                innerInnerLayout.addView(imageView);
                innerLayout.addView(innerInnerLayout);
                //innerLayout.addView(imageView);
                cardView.addView(innerLayout);

                layout.addView(cardView);

                int finalI = i;
                cardView.setOnClickListener(v -> {
                    WaysActivity.this.startActivity(new Intent(
                            WaysActivity.this, MainActivity.class
                    ).putExtra("id", finalI));
                });

                try {
                    label.setText(array.getJSONObject(String.valueOf(i)).getString("title"));
                    rate.setText(array.getJSONObject(String.valueOf(i)).getString("rate"));
                } catch (JSONException jex) {
                    Log.e("ea", "Ea");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ways);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new CheckRoutesTask().execute("pop");
    }
}
