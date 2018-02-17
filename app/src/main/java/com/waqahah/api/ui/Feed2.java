package com.waqahah.api.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.waqahah.R;
import com.waqahah.api.model.Post;
import com.waqahah.api.model.Profile;
import com.waqahah.api.service.PostClient;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;

public class Feed2 extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {
    private static final String Tag = "MainFeed";
    private static String url;
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    Retrofit.Builder builder;
    Retrofit retrofit;
    PostClient apiService;

    SharedPreferences settings2 ;
    ListView lv ;
    ArrayList<Post> al;
    ArrayList<String> noInternet;
    Profile p;
    TextView userlink;
    ImageView share_prof;
    TextView userabout;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed2);
        url = this.getResources().getString(R.string.url);
        builder = new Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
        tablayout.getTabAt(0).setIcon(R.drawable.envelope);
        tablayout.getTabAt(1).setIcon(R.drawable.profile_icon);
        tablayout.getTabAt(2).setIcon(R.drawable.search_icon);
        settings2 = PreferenceManager.getDefaultSharedPreferences(context);
        String token = settings2.getString("token", "");

        postlist(token);
        getProfile(token);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        AdRequest adRequest2 = new AdRequest.Builder()
                .build();

        mAdView = (AdView) findViewById(R.id.adView);

        mAdView.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter));
        mInterstitialAd.loadAd(adRequest2);


    }

private void setupViewPager(ViewPager viewPager)
{
   SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
    adapter.addFragment( new tab1_fragment(), "");
    adapter.addFragment(new tab2_fragment(), "");
    adapter.addFragment(new tab3_fragment(), "");
    viewPager.setAdapter(adapter);
}

