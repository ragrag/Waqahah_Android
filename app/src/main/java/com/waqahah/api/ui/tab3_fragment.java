package com.waqahah.api.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.waqahah.R;
import com.waqahah.api.model.Profile;
import com.waqahah.api.service.PostClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class tab3_fragment extends Fragment {
    private static final String TAB = "Tab 3";
    private static String url;
    EditText username_srch;
    Intent i;
    Retrofit.Builder builder ;
    Retrofit retrofit ;
    Profile p;
    PostClient apiService ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container, false);
        url = getActivity().getResources().getString(R.string.url);

        builder = new Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);

        TextView sb = (TextView) view.findViewById(R.id.search_btnn);
        sb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Dialog progress_spinner;
                progress_spinner = LoadingSpinner.Spinner(getActivity());
                progress_spinner.show();
                username_srch = (EditText) view.findViewById(R.id.search_txt);
                String username_srchstr = username_srch.getText().toString();
                Call<Profile> call = apiService.searchProfile(username_srchstr);
                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        if (response.isSuccessful())
                        {
                            progress_spinner.dismiss();
                            p = response.body();
                            i = new Intent(getActivity(), User_Profile.class);
                            i.putExtra("profile", p);
                            i.putExtra("username", username_srchstr);
                            getActivity().startActivity(i) ;
                        }
                        else
                        {
                            progress_spinner.dismiss();
                            Toast.makeText(getActivity(), "User "+username_srchstr+" Not Found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        progress_spinner.dismiss();
                        Toast.makeText(getActivity(), "Internal Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return view;

    }


}
