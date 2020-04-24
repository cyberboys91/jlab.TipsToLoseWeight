package jlab.TipsToLoseWeight.Activity;

import jlab.TipsToLoseWeight.R;

import android.os.Bundle;
import android.view.Menu;

import java.util.ArrayList;
import android.view.Surface;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.support.v7.app.ActionBar;
import com.google.android.gms.ads.AdView;
import android.support.v7.widget.Toolbar;
import android.content.res.Configuration;
import jlab.TipsToLoseWeight.Activity.Utils.Utils;
import com.google.android.gms.ads.AdRequest;
import android.view.animation.AnimationUtils;
import jlab.TipsToLoseWeight.Activity.Utils.Tip;
import android.support.v7.app.AppCompatActivity;
import jlab.TipsToLoseWeight.Activity.Utils.TipManager;

public class TipDetailsActivity extends AppCompatActivity {

    private Tip tip;
    private int tipId = 1, id = 1;
    private TipManager tipManager;
    private TextView tvDescription, tvTipTitle, tvTipCuriosity;
    private RelativeLayout rlDescription, rlTipImage;
    private ImageView ivTipImage;
    private ArrayList<Bitmap> bitmapImages;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Thread imageAnim = new Thread(new Runnable() {
        @Override
        public void run() {
            final int[] index = {0};
            while(true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivTipImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_out));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivTipImage.setImageBitmap(bitmapImages.get(index[0]));
                                        ivTipImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_in));
                                        index[0] = (index[0] + 1) % bitmapImages.size();
                                    }
                                });
                            }
                        }).start();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        this.id = getIntent().getIntExtra(Utils.ID_KEY, 1);
        this.tipId = getIntent().getIntExtra(Utils.TIP_ID_KEY, 1);
        this.tipManager = new TipManager(this);
        this.tvDescription = (TextView) findViewById(R.id.tvTipDescription);
        this.ivTipImage = (ImageView) findViewById(R.id.ivDessert);
        loadImageHeight();
        this.rlTipImage = (RelativeLayout) findViewById(R.id.rlDessertImage);
        this.rlTipImage.setAnimation(AnimationUtils.loadAnimation(this, R.anim.up_in));
        this.rlDescription = (RelativeLayout) findViewById(R.id.rlDescription);
        this.tvTipTitle = (TextView) findViewById(R.id.tvTipTitle);
        this.tvTipCuriosity = (TextView) findViewById(R.id.tvTipCuriosity);
        this.rlDescription.setAnimation(AnimationUtils.loadAnimation(this, R.anim.down_in));
        this.bitmapImages = tipManager.getBitmapImages(this.tipId);
        this.toolbar = (Toolbar) findViewById(R.id.tbHeader);
        this.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(this.toolbar);
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayShowHomeEnabled(true);
        this.actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadImageHeight() {
        ViewGroup.LayoutParams lp = this.ivTipImage.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        lp.height = displayMetrics.heightPixels / (isPortrait ? 3 : 2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadImageHeight();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.tip = tipManager.getTip(id);
        this.tvTipTitle.setText(tip.getTitle());
        this.tvDescription.setText(tip.getDescription());
        this.tvTipCuriosity.setText(tip.getCuriosity());
        if(!this.imageAnim.isAlive())
            this.imageAnim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mnRateApp:
                try {
                    Utils.rateApp(this);
                } catch (Exception | OutOfMemoryError ignored) {
                    ignored.printStackTrace();
                }
                break;
            case R.id.mnAbout:
                Utils.showAboutDialog(this, this.tvDescription);
                break;
            case R.id.mnClose:
                onBackPressed();
                break;
        }
        return true;
    }
}
