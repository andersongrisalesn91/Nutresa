package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Estandares extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, idt2, idt3, canalAct, subcAct, espAct, catAct, estndAct;
    RecyclerView recyclerEstnd;
    ArrayList<EstandarModel> lista;
    String[] estandaresID,estandaresRTA;
    TextView nombreCat;
    static Estandares activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estandares);
        activityA = this;
    }

    @Override
    public void onBackPressed() {
    }

    class Estandar extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView imgVSi;
        ImageView imgVNo;

        public Estandar(View itemView) {
            super(itemView);
            this.nombre = (TextView) itemView.findViewById(R.id.nombreEstSku);
            this.imgVSi = (ImageView) itemView.findViewById(R.id.rtaEstSku);
            this.imgVNo = (ImageView) itemView.findViewById(R.id.rtaEstSkuNo);
        }
    }

    class EstandarModel {
        String nom;
        ImageView imgVSi;
        ImageView imgVNo;

        public EstandarModel(String nombre, ImageView ivSi,ImageView ivNo) {
            this.nom = nombre;
            this.imgVSi = ivSi;
            this.imgVNo = ivNo;
        }
    }

    class EstandarAdapter extends RecyclerView.Adapter<Estandar> {
        ArrayList<EstandarModel> lista;

        public EstandarAdapter(ArrayList<EstandarModel> lista) {
            this.lista = lista;
        }

        @Override
        public Estandar onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Estandar(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_est_sku, parent, false));
        }

        @Override
        public void onBindViewHolder(Estandar holder, final int position) {
            final Estandar holder2 = holder;
            final EstandarModel estnd = lista.get(position);
            holder.nombre.setText(estnd.nom);
            if (estandaresRTA[position].equals("1")){
                holder.imgVSi.setImageResource(R.drawable.rdb);
                holder.imgVNo.setImageResource(R.drawable.rdnoselb);
            }
            if (estandaresRTA[position].equals("2")){
                holder.imgVSi.setImageResource(R.drawable.rdnoselb);
                holder.imgVNo.setImageResource(R.drawable.rdb);
            }
            if (estandaresRTA[position].equals("0")){
                holder.imgVSi.setImageResource(R.drawable.rdnoselb);
                holder.imgVNo.setImageResource(R.drawable.rdnoselb);
            }

            holder.imgVSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmarEstandarSi(position, holder2, estnd);
                }
            });
            holder.imgVNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmarEstandarNo(position, holder2, estnd);
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
        if (Espacios.getInstance() != null) {
            Espacios.getInstance().finish();
        }
        if (Categorias.getInstance() != null) {
            Categorias.getInstance().finish();
        }
        if (Validaciones.getInstance() != null) {Validaciones.getInstance().finish();}
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerEstnd = (RecyclerView) findViewById(R.id.recyclerEstand);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerEstnd.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("Estándares");
        idC = fg.clienteActual();
        nombreCat = (TextView) findViewById(R.id.tvNomCatEst);
        listarEstandares();
    }

    public void listarEstandares() {
        //Traer estándares que apliquen según canal , espacio y categoría
        ArrayList<String> resCampos = fg.existeACT(5);
        idt2 = resCampos.get(0);
        idt3 = resCampos.get(1);
        canalAct = resCampos.get(2);
        subcAct = resCampos.get(3);
        espAct = resCampos.get(4);
        catAct = resCampos.get(5);
        estndAct = resCampos.get(6);
        String nombreCategoria = fg.nombreCatAct(catAct);
        nombreCat.setText(nombreCategoria);
        String queryEst = "SELECT t4t.est,'109_ESTAND'.NESTAND,t4t.rta FROM t4t join '109_ESTAND' on t4t.est = '109_ESTAND'.ESTAND where esp = " + espAct + " and cat = " + catAct + " order by t4t.est asc";
        ArrayList<String>[] objEstand = conGen.queryObjeto2val(getBaseContext(), queryEst, null);

        if (objEstand != null) {
            //consulta para a partir del ID traer el nombre/etiqueta
            estandaresID = new String[objEstand.length];
            estandaresRTA = new String[objEstand.length];
            for (int i = 0; i < objEstand.length; i++) {
                String idE = objEstand[i].get(0);
                String rtat = objEstand[i].get(2);
                estandaresID[i] = idE;
                estandaresRTA[i] = rtat;
                String nEst = objEstand[i].get(1);
                ImageView ivSI = new ImageView(this);
                ImageView ivNO = new ImageView(this);
                //Sacar tiene
                EstandarModel em = new EstandarModel(nEst, ivSI,ivNO);
                lista.add(em);
            }
            EstandarAdapter ea = new EstandarAdapter(lista);
            recyclerEstnd.setAdapter(ea);
        }
    }

    public void confirmarEstandarSi(int pos, Estandar holder, EstandarModel estnd) {
        //Confirmar que existe el estándar

        final int pos2 = pos;
        final Estandar holder2 = holder;
        final EstandarModel product = estnd;
        estandaresRTA[pos] = "1";
        //Actualizar estándar en la tabla correspondiente
        at.actualizarT4(estandaresID[pos2], 1, espAct, catAct);

        //Cambiar imagen a mostrar
        holder2.imgVSi.setImageResource(R.drawable.rdb);
        holder2.imgVNo.setImageResource(R.drawable.rdnoselb);

        operaciones.queryNoData("UPDATE ACT SET VAL ='" + estandaresID[pos] + "' WHERE VA='ESTN'");
        estndAct = estandaresID[pos];
    }

    public void confirmarEstandarNo(int pos, Estandar holder, EstandarModel estnd) {
        //Confirmar que existe el estándar

        final int pos2 = pos;
        final Estandar holder2 = holder;
        final EstandarModel product = estnd;
        estandaresRTA[pos] = "2";
        //Actualizar estándar en la tabla correspondiente
        at.actualizarT4(estandaresID[pos2], 2, espAct, catAct);

        //Cambiar imagen a mostrar
        holder2.imgVSi.setImageResource(R.drawable.rdnoselb);
        holder2.imgVNo.setImageResource(R.drawable.rdb);

        operaciones.queryNoData("UPDATE ACT SET VAL ='" + estandaresID[pos] + "' WHERE VA='ESTN'");
        estndAct = estandaresID[pos];
    }

    public void Volver(View v) {
        //Validar si todos los estándares tienen respuesta
        String queryEst = "SELECT t4t.est , t4t.rta FROM t4t where esp=" + espAct + " and cat=" + catAct + " and rta=0;";
        ArrayList<String>[] respuesta = conGen.queryObjeto2val(getBaseContext(), queryEst, null);
        //String queryESTSI = "SELECT count(t4t.rta) as ev FROM t4t where esp = " + espAct + " and cat = " + catAct + " and rta=1";
        //ArrayList<String>[] skusi = conGen.queryObjeto2val(getBaseContext(),queryESTSI,null);
        boolean foto = false;
        if (respuesta == null) {
            String queryFoto = "SELECT count(tft.esp) as cf FROM tft where esp=" + espAct + " and cat=" + catAct;
            ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryFoto, null);
            String contf = eval[0].get(0);
            if (Integer.parseInt(contf) != 0) {
                foto = true;
            }
            if (foto == true) {
                operaciones.queryNoData("UPDATE t3t SET eval='1' WHERE esp='" + espAct + "' and cat='" + catAct + "'");
                String querySkuC = "SELECT count(t5t.esp) as csku FROM t5t where esp=14 and cat=" + catAct + " and rta=2";
                ArrayList<String>[] SkuC = conGen.queryObjeto2val(getBaseContext(), querySkuC, null);
                String contSkuC = SkuC[0].get(0);
                if (Integer.parseInt(contSkuC) != 0) {
                    operaciones.queryNoData("UPDATE t2t SET eval=2 WHERE esp=14");
                    operaciones.queryNoData("UPDATE t3t SET eval=2 WHERE esp=14 and cat=" + catAct );
                    operaciones.queryNoData("UPDATE t3t SET rta=1  WHERE esp=14 and cat=" + catAct );
                }
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 8, 3);
            } else if (foto == false) {
                operaciones.queryNoData("UPDATE t3t SET eval='2' WHERE esp='" + espAct + "' and cat='" + catAct + "'");
                String querySkuC = "SELECT count(t5t.esp) as csku FROM t5t where esp=14 and cat=" + catAct + " and rta=2";
                ArrayList<String>[] SkuC = conGen.queryObjeto2val(getBaseContext(), querySkuC, null);
                String contSkuC = SkuC[0].get(0);
                if (Integer.parseInt(contSkuC) != 0) {
                    operaciones.queryNoData("UPDATE t2t SET eval=2 WHERE esp=14");
                    operaciones.queryNoData("UPDATE t3t SET eval=2 WHERE esp=14 and cat=" + catAct );
                    operaciones.queryNoData("UPDATE t3t SET rta=1  WHERE esp=14 and cat=" + catAct );
                }
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 6, 3);
            }
        } else {
            operaciones.queryNoData("UPDATE t3t SET eval='2' WHERE esp='" + espAct + "' and cat='" + catAct + "';");
            PopUps pop = new PopUps();
            final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            pop.popUpConf(getBaseContext(), inflater, 6, 3);
        }
    }

    public void evaluar(View v) {
        String queryEst = "SELECT t4t.est , t4t.rta FROM t4t where esp = " + espAct + " and cat = " + catAct + " and rta = '0';";
        ArrayList<String>[] respuesta = conGen.queryObjeto2val(getBaseContext(), queryEst, null);
        if (respuesta == null) {
            Toast.makeText(getBaseContext(), "Tomar Foto", Toast.LENGTH_SHORT).show();
            operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='FOTO'");
            operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
            startActivity(new Intent(this, TomarFoto.class));
        } else {
            String texto = getString(R.string.foto_est);
            Toast.makeText(getBaseContext(), texto, Toast.LENGTH_SHORT).show();
        }
    }


    /*public void popUp1(final int pos, Estandar holder, EstandarModel estandar) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_conf_ej, null);
        TextView enunTV = (TextView) popUp.findViewById(R.id.textoConEj);
        ImageView imagenEj = (ImageView) popUp.findViewById(R.id.imageViewPopEj);
        imagenEj.setImageDrawable(null);
        String img = "";
        ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND EST_SKU='" + estandaresID[pos] + "'");
        if (foto != null) {
            img = foto[0].get(0);
            StringTokenizer st = new StringTokenizer(img, ".");
            String rutaImg = st.nextToken();
            String uri = "@drawable/" + rutaImg;
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
            if (imagen != null) {
                imagenEj.setImageDrawable(imagen);
            }
        }
        Button cancel = (Button) popUp.findViewById(R.id.buttonEjVolver);
        cancel.setText("NO");
        Button ok = (Button) popUp.findViewById(R.id.buttonEjConf);
        ok.setText("SI");
        String texto = getString(R.string.conf_estnd);
        enunTV.setText(texto);
        final Estandar holder2 = holder;
        final EstandarModel estnd2 = estandar;
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("SI");
                estnd2.rta = "SI";
                at.actualizarT4(estandaresID[pos], 1, espAct, catAct);
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("NO");
                estnd2.rta = "NO";
                at.actualizarT4(estandaresID[pos], 2, espAct, catAct);
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void popUp2(int pos, final Estandar holder, EstandarModel estandar) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = getString(R.string.conf_estnd);
        texto.setText(tx);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        cancel.setText("NO");
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        ok.setText("SI");
        final int pos2 = pos;
        final Estandar holder2 = holder;
        final EstandarModel estnd2 = estandar;
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("NO");
                estnd2.rta = "NO";
                at.actualizarT4(estandaresID[pos2], 2, espAct, catAct);
                popupWindow.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder2.tiene.setText("SI");
                estnd2.rta = "SI";
                at.actualizarT4(estandaresID[pos2], 1, espAct, catAct);
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }*/

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
            fg.ultimaPantallafta("Estándares");
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

    public static Estandares getInstance() {
        return activityA;
    }
}