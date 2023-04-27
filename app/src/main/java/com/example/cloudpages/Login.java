package com.example.cloudpages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloudpages.Conexiones.ConexionUsuarios;

public class Login extends AppCompatActivity {

    static final int REQUEST_INTERNET_PERMISSION = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override //Si se pide la autorizacion de los permisos
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_INTERNET_PERMISSION) { //Permiso de notificaciones
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                login();
            } else {
                Toast.makeText(Login.this, "Permisos de internet necessarios para el funcionamiento", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Metodo asignado al boton LOGIN
    public void onClickLogin(View v){

        //Si la aplicacion tiene sdk mayor que 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Si no tenemos permisos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                //Los pedimos
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_INTERNET_PERMISSION);
            } else{ //Si los tenemos
                //Realizamos el login
                login();
            }
        } else { //Realizamos el login
            login();
        }

    }

    public void login(){
        EditText tUsuario = (EditText) findViewById(R.id.t_usuario);
        EditText tContra = (EditText) findViewById(R.id.t_contra);

        String usuario = tUsuario.getText().toString();
        String password = tContra.getText().toString();

        //Si se han rellenado los campos necesarios
        if(!usuario.equals("") && !password.equals("")){

            //Definimos los parametros necesarios para la peticion
            Data datos = new Data.Builder()
                    .putString("tarea", "login")
                    .putString("usuario", usuario)
                    .putString("password", password)
                    .build();

            //Definimos que la peticion se va a realizar una unica vez
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionUsuarios.class)
                    .setInputData(datos)
                    .build();

            //Programamos la peticion
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo != null && workInfo.getState().isFinished()){
                                //Imprimimos el resultado
                                String resultado = workInfo.getOutputData().getString("resultado");
                                Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();

                                //Si el login ha sido exitoso
                                if(resultado.equals("Bienvenido a Cloud Pages")){

                                    //Vamos al menu principal y acabos esta actividad
                                    Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
                                    intent.putExtra("usuario", usuario);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        }
                    });
            //Anadimos la peticion a la cola
            WorkManager.getInstance(this).enqueue(otwr);

        } else { //Si no se han rellenado todos los campos obligatorios
            Toast.makeText(getApplicationContext(),"Completa todos los campos", Toast.LENGTH_LONG).show();
        }
    }


    //Si queremos registrarnos
    public void onClickRegistro(View v){
        //Vamos a la actividad de registro y acamos esta
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
        finish();
    }
}