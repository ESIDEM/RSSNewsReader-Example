package com.example.esidemjnr.techdeponews;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esidemjnr.techdeponews.handlers.DOMParser;
import com.example.esidemjnr.techdeponews.handlers.NavDrawerItem;
import com.example.esidemjnr.techdeponews.handlers.RSSFeed;
import com.example.esidemjnr.techdeponews.handlers.RSSItem;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import static com.example.esidemjnr.techdeponews.R.layout.items;

public class MainActivity extends AppCompatActivity implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener{

    public static MainActivity mInstance;
    //feed
    RSSFeed fFeed;
    RSSItem feedItem;
    String imageLink;
    String imageLink2;
    String feedTitle;
    String feedDate;
    String feedUrl;

    //Home ListView
    ListView list;
    CustomListAdapter adapter;
    ListView listfeed;
    String feedcustom;
    String feedcustom2;

    //Others
    SwipeRefreshLayout swiperefresh;

    //Connectivity manager
    ConnectivityManager cM;
    private int selectedPosition;

    //navigation drawer

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter navDrawerListAdapter;
    public TextView lfflTitle;
    public TextView urlText;


// Drawer end

    //the default feed
    String feedURL = SplashActivity.default_feed_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        setContentView(R.layout.activity_main);

        setUpDrawerProcess(savedInstanceState);


       // initActionBar();

       final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //set the toolbar
        setSupportActionBar(toolbar);

        //set the toolbar's title
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

         //   mItems = getResources().getStringArray(R.array.menus);
        //initialize the feeds items
        fFeed = (RSSFeed) getIntent().getExtras().get("feed");

        //initialize connectivity manager
        cM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Create Navigation drawer and inlfate layout
       // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //initialize the Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       // mDrawerList = (ListView) findViewById(R.id.drawer_list);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name) {

