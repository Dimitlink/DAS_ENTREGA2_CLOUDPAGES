package com.example.cloudpages.listadoDiarios;

import android.graphics.Bitmap;

import java.io.Serializable;

public class DiarioData implements Serializable {
    private  String usuario;

    private String titulo;
    private String cuerpo;
    private String fecha;
    private Bitmap imagen;

    //Clase usada para organizar y almacenar datos de los diarios para posteriormente cargarlos en la ListView
    public DiarioData(String usuario, String titulo, String cuerpo, String fecha, Bitmap imagen) {
        //Atributos de un diario
        this.usuario = usuario;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.fecha = fecha;
        this.imagen = imagen;
    }

    //Metodos getters y setters generados automaticamente necesarios para la insercion de datos a los items de la ListView
    public DiarioData(){}

    public String getUsuario() {
        return usuario;
    }


    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
    this.imagen = imagen;
    }
}
