package jlab.TipsToLoseWeight.Activity;

/*
 * Created by Javier on 22/03/2020.
 */

import android.os.Bundle;
import android.view.Menu;
import android.view.Surface;
import android.view.MenuItem;
import android.content.Intent;
import jlab.TipsToLoseWeight.R;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.ads.AdView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.ads.AdRequest;
import jlab.TipsToLoseWeight.Activity.Utils.Tip;
import android.support.v7.app.AppCompatActivity;
import jlab.TipsToLoseWeight.Activity.Utils.Utils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.NavigationView;
import jlab.TipsToLoseWeight.Activity.View.GridTipsView;


public class TipListActivity extends AppCompatActivity implements Utils.IRunOnUIThread,
        NavigationView.OnNavigationItemSelectedListener{

    private GridTipsView gridTipsView;
    private LayoutInflater inflater;
    private SwipeRefreshLayout srlRefresh;
    private Toolbar toolbar;
    private GridTipsView.OnTipClickListener onTipClickListener = new GridTipsView.OnTipClickListener() {
        @Override
        public void onClick(Tip tip) {
            Intent intent = new Intent(TipListActivity.this, TipDetailsActivity.class);
            intent.putExtra(Utils.ID_KEY, tip.getId());
            intent.putExtra(Utils.TIP_ID_KEY, tip.getTipId());
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.runnerOnUIThread = this;
        setContentView(R.layout.activity_tip_list);
        this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.gridTipsView = (GridTipsView) findViewById(android.R.id.list);
        this.gridTipsView.setOnGetSetViewListener(inflater, getListItemWidth(), getListItemHeight(), onTipClickListener);
        this.srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        this.srlRefresh.setColorSchemeResources(R.color.colorAccent);
        this.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadContent();
            }
        });
        this.toolbar = (Toolbar) findViewById(R.id.tbHeader);
        this.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(this.toolbar);
        AdView adView = (AdView) findViewById(R.id.adView0);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    public int getListItemHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        int numColumns = (isPortrait ? 1 : 2);
        this.gridTipsView.setNumColumns(numColumns);
        return (displayMetrics.heightPixels / (isPortrait ? 3 : 2));
    }


    public int getListItemWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        int numColumns = (isPortrait ? 1 : 2);
        this.gridTipsView.setNumColumns(numColumns);
        return (displayMetrics.widthPixels / numColumns);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.gridTipsView.setOnGetSetViewListener(inflater, getListItemWidth(), getListItemHeight(), onTipClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
    }

    private void loadContent() {
        this.srlRefresh.setRefreshing(true);
        this.gridTipsView.loadContent();
        this.srlRefresh.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnRateApp:
                try {
                    Utils.rateApp(this);
                } catch (Exception | OutOfMemoryError ignored) {
                    ignored.printStackTrace();
                }
                break;
            case R.id.mnAbout:
                Utils.showAboutDialog(this, this.gridTipsView);
                break;
            case R.id.mnClose:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void run(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onOptionsItemSelected(item);
    }
}
