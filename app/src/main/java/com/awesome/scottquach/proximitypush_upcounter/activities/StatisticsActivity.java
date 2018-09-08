package com.awesome.scottquach.proximitypush_upcounter.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.awesome.scottquach.proximitypush_upcounter.R;
import com.awesome.scottquach.proximitypush_upcounter.features.Statistics.StatisticsFragment;

public class StatisticsActivity extends AppCompatActivity implements StatisticsFragment.StatisticsFragmentInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }
}
