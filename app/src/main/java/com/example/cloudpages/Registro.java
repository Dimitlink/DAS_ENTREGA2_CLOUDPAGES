package com.example.cloudpages;

import static com.example.cloudpages.Login.REQUEST_INTERNET_PERMISSION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cloudpages.Conexiones.ConexionFirebase;
import com.example.cloudpages.Conexiones.ConexionUsuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class Registro extends AppCompatActivity {
    static final int REQUEST_NOTIFI_PERMISSION = 11;
    static String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
    }


    //Metodo que realiza la peticion para realizar la notificacion desde Firebase
    public void notificacion(){

        //Inicializamos la app de Firebase y obtenemos el token del dispositivo
        FirebaseApp.initializeApp(Registro.this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult();
                        Log.d("token", "El token es " + token);

                        //Definimos los parametros necesarios para la peticion
                        Data datos = new Data.Builder()
                                .putString("tarea", "registro")
                                .putString("token", token)
                                .build();

                        //Definimos que vamos a realizar la peticion una unica vez
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionFirebase.class)
                                .setInputData(datos)
                                .build();

                        //Programamos la peticion
                        WorkManager.getInstance(Registro.this).getWorkInfoByIdLiveData(otwr.getId())
                                .observe(Registro.this, new Observer<WorkInfo>() {
                                    @Override
                                    public void onChanged(WorkInfo workInfo) {
                                        Log.d("notificacion", "exito");
                                    }
                                });

                        //Asiganmos la peticion en la cola
                        WorkManager.getInstance(Registro.this).enqueue(otwr);
                    }
                });
    }
    @Override //Si se pide la autorizacion de los permisos
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFI_PERMISSION) { //Permiso de notificaciones
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notificacion();

            } else {

            }
        } else if(requestCode == REQUEST_INTERNET_PERMISSION){ //Permiso de uso de internet

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registro();

            } else {
                Toast.makeText(Registro.this, "Permisos de internet necessarios para el funcionamiento", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Metodo asigando al boton REGISTRARSE
    public void onClickRegistrarse(View v){
        //Si se trata de api 26 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Si no tenemos los permisos de intenet
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                //Los pedimos
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_INTERNET_PERMISSION);
            } else{ //Si los tenemos, nos registramos
                registro();
            }
        } else { //Si se trata de una api inferior
            registro(); //Nos registramos
        }

    }

    //Metodo empleado para regsistrar al usuario
    public void registro(){
        EditText tUsuario = (EditText) findViewById(R.id.t_usuario_reg);
        EditText tContra = (EditText) findViewById(R.id.t_contra_reg);

        String usuario = tUsuario.getText().toString();
        String password = tContra.getText().toString();


        //Si se han rellenado los campos obligatorios
        if (!usuario.equals("") && !password.equals("")) {

            //Definimos los parametros necesarios para la peticion
            Data datos = new Data.Builder()
                    .putString("tarea", "registro")
                    .putString("usuario", String.valueOf(tUsuario.getText()))
                    .putString("password", String.valueOf(tContra.getText()))
                    .build();

            //Definimos que la tarea se va a realizar una vez
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionUsuarios.class)
                    .setInputData(datos)
                    .build();

            //Programamos la peticion
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo != null && workInfo.getState().isFinished()){
                                //Notificamos el resultado de la peticion
                                Toast.makeText(getApplicationContext(), workInfo.getOutputData().getString("resultado"), Toast.LENGTH_LONG).show();

                                //Si el registro ha sido exitoso
                                if (workInfo.getOutputData().getString("resultado").equals("El usuario se ha registrado correctamente")) {

                                    //Comprobamos si podemos enviar notificaciones
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                        if (ContextCompat.checkSelfPermission(Registro.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(Registro.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFI_PERMISSION);
                                        } else { //Si podemos enviar, mandamos notificacion
                                            notificacion();
                                        }
                                    } else {
                                        notificacion();
                                    }


                                    //Se cierra esta actividad y se abre el Menu Principal
                                    Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
                                    intent.putExtra("usuario", usuario);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    });

            //Asignamos la peticion a la cola
            WorkManager.getInstance(this).enqueue(otwr);
        } else { //Si no se han rellenado todos los campso obligatorios
            Toast.makeText(getApplicationContext(),"Completa todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    @Override //Si el usuario pulsa atras
    public void onBackPressed() {

        //Volvemos la login y cerramos esta actividad
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}