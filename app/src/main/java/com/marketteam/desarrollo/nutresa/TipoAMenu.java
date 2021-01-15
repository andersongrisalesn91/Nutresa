package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class TipoAMenu extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, flujo;
    RecyclerView recyclerTipoAM;
    ArrayList<TipoAMModel> lista;
    String[] grupo, evaluados;
    Integer canitems = 0;
    Button fin;
    static TipoAMenu activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoamenu);
        activityA = this;
    }

    @Override
    public void onBackPressed() {
    }


    class TipoAM extends RecyclerView.ViewHolder {
        Button b;
        ImageView iv;

        public TipoAM(View itemView) {
            super(itemView);
            this.b = (Button) itemView.findViewById(R.id.buttonRVMenuTA);
            this.iv = (ImageView) itemView.findViewById(R.id.imageViewRVMenuTA);
        }
    }

    class TipoAMModel {
        String textoBoton;
        String textoEval;

        public TipoAMModel(String texto, String textoEval) {
            this.textoBoton = texto;
            this.textoEval = textoEval;
        }
    }

    class TipoAMAdapter extends RecyclerView.Adapter<TipoAM> {
        ArrayList<TipoAMModel> lista;

        public TipoAMAdapter(ArrayList<TipoAMModel> lista) {
            this.lista = lista;
        }

        @Override
        public TipoAM onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TipoAM(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_menuta, parent, false));
        }

        @Override
        public void onBindViewHolder(TipoAM holder, final int position) {
            final TipoAMModel opcion = lista.get(position);
            final TipoAM holder2 = holder;
            holder.b.setText(opcion.textoBoton);
            //Si está evaluado, poner chulito

            if (opcion.textoEval.equals("1")) {
                holder.iv.setImageResource(R.drawable.check_opt);
            } else if (opcion.textoEval.equals("2")) {
                holder.iv.setImageResource(R.drawable.equis_opt);
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

        if (Categorias.getInstance() != null) {
            Categorias.getInstance().finish();
        }
        if (Espacios.getInstance() != null) {
            Espacios.getInstance().finish();
        }
        if (Estandares.getInstance() != null) {
            Estandares.getInstance().finish();
        }
        if (SKUs.getInstance() != null) {
            SKUs.getInstance().finish();
        }
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("TipoAMenu");
        idC = fg.clienteActual();
        lista = new ArrayList<>();
        fin = (Button) findViewById(R.id.buttonIrMenu);

        //recycler view en un relative layout
        //Leer de la BD la jerarquía de los botones
        //Leer en la BD los botones (menús) a crear
        recyclerTipoAM = (RecyclerView) findViewById(R.id.recyclertipoamenu);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerTipoAM.setLayoutManager(llm);
        String queryMenu = "SELECT grp, 0 as PF, ev, cdn,grpid FROM '302_PREGGRU' order by grpid asc";
        ArrayList<String>[] objMenu = conGen.queryObjeto2val(getBaseContext(), queryMenu, null);
        if (objMenu != null) {
            grupo = new String[objMenu.length];
            evaluados = new String[objMenu.length];
            for (int op = 0; op < objMenu.length; op++) {
                String ConsultaMostrar = objMenu[op].get(3);
                if (ConsultaMostrar != null) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0 || mostrar[0].get(0) == null || mostrar[0].get(0) == "") {
                        grupo[canitems] = objMenu[op].get(4);
                        String etiqueta = objMenu[op].get(0);
                        evaluados[canitems] = objMenu[op].get(2);
                        ImageView imageView = new ImageView(this);
                        TipoAMModel model = new TipoAMModel(etiqueta, evaluados[canitems]);
                        lista.add(model);
                        canitems++;
                    }
                } else {
                    grupo[canitems] = objMenu[op].get(4);
                    String etiqueta = objMenu[op].get(0);
                    evaluados[canitems] = objMenu[op].get(2);
                    ImageView imageView = new ImageView(this);
                    if (evaluados[canitems].equals("1")) {
                        imageView.setImageResource(R.drawable.check_opt);
                    } else if (evaluados[canitems].equals("2")) {
                        imageView.setImageResource(R.drawable.equis_opt);
                    } else {
                        imageView.setImageDrawable(null);
                    }
                    TipoAMModel model = new TipoAMModel(etiqueta, evaluados[canitems]);
                    lista.add(model);
                    canitems++;
                }
            }
            TipoAMAdapter ma = new TipoAMAdapter(lista);
            recyclerTipoAM.setAdapter(ma);
        }
    }

    public void irOpcion(int pos, TipoAM holder2) {
        operaciones.queryNoData("UPDATE ACT SET VAL='" + grupo[pos] + "' WHERE VA='GRUPO'");
        Intent intent = new Intent(this, TipoASubMenu.class);
        startActivity(intent);
    }

    public void Regresar(View v) {
        String cantval = "";
        String queryValy = "SELECT cdn as CC FROM '302_PREGGRU' where ev=0";
        ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
        boolean gcompleto = true;
        if (objvaly!=null) {
            for (int op = 0; op < objvaly.length; op++) {
                String ConsultaMostrar = objvaly[op].get(0);
                if (ConsultaMostrar != null && !ConsultaMostrar.equals("0")) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0) {
                        gcompleto = false;
                    }
                }
            }
            if (gcompleto == false){
                cantval = "1";
            }else{
                cantval = "0";
            }

                if (cantval.equals("0")){
                    operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREGGENERALES'");
                }else{
                    operaciones.queryNoData("UPDATE 'ME' SET EV='0' WHERE ET='PREGGENERALES'");
                }
        }else{
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREGGENERALES'");
        }
        String queryVal = "select va from PA where pa='ultPTA'";
        ArrayList<String>[] objval = conGen.queryObjeto2val(getBaseContext(), queryVal, null);
        String cantvalx = objval[0].get(0);
        Irflujo2(cantvalx);
    }


    public static TipoAMenu getInstance() {
        return activityA;
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
        String cantval = "";
        String queryValy = "SELECT cdn as CC FROM '302_PREGGRU' where ev=0";
        ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
        boolean gcompleto = true;
        if (objvaly!= null) {
            for (int op = 0; op < objvaly.length; op++) {
                String ConsultaMostrar = objvaly[op].get(0);
                if (ConsultaMostrar != null && !ConsultaMostrar.equals("0")) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0) {
                        gcompleto = false;
                    }
                }
            }

            if (gcompleto == false){
                cantval = "1";
            }else{
                cantval = "0";
            }
            if (cantval.equals("0")){
                operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREGGENERALES'");
            }else{
                operaciones.queryNoData("UPDATE 'ME' SET EV='0' WHERE ET='PREGGENERALES'");
            }
        } else {
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='PREGGENERALES'");
        }

        Toast.makeText(getBaseContext(), "Verificando Validaciones", Toast.LENGTH_SHORT).show();
        ImageButton irval = (ImageButton) findViewById(R.id.ibValidaciones);
        irval.setClickable(false);
        String queryVal = "select count(CE) as ce from VALID";
        ArrayList<String>[] objval = conGen.queryObjeto2val(getBaseContext(), queryVal, null);
        String cantvalx = objval[0].get(0);
        if (!cantvalx.equals("0")) {
            Intent valid = new Intent(this, Validaciones.class);
            startActivity(valid);
        } else {
            irval.setClickable(true);
            Toast.makeText(getBaseContext(), "No hay validaciones en este momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void Irflujo2(String pantalla) {
        Intent p;
        switch (pantalla) {
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
            case "Partic2":
                p = new Intent(this, Participaciones2.class);
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
                p = new Intent(this, TipoASubMenu.class);
                startActivity(p);
                break;
        }
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