            //Called when the drawer is opened
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);

                //change toolbar title to 'Add a feed'
                toolbar.setTitle(getResources().getString(R.string.categority));


            }

            //Called when the drawer is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //change toolbar title to the app's name
                toolbar.setTitle(getResources().getString(R.string.app_name));


            }

        };
        // Set behavior of Navigation drawer
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    // This method will trigger on item Click of navigation menu
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        // Set item in checked state
//                        menuItem.setChecked(true);
//
//                        // TODO: handle navigation
//
//                        // Closing drawer on item click
//                        mDrawerLayout.closeDrawers();
//                        return true;
//                    }
//                });


        //this handle the hamburger animation
      //  mDrawerLayout.addDrawerListener(mDrawerToggle);

        //initialize swipe to refresh layout
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        //set on refresh listener
        swiperefresh.setOnRefreshListener(this);

        //set the default color of the arrow
        swiperefresh.setColorSchemeResources(R.color.colorAccent);

        //initialize the main ListView where items will be added
        list = (ListView) findViewById(android.R.id.list);

        //set the main ListView custom adapter
        adapter = new CustomListAdapter(this);

        list.setAdapter(adapter);

        //handle main ListView clicks
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                                        long arg3) {
                                                    int pos = arg2;

                                                    Intent detailIntent = new Intent(MainActivity.this, NewsDetails.class);
                                                    detailIntent.putExtra("title",lfflTitle.getText().toString() );
                                                    detailIntent.putExtra("url", urlText.getText().toString());

                                                   startActivity(detailIntent);
                                                }
                                            }
        );

    }

    public static MainActivity getInstance() {
        return mInstance;
    }

    @SuppressWarnings("ResourceType")
    private void setUpDrawerProcess(Bundle savedInstanceState) {
        //Drawer
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        // puan durumu
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
        // fikstür
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
        // canliskor
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
        // sporx
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));

        // sporx
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        // Setting item click listener to mDrawerList
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


        // setting the nav drawer list adapter
        navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(navDrawerListAdapter);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }



    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            openNewFeed(position);
        }
    }


    //this is the method to open a new feed rss on new Thread
    public void openNewFeed(int position) {
   //  final String datfeed = null;
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        setTitle(navMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        //show swipe refresh
        swiperefresh.setRefreshing(true);

        //detect if there's a connection issue or not: if there's a connection problem stop refreshing and show message
        if (cM.getActiveNetworkInfo() == null) {
            Toast toast = Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT);
            toast.show();
            swiperefresh.setRefreshing(false);

        } else {
            switch (position) {

                case 0: feedURL =  "http://saharareporters.com/feeds/latest/feed";
                    break;
                case 1:
                    feedURL = "http://saharareporters.com/feeds/sports/feed";
                    break;
                case 2:
                    feedURL = "http://saharareporters.com/feeds/reports/feed";
                    break;
                case 3:
                    feedURL = "http://saharareporters.com/feeds/opinion/feed";
                    break;
                case 4:
                    feedURL = "http://saharareporters.com/feeds/politics/feed";
                    break;
                case 5:
                    feedURL = "http://saharareporters.com/feeds/business/feed";
                    break;
                case 6:
                    feedURL = "http://saharareporters.com/feeds/entertainment/feed";
                    break;
                case 7:
                    feedURL = "http://saharareporters.com/feeds/lifestyle/feed";
                    break;
                case 8:
                    feedURL = "http://saharareporters.com/feeds/technology/feed";
                    break;
                case 9:
                    feedURL = "http://saharareporters.com/feeds/vidoes/feed";
                    break;
                case 10:
                    feedURL = "http://saharareporters.com/feeds/photos/feed";
                    break;
                case 11:
                    feedURL = "http://saharareporters.com/feeds/documents/feed";
                    break;
                default:
                    break;
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DOMParser tmpDOMParser = new DOMParser();
                    fFeed = tmpDOMParser.parseXml(feedURL);
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (fFeed != null && fFeed.getItemCount() > 0) {

                                adapter.notifyDataSetChanged();

                                //close swipe refresh
                                swiperefresh.setRefreshing(false);

                                //set feedURL calling setFeedString method, it is important if we want working swipe refresh listener
                                //setFeedString(datfeed);
                              //  HomeUtils.setFeedString(datfeed);

                            }
                        }
                    });
                }
            });
            thread.start();
        }
    }

    //this is the method to refresh the feed items and the list view
    //the xml is parsed again and if the number of the items is >0
    //new items will be added on top of the list activity's ListView
    public void onRefresh() {

        //detect if there's a connection issue or not: if there's a connection problem stop refreshing and show message
        if (cM.getActiveNetworkInfo() == null) {
            Toast toast = Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT);
            toast.show();
            swiperefresh.setRefreshing(false);

        } else {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DOMParser tmpDOMParser = new DOMParser();
                    fFeed = tmpDOMParser.parseXml(feedURL);
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (fFeed != null && fFeed.getItemCount() > 0) {
                                adapter.notifyDataSetChanged();
                                swiperefresh.setRefreshing(false);
                            }
                        }
                    });
                }
            });
            thread.start();
        }
    }

    //this is the custom list adapter for the home ListView
    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        CustomListAdapter(MainActivity activity) {

            //initialize layout inflater
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //get items count
        @Override
        public int getCount() {
            return fFeed.getItemCount();
        }

        //get items position
        @Override
        public Object getItem(int position) {
            return position;
        }

        //get items id at selected position
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            Activity activity = MainActivity.this;
            View listItem = convertView;

            //initialize feedItem
            feedItem = fFeed.getItem(pos);

            //get the feed content
            imageLink = feedItem.getImage();
            imageLink2 = feedItem.getImage2();
            feedTitle = feedItem.getTitle();
            feedDate = feedItem.getDate();
            feedUrl = feedItem.getLink();


            if (listItem == null) {

                //set the main ListView's layout
                listItem = layoutInflater.inflate(items, parent, false);
            }

            //get the chosen items text size from preferences
         //   float size = Preferences.resolveTextSizeListResId(getBaseContext());

            //initialize the dynamic items (the title, subtitle)
            lfflTitle = (TextView) listItem.findViewById(R.id.title);
            TextView pubDate = (TextView) listItem.findViewById(R.id.date);
            urlText = (TextView) listItem.findViewById(R.id.feed_url);

            //dynamically set title and subtitle according to the feed data

            //title
            lfflTitle.setText(feedTitle);

            urlText.setText(feedUrl);

            //subtitle= publication date
            pubDate.setText(feedDate);

            //set the list items text size from preferences in SP unit
            //lfflTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

          //  pubDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, size - 2);

            //initialize the ImageView
            ImageView lfflImage = (ImageView) listItem.findViewById(R.id.thumb);


            //else, load the image
            //if getImage() method fails (i.e when img is in content:encoded) load image2
            if (imageLink.isEmpty()) {

                //use glide to load the image into the ImageView (lfflimage)
                Glide.with(activity).load(imageLink2)

                        //load images as bitmaps to get fixed dimensions
                        .asBitmap()

                        //set a placeholder image
                        .placeholder(R.mipmap.ic_launcher)

                        //disable cache to avoid garbage collection that may produce crashes
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(lfflImage);

                //else use image
            } else {

                Glide.with(activity).load(imageLink)

                        //load images as bitmaps to get fixed dimensions
                        .asBitmap()

                        //set a placeholder image
                        .placeholder(R.mipmap.ic_launcher)

                        //disable cache to avoid garbage collection that may produce crashes
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(lfflImage);
            }

            return listItem;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);


    }
}
