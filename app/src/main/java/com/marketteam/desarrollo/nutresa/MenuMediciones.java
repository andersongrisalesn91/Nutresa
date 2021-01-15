package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MenuMediciones extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC;
    RecyclerView recyclerMenu;
    RelativeLayout relativeL;
    ArrayList<MenuModel> lista;
    String[] opciones, evaluados;
    Integer canitems = 0, actorden = 0;
    Button fin;
    static MenuMediciones activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mediciones);
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
                holder.iv.setColorFilter(R.color.light_purple);
            }
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
        if (completa==true){
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='ESQUEMAMKT'");
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
        String queryMenu = "SELECT ET, PF, EV, CDM,_ID FROM 'ME' order by 'ME'._ID asc";
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


    public void revision() {
        //Revisar cúales botones se habilitan o no
        //Revisar cúales opciones ya están evaluadas para pone check
        //O cuales se iniciaron solamente para poner X
        boolean finalizar = true;
        if (evaluados != null && evaluados.length > 0) {
            for (int e = 0; e < canitems; e++) {
                String evalu = evaluados[e];
                if (!evaluados[e].equals("1")) {
                    //0 no evaluado, 1 evaluado
                    finalizar = false;
                }
            }
            if (!finalizar) {
                fin.setVisibility(View.INVISIBLE);
                fin.setEnabled(false);
            } else {
                fin.setVisibility(View.VISIBLE);
                fin.setEnabled(true);
            }
        }
    }

    public void FinalizarAudt(View v) {
        //Ya todos están evaluados
        Finalizar f = new Finalizar(getBaseContext());
        boolean completa = f.verificarDatos();
        if (completa != true) {
            Toast.makeText(getBaseContext(), "La auditoría no se encuentra completa", Toast.LENGTH_SHORT).show();
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
    public static MenuMediciones getInstance() {
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