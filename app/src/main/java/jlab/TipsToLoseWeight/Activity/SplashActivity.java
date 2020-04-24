package jlab.TipsToLoseWeight.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.view.animation.AnimationUtils;
import android.support.v7.app.AppCompatActivity;

import jlab.TipsToLoseWeight.Activity.Utils.TipManager;
import jlab.TipsToLoseWeight.R;

/**
 * Created by Javier on 7/4/2020.
 */

public class SplashActivity extends AppCompatActivity {
    private ImageView ivIcon;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ivIcon = (ImageView) findViewById(R.id.ivIconInSplash);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.beat);
        ivIcon.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new TipManager(getBaseContext()).getAllDetails();
                        Intent intent = new Intent(getBaseContext(), TipListActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }).start();
            }
        }, 2000);
    }
}
