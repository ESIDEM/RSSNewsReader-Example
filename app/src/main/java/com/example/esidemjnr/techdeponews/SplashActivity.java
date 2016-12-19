package com.example.esidemjnr.techdeponews;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.esidemjnr.techdeponews.handlers.DOMParser;
import com.example.esidemjnr.techdeponews.handlers.RSSFeed;

public class SplashActivity extends AppCompatActivity {

    //the default feed
    public static String default_feed_value = "http://saharareporters.com/feeds/latest/feed";

    //the items
    RSSFeed lfflfeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new AsyncLoadXMLFeed().execute();
    }

    //using intents we send the lfflfeed (the parsed xml to populate the listview)
    // from the async task to listactivity
    private void startMainActivity(RSSFeed lfflfeed) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", lfflfeed);
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    //parse the xml in an async task (background thread)
    private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            DOMParser Do = new DOMParser();
            lfflfeed = Do.parseXml(default_feed_value);

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startMainActivity(lfflfeed);
        }

    }
}
