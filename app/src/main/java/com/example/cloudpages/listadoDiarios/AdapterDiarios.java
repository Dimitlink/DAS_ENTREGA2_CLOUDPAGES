package com.example.cloudpages.listadoDiarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.cloudpages.R;

import java.util.ArrayList;

public class AdapterDiarios extends ArrayAdapter {

    private Context contextA;

    private int resurceA;

    private ArrayList<DiarioData> listaDiarios;

    //Clase que hace la funciond de adapter entre la clase DiariosData y la listview de la lista
    public AdapterDiarios(@NonNull Context context, int resource, @NonNull ArrayList<DiarioData> objects) {
        super(context, resource, objects);

        this.contextA = context;
        this.resurceA = resource;
        this.listaDiarios = objects;
    }

    @NonNull
    @Override //Obtenemos la View del item
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(contextA);

        //Asignamos al view a la listview
        convertView = layoutInflater.inflate(resurceA,parent, false);

        //Asignamos a la view elementos necesarios para visualizar el diario
        ImageView imagen = (ImageView) convertView.findViewById(R.id.i_diario_lista);
        TextView titulo = (TextView) convertView.findViewById(R.id.t_titulo_lista);
        TextView fecha = (TextView) convertView.findViewById(R.id.t_fecha_lista);
        TextView cuerpo = (TextView) convertView.findViewById(R.id.t_cuerpo_lista);

        //Cargamos los datos en ellos
        imagen.setImageBitmap(listaDiarios.get(position).getImagen());
        titulo.setText(listaDiarios.get(position).getTitulo());
        cuerpo.setText(listaDiarios.get(position).getCuerpo());
        fecha.setText(listaDiarios.get(position).getFecha());

        //Devolvemos la view completada
        return convertView;
    }
}
