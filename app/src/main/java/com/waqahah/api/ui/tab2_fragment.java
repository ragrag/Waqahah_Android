package com.waqahah.api.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.waqahah.R;
import com.waqahah.api.model.Profile;
import com.waqahah.api.service.PostClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class tab2_fragment extends Fragment {
    private static final String TAB = "Tab 2";
    private static String url;
    Retrofit.Builder builder ;
    Retrofit retrofit ;
    PostClient apiService;
    String username;
    SharedPreferences settings2 ;
    TextView userlink ;
    ImageView  share_prof;
    TextView userabout ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);
        url = getActivity().getResources().getString(R.string.url);
         builder = new Retrofit.Builder()
                .baseUrl(url + "/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);



        settings2 = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        username = settings2.getString("username", "");



        TextView logout = (TextView) view.findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.app_name);
                builder.setMessage("Logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        settings2.edit().remove("token").commit();
                        settings2.edit().remove("username").commit();
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(i);

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


        ImageView refresh_btn = (ImageView) view.findViewById(R.id.profile_refresh_btn);

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


        url = getActivity().getResources().getString(R.string.url);
        String token = settings2.getString("token", "");
        Dialog progress_spinner;
        progress_spinner = LoadingSpinner.Spinner(getActivity());
        progress_spinner.show();
        Call<Profile> call = apiService.getProfile("Token "+token);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful())
                {
                    progress_spinner.dismiss();
                  Profile  p = response.body();


                        if (p.getAvatar() != null) {
                            Glide.with(getActivity())
                                    .load(url + p.getAvatar())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into((ImageView) view.findViewById(R.id.userdp));
                        }


                        TextView userlink = (TextView) view.findViewById(R.id.user_link);
                        ImageView share_prof = (ImageView) view.findViewById(R.id.prof_share);
                        TextView userabout = (TextView) view.findViewById(R.id.user_about);
                        userabout.setTextColor(Color.parseColor("#000000"));
                        userlink.setText(Html.fromHtml("<a href="+ url +"/users/" + username + ">" + username));
                        userlink.setMovementMethod(LinkMovementMethod.getInstance());
                        userabout.setText(p.getAbout());
                        userlink.setVisibility(TextView.VISIBLE);
                        share_prof.setVisibility(TextView.VISIBLE);
                        userabout.setVisibility(TextView.VISIBLE);

                        share_prof.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, username + " On Waqahah");
                                i.putExtra(Intent.EXTRA_TEXT, url + "/users/" + username);
                                startActivity(Intent.createChooser(i, "Share URL"));
                            }
                        });
                }
                else
                {
                    progress_spinner.dismiss();
                    Toast.makeText(getActivity(), "Internal Error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                progress_spinner.dismiss();
                userlink = (TextView) view.findViewById(R.id.user_link);
                share_prof = (ImageView) view.findViewById(R.id.prof_share);
                userabout = (TextView) view.findViewById(R.id.user_about);
                userlink.setVisibility(TextView.INVISIBLE);
                share_prof.setVisibility(TextView.INVISIBLE);
                userabout.setText("Failed to load profile");
                userabout.setTextColor(Color.parseColor("#FF0000"));
                Toast.makeText(getActivity(), "error, check internet connection", Toast.LENGTH_SHORT).show();

            }
        });
    }});

        return view;
        }


}
