package com.spk.ahp_lokasipabrikbambu.input;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spk.ahp_lokasipabrikbambu.R;
import com.spk.ahp_lokasipabrikbambu.model.KeputusanViewModel;
import com.spk.ahp_lokasipabrikbambu.utils.ViewUtils;
import com.spk.ahp_lokasipabrikbambu.view.MyItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wisnu on 12/23/17.
 */

public class KriteriaActivity extends AppCompatActivity {

    private LinearLayout kriteriaContainer;
    private LinearLayout addKriteriaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kriteria);

        bindView();

        initToolbar();
        initFirstKriteriaItem();
        initAddKriteria();
    }

    private void bindView() {
        kriteriaContainer = findViewById(R.id.kriteria_container);
        addKriteriaBtn = findViewById(R.id.tambah_kriteria_btn);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFirstKriteriaItem() {
        final MyItemView myItemView = new MyItemView(this, 1, "Criterio # ");
        myItemView.setOnDeleteItemListener(new MyItemView.ItemViewListener() {
            @Override
            public void onDeleteItemListener() {
                kriteriaContainer.removeViewAt(myItemView.getPosition() - 1);
                updateItemPosition();
            }
        });
        kriteriaContainer.addView(myItemView);
        myItemView.requestItemFocus();
    }

    private void initAddKriteria() {
        addKriteriaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = kriteriaContainer.getChildCount() + 1;
                final MyItemView myItemView = new MyItemView(KriteriaActivity.this, position, "Criterio #");
                myItemView.setOnDeleteItemListener(new MyItemView.ItemViewListener() {
                    @Override
                    public void onDeleteItemListener() {
                        kriteriaContainer.removeViewAt(myItemView.getPosition() - 1);
                        updateItemPosition();
                    }
                });
                kriteriaContainer.addView(myItemView);
                myItemView.requestItemFocus();
            }
        });
    }

    private void updateItemPosition() {
        for (int i = 0; i <= kriteriaContainer.getChildCount(); i++) {
            View child = kriteriaContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                ((MyItemView) child).setPosition(i + 1);
            }
        }
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
            ViewUtils.tintMenuIcon(KriteriaActivity.this, menuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_selesai) {
            if (validateScreen()) {
                LaunchAlternatifScreen();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LaunchAlternatifScreen() {
        Intent intent = new Intent(this, AlternatifActivity.class);
        intent.putExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY, getDataViewModel());
        startActivity(intent);
    }

    private KeputusanViewModel getDataViewModel() {
        HashMap<String, Float> kriteriaMap = new HashMap<>();
        KeputusanViewModel keputusanViewModel = new KeputusanViewModel();
        for (int i = 0; i <= kriteriaContainer.getChildCount(); i++) {
            View child = kriteriaContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                kriteriaMap.put(((MyItemView) child).getValue(), 0f);
            }
        }

        keputusanViewModel.kriteriaToBobotMap = kriteriaMap;
        return keputusanViewModel;
    }

    private boolean validateScreen() {
        return validateEmptyItem() && validateItem() && validateTwoItem() && validateSameItem();
    }

    private boolean validateItem() {
        boolean valid = true;
        for (int i = 0; i <= kriteriaContainer.getChildCount(); i++) {
            View child = kriteriaContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                if (!((MyItemView) child).validateField()) {
                    valid = false;
                }
            }
        }

        return valid;
    }

    private boolean validateEmptyItem() {
        boolean isItemNotEmpty = kriteriaContainer.getChildCount() > 0;
        if (!isItemNotEmpty) {
            Toast.makeText(this, "Kriteria belum ada, silahkan tambah kriteria", Toast.LENGTH_SHORT).show();
        }
        return isItemNotEmpty;
    }

    private boolean validateTwoItem() {
        boolean isTwoItems = kriteriaContainer.getChildCount() > 1;
        if (!isTwoItems) {
            Toast.makeText(this, "Kriteria minimal harus dua, silahkan tambah kriteria", Toast.LENGTH_SHORT).show();
        }
        return isTwoItems;
    }

    private boolean validateSameItem() {
        List<String> list = new ArrayList<>();
        Set<String> hashSet = new HashSet<>();
        for (int i = 0; i <= kriteriaContainer.getChildCount(); i++) {
            View child = kriteriaContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                String value = ((MyItemView) kriteriaContainer.getChildAt(i)).getValue().toLowerCase();
                list.add(value);
            }
        }
        hashSet.addAll(list);
        list.clear();
        list.addAll(hashSet);
        boolean valid = list.size() > 1;
        if (!valid) {
            Toast.makeText(this, "Kriteria tidak boleh sama", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

}
