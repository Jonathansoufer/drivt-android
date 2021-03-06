package com.lab.drivt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lab.drivt.utils.Tools;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mSliderViewPaper;
    private LinearLayout mDotLayout;

    private TextView[] mDots;
    private SliderAdapter sliderAdapter;


    @Override
    // The onCreate methods is equivalent to the useEffect hook in React. This method is called when the activity starts.
    protected void onCreate(Bundle savedInstanceState) {
        // This super is the constructor
        super.onCreate(savedInstanceState);
        // setContentView sets to which layout XML this activity is related.
        setContentView(R.layout.activity_main);

        // Setting local variable based on R = Resource elements within the layout based on their ids. Each element in a layout must have an ID.
        // name for the variable + type + findViewById method passing the element ID required.
        mSliderViewPaper = (ViewPager) findViewById(R.id.sliderView);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsContainer);


        sliderAdapter = new SliderAdapter(this);
        mSliderViewPaper.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        //Setting a listener to detect whenever the page changes.
        mSliderViewPaper.addOnPageChangeListener(viewListener);

        if (!Tools.internalIsAccessibilityOn(MainActivity.this, DrivtService.class)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            return;
        }
    }

    public void addDotsIndicator(int position){
      mDots = new TextView[4];
      mDotLayout.removeAllViews();

      for (int i = 0; i < mDots.length; i++){
          mDots[i] = new TextView(this);
          mDots[i].setText(Html.fromHtml("&#8226;"));
          mDots[i].setTextSize(35);
          mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

          mDotLayout.addView(mDots[i]);
        }

      if(mDots.length > 0 ) {
          mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
      }
    };

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void GoHome(View view)
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}