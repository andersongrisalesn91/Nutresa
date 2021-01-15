package com.marketteam.desarrollo.nutresa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Rechazar extends AppCompatActivity {

    EditText observaciones, nombre;
    Spinner spObsrv;
    ConsultaGeneral conGen;
    String[] razones, idRazones;
    String razon, idRazon;
    OperacionesBDInterna operaciones;
    ActualizarTablas at;
    LocationManager locationManager;
    Location location;
    FuncionesGenerales fg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechazar);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        conGen = new ConsultaGeneral();
        fg = new FuncionesGenerales(getBaseContext());
        operaciones = new OperacionesBDInterna(getBaseContext());
        at = new ActualizarTablas(getBaseContext());
        observaciones = (EditText) findViewById(R.id.etObservRecha);
        nombre = (EditText) findViewById(R.id.etNombreRecha);
        spObsrv = (Spinner) findViewById(R.id.spinnerRechazos);
        ArrayList<String>[] obj = conGen.queryGeneral(getBaseContext(),"'111_RRECHAZ'", new String[]{"RAZ", "NRAZ"}, null);
        if(obj != null){
            razones = new String[obj.length];
            idRazones = new String[obj.length];
            for(int r = 0; r < obj.length; r++){
                idRazones[r] = obj[r].get(0);
                razones[r] = obj[r].get(1);
            }
        }
        operaciones.close();
        razon = "";
        spObsrv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, razones));
        spObsrv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id){
                razon = razones[pos];
                idRazon = idRazones[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {    }
        });
    }

    public void Volver(View v){
        startActivity(new Intent(this, MenuOpciones.class));
    }

    public void Terminar(View v){
        //Verificar que los campos están llenos
        String observ = observaciones.getText().toString();
        String nomb = nombre.getText().toString();
        if((razon.equals("")) || ((observ.equals("")) || (observ == null) || (observ.length() < 10)) || ((nomb.equals("")) || (nomb == null))){
            Toast.makeText(this,"Campos incompletos", Toast.LENGTH_SHORT).show();
        } else {
            operaciones.queryNoData("UPDATE t1t SET observaciones='" + observ + "'");
            operaciones.queryNoData("UPDATE ACT SET VAL='" + idRazon + "' WHERE VA='IDR'");
            operaciones.queryNoData("UPDATE ACT SET VAL='" + razon + "' WHERE VA='NOMR'");
            operaciones.queryNoData("UPDATE ACT SET VAL='" + nomb + "' WHERE VA='PATEND'");
            operaciones.queryNoData("UPDATE ACT SET VAL='6' WHERE VA='FOTO'");
            startActivity(new Intent(this, TomarFoto.class));
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

    @Override
    protected void onPause() {
        super.onPause();
        operaciones.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        operaciones.close();
    }
}