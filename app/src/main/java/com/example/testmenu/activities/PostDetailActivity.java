package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import com.example.testmenu.R;
import com.example.testmenu.adapters.SliderAdapter;
import com.example.testmenu.entidades.SliderItem;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    PublicacionFirebase mPublicacionFirebase;

    String mExtraPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSliderView = findViewById(R.id.imageSlider);
        mPublicacionFirebase = new PublicacionFirebase();

        mExtraPostId = getIntent().getStringExtra("id");

        getPost();
    }

    private void instanceSlider() {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost() {
        mPublicacionFirebase.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("image1")) {
                    String image1 = documentSnapshot.getString("image1");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image1);
                    mSliderItems.add(item);
                }
                if (documentSnapshot.contains("image2")) {
                    String image2 = documentSnapshot.getString("image2");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image2);
                    mSliderItems.add(item);
                }

                instanceSlider();

            }
        });
    }
}