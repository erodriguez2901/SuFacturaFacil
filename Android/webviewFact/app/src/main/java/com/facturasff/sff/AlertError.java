package com.facturasff.sff;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlertError {
    public View Dialogo;
    public Dialog AlertErrordialog;
    private String Type="Error";
    public Button btnCancelar;
    public Button btnAceptar;

    public AlertError(Context context ){
        Dialogo = LayoutInflater.from(context).inflate(R.layout.alerta_descarga, null);
        AlertErrordialog=new Dialog(context);
        AlertErrordialog.setContentView(Dialogo);
        AlertErrordialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics displaymetrics = new DisplayMetrics();
        AlertErrordialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        AlertErrordialog.setCanceledOnTouchOutside(false);
        AlertErrordialog.setCancelable(false);
        WindowManager.LayoutParams params = AlertErrordialog.getWindow().getAttributes();

        params.width = (int) (displaymetrics.widthPixels * 0.95);
        params.gravity = Gravity.CENTER;
        AlertErrordialog.getWindow().setAttributes(params);

    }
    public void Show (String Type,String Texto){
        AlertErrordialog.show();
        TextView TextoDialogo = Dialogo.findViewById(R.id.txtTextoDialogo);
        TextView TextoTitulo = Dialogo.findViewById(R.id.txtSolicitud);
        btnCancelar = Dialogo.findViewById(R.id.btnCancelar);
        btnCancelar.setVisibility(View.GONE);
        btnAceptar = Dialogo.findViewById(R.id.btnAceptar);
        RelativeLayout lyheader = Dialogo.findViewById(R.id.lyheader);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                AlertErrordialog.cancel();
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                AlertErrordialog.cancel();
            }
        });
        //TextoDialogo.setText(Texto);
        if (Type.compareTo("Error")==0){

        }if (Type.compareTo("Success")==0){
            lyheader.setBackgroundResource( R.drawable.headerdialogsucces);
        }if (Type.compareTo("Confirmacion")==0){
            lyheader.setBackgroundResource( R.drawable.headerdialogsucces);
        }if(Type.compareTo("Error")!=0 &&  Type.compareTo("Success")!=0 && Type.compareTo("Confirmacion")!=0) {
            lyheader.setBackgroundResource( R.drawable.headerdialogsucces);
            TextoTitulo.setText(Type);
        }
    }
}
