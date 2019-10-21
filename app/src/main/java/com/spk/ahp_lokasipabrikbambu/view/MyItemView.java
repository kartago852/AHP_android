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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spk.ahp_lokasipabrikbambu.R;
import com.spk.ahp_lokasipabrikbambu.model.Criterios;

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
    private ImageView validadcampo;

    DatabaseReference mDatabase;

    private ItemViewListener onDeleteItemListener;

    public MyItemView(Context context, int position, String hint) {
        super(context);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.AppTheme);
        LayoutInflater layoutInflater = LayoutInflater.from(contextThemeWrapper);
        View view = layoutInflater.inflate(R.layout.item_field, this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        itemField = view.findViewById(R.id.item_field);
        itemFieldWrapper = view.findViewById(R.id.item_field_wrapper);
        deleteButton = view.findViewById(R.id.delete_item_btn);
        validadcampo = view.findViewById( R.id.validate_item_btn );

        setItemHint(hint);
        setPosition(position);
        initDeleteButton();

        validarCampo();
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

    private void validarCampo(){
            final String texto = itemField.getText().toString().trim();

        validadcampo.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mDatabase.push().getKey();
                Criterios cri = new Criterios(id, texto) ;
                mDatabase.child( "Criterios" ).child( id ).setValue( getValue() );
                Toast.makeText( getContext(),"Se registraron los criterio" , Toast.LENGTH_SHORT).show();
                    //itemField.setEnabled(false);

            }
        } );

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
