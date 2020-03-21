package com.newpage.hack2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, "f.txt");
        String text = "r";

        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(file));
            wr.write(text);
            wr.flush();
            wr.close();
        } catch (IOException ex) {
            Log.e("ex", "file ex");
        }

        ViewGroup rootHelper = findViewById(R.id.roothelper);

        ImageView nextButton1 = findViewById(R.id.nextButton1);
        nextButton1.setOnClickListener(v -> {
            final Scene scene2 = Scene.getSceneForLayout(rootHelper, R.layout.helperscene2, this);
            TransitionSet set = new TransitionSet();
            set.addTransition(new Fade());
            set.addTransition(new Slide(Gravity.RIGHT));
            set.setOrdering(TransitionSet.ORDERING_TOGETHER);
            // уставим свою длительность анимации
            set.setDuration(1000);
            // и изменим Interpolator
            set.setInterpolator(new AccelerateInterpolator());
            TransitionManager.go(scene2, set);

            ImageView nextButton2 = findViewById(R.id.nextbutton2);
            nextButton2.setOnClickListener(v1 -> {
                startActivity(new Intent(this,LoginActivity.class));
                finish();
            });
        });
    }
}
