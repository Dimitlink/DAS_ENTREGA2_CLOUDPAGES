package com.example.cloudpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditarDiario extends AppCompatActivity {

    private ImageView imagen;

    private String fecha, usuario, titulo, cuerpo;

    private TextView tituloTv, cuerpoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_diario);

        //Guardamos en variables los valores de los extras de la actividad anterior
        usuario = getIntent().getExtras().getString("usuario");
        fecha = getIntent().getExtras().getString("fecha");
        tituloTv = (EditText) findViewById(R.id.t_etitulo);
        tituloTv.setText(getIntent().getExtras().getString("titulo"));
        cuerpoTv = (EditText) findViewById(R.id.t_ecuerpo);
        cuerpoTv.setText(getIntent().getExtras().getString("cuerpo"));

        //Convertimos el array de bytes de la foto en un bitmap para poder mostrarla correctamente
        byte[] byteArray = getIntent().getByteArrayExtra("foto");
        Log.d("foto en editar diario", Arrays.toString(byteArray));
        Log.d("Tamaño del array", String.valueOf(byteArray.length));

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagen = (ImageView) findViewById(R.id.i_ediario);
        imagen.setImageBitmap(bitmap);

    }

    //Documentacion de volley : https://google.github.io/volley/
    //Metodo usado para realizar operaciones tras pulsar el boton GUARDAR
    public void onClickEditar(View v){

        titulo = tituloTv.getText().toString();
        cuerpo = cuerpoTv.getText().toString();

        //Si se han rellenado todos los campos obligatorios
        if(!titulo.equals("") && !cuerpo.equals("")) {

            //Inicializamos nuestra cola de peticiones
            RequestQueue rq = Volley.newRequestQueue(EditarDiario.this);

            //Indicamos la url a la que se va a realizar la peticion
            StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/diarios.php", new Response.Listener<String>() {
                @Override
                //Si la respuesta ha sido correcta
                public void onResponse(String response) {
                    String respuesta=response;
                    //Notificamos al usuarios del exito
                    Toast.makeText(EditarDiario.this, respuesta, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override //Si la respuesta no ha sido correcta
                public void onErrorResponse(VolleyError error) {
                    //Notificamos al usuarios del fallo
                    Toast.makeText(EditarDiario.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Inicializacion de todos los parametros necesarios para modificar el diario
                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("tarea", "editar");
                    parametros.put("usuario", usuario);
                    parametros.put("fecha", fecha);
                    parametros.put("titulo", titulo);
                    parametros.put("cuerpo", cuerpo);
                    return parametros;
                }
            };

            //Se envia la solicitud con los parametros
            rq = Volley.newRequestQueue(EditarDiario.this);
            rq.add(sr);

        } else {
            //Si no se ha rellenado o se ha vaciado el titulo o el cuerpo
            Toast.makeText(EditarDiario.this, "Debes rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
        }
    }

    //Metodo que se emplea el boton BORRAR
    public void onClickBorrar(View v){

        //Inicializamos la cola
        RequestQueue rq = Volley.newRequestQueue(EditarDiario.this);

        //Indicamos la url a la que se va a realizar la peticion
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/diarios.php", new Response.Listener<String>() {
            @Override //En caso de exito
            public void onResponse(String response) {
                String respuesta=response;

                //Volvemos a la lista de diarios
                Toast.makeText(EditarDiario.this, respuesta, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ListaDiarios.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override //En caso de error
            public void onErrorResponse(VolleyError error) {

                //Se notiofica al usuarios del error
                Toast.makeText(EditarDiario.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Definicion de parametros necesarios para realizar la tarea
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("tarea", "borrar");
                parametros.put("usuario", usuario);
                parametros.put("fecha", fecha);
                return parametros;
            }
        };

        //Enviamos la peticion
        rq = Volley.newRequestQueue(EditarDiario.this);
        rq.add(sr);
    }

    //Si pulsamos atras
    public void onBackPressed() {
        //Volvemos a la lista de diarios
        Intent intent = new Intent(this, ListaDiarios.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        finish();
    }
}