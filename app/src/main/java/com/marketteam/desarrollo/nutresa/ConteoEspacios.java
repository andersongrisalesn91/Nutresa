package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConteoEspacios extends AppCompatActivity {

    ArrayList<ConteoEspaciosModel> lista;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, espAct, idcont;
    RecyclerView recyclerConteoEspacios;
    TextView cantt;
    String[] ConteoEspaciosID, CategoriasID, idTabla6;
    static ConteoEspacios activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteoespacios);
        activityA = this;
    }

    class CONEsp extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView tiene;
        TextView cantidad;
        RelativeLayout rlit;
        public CONEsp(View itemView) {
            super(itemView);
            this.nombre = (TextView) itemView.findViewById(R.id.tvNombreContEspacio);
            this.tiene = (TextView) itemView.findViewById(R.id.tvRtaCE);
            this.cantidad = (EditText) itemView.findViewById(R.id.tvCant);
            this.rlit = (RelativeLayout) itemView.findViewById(R.id.RLCEspacios);
        }
    }

    class ConteoEspaciosModel {
        String nombre, tiene, cantidad;

        public ConteoEspaciosModel(String nombre, String tiene, String cantidad) {
            this.nombre = nombre;
            this.tiene = tiene;
            this.cantidad = cantidad;
        }
    }

    class ConteoEspaciosAdapter extends RecyclerView.Adapter<CONEsp> {
        ArrayList<ConteoEspaciosModel> lista;

        public ConteoEspaciosAdapter(ArrayList<ConteoEspaciosModel> lista) {
            this.lista = lista;
        }

        @Override
        public CONEsp onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CONEsp(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_contesp, parent, false));
        }

        @Override
        public void onBindViewHolder(CONEsp holder, final int position) {
            final CONEsp holder2 = holder;
            final ConteoEspaciosModel ConteoEspacios = lista.get(position);
            if (Integer.parseInt(espAct) == 3 || Integer.parseInt(espAct) == 9) {
                holder.cantidad.setVisibility(View.VISIBLE);
                holder.tiene.setVisibility(View.VISIBLE);
            }else{
                holder.tiene.setVisibility(View.VISIBLE);
                holder.cantidad.setVisibility(View.INVISIBLE);
            }
            holder.nombre.setText(ConteoEspacios.nombre);
            holder.tiene.setText(ConteoEspacios.tiene);
            if (ConteoEspacios.cantidad.equals("0")){
                holder.cantidad.setText(null);
            } else {
                holder.cantidad.setText(ConteoEspacios.cantidad);
            }
            String idConteoEspacios = ConteoEspaciosID[position];

            //holder.imgV.setImageDrawable();
            if (Integer.parseInt(espAct) == 3 || Integer.parseInt(espAct) == 9) {
                holder.cantidad.addTextChangedListener(new listener(position));
            }else{
                holder.nombre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarCONEsp(position, holder2, ConteoEspacios);
                    }
                });
                holder.tiene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarCONEsp(position, holder2, ConteoEspacios);
                    }
                });
                holder.rlit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarCONEsp(position, holder2, ConteoEspacios);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    public class listener implements TextWatcher {

        int pos;

        public listener(int position) {
            this.pos = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ((lista.isEmpty()) || (lista.size() > 0)) {
                lista.get(pos).cantidad = s.toString();
            } else {
                Toast.makeText(getBaseContext(), "La lista está vacía", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DetalleCliente.getInstance() != null) {
            DetalleCliente.getInstance().finish();
        }
        cantt = (TextView) findViewById(R.id.textView51);
        operaciones = new OperacionesBDInterna(getApplicationContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerConteoEspacios = (RecyclerView) findViewById(R.id.recyclerViewContEspacios);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerConteoEspacios.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("ConteoEspacios");
        idC = fg.clienteActual();
        listarConteoEspacios();
    }

    public void listarConteoEspacios() {
        //Traer los datos actuales
        ArrayList<String> resCampos = fg.existeACT(3);
        String canalAct = resCampos.get(0);
        String subcAct = resCampos.get(1);

        espAct = resCampos.get(2);
        if (Integer.parseInt(espAct) == 3 || Integer.parseInt(espAct) == 9) {
            cantt.setVisibility(View.VISIBLE);
            idcont = "1";
        } else {
            cantt.setVisibility(View.INVISIBLE);
            idcont = fg.getContesp();
        }
        String queryCONEsp = "SELECT id,cat,ncat,rta,cant FROM t6t where id=" + idcont + " and esp=" + espAct + " order by t6t.id,t6t.cat asc";
        //Con el canal, traigo los respectivos ConteoEspacios
        //ArrayList<String>[] ConteoEspacios = conGen.queryGeneral(getApplicationContext(),"'203_ESP'", new String[]{"ESP","IDT2"},"IDC='"+canalAct+"'");
        ArrayList<String>[] ConteoEspacios = conGen.queryObjeto2val(getBaseContext(), queryCONEsp, null);
        if (ConteoEspacios != null) {
            ConteoEspaciosID = new String[ConteoEspacios.length];
            CategoriasID = new String[ConteoEspacios.length];
            idTabla6 = new String[ConteoEspacios.length];
            for (int c = 0; c < ConteoEspacios.length; c++) {
                String temp = ConteoEspacios[c].get(0);
                ConteoEspaciosID[c] = temp;
                CategoriasID[c] = ConteoEspacios[c].get(1);
                idTabla6[c] = temp;
                String ncat = ConteoEspacios[c].get(2);
                String rta = ConteoEspacios[c].get(3);
                String rtaT = "";
                if (rta.equals("1")) {
                    rtaT = "SI";
                } else if (rta.equals("2")) {
                    rtaT = "NO";
                } else if (rta.equals("0")) {
                    rtaT = "";
                }
                String cant = ConteoEspacios[c].get(4);
                ConteoEspaciosModel em = new ConteoEspaciosModel(ncat, rtaT, cant);
                lista.add(em);
            }
            ConteoEspaciosAdapter ea = new ConteoEspaciosAdapter(lista);
            recyclerConteoEspacios.setAdapter(ea);
        }
    }

    public void editarCONEsp(int pos, CONEsp holder, ConteoEspaciosModel CONEspm) {
        final int pos2 = pos;
        final ConteoEspaciosModel product = CONEspm;
        String queryCONEsprta = "SELECT t6t.rta FROM t6t where id=" + idcont + " and esp=" + espAct + " and cat=" + CategoriasID[pos2] + "";
        ArrayList<String>[] rtaCONEsp = conGen.queryObjeto2val(getBaseContext(), queryCONEsprta, null);
        if (!espAct.equals("3") && !espAct.equals("9")) {
            if (Integer.parseInt(rtaCONEsp[0].get(0)) == 1) {
                at.actualizarT6(idcont, espAct, 2, CategoriasID[pos2], "0");
                holder.tiene.setText("NO");
                product.tiene = "NO";
                holder.cantidad.setText("");
                product.cantidad = "";
                holder.cantidad.setVisibility(View.INVISIBLE);
            } else if (Integer.parseInt(rtaCONEsp[0].get(0)) == 2) {
                at.actualizarT6(idcont, espAct, 1, CategoriasID[pos2], "0");
                holder.tiene.setText("SI");
                product.tiene = "SI";
            } else if (Integer.parseInt(rtaCONEsp[0].get(0)) == 0) {
                at.actualizarT6(idcont, espAct, 1, CategoriasID[pos2], "0");
                holder.tiene.setText("SI");
                product.tiene = "SI";
            }
        }
    }

    public void Volver(View v) {
        if (espAct.equals("3") || espAct.equals("9")){
            String[] valoresCant = new String[lista.size()];
            for (int d = 0; d < lista.size(); d++) {
                ConteoEspaciosModel pm = lista.get(d);
                valoresCant[d] = pm.cantidad;
                at.actualizarT6(idcont, espAct, 4, CategoriasID[d], valoresCant[d]);
            }
        }
        String queryCat = "SELECT COUNT(rta) FROM t6t WHERE cat < 100 AND rta=2 AND id=" + idcont;
        ArrayList<String>[] catNo = conGen.queryObjeto2val(getBaseContext(), queryCat,null);
        if(catNo != null){
            int cant = Integer.parseInt(catNo[0].get(0));
            String queryCont = "SELECT COUNT(rta) FROM t6t WHERE cat < 100 AND id=" + idcont;
            ArrayList<String>[] contNo = conGen.queryObjeto2val(getBaseContext(), queryCont,null);
            if(contNo != null){
                int cant2 = Integer.parseInt(contNo[0].get(0));
                if(cant2 == 0){
                    String queryCat2 = "SELECT COUNT(rta) FROM t6t WHERE rta=2 AND id=" + idcont;
                    ArrayList<String>[] catNo2 = conGen.queryObjeto2val(getBaseContext(), queryCat2,null);
                    if(catNo2 != null){
                        int cant3 = Integer.parseInt(catNo2[0].get(0));
                        String queryCont2 = "SELECT COUNT(rta) FROM t6t WHERE id=" + idcont;
                        ArrayList<String>[] contNo2 = conGen.queryObjeto2val(getBaseContext(), queryCont2,null);
                        if(contNo2 != null){
                            int cant4 = Integer.parseInt(contNo2[0].get(0));
                            if(cant3 == cant4){
                                //Mensaje error
                                Toast.makeText(getBaseContext(),"Revise que todas las categorias tengan respuesta", Toast.LENGTH_SHORT).show();
                            } else {
                                String queryCONEsprta = "SELECT rta FROM t6t where id=" + idcont + " and (((esp=3 or esp=9) and (rta=0 or cant=0 or rta is null or cant is null)) or ((esp<>3 and esp<>9) and rta=0))";
                                ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryCONEsprta, null);
                                if (eval == null) {
                                    if (!espAct.equals("3") && !espAct.equals("9")){
                                        operaciones.queryNoData("UPDATE ACT SET VAL='" + (Integer.parseInt(idcont) + 1) + "' WHERE VA='CONESP'");
                                    }
                                    Intent volver = new Intent(this, Categorias.class);
                                    startActivity(volver);
                                } else {
                                    Toast.makeText(getBaseContext(), "Revise que todas las categorias tengan respuesta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } else if(cant == cant2) {
                    //Mensaje de faltantes
                    Toast.makeText(getBaseContext(), "Revise que todas las categorias tengan respuesta", Toast.LENGTH_SHORT).show();
                } else {
                    String queryCONEsprta = "SELECT rta FROM t6t where id=" + idcont + " and (((esp=3 or esp=9) and (rta=0 or cant=0 or rta is null or cant is null)) or ((esp<>3 and esp<>9) and rta=0))";
                    ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryCONEsprta, null);
                    if (eval == null) {
                        if (!espAct.equals("3") && !espAct.equals("9")){
                            operaciones.queryNoData("UPDATE ACT SET VAL='" + (Integer.parseInt(idcont) + 1) + "' WHERE VA='CONESP'");
                        }
                        Intent volver = new Intent(this, Categorias.class);
                        startActivity(volver);
                    } else {
                        Toast.makeText(getBaseContext(), "Revise que todas las categorias tengan respuesta", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void Borrarpoc(View v) {
        if (espAct.equals("3") || espAct.equals("9")){

            String[] valoresCant = new String[lista.size()];
            for (int d = 0; d < lista.size(); d++) {
                ConteoEspaciosModel pm = lista.get(d);
                valoresCant[d] = pm.cantidad;
                at.actualizarT6(idcont, espAct, 4, CategoriasID[d], valoresCant[d]);
            }
            String queryCONEsprta = "SELECT rta FROM t6t where id=" + idcont + " and (((esp=3 or esp=9) and (rta=0 or cant=0 or rta is null or cant is null)) or ((esp<>3 and esp<>9) and rta=0))";
            ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryCONEsprta, null);
            if (eval == null) {
                if (!espAct.equals("3") && !espAct.equals("9")){
                    operaciones.queryNoData("UPDATE ACT SET VAL='" + (Integer.parseInt(idcont) + 1) + "' WHERE VA='CONESP'");
                }
                Intent volver = new Intent(this, Categorias.class);
                startActivity(volver);
            } else {
                Toast.makeText(getBaseContext(), "Debe llenar toda las ristras para continuar", Toast.LENGTH_SHORT).show();
            }
        } else {
            PopUps pop = new PopUps();
            final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            pop.popUpConf(getBaseContext(), inflater, 9, 1);
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
            fg.ultimaPantallafta("ConteoEspacios");
            Intent valid = new Intent(this, TipoAMenu.class);
            startActivity(valid);
        } else {
            Toast.makeText(getBaseContext(), "No hay Preguntas TipoA Disponibles en este momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void irValidaciones(View view) {
        Toast.makeText(getBaseContext(),"Verificando Validaciones",Toast.LENGTH_SHORT).show();
        ImageButton irval = (ImageButton) findViewById(R.id.ibValidaciones);
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

    public static ConteoEspacios getInstance() {
        return activityA;
    }
}


