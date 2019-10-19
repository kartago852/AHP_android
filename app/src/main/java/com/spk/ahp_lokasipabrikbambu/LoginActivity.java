package com.spk.ahp_lokasipabrikbambu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private long backPressedTime;
    String email,pass;


    private CardView Login,Register;
    private FirebaseAuth firebaseAuth;
    private EditText TextEmail;
    private EditText TextPasss;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );


        firebaseAuth = FirebaseAuth.getInstance();
        Login = (CardView)findViewById(R.id.login);
        Register = (CardView)findViewById(R.id.register);
        TextEmail = (EditText)findViewById(R.id.log_email);
        TextPasss = (EditText)findViewById(R.id.log_pass);
        progressDialog = new ProgressDialog(this);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //registeractivity();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logearusuario();
            }
        });
    }

    private void logearusuario(){
        //Obtenemos el email y la contraseña desde las cajas de texto
        email = TextEmail.getText().toString().trim();
        pass = TextPasss.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }
        // Sigue el flujo de validacion
        progressDialog.setMessage("Verificando en la base de datos...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);
                            Toast.makeText(LoginActivity.this,"Bienvenido "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                            Intent intencion=  new Intent(getApplication(), HomeActivity.class);
                            intencion.putExtra(HomeActivity.user, user);
                            startActivity(intencion);
                        }else{

                            Toast.makeText(LoginActivity.this,"Datos incorrectos ",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
}
