package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DetalleCliente extends AppCompatActivity {

    RelativeLayout rlContenedor;
    RecyclerView recyclerBusqOtra;
    ArrayList<ClienteModel> lista;
    String cliente, idclienteLC, nclienteLC, eclienteLC, canalA, scanalA, flujo;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    TextView idET;
    EditText busq, propET, estbET, dirET, barET, telET, correoET, buscadorNEW;
    Spinner canET, scET;
    ImageButton pB, eB, dB, bB, tB, cB, canB, scB, busC;
    String[] canales, subcanales;
    Button irMenu;
    int b1 = 2, b2 = 2, b3 = 2, b4 = 2, b5 = 2, b6 = 2, b7 = 2, b8 = 2, bc = 2;
    View rvView, datosTable;
    TableLayout tableLayout;
    String ciudad, direccion, barrio, telefono, correo, canal, subC,tipo;
    ArrayList<String> datosAnt, datosNuev;
    static DetalleCliente activityA;
    Integer count;
    Integer cconfir = 0;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cliente);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SeleccionCuestionario.getInstance() != null){SeleccionCuestionario.getInstance().finish();}
        if(MenuInicio.getInstance() != null){MenuInicio.getInstance().finish();}
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) { Log.e("first","error"); }
        Criteria criteria = new Criteria();
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        buscadorNEW = (EditText) findViewById(R.id.editTextBuscarDetalle);
        busC = (ImageButton) findViewById(R.id.IVBusc);
        buscadorNEW.setVisibility(View.INVISIBLE);
        busC.setVisibility(View.INVISIBLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rvView = inflater.inflate(R.layout.ver_recycler, null);
        datosTable = inflater.inflate(R.layout.ver_datos_cliente, null);
        datosAnt = new ArrayList<>();
        datosNuev = new ArrayList<>();
        //Update campo ultP
        operaciones = new OperacionesBDInterna(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("DetalleCliente");
        at = new ActualizarTablas(getBaseContext());
        rlContenedor = (RelativeLayout) findViewById(R.id.relativeLayoutDetalle);
        recyclerBusqOtra = (RecyclerView) rvView.findViewById(R.id.recyclerDatosDetalle);
        tableLayout = (TableLayout) datosTable.findViewById(R.id.tableLayoutcliente);
        busq = (EditText) findViewById(R.id.editTextBuscarDetalle);
        idET = (TextView) datosTable.findViewById(R.id.tvIdCliente);
        propET = (EditText) datosTable.findViewById(R.id.etNomProp);
        estbET = (EditText) datosTable.findViewById(R.id.etNomEstb);
        dirET = (EditText) datosTable.findViewById(R.id.etDirecc);
        barET = (EditText) datosTable.findViewById(R.id.etBarrio);
        telET = (EditText) datosTable.findViewById(R.id.etTelefono);
        correoET = (EditText) datosTable.findViewById(R.id.etCorreoE);
        canET = (Spinner) datosTable.findViewById(R.id.spinnerCanalDC);
        irMenu = (Button) findViewById(R.id.buttonIrMenu);
        scET = (Spinner) datosTable.findViewById(R.id.spinnerSubCanalDC);
        conGen = new ConsultaGeneral();
        //Leer de PA el cliente
        idclienteLC = "";
        String queryCli = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] objeto = conGen.queryObjeto(getBaseContext(), queryCli, new String[]{"cliA"});
        if ((objeto == null) || (objeto.length < 1)) {//No existe ????
        } else {
            idclienteLC = objeto[0].get(0);
        }
        //Consulta de los datos
        //Revisar el flujo
        ArrayList<String>[] objFlujo = conGen.queryGeneral(getBaseContext(), "PA", new String[]{"va"}, "pa='opc'");
        if (objFlujo != null) {
            flujo = objFlujo[0].get(0);
        }
        String queryTipoM = "SELECT VAL FROM ACT WHERE VA='TIPOM'";
        ArrayList<String>[] tipox = conGen.queryObjeto2val(getBaseContext(), queryTipoM, null);
        tipo = tipox[0].get(0);
        String TABLE_NAME = "", where = "";
        String[] columns = {};
        if (flujo.equals("1")) {
            TABLE_NAME = "'300_MAESTRO'";
            columns = new String[]{"CIU", "NCLI1", "NCLI2", "DIR", "BAR", "TEL", "CAN", "SUBCAN"};
            where = "CLI='" + idclienteLC + "' and tipo=" + tipo;
        } else if (flujo.equals("2")) {
            TABLE_NAME = "'t1'";
            columns = new String[]{"maestro1", "maestro2", "maestro3", "maestro4", "maestro5", "maestro6", "maestro8", "maestro9"};
            where = "cliente_t='" + idclienteLC + "'";
        }
        ArrayList<String>[] datosC = conGen.queryGeneral(getBaseContext(), TABLE_NAME, columns, where);
        //Llenar campos
        if (datosC != null) {
            String ciudad2 = datosC[0].get(0);
            StringTokenizer st = new StringTokenizer(ciudad2, ",");
            ciudad = st.nextToken();
            datosAnt.add(ciudad);
            nclienteLC = datosC[0].get(1);
            datosAnt.add(nclienteLC);
            eclienteLC = datosC[0].get(2);
            datosAnt.add(eclienteLC);
            direccion = datosC[0].get(3);
            datosAnt.add(direccion);
            barrio = datosC[0].get(4);
            datosAnt.add(barrio);
            telefono = datosC[0].get(5);
            datosAnt.add(telefono);
            canal = datosC[0].get(6);
            subC = datosC[0].get(7);
            correo = "";
            datosAnt.add(correo);
            st = new StringTokenizer(canal, ",");
            canalA = st.nextToken();
            datosAnt.add(canalA);
            st = new StringTokenizer(subC, ",");
            scanalA = st.nextToken();
            datosAnt.add(scanalA);
            idET.setText(idclienteLC);
            propET.setText(nclienteLC);
            estbET.setText(eclienteLC);
            dirET.setText(direccion);
            barET.setText(barrio);
            telET.setText(telefono);
            correoET.setText(correo);
            //Query de objeto
            String Filtro = "";


            String queryObjCan = "SELECT CAN,NCAN FROM '103_CANAL'";
            ArrayList<String>[] objCan = conGen.queryObjeto2val(getBaseContext(), queryObjCan, null);

            String queryobjSubCan = "SELECT SUBCAN,NSUBCAN FROM '104_SUBCAN' WHERE TIPO='" + tipox[0].get(0) +"'";
            ArrayList<String>[] objSubCan = conGen.queryObjeto2val(getBaseContext(), queryobjSubCan, null);

            if (objCan != null) {
                if (objSubCan != null) {
                    canales = new String[objCan.length];
                    String[] canalesS = new String[objCan.length];
                    for (int c = 0; c < objCan.length; c++) {
                        canales[c] = objCan[c].get(0);
                        canalesS[c] = objCan[c].get(1);
                    }
                    subcanales = new String[objSubCan.length];
                    String[] subcanalesS = new String[objSubCan.length];
                    for (int s = 0; s < objSubCan.length; s++) {
                        subcanales[s] = objSubCan[s].get(0);
                        subcanalesS[s] = objSubCan[s].get(1);
                    }
                    int selectC = -1;
                    int selectSC = -1;
                    for (int j = 0; j < canales.length; j++) {
                        if (canales[j].equals(canalA)) {
                            selectC = j;
                            //break;
                        }
                    }
                    for (int k = 0; k < subcanales.length; k++) {
                        if (subcanales[k].equals(scanalA)) {
                            selectSC = k;
                            //break;
                        }
                    }
                    canET.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, canalesS));
                    scET.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subcanalesS));
                    canET.setSelection(selectC);
                    scET.setSelection(selectSC);
                } else {//No hay subcanales
                }
            }
        } else { //No hay canales
        }
        //Poner campos como no editables
        idET.setEnabled(false);
        propET.setEnabled(false);
        estbET.setEnabled(false);
        dirET.setEnabled(false);
        barET.setEnabled(false);
        telET.setEnabled(false);
        correoET.setEnabled(false);
        canET.setEnabled(false);
        scET.setEnabled(false);
        //Poner en el relative layout los datos - rlDatos
        rlContenedor.removeAllViews();
        rlContenedor.addView(datosTable, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        accBotones();
        operaciones.close();
        buscadorNEW.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onBackPressed() { }

    class Cliente extends RecyclerView.ViewHolder {
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

    class ClienteModel {
        String id, propietario, estab;

        public ClienteModel(String id, String propietario, String establecimiento) {
            this.id = id;
            this.propietario = propietario;
            this.estab = establecimiento;
        }
    }

    class ClienteAdapter extends RecyclerView.Adapter<Cliente> {
        ArrayList<ClienteModel> lista;

        public ClienteAdapter(ArrayList<ClienteModel> lista) {
            this.lista = lista;
        }

        @Override
        public Cliente onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Cliente(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_cliente2, parent, false));
        }

        @Override
        public void onBindViewHolder(Cliente holder, int position) {
            final ClienteModel per = lista.get(position);
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
                public void onClick(View view) {
                    funcPopUp(sId, sP, sE);
                }
            });
            holder.estab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    funcPopUp(sId, sP, sE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    public void Buscar(View v) {
        String queryTipoM = "SELECT VAL FROM ACT WHERE VA='TIPOM'";
        ArrayList<String>[] tipox = conGen.queryObjeto2val(getBaseContext(), queryTipoM, null);
        //Realizar la búsqueda
        String[] carac = {"|", "°", "¬", "!", "\"", "@", "#", "$", "%", "/", "\'", "(", ")", "=", "?", "'", "¿", "¡",
                "´", "¨", "+", "*", "{", "}", "[", "]", "^", "`", "-", "_", ".", ":", ",", ";", "<", ">"};
        cliente = busq.getText().toString().toUpperCase();
        if (cliente.equals("") || cliente == null || cliente.length() < 4) {
            Toast.makeText(getBaseContext(), "Se requieren 4 caracteres válidos como mínimo para iniciar la búsqueda", Toast.LENGTH_SHORT).show();
        } else {
            boolean val = true;
            for (int i = 0; i < cliente.length(); i++) {
                for (int j = 0; j < carac.length; j++) {
                    if (cliente.contains(carac[j])) {
                        val = false;
                    }
                }
            }
            if (val) {
                String like1 = "((CLI = '" + cliente + "' OR CLI LIKE '%" + cliente + "%')";
                String like2 = "(NCLI1 = '" + cliente + "' OR NCLI1 LIKE '%" + cliente + "%')";
                String like3 = "(NCLI2 = '" + cliente + "' OR NCLI2 LIKE '%" + cliente + "%')";
                String like = like1 + " OR " + like2 + " OR " + like3 + ") AND TIPO='" + tipox[0].get(0) + "'";
                ConsultaGeneral query = new ConsultaGeneral();
                ArrayList<String>[] objRes = query.queryGeneral(getBaseContext(), "'300_MAESTRO'", new String[]{"CLI", "NCLI1", "NCLI2"}, like);
                if (objRes != null) {
                    int size = objRes.length;
                    //Quitar layout de los datos - Poner el Recycler
                    rlContenedor.removeAllViews();
                    rlContenedor.addView(rvView);
                    irMenu.setVisibility(View.INVISIBLE);
                    lista = new ArrayList<>();
                    LinearLayoutManager llm = new LinearLayoutManager(this);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerBusqOtra.setLayoutManager(llm);

                    for (int i = 0; i < size; i++) {
                        String idObj = objRes[i].get(0).toString();
                        String propObj = objRes[i].get(1).toString();
                        String estbObj = objRes[i].get(2).toString();
                        ClienteModel cl = new ClienteModel(idObj, propObj, estbObj);
                        lista.add(cl);
                    }

                    ClienteAdapter adapter = new ClienteAdapter(lista);
                    recyclerBusqOtra.setAdapter(adapter);
                    View view = this.getCurrentFocus();
                    view.clearFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "No hay clientes que coincidan con el criterio de busqueda", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getBaseContext(), "No se permiten caracteres especiales como: !#$%&/(.),*-+=?¡", Toast.LENGTH_SHORT).show();
            }

        }
        operaciones.close();
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) { Log.e("first","error"); }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                location = loc;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
    }

    public void funcPopUp(String id, String propietario, String estab) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        PopUps pop = new PopUps();
        pop.popUpCliente(getBaseContext(), inflater, new String[]{id, propietario, estab});
    }

    public void accBotones() {
        eB = (ImageButton) findViewById(R.id.imgbEstab);
        pB = (ImageButton) findViewById(R.id.imgbPropie);
        dB = (ImageButton) findViewById(R.id.imgbDirecc);
        bB = (ImageButton) findViewById(R.id.imgbBarrio);
        tB = (ImageButton) findViewById(R.id.imgbTelefono);
        cB = (ImageButton) findViewById(R.id.imgbCorreo);
        canB = (ImageButton) findViewById(R.id.imgbCanal);
        scB = (ImageButton) findViewById(R.id.imgbSubC);

        eB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b1 == 1) {
                    b1 = 2;
                } else {
                    b1 = 1;
                }
                fBotones("Estab", b1);
            }
        });
        pB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b2 == 1) {
                    b2 = 2;
                } else {
                    b2 = 1;
                }
                fBotones("Prop", b2);
            }
        });
        dB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b3 == 1) {
                    b3 = 2;
                } else {
                    b3 = 1;
                }
                fBotones("Dir", b3);
            }
        });
        bB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b4 == 1) {
                    b4 = 2;
                } else {
                    b4 = 1;
                }
                fBotones("Barrio", b4);
            }
        });
        tB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b5 == 1) {
                    b5 = 2;
                } else {
                    b5 = 1;
                }
                fBotones("Tel", b5);
            }
        });
        cB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b6 == 1) {
                    b6 = 2;
                } else {
                    b6 = 1;
                }
                fBotones("Correo", b6);
            }
        });
        canB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b7 == 1) {
                    b7 = 2;
                } else {
                    b7 = 1;
                }
                fBotones("Canal", b7);
            }
        });
        scB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canal.equals("1000") && !tipo.equals("4")) {
                    if (b8 == 1) {
                        b8 = 2;
                    } else {
                        b8 = 1;
                    }
                    fBotones("SubC", b8);
                }
            }
        });
    }

    public void fBotones(String campo, int acc) {
        if (acc == 2) {
            //Obtener texto y desactivar edición
            if (campo.equals("Prop")) {
                nclienteLC = propET.getText().toString();
                propET.setEnabled(false);
                pB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("Estab")) {
                eclienteLC = estbET.getText().toString();
                estbET.setEnabled(false);
                eB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("Dir")) {
                direccion = dirET.getText().toString();
                dirET.setEnabled(false);
                dB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("Barrio")) {
                barrio = barET.getText().toString();
                barET.setEnabled(false);
                bB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("Tel")) {
                telefono = telET.getText().toString();
                telET.setEnabled(false);
                tB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("Correo")) {
                correo = correoET.getText().toString();
                correoET.setEnabled(false);
                cB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("Canal")) {
                canET.setEnabled(false);
                canB.setImageResource(R.drawable.lapiz_opt);
            } else if (campo.equals("SubC")) {
                scET.setEnabled(false);
                scB.setImageResource(R.drawable.lapiz_opt);
            }
        } else {
            //Funciones de edición
            if (campo.equals("Prop")) {
                propET.setEnabled(true);
                pB.setImageResource(R.drawable.check_opto);
            } else if (campo.equals("Estab")) {
                estbET.setEnabled(true);
                eB.setImageResource(R.drawable.check_opto);
            } else if (campo.equals("Dir")) {
                dirET.setEnabled(true);
                dB.setImageResource(R.drawable.check_opto);
            } else if (campo.equals("Barrio")) {
                barET.setEnabled(true);
                bB.setImageResource(R.drawable.check_opto);
            } else if (campo.equals("Tel")) {
                telET.setEnabled(true);
                tB.setImageResource(R.drawable.check_opto);
            } else if (campo.equals("Correo")) {
                correoET.setEnabled(true);
                cB.setImageResource(R.drawable.check_opto);
            } else if (campo.equals("Canal")) {
                if (!canal.equals("1000") && !tipo.equals("4")) {
                    canET.setEnabled(true);
                    canB.setImageResource(R.drawable.check_opto);
                    canET.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                            canal = canales[pos];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            } else if (campo.equals("SubC")) {
                if (!canal.equals("1000") && !tipo.equals("4")) {
                    scET.setEnabled(true);
                    scB.setImageResource(R.drawable.check_opto);
                    //Según el canal escogido
                    ArrayList<String>[] objSC = conGen.queryGeneral(getBaseContext(), "'202_CAN_SUBCAN'", new String[]{"SUBCAN"}, "IDC='" + canal + "'");
                    if (objSC != null) {
                        int sizeSC = objSC.length;
                        //Recorrer objSC para ir a buscar el texto del subcanal
                        subcanales = new String[sizeSC];
                        for (int s = 0; s < sizeSC; s++) {
                            String idSubCanal = objSC[s].get(0);
                            //Consulta para traer el texto del subcanal con id = idSubCanal
                            ArrayList<String>[] objNombres = conGen.queryGeneral(getBaseContext(), "'104_SUBCAN'", new String[]{"NSUBCAN"}, "SUBCAN='" + idSubCanal + "' AND TIPO='" + tipo + "'");
                            int size2 = objNombres.length;
                            for (int n = 0; n < size2; n++) {
                                subcanales[s] = objNombres[n].get(0);
                            }
                        }
                        scET.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subcanales));
                        scET.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                                subC = subcanales[pos];
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            }
        }
    }

    public void IrMenu(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = getString(R.string.conf_datos);;
        texto.setText(tx);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        final Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);

        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        //PopUp de confirmación de datos
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cconfir == 0) {
                    correo = correoET.getText().toString();
                    telefono = telET.getText().toString();
                    if (correo.equals("") || correo.length() < 7 || telefono.equals("") || telefono.length() <7) {
                        Toast.makeText(getBaseContext(), "El campo correo o telefono es obligatorio y debe ser más largo que 7 caracteres", Toast.LENGTH_SHORT).show();
                    } else {
                        cconfir++;
                        IrMenuV2();
                    }
                }
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void IrMenuV2() {
        String query = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] encuesta = conGen.queryObjeto(this, query, new String[]{"AUD"});
        String encuAct = encuesta[0].get(0);

        if (flujo.equals("1")) {
            operaciones.queryNoData("DELETE FROM t1t");
            operaciones.queryNoData("DELETE FROM t2t");
            operaciones.queryNoData("DELETE FROM t3t");
            operaciones.queryNoData("DELETE FROM t4t");
            operaciones.queryNoData("DELETE FROM t5t");
            operaciones.queryNoData("DELETE FROM t6t");
            operaciones.queryNoData("DELETE FROM t7t");
            operaciones.queryNoData("DELETE FROM t8t");
            operaciones.queryNoData("DELETE FROM t9t");
            operaciones.queryNoData("DELETE FROM t10t");
            operaciones.queryNoData("DELETE FROM t11t");
            operaciones.queryNoData("DELETE FROM tft");
            datosNuev.add(ciudad);
            datosNuev.add(propET.getText().toString());
            datosNuev.add(estbET.getText().toString());
            datosNuev.add(dirET.getText().toString());
            datosNuev.add(barET.getText().toString());
            datosNuev.add(telET.getText().toString());
            datosNuev.add(correo);
            datosNuev.add(canal);
            datosNuev.add(subC);
            double [] gps;
            if(location == null){
                gps = new double [] {0,0};
            } else {
                gps = new double [] {location.getLatitude(),location.getLongitude()};
            }

            at.insertarT1(idclienteLC, datosAnt, datosNuev,gps);
            //Insertar en todas las tablas
            at.insertarT2(); //Espacios
            at.insertarT3(); //Todas las categorías
            at.insertarT4();//Todos los estándares
            at.insertarT5();//Todos los SKUs
            at.insertarT10();//Todos los SKUsC
            at.insertarT11();//Todos los Activos
            operaciones.close();
            //Ir al menú
            Intent menu = new Intent(getBaseContext(), MenuOpciones.class);
            startActivity(menu);
        } else {
            cconfir = 1;
            try {
                operaciones.queryNoData("INSERT INTO t1t (encuesta,maquina ,fecha_ini ,fecha_fin ,hora_ini ,hora_fin ,gpslat_ini ,gpslon_ini ,gpslat_fin ,gpslon_fin ,fecha_sinc ,hora_sinc ,usuario ,sup_tel1 ,sup_tel2 ,sup_tel3 ,sup_pre1 ,sup_pre2 ,sup_pre3 ,estado,entregada,sincronizada ,cliente_t ,cliente_n ,maestro1 ,maestro2 ,maestro3 ,maestro4 ,maestro5 ,maestro6 ,maestro7 ,maestro8 ,maestro9 ,maestro10 ,maestro11 ,maestro12 ,mconfir1 ,mconfir2 ,mconfir3 ,mconfir4 ,mconfir5 ,mconfir6 ,mconfir7 ,mconfir8 ,mconfir9 ,mconfir10 ,mconfir11 ,mconfir12 ,observaciones ,fcr) SELECT encuesta,maquina ,fecha_ini ,fecha_fin ,hora_ini ,hora_fin ,gpslat_ini ,gpslon_ini ,gpslat_fin ,gpslon_fin ,fecha_sinc ,hora_sinc ,usuario ,sup_tel1 ,sup_tel2 ,sup_tel3 ,sup_pre1 ,sup_pre2 ,sup_pre3 ,estado,entregada,sincronizada ,cliente_t ,cliente_n ,maestro1 ,maestro2 ,maestro3 ,maestro4 ,maestro5 ,maestro6 ,maestro7 ,maestro8 ,maestro9 ,maestro10 ,maestro11 ,maestro12 ,mconfir1 ,mconfir2 ,mconfir3 ,mconfir4 ,mconfir5 ,mconfir6 ,mconfir7 ,mconfir8 ,mconfir9 ,mconfir10 ,mconfir11 ,mconfir12 ,observaciones ,fcr FROM t1 where encuesta=" + encuAct + "");
            } catch (Exception exq) {
                System.out.println("No Inserto T1");
            } finally {
                operaciones.queryNoData("DELETE FROM t1 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t2t ('encuesta', 'esp', 'rta', 'eval', 'fcr') SELECT encuesta, esp, rta, eval, fcr FROM t2 where encuesta='" + encuAct + "' order by esp asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T2");
            } finally {
                operaciones.queryNoData("DELETE FROM t2 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t3t ('encuesta','esp','cat','rta','eval','fcr') SELECT encuesta,esp,cat,rta,eval,fcr FROM t3 where encuesta='" + encuAct + "'order by esp,cat asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T3");
            } finally {
                operaciones.queryNoData("DELETE FROM t3 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t4t ('encuesta','esp','cat','est','rta','fcr') SELECT encuesta,esp,cat,est,rta,fcr FROM t4 where encuesta='" + encuAct + "' order by esp,cat,est asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T4");
            } finally {
                operaciones.queryNoData("DELETE FROM t4 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t5t ('encuesta','esp','cat','sku','rta','fcr') SELECT encuesta,esp,cat,sku,rta,fcr FROM t5 where encuesta='" + encuAct + "' order by esp,cat,sku asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T5");
            } finally {
                operaciones.queryNoData("DELETE FROM t5 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t6t ('encuesta','esp','cat','ncat','id','rta','cant','tipo','fcr') SELECT encuesta,esp,cat,ncat,id,rta,cant,tipo,fcr FROM t6 where encuesta='" + encuAct + "' order by id,esp,cat asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T6");
            } finally {
                operaciones.queryNoData("DELETE FROM t6 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t7t ('encuesta','cat','part','ep','part_t','part_n','cumple','fcr') SELECT encuesta,cat,part,ep,part_t,part_n,cumple,fcr FROM t7 where encuesta='" + encuAct + "' order by cat,ep asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T7");
            } finally {
                operaciones.queryNoData("DELETE FROM t7 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t8t ('encuesta','orden','idpreg','preg','optn','optt','rta','aplica','fcr','gr') SELECT encuesta,orden,idpreg,preg,optn,optt,rta,aplica,fcr,gr FROM t8 where encuesta='" + encuAct + "' order by orden asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T8");
            } finally {
                operaciones.queryNoData("DELETE FROM t8 where encuesta='" + encuAct + "'");
            }
            try {
                operaciones.queryNoData("INSERT INTO t9t ('encuesta','cat','sku','prec','fcr') SELECT encuesta,cat,sku,prec,fcr FROM t9 where encuesta='" + encuAct + "' order by cat,sku asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T9");
            } finally {
                operaciones.queryNoData("DELETE FROM t9 where encuesta='" + encuAct + "'");
            }

            try {
                operaciones.queryNoData("INSERT INTO t10t ('encuesta','cat','mar','sku','nsku','rta','mis','fcr','foto') SELECT encuesta,cat,mar,sku,nsku,rta,mis,fcr,foto FROM t10 where encuesta='" + encuAct + "' order by cat,sku asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T10");
            } finally {
                operaciones.queryNoData("DELETE FROM t10 where encuesta='" + encuAct + "'");
            }

            try {
                operaciones.queryNoData("INSERT INTO t11t ('encuesta','codb','ida','nact','rta','coin','fcr','foto') SELECT encuesta,codb,ida,nact,rta,coin,fcr,foto FROM t11 where encuesta='" + encuAct + "' order by ida asc");
            } catch (Exception exq) {
                System.out.println("No Inserto T11");
            } finally {
                operaciones.queryNoData("DELETE FROM t11 where encuesta='" + encuAct + "'");
            }

            try {
                operaciones.queryNoData("INSERT INTO tft ('encuesta','path','ref','esp','cat','preg','obs') SELECT encuesta,path,ref,esp,cat,preg,obs FROM tf where encuesta='" + encuAct + "'");
            } catch (Exception exq) {
                System.out.println("No Inserto TF");
            } finally {
                operaciones.queryNoData("DELETE FROM tf where encuesta='" + encuAct + "'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + ciudad + "' WHERE VA='CIU'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + canal + "' WHERE VA='CAN'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + datosAnt.get(8) + "' WHERE VA='SUBC'");
                operaciones.queryNoData("UPDATE ACT SET VAL='" + encuAct + "' WHERE VA='AUD'");
                ArrayList<String>[] idc = conGen.queryGeneral(getBaseContext(), "'201_CIU_CAN'", new String[]{"_ID"}, "CIU='" + ciudad + "' AND CAN='" + canal + "'");
                if (idc != null) {
                    String id = idc[0].get(0);
                    operaciones.queryNoData("UPDATE ACT SET VAL='" + id + "' WHERE VA='CIUCAN'");
                }
                Intent menu = new Intent(getBaseContext(), MenuOpciones.class);
                startActivity(menu);
            }
        }

    }

    public void mbusc(View v) {
        if (bc == 2) {
            bc = 1;
            buscadorNEW.setVisibility(View.VISIBLE);
            busC.setVisibility(View.VISIBLE);
            buscadorNEW.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            bc = 2;
            buscadorNEW.setVisibility(View.INVISIBLE);
            busC.setVisibility(View.INVISIBLE);
            buscadorNEW.setInputType(InputType.TYPE_NULL);
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

    public static DetalleCliente getInstance() {
        return activityA;
    }

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
        startActivity(new Intent(this,ListaClientes.class));
    }
}