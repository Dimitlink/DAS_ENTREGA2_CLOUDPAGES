package com.example.cloudpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cloudpages.listadoDiarios.AdapterDiarios;
import com.example.cloudpages.listadoDiarios.DiarioData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ListaDiarios extends AppCompatActivity {

    static String usuario;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_diarios);


        //Obtenemos los extras de la actividad anterior
        Log.d("usuario en la lista de diarios",this.getIntent().getExtras().getString("usuario"));
        usuario = this.getIntent().getExtras().getString("usuario");

        //Asignamos a la actividad un listView que permite ver los diarios guardados
        listView = (ListView) findViewById(R.id.listaDiarios);
        //Rellenamos la listview
        cargarDiarios();

    }

    //Metodo empleado para cargar los diarios en la listview
    private void cargarDiarios() {

        //Documentacion de volley : https://google.github.io/volley/
        ArrayList<DiarioData> listaDiarios =  new ArrayList<DiarioData>();

        //Inicializamos la cola de peticiones
        RequestQueue rq = Volley.newRequestQueue(ListaDiarios.this);

        //Definimos la URL a la que se va a hacer peticiones
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/diarios.php", new Response.Listener<String>() {
            @Override //En caso de exito
            public void onResponse(String response) {

                String respuesta=response;
                ArrayList<DiarioData> listaDatos = new ArrayList<DiarioData>();
                Log.d("respuesta", respuesta);
                try {
                    //Convertimos la respuesta en JSON para poder recorrerla
                    JSONArray json = new JSONArray(respuesta);

                        //Guardamos los datos para cada diario
                        for (int i = 0; i < json.length(); i++) {

                            String fecha = json.getJSONObject(i).getString("fecha");
                            String titulo = json.getJSONObject(i).getString("titulo");
                            String cuerpo = json.getJSONObject(i).getString("cuerpo");
                            String foto = json.getJSONObject(i).getString("foto");

                            byte[] fotoDecoded = Base64.decode(foto.getBytes("UTF-8"), Base64.DEFAULT);

                            DiarioData diario = new DiarioData(usuario, titulo, cuerpo, fecha, BitmapFactory.decodeByteArray(fotoDecoded, 0, fotoDecoded.length));
                            System.out.println("---------------------------------------------");
                            System.out.print(diario.getCuerpo());
                            System.out.println("---------------------------------------------");
                            listaDatos.add(diario);

                            for (int j = 0; j < listaDatos.size(); j++) {
                                System.out.print(listaDatos.get(j).getUsuario());
                            }
                        }

                        //Plasmamos los datos en el listview
                        AdapterDiarios adapter = new AdapterDiarios(ListaDiarios.this, R.layout.row, listaDatos);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override //Metodo que se activa cuando se pulsa un diario
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //Pasamos al editor del diario todos los datos del diario pulsado
                                Intent intent = new Intent(getApplicationContext(), EditarDiario.class);

                                intent.putExtra("usuario", usuario);
                                intent.putExtra("fecha", listaDatos.get(position).getFecha());
                                intent.putExtra("titulo", listaDatos.get(position).getTitulo());
                                intent.putExtra("cuerpo", listaDatos.get(position).getCuerpo());

                                Bitmap bitmap = listaDatos.get(position).getImagen();
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Comprimir a formato PNG con calidad 100


                                //Iniciamos la edicion del diario
                                byte[] byteArray = outputStream.toByteArray();
                                Log.d("foto", Arrays.toString(byteArray));
                                intent.putExtra("foto", byteArray);
                                startActivity(intent);
                                finish();
                            }
                        });


                    } catch(JSONException e){

                    } catch(UnsupportedEncodingException e){
                        throw new RuntimeException(e);
                    }



            }
        }, new Response.ErrorListener() {
            @Override //En caso de error
            public void onErrorResponse(VolleyError error) {
                //Notificamos al usuario
                Toast.makeText(ListaDiarios.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Definicion de los parametros necesarios para realizar la peticion

                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("tarea", "listar");
                parametros.put("usuario", usuario);
                return parametros;
            }
        };

        //Enviamos la peticion
        rq = Volley.newRequestQueue(ListaDiarios.this);
        rq.add(sr);



    }

    @Override //Metodo que se activa cuando el usaurio pulsa atras
    public void onBackPressed() {

        //Volvemos al menu principal
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        finish();
    }
}