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
import com.spk.ahp_lokasipabrikbambu.bobot.KriteriaBobotActivity;
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

public class AlternatifActivity extends AppCompatActivity {

    private KeputusanViewModel keputusanViewModel = null;

    private LinearLayout alternatifContainer;
    private LinearLayout addAlternatifBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternatif);

        bindData();
        bindView();

        initToolbar();
        initFirstAlternatifItem();
        initAddAlternatifButton();
    }

    private void bindData() {
        keputusanViewModel = (KeputusanViewModel) getIntent().getSerializableExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void bindView() {
        alternatifContainer = findViewById(R.id.alternatif_container);
        addAlternatifBtn = findViewById(R.id.tambah_alternatif_btn);
    }

    private void initFirstAlternatifItem() {
        final MyItemView myItemView = new MyItemView(this, 1, "Alternativa ");
        myItemView.setOnDeleteItemListener(new MyItemView.ItemViewListener() {
            @Override
            public void onDeleteItemListener() {
                alternatifContainer.removeViewAt(myItemView.getPosition() - 1);
                updateItemPosition();
            }
        });
        alternatifContainer.addView(myItemView);
        myItemView.requestItemFocus();
    }

    private void initAddAlternatifButton() {
        addAlternatifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = alternatifContainer.getChildCount() + 1;
                final MyItemView myItemView = new MyItemView(AlternatifActivity.this, position, "Alternativa");
                myItemView.setOnDeleteItemListener(new MyItemView.ItemViewListener() {
                    @Override
                    public void onDeleteItemListener() {
                        alternatifContainer.removeViewAt(myItemView.getPosition() - 1);
                        updateItemPosition();
                    }
                });
                alternatifContainer.addView(myItemView);
                myItemView.requestItemFocus();
            }
        });
    }

    private void updateItemPosition() {
        for (int i = 0; i <= alternatifContainer.getChildCount(); i++) {
            View child = alternatifContainer.getChildAt(i);
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
            ViewUtils.tintMenuIcon(AlternatifActivity.this, menuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_selesai) {
            if (validateScreen()) {
                launchKriteriaBobotScreen();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchKriteriaBobotScreen() {
        Intent intent = new Intent(this, KriteriaBobotActivity.class);
        intent.putExtra(KeputusanViewModel.DATA_KEPUTUSAN_KEY, getDataViewModel());
        startActivity(intent);
    }

    private KeputusanViewModel getDataViewModel() {
        HashMap<String, Float> alternatifMap = new HashMap<>();
        for (int i = 0; i <= alternatifContainer.getChildCount(); i++) {
            View child = alternatifContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                alternatifMap.put(((MyItemView) child).getValue(), 0f);
            }
        }
        keputusanViewModel.alternatifToBobotMap = alternatifMap;
        return keputusanViewModel;
    }

    private boolean validateScreen() {
        return validateEmptyItem() && validateItem() && validateTwoItem() && validateSameItem();
    }

    private boolean validateItem() {
        boolean valid = true;
        for (int i = 0; i <= alternatifContainer.getChildCount(); i++) {
            View child = alternatifContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                if (!((MyItemView) child).validateField()) {
                    valid = false;
                }
            }
        }

        return valid;
    }

    private boolean validateEmptyItem() {
        boolean isItemNotEmpty = alternatifContainer.getChildCount() > 0;
        if (!isItemNotEmpty) {
            Toast.makeText(this, "Ingrese Alternativas", Toast.LENGTH_SHORT).show();
        }
        return isItemNotEmpty;
    }

    private boolean validateTwoItem() {
        boolean isTwoItems = alternatifContainer.getChildCount() > 1;
        if (!isTwoItems) {
            Toast.makeText(this, "Minimo dos Alternativas", Toast.LENGTH_SHORT).show();
        }
        return isTwoItems;
    }

    private boolean validateSameItem() {
        List<String> list = new ArrayList<>();
        Set<String> hashSet = new HashSet<>();
        for (int i = 0; i <= alternatifContainer.getChildCount(); i++) {
            View child = alternatifContainer.getChildAt(i);
            if (child instanceof MyItemView) {
                String value = ((MyItemView) alternatifContainer.getChildAt(i)).getValue().toLowerCase();
                list.add(value);
            }
        }
        hashSet.addAll(list);
        list.clear();
        list.addAll(hashSet);
        boolean valid = list.size() > 1;
        if (!valid) {
            Toast.makeText(this, "Error, Alternativas Iguales", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

}
