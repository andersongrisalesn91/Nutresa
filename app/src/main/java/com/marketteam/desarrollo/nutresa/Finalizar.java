package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class Finalizar {

    Context contexto;
    ConsultaGeneral conGen;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;

    public Finalizar(Context context) {
        this.contexto = context;
        this.conGen = new ConsultaGeneral();
        this.operaciones = new OperacionesBDInterna(context);
        this.fg = new FuncionesGenerales(contexto);
    }

    public boolean verificarDatos() {
        boolean completado = true;
        //Verificar que esté terminado lo de generales - incluso obse rvaciones
        String queryFoto1 = "SELECT count(tft.ref) as cf FROM tft where ref='FACHADA'";
        ArrayList<String>[] objFotoF = conGen.queryObjeto2val(contexto, queryFoto1, null);
        if (objFotoF != null) {
            String queryFoto2 = "SELECT count(tft.ref) as cf FROM tft where ref='SELFIE'";
            ArrayList<String>[] objFotoS = conGen.queryObjeto2val(contexto, queryFoto2, null);
            if (objFotoS != null) {
                String queryObs = "select length(observaciones) as lo from t1t";
                ArrayList<String>[] objObv = conGen.queryObjeto2val(contexto, queryObs, null);
                if (objObv != null) {
                        String foto1 = objFotoF[0].get(0);
                        String foto2 = objFotoS[0].get(0);
                        String observ = objObv[0].get(0);
                        if (observ != null) {
                                if ((foto1.equals("0")) || (foto2.equals("0")) || (Integer.parseInt(observ) < 10)) {
                                    completado = false;
                                }
                        } else {
                            completado = false;
                        }

                } else {
                    completado = false;
                }
            } else {
                completado = false;
            }
        } else {
            completado = false;
        }
        return completado;
    }


    public void finAuditoria(int caso, double[] coord) {
        //Poner barra o círculo de progreso
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(contexto, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        String queryidr = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idraz = conGen.queryObjeto(contexto, queryidr, new String[]{"IDR"});
        String querynor = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] nraz = conGen.queryObjeto(contexto, querynor, new String[]{"NOMR"});
        String querypau = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] pate = conGen.queryObjeto(contexto, querypau, new String[]{"PATEND"});
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        StringTokenizer st = new StringTokenizer(fecha, " ");
        String fechaC = st.nextToken();
        String horaC = st.nextToken();
        operaciones.queryNoData("UPDATE t1t SET fecha_fin='" + fechaC + "'");
        operaciones.queryNoData("UPDATE t1t SET hora_fin='" + horaC + "'");
        try {
            if (caso == 2) {
                operaciones.queryNoData("INSERT INTO tr (encuesta,maquina,fecha_ini ,fecha_fin ,hora_ini ,hora_fin ,gpslat_ini ,gpslon_ini ,gpslat_fin ,gpslon_fin ,usuario,sincronizada,cliente_t,cliente_n ,observaciones ,fcr) SELECT encuesta,maquina,fecha_ini ,fecha_fin ,hora_ini ,hora_fin ,gpslat_ini ,gpslon_ini ,gpslat_fin ,gpslon_fin ,usuario,sincronizada,cliente_t,cliente_n ,observaciones ,fcr FROM t1t");
                operaciones.queryNoData("UPDATE tr SET idrazon='" + idraz[0].get(0) + "' WHERE encuesta='" + idAud + "'");
                operaciones.queryNoData("UPDATE tr SET nrazon='" + nraz[0].get(0) + "' WHERE encuesta='" + idAud + "'");
                operaciones.queryNoData("UPDATE tr SET peratendio='" + pate[0].get(0) + "' WHERE encuesta='" + idAud + "'");
                operaciones.queryNoData("UPDATE tr SET gpslat_fin='" + coord[0] + "' WHERE encuesta='" + idAud + "'");
                operaciones.queryNoData("UPDATE tr SET gpslon_fin='" + coord[1] + "' WHERE encuesta='" + idAud + "'");
            } else {
                operaciones.queryNoData("INSERT INTO t1 ('encuesta','maquina' ,'fecha_ini' ,'fecha_fin' ,'hora_ini' ,'hora_fin' ,'gpslat_ini' ,'gpslon_ini' ,'gpslat_fin' ,'gpslon_fin' ,'fecha_sinc' ,'hora_sinc' ,'usuario' ,'sup_tel1' ,'sup_tel2' ,'sup_tel3' ,'sup_pre1' ,'sup_pre2' ,'sup_pre3' ,'estado','entregada','sincronizada' ,'cliente_t' ,'cliente_n' ,'maestro1' ,'maestro2' ,'maestro3' ,'maestro4' ,'maestro5' ,'maestro6' ,'maestro7' ,'maestro8' ,'maestro9' ,'maestro10' ,'maestro11' ,'maestro12' ,'mconfir1' ,'mconfir2' ,'mconfir3' ,'mconfir4' ,'mconfir5' ,'mconfir6' ,'mconfir7' ,'mconfir8' ,'mconfir9' ,'mconfir10' ,'mconfir11' ,'mconfir12' ,'observaciones' ,'fcr','_ID') SELECT * FROM t1t");
            }
        } catch (Exception exq) {
            System.out.println("No Inserto T1");
        } finally {
            String estado = "";
            if (caso == 1) {
                //Aplazada
                estado = "1";
            } else if (caso == 3) {
                //Aplazada
                estado = "2";
            }
            if (caso != 2) {
                operaciones.queryNoData("UPDATE t1 SET estado='" + estado + "' WHERE encuesta='" + idAud + "'");
            }
        }
        if (caso != 2) {
            operaciones.queryNoData("UPDATE t1 SET gpslat_fin='" + coord[0] + "' WHERE encuesta='" + idAud + "'");
            operaciones.queryNoData("UPDATE t1 SET gpslon_fin='" + coord[1] + "' WHERE encuesta='" + idAud + "'");
            operaciones.queryNoData("INSERT INTO t2 ('encuesta', 'esp', 'rta', 'eval', 'fcr', '_ID') SELECT * FROM t2t");
            operaciones.queryNoData("INSERT INTO t3 ('encuesta','esp','cat','rta','eval','fcr','_ID') SELECT * FROM t3t");
            operaciones.queryNoData("INSERT INTO t4 ('encuesta','esp','cat','est','rta','fcr','_ID') SELECT * FROM t4t");
            operaciones.queryNoData("INSERT INTO t5 ('encuesta','esp','cat','sku','rta','fcr','_ID') SELECT * FROM t5t");
            operaciones.queryNoData("INSERT INTO t6 ('encuesta','id','esp','cat','ncat','rta','cant','tipo','fcr','_ID') SELECT * FROM t6t");
            operaciones.queryNoData("INSERT INTO t7 ('encuesta','cat','part','ep','part_t','part_n','cumple','fcr','_ID') SELECT * FROM t7t");
            operaciones.queryNoData("INSERT INTO t8 ('encuesta','orden','idpreg','preg','optn','optt','rta','aplica','fcr','_ID','gr') SELECT * FROM t8t");
            operaciones.queryNoData("INSERT INTO t9 ('encuesta','cat','sku','prec','fcr','_ID') SELECT * FROM t9t");
            operaciones.queryNoData("INSERT INTO t10 ('encuesta','cat','mar','sku','nsku','rta','mis','fcr','_ID','foto') SELECT * FROM t10t");
            operaciones.queryNoData("INSERT INTO t11 ('encuesta', 'codb', 'ida', 'nact', 'rta','coin', 'fcr' , '_ID','foto') SELECT * FROM t11t");
        }
        operaciones.queryNoData("INSERT INTO tf ('encuesta','path','ref','esp','cat','preg','obs','_ID','sinc') SELECT * FROM tft");
        //Limpiar tablas temporales
        operaciones.queryNoData("DELETE FROM t1t");
        try {
            operaciones.queryNoData("DROP TABLE t2t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t2t (encuesta TEXT NOT NULL, esp INTEGER, rta INTEGER, eval INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT)");
        }
        try {
            operaciones.queryNoData("DROP TABLE t3t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t3t ( encuesta TEXT NOT NULL, esp INTEGER, cat INTEGER, rta INTEGER, eval INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t4t");
        } finally {
            operaciones.queryNoData("CREATE TABLE `t4t` ( `encuesta` TEXT NOT NULL, `esp` INTEGER, `cat` INTEGER, `est`INTEGER, `rta` INTEGER, `fcr`TEXT, `_ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT );");
        }
        try {
            operaciones.queryNoData("DROP TABLE t5t");
        } finally {
            operaciones.queryNoData("CREATE TABLE  t5t  ( encuesta TEXT NOT NULL, esp INTEGER, cat INTEGER, sku INTEGER, rta INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t6t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t6t ( encuesta TEXT NOT NULL, id INTEGER, esp INTEGER, cat INTEGER, ncat TEXT, rta INTEGER, cant INTEGER, tipo INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT );");
        }
        try {
            operaciones.queryNoData("DROP TABLE t7t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t7t ( encuesta TEXT NOT NULL, cat INTEGER, part INTEGER, ep INTEGER, part_t INTEGER, part_n INTEGER, cumple INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t8t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t8t ( encuesta TEXT NOT NULL, orden INTEGER, idpreg TEXT, preg TEXT, optn INTEGER, optt TEXT, rta TEXT, aplica INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,gr INTEGER);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t9t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t9t ( encuesta TEXT NOT NULL , cat INTEGER NOT NULL , sku INTEGER NOT NULL , prec INTEGER, fcr INTEGER, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t10t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t10t (  encuesta TEXT NOT NULL,  cat INTEGER NOT NULL,  mar INTEGER NOT NULL,  sku INTEGER NOT NULL,  nsku TEXT,  rta INTEGER,  mis INTEGER,  fcr TEXT,  _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT  , foto TEXT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t11t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t11t ( encuesta TEXT NOT NULL, codb TEXT, ida INTEGER, nact TEXT, rta INTEGER, coin INTEGER, fcr TEXT, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , foto TEXT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE tft");
        } finally {
            operaciones.queryNoData("CREATE TABLE tft ( encuesta TEXT NOT NULL, path TEXT NOT NULL, ref TEXT NOT NULL, esp TEXT, cat TEXT, preg TEXT, obs TEXT, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , sinc TEXT);");
        }
        operaciones.queryNoData("UPDATE 'ME' SET EV=0");
        operaciones.queryNoData("UPDATE '302_PREGGEN' SET aplica=0,eval=0,fto=0");
        operaciones.queryNoData("UPDATE '302_PREGGRU' SET ev=0");
        operaciones.queryNoData("UPDATE 'ACT' SET VAL='0'");
        operaciones.queryNoData("UPDATE 'PA' SET va='0' where pa='ultPTA'");
        operaciones.queryNoData("UPDATE 'PA' SET va='0' where pa='CVAL'");

        operaciones.queryNoData("UPDATE 'ACT' SET VAL='2' where VA='CONESP'");
        operaciones.close();
        //Ir a menú inicio
        try {
            fg.BorrarBaseBackup();
        } catch (Exception ex){
            Log.i("No hay db para borrar: ", ex.toString());
        }
        try {
            fg.backupdDatabase();
        } finally {
            Toast.makeText(contexto, "AUDITORIA FINALIZADA", Toast.LENGTH_LONG).show();
            Intent menu = new Intent(contexto, SeleccionCuestionario.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contexto.startActivity(menu);
        }
    }

    public void finCerrar() {
        operaciones.queryNoData("DELETE FROM t1t");
        try {
            operaciones.queryNoData("DROP TABLE t2t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t2t (encuesta TEXT NOT NULL, esp INTEGER, rta INTEGER, eval INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT)");
        }
        try {
            operaciones.queryNoData("DROP TABLE t3t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t3t ( encuesta TEXT NOT NULL, esp INTEGER, cat INTEGER, rta INTEGER, eval INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t4t");
        } finally {
            operaciones.queryNoData("CREATE TABLE `t4t` ( `encuesta` TEXT NOT NULL, `esp` INTEGER, `cat` INTEGER, `est`INTEGER, `rta` INTEGER, `fcr`TEXT, `_ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT );");
        }
        try {
            operaciones.queryNoData("DROP TABLE t5t");
        } finally {
            operaciones.queryNoData("CREATE TABLE  t5t  ( encuesta TEXT NOT NULL, esp INTEGER, cat INTEGER, sku INTEGER, rta INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t6t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t6t ( encuesta TEXT NOT NULL, id INTEGER, esp INTEGER, cat INTEGER, ncat TEXT, rta INTEGER, cant INTEGER, tipo INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT );");
        }
        try {
            operaciones.queryNoData("DROP TABLE t7t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t7t ( encuesta TEXT NOT NULL, cat INTEGER, part INTEGER, ep INTEGER, part_t INTEGER, part_n INTEGER, cumple INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t8t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t8t ( encuesta TEXT NOT NULL, orden INTEGER, idpreg TEXT, preg TEXT, optn INTEGER, optt TEXT, rta TEXT, aplica INTEGER, fcr TEXT NOT NULL, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,gr INTEGER);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t9t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t9t ( encuesta TEXT NOT NULL , cat INTEGER NOT NULL , sku INTEGER NOT NULL , prec INTEGER, fcr INTEGER, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t10t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t10t (  encuesta TEXT NOT NULL,  cat INTEGER NOT NULL,  mar INTEGER NOT NULL,  sku INTEGER NOT NULL,  nsku TEXT,  rta INTEGER,  mis INTEGER,  fcr TEXT,  _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , foto TEXT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE t11t");
        } finally {
            operaciones.queryNoData("CREATE TABLE t11t ( encuesta TEXT NOT NULL, codb TEXT, ida INTEGER, nact TEXT, rta INTEGER, coin INTEGER, fcr TEXT, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , foto TEXT);");
        }
        try {
            operaciones.queryNoData("DROP TABLE tft");
        } finally {
            operaciones.queryNoData("CREATE TABLE tft ( encuesta TEXT NOT NULL, path TEXT NOT NULL, ref TEXT NOT NULL, esp TEXT, cat TEXT, preg TEXT, obs TEXT, _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,sinc TEXT );");
        }
        operaciones.queryNoData("UPDATE 'ME' SET EV=0");
        operaciones.queryNoData("UPDATE '302_PREGGEN' SET aplica=0,eval=0,fto=0");
        operaciones.queryNoData("UPDATE '302_PREGGRU' SET ev=0");
        operaciones.queryNoData("UPDATE 'PA' SET va='0' where pa='ultPTA'");
        operaciones.queryNoData("UPDATE 'PA' SET va='0' where pa='CVAL'");
        operaciones.queryNoData("UPDATE 'ACT' SET VAL='0'");
        operaciones.queryNoData("UPDATE 'ACT' SET VAL='2' where VA='CONESP'");
        operaciones.close();
        try {
            fg.BorrarBaseBackup();
        } catch (Exception ex){
            Log.i("No hay db para borrar: ", ex.toString());
        }
        try {
            fg.backupdDatabase();
        } finally {
            Toast.makeText(contexto, "AUDITORIA FINALIZADA", Toast.LENGTH_LONG).show();
            Intent menu = new Intent(contexto, SeleccionCuestionario.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contexto.startActivity(menu);
        }
    }
}