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

public class Activos extends AppCompatActivity {

    ArrayList<ActivoModel> lista;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC;
    RecyclerView recyclerActivos;
    String[] ActivosID, idTabla11, CodBarID;
    static Activos activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activos);
        activityA = this;
    }

    class Activ extends RecyclerView.ViewHolder {

        TextView codBarras;
        TextView nombreActivos;
        TextView rtaA;
        TextView rtaAC;

        public Activ(View itemView) {
            super(itemView);

            this.codBarras = (TextView) itemView.findViewById(R.id.tvCodBar);
            this.nombreActivos = (TextView) itemView.findViewById(R.id.tvNombreActivo);
            this.rtaA = (TextView) itemView.findViewById(R.id.tvRtaA);
            this.rtaAC = (TextView) itemView.findViewById(R.id.tvRtaAC);
        }
    }

    class ActivoModel {
        String codBarras, nombreActivos, rtaA, rtaAC;

        public ActivoModel(String codBarras, String nombreActivos, String rtaA, String rtaAC) {
            this.codBarras = codBarras;
            this.nombreActivos = nombreActivos;
            this.rtaA = rtaA;
            this.rtaAC = rtaAC;
        }
    }

    class ActivoAdapter extends RecyclerView.Adapter<Activ> {
        ArrayList<ActivoModel> lista;

        public ActivoAdapter(ArrayList<ActivoModel> lista) {
            this.lista = lista;
        }

        @Override
        public Activ onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Activ(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_activos, parent, false));
        }

        @Override
        public void onBindViewHolder(Activ holder, final int position) {
            final Activ holder2 = holder;
            final ActivoModel Activo = lista.get(position);
            holder.codBarras.setText(Activo.codBarras);
            holder.nombreActivos.setText(Activo.nombreActivos);
            holder.rtaA.setText(Activo.rtaA);
            holder.rtaAC.setText(Activo.rtaAC);
            String codbar = CodBarID[position];
            holder.rtaA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarActivo2(position, holder2, Activo);
                }
            });
            holder.rtaAC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String respuestaUno = holder2.rtaA.getText().toString();
                    if ((respuestaUno.equals("NO")) || (Activo.rtaA.equals("NO"))) {
                        holder2.rtaAC.setText("NO");
                        Activo.rtaAC = "NO";
                        //Actualizar en la base de datos
                        at.actualizarT11C(ActivosID[position], 2, CodBarID[position]);
                    } else {
                        editarActivoC(position, holder2, Activo);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
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
        operaciones = new OperacionesBDInterna(getApplicationContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerActivos = (RecyclerView) findViewById(R.id.recyclerActivos);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerActivos.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("Activos");
        idC = fg.clienteActual();
        listarActivos();
    }

    public void listarActivos() {
        //Traer los datos actuales
        ArrayList<String> resCampos = fg.existeACT(3);
        String canalAct = resCampos.get(0);
        String subcAct = resCampos.get(1);
        String Activt = resCampos.get(2);
        //Con el canal, traigo los respectivos Activos
        String queryAct = "SELECT t11t.ida,t11t.codb,t11t.nact,t11t.rta,t11t.coin FROM t11t order by t11t.ida asc";
        ArrayList<String>[] Activos = conGen.queryObjeto2val(getBaseContext(), queryAct, null);
        if (Activos != null) {
            ActivosID = new String[Activos.length];
            idTabla11 = new String[Activos.length];
            CodBarID = new String[Activos.length];
            for (int c = 0; c < Activos.length; c++) {
                String temp = Activos[c].get(0);
                ActivosID[c] = temp;
                idTabla11[c] = temp;
                CodBarID[c] = Activos[c].get(1);
                String cb = CodBarID[c];
                String noma = Activos[c].get(2);
                String rta = Activos[c].get(3);
                String coin = Activos[c].get(4);
                String rtaT = "";
                String coinT = "";
                if (rta.equals("1")) {
                    rtaT = "SI";
                } else if (rta.equals("2")) {
                    rtaT = "NO";
                } else if (rta.equals("0")) {
                    rtaT = "TIENE";
                }
                if (coin.equals("1")) {
                    coinT = "SI";
                } else if (coin.equals("2")) {
                    coinT = "NO";
                } else if (coin.equals("3")) {
                    coinT = "Borrosa";
                } else if (coin.equals("4")) {
                    coinT = "No Visible";
                } else if (coin.equals("0")) {
                    coinT = "TIENE";
                }
                ActivoModel am = new ActivoModel(cb, noma, rtaT, coinT);
                lista.add(am);
                ActivoAdapter aa = new ActivoAdapter(lista);
                recyclerActivos.setAdapter(aa);

            }
        }
    }

    public void editarActivo2(int pos, Activ holder, ActivoModel activos) {
        final int pos2 = pos;
        final Activ holder2 = holder;
        final ActivoModel product = activos;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = getString(R.string.activaciones);
        texto.setText(tx);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        cancel.setText("NO");
        ok.setText("SI");
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.rtaA.setText("NO");
                product.rtaA = "NO";
                holder2.rtaAC.setText("");
                product.rtaAC = "0";
                //Guardar respuesta campo1
                operaciones.queryNoData("UPDATE t11t SET rta=2 WHERE encuesta='" + fg.getAuditoria() + "' AND ida='" + ActivosID[pos2] + "'");
                //Guardar respuesta campo2
                //Actualizar en la base de datos
                at.actualizarT11C(ActivosID[pos2], 3, CodBarID[pos2]);
                popupWindow.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.rtaA.setText("SI");
                product.rtaA = "SI";
                //Guardar respuesta
                operaciones.queryNoData("UPDATE t11t SET rta=1 WHERE encuesta='" + fg.getAuditoria() + "' AND ida='" + ActivosID[pos2] + "'");
                //TomarFoto case:9
                operaciones.queryNoData("UPDATE ACT SET VAL='9' WHERE VA='FOTO'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + ActivosID[pos2] + "' WHERE VA='ACTV'");
                //Verificar si ya tiene foto
                ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "t11t", new String[]{"foto"}, "encuesta='" + fg.getAuditoria() + "' AND ida='" + ActivosID[pos2] + "'");
                String existe = foto[0].get(0);
                if (existe != null) {
                    if (!existe.equals("1")) {
                        tFoto();
                    } else {
                        Toast.makeText(getBaseContext(), "Foto ya almacenada", Toast.LENGTH_SHORT).show();
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

    public void tFoto() {
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this, TomarFoto.class));
    }

    public void editarActivoC(int pos, Activ holder, ActivoModel activos) {
        final int pos2C = pos;
        final Activ holder2C = holder;
        final ActivoModel productC = activos;

        String queryactivosrta = "SELECT t11t.coin FROM t11t where codb='" + CodBarID[pos2C] + "'";
        ArrayList<String>[] rtaactivos = conGen.queryObjeto2val(getBaseContext(), queryactivosrta, null);
        if (Integer.parseInt(rtaactivos[0].get(0)) == 1) {
            at.actualizarT11C(ActivosID[pos2C], 2, CodBarID[pos2C]);
            holder.rtaAC.setText("NO");
        } else if (Integer.parseInt(rtaactivos[0].get(0)) == 2) {
            at.actualizarT11C(ActivosID[pos2C], 4, CodBarID[pos2C]);
            holder.rtaAC.setText("Borrosa");
        } else if (Integer.parseInt(rtaactivos[0].get(0)) == 3) {
            at.actualizarT11C(ActivosID[pos2C], 5, CodBarID[pos2C]);
            holder.rtaAC.setText("No Visible");
        } else if (Integer.parseInt(rtaactivos[0].get(0)) == 4) {
            at.actualizarT11C(ActivosID[pos2C], 1, CodBarID[pos2C]);
            holder.rtaAC.setText("SI");
        } else if (Integer.parseInt(rtaactivos[0].get(0)) == 0) {
            at.actualizarT11C(ActivosID[pos2C], 1, CodBarID[pos2C]);
            holder.rtaAC.setText("SI");
        }
    }

    public void Volver(View v) {
        //Validar si todos los Activos están evaluados
        String queryactivosrta = "SELECT count(rta) as rsr FROM t11t where rta=0 or (rta=1 and coin=0)";
        ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryactivosrta, null);
        if (eval[0].get(0).equals("0")) {
            operaciones.queryNoData("UPDATE 'ME' SET EV='1' WHERE ET='ACTIVOS'");
            Intent menu = new Intent(this, MenuOpciones.class);
            startActivity(menu);
        } else {
            operaciones.queryNoData("UPDATE 'ME' SET EV='2' WHERE ET='ACTIVOS'");
            PopUps pop = new PopUps();
            final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            pop.popUpConf(getBaseContext(), inflater, 6, 1);
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

    public static Activos getInstance() {
        return activityA;
    }
}
