package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Categorias extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC, idt2, canalAct, subcAct, espAct, catAct;
    RecyclerView recyclerCatg;
    ArrayList<CategoriaModel> lista;
    String[] categoriasID, idtabla3;
    String posicionF = "",tienece = "",tienece2 = "";
    Button pocnut, poccom;
    TextView nombreEsp;
    static Categorias activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onBackPressed() {
    }

    class Categ extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView tiene;
        ImageView imgV;

        public Categ(View itemView) {
            super(itemView);
            this.nombre = (TextView) itemView.findViewById(R.id.nItem);
            this.tiene = (TextView) itemView.findViewById(R.id.rItem);
            this.imgV = (ImageView) itemView.findViewById(R.id.imgVEC);
        }
    }

    class CategoriaModel {
        String nombre, tiene;
        ImageView imgV;

        public CategoriaModel(String nombre, String tiene, ImageView iv) {
            this.nombre = nombre;
            this.tiene = tiene;
            this.imgV = iv;
        }
    }

    class CategoriaAdapter extends RecyclerView.Adapter<Categ> {
        ArrayList<CategoriaModel> lista;

        public CategoriaAdapter(ArrayList<CategoriaModel> lista) {
            this.lista = lista;
        }

        @Override
        public Categ onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Categ(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_esp_cat, parent, false));
        }

        @Override
        public void onBindViewHolder(Categ holder, final int position) {
            final Categ holder2 = holder;
            final CategoriaModel categoria = lista.get(position);
            holder.nombre.setText(categoria.nombre);
            holder.tiene.setText(categoria.tiene);
            String idCat = categoriasID[position];
            String queryCat = "SELECT t3t.rta, t3t.eval FROM t3t where esp = '" + espAct + "' AND cat='" + idCat + "' order by t3t.cat asc";
            ArrayList<String>[] objeto2 = conGen.queryObjeto2val(getBaseContext(), queryCat, null);

            if (objeto2 != null) {
                String rta = objeto2[0].get(0);
                String ev = objeto2[0].get(1);
                if (rta.equals("1")) {
                    holder.tiene.setText("SI");
                } else if (rta.equals("2")) {
                    holder.tiene.setText("NO");
                }
                if (ev.equals("1")) {
                    holder.imgV.setImageResource(R.drawable.check_opto);
                } else if (ev.equals("2")) {
                    holder.imgV.setImageResource(R.drawable.equis_opto);
                } else {
                    holder.imgV.setImageResource(R.drawable.uncheck);
                }
            }
            posicionF = "" + position;
            holder.nombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irCategoria(position, holder2, categoria);
                }
            });
            holder.tiene.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irCategoria(position, holder2, categoria);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        operaciones.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Validaciones.getInstance() != null) {Validaciones.getInstance().finish();}
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) { Log.e("first","error"); }
        Criteria criteria = new Criteria();
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        recyclerCatg = (RecyclerView) findViewById(R.id.recyclerCateg);
        recyclerCatg.removeAllViews();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerCatg.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("Categorías");
        idC = fg.clienteActual();
        pocnut = (Button) findViewById(R.id.buttonPocNut);
        poccom = (Button) findViewById(R.id.buttonPocComp);
        nombreEsp = (TextView) findViewById(R.id.tvNomEspCat);
        listarCatg();
    }

    public void listarCatg() {
        //Leer las categorías correspondientes según el canal , espacio en la tabla
        //Revisar si existe ya el/los parámetros
        ArrayList<String> resCampos = fg.existeACT(4);
        idt2 = resCampos.get(0);
        canalAct = resCampos.get(1);
        subcAct = resCampos.get(2);
        espAct = resCampos.get(3);
        String espacioA = fg.nombreEspAct(espAct);
        nombreEsp.setText(espacioA);
        if ((!espAct.equals("2") && !espAct.equals("6")) || canalAct.equals("1000")) {
            poccom.setVisibility(View.GONE);
        } else{
            tienece = "1";
        }

        if (espAct.equals("1") || espAct.equals("7") || espAct.equals("8") || espAct.equals("10") || espAct.equals("11") || Integer.parseInt(espAct) > 13  || canalAct.equals("1000")) {
            pocnut.setVisibility(View.GONE);

        }else {
            tienece2 = "1";
        }

        catAct = resCampos.get(4);
        //Llenar el recycler view
        String queryCat = "SELECT t3t.cat, '106_CATEG'.NCAT , t3t.rta FROM t3t join '106_CATEG' on t3t.cat = '106_CATEG'.CAT where esp = " + espAct + " order by t3t.cat asc";
        ArrayList<String>[] objCat = conGen.queryObjeto2val(getBaseContext(), queryCat, null);
        if (objCat != null) {
            categoriasID = new String[objCat.length];
            idtabla3 = new String[objCat.length];
            for (int c = 0; c < objCat.length; c++) {
                String idCatg = objCat[c].get(0);
                categoriasID[c] = idCatg;
                idtabla3[c] = idCatg;
                String categ = objCat[c].get(1);
                ImageView iv = new ImageView(this);
                String tiene = "";
                String val = "";
                val = objCat[c].get(2);
                if (val.equals("1")) {
                    tiene = "SI";
                } else if (val.equals("2")) {
                    tiene = "NO";
                }
                CategoriaModel cm = new CategoriaModel(categ, tiene, iv);
                lista.add(cm);
            }
            CategoriaAdapter ca = new CategoriaAdapter(lista);
            recyclerCatg.setAdapter(ca);
        }
    }

    public void irCategoria(int pos, final Categ holder, final CategoriaModel categoria) {
        //Revisar si si existe o no
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popUp = inflater.inflate(R.layout.popup_conf_ej, null);
        TextView enunTV = (TextView) popUp.findViewById(R.id.textoConEj);
        ImageView imagenEj = (ImageView) popUp.findViewById(R.id.imageViewPopEj);
        imagenEj.setImageDrawable(null);
        String img = "";
        ArrayList<String>[] foto = conGen.queryGeneral(getBaseContext(), "'301_IMPUT_FOTOS'", new String[]{"PATH_AYUDA"}, "TIPO='2' AND CAT='" + categoriasID[pos] + "'");
        if (foto != null) {
            img = foto[0].get(0);
            StringTokenizer st = new StringTokenizer(img, ".");
            String rutaImg = st.nextToken();
            String uri = "@drawable/" + rutaImg;
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            if (imageResource != 0) {
                Drawable imagen = ContextCompat.getDrawable(getBaseContext(), imageResource);
                if (imagen != null) {
                    imagenEj.setImageDrawable(imagen);
                }
            }
        }
        Button cancel = (Button) popUp.findViewById(R.id.buttonEjVolver);
        Button ok = (Button) popUp.findViewById(R.id.buttonEjConf);
        String texto = getString(R.string.conf_cat);
        enunTV.setText(texto);
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        final String rta = categoria.tiene;
        final Categ holder2 = holder;
        final int pos2 = pos;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria.tiene = "SI";
                holder2.tiene.setText("SI");
                holder2.imgV.setImageResource(R.drawable.equis_opto);
                //Guardar el SI en la base de datos
                //Actualizar Categorías actual
                operaciones.queryNoData("UPDATE ACT SET VAL ='" + categoriasID[pos2] + "' WHERE VA='CAT'");
                operaciones.queryNoData("UPDATE ACT SET VAL ='" + idtabla3[pos2] + "' WHERE VA='IDT3'");
                if (rta.equals("NO")) {
                    at.actualizarT3(categoriasID[pos2], 5, espAct);
                }else{
                    at.actualizarT3(categoriasID[pos2], 1, espAct);
                }


                //Si le da que sí, Ir a Estándares
                boolean existen = revisarEstandares(pos2, idtabla3[pos2]);
                catAct = categoriasID[pos2];
                if ((((canalAct.equals("2") && (subcAct.equals("6") || subcAct.equals("7"))) || Integer.parseInt(canalAct)>=4) && !canalAct.equals("1000")) && espAct.equals("1")) {
                    irParticipaciones();
                } else if (espAct.equals("14")) {
                    //¿qué pasa si no existen skus?
                    irSKU();
                } else {
                    if (existen == true) {
                        irEstandar();
                    } else {
                        //Si no hay estándares, tomar la foto y seguir en Categorías
                        Toast.makeText(getBaseContext(), "Tomar Foto", Toast.LENGTH_SHORT).show();
                        at.actualizarT3(categoriasID[pos2], 4, espAct);
                        operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FOTO'");
                        new Thread() {
                            @Override
                            public void run() {
                                irFoto();
                            }
                        }.start();
                    }
                }
                popupWindow.dismiss();
            }
        });
        //cancel - confirmacion = false;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((categoriasID[pos2].equals("15")) || (categoriasID[pos2].equals("16")) || (categoriasID[pos2].equals("17"))){
                    categoriasEsp(pos2,categoria,holder);
                    popupWindow.dismiss();
                } else if (rta.equals("SI")) {
                    //Confirmar que quiere borrar la información
                    popUp3(pos2, holder2, categoria);
                } else {
                    categoria.tiene = "NO";
                    holder2.tiene.setText("NO");
                    holder2.imgV.setImageResource(R.drawable.check_opto);
                    at.actualizarT3(categoriasID[pos2], 3, espAct);
                    operaciones.queryNoData("delete from t6t WHERE esp=" + espAct + " and cat=" + categoriasID[pos2] );
                    if (espAct.equals("1")) {
                        operaciones.queryNoData("delete from t7t WHERE cat=" + categoriasID[pos2]);
                    }
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void categoriasEsp(int pos2, CategoriaModel categoria,Categ holder2){
        categoria.tiene = "SI";
        holder2.tiene.setText("SI");
        holder2.imgV.setImageResource(R.drawable.equis_opto);
        //Guardar el SI en la base de datos
        //Actualizar Categorías actual
        operaciones.queryNoData("UPDATE ACT SET VAL ='" + categoriasID[pos2] + "' WHERE VA='CAT'");
        operaciones.queryNoData("UPDATE ACT SET VAL ='" + idtabla3[pos2] + "' WHERE VA='IDT3'");
        at.actualizarT3(categoriasID[pos2], 1, espAct);
        //Si la respuesta es sí, Ir a Estándares
        boolean existen = revisarEstandares(pos2, idtabla3[pos2]);
        catAct = categoriasID[pos2];
        if (((canalAct.equals("2") || Integer.parseInt(canalAct)>=4)     && !canalAct.equals("1000")) && espAct.equals("1")) {
            irParticipaciones();
        } else if (espAct.equals("14")) {
            irSKU();
        } else {
            if (existen == true) {
                irEstandar();
            } else {
                //Si no hay estándares, tomar la foto y seguir en Categorías
                Toast.makeText(getBaseContext(), "Tomar Foto", Toast.LENGTH_SHORT).show();
                at.actualizarT3(categoriasID[pos2], 4, espAct);
                operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FOTO'");
                new Thread() {
                    @Override
                    public void run() {
                        irFoto();
                    }
                }.start();
            }
        }
    }

    public void irFoto() {
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        Intent foto = new Intent(this, TomarFoto.class);
        startActivity(foto);
    }

    public void irParticipaciones() {
        Intent part = new Intent(this, Participaciones.class);
        startActivity(part);
    }

    public void irEstandar() {
        Intent estnd = new Intent(this, Estandares.class);
        startActivity(estnd);
    }

    public void irSKU() {
        //¿qué pasa si no existen skus?
        Intent sku = new Intent(this, SKUs.class);
        //SET FL = 2 en ACT
        operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='FL'");
        startActivity(sku);
    }

    public boolean revisarEstandares(int pos, String idt3) {
        //Leer si t4t está vacía
        ArrayList<String>[] estd = conGen.queryGeneral(getBaseContext(), "t4t", new String[]{"est"}, "esp='" + espAct + "' AND cat='" + categoriasID[pos] + "'");
        return (estd != null);
    }

    public void popUp3(int pos, final Categ holder, final CategoriaModel categoria) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = getString(R.string.conf_borrar_datos);
        texto.setText(tx);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        final int pos2 = pos;
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operaciones.queryNoData("delete from t6t WHERE esp=" + espAct + " and cat=" + categoriasID[pos2] );
                if (espAct.equals("1")) {
                    operaciones.queryNoData("delete from t7t WHERE cat=" + categoriasID[pos2]);
                }
                at.actualizarT3(categoriasID[pos2], 3, espAct);
                holder.tiene.setText("NO");
                categoria.tiene = "NO";
                holder.imgV.setImageResource(R.drawable.check_opto);
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void Volver(View v) {
        //Validar si todas las categorías están evaluadas
        ArrayList<String>[] eval = conGen.queryGeneral(getBaseContext(), "t3t", new String[]{"eval"}, "esp='" + espAct + "'");
        if (eval != null) {
            boolean evaluados = true;
            for (int e = 0; e < eval.length; e++) {
                String temp = eval[e].get(0);
                if (!temp.equals("1")) {
                    evaluados = false;
                }
            }
            //Si si
            if (evaluados == true) {
                //Actualizar módulo como evaluado eval = 1 en 't2t'
                if (tienece.equals("1") || tienece2.equals("1")) {
                    String queryCatC = "select count(t3t.encuesta) as ce from t3t where (t3t.encuesta || t3t.esp || t3t.cat) not in (select (t6t.encuesta || t6t.esp || t6t.cat) from t6t where rta='1' AND esp='" + espAct + "') AND esp='" + espAct + "' AND rta='1' AND (cat<15 OR cat >17)";
                    ArrayList<String>[] objCatC = conGen.queryObjeto2val(getBaseContext(), queryCatC, null);
                    if (Integer.parseInt(objCatC[0].get(0)) > 0) {
                        operaciones.queryNoData("UPDATE t2t SET eval='2' WHERE esp='" + espAct + "'");
                        PopUps pop = new PopUps();
                        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        pop.popUpConf(getBaseContext(), inflater, 6, 2);
                    } else {
                        String queryCatNO = "select count(t3t.encuesta) as ce from t3t where rta='1' AND esp='" + espAct + "'";
                        ArrayList<String>[] objCatNO = conGen.queryObjeto2val(getBaseContext(), queryCatNO, null);
                        if (Integer.parseInt(objCatNO[0].get(0)) > 0) {
                            operaciones.queryNoData("UPDATE t2t SET eval='1' WHERE esp='" + espAct + "'");
                            Intent menu = new Intent(this, Espacios.class);
                            startActivity(menu);
                        }else {
                            operaciones.queryNoData("UPDATE t2t SET eval='2' WHERE esp='" + espAct + "'");
                            PopUps pop = new PopUps();
                            final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            pop.popUpConf(getBaseContext(), inflater, 10, 1);
                        }
                    }
                } else {
                    String queryCatNO = "select count(t3t.encuesta) as ce from t3t where rta='1' AND esp='" + espAct + "'";
                    ArrayList<String>[] objCatNO = conGen.queryObjeto2val(getBaseContext(), queryCatNO, null);
                    if (Integer.parseInt(objCatNO[0].get(0)) > 0) {
                        operaciones.queryNoData("UPDATE t2t SET eval='1' WHERE esp='" + espAct + "'");
                        Intent menu = new Intent(this, Espacios.class);
                        startActivity(menu);
                    }else {
                        operaciones.queryNoData("UPDATE t2t SET eval='2' WHERE esp='" + espAct + "'");
                        PopUps pop = new PopUps();
                        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        pop.popUpConf(getBaseContext(), inflater, 10, 1);
                    }
                }

            } else {
                //Si no
                operaciones.queryNoData("UPDATE t2t SET eval='2' WHERE esp='" + espAct + "'");
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 6, 2);
            }
        }
    }

    public void fpocnut(View v) {
        //Validar si todas las categorías están evaluadas
        pocnut.setClickable(false);
        String queryCatSi = "SELECT count(rta) crta FROM t3t where esp=" + espAct + " and rta=1";
        ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryCatSi, null);

        Integer contesp = Integer.parseInt(fg.getContesp());
        if (Integer.parseInt(eval[0].get(0)) > 0) {

            try {
                String queryCatSiT3 = "select t3t.cat,'106_CATEG'.NCAT from t3t INNER JOIN '106_CATEG' ON t3t.cat = CAST('106_CATEG'.CAT AS INT)  where esp=" + espAct + " and rta=1";
                ArrayList<String>[] catSi = conGen.queryObjeto2val(getBaseContext(), queryCatSiT3, null);
                for (int e = 0; e < catSi.length; e++) {

                    String esp = "";
                    String cat = "";
                    String ncat = "";
                    esp = espAct;
                    cat = catSi[e].get(0);
                    ncat = catSi[e].get(1);
                    String queryCatE = "";

                    if (espAct.equals("3") || espAct.equals("9")) {
                         queryCatE = "SELECT count(encuesta) ce FROM t6t where id=" + 1 + " and esp=" + espAct + " and cat=" + cat;
                    } else {
                         queryCatE = "SELECT count(encuesta) ce FROM t6t where id=" + contesp + " and esp=" + espAct + " and cat=" + cat;
                    }

                    ArrayList<String>[] catE = conGen.queryObjeto2val(getBaseContext(), queryCatE, null);
                    if (catE[0].get(0).equals("0") && !cat.equals("15") && !cat.equals("16") && !cat.equals("17") ) {
                        at.insertarT6(esp, cat, ncat, 1);
                    }
                }
                if (espAct.equals("2") || espAct.equals("6")) {
                    String queryCatCom = "select CATC,NCATC from '207_CATCOMP' order by CATC ASC";
                    ArrayList<String>[] catCom = conGen.queryObjeto2val(getBaseContext(), queryCatCom, null);
                    for (int e = 0; e < catCom.length; e++) {
                        String esp = "";
                        String cat = "";
                        String ncat = "";
                        esp = espAct;
                        cat = catCom[e].get(0);
                        ncat = catCom[e].get(1);
                        at.insertarT6(esp, cat, ncat, 1);
                    }
                }
            } finally {
                //operaciones.queryNoData("UPDATE ACT SET VAL='" + (contesp + 1) + "' WHERE VA='CONESP'");
                Intent conteoespacios = new Intent(this, ConteoEspacios.class);
                startActivity(conteoespacios);
            }

        } else {
            //Si no
            Toast.makeText(getBaseContext(), "No hay Categorias en Si , por lo que no puede crear poc Nutresa", Toast.LENGTH_LONG).show();
        }
    }

    public void fpoccomp(View v) {
        poccom.setClickable(false);
        try {
            String queryCatCom = "select CATC,NCATC from '207_CATCOMP' order by CATC ASC";
            ArrayList<String>[] catCom = conGen.queryObjeto2val(getBaseContext(), queryCatCom, null);
                                                                                                                                                                                                                                                  for (int e = 0; e < catCom.length; e++) {
                String esp = "";
                String cat = "";
                String ncat = "";
                esp = espAct;
                cat = catCom[e].get(0);
                ncat = catCom[e].get(1);
                at.insertarT6(esp, cat, ncat, 2);
            }
        } finally {
            Intent conteoespacios = new Intent(this, ConteoEspacios.class);
            startActivity(conteoespacios);
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
        try {
            Toast.makeText(getBaseContext(), "Verificando preguntas TIPOA", Toast.LENGTH_SHORT).show();
        }
        finally {
            String aplicaTA = fg.crearTipoA();

            if (aplicaTA.equals("1")) {
                fg.ultimaPantallafta("Categorías");
                Intent valid = new Intent(this, TipoAMenu.class);
                startActivity(valid);
            } else {
                Toast.makeText(getBaseContext(), "No hay Preguntas TipoA Disponibles en este momento", Toast.LENGTH_SHORT).show();
            }
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

    public static Categorias getInstance() {
        return activityA;
    }
}