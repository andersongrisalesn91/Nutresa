package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class SKUs extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String flujo, idC, idt2, idt3, canalAct, subcAct, espAct, catAct, estndAct, skuAct;
    RecyclerView recyclerSKU;
    ArrayList<SKUModel> lista;
    String[] skusID, skusRTA;
    TextView nombreCat;
    static SKUs activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skus);
        activityA = this;
    }

    @Override
    public void onBackPressed() {
    }

    class SKU extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView imgVSi;
        ImageView imgVNo;

        public SKU(View itemView) {
            super(itemView);
            this.nombre = (TextView) itemView.findViewById(R.id.nombreEstSku);
            this.imgVSi = (ImageView) itemView.findViewById(R.id.rtaEstSku);
            this.imgVNo = (ImageView) itemView.findViewById(R.id.rtaEstSkuNo);
        }
    }

    class SKUModel {
        String nom;
        ImageView imgVSi;
        ImageView imgVNo;

        public SKUModel(String nombre, ImageView ivSi, ImageView ivNo) {
            this.nom = nombre;
            this.imgVSi = ivSi;
            this.imgVNo = ivNo;
        }
    }

    class SKUAdapter extends RecyclerView.Adapter<SKU> {
        ArrayList<SKUModel> lista;

        public SKUAdapter(ArrayList<SKUModel> lista) {
            this.lista = lista;
        }

        @Override
        public SKU onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SKU(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_est_sku, parent, false));
        }

        @Override
        public void onBindViewHolder(final SKU holder, final int position) {
            final SKUModel producto = lista.get(position);
            holder.nombre.setText(producto.nom);

            if (skusRTA[position].equals("1")) {
                holder.imgVSi.setImageResource(R.drawable.rdb);
                holder.imgVNo.setImageResource(R.drawable.rdnoselb);
            }
            if (skusRTA[position].equals("2")) {
                holder.imgVSi.setImageResource(R.drawable.rdnoselb);
                holder.imgVNo.setImageResource(R.drawable.rdb);
            }
            if (skusRTA[position].equals("0")) {
                holder.imgVSi.setImageResource(R.drawable.rdnoselb);
                holder.imgVNo.setImageResource(R.drawable.rdnoselb);
            }

            holder.imgVSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editarSKUSi(position, holder, producto);
                }
            });
            holder.imgVNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editarSKUNo(position, holder, producto);
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
        if (Validaciones.getInstance() != null) {
            Validaciones.getInstance().finish();
        }
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        //flujo = getIntent().getStringExtra("flujo");
        flujo = "";
        ArrayList<String>[] objFlujo = conGen.queryGeneral(getBaseContext(), "ACT", new String[]{"VAL"}, "VA='FL'");
        if (objFlujo != null) {
            flujo = objFlujo[0].get(0);
        }
        recyclerSKU = (RecyclerView) findViewById(R.id.recyclerSKUs);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerSKU.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("SKUs");
        idC = fg.clienteActual();
        nombreCat = (TextView) findViewById(R.id.tvNomCatSku);
        //Depende de la pantalla anterior para su funcionamiento (Flujo1 - Flujo2)
        listarSKUs();
    }

    public void listarSKUs() {
        ArrayList<String> resCampos = fg.existeACT(6);
        idt2 = resCampos.get(0);
        idt3 = resCampos.get(1);
        canalAct = resCampos.get(2);
        subcAct = resCampos.get(3);
        espAct = "14";
        catAct = resCampos.get(5);
        estndAct = resCampos.get(6);
        skuAct = resCampos.get(7);
        String nombreCategoria = fg.nombreCatAct(catAct);
        nombreCat.setText(nombreCategoria);
        //SKUs Tipo=2
        String querySku;
        if (flujo.equals("1")) {
            querySku = "SELECT t5t.sku,'110_SKU'.NSKU,t5t.rta FROM t5t join '110_SKU' on t5t.sku = '110_SKU'.SKU where esp = " + espAct + " and cat = " + catAct + " and t5t.rta=0 order by t5t.sku asc";
        } else {
            querySku = "SELECT t5t.sku,'110_SKU'.NSKU,t5t.rta FROM t5t join '110_SKU' on t5t.sku = '110_SKU'.SKU where esp = " + espAct + " and cat = " + catAct + " order by t5t.sku asc";
        }

        ArrayList<String>[] objSkus = conGen.queryObjeto2val(getBaseContext(), querySku, null);

        if (objSkus != null) {
            //consulta para a partir del ID traer el nombre/etiqueta
            skusID = new String[objSkus.length];
            skusRTA = new String[objSkus.length];
            for (int i = 0; i < objSkus.length; i++) {
                String idS = objSkus[i].get(0);
                String rtat = objSkus[i].get(2);
                skusID[i] = idS;
                skusRTA[i] = rtat;
                String nSku = objSkus[i].get(1);
                ImageView ivSI = new ImageView(this);
                ImageView ivNO = new ImageView(this);
                SKUModel sm = new SKUModel(nSku, ivSI, ivNO);
                lista.add(sm);
            }
            SKUAdapter sa = new SKUAdapter(lista);
            recyclerSKU.setAdapter(sa);
        } else {
            Intent cat = new Intent(this, Categorias.class);
            startActivity(cat);
        }

    }

    public void editarSKUSi(int pos, SKU holder, SKUModel producto) {
        final int pos2 = pos;
        final SKU holder2 = holder;
        final SKUModel product = producto;
        //Actualizar estándar en la tabla correspondiente

        if (flujo.equals("1")) {
            if (Integer.parseInt(skusRTA[pos]) == 1) {
                at.actualizarT5(skusID[pos], 3, espAct, catAct);
                holder2.imgVSi.setImageResource(R.drawable.rdnoselb);
                skusRTA[pos] = "0";
            } else {
                at.actualizarT5(skusID[pos], 1, espAct, catAct);
                holder2.imgVSi.setImageResource(R.drawable.rdb);
                skusRTA[pos] = "1";
            }
        } else if (flujo.equals("2")) {
            at.actualizarT5(skusID[pos], 1, espAct, catAct);
            holder2.imgVSi.setImageResource(R.drawable.rdb);
            holder2.imgVNo.setImageResource(R.drawable.rdnoselb);
            skusRTA[pos] = "1";

        }
        //Actualizar SKU en la tabla correspondiente
        //operaciones.queryNoData("UPDATE ACT SET VAL ='" + skusID[pos] + "' WHERE VA='SKU'");
    }

    public void editarSKUNo(int pos, SKU holder, SKUModel producto) {
        final int pos2 = pos;
        final SKU holder2 = holder;
        final SKUModel product = producto;

        if (flujo.equals("2")) {
            //Si es el flujo 1 - Que sólo con presionar el SKU, lo marque como que existe
            at.actualizarT5(skusID[pos2], 2, espAct, catAct);
            holder2.imgVSi.setImageResource(R.drawable.rdnoselb);
            holder2.imgVNo.setImageResource(R.drawable.rdb);
            skusRTA[pos] = "2";
        }
        //Actualizar SKU en la tabla correspondiente
        //operaciones.queryNoData("UPDATE ACT SET VAL ='" + skusID[pos] + "' WHERE VA='SKU'");
    }

    public void Volver(View v) {
        //Siempre vuelvo a Categorías
        //Si los skus están evaluados marco la categoría como evaluada
        String querySku = "SELECT t5t.rta FROM t5t where esp = " + espAct + " and cat = " + catAct + " and rta=0";
        ArrayList<String>[] respuesta = conGen.queryObjeto2val(getBaseContext(), querySku, null);
        String querySKUSI = "SELECT count(t5t.rta) as ev FROM t5t where esp = " + espAct + " and cat = " + catAct + " and rta=1";
        ArrayList<String>[] skusi = conGen.queryObjeto2val(getBaseContext(), querySKUSI, null);
        boolean foto = false;
        if (respuesta == null) {
            String queryFoto = "SELECT count(tft.esp) as cf FROM tft where cat = " + catAct;
            ArrayList<String>[] eval = conGen.queryObjeto2val(getBaseContext(), queryFoto, null);
            if (eval != null) {
                String valor = eval[0].get(0);
                Integer valori = Integer.parseInt(valor);
                if (valori != 0) {
                    foto = true;
                }
            }
            if (foto == true) {
                if (Integer.parseInt(skusi[0].get(0)) > 0) {
                    operaciones.queryNoData("UPDATE t3t SET rta=1,eval=1 WHERE esp='" + espAct + "' and cat='" + catAct + "';");
                } else {
                    operaciones.queryNoData("UPDATE t3t SET rta=1,eval=1 WHERE esp='" + espAct + "' and cat='" + catAct + "';");
                }

                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(getBaseContext(), inflater, 8, 3);
            } else {
                if (Integer.parseInt(skusi[0].get(0)) > 0) {
//                    String texto = "Tome Una Foto para los SKU";
//                    Toast.makeText(getBaseContext(), texto, Toast.LENGTH_SHORT).show();
                    if (flujo.equals("1")) {
                        operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='FOTO'");
                    } else {
                        operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FOTO'");
                    }
                    operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
                    startActivity(new Intent(this, TomarFoto.class));
                } else {
                    operaciones.queryNoData("UPDATE t3t SET rta=1,eval=1 WHERE esp='" + espAct + "' and cat='" + catAct + "';");
                    PopUps pop = new PopUps();
                    final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    pop.popUpConf(getBaseContext(), inflater, 8, 3);
                }
            }

        } else {
            operaciones.queryNoData("UPDATE t3t SET rta=1,eval=2 WHERE esp='" + espAct + "' and cat='" + catAct + "';");
            operaciones.queryNoData("UPDATE t2t SET rta=1,eval=2 WHERE esp=14");
            PopUps pop = new PopUps();
            final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            pop.popUpConf(getBaseContext(), inflater, 8, 3);
        }

    }



    /*public void popUp1(final int pos, final SKU holder, final SKUModel producto) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = getString(R.string.conf_sku);
        texto.setText(tx);
        texto.setTextSize(24);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        cancel.setText("NO");
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        ok.setText("SI");
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.rta = "SI";
                holder.tiene.setText("SI");
                at.actualizarT5(skusID[pos], 1, espAct, catAct);
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.rta = "NO";
                holder.tiene.setText("NO");
                at.actualizarT5(skusID[pos], 2, espAct, catAct);
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }*/

    public void tfoto(View vis) {
        operaciones.queryNoData("UPDATE ACT SET VAL='12' WHERE VA='FOTO'");
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this, TomarFoto.class));
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
        Toast.makeText(getBaseContext(), "Verificando preguntas TIPOA", Toast.LENGTH_SHORT).show();
        String aplicaTA = fg.crearTipoA();

        if (aplicaTA.equals("1")) {
            fg.ultimaPantallafta("SKUs");
            Intent valid = new Intent(this, TipoAMenu.class);
            startActivity(valid);
        } else {
            Toast.makeText(getBaseContext(), "No hay Preguntas TipoA Disponibles en este momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void irValidaciones(View view) {
        Toast.makeText(getBaseContext(), "Verificando Validaciones", Toast.LENGTH_SHORT).show();
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

    public static SKUs getInstance() {
        return activityA;
    }
}