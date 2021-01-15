package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MenuOpciones extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, flujo, queryActVal;
    RecyclerView recyclerMenu;
    RelativeLayout relativeL;
    ArrayList<MenuModel> lista;
    String[] opciones, evaluados;
    Integer canitems = 0, actorden = 0, cvaltend = 0, cvaltendr = 0;
    Button fin;
    static MenuOpciones activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_opciones);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onBackPressed() {
    }




    class Menu extends RecyclerView.ViewHolder {
        Button b;
        ImageView iv;

        public Menu(View itemView) {
            super(itemView);
            this.b = (Button) itemView.findViewById(R.id.buttonRVMenu);
            this.iv = (ImageView) itemView.findViewById(R.id.imageViewRVMenu);
        }
    }

    class MenuModel {
        String textoBoton;
        String textoEval;

        public MenuModel(String texto, String textoEval) {
            this.textoBoton = texto;
            this.textoEval = textoEval;
        }
    }

    class MenuAdapter extends RecyclerView.Adapter<Menu> {
        ArrayList<MenuModel> lista;

        public MenuAdapter(ArrayList<MenuModel> lista) {
            this.lista = lista;
        }

        @Override
        public Menu onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Menu(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_menu, parent, false));
        }

        @Override
        public void onBindViewHolder(Menu holder, final int position) {
            final MenuModel opcion = lista.get(position);
            final Menu holder2 = holder;
            holder.b.setText(opcion.textoBoton);
            //Si está evaluado, poner chulito

            if (opcion.textoEval.equals("1")) {
                holder.iv.setImageResource(R.drawable.check_opt);
            } else if (opcion.textoEval.equals("2")) {
                holder.iv.setImageResource(R.drawable.equis_opt);
            } else if (opcion.textoEval.equals("0")) {
                holder.iv.setImageResource(R.drawable.uncheck);
            }

            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        canitems = 0;
        cvaltend = 0;
        cvaltendr = 0;
        if (ListaClientes.getInstance() != null) {
            ListaClientes.getInstance().finish();
        }
        if (Categorias.getInstance() != null) {
            Categorias.getInstance().finish();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("first", "error");
        }
        Criteria criteria = new Criteria();
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("Menu");
        idC = fg.clienteActual();
        lista = new ArrayList<>();
        fin = (Button) findViewById(R.id.buttonIrMenu);
        Finalizar f = new Finalizar(getBaseContext());
        boolean completa = f.verificarDatos();
        if (completa == true) {
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='ESQUEMAMKT'");
            String queryVal = "SELECT count(COM) as CC FROM 'VALID'  where 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE not in (select 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg as CV from tv where encuesta='" + fg.getAuditoria() + "' and rt=1) order by 'VALID'.IDV asc";
            ArrayList<String>[] objval = conGen.queryObjeto2val(getBaseContext(), queryVal, null);
            String cantval = objval[0].get(0);
            if (objval !=null) {
                if (cantval.equals("0")) {
                    operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                }else{
                    operaciones.queryNoData("UPDATE ME SET EV='2' WHERE ET='VALIDACIONES'");
                }
            }else{
                operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
            }
        }
        ArrayList<String>[] objFlujo = conGen.queryGeneral(getBaseContext(), "ACT", new String[]{"VAL"}, "VA='TIPOM'");
        if (objFlujo != null) {
            flujo = objFlujo[0].get(0);
        }
        //recycler view en un relative layout
        //Leer de la BD la jerarquía de los botones
        //Leer en la BD los botones (menús) a crear
        relativeL = (RelativeLayout) findViewById(R.id.relativeLayoutMenu);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ver_recycler, null);
        recyclerMenu = (RecyclerView) v.findViewById(R.id.recyclerDatosDetalle);
        relativeL.addView(v);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMenu.setLayoutManager(llm);
        String queryMenu = "";
        if (completa == true) {
            queryMenu = "SELECT ET, PF, EV, CDM,_ID FROM 'ME' order by 'ME'._ID asc";
        } else {
            queryMenu = "SELECT ET, PF, EV, CDM,_ID FROM 'ME' where ET<>'VALIDACIONES' order by 'ME'._ID asc";
        }
        ArrayList<String>[] objMenu = conGen.queryObjeto2val(getBaseContext(), queryMenu, null);
        if (objMenu != null) {
            opciones = new String[objMenu.length];
            evaluados = new String[objMenu.length];
            for (int op = 0; op < objMenu.length; op++) {
                String ConsultaMostrar = objMenu[op].get(3);
                if (ConsultaMostrar != null) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0 || mostrar[0].get(0) == null || mostrar[0].get(0) == "") {

                        String etiqueta = objMenu[op].get(0);
                        opciones[canitems] = objMenu[op].get(1);
                        evaluados[canitems] = objMenu[op].get(2);
                        ImageView imageView = new ImageView(this);
                        MenuModel model = new MenuModel(etiqueta, evaluados[canitems]);
                        lista.add(model);
                        canitems++;
                    }
                } else {

                    String etiqueta = objMenu[op].get(0);
                    opciones[canitems] = objMenu[op].get(1);
                    evaluados[canitems] = objMenu[op].get(2);
                    ImageView imageView = new ImageView(this);
                    if (evaluados[canitems].equals("1")) {
                        imageView.setImageResource(R.drawable.check_opt);
                    } else if (evaluados[canitems].equals("2")) {
                        imageView.setImageResource(R.drawable.equis_opt);
                    } else {
                        imageView.setImageDrawable(null);
                    }
                    MenuModel model = new MenuModel(etiqueta, evaluados[canitems]);
                    lista.add(model);
                    canitems++;
                }
            }
            MenuAdapter ma = new MenuAdapter(lista);
            recyclerMenu.setAdapter(ma);
        }

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

        //Revisar si aparte de espacios si algún botón está habilitado desde el inicio o cuál botón depende de cuál
        revision();
    }

    public void irOpcion(int pos, Menu holder2) {

        String Tipo = "";
        //Leer la posición y según la que sea, enviar al usuario a la vista correspondiente
        String clase = opciones[pos];

        if (flujo.equals("1")) {
            queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and (cu=1 or cu=3)";
        } else if (flujo.equals("2")) {
            queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and (cu=2 or cu=3)";
        } else if (flujo.equals("3")) {
            queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and cu=5";
        } else if (flujo.equals("4")) {
            queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and cu=4";
        }

        if (clase.equals("0")) {


            ArrayList<String>[] actidpreg = conGen.queryObjeto2val(getBaseContext(), queryActVal, null);
            if (actidpreg != null) {
                for (Integer i = 0; i < actidpreg.length; i++) {
                    String consval = actidpreg[i].get(3);
                    if (consval != null || consval.equals("0")) {
                        Integer acval = 0;
                        if (consval.equals("0")) {
                            acval = 1;
                        } else {
                            ArrayList<String>[] activval = conGen.queryObjeto2val(getBaseContext(), consval, null);
                            acval = Integer.parseInt(activval[0].get(0));
                        }

                        if (acval > 0) {
                            Tipo = actidpreg[i].get(7);
                            operaciones.queryNoData("UPDATE '302_PREGGEN' SET aplica='1' WHERE orden='" + actidpreg[i].get(0) + "'");
                            if (!Tipo.equals("3") && !Tipo.equals("4") && !Tipo.equals("6")) {

                                String queryActopc = "SELECT opn,opt,cnd FROM '302_PREGGEND' WHERE idpreg='" + actidpreg[i].get(1) + "' ORDER BY opn ASC";
                                ArrayList<String>[] actidopc = conGen.queryObjeto2val(getBaseContext(), queryActopc, null);
                                if (actidopc != null) {
                                    for (Integer x = 0; x < actidopc.length; x++) {
                                        String consvalx = actidopc[x].get(2);
                                        if (consvalx != null) {
                                            Integer validactopt = 0;
                                            if (consvalx.equals("0")) {
                                                validactopt = 1;
                                            } else {
                                                ArrayList<String>[] activvalx = conGen.queryObjeto2val(getBaseContext(), consvalx, null);
                                                validactopt = Integer.parseInt(activvalx[0].get(0));
                                            }

                                            String queryActext = "SELECT count(encuesta) as ce FROM t8t WHERE idpreg='" + actidpreg[i].get(1) + "' and optn='" + actidopc[x].get(0) + "'";
                                            ArrayList<String>[] actidex = conGen.queryObjeto2val(getBaseContext(), queryActext, null);

                                            if (Integer.parseInt(actidex[0].get(0)) == 0) {
                                                if (validactopt > 0) {
                                                    at.insertarT8(actidpreg[i].get(0), actidpreg[i].get(1), actidpreg[i].get(2), actidopc[x].get(0), actidopc[x].get(1), "1",actidpreg[i].get(8));
                                                } else {
                                                    at.insertarT8(actidpreg[i].get(0), actidpreg[i].get(1), actidpreg[i].get(2), actidopc[x].get(0), actidopc[x].get(1), "2",actidpreg[i].get(8));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {

                                String queryActext = "SELECT count(encuesta) as ce FROM t8t WHERE idpreg='" + actidpreg[i].get(1) + "'";
                                ArrayList<String>[] actidex = conGen.queryObjeto2val(getBaseContext(), queryActext, null);
                                if (Integer.parseInt(actidex[0].get(0)) == 0) {
                                    at.insertarT8(actidpreg[i].get(0), actidpreg[i].get(1), actidpreg[i].get(2), "0", "0", "1",actidpreg[i].get(8));
                                }
                            }
                        }
                    }
                }

                String queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1 and ap=1";
                ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);
                if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                    fg.ultimaPantallafta("Menu");
                    Intent intent = new Intent(this, TipoAMenu.class);
                    startActivity(intent);
                } else {
                    operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREGGENERALES'");
                    holder2.iv.setImageResource(R.drawable.check_opt);
                }
            }
        } else if (clase.equals("Generales.class")) {

            StringTokenizer st =
                    new StringTokenizer(clase, ".");

            String nClase = st.nextToken();
            String ruta = "com.marketteam.desarrollo.nutresa." + nClase;
            Class<?> sigClass = null;
            try {
                operaciones.queryNoData("UPDATE ACT SET VAL='" + fg.pantallaactual() + "' WHERE VA='PANT'");
                sigClass = Class.forName(ruta);
                Intent intent = new Intent(this, sigClass);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (clase.equals("Validaciones.class")) {

            StringTokenizer st =
                    new StringTokenizer(clase, ".");

            String nClase = st.nextToken();
            String ruta = "com.marketteam.desarrollo.nutresa." + nClase;
            Class<?> sigClass = null;
            String queryVal = "select count(CE) as ce from VALID";
            ArrayList<String>[] objval = conGen.queryObjeto2val(getBaseContext(), queryVal, null);
            String cantval = objval[0].get(0);
            String queryValA = "select SQLTXT as SQ from 'AUTOVAL'";
            ArrayList<String>[] objvalA = conGen.queryObjeto2val(getBaseContext(), queryValA, null);
            if (!(objvalA==null)) {
                for (int e = 0; e < objvalA.length; e++) {
                    operaciones.queryNoData(objvalA[e].get(0));
                }
            }
            if (!cantval.equals("0")) {
                try {
                    operaciones.queryNoData("UPDATE ME SET EV='2' WHERE ET='VALIDACIONES'");
                    sigClass = Class.forName(ruta);
                    Intent intent = new Intent(this, sigClass);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                operaciones.queryNoData("UPDATE tv SET rt='1' where rt='2' and encuesta='" + fg.getAuditoria() + "'");
                operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                holder2.iv.setImageResource(R.drawable.check_opt);
                evaluados[pos] = "1";
                revision();
            }
        } else {

            StringTokenizer st =
                    new StringTokenizer(clase, ".");

            String nClase = st.nextToken();
            String ruta = "com.marketteam.desarrollo.nutresa." + nClase;
            Class<?> sigClass = null;
            try {
                sigClass = Class.forName(ruta);
                Intent intent = new Intent(this, sigClass);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void revision() {
        boolean finalizar = true;
        if (evaluados != null && evaluados.length > 0) {
            for (int e = 0; e < canitems; e++) {
                String evalu = evaluados[e];
                String opt = opciones[e];
                if (!evaluados[e].equals("1")) {
                    //0 no evaluado, 1 evaluado
                    finalizar = false;
                }
            }
            if (!finalizar) {
                fin.setVisibility(View.INVISIBLE);
                fin.setEnabled(false);
            } else {
                String queryValy = "SELECT count(COM) as CC FROM 'VALID'  where 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE not in (select 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg as CV from tv where encuesta='" + fg.getAuditoria() + "' and rt=1) order by 'VALID'.IDV asc";
                ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
                String cantval = objvaly[0].get(0);
                if (objvaly != null) {
                    if (cantval.equals("0")){
                        operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                        fin.setVisibility(View.VISIBLE);
                        fin.setEnabled(true);
                    }else{
                        operaciones.queryNoData("UPDATE ME SET EV='2' WHERE ET='VALIDACIONES'");
                        fin.setVisibility(View.INVISIBLE);
                        fin.setEnabled(false);
                    }
                }else{
                    operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                    fin.setVisibility(View.VISIBLE);
                    fin.setEnabled(true);
                }
            }
        }
    }

    public void FinalizarAudt(View v) {
        //Ya todos están evaluados
        Finalizar f = new Finalizar(getBaseContext());
        boolean completa = f.verificarDatos();
        if (completa != true) {
            Toast.makeText(getBaseContext(), "La auditoría no se encuentra completa o faltan validaciones x responder", Toast.LENGTH_SHORT).show();
        } else {
            double[] gps;
            if (location == null) {
                gps = new double[]{0, 0};
            } else {
                gps = new double[]{location.getLatitude(), location.getLongitude()};
            }
            f.finAuditoria(1, gps);
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
    public static MenuOpciones getInstance() {
        return activityA;
    }



    @Override
    protected void onStop() {
        super.onStop();
        /*        operaciones.close();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*operaciones.close();*/
    }

}