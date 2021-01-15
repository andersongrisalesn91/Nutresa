package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class SincronizaVF {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    Context context;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    FuncionesGenerales fg;
    String encuestaAct;
    Uri file;
    int ct2,ct3,ct4,ct5,ct6,ct7,ct8,ct9,ct10,ct11,cttf,cttr;

    public SincronizaVF(Context contexto) {
        this.context = contexto;
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        this.db = FirebaseFirestore.getInstance();
        this.fg = new FuncionesGenerales(contexto);
    }

    public void sincronizarTablasVF() {
        if(Login.getInstance() != null){Login.getInstance().finish();}
        if(MenuInicio.getInstance() != null){MenuInicio.getInstance().finish();}
        if(ListaClientes.getInstance() != null){ListaClientes.getInstance().finish();}
        if(DetalleCliente.getInstance() != null){DetalleCliente.getInstance().finish();}
        if(Espacios.getInstance() != null){Espacios.getInstance().finish();}
        if(Categorias.getInstance() != null){Categorias.getInstance().finish();}
        if(Estandares.getInstance() != null){Estandares.getInstance().finish();}
        if(SKUs.getInstance() != null){SKUs.getInstance().finish();}
        if(Activaciones.getInstance() != null){Activaciones.getInstance().finish();}
        if(Activacionesrd.getInstance() != null){Activacionesrd.getInstance().finish();}
        if(Activacionestb.getInstance() != null){Activacionestb.getInstance().finish();}
        if(Activos.getInstance() != null){Activos.getInstance().finish();}
        if(SKUCalidad.getInstance() != null){SKUCalidad.getInstance().finish();}
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        if(SeleccionCuestionario.getInstance() != null){SeleccionCuestionario.getInstance().finish();}
        //Tomar los datos de cada tabla y enviarlos a tablas a Firebase
        //Traer todos los registros de cada tabla e insertarlos individualmente en Firebase
        VerificarConex vc = new VerificarConex();
        boolean net = vc.revisarconexión(context);
        if (net == true) {
            operaciones = new OperacionesBDInterna(context);
            conGen = new ConsultaGeneral();
            try {
                sincronizarT1();
            } finally {
                //Toast.makeText(context, "Sincronizando por favor espere...", Toast.LENGTH_LONG).show();
                sincronizarTR();
                Toast.makeText(context,"Sincronización de datos finalizando", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No se encuentra conectado a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT1() {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t1 where estado=1 and sincronizada<>1", null);
        encuestaAct = "";
        int i;
        if (registros != null) {
            Toast.makeText(context, "Sincronizando .........", Toast.LENGTH_LONG).show();
            for (i = 0; i < registros.length; i++) {
                encuestaAct = registros[i].get(0);
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
                StringTokenizer st = new StringTokenizer(fechaT, " ");
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
                try {
                    final int finalI = i;
                    db.collection("t1").document(registros[i].get(0)).set(regis).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void T) {
                            Sincronizar ob=new Sincronizar();
                            TextView tv = ob.retornartext(1);
                            tv.setText("" + finalI);
                        }
                    });

                } catch (Exception e) {
                    //Que intente otra vez
                    final String s = registros[i].get(0);
                    db.collection("t1").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "La sincronización ha fallado en T1", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {

                    regis = new HashMap<>();
                    sincronizarT2(encuestaAct);
                }
            }
        } else {
            Intent intX = new Intent(context, SeleccionCuestionario.class);
            intX.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intX);
        }
    }

    public void sincronizarT2(String enct2) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t2 where encuesta='" + enct2 + "' and rta<>0 order by ID2", null);
        Map<String, Object> tabla2 = new HashMap<>();
        Map<String, Object> regis;
        String Encuesta = enct2;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("rta", registros[i].get(2));
                regis.put("eval", registros[i].get(3));
                regis.put("fcr", registros[i].get(4));
                regis.put("_ID", registros[i].get(5));
                regis.put("ID2", registros[i].get(6));
                tabla2.put(enct2 + "_" + i, regis);
            }

            try {
                db.collection("t2").document(Encuesta).set(tabla2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct2++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(2);
                        tv.setText("" + ct2);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t2").document(s).set(tabla2).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "La sincronización ha fallado en T2", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla2.clear();
                sincronizarT3(encuestaAct);
            }
        } else {
            sincronizarT3(encuestaAct);
        }
    }

    public void sincronizarT3(String enct3) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t3 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla3 = new HashMap<>();
        Map<String, Object> regis;
        String Encuesta = enct3;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("rta", registros[i].get(3));
                regis.put("eval", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                tabla3.put(enct3 + "_" + i, regis);
            }
            try {
                db.collection("t3").document(Encuesta).set(tabla3).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct3++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(3);
                        tv.setText("" + ct3);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t3").document(s).set(tabla3).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "La sincronización ha fallado en T3", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla3.clear();
                sincronizarT4(encuestaAct);
            }

        } else {
            sincronizarT4(encuestaAct);
        }
    }

    public void sincronizarT4(String enct4) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t4 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla4 = new HashMap<>();
        String Encuesta = enct4;
        Map<String, Object> regis;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("est", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                tabla4.put(enct4 + "_" + i, regis);
            }
            try {
                db.collection("t4").document(Encuesta).set(tabla4).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct4++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(4);
                        tv.setText("" + ct4);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t4").document(s).set(tabla4).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "La sincronización ha fallado en T4", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla4.clear();
                sincronizarT5(encuestaAct);
            }
        } else {
            sincronizarT5(encuestaAct);
        }
    }

    public void sincronizarT5(String enct5) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t5 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla5 = new HashMap<>();
        String Encuesta = enct5;
        Map<String, Object> regis;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("sku", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                tabla5.put(enct5 + "_" + i, regis);
            }
            try {
                db.collection("t5").document(Encuesta).set(tabla5).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct5++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(5);
                        tv.setText("" + ct5);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t5").document(s).set(tabla5).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t5").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T5", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla5.clear();
                sincronizarT6(encuestaAct);
            }
        } else {
            sincronizarT6(encuestaAct);
        }
    }

    public void sincronizarT6(String enct6) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t6 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla6 = new HashMap<>();
        String Encuesta = enct6;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
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
                tabla6.put(enct6 + "_" + i, regis);
            }
            try {
                db.collection("t6").document(Encuesta).set(tabla6).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct6++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(6);
                        tv.setText("" + ct6);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t6").document(s).set(tabla6).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t6").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T6", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla6.clear();
                sincronizarT7(encuestaAct);
            }
        } else {
            sincronizarT7(encuestaAct);
        }
    }

    public void sincronizarT7(String enct7) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t7 where encuesta='" + encuestaAct + "' order by ID2", null);
        Map<String, Object> tabla7 = new HashMap<>();
        String Encuesta = enct7;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
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
                tabla7.put(enct7 + "_" + i, regis);
            }
            try {
                db.collection("t7").document(Encuesta).set(tabla7).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct7++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(7);
                        tv.setText("" + ct7);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t7").document(s).set(tabla7).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t7").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T7", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla7.clear();
                sincronizarT8(encuestaAct);
            }
        } else {
            sincronizarT8(encuestaAct);
        }
    }

    public void sincronizarT8(String enct8) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t8 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla8 = new HashMap<>();
        String Encuesta = enct8;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
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
                tabla8.put(enct8 + "_" + i, regis);
            }
            try {
                db.collection("t8").document(Encuesta).set(tabla8).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct8++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(8);
                        tv.setText("" + ct8);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t8").document(s).set(tabla8).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t8").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T8", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla8.clear();
                sincronizarT9(encuestaAct);
            }
        } else {
            sincronizarT9(encuestaAct);
        }
    }

    public void sincronizarT9(String enct9) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t9 where encuesta='" + encuestaAct + "' and prec<>0  order by ID2", null);
        Map<String, Object> tabla9 = new HashMap<>();
        String Encuesta = enct9;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("sku", registros[i].get(2));
                regis.put("prec", registros[i].get(3));
                regis.put("fcr", registros[i].get(4));
                regis.put("_ID", registros[i].get(5));
                regis.put("ID2", registros[i].get(6));
                tabla9.put(enct9 + "_" + i, regis);
            }
            try {
                db.collection("t9").document(Encuesta).set(tabla9).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct9++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(9);
                        tv.setText("" + ct9);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t9").document(s).set(tabla9).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t9").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T9", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla9.clear();
                sincronizarT10(encuestaAct);
            }
        } else {
            sincronizarT10(encuestaAct);
        }
    }

    public void sincronizarT10(String enct10) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t10 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla10 = new HashMap<>();
        String Encuesta = enct10;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
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
                tabla10.put(enct10 + "_" + i, regis);
            }
            try {
                db.collection("t10").document(Encuesta).set(tabla10).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct10++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(10);
                        tv.setText("" + ct10);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t10").document(s).set(tabla10).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t10").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T10", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla10.clear();
                sincronizarT11(encuestaAct);
            }
        } else {
            sincronizarT11(encuestaAct);
        }
    }

    public void sincronizarT11(String enct11) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t11 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla11 = new HashMap<>();
        String Encuesta = enct11;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
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
                tabla11.put(enct11 + "_" + i, regis);
            }
            try {
                db.collection("t11").document(Encuesta).set(tabla11).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct11++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(11);
                        tv.setText("" + ct11);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t11").document(s).set(tabla11).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t11").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T11", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla11.clear();
                sincronizarTF(encuestaAct);
            }
        } else {
            sincronizarTF(encuestaAct);
        }
    }

    public void sincronizarTF(String enctf) {
        boolean fotosubida = false;
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM tf where encuesta='" + encuestaAct + "' order by ID2", null);
        Map<String, Object> tablaf = new HashMap<>();
        String Encuesta = enctf;
        Map<String, Object> regis;
        
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("path", registros[i].get(1));
//              String nombre = registros[i].get(1);
                regis.put("ref", registros[i].get(2));
                regis.put("esp", registros[i].get(3));
                regis.put("cat", registros[i].get(4));
                regis.put("preg", registros[i].get(5));
                regis.put("obs", registros[i].get(6));
                regis.put("_ID", registros[i].get(7));
                regis.put("ID2", registros[i].get(8));
                tablaf.put(enctf + "_" + i, regis);
            }
            try {
                db.collection("tf").document(Encuesta).set(tablaf).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        cttf++;
                        Sincronizar ob=new Sincronizar();
                        TextView tv = ob.retornartext(12);
                        tv.setText("" + cttf);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("tf").document(s).set(tablaf).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("tf").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en TF", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tablaf.clear();
                operaciones.queryNoData("UPDATE t1 SET sincronizada='" + 1 + "' WHERE encuesta='" + encuestaAct + "'");
 //               Intent intX = new Intent(context, SeleccionCuestionario.class);
//                intX.addFlags(FLAG_ACTIVITY_NEW_TASK);
   //             context.startActivity(intX);
            }
        } else{
            Toast.makeText(context, "Sincronizacion finalizada", Toast.LENGTH_LONG).show();
        }

    }

    public void sincronizarTR() {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM tr", null);
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("maquina", registros[i].get(1));
                regis.put("fecha_ini", registros[i].get(2));
                regis.put("fecha_fin", registros[i].get(3));
                regis.put("hora_ini", registros[i].get(4));
                String fechaC = fg.fechaActual(3);
                StringTokenizer st = new StringTokenizer(fechaC, " ");
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
                try {
                    db.collection("tr").document(registros[i].get(0)).set(regis).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void T) {
                            cttr++;
                            Sincronizar ob=new Sincronizar();
                            TextView tv = ob.retornartext(13);
                            tv.setText("" + cttr);
                        }
                    });
                } catch (Exception e) {
                    final String s = registros[i].get(0);
                    db.collection("tr").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("tr").document(s).set(cv2);
                            Toast.makeText(context, "La sincronización ha fallado en TR", Toast.LENGTH_SHORT).show();
                        }
                    });
                    regis = new HashMap<>();
                } finally {
                    regis = new HashMap<>();
                }
            }
        } else {
        }
    }
}