package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FlujoPantallas {
    Intent pantalla;
    public Intent flujo(String valBD, Context context){
        if(valBD.equals("")){
            pantalla = new Intent(context, Login.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, ListaClientes.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, DetalleCliente.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, MenuOpciones.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, Espacios.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, Categorias.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, Participaciones.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, Estandares.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, SKUs.class);
        } else if(valBD.equals("")){
            pantalla = new Intent(context, Activaciones.class);
        } else {
            String texto = context.getString(R.string.error_flujo);
            Toast.makeText(context,texto, Toast.LENGTH_SHORT).show();
        }
        return pantalla;
    }
}