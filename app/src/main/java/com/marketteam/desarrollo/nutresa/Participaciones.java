package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Participaciones extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    Integer cent;
    String espAct, catAct,partAct, idC, idP, partic, particComp, entrep, idAud,ciucan;
    String[] entrepaños = {"2", "3", "4", "5", "6"};
    Spinner partE;
    RecyclerView recyclerPart;
    ArrayList<PartModel> lista;
    TextView tvCompleto;
    Button fin;
    int id = 0,cpart = 0,parte = 0;
    Integer PosFinal = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participaciones);
    }

    @Override
    public void onBackPressed() {
    }

    class Parti extends RecyclerView.ViewHolder {
        TextView idpart;
        TextView npart;
        EditText partt;
        EditText partn;

        public Parti(View itemView) {
            super(itemView);
            this.idpart = (TextView) itemView.findViewById(R.id.tvCodBar);
            this.npart = (TextView) itemView.findViewById(R.id.tvNombreActivo);
            this.partt = (EditText) itemView.findViewById(R.id.tvValTPartc);
            this.partn = (EditText) itemView.findViewById(R.id.tvValorNPartc);
        }
    }

    class PartModel {
        String textoid;
        String textonombre;
        String textopt;
        String textopn;

        public PartModel(String textop1, String textop2, String textop3, String textop4) {
            this.textoid = textop1;
            this.textonombre = textop2;
            this.textopt = textop3;
            this.textopn = textop4;
        }
    }

    class PartiAdapter extends RecyclerView.Adapter<Parti> {
        ArrayList<PartModel> lista;

        public PartiAdapter(ArrayList<PartModel> lista) {
            this.lista = lista;
        }

        @Override
        public Parti onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Parti(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_partc, parent, false));
        }

        @Override
        public void onBindViewHolder(final Parti holder, final int position) {
            final PartModel particip = lista.get(position);
            holder.idpart.setText(particip.textoid);
            holder.npart.setText(particip.textonombre);
            holder.partt.setText(particip.textopt);
            holder.partt.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() == KeyEvent.ACTION_DOWN && ((keyCode > 6 && keyCode < 17) || (event.getKeyCode() == KeyEvent.KEYCODE_DEL))) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                            String XD = holder.partt.getText().toString();
                            String XE = holder.partn.getText().toString();
                            if (XD.length() > 1) {
                                at.actualizarT7U(idAud, catAct, idP, XD.substring(1, (XD.length() - 1)), XE, position);
                                holder.partt.setText(XD.substring(1, (XD.length() - 1)));
                                particip.textopt = holder.partt.getText().toString();
                            } else {
                                at.actualizarT7U(idAud, catAct, idP, "", XE, position);
                                holder.partt.setText("");
                                particip.textopt = "";
                            }

                            return true;
                        } else {
                            String XD = holder.partt.getText().toString() + (keyCode - 7);
                            String XE = holder.partn.getText().toString();
                            at.actualizarT7U(idAud, catAct, idP, XD, XE, position);
                            holder.partt.setText(XD);
                            particip.textopt = XD;
                            return true;
                        }
                    }
                    return false;

                }
            });
            //holder.partt.addTextChangedListener(new listener(position,"1"));
            holder.partn.setText(particip.textopn);
            holder.partn.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() == KeyEvent.ACTION_DOWN && ((keyCode > 6 && keyCode < 17) || (event.getKeyCode() == KeyEvent.KEYCODE_DEL))) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                            String XD = holder.partt.getText().toString();
                            String XE = holder.partn.getText().toString();
                            if (XE.length() > 1) {
                                at.actualizarT7U(idAud, catAct, idP, XD, XE.substring(1, (XE.length() - 1)), position);
                                holder.partn.setText(XE.substring(1, (XE.length() - 1)));
                                particip.textopn = holder.partn.getText().toString();
                            } else {
                                at.actualizarT7U(idAud, catAct, idP, XD, "", position);
                                holder.partn.setText("");
                                particip.textopn = "";
                            }
                            return true;
                        } else {
                            String XD = holder.partt.getText().toString();
                            String XE = holder.partn.getText().toString() + (keyCode - 7);
                            at.actualizarT7U(idAud, catAct, idP, XD, XE, position);
                            holder.partn.setText(XE);
                            particip.textopn = XE;
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    public void cambiarpos(int posic) {
        PosFinal = posic;
    }

    @Override
    protected void onStart() {
        super.onStart();
        operaciones = new OperacionesBDInterna(getApplicationContext());
        conGen = new ConsultaGeneral();
        fg = new FuncionesGenerales(getBaseContext());
        at = new ActualizarTablas(getBaseContext());
        idC = fg.clienteActual();
        recyclerPart = (RecyclerView) findViewById(R.id.recyclerViewPart);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerPart.setLayoutManager(llm);
        lista = new ArrayList<>();
        fg.ultimaPantalla("Partic");
        partE = findViewById(R.id.spinnerCantE);
        tvCompleto = (TextView) findViewById(R.id.tvCampo3);
        fin = (Button) findViewById(R.id.buttonPartFin);
        idAud = fg.getAuditoria();
        ciucan = fg.getCiuCan();

        ArrayList<String> resCampos = fg.existeACT(5);
        String idt2 = resCampos.get(0);
        String idt3 = resCampos.get(1);
        String canalAct = resCampos.get(2);
        String subcAct = resCampos.get(3);
        espAct = resCampos.get(4);
        catAct = resCampos.get(5);
        String estndAct = resCampos.get(6);

        //Traer la participación
        ArrayList<String>[] participacion = conGen.queryObjeto(getBaseContext(), "SELECT '205_PREG'.preg,'121_PART'.NPART_T,'121_PART'.NPART_N FROM '203_ESP' INNER JOIN '204_CAT' ON '203_ESP'.IDC = '204_CAT'.IDC AND '203_ESP'.IDT2 = '204_CAT'.IDT2 INNER JOIN '205_PREG' ON '204_CAT'.IDC = '205_PREG'.IDC AND '204_CAT'.IDT3 = '205_PREG'.IDT3 INNER JOIN '121_PART' ON '205_PREG'.preg = '121_PART'.PART WHERE '203_ESP'.IDC='" + ciucan + "' AND '203_ESP'.ESP = '" + espAct + "' AND '204_CAT'.CAT = '" + catAct + "' AND '205_PREG'.TIPO = '3'", null);
        if (participacion != null) {
            cpart = participacion.length;
            //Mirar si tiene entrepaños
            idP = participacion[0].get(0);
            particComp = participacion[0].get(1); //Poner esta en el TextView Grande
            tvCompleto.setText(particComp);
            partic = participacion[0].get(2);
            ArrayList<String>[] objEnt = conGen.queryObjeto(getBaseContext(), "SELECT ep,part_t,part_n FROM t7t WHERE cat='" + catAct + "' AND part='" + idP + "' order by ep", null);
            if (objEnt != null) {
                //Si si - Sacar el último id y poner los entrepaños en el recycler
                for (int e = 0; e < objEnt.length; e++) {
                    String idE = objEnt[e].get(0);
                    String partT = objEnt[e].get(1);
                    String partN = objEnt[e].get(2);
                    PartModel em = new PartModel(idE, partic, partT, partN);
                    lista.add(em);
                    id = (Integer.parseInt(idE));
                }
                PartiAdapter ea = new PartiAdapter(lista);
                recyclerPart.setAdapter(ea);
            }
        } else {
            irEstandares();
        }

        partE.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entrepaños));
        partE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                entrep = entrepaños[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void irEstandares() {
        Intent est = new Intent(this, Estandares.class);
        startActivity(est);
    }
    public void irParticipacion2() {
        Intent est = new Intent(this, Participaciones2.class);
        startActivity(est);
    }

    public void agregarUno(View v) {
        id++;
        String idS = "" + id;
        PartModel em = new PartModel(idS, partic, "", "");
        at.insertarT7(id, idAud, catAct, idP);
        lista.clear();
        ArrayList<String>[] participacionx = conGen.queryObjeto(getBaseContext(), "SELECT '205_PREG'.preg,'121_PART'.NPART_T,'121_PART'.NPART_N FROM '203_ESP' INNER JOIN '204_CAT' ON '203_ESP'.IDC = '204_CAT'.IDC AND '203_ESP'.IDT2 = '204_CAT'.IDT2 INNER JOIN '205_PREG' ON '204_CAT'.IDC = '205_PREG'.IDC AND '204_CAT'.IDT3 = '205_PREG'.IDT3 INNER JOIN '121_PART' ON '205_PREG'.preg = '121_PART'.PART WHERE '203_ESP'.IDC='" + ciucan + "' AND '203_ESP'.ESP = '" + espAct + "' AND '204_CAT'.CAT = '" + catAct + "' AND '205_PREG'.TIPO = '3'", null);
        if (participacionx != null) {
            int lpart = participacionx.length;
            //Mirar si tiene entrepaños
            idP = participacionx[0].get(0);
            particComp = participacionx[0].get(1); //Poner esta en el TextView Grande
            tvCompleto.setText(particComp);
            partic = participacionx[0].get(2);
            ArrayList<String>[] objEnt = conGen.queryObjeto(getBaseContext(), "SELECT ep,part_t,part_n FROM t7t WHERE cat='" + catAct + "' AND part='" + idP + "' order by ep", null);
            if (objEnt != null) {
                //Si si - Sacar el último id y poner los entrepaños en el recycler
                for (int e = 0; e < objEnt.length; e++) {
                    String idE = objEnt[e].get(0);
                    String partT = objEnt[e].get(1);
                    String partN = objEnt[e].get(2);
                    PartModel emx = new PartModel(idE, partic, partT, partN);
                    lista.add(emx);
                    id = (Integer.parseInt(idE));
                }
                PartiAdapter eax = new PartiAdapter(lista);
                recyclerPart.setAdapter(eax);
            }
        }
    }

    public void agregarVarios(View v) {
        //Sacar número del spinner y hacer for con eso
        int ep = Integer.parseInt(entrep);
        for (int e = 0; e < ep; e++) {
            id++;
            String idS = "" + id;
            PartModel em = new PartModel(idS, partic, "", "");
            at.insertarT7(id, idAud, catAct, idP);

        }
        lista.clear();
        ArrayList<String>[] participacionx = conGen.queryObjeto(getBaseContext(), "SELECT '205_PREG'.preg,'121_PART'.NPART_T,'121_PART'.NPART_N FROM '203_ESP' INNER JOIN '204_CAT' ON '203_ESP'.IDC = '204_CAT'.IDC AND '203_ESP'.IDT2 = '204_CAT'.IDT2 INNER JOIN '205_PREG' ON '204_CAT'.IDC = '205_PREG'.IDC AND '204_CAT'.IDT3 = '205_PREG'.IDT3 INNER JOIN '121_PART' ON '205_PREG'.preg = '121_PART'.PART WHERE '203_ESP'.IDC='" + ciucan + "' AND '203_ESP'.ESP = '" + espAct + "' AND '204_CAT'.CAT = '" + catAct + "' AND '205_PREG'.TIPO = '3'", null);
        if (participacionx != null) {
            //Mirar si tiene entrepaños
            idP = participacionx[0].get(0);
            particComp = participacionx[0].get(1); //Poner esta en el TextView Grande
            tvCompleto.setText(particComp);
            partic = participacionx[0].get(2);
            ArrayList<String>[] objEnt = conGen.queryObjeto(getBaseContext(), "SELECT ep,part_t,part_n FROM t7t WHERE cat='" + catAct + "' AND part='" + idP + "' order by ep", null);
            if (objEnt != null) {
                //Si si - Sacar el último id y poner los entrepaños en el recycler
                for (int e = 0; e < objEnt.length; e++) {
                    String idE = objEnt[e].get(0);
                    String partT = objEnt[e].get(1);
                    String partN = objEnt[e].get(2);
                    PartModel emx = new PartModel(idE, partic, partT, partN);
                    lista.add(emx);
                    id = (Integer.parseInt(idE));
                }
                PartiAdapter eax = new PartiAdapter(lista);
                recyclerPart.setAdapter(eax);
            }
        }
    }

    public void eliminarUltimo(View v) {
        //Sacar número del spinner y hacer for con eso
        if (id > 0) {
            String idS = "" + id;
            lista.remove(lista.size() - 1);
            at.eliminarT7(idAud, catAct, idP, idS);
            PartiAdapter ea = new PartiAdapter(lista);
            recyclerPart.setAdapter(ea);
            id = id - 1;
        } else {
            Toast.makeText(getBaseContext(), "No puede eliminar más participaciones.", Toast.LENGTH_LONG).show();
        }
    }

    public void verificarDatos(View v) {
        boolean terminado = true;
        Integer canalx = Integer.parseInt(fg.getCan());
        Integer subcanalx = Integer.parseInt(fg.getSCan());
        Integer tipop = 1;
        Integer totaln = 0;
        if ((canalx == 2 && (subcanalx == 6 || subcanalx == 7)) || canalx > 3 ){
            tipop=2;
        }
        ArrayList<String>[] participacionx = conGen.queryObjeto(getBaseContext(), "select part_t,part_n from t7t where CAT='" + catAct + "' AND part='" + idP  + "' ORDER BY EP ASC", null);
        if (participacionx != null) {
            for (int t = 0; t < participacionx.length; t++) {
                String t1 = participacionx[t].get(0);
                String t2 = participacionx[t].get(1);
                if ((t1 == null) || (t2 == null)) {
                    terminado = false;
                    Toast.makeText(getBaseContext(), "El valor de total o nutresa no puede estar vacio", Toast.LENGTH_SHORT).show();
                } else if((t1.length() > 6) || (t2.length() > 6)){
                    terminado = false;
                    Toast.makeText(getBaseContext(), "Los campos tiene longitud máxima de 6 caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    if (t1.equals("")) {
                        t1 = "0";
                    }
                    if (t2.equals("")) {
                        t2 = "0";
                    }

                    int v1 = Integer.parseInt(t1);
                    int v2 = Integer.parseInt(t2);
                    totaln = totaln + v2;
                    if (v1 > 1000 && tipop==1) {
                        terminado = false;
                        Toast.makeText(getBaseContext(), "Recuerde que el Total no puede ser superior a 1000 en este canal", Toast.LENGTH_SHORT).show();

                    }
                    if (v1 == 0) {
                        terminado = false;
                        Toast.makeText(getBaseContext(), "El valor de total no puede ser 0 (Cero)", Toast.LENGTH_SHORT).show();

                    }

                    if (v1 < v2) {
                        terminado = false;
                        Toast.makeText(getBaseContext(), "Recuerde que el Total no puede ser menor al valor Nutresa", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            if (totaln == 0){
                terminado = false;
                Toast.makeText(getBaseContext(), "El total nutresa no puede ser 0 o estar vacio en todos los entrepaños", Toast.LENGTH_SHORT).show();
            }
            if (terminado==true) {
                if (cpart==1) {
                    irEstandares();
                }else {
                    irParticipacion2();
                }

            }
        }
    }

    public void volver(View v) {
        Intent cat = new Intent(this, Categorias.class);
        startActivity(cat);
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
            fg.ultimaPantallafta("Partic");
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
}