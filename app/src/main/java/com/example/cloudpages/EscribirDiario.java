package com.example.cloudpages;

import static com.example.cloudpages.Registro.REQUEST_NOTIFI_PERMISSION;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cloudpages.Conexiones.ConexionFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EscribirDiario extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 123;
    static String usuario;
    static String token;

    private Bitmap img;
    ImageView iDiario;
    EditText e_titulo,e_cuerpo;

    String titulo,cuerpo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_diario);


        Log.d("usuario en escribir diarios",this.getIntent().getExtras().getString("usuario"));
        usuario = this.getIntent().getExtras().getString("usuario");

        iDiario = (ImageView) findViewById(R.id.i_diario);
        e_titulo = (EditText) findViewById(R.id.t_titulo);
        e_cuerpo = (EditText) findViewById(R.id.t_cuerpoDiario);

        //Si se ha girado la pantalla o pulsado el boton
        if(savedInstanceState != null){
            //Recuperamos los valores de las variables
            byte[] byteArray = savedInstanceState.getByteArray("imagen");
            try{
                img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                iDiario.setImageBitmap(img);
            }catch (NullPointerException e){}

        }

    }




    @Override //Metodo para pedir permiso para usar la camara
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso ha sido otorgado y abrimos la camara
                camaraLouncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            } else {
                // El permiso denegado
                Toast.makeText(EscribirDiario.this, "Para guardar los diarios, tienes que incluir imagen", Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_NOTIFI_PERMISSION){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso ha sido otorgado y podemos notificar al usuario

            } else {

            }

        }
    }

    public void notificar(){
        //Inicializamos la app de Firebase y obtenemos el token
        FirebaseApp.initializeApp(EscribirDiario.this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult();
                        Log.d("token", "El token es " + token);
                        //Definimos los parametros para la notificacion
                        Data datos = new Data.Builder()
                                .putString("token", token)
                                .putString("tarea", "escritura")
                                .build();

                        //Definimos que la peticion se va a realizar una vez
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionFirebase.class)
                                .setInputData(datos)
                                .build();


                        //Programamos la tarea en segundo plano
                        WorkManager.getInstance(EscribirDiario.this).getWorkInfoByIdLiveData(otwr.getId())
                                .observe(EscribirDiario.this, new Observer<WorkInfo>() {
                                    @Override
                                    public void onChanged(WorkInfo workInfo) {}});

                        //Metemos en cola la peticion
                        WorkManager.getInstance(EscribirDiario.this).enqueue(otwr);
                    }
                });
    }
    //Documentacion de volley : https://google.github.io/volley/
    //Metodo asignado al boton de GUARDAR
    public void onClickGuardar(View v){

        titulo = e_titulo.getText().toString();
        cuerpo = e_titulo.getText().toString();

        //Si se han rellenado todos los campos obligatorios
        if(!titulo.equals("") && !cuerpo.equals("") && img !=null) {
            //Inicializamos la cola de peticiones
            RequestQueue rq = Volley.newRequestQueue(EscribirDiario.this);

            //Transformamos la foto en un BLOB
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray= baos.toByteArray();
            String blob = String.valueOf(Base64.encodeToString(byteArray, Base64.DEFAULT));

            //Identificamos la URL a la que se va a hacer la peticion
            StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/diarios.php", new Response.Listener<String>() {
                @Override //En caso de exito
                public void onResponse(String response) {
                    String respuesta=response;

                    //Notificamos al usuario
                    Toast.makeText(EscribirDiario.this, respuesta, Toast.LENGTH_SHORT).show();

                    //Si el diario se ha guardado correctamente
                    if(respuesta.equals("El diario se ha guardado correctamente")){
                        //Enviamos una notificacion a traves de firebase
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (ContextCompat.checkSelfPermission(EscribirDiario.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(EscribirDiario.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFI_PERMISSION);
                            } else { //Si podemos enviar, mandamos notificacion
                                notificar();
                            }
                        } else {
                            notificar();
                        }
                        e_cuerpo.setText("");
                        e_titulo.setText("");


                    }
                }
            }, new Response.ErrorListener() {
                @Override //En caso de error
                public void onErrorResponse(VolleyError error) {
                    //Notificamos sobre el fallo
                    Toast.makeText(EscribirDiario.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Definicion de los parametros necesarios para la peticion

                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("tarea", "anadir");
                    parametros.put("usuario", usuario);
                    parametros.put("titulo", titulo);
                    parametros.put("cuerpo", cuerpo);
                    parametros.put("foto", blob);
                    return parametros;
                }
            };

            //Enviamos la peticion
            rq = Volley.newRequestQueue(EscribirDiario.this);
            rq.add(sr);

        } else {
            //Si no se ha rellenado el titulo o el cuerpo
            Toast.makeText(EscribirDiario.this, "Debes rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
        }
    }

    //Metodo asignado al boton de SACAR FOTO
    public void onClickFoto(View v){

        //Si la version es mayor que la version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Si no tenemos permisos, pedimos los permisos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else { //En caso contrario abrimos la camara

                camaraLouncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            }
        } else { //En caso contrario abrimos la camara
            camaraLouncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        }



    }
     //Actividad que abre la camara y la asigna al ImageView
    ActivityResultLauncher<Intent> camaraLouncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                Bundle extras = result.getData().getExtras();
                img =  (Bitmap) extras.get("data");
                iDiario.setImageBitmap(img);
            }
        }
    });

    @Override //Metodo que se activa cuando se gira el movil o pulsa el boton home
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Si se ha asignado una foto
            if(img !=null) {
                //Guardamos su valor para no perderla
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                outState.putByteArray("imagen", byteArray);
            }
    }



    @Override //Metodo empelado cuando el usuario pulsa atras
    public void onBackPressed() {

        //Volvemos al menu principal y acabamos con esta actividad
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        finish();
    }
}