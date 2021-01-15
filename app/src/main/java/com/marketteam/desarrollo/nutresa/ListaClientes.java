package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListaClientes extends AppCompatActivity {

    ArrayList<ClienteModel> lista;
    RecyclerView recyclerView;
    EditText busq;
    String cliente;
    FuncionesGenerales fg;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    static ListaClientes activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerListaClientes);
        lista=new ArrayList<>();
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        activityA = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Login.getInstance() != null){Login.getInstance().finish();}
        if(MenuInicio.getInstance() != null){MenuInicio.getInstance().finish();}
        busq = (EditText) findViewById(R.id.autoCompleteLista);
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        //Update campo ultP
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("ListaClientes");
    }

    @Override
    public void onBackPressed() {}

    class Cliente extends RecyclerView.ViewHolder{
        TextView id;
        TextView propietario;
        TextView estab;
        public Cliente(View itemView) {
            super(itemView);
            this.id = (TextView) itemView.findViewById(R.id.VerIdCliente);
            this.propietario = (TextView) itemView.findViewById(R.id.VerPropietario);
            this.estab = (TextView) itemView.findViewById(R.id.VerEstablecimiento);
        }
    }

    class ClienteModel{
        String id, propietario, estab;
        public ClienteModel(String id, String propietario, String establecimiento) {
            this.id = id;
            this.propietario = propietario;
            this.estab = establecimiento;
        }
    }

    class ClienteAdapter extends RecyclerView.Adapter<Cliente>{
        ArrayList<ClienteModel> lista;

        public ClienteAdapter(ArrayList<ClienteModel> lista) {
            this.lista = lista;
        }


        @Override
        public Cliente onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Cliente(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_cliente2,parent,false));
        }

        @Override
        public void onBindViewHolder(Cliente holder, int position) {
            final ClienteModel per=lista.get(position);
            holder.id.setText(per.id);
            holder.propietario.setText(per.propietario);
            holder.estab.setText(per.estab);

            final String sId = per.id;
            final String sP = per.propietario;
            final String sE = per.estab;

            holder.id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    funcPopUp(sId, sP, sE);
                }
            });

            holder.propietario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {funcPopUp(sId, sP, sE);}
            });

            holder.estab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {funcPopUp(sId, sP, sE);}
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    public void BuscarCliente(View v){
        String queryTipoM = "SELECT VAL FROM ACT WHERE VA='TIPOM'";
        ArrayList<String>[] tipox = conGen.queryObjeto2val(getBaseContext(), queryTipoM, null);
        String tipom = tipox[0].get(0);
        if (tipom.equals("0")){
            tipom = "1";
        }
        if(lista.size() > 0){
            lista.clear();
        }
        //Realizar la búsqueda
        String [] carac = {"|", "°", "¬", "!", "\"", "@", "#", "$","%","/","\'","(", ")", "=", "?", "'", "¿", "¡",
                            "´", "¨", "+", "*", "{", "}", "[", "]", "^", "`", "-", "_", ".", ":", ",", ";", "<", ">"};
        cliente = busq.getText().toString().toUpperCase();
        if(cliente.equals("") || cliente == null || cliente.length() < 4){
            Toast.makeText(getBaseContext(), "Se requieren 4 caracteres válidos como mínimo para iniciar la búsqueda", Toast.LENGTH_SHORT).show();
        } else {
            boolean val = true;
            for (int i = 0; i < cliente.length(); i++){
                for (int j = 0; j < carac.length; j++){
                    if(cliente.contains(carac[j])){
                        val = false;
                        break;
                    }
                }
            }
            if(val){
                String like1 = "((CLI = '"+ cliente +"' OR CLI LIKE '%" + cliente + "%')";
                String like2 = "(NCLI1 = '"+ cliente +"' OR NCLI1 LIKE '%" + cliente + "%')";
                String like3 = "(NCLI2 = '"+ cliente +"' OR NCLI2 LIKE '%" + cliente + "%')";
                String like = like1 + " OR " + like2 + " OR " + like3 + ") AND TIPO='" + tipom + "'";
                ConsultaGeneral query = new ConsultaGeneral();
                ArrayList<String> [] objRes = query.queryGeneral(getBaseContext(),"'300_MAESTRO'", new String[]{"CLI", "NCLI1", "NCLI2"}, like);
                if(objRes != null){
                    int size = objRes.length;
                    for (int i = 0; i < size; i++){
                        String idObj = objRes[i].get(0).toString();
                        String propObj = objRes[i].get(1).toString();
                        String estbObj = objRes[i].get(2).toString();
                        ClienteModel cl = new ClienteModel(idObj, propObj, estbObj);
                        lista.add(cl);
                    }
                    ClienteAdapter adapter=new ClienteAdapter(lista);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getBaseContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getBaseContext(), "No se permiten caracteres especiales como: !#$%&/(.),*-+=?¡", Toast.LENGTH_SHORT).show();
            }
            View view = this.getCurrentFocus();
            view.clearFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void funcPopUp(String id, String propietario, String estab) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        PopUps pop = new PopUps();
        pop.popUpCliente(getBaseContext(),inflater, new String[]{id,propietario,estab});
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

    public static ListaClientes getInstance(){ return activityA; }

    @Override
    protected void onStop() {
        super.onStop();
        operaciones.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        operaciones.close();
    }

    public void Volver(View v){
        startActivity(new Intent(this,SeleccionCuestionario.class));
    }
}