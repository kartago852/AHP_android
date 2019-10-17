package com.spk.ahp_lokasipabrikbambu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.spk.ahp_lokasipabrikbambu.R;

/**
 * Created by wisnu on 12/23/17.
 */

@SuppressLint("ViewConstructor")
public class MyItemView extends LinearLayout {

    private int position = 0;
    private String hint = "";

    private TextInputEditText itemField;
    private TextInputLayout itemFieldWrapper;
    private ImageView deleteButton;

    private ItemViewListener onDeleteItemListener;

    public MyItemView(Context context, int position, String hint) {
        super(context);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.AppTheme);
        LayoutInflater layoutInflater = LayoutInflater.from(contextThemeWrapper);
        View view = layoutInflater.inflate(R.layout.item_field, this);

        itemField = view.findViewById(R.id.item_field);
        itemFieldWrapper = view.findViewById(R.id.item_field_wrapper);
        deleteButton = view.findViewById(R.id.delete_item_btn);

        setItemHint(hint);
        setPosition(position);
        initDeleteButton();
    }

    void setItemHint(String hint) {
        this.hint = hint;
    }

    public void setPosition(int position) {
        this.position = position;
        itemFieldWrapper.setHint(hint + position);
    }

    private void initDeleteButton() {
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemField.clearFocus();
                if (onDeleteItemListener != null) {
                    onDeleteItemListener.onDeleteItemListener();
                }
            }
        });
    }

    public void setOnDeleteItemListener(ItemViewListener onDeleteItemListener) {
        this.onDeleteItemListener = onDeleteItemListener;
    }

    public String getValue() {
        return itemField.getText().toString().trim();
    }

    public int getPosition() {
        return position;
    }

    public void requestItemFocus() {
        itemField.requestFocus();
    }

    public boolean validateField() {
        String text = itemField.getText().toString();
        if (text.trim().isEmpty()) {
            itemFieldWrapper.setError("Campo Vacio");
            itemFieldWrapper.setErrorEnabled(true);
            return false;
        }

        itemFieldWrapper.setError(null);
        itemFieldWrapper.setErrorEnabled(false);
        return true;
    }

    public interface ItemViewListener {
        void onDeleteItemListener();
    }

}
