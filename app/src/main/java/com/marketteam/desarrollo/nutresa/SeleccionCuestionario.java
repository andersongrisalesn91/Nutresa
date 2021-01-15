package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SeleccionCuestionario extends AppCompatActivity {

    ConsultaGeneral cGeneral;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    static SeleccionCuestionario activityA;
    TextView ssin, cont;
    Context context;
    StorageReference mStorageRef;
    Uri file;
    ProgressBar pbfotos;
    FirebaseFirestore db;
    ImageView iv;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_cuestionario);
        activityA = this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getBaseContext();
        cGeneral = new ConsultaGeneral();
        try {
            context.deleteDatabase("firestore.%5BDEFAULT%5D.nutresa-7f30b.%28default%29");
        }catch (Exception wx){
            Toast.makeText(context,"Fallo borrar la DB"+wx.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            context.deleteDatabase("firestore.%5BDEFAULT%5D.nutresa-7f30b.%28default%29-journal");
        }catch (Exception wy){
            Toast.makeText(context,"Fallo borrar la DB"+wy.getMessage(), Toast.LENGTH_SHORT).show();
        }

        operaciones = new OperacionesBDInterna(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("SelCuest");
        db = FirebaseFirestore.getInstance();
        Button iniciar = (Button) findViewById(R.id.buttonIniciarAud);
        Button iniciarC = (Button) findViewById(R.id.buttonIniciarAudC);
        Button iniciarT = (Button) findViewById(R.id.buttonIniciarAudT);
        Button iniciarB = (Button) findViewById(R.id.buttonIniciarAudB);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAuditoria();
            }
        });
        iniciarC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAuditoriac();
            }
        });
        iniciarT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAuditoriat();
            }
        });
        iniciarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAuditoriaB();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void iniciarAuditoria() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='TIPOM'");
        Intent lista = new Intent(this, MenuInicio.class);
        startActivity(lista);
    }

    public void iniciarAuditoriac() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='TIPOM'");
        Intent lista = new Intent(this, MenuInicio.class);
        startActivity(lista);
    }

    public void iniciarAuditoriat() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        operaciones.queryNoData("UPDATE ACT SET VAL='3' WHERE VA='TIPOM'");
        Intent lista = new Intent(this, MenuInicio.class);
        startActivity(lista);
    }

    public void iniciarAuditoriaB() {
        Toast.makeText(getBaseContext(),"No disponible en esta medición", Toast.LENGTH_LONG).show();

        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //operaciones.queryNoData("UPDATE ACT SET VAL='4' WHERE VA='TIPOM'");
        //Intent lista = new Intent(this, MenuInicio.class);
        //startActivity(lista);
    }

    public void cerrarSesion(View vista) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }

    public void MenuLateral(View vista) {
        fg.MenuLateral(vista);
    }

    public void irTipoA(View view) {

        String aplicaTA = fg.crearTipoA();

        if (aplicaTA.equals("1")) {
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
        ArrayList<String>[] objval = cGeneral.queryObjeto2val(getBaseContext(), queryVal, null);
        String cantval = objval[0].get(0);
        if (!cantval.equals("0")) {
            Intent valid = new Intent(this, Validaciones.class);
            startActivity(valid);
        } else {
            irval.setClickable(true);
            Toast.makeText(getBaseContext(), "No hay validaciones en este momento", Toast.LENGTH_SHORT).show();
        }
    }


    public void sincronizar(View vista) {
  //      SincronizaVF s = new SincronizaVF(getBaseContext());
  //      Toast.makeText(getBaseContext(),"Sincronizando los datos, por favor espere", Toast.LENGTH_LONG).show();
  //      s.sincronizarTablasVF();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent lista = new Intent(this, Sincronizar.class);
        startActivity(lista);
    }

    public static SeleccionCuestionario getInstance() {return activityA;}
}