package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private static final String TAG = "HomeActivity";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        //eklendi
        Log.d(TAG, "onCreate: started.");

        mNames.add("Ali Veli");
        mNames.add("Ahmet Dd");
        mNames.add("Ahmet Dd");
        mNames.add("Ahmet Dd");
        mNames.add("Ahmet Dd");
        mNames.add("Ahmet Dd");
        mNames.add("XXx");
        mNames.add("Ahmet Dd");

        //initImageBitmaps();  asagiya resim ekleyince bunu yorumdan cikar.
        initRecycleView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    //eklendi
    private void initRecycleView(){
        Log.d(TAG, "imageRecycleView: init recycleview");
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        RecycleViewAdapter adapter = new RecycleViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}