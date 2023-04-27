package com.example.cloudpages.Conexiones;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ConexionUsuarios extends Worker {
    public ConexionUsuarios(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    public Result doWork() {

        //Definimos la URL a la que se va a realizar la peticion
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/usuarios.php";
        String result = "";
        HttpURLConnection urlConnection = null;
        try
        {
            //Definimos la conexion
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            String tarea = getInputData().getString("tarea");
            String usuario = getInputData().getString("usuario");
            String password = getInputData().getString("password");

            //Anadmios los parametros a la URL
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("tarea", tarea)
                    .appendQueryParameter("usuario", usuario)
                    .appendQueryParameter("password", password);
            String parametros = builder.build().getEncodedQuery();

            //Definimos el metodo y el tipo de conetnido
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Obtenemos la respuesta
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();


            int statusCode = urlConnection.getResponseCode();

            //Si la conexion se ha realizado correctamente
            if (statusCode == 200){

                //Guardamos la respuesta en la variable result
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
            } else {
                Log.d("Error", "Not 200");

            }

        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e){
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        //Devolvemos el resultado de la peticion
        Data resultadoDevuelto = new Data.Builder()
                .putString("resultado", result)
                .build();

        return Result.success(resultadoDevuelto);
    }
}
