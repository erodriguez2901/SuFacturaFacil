package com.facturasff.sff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OpcionesDescargaAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList datos;
    private TextView Nombre, Puesto;
    private ImageView Imagen;
    private  ListView ListView;
    MainActivity pantalla;
    CallMethodFromAdapter listener;

    public OpcionesDescargaAdapter(Context context1, ArrayList arraylist, ListView listView, CallMethodFromAdapter listener) {
        super(context1, R.layout.item_opcionesdescarga, arraylist);
        context = context1;
        datos = arraylist;
        ListView=listView;
        pantalla= new MainActivity();
        this.listener = listener;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_opcionesdescarga, null);
        bindViews(view);
        setInformation(i);
        Imagen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int pos = (int)arg0.getTag();
                listener.mtdResultado(String.valueOf(pos));
                OpcionesDescargaAdapter.this.notifyDataSetChanged();
                //new setListViewHeightBasedOnItems(ListView);
            }
        });

        return view;
    }

    public void bindViews(View view) {
        Nombre =  view.findViewById(R.id.txtNombre);
        Imagen =  view.findViewById(R.id.imgClose);
    }

    public void setInformation(int i) {
        String desc= ((DownloadInfo) datos.get(i)).getDownloadName();
        String recurso="mipmap";
        int res_imagen=1;
        if(desc.endsWith("pdf")) {
             res_imagen = context.getResources().getIdentifier("filepdf", recurso,context.getPackageName());
        }if(desc.endsWith("xlsx")) {
             res_imagen = context.getResources().getIdentifier("filexlsx", recurso,context.getPackageName());
        }if(desc.endsWith("xml")) {
             res_imagen = context.getResources().getIdentifier("filexml", recurso,context.getPackageName());
        }

        Nombre.setText(desc);
        Imagen.setBackgroundResource(res_imagen);
        Imagen.setTag(i);
    }
}
