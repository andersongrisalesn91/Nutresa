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
import java.util.StringTokenizer;

public class Validaciones extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC,queryActVal;
    RecyclerView recyclerValid;
    ArrayList<ValidModel> lista;
    String[] opciones, evaluados, espaciosx, categoriasx,pregx,errores,tipoerror,clasiferror;
    Integer canitems = 0;
    Button fin;
    static Validaciones activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validaciones);
        activityA = this;
    }

    @Override
    public void onBackPressed() {
    }

    class Valid extends RecyclerView.ViewHolder {
        Button b;
        ImageView iv;

        public Valid(View itemView) {
            super(itemView);
            this.b = (Button) itemView.findViewById(R.id.buttonRVValidv);
            this.iv = (ImageView) itemView.findViewById(R.id.imageViewRVValidv);
        }
    }

    class ValidModel {
        String textoBoton;
        String textoEval;

        public ValidModel(String texto, String textoEval) {
            this.textoBoton = texto;
            this.textoEval = textoEval;
        }
    }

    class ValidAdapter extends RecyclerView.Adapter<Valid> {
        ArrayList<ValidModel> lista;

        public ValidAdapter(ArrayList<ValidModel> lista) {
            this.lista = lista;
        }

        @Override
        public Valid onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Valid(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_valid, parent, false));
        }

        @Override
        public void onBindViewHolder(Valid holder, final int position) {
            final ValidModel opcion = lista.get(position);
            final Valid holder2 = holder;
            holder.b.setText(opcion.textoBoton);
            //Si está evaluado, poner chulito

            if (opcion.textoEval.equals("1")) {
                holder.iv.setImageResource(R.drawable.check_opt);
            } else if (opcion.textoEval.equals("2")) {
                holder.iv.setImageResource(R.drawable.equis_opt);
            } else if (opcion.textoEval.equals("0")) {
                holder.iv.setColorFilter(R.color.light_purple);
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
        fg.ultimaPantalla("Validaciones");
        idC = fg.clienteActual();
        lista = new ArrayList<>();
        fin = (Button) findViewById(R.id.buttonIrMenu);

        //recycler view en un relative layout
        //Leer de la BD la jerarquía de los botones
        //Leer en la BD los botones (menús) a crear
        recyclerValid = (RecyclerView) findViewById(R.id.recyclerValidaciones);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerValid.setLayoutManager(llm);
        operaciones.queryNoData("UPDATE tv SET rt='1' where rt=2 and 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg  not in (select 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE as VC from 'VALID') and encuesta='" + fg.getAuditoria() + "'");
        operaciones.queryNoData("UPDATE tv SET rt='2' where rt=1 and 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg  in (select 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE as VC from 'VALID' where CE='1') and encuesta='" + fg.getAuditoria() + "'");
        operaciones.queryNoData("INSERT INTO tv (encuesta,idv,et,esp,cat,preg,rt) select '" + fg.getAuditoria() + "' as IE,IDV,COM ,ES ,CA ,PRE , '2' as RT from 'VALID' where 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE not in (select 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg as CV from tv where encuesta='" + fg.getAuditoria() + "')");

        String queryValid = "SELECT COM, PF, '0' as EV,'0' as CDM,IDV,ES,CA,PRE,CE FROM 'VALID'  where 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE not in (select 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg as CV from tv where encuesta='" + fg.getAuditoria() + "' and rt=1) order by 'VALID'.IDV asc";
        ArrayList<String>[] objValid = conGen.queryObjeto2val(getBaseContext(), queryValid, null);
        if (objValid != null) {
            errores = new String[objValid.length];
            opciones = new String[objValid.length];
            evaluados = new String[objValid.length];
            tipoerror = new String[objValid.length];
            espaciosx = new String[objValid.length];
            categoriasx = new String[objValid.length];
            pregx = new String[objValid.length];
            clasiferror = new String[objValid.length];
            for (int op = 0; op < objValid.length; op++) {
                    String etiqueta = objValid[op].get(0);
                    errores[canitems] = objValid[op].get(0);
                    opciones[canitems] = objValid[op].get(1);
                    tipoerror[canitems] = objValid[op].get(4);
                    espaciosx[canitems] = objValid[op].get(5);
                    categoriasx[canitems] = objValid[op].get(6);
                    pregx[canitems] = objValid[op].get(7);
                    clasiferror[canitems] = objValid[op].get(8);

                String queryVale = "SELECT rt FROM tv where idv=" + tipoerror[canitems] + " and esp=" + espaciosx[canitems] + " and cat=" + categoriasx[canitems] + " and preg=" + pregx[canitems] + " and encuesta='" + fg.getAuditoria() + "'";
                ArrayList<String>[] objValide = conGen.queryObjeto2val(getBaseContext(), queryVale, null);
                evaluados[canitems] = objValide[0].get(0);
                    ImageView imageView = new ImageView(this);
                    if (evaluados[canitems].equals("1")) {
                        imageView.setImageResource(R.drawable.check_opt);
                    } else if (evaluados[canitems].equals("2")) {
                        imageView.setImageResource(R.drawable.equis_opt);
                    } else {
                        imageView.setImageResource(R.drawable.uncheck);
                    }
                    ValidModel model = new ValidModel(etiqueta, evaluados[canitems]);
                    lista.add(model);
                    canitems++;

            }
            ValidAdapter ma = new ValidAdapter(lista);
            recyclerValid.setAdapter(ma);
        } else {
            operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
            Intent menu = new Intent(this, MenuOpciones.class);
            startActivity(menu);
        }
    }

    public void irOpcion(int pos, Valid holder2) {

        String Tipo = "";
        //Leer la posición y según la que sea, enviar al usuario a la vista correspondiente
        String clase = opciones[pos];

            operaciones.queryNoData("UPDATE tv SET rt='1' where idv=" + tipoerror[pos] + " and esp=" + espaciosx[pos] + " and cat=" + categoriasx[pos] + " and preg=" + pregx[pos] + " and encuesta='" + fg.getAuditoria() + "'");

        if (clase.equals("Categorias.class")) {

              StringTokenizer st =
                    new StringTokenizer(clase, ".");

            String nClase = st.nextToken();
            String ruta = "com.marketteam.desarrollo.nutresa." + nClase;
            Class<?> sigClass = null;
            try {
                operaciones.queryNoData("UPDATE ACT SET VAL='" + espaciosx[pos] + "' WHERE VA='ESP'");
                sigClass = Class.forName(ruta);
                Intent intent = new Intent(this, sigClass);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (clase.equals("Estandares.class") || clase.equals("SKUs.class")) {

            StringTokenizer st =
                    new StringTokenizer(clase, ".");

            String nClase = st.nextToken();
            String ruta = "com.marketteam.desarrollo.nutresa." + nClase;
            Class<?> sigClass = null;
            try {
                operaciones.queryNoData("UPDATE ACT SET VAL='" + espaciosx[pos] + "' WHERE VA='ESP'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + categoriasx[pos] + "' WHERE VA='CAT'");
                sigClass = Class.forName(ruta);
                Intent intent = new Intent(this, sigClass);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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

    public void Regresar(View v) {
        String queryValy = "SELECT count(COM) as CC FROM 'VALID'  where 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE not in (select 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg as CV from tv where encuesta='" + fg.getAuditoria() + "' and rt=1) order by 'VALID'.IDV asc";
        ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
        String cantval = objvaly[0].get(0);
        if (objvaly != null) {
            if (cantval.equals("0")){
                operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                Intent menu = new Intent(this, MenuOpciones.class);
                startActivity(menu);
            }else{
                operaciones.queryNoData("UPDATE ME SET EV='2' WHERE ET='VALIDACIONES'");
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 11, 0);
            }
        }else{
            operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
            Intent menu = new Intent(this, MenuOpciones.class);
            startActivity(menu);
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
        Toast.makeText(getBaseContext(),"Verificando preguntas TIPOA",Toast.LENGTH_SHORT).show();
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
    public static Validaciones getInstance() {
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