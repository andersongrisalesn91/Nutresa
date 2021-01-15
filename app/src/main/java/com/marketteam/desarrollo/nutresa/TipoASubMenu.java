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

public class TipoASubMenu extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, flujo, grupop;
    RecyclerView recyclerTipoASM;
    ArrayList<TipoASMModel> lista;
    String[] subgrupo, evaluados, tipo;
    Integer canitems = 0;
    Button fin;
    static TipoASubMenu activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoasubmenu);
        activityA = this;
    }

    @Override
    public void onBackPressed() {
    }


    class TipoASM extends RecyclerView.ViewHolder {
        Button b;
        ImageView iv;

        public TipoASM(View itemView) {
            super(itemView);
            this.b = (Button) itemView.findViewById(R.id.buttonRVSubMenuTA);
            this.iv = (ImageView) itemView.findViewById(R.id.imageViewRVSubMenuTA);
        }
    }

    class TipoASMModel {
        String textoBoton;
        String textoEval;

        public TipoASMModel(String texto, String textoEval) {
            this.textoBoton = texto;
            this.textoEval = textoEval;
        }
    }

    class TipoASMAdapter extends RecyclerView.Adapter<TipoASM> {
        ArrayList<TipoASMModel> lista;

        public TipoASMAdapter(ArrayList<TipoASMModel> lista) {
            this.lista = lista;
        }

        @Override
        public TipoASM onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TipoASM(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_submenuta, parent, false));
        }

        @Override
        public void onBindViewHolder(TipoASM holder, final int position) {
            final TipoASMModel opcion = lista.get(position);
            final TipoASM holder2 = holder;
            holder.b.setText(opcion.textoBoton);
            //Si está evaluado, poner chulito

            if (opcion.textoEval.equals("1")) {
                holder.iv.setImageResource(R.drawable.check_opt);
            } else if (opcion.textoEval.equals("2")) {
                holder.iv.setImageResource(R.drawable.equis_opt);
            } else if (opcion.textoEval.equals("0")) {
                holder.iv.setImageDrawable(null);
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
        fg.ultimaPantalla("TipoASMenu");
        idC = fg.clienteActual();
        lista = new ArrayList<>();
        fin = (Button) findViewById(R.id.buttonIrMenu);
        String querymino = "SELECT VAL FROM ACT where VA='GRUPO'";
        ArrayList<String>[] mino = conGen.queryObjeto2val(getBaseContext(), querymino, null);
        grupop = mino[0].get(0);
        //recycler view en un relative layout
        //Leer de la BD la jerarquía de los botones
        //Leer en la BD los botones (menús) a crear
        recyclerTipoASM = (RecyclerView) findViewById(R.id.recyclertipoasubmenu);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerTipoASM.setLayoutManager(llm);
        String queryMenu = "SELECT preg, tipo, eval, cnd2,orden FROM '302_PREGGEN' where aplica=1 and ap=1 and gr=" + grupop + " order by orden asc";
        ArrayList<String>[] objMenu = conGen.queryObjeto2val(getBaseContext(), queryMenu, null);
        if (objMenu != null) {
            subgrupo = new String[objMenu.length];
            evaluados = new String[objMenu.length];
            tipo = new String[objMenu.length];
            for (int op = 0; op < objMenu.length; op++) {
                String ConsultaMostrar = objMenu[op].get(3);
                if (ConsultaMostrar != null && !ConsultaMostrar.equals("0")) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0 || mostrar[0].get(0) == null || mostrar[0].get(0) == "") {
                        tipo[canitems] = objMenu[op].get(1);
                        subgrupo[canitems] = objMenu[op].get(4);
                        String etiqueta = objMenu[op].get(0);
                        evaluados[canitems] = objMenu[op].get(2);
                        ImageView imageView = new ImageView(this);
                        TipoASMModel model = new TipoASMModel(etiqueta, evaluados[canitems]);
                        lista.add(model);
                        canitems++;
                    }
                } else {
                    tipo[canitems] = objMenu[op].get(1);
                    subgrupo[canitems] = objMenu[op].get(4);
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
                    TipoASMModel model = new TipoASMModel(etiqueta, evaluados[canitems]);
                    lista.add(model);
                    canitems++;
                }
            }
            TipoASMAdapter ma = new TipoASMAdapter(lista);
            recyclerTipoASM.setAdapter(ma);
        }
    }

    public void irOpcion(int pos, TipoASM holder2) {
        operaciones.queryNoData("UPDATE ACT SET VAL='" + subgrupo[pos] + "' WHERE VA='ORDEN'");

        if (Integer.parseInt(tipo[pos]) == 1) {
            Intent intent = new Intent(this, Activaciones.class);
            startActivity(intent);
        } else if (Integer.parseInt(tipo[pos]) == 2) {
            Intent intent = new Intent(this, Activacionesrd.class);
            startActivity(intent);
        } else if (Integer.parseInt(tipo[pos]) == 3) {
            Intent intent = new Intent(this, Activacionestb.class);
            startActivity(intent);
        } else if (Integer.parseInt(tipo[pos]) == 4) {
            Intent intent = new Intent(this, Activacionestbt.class);
            startActivity(intent);
        } else if (Integer.parseInt(tipo[pos]) == 5) {
            Intent intent = new Intent(this, Activacionesrdsn.class);
            startActivity(intent);
        }
    }

    public void Regresar(View v) {
        String cantval = "";
        String queryValy = "SELECT cnd2 as CC FROM '302_PREGGEN' where aplica=1 and ap=1 and eval=0 and gr=" + grupop;
        ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
        boolean gcompleto = true;
        if (objvaly != null) {
            for (int op = 0; op < objvaly.length; op++) {
                String ConsultaMostrar = objvaly[op].get(0);
                if (ConsultaMostrar != null && !ConsultaMostrar.equals("0")) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0) {
                        gcompleto = false;
                    }
                } else if (ConsultaMostrar.equals("0")) {
                    gcompleto = false;
                }
            }
            if (gcompleto == false) {
                cantval = "1";
            } else {
                cantval = "0";
            }
            if (cantval.equals("0")) {
                operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='1' WHERE grpid=" + grupop);
                Intent menu = new Intent(this, TipoAMenu.class);
                startActivity(menu);
            } else {
                operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='0' WHERE grpid=" + grupop);
                Intent menu = new Intent(this, TipoAMenu.class);
                startActivity(menu);
            }
        } else {
            operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='1' WHERE grpid=" + grupop);
            Intent menu = new Intent(this, TipoAMenu.class);
            startActivity(menu);
        }

    }


    public static TipoASubMenu getInstance() {
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
        Toast.makeText(getBaseContext(), "Verificando preguntas TIPOA", Toast.LENGTH_SHORT).show();
        if (aplicaTA.equals("1")) {
            Intent valid = new Intent(this, TipoAMenu.class);
            startActivity(valid);
        } else {
            Toast.makeText(getBaseContext(), "No hay Preguntas TipoA Disponibles en este momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void irValidaciones(View view) {
        Toast.makeText(getBaseContext(), "Verificando Validaciones", Toast.LENGTH_LONG).show();

        String cantval = "";
        String queryValy = "SELECT cnd2 as CC FROM '302_PREGGEN' where aplica=1 and ap=1 and eval=0 and gr=" + grupop;
        ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
        boolean gcompleto = true;
        if (objvaly != null) {
            for (int op = 0; op < objvaly.length; op++) {
                String ConsultaMostrar = objvaly[op].get(0);
                if (ConsultaMostrar != null && !ConsultaMostrar.equals("0")) {
                    ArrayList<String>[] mostrar = conGen.queryObjeto2val(getBaseContext(), ConsultaMostrar, null);
                    if (Integer.parseInt(mostrar[0].get(0)) > 0) {
                        gcompleto = false;
                    }
                }
            }
            if (gcompleto == false) {
                cantval = "1";
            } else {
                cantval = "0";
            }
            if (cantval.equals("0")) {
                operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='1' WHERE grpid=" + grupop);
            } else {
                operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='0' WHERE grpid=" + grupop);
            }
        } else {
            operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='1' WHERE grpid=" + grupop);
        }

        ImageButton irval = (ImageButton) findViewById(R.id.ibValidaciones);
        irval.setClickable(false);
        String queryVal = "select count(CE) as ce from VALID";
        ArrayList<String>[] objval = conGen.queryObjeto2val(getBaseContext(), queryVal, null);
        if (objval!=null) {
            String cantvalx = objval[0].get(0);
            if (!cantvalx.equals("0")) {
                Intent valid = new Intent(this, Validaciones.class);
                startActivity(valid);
            } else {
                irval.setClickable(true);
                Toast.makeText(getBaseContext(), "No hay validaciones en este momento", Toast.LENGTH_SHORT).show();
            }
        } else {
            irval.setClickable(true);
            Toast.makeText(getBaseContext(), "No hay validaciones en este momento", Toast.LENGTH_SHORT).show();
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