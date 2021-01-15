package com.marketteam.desarrollo.nutresa;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MenuInicio extends AppCompatActivity {

    RecyclerView recyclerMenuIni;
    ArrayList<MenuModel> lista;
    ConsultaGeneral cGeneral;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    static MenuInicio activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicio);
        recyclerMenuIni=(RecyclerView)findViewById(R.id.recyclerViewMenuI);
        lista=new ArrayList<>();
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMenuIni.setLayoutManager(llm);
        activityA = this;
    }

    class Menu extends RecyclerView.ViewHolder{
        TextView audit;
        TextView cli;
        TextView estab;
        public Menu(View itemView){
            super(itemView);
            this.audit = (TextView) itemView.findViewById(R.id.tvRetIdAuditoria);
            this.cli = (TextView) itemView.findViewById(R.id.tvRetIdCliente);
            this.estab = (TextView) itemView.findViewById(R.id.tvRetEstab);
        }
    }

    class MenuModel{
        String audit, cli, estab;
        public MenuModel(String audit, String cliente, String establec){
            this.audit = audit;
            this.cli = cliente;
            this.estab = establec;
        }
    }

    class MenuAdapter extends RecyclerView.Adapter<Menu>{
        ArrayList<MenuModel> lista;

        public  MenuAdapter(ArrayList<MenuModel> lista){this.lista = lista;}

        @Override
        public Menu onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Menu(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_auditoria,parent,false));
        }

        @Override
        public void onBindViewHolder(Menu holder, int position) {
            final MenuModel opcion = lista.get(position);
            holder.audit.setText(opcion.audit);
            holder.cli.setText(opcion.cli);
            holder.estab.setText(opcion.estab);
            //Poner opcion.cli en PA where  VA='cliA'
            final String cliente = opcion.cli;
            final String encuesta = opcion.audit;
            holder.audit.setOnClickListener(new View.OnClickListener() {
                @Override
               public void onClick(View v) { retomarAuditoria(cliente,encuesta); }
            });
            holder.cli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { retomarAuditoria(cliente,encuesta); }
            });
            holder.estab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { retomarAuditoria(cliente,encuesta); }
            });
        }

        @Override
        public int getItemCount() { return lista.size(); }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ListaClientes.getInstance() != null){ListaClientes.getInstance().finish();}
        cGeneral = new ConsultaGeneral();
        operaciones = new OperacionesBDInterna(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("MenuI");
        lista = new ArrayList<>();
        Button iniciar = (Button) findViewById(R.id.buttonIniciarAud);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAuditoria();
            }
        });
        //Mostrar encuestas con estado Aplazado
        ArrayList<String>[] objAudi;
        String queryTipoM = "SELECT VAL FROM ACT WHERE VA='TIPOM'";
        ArrayList<String>[] tipox = cGeneral.queryObjeto2val(getBaseContext(), queryTipoM, null);
        String tipom = tipox[0].get(0);
        if(tipom.equals("1")){
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT) < 4 AND CAST(maestro1 AS INT)<1000");
        } else if(tipom.equals("3")){
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT)=1000");
        }else if(tipom.equals("4")){
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT) < 4 AND CAST(maestro1 AS INT)>1000");
        } else {
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT) > 3 AND CAST(maestro8 AS INT) < 1000");
        }
        if(objAudi != null){
            for(int a = 0; a < objAudi.length; a++){
                String encuesta = objAudi[a].get(0);
                String cliente = objAudi[a].get(1);
                String establecimiento = objAudi[a].get(2);
                MenuModel mm = new MenuModel(encuesta,cliente,establecimiento);
                lista.add(mm);
            }
            MenuAdapter ma = new MenuAdapter(lista);
            recyclerMenuIni.setAdapter(ma);
        } else {
            Toast.makeText(getBaseContext(),"No hay encuestas aplazadas", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void iniciarAuditoria(){
        Intent lista = new Intent(this,ListaClientes.class);
        startActivity(lista);
    }

    public void retomarAuditoria(String id,String enc){
        //Actualizar cliente actual
        String query = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] obj = cGeneral.queryObjeto(this,query, new String[]{"cliA"});
        if(obj == null){
            ContentValues cv = new ContentValues();
            cv.put("pa","cliA");
            cv.put("va",id);
            boolean insert = operaciones.insertar("PA", cv);
            if(!insert){
                Toast.makeText(this, "Registro no insertado", Toast.LENGTH_SHORT).show();
            } else {
                //Actualizar pa=opc en PA con va=1
                //Va con flujo 1
                operaciones.queryNoData("UPDATE ACT SET VAL='" + enc + "' WHERE VA='AUD'");
                operaciones.queryNoData("UPDATE PA SET va='2' WHERE pa='opc'");
                Intent detalle = new Intent(this,DetalleCliente.class);
                startActivity(detalle);
            }
        } else {
            //Update al campo cliA
            ContentValues cv = new ContentValues();
            cv.put("va",id);
            boolean update = operaciones.actualizar("PA",cv, "pa='cliA'",null);
            if(!update){
                Toast.makeText(this, "Registro no actualizado", Toast.LENGTH_SHORT).show();
            } else {
                //Actualizar pa=opc en PA con va=1
                //Va con flujo 1
                operaciones.queryNoData("UPDATE ACT SET VAL='" + enc + "' WHERE VA='AUD'");
                operaciones.queryNoData("UPDATE PA SET va='2' WHERE pa='opc'");
                Intent detalle = new Intent(this,DetalleCliente.class);
                startActivity(detalle);
            }
        }
    }

    public void sincronizar(View vista) {
        SincronizaVF s = new SincronizaVF(getBaseContext());
        s.sincronizarTablasVF();
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
        ArrayList<String>[] objval = cGeneral.queryObjeto2val(getBaseContext(), queryVal, null);
        String cantval = objval[0].get(0);
        if (!cantval.equals("0")) {
            Intent valid = new Intent(this, Validaciones.class);
            startActivity(valid);
        } else {
            irval.setClickable(true);
            Toast.makeText(getBaseContext(), "No hay validaciones en este momento", Toast.LENGTH_SHORT).show();
        }
    }


    public void Volver(View v){
        startActivity(new Intent(this,SeleccionCuestionario.class));
    }

    public static MenuInicio getInstance(){ return activityA; }
}