private void postlist(String token)
{

    Call<List<Post>> call = apiService.getPostList("Token "+token);
    Dialog progress_spinner;
    progress_spinner = LoadingSpinner.Spinner(this);
    progress_spinner.show();
    call.enqueue(new Callback<List<Post>>() {
        @Override
        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
            if (response.isSuccessful())
            {
                progress_spinner.dismiss();
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                lv = (ListView) findViewById(R.id.myList);
                al = new ArrayList<Post>();
                al.addAll(response.body());
               PostAdapter adapter = new PostAdapter(getApplicationContext(),R.layout.postrow,al);
                lv.setAdapter(adapter);
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
                swipeRefreshLayout.setOnRefreshListener(Feed2.this);
            }
            else
            {
                progress_spinner.dismiss();
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Feed2.this, "fail", Toast.LENGTH_SHORT).show();
                settings2.edit().remove("token").commit();
                settings2.edit().remove("username").commit();
                Intent i = new Intent(Feed2.this, MainActivity.class);
                startActivity(i);
            }
        }

        @Override
        public void onFailure(Call<List<Post>> call, Throwable t) {
            progress_spinner.dismiss();
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);
            lv = (ListView) findViewById(R.id.myList);
            noInternet = new ArrayList<String>();
            noInternet.add("Error loading, Check internet connection");
            PostAdapter_no adapter = new PostAdapter_no(getApplicationContext(),R.layout.no_internet,noInternet);
            lv.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(Feed2.this);
            Toast.makeText(Feed2.this, "error, check internet connection", Toast.LENGTH_SHORT).show();




        }
    });
}

    @Override
    public void onRefresh() {
        String token = settings2.getString("token", "");

        postlist(token);
        showInterstitial();
    }


    private void getProfile(String token)
    {
        url = this.getResources().getString(R.string.url);
        Dialog progress_spinner;
        progress_spinner = LoadingSpinner.Spinner(this);
        progress_spinner.show();
        Call<Profile> call = apiService.getProfile("Token "+token);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful())
                {
                    progress_spinner.dismiss();
                    p = response.body();
                            if (p.getAvatar() != null) {
                            Glide.with(context)
                                    .load(url + p.getAvatar())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into((ImageView) findViewById(R.id.userdp));
                        }
                        userlink = (TextView) findViewById(R.id.user_link);
                        share_prof = (ImageView) findViewById(R.id.prof_share);
                        userabout = (TextView) findViewById(R.id.user_about);
                        userlink.setText(Html.fromHtml("<a href="+url+ "/users/" + p.getUsername() + ">" + p.getUsername()));
                        userlink.setMovementMethod(LinkMovementMethod.getInstance());
                        userabout.setText(p.getAbout());
                        userabout.setTextColor(Color.parseColor("#000000"));
                        userlink.setVisibility(TextView.VISIBLE);
                        share_prof.setVisibility(TextView.VISIBLE);
                        userabout.setVisibility(TextView.VISIBLE);

                        share_prof.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, p.getUsername() + " On Waqahah");
                                i.putExtra(Intent.EXTRA_TEXT, url +"/users/" + p.getUsername());
                                startActivity(Intent.createChooser(i, "Share URL"));
                            }
                        });
                }
                else
                {
                    progress_spinner.dismiss();
                    Toast.makeText(Feed2.this, "Internal Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                progress_spinner.dismiss();
                userlink = (TextView) findViewById(R.id.user_link);
                share_prof = (ImageView) findViewById(R.id.prof_share);
                userabout = (TextView) findViewById(R.id.user_about);
                userlink.setVisibility(TextView.INVISIBLE);
                share_prof.setVisibility(TextView.INVISIBLE);
                userabout.setText("Failed to load profile");
                userabout.setTextColor(Color.parseColor("#FF0000"));
                Toast.makeText(Feed2.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    public class PostAdapter extends ArrayAdapter{
    private List<Post> posts;
    private int resource;
    private LayoutInflater inflater;
    public PostAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);
        posts = objects;
        this.resource = resource;
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)   ;
   }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView  == null)
        {
            convertView = inflater.inflate(resource, null);
        }
        TextView like_lbl;
        TextView like_txt;
        TextView dislike_lbl;
        TextView dislike_txt;
        TextView fp_lbl;
        TextView fp_txt;
        TextView cp_lbl;
        TextView cp_txt;
        TextView message_lbl;
        TextView message_txt;

        TextView post_date;
        ImageView post_delete_btn;
        ImageView post_share_btn;
        like_lbl = (TextView) convertView.findViewById(R.id.like_lbl);
        like_txt = (TextView) convertView.findViewById(R.id.like_txt);
        fp_lbl = (TextView) convertView.findViewById(R.id.fp_lbl);
        fp_txt = (TextView) convertView.findViewById(R.id.fp_txt);
        cp_lbl = (TextView) convertView.findViewById(R.id.cp_lbl);
        cp_txt = (TextView) convertView.findViewById(R.id.cp_txt);
        dislike_lbl = (TextView) convertView.findViewById(R.id.dislike_lbl);
        dislike_txt = (TextView) convertView.findViewById(R.id.dislike_txt);
        message_lbl = (TextView) convertView.findViewById(R.id.message_lbl);
        message_txt = (TextView) convertView.findViewById(R.id.message_txt);


        post_date = (TextView) convertView.findViewById(R.id.post_date);
        post_delete_btn = (ImageView) convertView.findViewById(R.id.post_delete_btn);
        post_share_btn = (ImageView) convertView.findViewById(R.id.post_share_btn);
        like_lbl.setText("Like");
        dislike_lbl.setText("Waqahah");
        fp_lbl.setText("First Impression");
        cp_lbl.setText("Current Impression");
        message_lbl.setText("Message");

        like_txt.setText(posts.get(position).getLike());
        dislike_txt.setText(posts.get(position).getDislike());
        fp_txt.setText(posts.get(position).getFirstImpression());
        cp_txt.setText(posts.get(position).getCurrentImpression());
        message_txt.setText(posts.get(position).getMessage());
        TextView[] tar = {like_txt, like_lbl,dislike_txt,dislike_lbl,fp_txt,fp_lbl,cp_txt,cp_lbl,message_txt,message_lbl};
        for (int i=0;i<10;i++)
        {
            if (tar[i].getText() == "") {
                tar[i].setVisibility(GONE);
                tar[i + 1].setVisibility(GONE);
            }
        }


        try {
            post_date.setText(posts.get(position).getDateCreated());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        post_share_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, p.getUsername() + " On Waqahah");
                i.putExtra(Intent.EXTRA_TEXT, url  +"/posts/" + posts.get(position).getPk());
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });

        post_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Feed2.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Delete this message?");
                builder.setPositiveButton(" `Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String token = settings2.getString("token", "");
                        Call<ResponseBody> call = apiService.deletePost("Token " + token, posts.get(position).getPk());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    postlist(token);
                                }
                                else {
                                    Toast.makeText(Feed2.this, "Internal Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(Feed2.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return convertView;
    }
}

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public class PostAdapter_no extends ArrayAdapter{
        private List<String> posts;
        private int resource;
        private LayoutInflater inflater;
        public PostAdapter_no(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            posts = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)   ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView  == null)
            {
                convertView = inflater.inflate(resource, null);
            }
            TextView post_content;


            post_content = (TextView) convertView.findViewById(R.id.no_internet_txt);
            post_content.setTextColor(Color.parseColor("#FF0000"));
            post_content.setText(posts.get(position));

            return convertView;
        }
    }

}
