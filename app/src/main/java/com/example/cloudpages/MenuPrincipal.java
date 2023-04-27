package com.example.cloudpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuPrincipal extends AppCompatActivity {

    static String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);


        //Recogemos los extras de las actividades anteriores
        Log.d("usuario en el menu principal",this.getIntent().getExtras().getString("usuario"));
        usuario = this.getIntent().getExtras().getString("usuario");
    }


    //Metoso asignado al boton ESCRIBIR DIARIO
    public void onClickEscribir(View v){
        //Creamos una actividad de Escritura de diario y terminamos esta
        Intent intent = new Intent(this, EscribirDiario.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        finish();
    }

    //Metoso asignado al boton VER DIARIOS
    public void onClickListar(View v){

        //Creamos una actividad de Listado de diarios y acabamos con esta
        Intent intent = new Intent(this, ListaDiarios.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        finish();
    }

    //Si el usuario pulsa atras
    @Override
    public void onBackPressed() {

        //Volvemos al login y acabamos con esta actividad
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}