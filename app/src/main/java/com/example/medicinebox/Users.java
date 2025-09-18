package com.example.medicinebox;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.medicinebox.adapter.MyViewPagerAdapter;
import com.example.medicinebox.utils.UserRef;
import com.google.android.material.tabs.TabLayout;

public class Users extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    MyViewPagerAdapter myViewPagerAdapter;
    ImageView backBtn;
    Toolbar toolbar;
    UserRef userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_users);
        userRef = new UserRef(this);
        initWidgets();
        setUpViewPager();

        backBtn.setOnClickListener(v->onBackPressed());

    }



    private void setUpViewPager() {
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(myViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void initWidgets() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        backBtn = findViewById(R.id.back_Imageview);
        toolbar = findViewById(R.id.toolbar);
    }
}