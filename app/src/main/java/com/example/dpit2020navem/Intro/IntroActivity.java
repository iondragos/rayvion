package com.example.dpit2020navem.Intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.dpit2020navem.HomePage.MainActivity;
import com.example.dpit2020navem.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    List<IntroScreenItem> introScreenItems;
    private ViewPager introScreenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    int position = 0;
    Button buttonGetStarted;
    Button buttonSkip;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        setUpIntroScreenItemsList();
        setUpIntroScreenPager();
        setUpIntroScreenButtons();

        autoRefreshActivity();

    }

    private void setUpIntroScreenItemsList(){
        introScreenItems = new ArrayList<>();
        introScreenItems.add(new IntroScreenItem("1",R.drawable.slide_one));
        introScreenItems.add(new IntroScreenItem("2",R.drawable.slide_two));
        introScreenItems.add(new IntroScreenItem("3",R.drawable.slide_three));
        introScreenItems.add(new IntroScreenItem("4",R.drawable.slide_four));
    }

    private void setUpIntroScreenPager(){
        introScreenPager = findViewById(R.id.introScreenPager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, introScreenItems);
        //final int pageMargin = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 8, getResources() .getDisplayMetrics());
        //introScreenPager.setPageMargin(pageMargin);
        //introScreenPager.setPageMargin(-50);
        introScreenPager.setAdapter(introViewPagerAdapter);


        tabIndicator = findViewById(R.id.tabIndicator);
        tabIndicator.setupWithViewPager(introScreenPager);
    }

    private void setUpIntroScreenButtons(){
        buttonGetStarted = findViewById(R.id.buttonGetStarted);
        buttonGetStarted.setVisibility(View.INVISIBLE);
        buttonSkip = findViewById(R.id.buttonSkip);

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void autoRefreshActivity(){
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                loadLastIntroScreen();
                handler.postDelayed(this, 100); // set time here to refresh activity
            }
        });
    }

    private void loadLastIntroScreen(){

        position = introScreenPager.getCurrentItem();
        if(position < introScreenItems.size()){
            buttonSkip.setVisibility(View.VISIBLE);
            buttonGetStarted.setVisibility(View.INVISIBLE);
            tabIndicator.setVisibility(View.VISIBLE);
        }
        if(position == introScreenItems.size()-1){

            buttonSkip.setVisibility(View.INVISIBLE);
            buttonGetStarted.setVisibility(View.VISIBLE);
            tabIndicator.setVisibility(View.INVISIBLE);
        }
    }
}