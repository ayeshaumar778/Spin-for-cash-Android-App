package com.cash.spinningwheelandroid;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.cash.spinningwheelandroid.MainActivity.coin;

public class Wallet extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    Toolbar toolbar;
    public static TextView coins;
    static Button redeem;
    public static ViewPagerAdapter adapter;
    private String[] tabs = {"Earning", "Withdraw"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        coins = (TextView) findViewById(R.id.coin);
        redeem = (Button) findViewById(R.id.redeem);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setCoins(coin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void setCoins(int total) {
        coins.setText("" + total);
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Earning(), tabs[0]);
        adapter.addFrag(new Withdraw(), tabs[1]);
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}
