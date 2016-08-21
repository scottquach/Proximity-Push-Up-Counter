package com.awesome.scottquach.proximitypush_upcounter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.wearable.internal.StorageInfoResponse;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.view.Chart;

public class Graph extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    ArrayList<Integer> numberOfPushArray = new ArrayList<Integer>();


    int sentinel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        sharedPref = getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        editor = sharedPref.edit();

        sentinel = sharedPref.getInt("sentinel", 0);


        iterateThroughSharedPref();
        setUpGraph();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);

        int numberOfSaves = sharedPref.getInt("numberSaves", 1);
        LineGraphSeries<DataPoint> series = null;
        for (int i = 0; i < numberOfSaves; i++) {
            int numberOfPushInt = sharedPref.getInt(String.valueOf(numberOfSaves), 1);


            series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(i, numberOfPushInt),

            });
        }

        graph.addSeries(series);
    }

    private void iterateThroughSharedPref() {
        int numberOfSaves = sharedPref.getInt("numberSaves", 1);
        for (int i = 0; i < numberOfSaves; i++) {
            int numberOfPushInt = sharedPref.getInt(String.valueOf(numberOfSaves), 1);
            numberOfPushArray.add(numberOfPushInt);
        }

    }
}
