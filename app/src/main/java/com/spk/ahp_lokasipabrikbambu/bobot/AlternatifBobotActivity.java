package com.spk.ahp_lokasipabrikbambu.bobot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spk.ahp_lokasipabrikbambu.R;
import com.spk.ahp_lokasipabrikbambu.model.KeputusanViewModel;
import com.spk.ahp_lokasipabrikbambu.result.AlternatifBobotResultActivity;
import com.spk.ahp_lokasipabrikbambu.utils.Matrix;
import com.spk.ahp_lokasipabrikbambu.utils.StringUtils;
import com.spk.ahp_lokasipabrikbambu.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wisnu on 12/23/17.
 */

public class AlternatifBobotActivity extends AppCompatActivity {

    private static final float DEFAULT_RATING = 0f;
    private KeputusanViewModel keputusanViewModel;

    private LinearLayout alternatifBobotContainer;

    private Matrix matrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternatif_bobot);

        bindData();
        bindView();
        initToolbar();
    }

    @SuppressLint("SetTextI18n")
    private void bindView() {
        this.matrix = new Matrix(keputusanViewModel.alternatifToBobotMap.keySet(),
                keputusanViewModel.kriteriaToBobotMap.keySet());

        alternatifBobotContainer = findViewById(R.id.alternatif_bobot_container);
        LinearLayout layoutContainer = new LinearLayout(this);
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (String option : keputusanViewModel.alternatifToBobotMap.keySet()) {
            TextView label = new TextView(this);
            label.setText("Alternativa :" + option);
            label.setGravity(Gravity.START);
            label.setTextSize(28);
            label.setTypeface(Typeface.DEFAULT_BOLD);
            label.setPadding(8, 16, 8, 8);
            layout.addView(label);

            List<LinearLayout> bars = generateRatingBars(option, keputusanViewModel.kriteriaToBobotMap.keySet());
            for (LinearLayout bar : bars) {
                layout.addView(bar);
            }
        }

        layoutContainer.addView(layout);
        ScrollView scroller = new ScrollView(this);
        scroller.addView(layoutContainer);
        alternatifBobotContainer.addView(scroller);
    }

    private void bindData() {
        keputusanViewModel = (KeputusanViewModel) getIntent().getSerializableExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("SetTextI18n")
    private List<LinearLayout> generateRatingBars(String option, Set<String> kriteriaSet) {
        List<LinearLayout> bars = new ArrayList<>();
        for (String kriteria : kriteriaSet) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            TextView kriteriaLabel = new TextView(this);
            kriteriaLabel.setText(kriteria + ": ");
            kriteriaLabel.setTextSize(20);
            kriteriaLabel.setGravity(Gravity.START);
            kriteriaLabel.setTypeface(Typeface.DEFAULT_BOLD);
            kriteriaLabel.setPadding(8, 32, 8, 8);
            row.addView(kriteriaLabel);
            row.addView(createRatingBar(option, kriteria));

            bars.add(row);
        }

        return bars;
    }

    private RatingBar createRatingBar(String option, String kriteria) {
        final RatingBar bar = new RatingBar(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(36, 8, 0, 8);
        bar.setTag(StringUtils.encodeStringPair(option, kriteria));
        bar.setNumStars(5);
        bar.setStepSize(0.5f);
        bar.setRating(DEFAULT_RATING);
        bar.setLayoutParams(params);
        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String[] labels = StringUtils.decodeStringPair((String) bar.getTag());
                String rowLabel = labels[0];
                String colLabel = labels[1];
                AlternatifBobotActivity.this.matrix.setValue(rowLabel, colLabel, rating);
                Log.i(this.getClass().getName(), AlternatifBobotActivity.this.matrix.toString());
            }
        });
        this.matrix.setValue(option, kriteria, DEFAULT_RATING);

        return bar;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        MenuItem menuItem = menu.findItem(R.id.action_selesai);
        if (menuItem != null) {
            ViewUtils.tintMenuIcon(AlternatifBobotActivity.this, menuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_selesai) {
            launchAlternatifBobotResultScreen();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchAlternatifBobotResultScreen() {
        Intent intent = new Intent(this, AlternatifBobotResultActivity.class);
        intent.putExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY, getHasilPembobotan());
        startActivity(intent);
    }

    private KeputusanViewModel getHasilPembobotan() {
        for (String alternatif : keputusanViewModel.alternatifToBobotMap.keySet()) {
            float bobot = 0;
            for (String kriteria : keputusanViewModel.kriteriaToBobotMap.keySet()) {
                float weight = keputusanViewModel.kriteriaToBobotMap.get(kriteria);
                float rating = this.matrix.getValue(alternatif, kriteria) * 20; // 5 bintang = 100%
                bobot += rating * weight;
            }
            keputusanViewModel.alternatifToBobotMap.put(alternatif, bobot);
        }
        return keputusanViewModel;
    }

}
