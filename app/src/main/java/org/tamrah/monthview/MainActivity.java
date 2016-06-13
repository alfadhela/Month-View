package org.tamrah.monthview;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.tamrah.monthview.widget.MonthPagerAdapter;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MonthPagerAdapter adapter = new MonthPagerAdapter(this, Calendar.getInstance(), null);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(100);
        */
    }
}
