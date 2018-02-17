package com.waqahah.api.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.waqahah.R;
import com.waqahah.api.model.Post;
import com.waqahah.api.model.Profile;
import com.waqahah.api.service.PostClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class User_Profile extends AppCompatActivity {
    private static String url;
    Retrofit.Builder builder ;
    Retrofit retrofit;
    PostClient apiService ;
    SharedPreferences settings2 ;
    Profile profile;
    Bundle bundle ;
    String username;
    TextView user_link;
    TextView user_about;
    ImageView user_dp;
    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });


        setContentView(R.layout.activity_user__profile);
        url = this.getResources().getString(R.string.url);
        builder = new Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        bundle = getIntent().getExtras();
        username = bundle.getString("username");
        user_link = (TextView) findViewById(R.id.user_link_other);
        user_about = (TextView) findViewById(R.id.user_about_other);
        user_dp = (ImageView) findViewById(R.id.userdp_other);
        if (profile.getAvatar() != null) {
            Glide.with(this)
                    .load(url + profile.getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into((ImageView) findViewById(R.id.userdp_other));
        }
        user_link.setText(Html.fromHtml("<a href="+ url + "/users/" + username + ">" + username));
        user_link.setMovementMethod(LinkMovementMethod.getInstance());

        if (profile.getAbout() != null) {

            user_about = (TextView) findViewById(R.id.user_about_other);
            user_about.setText(profile.getAbout());
        }
        TextView post_btn = (TextView) findViewById(R.id.post_send_btn);
        post_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
              makepost();
            }
        });
    }

public void makepost()
    {
        settings2 = PreferenceManager
                .getDefaultSharedPreferences(User_Profile.this);
        String token = settings2.getString("token", "");
        Post post = new Post();

        post.setLike(((TextView) findViewById(R.id.post_like_txt)).getText().toString());
        post.setDislike(((TextView) findViewById(R.id.post_dislike_txt)).getText().toString());
        post.setFirstImpression(((TextView) findViewById(R.id.post_first_impression_txt)).getText().toString());
        post.setCurrentImpression(((TextView) findViewById(R.id.post_current_impression_txt)).getText().toString());
        post.setMessage(((TextView) findViewById(R.id.post_message_txt)).getText().toString());

        Call<ResponseBody> call = apiService.makePost("Token "+token,username,post);
        Dialog progress_spinner;
        progress_spinner = LoadingSpinner.Spinner(this);
        progress_spinner.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                {
                    progress_spinner.dismiss();
                    Toast.makeText(User_Profile.this, "Post Created", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(User_Profile.this, Feed2.class);
                    startActivity(i);

                }
                else
                {
                    progress_spinner.dismiss();
                    Toast.makeText(User_Profile.this, "Internal Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress_spinner.dismiss();
                Toast.makeText(User_Profile.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(User_Profile.this, Feed2.class);
        startActivity(i);
    }

}
