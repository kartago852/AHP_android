package com.spk.ahp_lokasipabrikbambu.bobot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.spk.ahp_lokasipabrikbambu.R;
import com.spk.ahp_lokasipabrikbambu.model.KeputusanViewModel;
import com.spk.ahp_lokasipabrikbambu.result.KriteriaBobotResultActivity;
import com.spk.ahp_lokasipabrikbambu.utils.Matrix;
import com.spk.ahp_lokasipabrikbambu.utils.StringUtils;
import com.spk.ahp_lokasipabrikbambu.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wisnu on 12/23/17.
 */

public class KriteriaBobotActivity extends AppCompatActivity {

    private KeputusanViewModel keputusanViewModel = null;

    private LinearLayout kriteriaBobotContainer;

    private static final String[] spinnerLabelsTemplate = {
            " absolutamente no más importante que(0.11) ", // absolutamente no más importante que
            " muy no más importante qu(0.14) ", // muy no más importante que
            " más sin importancia que(0.2) ", // más sin importancia que
            " bastante sin importancia que(0.33) ", // bastante sin importancia que
            " es igual de importante que(1) ", // es igual de importante que
            " es moderadamente importante que(3) ", // es moderadamente importante que
            " es fuertemente importante que(5) ", // es fuertemente importante que
            " es muy fuertemente importante que(7) ", // es muy fuertemente importante que
            " es extremadamente importante que "}; // es extremadamente importante que
    private static final float[] spinnerValues = {1f / 9f, 1f / 7f, 1f / 5f, 1f / 3f, 1f, 3f, 5f, 7f, 9f};
    private static final int DEFAULT_SPINNER_VALUE_POSITION = 4;
    private static final float DEFAULT_SPINNER_VALUE = spinnerValues[DEFAULT_SPINNER_VALUE_POSITION];

    private Matrix matrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kriteria_bobot);

        initToolbar();
        bindData();
        bindView();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void bindData() {
        keputusanViewModel = (KeputusanViewModel) getIntent().getSerializableExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY);
    }

    private void bindView() {
        List<String> comparisons = generateKriteriaPairs(keputusanViewModel.kriteriaToBobotMap.keySet());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        kriteriaBobotContainer = findViewById(R.id.kriteria_bobot_container);
        for (String comparison : comparisons) {
            Spinner spinner = createSpinner(comparison);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 40);
            spinner.setLayoutParams(layoutParams);
            layout.addView(spinner);
        }
        ScrollView scroller = new ScrollView(this);
        scroller.addView(layout);
        kriteriaBobotContainer.addView(scroller);
    }

    private Spinner createSpinner(String encodedKriteriaPair) {
        String[] kriteria = StringUtils.decodeStringPair(encodedKriteriaPair);

        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<CharSequence>(
                        this,
                        android.R.layout.simple_spinner_item,
                        generatePickerDisplayedValues(kriteria[0], kriteria[1]));
        adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

        Spinner spinner = new Spinner(this);
        spinner.setTag(encodedKriteriaPair);
        spinner.setAdapter(adapter);
        spinner.setSelection(DEFAULT_SPINNER_VALUE_POSITION);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String encodedPair = (String) parent.getTag();
                String[] kriteria = StringUtils.decodeStringPair(encodedPair);
                String barisKriteria = kriteria[0];
                String kolomKriteria = kriteria[1];
                float normalValue = spinnerValues[position];
                float invertedValue = 1 / normalValue;

                KriteriaBobotActivity.this.matrix.setValue(barisKriteria, kolomKriteria, normalValue);
                KriteriaBobotActivity.this.matrix.setValue(kolomKriteria, barisKriteria, invertedValue);

                Log.d("matriz", "matriz: " + KriteriaBobotActivity.this.matrix.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return spinner;
    }

    private String[] generatePickerDisplayedValues(String leftKriteria, String rightKriteria) {
        String[] values = new String[spinnerLabelsTemplate.length];
        for (int i = 0; i < spinnerLabelsTemplate.length; i++) {
            values[i] = leftKriteria + spinnerLabelsTemplate[i] + rightKriteria;
        }
        return values;
    }

    // Buat default matrix 1 semua by default
    private List<String> generateKriteriaPairs(Set<String> kriteriaSet) {
        this.matrix = new Matrix(kriteriaSet, kriteriaSet);
        List<String> comparisons = new ArrayList<>();
        Object[] kriteria = kriteriaSet.toArray();
        for (int i = 0; i < kriteria.length; i++) {
            String barisKriteria = (String) kriteria[i];
            this.matrix.setValue(barisKriteria, barisKriteria, DEFAULT_SPINNER_VALUE);

            for (int j = i + 1; j < kriteria.length; j++) {
                String kolomKriteria = (String) kriteria[j];
                this.matrix.setValue(barisKriteria, kolomKriteria, DEFAULT_SPINNER_VALUE);
                this.matrix.setValue(kolomKriteria, barisKriteria, DEFAULT_SPINNER_VALUE);

                String normalEncoding = StringUtils.encodeStringPair(barisKriteria, kolomKriteria);
                comparisons.add(normalEncoding);
            }
        }

        return comparisons;
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
            ViewUtils.tintMenuIcon(KriteriaBobotActivity.this, menuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_selesai) {
            Intent intent = new Intent(this, KriteriaBobotResultActivity.class);
            intent.putExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY, getHasilPembobotan());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private KeputusanViewModel getHasilPembobotan() {
        Map<String, Float> totalKolom = this.matrix.getTotalKolom();
        this.matrix.divideMatrixBySumOfColumn(totalKolom);
        Map<String, Float> bobot = this.matrix.getSumMatrix();

        for (String kriteria : bobot.keySet()) {
            Float bobotBaris = bobot.get(kriteria);
            keputusanViewModel.kriteriaToBobotMap.put(kriteria, bobotBaris);
        }
        return keputusanViewModel;
    }

//    private KeputusanViewModel getHasilPembobotan2() {
//        Map<String, Float> totalBaris = this.matrix.getTotalBaris();
//        Iterator<String> it = totalBaris.keySet().iterator();
//        float total = totalBaris.get(Matrix.GRAND_TOTAL_KEY);
//        while (it.hasNext()) {
//            String kriteria = it.next();
//            Float bobotBaris = totalBaris.get(kriteria);
//            Float persentaseBobot = bobotBaris / total;
//            if (!kriteria.equals(Matrix.GRAND_TOTAL_KEY)) {
//                keputusanViewModel.kriteriaToBobotMap.put(kriteria, persentaseBobot);
//            }
//        }
//        return keputusanViewModel;
//    }

}
