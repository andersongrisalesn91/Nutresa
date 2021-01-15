package com.marketteam.desarrollo.nutresa;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;

public class Login extends AppCompatActivity {

    static final int MULT_PERMISOS = 4;
    private String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
    EditText userTV, paswTV;
    File carpetaIMG, sd;

    String URLws = "http://52.0.134.0:8080/MT_AppMovil//HTTPWrapperWS?wservice=1&param1=";
    String user = "";
    String pasw = "";

    String usero = "";
    String paswo = "";
    String data = "";
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    static Login activityA;

    boolean existe = false;
    FuncionesGenerales fg;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            ConsultaGeneral cons = new ConsultaGeneral();
            String queryUP = "SELECT va FROM PA WHERE pa=?";
            ArrayList<String>[] pant = cons.queryObjeto(getBaseContext(), queryUP, new String[]{"ultP"});
            if (pant != null) {
                String ultima = pant[0].get(0);
                if (!ultima.equals("Login")) {
                    //Llevar a su última pantalla
                    Ir(ultima);
                }
            } else {
                Toast.makeText(getBaseContext(), "Recuerde otorgar todos los permisos que aparecerán a continuación con el fin del correcto funcionamiento de la aplicación", Toast.LENGTH_LONG).show();
            }
            activityA = this;
        } catch (Exception c) {
            Log.i("Error ocreate:", c.toString());
        }
        finally {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
        //Validar si esta fue la última pantalla o si no enviar a la última pantalla


    }

    @Override
    protected void onStart() {
        super.onStart();
        userTV = (EditText) findViewById(R.id.userLogin);
        paswTV = (EditText) findViewById(R.id.passLogin);
        revisarPermisos();
        //Agregar la última pantalla
        try {
            OperacionesBDInterna operaciones;
            ConsultaGeneral cGeneral;
            cGeneral = new ConsultaGeneral();
            operaciones = new OperacionesBDInterna(getBaseContext());
            fg = new FuncionesGenerales(getBaseContext());
            String query = "SELECT va FROM PA WHERE pa=?";
            ArrayList<String>[] obj = cGeneral.queryObjeto(getBaseContext(), query, new String[]{"ultP"});
            if (obj == null) {
                //Insertar en la BD
                ContentValues cvI = new ContentValues();
                cvI.put("pa", "ultP");
                cvI.put("va", "Login");
                boolean insert = operaciones.insertar("PA", cvI);
                if (!insert) {
                    Toast.makeText(this, "Registro no insertado", Toast.LENGTH_SHORT).show();
                }
                //operaciones.queryNoData("INSERT INTO PA (pa,va) VALUES ('ultP', 'Login')");
            } else {
                //Update campo ultP
                fg.ultimaPantalla("Login");
            }

            try {
                carpetaIMG = new File(Environment.getExternalStorageDirectory(), "DATABASESMKT");
                if (!carpetaIMG.exists()) {
                    carpetaIMG.mkdirs();
                }
            } finally {
                try {
                    sd = new File(Environment.getExternalStorageDirectory() + "/DATABASESMKT/", fg.getCodProy());
                    if (!sd.exists()) {
                        sd.mkdirs();
                    }
                } catch (Exception e) {
                    Log.i("Error Creando Carpeta:", e.toString());
                }
            }
        }catch (Exception i){
            Log.i("problema en  inicio:", i.toString());
        }

    }

    @Override
    public void onBackPressed() {
    }

    public void Login(View vista) throws Exception {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("first", "error");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                location = loc;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
        //Validar caracters en el evento de presionar una tecla
        VerificarConex connex = new VerificarConex();
        boolean red = connex.revisarconexión(getBaseContext());

        user = userTV.getText().toString();
        usero = user;
        user = user.toUpperCase();

        pasw = paswTV.getText().toString();
        paswo = pasw;
        pasw = pasw.toUpperCase();
        ConsultaGeneral query = new ConsultaGeneral();
        if ((user == null) || (user.equals("")) || (pasw == null) || (pasw.equals(""))) {
            Toast.makeText(this, "Los campos de usuario y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
        } else {

            if (red) {

                new HttpRequestTask(
                        new HttpRequest(URLws + usero + "&param2=" + paswo + "&param5=si", HttpRequest.GET, null),
                        new HttpRequest.Handler() {
                            @Override
                            public void response(HttpResponse response) {
                                if (response.body != "" && response.body != null) {
                                    Log.d(this.getClass().toString(), "Request successful!");
                                    existe = true;
                                } else {
                                    Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                                    existe = false;
                                }
                                logeoOnline();
                            }
                        }).execute();

            } else {
                //Leo la BD local en la tabla params y miro si coincide
                String valUser = "SELECT va FROM PA WHERE pa= ?";
                String passUser = "SELECT va FROM PA WHERE pa= ?";
                ArrayList<String>[] obj = query.queryObjeto(getBaseContext(), valUser, new String[]{"user"});
                ArrayList<String>[] obj2 = query.queryObjeto(getBaseContext(), passUser, new String[]{"pass"});
                String userBD = obj[0].get(0).toUpperCase();
                String paswBD = obj2[0].get(0).toUpperCase();
                if (userBD.equals(user) && paswBD.equals(pasw)) {
                    try {
                        carpetaIMG = new File(Environment.getExternalStorageDirectory(), "DATABASESMKT");
                        if (!carpetaIMG.exists()) {
                            carpetaIMG.mkdirs();
                        }
                    } finally {
                        try {
                            sd = new File(Environment.getExternalStorageDirectory() + "/DATABASESMKT/", fg.getCodProy());
                            if (!sd.exists()) {
                                sd.mkdirs();
                            }
                        } catch (Exception e) {
                            Log.i("Error Creando Carpeta:", e.toString());
                        }
                    }
                    Intent intX = new Intent(this, SeleccionCuestionario.class);
                    startActivity(intX);
                } else {
                    Toast.makeText(this, "Datos incorrectos, realize Logeo online o corrija los datos", Toast.LENGTH_SHORT).show();
                    userTV.setText("");
                    paswTV.setText("");
                }
            }
        }
    }

    public void logeoOnline() {
        OperacionesBDInterna opers = new OperacionesBDInterna(getBaseContext());
        if (existe) {
            String TABLE_NAME = "PA";
            ContentValues mapu = new ContentValues();
            mapu.put("va", user);
            boolean oku = opers.actualizar(TABLE_NAME, mapu, "pa='user'", null);
            ContentValues mapp = new ContentValues();
            mapp.put("va", pasw);
            boolean okp = opers.actualizar(TABLE_NAME, mapp, "pa='pass'", null);
            if (oku && okp) {
                try {
                    carpetaIMG = new File(Environment.getExternalStorageDirectory(), "DATABASESMKT");
                    if (!carpetaIMG.exists()) {
                        carpetaIMG.mkdirs();
                    }
                } finally {
                    try {
                        sd = new File(Environment.getExternalStorageDirectory() + "/DATABASESMKT/", fg.getCodProy());
                        if (!sd.exists()) {
                            sd.mkdirs();
                        }
                    } catch (Exception e) {
                        Log.i("Error Creando Carpeta:", e.toString());
                    }
                }
                Toast.makeText(this, "Datos Almacenados Correctamente", Toast.LENGTH_SHORT).show();
                Intent intX = new Intent(this, SeleccionCuestionario.class);
                startActivity(intX);
            } else {
                Toast.makeText(this, "No es posible almacenar los Datos", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Este usuario no existe, ingrese los datos correctamente o verifique la existencia de su usuario", Toast.LENGTH_SHORT).show();
        }
    }

    public void revisarPermisos() {
        //Verifica si los permisos establecidos se encuentran concedidos
        if (ActivityCompat.checkSelfPermission(Login.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[2]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[3]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[4]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[5]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[6]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[7]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[8]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Login.this, permissions[9]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            ActivityCompat.requestPermissions(Login.this, permissions, MULT_PERMISOS);
        } else {
            //Si todos los permisos estan concedidos prosigue con el flujo normal
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULT_PERMISOS:
                boolean todos = true;
                for (int p = 0; p < grantResults.length; p++) {
                    if (grantResults[p] == PackageManager.PERMISSION_DENIED) {
                        todos = false;
                    }
                }
                break;
        }
    }

    public void Ir(String pantalla) {
        Intent p;
        switch (pantalla) {
            case "ListaClientes":
                p = new Intent(this, ListaClientes.class);
                startActivity(p);
                break;
            case "DetalleCliente":
                p = new Intent(this, DetalleCliente.class);
                startActivity(p);
                break;
            case "Menu":
                p = new Intent(this, MenuOpciones.class);
                startActivity(p);
                break;
            case "Espacios":
                p = new Intent(this, Espacios.class);
                startActivity(p);
                break;
            case "Categorías":
                p = new Intent(this, Categorias.class);
                startActivity(p);
                break;
            case "Estándares":
                p = new Intent(this, Estandares.class);
                startActivity(p);
                break;
            case "SKUs":
                p = new Intent(this, SKUs.class);
                startActivity(p);
                break;
            case "Generales":
                p = new Intent(this, Generales.class);
                startActivity(p);
                break;
            case "Activaciones":
                p = new Intent(this, Activaciones.class);
                startActivity(p);
                break;
            case "Activacionesrd":
                p = new Intent(this, Activacionesrd.class);
                startActivity(p);
                break;
            case "Activacionesrdsn":
                p = new Intent(this, Activacionesrdsn.class);
                startActivity(p);
                break;
            case "Activacionestb":
                p = new Intent(this, Activacionestb.class);
                startActivity(p);
                break;
            case "Activacionestbt":
                p = new Intent(this, Activacionestbt.class);
                startActivity(p);
                break;
            case "Partic":
                p = new Intent(this, Participaciones.class);
                startActivity(p);
                break;
            case "Partic2":
                p = new Intent(this, Participaciones2.class);
                startActivity(p);
                break;
            case "MenuI":
                p = new Intent(this, MenuInicio.class);
                startActivity(p);
                break;
            case "Activos":
                p = new Intent(this, Activos.class);
                startActivity(p);
                break;
            case "ConteoEspacios":
                p = new Intent(this, ConteoEspacios.class);
                startActivity(p);
                break;
            case "SKUCalidad":
                p = new Intent(this, SKUCalidad.class);
                startActivity(p);
                break;
            case "SelCuest":
                p = new Intent(this, SeleccionCuestionario.class);
                startActivity(p);
                break;
            case "Premios1":
                p = new Intent(this, Premios1.class);
                startActivity(p);
                break;
            case "Premios2":
                p = new Intent(this, Premios2.class);
                startActivity(p);
                break;
            case "Sincron":
                p = new Intent(this, Sincronizar.class);
                startActivity(p);
                break;
            case "Validaciones":
                p = new Intent(this, Validaciones.class);
                startActivity(p);
                break;
            case "TipoAMenu":
                p = new Intent(this, TipoAMenu.class);
                startActivity(p);
                break;
            case "TipoASMenu":
                p = new Intent(this, TipoASubMenu.class);
                startActivity(p);
                break;
        }
    }

    public void RestaurarDB(View v) {
        //Cuenta como hecha si tiene más de 5 caracteres
        //Guardar acá de una vez (Botón)
        operaciones = new OperacionesBDInterna(getBaseContext());
        conGen = new ConsultaGeneral();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popUp = inflater.inflate(R.layout.ver_contrasena, null);

        final String cont = "RESTAURAR";
        final EditText contras = (EditText) popUp.findViewById(R.id.textoPass);
        Button desc = (Button) popUp.findViewById(R.id.buttonObsvNo);
        Button guardar = (Button) popUp.findViewById(R.id.buttonObsvSi);
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contras.setText("");
                popupWindow.dismiss();
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = contras.getText().toString();
                if ((texto.equals("")) || (texto == null) || !(texto.equals(cont))) {
                    //Texto inválido
                    Toast.makeText(getBaseContext(), "verifique el texto ingresado", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        fg.BorrarBaseoriginal();
                    } catch (Exception ex) {
                        Log.i("No hay db para borrar: ", ex.toString());
                    }
                    try {
                        fg.originalDatabase();
                    } catch (Exception excep) {
                        Log.i("No se puede restaurar:", excep.toString());
                    } finally {
                        recargarapp();
                    }
                    popupWindow.dismiss();
                }

            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void recargarapp() {
        ConsultaGeneral cons = new ConsultaGeneral();
        String queryUP = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] pant = cons.queryObjeto(getBaseContext(), queryUP, new String[]{"ultP"});
        if (pant != null) {
            String ultima = pant[0].get(0);
            if (!ultima.equals("Login")) {
                //Llevar a su última pantalla
                Ir(ultima);
            }
        } else {
            Toast.makeText(getBaseContext(), "Recuerde otorgar todos los permisos que aparecerán a continuación con el fin del correcto funcionamiento de la aplicación", Toast.LENGTH_LONG).show();
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


    public static Login getInstance() {
        return activityA;
    }
}
