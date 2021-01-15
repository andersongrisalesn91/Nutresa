package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Premios1 extends AppCompatActivity {

    EditText observaciones, nombre;
    TextView tvCatalogo, tvTipoS;
    Spinner spObsrv;
    ConsultaGeneral conGen;
    String[] premios, idpremios;
    String premio, idpremio, SOCPRE, CATSOC,tipoSocio, catalogo;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    ActualizarTablas at;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premios1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        conGen = new ConsultaGeneral();
        operaciones = new OperacionesBDInterna(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        at = new ActualizarTablas(getBaseContext());
        spObsrv = (Spinner) findViewById(R.id.spinnerPremios);
        fg.ultimaPantalla("Premios1");
        String clienteAct = fg.clienteActual();
        String queryCPrem = "select CATSOC,CLACIU from t1t inner join '300_MAESTRO' on t1t.cliente_t  = '300_MAESTRO'.CLI where '300_MAESTRO'.CLAEST ='1'";
        ArrayList<String>[] objCprem = conGen.queryObjeto2val(getBaseContext(), queryCPrem, null);
        SOCPRE = objCprem[0].get(0);
        CATSOC = objCprem[0].get(1);
        String queryPrem = "select '120_COMBCATSOC'.PRE,NPRE FROM '120_COMBCATSOC' inner join '117_PRE' on '120_COMBCATSOC'.PRE = '117_PRE'.PRE where SOCPRE='" + SOCPRE + "' and CATSOC='" + CATSOC + "'";
        ArrayList<String>[] obj = conGen.queryObjeto2val(getBaseContext(), queryPrem, null);
        if (obj != null) {
            premios = new String[obj.length];
            idpremios = new String[obj.length];
            for (int r = 0; r < obj.length; r++) {
                idpremios[r] = obj[r].get(0);
                premios[r] = obj[r].get(1);
            }
        }
        tvCatalogo = (TextView) findViewById(R.id.tvCatgP1);
        tvTipoS = (TextView) findViewById(R.id.tvTipoP1);
        String querySocio = "select NSOCPRE AS SOCIO,NCATSOC AS CATALOGO from '300_MAESTRO' inner join '119_SOCPRE' on '300_MAESTRO'.CATSOC = '119_SOCPRE'.SOCPRE inner join '118_CATSOC' on '300_MAESTRO'.CLACIU = '118_CATSOC'.CATSOC where CLI='" + clienteAct + "'";
        ArrayList<String>[] socio = conGen.queryObjeto2val(getBaseContext(),querySocio,null);
        if(socio != null){
            tipoSocio = socio[0].get(0);
            catalogo = socio[0].get(1);
        }
        tvCatalogo.setText(catalogo);
        tvTipoS.setText(tipoSocio);
        operaciones.close();
        premio = "";
        spObsrv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, premios));
        spObsrv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                premio = premios[pos];
                idpremio = idpremios[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void Volver(View v) {
        startActivity(new Intent(this, MenuOpciones.class));
    }

    public void Firmar(View v) {
        startActivity(new Intent(this, signature.class));
    }

    public void Terminar(View v) {
        //Verificar si se tomo foto
        boolean foto = false;
        String queryFoto = "SELECT count(tft.esp) as cf FROM tft where ref='PREMIOS'";
        ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryFoto, null);
        String contf = eval[0].get(0);
        if (Integer.parseInt(contf) != 0) {
            foto = true;
        }
        //Verificar si existe el registro
        boolean rexist = false;
        String queryPE = "SELECT count(encuesta) as cf FROM t8t WHERE orden=500";
        ArrayList<String>[] evalPE = conGen.queryObjeto2val(getBaseContext(), queryPE, null);
        String contPE = evalPE[0].get(0);
        if (Integer.parseInt(contPE) != 0) {
            rexist = true;
        }

        if (premio.equals("") || foto == false) {
            Toast.makeText(this, "Campos incompletos o falta foto", Toast.LENGTH_SHORT).show();
            operaciones.queryNoData("UPDATE 'ME' SET EV='2' WHERE ET='PREMIOSE'");
        } else {
            if (rexist == false) {
                at.insertarT8("500", "PREMIOS1", "PREMIOS1", "500", "0", "3","500");
            }
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREMIOSE'");
            operaciones.queryNoData("UPDATE t8t SET rta='" + idpremio + "' WHERE orden=500");
            operaciones.queryNoData("UPDATE t8t SET optt='" + premio + "' WHERE orden=500");
            startActivity(new Intent(this, MenuOpciones.class));
        }
    }

    public void TomarFPrem(View v) {
        Toast.makeText(getBaseContext(), "Tomar Foto", Toast.LENGTH_SHORT).show();
        operaciones.queryNoData("UPDATE ACT SET VAL='7' WHERE VA='FOTO'");
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this, TomarFoto.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        operaciones.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        operaciones.close();
    }
}
