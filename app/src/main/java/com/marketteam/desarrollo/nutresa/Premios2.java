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

public class Premios2 extends AppCompatActivity {

    EditText observaciones, nombre;
    TextView tvCatalogo, tvTipoS,est2,est3,est4,est5;
    Spinner spObsrv,spObsrv1,spObsrv2,spObsrv3;
    ConsultaGeneral conGen;
    String[] premios,premios1,premios2,premios3, idpremios,idpremios1,idpremios2,idpremios3;
    String premio,premio1,premio2,premio3, idpremio,idpremio1,idpremio2,idpremio3, SOCPRE, CATSOC,catalogo,tipoSocio;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    ActualizarTablas at;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premios2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        conGen = new ConsultaGeneral();
        operaciones = new OperacionesBDInterna(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        at = new ActualizarTablas(getBaseContext());
        spObsrv = (Spinner) findViewById(R.id.spinnerPremios);
        spObsrv1 = (Spinner) findViewById(R.id.spinnerPremios2);
        spObsrv2 = (Spinner) findViewById(R.id.spinnerPremios3);
        spObsrv3 = (Spinner) findViewById(R.id.spinnerPremios4);
        est2 = (TextView) findViewById(R.id.tVEst2);
        est3 = (TextView) findViewById(R.id.tVEst3);
        est4 = (TextView) findViewById(R.id.tVEst4);
        est5 = (TextView) findViewById(R.id.tVEst5);
        fg.ultimaPantalla("Sincron");
        String clienteAct = fg.clienteActual();
        tvCatalogo = (TextView) findViewById(R.id.tvCatgP2);
        tvTipoS = (TextView) findViewById(R.id.tvTipoP2);
        String querySocio = "select NSOCPRE AS SOCIO,NCATSOC AS CATALOGO from '300_MAESTRO' inner join '119_SOCPRE' on '300_MAESTRO'.CATSOC = '119_SOCPRE'.SOCPRE inner join '118_CATSOC' on '300_MAESTRO'.CLACIU = '118_CATSOC'.CATSOC where CLI='" + clienteAct + "'";
        ArrayList<String>[] socio = conGen.queryObjeto2val(getBaseContext(),querySocio,null);
        if(socio != null){
            tipoSocio = socio[0].get(0);
            catalogo = socio[0].get(1);
        }
        tvCatalogo.setText(catalogo);
        tvTipoS.setText(tipoSocio);
        String queryCPrem = "select CATSOC,CLACIU from t1t inner join '300_MAESTRO' on t1t.cliente_t  = '300_MAESTRO'.CLI where '300_MAESTRO'.CLAEST <>'1'";
        ArrayList<String>[] objCprem = conGen.queryObjeto2val(getBaseContext(), queryCPrem, null);
        SOCPRE = objCprem[0].get(0);
        CATSOC = objCprem[0].get(1);
        String queryPrem = "select '120_COMBCATSOC'.PRE,NPRE FROM '120_COMBCATSOC' inner join '117_PRE' on '120_COMBCATSOC'.PRE = '117_PRE'.PRE where SOCPRE='" + SOCPRE + "' and CATSOC='" + CATSOC + "' AND EST='2'";
        ArrayList<String>[] obj = conGen.queryObjeto2val(getBaseContext(), queryPrem, null);
        if (obj != null) {
            premios = new String[obj.length];
            idpremios = new String[obj.length];
            for (int r = 0; r < obj.length; r++) {
                idpremios[r] = obj[r].get(0);
                premios[r] = obj[r].get(1);
            }
            premio = "";
            spObsrv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, premios));
            spObsrv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    premio = premios[pos];
                    idpremio = idpremios[pos];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            est2.setVisibility(View.GONE);
            spObsrv.setVisibility(View.GONE);
        }
        String queryPrem1 = "select '120_COMBCATSOC'.PRE,NPRE FROM '120_COMBCATSOC' inner join '117_PRE' on '120_COMBCATSOC'.PRE = '117_PRE'.PRE where SOCPRE='" + SOCPRE + "' and CATSOC='" + CATSOC + "' AND EST='3'";
        ArrayList<String>[] obj1 = conGen.queryObjeto2val(getBaseContext(), queryPrem1, null);
        if (obj1 != null) {
            premios1 = new String[obj1.length];
            idpremios1 = new String[obj1.length];
            for (int r = 0; r < obj1.length; r++) {
                idpremios1[r] = obj1[r].get(0);
                premios1[r] = obj1[r].get(1);
            }
            premio1 = "";
            spObsrv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, premios1));
            spObsrv1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    premio1 = premios1[pos];
                    idpremio1 = idpremios1[pos];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            est3.setVisibility(View.GONE);
            spObsrv1.setVisibility(View.GONE);
        }
        String queryPrem2 = "select '120_COMBCATSOC'.PRE,NPRE FROM '120_COMBCATSOC' inner join '117_PRE' on '120_COMBCATSOC'.PRE = '117_PRE'.PRE where SOCPRE='" + SOCPRE + "' and CATSOC='" + CATSOC + "' AND EST='4'";
        ArrayList<String>[] obj2 = conGen.queryObjeto2val(getBaseContext(), queryPrem2, null);
        if (obj2 != null) {
            premios2 = new String[obj2.length];
            idpremios2 = new String[obj2.length];
            for (int r = 0; r < obj2.length; r++) {
                idpremios2[r] = obj2[r].get(0);
                premios2[r] = obj2[r].get(1);
            }
            premio2 = "";
            spObsrv2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, premios2));
            spObsrv2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    premio2 = premios2[pos];
                    idpremio2 = idpremios2[pos];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            est4.setVisibility(View.GONE);
            spObsrv2.setVisibility(View.GONE);
        }
        String queryPrem3 = "select '120_COMBCATSOC'.PRE,NPRE FROM '120_COMBCATSOC' inner join '117_PRE' on '120_COMBCATSOC'.PRE = '117_PRE'.PRE where SOCPRE='" + SOCPRE + "' and CATSOC='" + CATSOC + "' AND EST='5'";
        ArrayList<String>[] obj3 = conGen.queryObjeto2val(getBaseContext(), queryPrem3, null);
        if (obj3 != null) {
            premios3 = new String[obj3.length];
            idpremios3 = new String[obj3.length];
            for (int r = 0; r < obj3.length; r++) {
                idpremios3[r] = obj3[r].get(0);
                premios3[r] = obj3[r].get(1);
            }
            premio3 = "";
            spObsrv3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, premios3));
            spObsrv3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    premio3 = premios3[pos];
                    idpremio3 = idpremios3[pos];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            est5.setVisibility(View.GONE);
            spObsrv3.setVisibility(View.GONE);
        }
        operaciones.close();
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
        boolean rexist1 = false;
        boolean rexist2 = false;
        boolean rexist3 = false;
        String queryPE = "SELECT count(encuesta) as cf FROM t8t WHERE orden=501";
        ArrayList<String>[] evalPE = conGen.queryObjeto2val(getBaseContext(), queryPE, null);
        String queryPE1 = "SELECT count(encuesta) as cf FROM t8t WHERE orden=502";
        ArrayList<String>[] evalPE1 = conGen.queryObjeto2val(getBaseContext(), queryPE1, null);
        String queryPE2 = "SELECT count(encuesta) as cf FROM t8t WHERE orden=503";
        ArrayList<String>[] evalPE2 = conGen.queryObjeto2val(getBaseContext(), queryPE2, null);
        String queryPE3 = "SELECT count(encuesta) as cf FROM t8t WHERE orden=504";
        ArrayList<String>[] evalPE3 = conGen.queryObjeto2val(getBaseContext(), queryPE3, null);
        String contPE = evalPE[0].get(0);
        if (Integer.parseInt(contPE) != 0) {
            rexist = true;
        }
        String contPE1 = evalPE1[0].get(0);
        if (Integer.parseInt(contPE1) != 0) {
            rexist1 = true;
        }
        String contPE2 = evalPE2[0].get(0);
        if (Integer.parseInt(contPE2) != 0) {
            rexist2 = true;
        }
        String contPE3 = evalPE3[0].get(0);
        if (Integer.parseInt(contPE3) != 0) {
            rexist3 = true;
        }


        if (premio.equals("") || foto == false) {
            Toast.makeText(this, "Campos incompletos o falta foto", Toast.LENGTH_SHORT).show();
            operaciones.queryNoData("UPDATE 'ME' SET EV='2' WHERE ET='PREMIOST'");
        } else {
            if (rexist == false) {
                at.insertarT8("501", "PREMIOS2", "PREMIOS2", "501", "0", "3","500");
            }
            if (rexist1 == false) {
                at.insertarT8("502", "PREMIOS3", "PREMIOS3", "502", "0", "3","500");
            }
            if (rexist2 == false) {
                at.insertarT8("503", "PREMIOS4", "PREMIOS4", "503", "0", "3","500");
            }
            if (rexist3 == false) {
                at.insertarT8("504", "PREMIOS5", "PREMIOS5", "504", "0", "3","500");
            }
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREMIOST'");
            operaciones.queryNoData("UPDATE t8t SET rta='" + idpremio + "' WHERE orden=501");
            operaciones.queryNoData("UPDATE t8t SET rta='" + premio + "' WHERE orden=501");
            operaciones.queryNoData("UPDATE t8t SET rta='" + idpremio1 + "' WHERE orden=502");
            operaciones.queryNoData("UPDATE t8t SET rta='" + premio1 + "' WHERE orden=502");
            operaciones.queryNoData("UPDATE t8t SET rta='" + idpremio2 + "' WHERE orden=503");
            operaciones.queryNoData("UPDATE t8t SET rta='" + premio2 + "' WHERE orden=503");
            operaciones.queryNoData("UPDATE t8t SET rta='" + idpremio3 + "' WHERE orden=504");
            operaciones.queryNoData("UPDATE t8t SET rta='" + premio3 + "' WHERE orden=504");
            startActivity(new Intent(this, MenuOpciones.class));
        }
    }

    public void TomarFPrem(View v) {
        Toast.makeText(getBaseContext(), "Tomar Foto", Toast.LENGTH_SHORT).show();
        operaciones.queryNoData("UPDATE ACT SET VAL='8' WHERE VA='FOTO'");
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
