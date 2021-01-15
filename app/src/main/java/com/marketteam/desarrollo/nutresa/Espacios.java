package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Espacios extends AppCompatActivity {

    ArrayList<EspacioModel> lista;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC,flujo;
    RecyclerView recyclerEspacios;
    String[] espaciosID, idTabla2;
    static Espacios activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espacios);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    class Espac extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView tiene;
        ImageView imgV;

        public Espac(View itemView) {
            super(itemView);

            this.nombre = (TextView) itemView.findViewById(R.id.nItem);
            this.tiene = (TextView) itemView.findViewById(R.id.rItem);
            this.imgV = (ImageView) itemView.findViewById(R.id.imgVEC);
        }
    }

    class EspacioModel {
        String nombre, tiene;
        ImageView imgV;

        public EspacioModel(String nombre, String tiene, ImageView iv) {

            this.nombre = nombre;
            this.tiene = tiene;
            this.imgV = iv;
        }
    }

    class EspacioAdapter extends RecyclerView.Adapter<Espac> {
        ArrayList<EspacioModel> lista;

        public EspacioAdapter(ArrayList<EspacioModel> lista) {
            this.lista = lista;
        }

        @Override
        public Espac onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Espac(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_esp_cat, parent, false));
        }

        @Override
        public void onBindViewHolder(Espac holder, final int position) {
            final Espac holder2 = holder;
            final EspacioModel espacio = lista.get(position);
            holder.nombre.setText(espacio.nombre);
            holder.tiene.setText(espacio.tiene);
            String idEspacio = espaciosID[position];
            ArrayList<String>[] objeto2 = conGen.queryGeneral(getBaseContext(), "t2t", new String[]{"rta", "eval"}, "esp='" + idEspacio + "'");
            if (objeto2 != null) {
                String rta = objeto2[0].get(0);
                String ev = objeto2[0].get(1);
                if (ev.equals("1")) {
                    holder.imgV.setImageResource(R.drawable.check_opto);
                } else if (ev.equals("2")) {
                    holder.imgV.setImageResource(R.drawable.equis_opto);
                } else{
                    holder.imgV.setImageResource(R.drawable.uncheck);
                }

                if (rta.equals("1")) {
                    holder.tiene.setText("SI");
                } else if (rta.equals("2")) {
                    holder.tiene.setText("NO");
                }
            }
            //holder.imgV.setImageDrawable();
            holder.nombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popUp1(position, holder2, espacio);
                }
            });
            holder.tiene.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popUp1(position, holder2, espacio);
                }
            });
            holder.imgV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popUp1(position, holder2, espacio);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onStart() {
        super.onStart();
        if (DetalleCliente.getInstance() != null) {DetalleCliente.getInstance().finish();}
        if (Validaciones.getInstance() != null) {Validaciones.getInstance().finish();}
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) { Log.e("first","error"); }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                location = loc;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerEspacios = (RecyclerView) findViewById(R.id.recyclerEspc);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerEspacios.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("Espacios");
        idC = fg.clienteActual();

        listarEsp();
    }

    public void listarEsp() {
        //Traer los datos actuales
        ArrayList<String> resCampos = fg.existeACT(3);
        String canalAct = resCampos.get(0);
        String subcAct = resCampos.get(1);
        String espAct = resCampos.get(2);
        String queryEsp = "SELECT t2t.esp,'108_ESPACIO'.NESP FROM t2t join '108_ESPACIO' on t2t.esp = '108_ESPACIO'.ESP order by t2t.esp asc";
        ArrayList<String>[] espacios = conGen.queryObjeto2val(getBaseContext(), queryEsp, null);
        if (espacios != null) {
            espaciosID = new String[espacios.length];
            idTabla2 = new String[espacios.length];
            for (int c = 0; c < espacios.length; c++) {
                String temp = espacios[c].get(0);
                espaciosID[c] = temp;
                idTabla2[c] = temp;
                String esp = espacios[c].get(1);
                ImageView iv = new ImageView(this);
                EspacioModel em = new EspacioModel(esp, "", iv);
                lista.add(em);
            }
            EspacioAdapter ea = new EspacioAdapter(lista);
            recyclerEspacios.setAdapter(ea);
        }
    }

    public void popUp1(int pos, Espac holder, EspacioModel espacio) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_conf_ej, null);
        TextView enunTV = (TextView) popUp.findViewById(R.id.textoConEj);
        ImageView imagenEj = (ImageView) popUp.findViewById(R.id.imageViewPopEj);
        imagenEj.setImageDrawable(null);
        String img = "";
        ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND ESP='" + espaciosID[pos] + "'");
        if (foto != null) {
            img = foto[0].get(0);
            StringTokenizer st = new StringTokenizer(img, ".");
            String rutaImg = st.nextToken();
            String uri = "@drawable/" + rutaImg;
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
            if (imagen != null) {
                imagenEj.setImageDrawable(imagen);
            }
        }
        Button cancel = (Button) popUp.findViewById(R.id.buttonEjVolver);
        Button ok = (Button) popUp.findViewById(R.id.buttonEjConf);
        String texto = getString(R.string.conf_esp);
        enunTV.setText(texto);
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        final String rta = espacio.tiene;
        final Espac holder2 = holder;
        final EspacioModel espacio2 = espacio;
        final int pos2 = pos;
        //ok - confirmacion = true;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryEsp = "SELECT t2t.rta FROM t2t where esp = " + espaciosID[pos2] ;
                ArrayList<String>[] respuesta = conGen.queryObjeto2val(getBaseContext(),queryEsp,null);
                String rta = respuesta[0].get(0);
                Integer rtan = Integer.parseInt(respuesta[0].get(0));
                String img = "";
                espacio2.tiene = "SI";
                holder2.tiene.setText("SI");
                holder2.imgV.setImageResource(R.drawable.equis_opto);
                //Guardar el SI en la base de datos
                if (rtan == 2) {
                    at.actualizarT2(espaciosID[pos2], 4);
                }else{
                    at.actualizarT2(espaciosID[pos2], 1);
                }

                //Proceso de antiguo popUp2
                //Actualizar espacio actual
                operaciones.queryNoData("UPDATE ACT SET VAL ='" + espaciosID[pos2] + "' WHERE VA='ESP'");
                operaciones.queryNoData("UPDATE ACT SET VAL ='" + idTabla2[pos2] + "' WHERE VA='IDT2'");
                //Si le da que sí, Ir a Categorías
                irCategoria();
                popupWindow.dismiss();
            }
        });
        //cancel - confirmacion = false;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(espaciosID[pos2])  == 14) {
                    String img = "";
                    espacio2.tiene = "SI";
                    holder2.tiene.setText("SI");
                    holder2.imgV.setImageResource(R.drawable.check_opto);
                    //Guardar el SI en la base de datos
                    at.actualizarT2(espaciosID[pos2], 1);
                    //Proceso de antiguo popUp2
                    //Actualizar espacio actual
                    operaciones.queryNoData("UPDATE ACT SET VAL ='" + espaciosID[pos2] + "' WHERE VA='ESP'");
                    operaciones.queryNoData("UPDATE ACT SET VAL ='" + idTabla2[pos2] + "' WHERE VA='IDT2'");
                    //Si le da que sí, Ir a Categorías
                    irCategoria();
                    popupWindow.dismiss();

                } else {
                    String queryEsp = "SELECT t2t.rta FROM t2t where esp = " + espaciosID[pos2] ;
                    ArrayList<String>[] respuesta = conGen.queryObjeto2val(getBaseContext(),queryEsp,null);
                    String rta = respuesta[0].get(0);
                    Integer rtan = Integer.parseInt(respuesta[0].get(0));
                    if (rtan == 1) {
                        //Confirmar que quiere borrar la información
                        int posic = pos2 ;
                        //Buscar posic como _id para sacar el ID de Espacio al cual se le borra la info
                        popUp3(posic, holder2, espacio2);
                    } else {
                        espacio2.tiene = "NO";
                        holder2.tiene.setText("NO");
                        holder2.imgV.setImageResource(R.drawable.check_opto);
                        int posic = pos2;
                        at.actualizarT2(espaciosID[pos2], 3);
                        at.actualizarT2(espaciosID[pos2], 3);
                        operaciones.queryNoData("update t3t set rta=2 where esp=" + espaciosID[pos2]);
                        operaciones.queryNoData("update t4t set rta=2 where esp=" + espaciosID[pos2]);
                        operaciones.queryNoData("update t5t set rta=2 where esp=" + espaciosID[pos2]);
                        operaciones.queryNoData("delete from t6t where esp=" + espaciosID[pos2]);
                        if (espaciosID[pos2].equals("1")) {
                            operaciones.queryNoData("delete from t7t");
                        }
                    }
                    popupWindow.dismiss();
                }


            }
        });

        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    //Método para sacar imagen

    public void popUp3(int pos, final Espac holder, final EspacioModel espacio) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = getString(R.string.conf_borrar_datos);
        texto.setText(tx);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        final int pos2 = pos;
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                at.actualizarT2(espaciosID[pos2], 3);
                operaciones.queryNoData("delete from t6t where esp=" + espaciosID[pos2]);
                if (espaciosID[pos2].equals("1")) {
                    operaciones.queryNoData("delete from t7t");
                }
                holder.tiene.setText("NO");
                espacio.tiene = "NO";
                holder.imgV.setImageResource(R.drawable.check_opto);
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void irCategoria() {
        Intent cat = new Intent(this, Categorias.class);
        startActivity(cat);
    }

    public void Volver(View v) {
        //Validar si todos los espacios están evaluados
        ArrayList<String>[] eval = conGen.queryGeneral(getBaseContext(), "t2t", new String[]{"eval"}, null);
        if (eval != null) {
            boolean evaluados = true;
            for (int e = 0; e < eval.length; e++) {
                String temp = eval[e].get(0);
                if (!temp.equals("1")) {
                    evaluados = false;
                    break;
                }
            }
            //Si si
            if (evaluados == true) {
                //Actualizar módulo como evaluado EV = 1 en 'ME'
                operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='ESPACIOS'");
                Intent menu = new Intent(this, MenuOpciones.class);
                startActivity(menu);
            } else {
                //Si no
                operaciones.queryNoData("UPDATE ME SET EV='2' WHERE ET='ESPACIOS'");
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 6, 1);
            }
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
        Toast.makeText(getBaseContext(),"Verificando Preguntas TIPOA",Toast.LENGTH_SHORT).show();
        if (aplicaTA.equals("1")) {
            fg.ultimaPantallafta("Espacios");
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

    public static Espacios getInstance() {
        return activityA;
    }
}
