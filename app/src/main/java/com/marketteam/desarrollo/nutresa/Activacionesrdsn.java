package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Activacionesrdsn extends AppCompatActivity {

    ArrayList<ActivacionModel> lista;

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, idPREG, PREG,fotoa;
    RecyclerView recyclerActivaciones;
    String[] ActivacionesID, idTabla8, OpcionesID, OpcionesTID, OpcionesRtaID, OpcionesAplicaID;
    Integer Orden,TipoCar,Fmostrar,Grupo;
    TextView pregunt;
    Button bfoto;
    ImageView iv;
    static Activacionesrdsn activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activacionesrdsn);
        activityA = this;
    }

    class Activ extends RecyclerView.ViewHolder {

        TextView nombreOpcion;
        ImageView CBV;

        public Activ(View itemView) {
            super(itemView);
            this.nombreOpcion = (TextView) itemView.findViewById(R.id.tvNombreOpcionRD);
            this.CBV = (ImageView) itemView.findViewById(R.id.imgRBV);

        }
    }

    class ActivacionModel {
        String nombreOpcion, codRta, rtaCK;

        public ActivacionModel(String nombreOpcion, String codRta, String rtaCK) {
            this.nombreOpcion = nombreOpcion;
            this.codRta = codRta;
            this.rtaCK = rtaCK;

        }
    }

    class ActivacionAdapter extends RecyclerView.Adapter<Activ> {

        ArrayList<ActivacionModel> lista;

        public ActivacionAdapter(ArrayList<ActivacionModel> lista) {
            this.lista = lista;
        }

        @Override
        public Activ onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Activ(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_choice, parent, false));

        }

        @Override
        public void onBindViewHolder(Activ holder, final int position) {
            final Activ holder2 = holder;
            final ActivacionModel Activacion = lista.get(position);
            holder.nombreOpcion.setText(Activacion.nombreOpcion);
            if (!Activacion.rtaCK.equals("0")) {
                holder.CBV.setImageResource(R.drawable.rdb);
            } else {
                holder.CBV.setImageResource(R.drawable.rdnoselb);
            }
            holder.CBV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarActivacion(position, holder2, Activacion);
                }
            });
            holder.nombreOpcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarActivacion(position, holder2, Activacion);
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
        Fmostrar = 0;
        if (DetalleCliente.getInstance() != null) { DetalleCliente.getInstance().finish(); }
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        operaciones = new OperacionesBDInterna(getApplicationContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerActivaciones = (RecyclerView) findViewById(R.id.recyclerActivaciones);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerActivaciones.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("Activacionesrdsn");
        idC = fg.clienteActual();
        iv = (ImageView) findViewById(R.id.ivImgPreg);
        iv.setVisibility(View.INVISIBLE);
        String queryOrden = "SELECT VAL FROM ACT where VA='ORDEN'";
        ArrayList<String>[] alorden = conGen.queryObjeto2val(getBaseContext(), queryOrden, null);
        String querytipocarga = "SELECT VAL FROM ACT where VA='TIPOCARGA'";
        ArrayList<String>[] altipocar = conGen.queryObjeto2val(getBaseContext(), querytipocarga, null);
        String queryGrupo = "SELECT VAL FROM ACT where VA='GRUPO'";
        ArrayList<String>[] algrupo = conGen.queryObjeto2val(getBaseContext(), queryGrupo, null);
        Grupo = Integer.parseInt(algrupo[0].get(0));
        Orden = Integer.parseInt(alorden[0].get(0));
        TipoCar = Integer.parseInt(altipocar[0].get(0));
        String queryActivacionesid = "SELECT idpreg,preg,cnd2,fap FROM '302_PREGGEN' where orden=" + Orden + " and gr=" + Grupo + " and gr=" + Grupo;
        ArrayList<String>[] actidpreg = conGen.queryObjeto2val(getBaseContext(), queryActivacionesid, null);
        idPREG = actidpreg[0].get(0);
        PREG = actidpreg[0].get(1);
        String cond2 = actidpreg[0].get(2);
        fotoa = actidpreg[0].get(3);
        String valcn = "0";
        String Tipox = "0";
        bfoto = (Button) findViewById(R.id.BTFotos);
        if (fotoa.equals("0")){
            bfoto.setClickable(false);
        }
        if (!cond2.equals("0")){
            ArrayList<String>[] Activacionescnd = conGen.queryObjeto2val(getBaseContext(), cond2, null);
            valcn = Activacionescnd[0].get(0);
        }else {
            valcn = "1";
        }
        if (Integer.parseInt(valcn)>0) {
            operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='FTATID'");
            pregunt = (TextView) findViewById(R.id.tvPregunta);
            pregunt.setText(PREG);
            listarActivaciones();
        } else {
            String queryActextx = "";
            if (TipoCar==1) {
                 queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden>" + Orden + " and gr=" + Grupo + " and gr=" + Grupo;
            }else {
                 queryActextx = "SELECT count(orden) as co,max(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden<" + Orden + " and gr=" + Grupo + " and gr=" + Grupo;
            }

            ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                String querymino = "SELECT tipo FROM '302_PREGGEN' where orden=" + Integer.parseInt(actidexx[0].get(1));
                ArrayList<String>[] mino = conGen.queryObjeto2val(getBaseContext(), querymino, null);
                Tipox = mino[0].get(0);
                operaciones.queryNoData("UPDATE ACT SET VAL='" + Integer.parseInt(actidexx[0].get(1)) + "' WHERE VA='ORDEN'");
            }
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                if (Integer.parseInt(Tipox) == 1) {
                    Intent intent = new Intent(this, Activaciones.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipox) == 2) {
                    Intent intent = new Intent(this, Activacionesrd.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipox) == 3) {
                    Intent intent = new Intent(this, Activacionestb.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipox) == 4) {
                    Intent intent = new Intent(this, Activacionestbt.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipox) == 5) {
                    Intent intent = new Intent(this, Activacionesrdsn.class);
                    startActivity(intent);
                }
            } else {
                operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='1' WHERE grpid=" + Grupo);
                Intent menu = new Intent(this, TipoASubMenu.class);
                startActivity(menu);
            }
        }
    }

    public void mbusc(View v){
        if (Fmostrar==0){
        if(idPREG.equals("DIC18_P1")){
            ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND EST_SKU='DIC18_P1'");
            if (foto != null) {
                String img = foto[0].get(0);
                StringTokenizer st = new StringTokenizer(img, ".");
                String rutaImg = st.nextToken();
                String uri = "@drawable/" + rutaImg;
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
                if (imagen != null) {
                    iv.setImageDrawable(imagen);
                    iv.setVisibility(View.VISIBLE);
                }
                Fmostrar = 1;
            }

        } else if(idPREG.equals("DIC18_P2")){
            ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND EST_SKU='DIC18_P2'");
            if (foto != null) {
                String img = foto[0].get(0);
                StringTokenizer st = new StringTokenizer(img, ".");
                String rutaImg = st.nextToken();
                String uri = "@drawable/" + rutaImg;
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
                if (imagen != null) {
                    iv.setImageDrawable(imagen);
                    iv.setVisibility(View.VISIBLE);
                }
                Fmostrar = 1;
            }

        } else if(idPREG.equals("DIC18_P3")){
            ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND EST_SKU='DIC18_P3'");
            if (foto != null) {
                String img = foto[0].get(0);
                StringTokenizer st = new StringTokenizer(img, ".");
                String rutaImg = st.nextToken();
                String uri = "@drawable/" + rutaImg;
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
                if (imagen != null) {
                    iv.setImageDrawable(imagen);
                    iv.setVisibility(View.VISIBLE);
                }
                Fmostrar = 1;
            }

        } else if(idPREG.equals("DIC18_P4")){
            ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND EST_SKU='DIC18_P4'");
            if (foto != null) {
                String img = foto[0].get(0);
                StringTokenizer st = new StringTokenizer(img, ".");
                String rutaImg = st.nextToken();
                String uri = "@drawable/" + rutaImg;
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
                if (imagen != null) {
                    iv.setImageDrawable(imagen);
                    iv.setVisibility(View.VISIBLE);
                }
                Fmostrar = 1;
            }

        } else {
            iv.setVisibility(View.GONE);
            Fmostrar = 0;
        }

        } else {
            iv.setVisibility(View.GONE);
            Fmostrar = 0;
        }
    }

    public void listarActivaciones() {
        //Traer los datos actuales
        ArrayList<String> resCampos = fg.existeACT(3);
        String canalAct = resCampos.get(0);
        String subcAct = resCampos.get(1);
        String Activt = resCampos.get(2);
        String queryAct = "SELECT t8t.optn,t8t.optt,t8t.rta,t8t.aplica FROM t8t where idpreg = '" + idPREG + "' and aplica='1' order by t8t.optn asc";
        ArrayList<String>[] Activaciones = conGen.queryObjeto2val(getBaseContext(), queryAct, null);
        if (Activaciones != null) {
            OpcionesID = new String[Activaciones.length];
            OpcionesTID = new String[Activaciones.length];
            OpcionesRtaID = new String[Activaciones.length];
            OpcionesAplicaID = new String[Activaciones.length];
            for (int c = 0; c < Activaciones.length; c++) {
                OpcionesID[c] = Activaciones[c].get(0);
                OpcionesTID[c] = Activaciones[c].get(1);
                OpcionesRtaID[c] = Activaciones[c].get(2);
                OpcionesAplicaID[c] = Activaciones[c].get(3);
                String opt = OpcionesTID[c];
                String opn = OpcionesID[c];
                String rta = OpcionesRtaID[c];
                ActivacionModel am = new ActivacionModel(opt, opn, rta);
                lista.add(am);
            }
        }
        ActivacionAdapter aa = new ActivacionAdapter(lista);
        recyclerActivaciones.setAdapter(aa);
    }

    public void editarActivacion(int pos, Activ holder, ActivacionModel Activaciones) {
        final int pos2 = pos;
        final Activ holder2 = holder;
        final ActivacionModel product = Activaciones;
        String queryActivacionesrta = "SELECT t8t.rta FROM t8t where idpreg='" + idPREG + "' and optn='" + OpcionesID[pos2] + "'";
        ArrayList<String>[] rtaActivaciones = conGen.queryObjeto2val(getBaseContext(), queryActivacionesrta, null);
        at.actualizarT8RD(idPREG, OpcionesID[pos2]);

        lista.clear();
        String queryAct = "SELECT t8t.optn,t8t.optt,t8t.rta,t8t.aplica FROM t8t where idpreg = '" + idPREG + "' and aplica='1' order by t8t.optn asc";
        ArrayList<String>[] Activacionesx = conGen.queryObjeto2val(getBaseContext(), queryAct, null);
        if (Activaciones != null) {
            OpcionesID = new String[Activacionesx.length];
            OpcionesTID = new String[Activacionesx.length];
            OpcionesRtaID = new String[Activacionesx.length];
            OpcionesAplicaID = new String[Activacionesx.length];
            for (int c = 0; c < Activacionesx.length; c++) {
                OpcionesID[c] = Activacionesx[c].get(0);
                OpcionesTID[c] = Activacionesx[c].get(1);
                OpcionesRtaID[c] = Activacionesx[c].get(2);
                OpcionesAplicaID[c] = Activacionesx[c].get(3);
                String opt = OpcionesTID[c];
                String opn = OpcionesID[c];
                String rta = OpcionesRtaID[c];
                ActivacionModel am = new ActivacionModel(opt, opn, rta);
                lista.add(am);
            }
        }
        ActivacionAdapter aa = new ActivacionAdapter(lista);
        recyclerActivaciones.setAdapter(aa);
    }


    public void Volver(View v) {
        String rtax = "";
        String queryActivacionesrtaF = "SELECT optn FROM t8t where idpreg='" + idPREG + "' and rta<>0";
        ArrayList<String>[] rtaActivacionesF = conGen.queryObjeto2val(getBaseContext(), queryActivacionesrtaF, null);
        if (rtaActivacionesF!=null) {
            rtax = rtaActivacionesF[0].get(0);
        }else{
            rtax = "0";
        }
        if ((Integer.parseInt(rtax) == 1 && ((Integer.parseInt(rtax) == 1 && fotoa.equals("1") && fg.getFototom(Orden).equals("1")) || fotoa.equals("0"))) || (Integer.parseInt(rtax) == 2)) {
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='1' WHERE orden='" + Orden + "'");
        }else {
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='0' WHERE orden='" + Orden + "'");
        }
        String queryActextx = "SELECT count(orden) as co,max(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden<" + Orden + " and gr=" + Grupo ;
        ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);

        if (Integer.parseInt(actidexx[0].get(0)) > 0) {
            Integer val = Integer.parseInt(actidexx[0].get(1));
            String queryActexty = "SELECT tipo FROM '302_PREGGEN' where  orden=" + val;
            ArrayList<String>[] actidexy = conGen.queryObjeto2val(getBaseContext(), queryActexty, null);
            operaciones.queryNoData("UPDATE ACT SET VAL='" + val + "' WHERE VA='ORDEN'");
            operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='TIPOCARGA'");
            String Tipo = actidexy[0].get(0);
            if (Integer.parseInt(Tipo) == 1) {
                Intent intent = new Intent(this, Activaciones.class);
                startActivity(intent);
            } else if (Integer.parseInt(Tipo) == 2) {
                Intent intent = new Intent(this, Activacionesrd.class);
                startActivity(intent);
            } else if (Integer.parseInt(Tipo) == 3) {
                Intent intent = new Intent(this, Activacionestb.class);
                startActivity(intent);
            } else if (Integer.parseInt(Tipo) == 4) {
                Intent intent = new Intent(this, Activacionestbt.class);
                startActivity(intent);
            } else if (Integer.parseInt(Tipo) == 5) {
                Intent intent = new Intent(this, Activacionesrdsn.class);
                startActivity(intent);
            }
        } else {
            Intent menu = new Intent(this, TipoASubMenu.class);
            startActivity(menu);
        }

    }

    public void Regresar(View v) {
        String rtax = "";
        String queryActivacionesrtaF = "SELECT optn FROM t8t where idpreg='" + idPREG + "' and rta<>0";
        ArrayList<String>[] rtaActivacionesF = conGen.queryObjeto2val(getBaseContext(), queryActivacionesrtaF, null);
        if (rtaActivacionesF!=null) {
            rtax = rtaActivacionesF[0].get(0);
            if ((Integer.parseInt(rtax) == 1 && ((Integer.parseInt(rtax) == 1 && fotoa.equals("1") && fg.getFototom(Orden).equals("1")) || fotoa.equals("0"))) || (Integer.parseInt(rtax) == 2)) {
                operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='1' WHERE orden='" + Orden + "'");
                Intent menu = new Intent(this, TipoASubMenu.class);
                startActivity(menu);
            } else {
                operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='0' WHERE orden='" + Orden + "'");
                Intent menu = new Intent(this, TipoASubMenu.class);
                startActivity(menu);
            }
        }else {
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='0' WHERE orden='" + Orden + "'");
            Intent menu = new Intent(this, TipoASubMenu.class);
            startActivity(menu);
        }
    }

    public void Fotord(View v) {
        //Validar si todos los Activaciones están evaluados
        operaciones.queryNoData("UPDATE ACT SET VAL='11' WHERE VA='FOTO'");
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this, TomarFoto.class));
    }

    public void Siguienterd(View v) {
        String rtax = "";
        String queryActivacionesrtaF = "SELECT optn FROM t8t where idpreg='" + idPREG + "' and rta<>0";
        ArrayList<String>[] rtaActivacionesF = conGen.queryObjeto2val(getBaseContext(), queryActivacionesrtaF, null);
        if (rtaActivacionesF!=null) {
            rtax = rtaActivacionesF[0].get(0);
        }else{
            rtax = "0";
        }

        String Tipo = "";
        if ((Integer.parseInt(rtax) == 1 && ((Integer.parseInt(rtax) == 1 && fotoa.equals("1") && fg.getFototom(Orden).equals("1")) || fotoa.equals("0"))) || (Integer.parseInt(rtax) == 2)) {
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='1' WHERE orden=" + Orden + " and gr=" + Grupo );
            String queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden>" + Orden + " and gr=" + Grupo ;
            ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                String querymino = "SELECT tipo FROM '302_PREGGEN' where orden=" + Integer.parseInt(actidexx[0].get(1));
                ArrayList<String>[] mino = conGen.queryObjeto2val(getBaseContext(), querymino, null);
                Tipo = mino[0].get(0);
                operaciones.queryNoData("UPDATE ACT SET VAL='" + Integer.parseInt(actidexx[0].get(1)) + "' WHERE VA='ORDEN'");
                operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='TIPOCARGA'");
            }
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                if (Integer.parseInt(Tipo) == 1) {
                    Intent intent = new Intent(this, Activaciones.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipo) == 2) {
                    Intent intent = new Intent(this, Activacionesrd.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipo) == 3) {
                    Intent intent = new Intent(this, Activacionestb.class);
                    startActivity(intent);
                } else if (Integer.parseInt(Tipo) == 4) {
                    Intent intent = new Intent(this, Activacionestbt.class);
                    startActivity(intent);
                }else if (Integer.parseInt(Tipo) == 5) {
                    Intent intent = new Intent(this, Activacionesrdsn.class);
                    startActivity(intent);
                }
            } else {
                operaciones.queryNoData("UPDATE '302_PREGGRU' SET EV='1' WHERE grpid=" + Grupo);
                Intent menu = new Intent(this, TipoASubMenu.class);
                startActivity(menu);
            }
        } else {
            Toast.makeText(getBaseContext(), "Valor/es no valido o Foto no Tomada, revise segun manual", Toast.LENGTH_LONG).show();

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
            Intent valid = new Intent(this, TipoASubMenu.class);
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
    public static Activacionesrdsn getInstance() {
        return activityA;
    }
}
