package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class Sincroniza {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    Context context;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    FuncionesGenerales fg;
    Uri file;
    ProgressBar pbfotos;

    public Sincroniza(Context contexto){
        this.context = contexto;
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        this.db = FirebaseFirestore.getInstance();
        this.fg = new FuncionesGenerales(contexto);

    }

    public void sincronizarTablas(){
        //Tomar los datos de cada tabla y enviarlos a tablas a Firebase
        //Traer todos los registros de cada tabla e insertarlos individualmente en Firebase
        if(Login.getInstance() != null){Login.getInstance().finish();}
        if(SeleccionCuestionario.getInstance() != null){SeleccionCuestionario.getInstance().finish();}
        if(MenuInicio.getInstance() != null){MenuInicio.getInstance().finish();}
        if(ListaClientes.getInstance() != null){ListaClientes.getInstance().finish();}
        if(DetalleCliente.getInstance() != null){DetalleCliente.getInstance().finish();}
        if(Espacios.getInstance() != null){Espacios.getInstance().finish();}
        if(Categorias.getInstance() != null){Categorias.getInstance().finish();}
        if(Activaciones.getInstance() != null){Activaciones.getInstance().finish();}
        if(Activacionesrd.getInstance() != null){Activacionesrd.getInstance().finish();}
        if(Activacionestb.getInstance() != null){Activacionestb.getInstance().finish();}
        if(Activos.getInstance() != null){Activos.getInstance().finish();}
        if(SKUCalidad.getInstance() != null){SKUCalidad.getInstance().finish();}
        VerificarConex vc = new VerificarConex();
        boolean net = vc.revisarconexión(context);
        if(net == true){
            operaciones = new OperacionesBDInterna(context);
            conGen = new ConsultaGeneral();
            try {
                sincronizarT1();
                sincronizarT2();
                sincronizarT3();
                sincronizarT4();
                sincronizarT5();
                sincronizarT6();
                sincronizarT7();
                sincronizarT8();
                sincronizarT9();
                sincronizarT10();
                sincronizarT11();
                //sincronizarTF();
                //sincronizarTR();
            }
            finally {
                Toast.makeText(context,"Sincronizacion Finalizada", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context,"No se encuentra conectado a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT1(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t1 where estado=1 and sincronizada<>1", null);
        if(registros != null){
            Toast.makeText(context,"Sincronizando .........", Toast.LENGTH_LONG).show();
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("maquina", registros[i].get(1));
                regis.put("fecha_ini", registros[i].get(2));
                regis.put("fecha_fin", registros[i].get(3));
                regis.put("hora_ini", registros[i].get(4));
                regis.put("hora_fin", registros[i].get(5));
                regis.put("gpslat_ini", registros[i].get(6));
                regis.put("gpslon_ini", registros[i].get(7));
                regis.put("gpslat_fin", registros[i].get(8));
                regis.put("gpslon_fin", registros[i].get(9));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fechaT = dateFormat.format(date);
                StringTokenizer st = new StringTokenizer(fechaT," ");
                regis.put("fecha_sinc", st.nextToken());
                regis.put("hora_sinc", st.nextToken());
                regis.put("usuario", registros[i].get(12));
                regis.put("sup_tel1", registros[i].get(13));
                regis.put("sup_tel2", registros[i].get(14));
                regis.put("sup_tel3", registros[i].get(15));
                regis.put("sup_pre1", registros[i].get(16));
                regis.put("sup_pre2", registros[i].get(17));
                regis.put("sup_pre3", registros[i].get(18));
                regis.put("estado", registros[i].get(19));
                regis.put("entregada", registros[i].get(20));
                regis.put("sincronizada", registros[i].get(21));
                regis.put("cliente_t", registros[i].get(22));
                regis.put("cliente_n", registros[i].get(23));
                regis.put("maestro1", registros[i].get(24));
                regis.put("maestro2", registros[i].get(25));
                regis.put("maestro3", registros[i].get(26));
                regis.put("maestro4", registros[i].get(27));
                regis.put("maestro5", registros[i].get(28));
                regis.put("maestro6", registros[i].get(29));
                regis.put("maestro7", registros[i].get(30));
                regis.put("maestro8", registros[i].get(31));
                regis.put("maestro9", registros[i].get(32));
                regis.put("maestro10", registros[i].get(33));
                regis.put("maestro11", registros[i].get(34));
                regis.put("maestro12", registros[i].get(35));
                regis.put("mconfir1", registros[i].get(36));
                regis.put("mconfir2", registros[i].get(37));
                regis.put("mconfir3", registros[i].get(38));
                regis.put("mconfir4", registros[i].get(39));
                regis.put("mconfir5", registros[i].get(40));
                regis.put("mconfir6", registros[i].get(41));
                regis.put("mconfir7", registros[i].get(42));
                regis.put("mconfir8", registros[i].get(43));
                regis.put("mconfir9", registros[i].get(44));
                regis.put("mconfir10", registros[i].get(45));
                regis.put("mconfir11", registros[i].get(46));
                regis.put("mconfir12", registros[i].get(47));
                regis.put("observaciones", registros[i].get(48));
                regis.put("fcr", registros[i].get(49));
                regis.put("_ID", registros[i].get(50));
                regis.put("ID2", registros[i].get(51));
                try{
                    db.collection("t1").document(registros[i].get(0)).set(regis);
                } catch(Exception e){
                    //Que intente otra vez
                    final String s = registros[i].get(0);
                    db.collection("t1").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"La sincronización ha fallado en T1", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    operaciones.queryNoData("UPDATE t1 SET sincronizada='" + 1 + "' WHERE encuesta='" + registros[i].get(0) + "'");
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 1", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT2(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t2 WHERE rta<>0", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("rta", registros[i].get(2));
                regis.put("eval", registros[i].get(3));
                regis.put("fcr", registros[i].get(4));
                regis.put("_ID", registros[i].get(5));
                regis.put("ID2", registros[i].get(6));
                try{
                    db.collection("t2").document(registros[i].get(0) + registros[i].get(5)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(5);
                    db.collection("t2").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"La sincronización ha fallado en T2", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 2", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT3(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t3 WHERE rta<>0", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("rta", registros[i].get(3));
                regis.put("eval", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                try{
                    db.collection("t3").document(registros[i].get(0) + registros[i].get(6)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(6);
                    db.collection("t3").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"La sincronización ha fallado en T3", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 3", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT4(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t4 WHERE rta<>0", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                final Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("est", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                try{
                    db.collection("t4").document(registros[i].get(0) + registros[i].get(6)).set(regis);

                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(6);
                    db.collection("t4").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t4").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T4", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 4", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT5(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t5 WHERE rta<>0", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("sku", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                try{
                    db.collection("t5").document(registros[i].get(0) + registros[i].get(6)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(6);
                    db.collection("t5").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t5").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T5", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 5", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT6(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t6", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("id", registros[i].get(1));
                regis.put("esp", registros[i].get(2));
                regis.put("cat", registros[i].get(3));
                regis.put("ncat", registros[i].get(4));
                regis.put("rta", registros[i].get(5));
                regis.put("cant", registros[i].get(6));
                regis.put("tipo", registros[i].get(7));
                regis.put("fcr", registros[i].get(8));
                regis.put("_ID", registros[i].get(9));
                regis.put("ID2", registros[i].get(10));
                try{
                    db.collection("t6").document(registros[i].get(0) + registros[i].get(9)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(9);
                    db.collection("t6").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t6").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T6", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 6", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT7(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t7", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("part", registros[i].get(2));
                regis.put("ep", registros[i].get(3));
                regis.put("part_t", registros[i].get(4));
                regis.put("part_n", registros[i].get(5));
                regis.put("cumple", registros[i].get(6));
                regis.put("fcr", registros[i].get(7));
                regis.put("_ID", registros[i].get(8));
                regis.put("ID2", registros[i].get(9));
                try{
                    db.collection("t7").document(registros[i].get(0) + registros[i].get(8)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(8);
                    db.collection("t7").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t7").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T7", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 7", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT8(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t8", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("orden", registros[i].get(1));
                regis.put("idpreg", registros[i].get(2));
                regis.put("preg", registros[i].get(3));
                regis.put("optn", registros[i].get(4));
                regis.put("optt", registros[i].get(5));
                regis.put("rta", registros[i].get(6));
                regis.put("aplica", registros[i].get(7));
                regis.put("fcr", registros[i].get(8));
                regis.put("_ID", registros[i].get(9));
                regis.put("ID2", registros[i].get(10));
                try{
                    db.collection("t8").document(registros[i].get(0) + registros[i].get(10)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(10);
                    db.collection("t8").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t8").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T8", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 8", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT9(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t9", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("sku", registros[i].get(2));
                regis.put("prec", registros[i].get(3));
                regis.put("fcr", registros[i].get(4));
                regis.put("_ID", registros[i].get(5));
                regis.put("ID2", registros[i].get(6));
                try{
                    db.collection("t9").document(registros[i].get(0) + registros[i].get(5)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(5);
                    db.collection("t9").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t9").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T9", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 9", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT10(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t10", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("mar", registros[i].get(2));
                regis.put("sku", registros[i].get(3));
                regis.put("nsku", registros[i].get(4));
                regis.put("rta", registros[i].get(5));
                regis.put("mis", registros[i].get(6));
                regis.put("fcr", registros[i].get(7));
                regis.put("_ID", registros[i].get(8));
                regis.put("ID2", registros[i].get(9));
                regis.put("foto", registros[i].get(10));
                try{
                    db.collection("t10").document(registros[i].get(0) + registros[i].get(9)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(8);
                    db.collection("t10").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t10").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T10", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
           //Toast.makeText(context,"No existen registros en la tabla 10", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT11(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM t11", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("codb", registros[i].get(1));
                regis.put("ida", registros[i].get(2));
                regis.put("nact", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("coin", registros[i].get(5));
                regis.put("fcr", registros[i].get(6));
                regis.put("_ID", registros[i].get(7));
                regis.put("ID2", registros[i].get(8));
                regis.put("foto", registros[i].get(9));
                try{
                    db.collection("t11").document(registros[i].get(0) + registros[i].get(8)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(7);
                    db.collection("t11").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("t11").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en T11", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla 11", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarTF() {
        boolean fotosubida = false;
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM tf where sinc is null or sinc='0'", null);
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("path", registros[i].get(1));
                String nombre = registros[i].get(1);
                regis.put("ref", registros[i].get(2));
                regis.put("esp", registros[i].get(3));
                regis.put("cat", registros[i].get(4));
                regis.put("preg", registros[i].get(5));
                regis.put("obs", registros[i].get(6));
                regis.put("_ID", registros[i].get(7));
                regis.put("ID2", registros[i].get(8));
                try {
                    db.collection("tf").document(registros[i].get(0) + registros[i].get(7)).set(regis);
                } catch (Exception e) {
                    final String s = registros[i].get(0) + registros[i].get(7);
                    db.collection("tf").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("tf").document(s).set(cv2);
                            Toast.makeText(context, "La sincronización ha fallado en TF", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    try {
                        fotosubida = guardarFoto(registros[i].get(0), nombre);
                    } catch (Exception w) {
                        operaciones.queryNoData("UPDATE tf SET sinc='" + 0 + "' WHERE encuesta='" + registros[i].get(0) + "' and path='" + registros[i].get(1) + "';");
                    } finally {
                        if (fotosubida == true) {
                            operaciones.queryNoData("UPDATE tf SET sinc='" + 1 + "' WHERE encuesta='" + registros[i].get(0) + "' and path='" + registros[i].get(1) + "';");
                        } else {
                            operaciones.queryNoData("UPDATE tf SET sinc='" + 0 + "' WHERE encuesta='" + registros[i].get(0) + "' and path='" + registros[i].get(1) + "';");
                        }
                    }
                }
            }
        } else {
            //Toast.makeText(context, "No existen registros en la tabla de fotos", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarTR(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT * FROM tr", null);
        if(registros != null){
            for (int i = 0; i < registros.length; i++){
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("maquina", registros[i].get(1));
                regis.put("fecha_ini", registros[i].get(2));
                regis.put("fecha_fin", registros[i].get(3));
                regis.put("hora_ini", registros[i].get(4));
                String fechaC = fg.fechaActual(3);
                StringTokenizer st = new StringTokenizer(fechaC," ");
                st.nextToken();
                regis.put("hora_fin", st.nextToken());
                regis.put("gpslat_ini", registros[i].get(6));
                regis.put("gpslon_ini", registros[i].get(7));
                regis.put("gpslat_fin", registros[i].get(8));
                regis.put("gpslon_fin", registros[i].get(9));
                regis.put("usuario", registros[i].get(10));
                regis.put("sincronizada", registros[i].get(11));
                regis.put("cliente_t", registros[i].get(12));
                regis.put("cliente_n", registros[i].get(13));
                regis.put("idrazon", registros[i].get(14));
                regis.put("nrazon", registros[i].get(15));
                regis.put("peratendio", registros[i].get(16));
                regis.put("observaciones", registros[i].get(17));
                regis.put("fcr", registros[i].get(18));
                regis.put("_ID", registros[i].get(19));
                try{
                    db.collection("tr").document(registros[i].get(0) + registros[i].get(19)).set(regis);
                } catch(Exception e){
                    final String s = registros[i].get(0) + registros[i].get(19);
                    final Map<String, Object> cv2 = regis;
                    db.collection("tr").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("tr").document(s).set(cv2);
                            Toast.makeText(context,"La sincronización ha fallado en TR", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally{
                    //Llevar la cuenta de registros
                    //guardarFotosR();
                    new Thread(new Runnable() {
                        public void run() {
                            guardarFotosR();
                        }
                    }).start();
                }
            }
        } else {
            //Toast.makeText(context,"No existen registros en la tabla de rechazos", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean guardarFoto(final String idencuestafoto, final String nombreFoto) {
        VerificarConex vc = new VerificarConex();
        boolean net = vc.revisarconexión(context);
        if (net == true) {
            //Guardar la foto en Storage
            String path = Environment.getExternalStorageDirectory() + "/DCIM/multiquest/img/" + nombreFoto;
            File f = new File(path);
            if(f.exists() && f.length() > 0 && f != null){
                try {
                    file = Uri.fromFile(new File(path));
                } catch (Exception e) {
                    operaciones.queryNoData("UPDATE tf SET sinc='" + 2 + "' WHERE encuesta='" + idencuestafoto + "' and path='" + nombreFoto + "';");
                    return false;
                } finally {
                    if(!file.toString().equals("") || file.toString() != null){
                        final StorageReference picRef = mStorageRef.child("11964/img/" + nombreFoto);
                        picRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Guardar en una colección todas las url
                                String url = taskSnapshot.getMetadata().getPath();
                                Map<String, Object> regis = new HashMap<>();
                                //Poner proyecto automático
                                regis.put("PATH", url);
                                regis.put("URL", picRef.getDownloadUrl());
                                db.collection("url_fotos").document(nombreFoto).set(regis);
                            }
                        });
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            Toast.makeText(context, "No hay red disponible para cargar fotos", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void guardarFotosR(){
        ArrayList<String>[] registros = conGen.queryObjeto(context,"SELECT path FROM tf", null);
        if(registros != null){
            for(int r = 0; r < registros.length;r++){
                VerificarConex vc = new VerificarConex();
                boolean net = vc.revisarconexión(context);
                if(net == true){
                    final String nombreFoto = registros[r].get(0);
                    String path = Environment.getExternalStorageDirectory() + "/DCIM/multiquest/img/" + nombreFoto;
                    File f = new File(path);
                    if(f.exists() && f.length() > 0 && f != null){
                        Uri file = Uri.fromFile(f);
                        final StorageReference picRef = mStorageRef.child("11964/img/" + nombreFoto);
                        picRef.putFile(file).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                operaciones.queryNoData("UPDATE tf SET sinc='" + 0 + "' WHERE path='" + nombreFoto + "';");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Guardar en una colección todas las url
                                String url = taskSnapshot.getMetadata().getPath();
                                Map<String, Object> regis = new HashMap<>();
                                //Poner proyecto automático
                                regis.put("PATH", url);
                                regis.put("URL", picRef.getDownloadUrl());
                                db.collection("url_fotos").document(nombreFoto).set(regis);
                                operaciones.queryNoData("UPDATE tf SET sinc='" + 2 + "' WHERE path='" + nombreFoto + "';");
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        updateProgress(taskSnapshot);
                                    }
                                });
                    }
                } else {
                    Toast.makeText(context,"No hay red disponible para cargar fotos",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        pbfotos.setProgress((int) progress);
    }

}