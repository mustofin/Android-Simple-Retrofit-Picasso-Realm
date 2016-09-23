package com.mustofin.jsonlistview.view.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mustofin.jsonlistview.BaseAPI;
import com.mustofin.jsonlistview.R;
import com.mustofin.jsonlistview.model.ModelPhoto;
import com.mustofin.jsonlistview.view.adapter.MainAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    BaseAPI baseAPI;
    ListView listView;
    ProgressBar progressbar;
    SwipeRefreshLayout refresh;
    TextView not_found;


    RealmConfiguration realmConfiguration;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseAPI = BaseAPI.Factory.create();
        realmConfiguration = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfiguration);
        assignUIElements();
        assignUIEvent();

//        CEK DATA LOKAL
        RealmResults<ModelPhoto> photos = realm.where(ModelPhoto.class).findAll();
        if (photos.size() <= 0){
            System.out.println(">> TIDAK DITEMUKAN DATA");
            loadPhoto();
        }else{
            System.out.println(">> DITEMUKAN DATA = "+photos.size());
            MainAdapter adapter = new MainAdapter(MainActivity.this, photos);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            not_found.setVisibility(View.GONE);
        }
    }

    private void assignUIElements(){
        listView = (ListView) findViewById(R.id.listview);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        not_found = (TextView) findViewById(R.id.not_found);
    }

    private void assignUIEvent(){
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPhoto();
            }
        });

        not_found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPhoto();
            }
        });
    }

    private void loadPhoto(){
        not_found.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        showProgress();
        Call<List<ModelPhoto>> photoList = baseAPI.getPhotoList();
        photoList.enqueue(new Callback<List<ModelPhoto>>() {
            @Override
            public void onResponse(Call<List<ModelPhoto>> call, Response<List<ModelPhoto>> response) {
                MainAdapter adapter = new MainAdapter(MainActivity.this, response.body());
                listView.setAdapter(adapter);
                closeProgress();

//                SAVE LOCAL
                for (int i = 0; i < response.body().size(); i++) {
                    realm.beginTransaction();
                    ModelPhoto modelPhoto = realm.createObject(ModelPhoto.class);
                    modelPhoto.nama = response.body().get(i).nama;
                    modelPhoto.foto = response.body().get(i).foto;
                    realm.commitTransaction();
                }

                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<ModelPhoto>> call, Throwable t) {
                System.out.println(">> GAGAL LOAD PHOTO");
                System.out.println(">> "+t.getMessage());
                closeProgress();

                not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showProgress(){
        if (!refresh.isRefreshing()){
            progressbar.setVisibility(View.VISIBLE);
        }
    }

    private void closeProgress(){
        progressbar.setVisibility(View.GONE);
        refresh.setRefreshing(false);
    }
}
