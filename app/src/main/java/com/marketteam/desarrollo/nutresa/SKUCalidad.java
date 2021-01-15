package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SKUCalidad extends AppCompatActivity {

    ArrayList<SKUCalidadModel> lista;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC;
    RecyclerView recyclerSKUCalidad;
    String[] SKUCalidadID, idTabla10;
    static SKUCalidad activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skucalidad);
        activityA = this;
    }

    class SKUCal extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView tiene;
        TextView mismo;

        public SKUCal(View itemView) {
            super(itemView);
            this.nombre = (TextView) itemView.findViewById(R.id.tvNombreSkuCalidad);
            this.tiene = (TextView) itemView.findViewById(R.id.tvRtaS);
            this.mismo = (TextView) itemView.findViewById(R.id.tvRtaM);
        }
    }

    class SKUCalidadModel {
        String nombre, tiene, mismo;
        public SKUCalidadModel(String nombre, String tiene, String mismo) {
            this.nombre = nombre;
            this.tiene = tiene;
            this.mismo = mismo;
        }
    }

    class SKUCalidadAdapter extends RecyclerView.Adapter<SKUCal> {
        ArrayList<SKUCalidadModel> lista;

        public SKUCalidadAdapter(ArrayList<SKUCalidadModel> lista) {
            this.lista = lista;
        }

        @Override
        public SKUCal onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SKUCal(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_skucalidad, parent, false));
        }

        @Override
        public void onBindViewHolder(SKUCal holder, final int position) {
            final SKUCal holder2 = holder;
            final SKUCalidadModel SKUCalidad = lista.get(position);
            holder.nombre.setText(SKUCalidad.nombre);
            holder.tiene.setText(SKUCalidad.tiene);
            holder.mismo.setText(SKUCalidad.mismo);
            String idSKUCalidad = SKUCalidadID[position];

            holder.tiene.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarSku2(position, holder2, SKUCalidad);
                }
            });
            holder.mismo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String campo1 = holder2.tiene.getText().toString();
                    if(!(campo1.equals("N/A")) && !(SKUCalidad.tiene.equals("N/A"))){
                        editarSKUCalM(position, holder2, SKUCalidad);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {return lista.size();}
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onStart() {
        super.onStart();
        if (DetalleCliente.getInstance() != null) {
            DetalleCliente.getInstance().finish();
        }
        if (MenuOpciones.getInstance() != null) {
            MenuOpciones.getInstance().finish();
        }
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerSKUCalidad = (RecyclerView) findViewById(R.id.recyclerSKUCalidad);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerSKUCalidad.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("SKUCalidad");
        idC = fg.clienteActual();
        listarSkuCalidad();
    }

    public void listarSkuCalidad() {
        //Traer los datos actuales
        ArrayList<String> resCampos = fg.existeACT(3);
        String canalAct = resCampos.get(0);
        String subcAct = resCampos.get(1);
        String SKUCalt = resCampos.get(2);
        String querySKUCal = "SELECT sku,nsku,rta,mis FROM t10t order by t10t.cat,t10t.mar,t10t.sku asc";
        //Con el canal, traigo los respectivos SKUCalidad
        ArrayList<String>[] SKUCalidad = conGen.queryObjeto2val(getBaseContext(), querySKUCal, null);
        if (SKUCalidad != null) {
            SKUCalidadID = new String[SKUCalidad.length];
            idTabla10 = new String[SKUCalidad.length];
            for (int c = 0; c < SKUCalidad.length; c++) {
                String temp = SKUCalidad[c].get(0);
                SKUCalidadID[c] = temp;
                idTabla10[c] = temp;
                String skucalx = SKUCalidad[c].get(1);
                String rta = SKUCalidad[c].get(2);
                String mis = SKUCalidad[c].get(3);
                String rtaT = "";
                String misT = "";
                if (rta.equals("1")) {
                    rtaT="SI";
                } else if (rta.equals("2")) {
                    rtaT="NO";
                } else if (rta.equals("3")) {
                    rtaT="N/A";
                } else if (rta.equals("0")) {
                    rtaT="";
                }
                if (mis!=null) {
                    if (mis.equals("1")) {
                        misT = "SI";
                    } else if (mis.equals("2")) {
                        misT = "NO";
                    } else if (mis.equals("0")) {
                        misT = "";
                    }
                }
                SKUCalidadModel em = new SKUCalidadModel(skucalx, rtaT,misT);
                lista.add(em);
                SKUCalidadAdapter ea = new SKUCalidadAdapter(lista);
                recyclerSKUCalidad.setAdapter(ea);

            }
        }
    }

    public void editarSku2(int pos, SKUCal holder, SKUCalidadModel skucal){
        final int pos2 = pos;
        final SKUCal holder2 = holder;
        final SKUCalidadModel product = skucal;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_skucalidad, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoSkusC);
        String tx = getString(R.string.skus_calidad);
        texto.setText(tx);
        Button no = (Button) popUp.findViewById(R.id.buttonSkuNo);
        Button si = (Button) popUp.findViewById(R.id.buttonSkuSi);
        Button nAplica = (Button) popUp.findViewById(R.id.buttonSkuAplica);
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        nAplica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("N/A");
                product.tiene = "N/A";
                holder2.mismo.setText("TIENE");
                product.mismo = "TIENE";
                //Guardar respuesta
                operaciones.queryNoData("UPDATE t10t SET rta=3,mis=0 WHERE encuesta='" + fg.getAuditoria() + "' AND sku='" + SKUCalidadID[pos2] + "'");
                popupWindow.dismiss();}
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("NO");
                product.tiene = "NO";
                //Guardar respuesta
                operaciones.queryNoData("UPDATE t10t SET rta=2 WHERE encuesta='" + fg.getAuditoria() + "' AND sku='" + SKUCalidadID[pos2] + "'");
                //TomarFoto case:10
                operaciones.queryNoData("UPDATE ACT SET VAL='10' WHERE VA='FOTO'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + SKUCalidadID[pos2] + "' WHERE VA='SKU_C'");
                //Verificar si ya tiene foto
                ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(),"t10t",new String[]{"foto"},"encuesta='" + fg.getAuditoria() + "' AND sku='" + SKUCalidadID[pos2] + "'");
                String existe = foto[0].get(0);
                if(existe != null){
                    if(!existe.equals("1")){
                        tFoto();
                    } else {
                        Toast.makeText(getBaseContext(),"Foto ya almacenada",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tFoto();
                }
                popupWindow.dismiss();}
        });
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("SI");
                product.tiene = "SI";
                //Guardar respuesta
                operaciones.queryNoData("UPDATE t10t SET rta=1 WHERE encuesta='" + fg.getAuditoria() + "' AND sku='" + SKUCalidadID[pos2] + "'");
                //TomarFoto case:10
                operaciones.queryNoData("UPDATE ACT SET VAL='10' WHERE VA='FOTO'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + SKUCalidadID[pos2] + "' WHERE VA='SKU_C'");
                //Verificar si ya tiene foto
                ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(),"t10t",new String[]{"foto"},"encuesta='" + fg.getAuditoria() + "' AND sku='" + SKUCalidadID[pos2] + "'");
                String existe = foto[0].get(0);
                if(existe != null){
                    if(!existe.equals("1")){
                        tFoto();
                    } else {
                        Toast.makeText(getBaseContext(),"Foto ya almacenada",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tFoto();
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void tFoto(){
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this, TomarFoto.class));
    }

    public void editarSKUCalM(int pos, SKUCal holder, SKUCalidadModel skucal) {
        final int pos2 = pos;
        final SKUCal holder2 = holder;
        final SKUCalidadModel product = skucal;
        String queryskucalrta = "SELECT t10t.mis FROM t10t where sku='" + SKUCalidadID[pos2] + "'";
        ArrayList<String>[] rtaskucal = conGen.queryObjeto2val(getBaseContext(), queryskucalrta, null);
        if (rtaskucal[0].get(0) != null) {
            if (Integer.parseInt(rtaskucal[0].get(0)) == 1) {
                at.actualizarT10M(SKUCalidadID[pos2], 2);
                holder.mismo.setText("NO");
                product.mismo = "NO";
            } else if (Integer.parseInt(rtaskucal[0].get(0)) == 2) {
                at.actualizarT10M(SKUCalidadID[pos2], 1);
                holder.mismo.setText("SI");
                product.mismo = "SI";
            }  else if (Integer.parseInt(rtaskucal[0].get(0)) == 0) {
                at.actualizarT10M(SKUCalidadID[pos2], 1);
                holder.mismo.setText("SI");
                product.mismo = "SI";
            }
        } else {
            at.actualizarT10M(SKUCalidadID[pos2], 1);
            holder.mismo.setText("SI");
            product.mismo = "SI";
        }
    }

    public void Volver(View v) {
        //Validar si todos los SKUCalidad están evaluados
        boolean completo = true;
        for(int r = 0; r < lista.size(); r++){
            SKUCalidadModel model = lista.get(r);
            String tiene = model.tiene;
            String mismo = model.mismo;
            if((!tiene.equals("N/A")) && ((tiene.equals("TIENE")) || (mismo.equals("TIENE")))){
                completo = false;
                break;
            }
        }
        if(completo == true){
            String queryskucalrta = "SELECT rta FROM t10t where rta=0";
            ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryskucalrta, null);
            if (eval == null) {
                operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='SKUCALIDAD'");
                Intent menu = new Intent(this, MenuOpciones.class);
                startActivity(menu);
            } else {
                operaciones.queryNoData("UPDATE 'ME' SET EV='2' WHERE ET='SKUCALIDAD'");
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 6, 1);
            }
        } else {
            Toast.makeText(getBaseContext(),"Es necesario llenar todos los campos que tengan 'Vigente' como 'SI' o 'NO'", Toast.LENGTH_SHORT).show();
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
            fg.ultimaPantallafta("SKUCalidad");
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


    public static SKUCalidad getInstance() {
        return activityA;
    }
}
