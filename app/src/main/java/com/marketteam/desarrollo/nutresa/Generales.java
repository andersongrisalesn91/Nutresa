package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

public class Generales extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, idt2,canalAct, subcAct,espAct, catAct;
    Button buttonFF, buttonTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generales);
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onStart() {
        super.onStart();
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        fg = new FuncionesGenerales(getBaseContext());
        at = new ActualizarTablas(getBaseContext());
        idC = fg.clienteActual();
        fg.ultimaPantalla("Generales");
        buttonFF = (Button) findViewById(R.id.buttonFotoFachada);
        buttonTS = (Button) findViewById(R.id.buttonSelfie);
        buttonFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {tomarFoto(1);}
        });
        buttonTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {tomarFoto(2);}
        });
        ArrayList<String> resCampos = fg.existeACT(4);
        idt2 = resCampos.get(0);
        canalAct = resCampos.get(1);
        subcAct = resCampos.get(2);
        espAct = resCampos.get(3);
        catAct = resCampos.get(4);
    }

    public void tomarFoto(int acc){
        if(acc == 1){
            operaciones.queryNoData("UPDATE ACT SET VAL='4' WHERE VA='FOTO'");
        } else if(acc == 2){
            operaciones.queryNoData("UPDATE ACT SET VAL='5' WHERE VA='FOTO'");
        }
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this,TomarFoto.class));
    }

    public void Observaciones(View v){
        //Cuenta como hecha si tiene más de 5 caracteres
        //Guardar acá de una vez (Botón)
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popUp = inflater.inflate(R.layout.ver_observaciones,null);
        String queryObs = "SELECT observaciones FROM t1t" ;
        ArrayList<String>[] observx = conGen.queryObjeto2val(getBaseContext(),queryObs,null);
        String obs = observx[0].get(0);
        final EditText observ = (EditText) popUp.findViewById(R.id.textoPass);
        observ.setText(obs);
        Button desc = (Button) popUp.findViewById(R.id.buttonObsvNo);
        Button guardar = (Button) popUp.findViewById(R.id.buttonObsvSi);
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,true);
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { popupWindow.dismiss(); }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = observ.getText().toString();
                if((texto.equals("")) || (texto == null) || (texto.length() < 15)){
                    //Texto inválido
                    Toast.makeText(getBaseContext(),"El texto válido debe tener una longitud mayor a 15 caracteres", Toast.LENGTH_LONG).show();
                } else {
                    //Guardar observaciones
                    //Actualizar campo
                    operaciones.queryNoData("UPDATE t1t SET observaciones='" + texto + "'");
                    operaciones.queryNoData("UPDATE ACT SET VAL='1' where VA='OBSV'");

                    try {
                        String queryCli = "SELECT VAL FROM ACT WHERE VA=?";
                        ArrayList<String>[] objeto = conGen.queryObjeto(getBaseContext(),queryCli,new String[]{"PANT"});
                        Irfg(objeto[0].get(0));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void Volver(View v){
        Intent menu = new Intent(this, MenuOpciones.class);
        startActivity(menu);
    }

    public void Irfg(String pantalla){
        Intent p;
        switch (pantalla){
            case "ListaClientes":
                p = new Intent(this, ListaClientes.class);
                startActivity(p);
                break;
            case "DetalleCliente":
                p = new Intent(this, DetalleCliente.class);
                startActivity(p);
                break;
            case "Menu":
                p = new Intent(this, MenuOpciones.class);
                startActivity(p);
                break;
            case "Espacios":
                p = new Intent(this, Espacios.class);
                startActivity(p);
                break;
            case "Categorías":
                p = new Intent(this, Categorias.class);
                startActivity(p);
                break;
            case "Estándares":
                p = new Intent(this, Estandares.class);
                startActivity(p);
                break;
            case "SKUs":
                p = new Intent(this, SKUs.class);
                startActivity(p);
                break;
            case "Generales":
                p = new Intent(this, Generales.class);
                startActivity(p);
                break;
            case "Activaciones":
                p = new Intent(this, Activaciones.class);
                startActivity(p);
                break;
            case "Activacionesrd":
                p = new Intent(this, Activacionesrd.class);
                startActivity(p);
                break;
            case "Activacionesrdsn":
                p = new Intent(this, Activacionesrdsn.class);
                startActivity(p);
                break;
            case "Activacionestb":
                p = new Intent(this, Activacionestb.class);
                startActivity(p);
                break;
            case "Activacionestbt":
                p = new Intent(this, Activacionestbt.class);
                startActivity(p);
                break;
            case "Partic":
                p = new Intent(this, Participaciones.class);
                startActivity(p);
                break;
            case "MenuI":
                p = new Intent(this, MenuInicio.class);
                startActivity(p);
                break;
            case "Activos":
                p = new Intent(this, Activos.class);
                startActivity(p);
                break;
            case "ConteoEspacios":
                p = new Intent(this, ConteoEspacios.class);
                startActivity(p);
                break;
            case "SKUCalidad":
                p = new Intent(this, SKUCalidad.class);
                startActivity(p);
                break;
            case "SelCuest":
                p = new Intent(this, SeleccionCuestionario.class);
                startActivity(p);
                break;
            case "Premios1":
                p = new Intent(this, Premios1.class);
                startActivity(p);
                break;
            case "Premios2":
                p = new Intent(this, Premios2.class);
                startActivity(p);
                break;
            case "Sincron":
                p = new Intent(this, Sincronizar.class);
                startActivity(p);
                break;
            case "Validaciones":
                p = new Intent(this, Validaciones.class);
                startActivity(p);
                break;
            case "TipoAMenu":
                p = new Intent(this, TipoAMenu.class);
                startActivity(p);
                break;
            case "TipoASMenu":
                p = new Intent(this, TipoAMenu.class);
                startActivity(p);
                break;
        }
    }

    public void MenuLateral(View vista) {
        fg.MenuLateral(vista);
    }
    public void cerrarsesion(View v) {
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }
    public void irTipoA(View view) {

        String aplicaTA = fg.crearTipoA();
        Toast.makeText(getBaseContext(),"Verificando preguntas TIPOA",Toast.LENGTH_SHORT).show();
        if (aplicaTA.equals("1")) {
            fg.ultimaPantallafta("Generales");
            Intent valid = new Intent(this, TipoAMenu.class);
            startActivity(valid);
        } else {
            Toast.makeText(getBaseContext(), "No hay Preguntas TipoA Disponibles en este momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void irValidaciones(View view) {
        Toast.makeText(getBaseContext(),"Verificando Validaciones",Toast.LENGTH_SHORT).show();
        ImageButton irval = (ImageButton) view.findViewById(R.id.ibValidaciones);
        irval.setClickable(false);
        String queryVal = "select count(CE) as ce from VALID";
        ArrayList<String>[] objval = conGen.queryObjeto2val(getBaseContext(), queryVal, null);
        String cantval = objval[0].get(0);
        if (!cantval.equals("0")) {
            Intent valid = new Intent(this, Validaciones.class);
            startActivity(valid);
        } else {
            irval.setClickable(true);
            Toast.makeText(getBaseContext(), "No hay validaciones en este momento", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        operaciones.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        operaciones.close();
    }
}