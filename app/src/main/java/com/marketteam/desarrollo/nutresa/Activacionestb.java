package com.marketteam.desarrollo.nutresa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Activacionestb extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idC,idPREG,PREG,fotoa;
    RecyclerView recyclerActivaciones;
    TextView pregunt;
    EditText valcampo;
    Button bfoto;
    String[] ActivacionesID, idTabla8, OpcionesID,OpcionesTID,OpcionesRtaID,OpcionesAplicaID;
    Integer Orden,TipoCar,Grupo;
    static Activacionestb activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activacionestb);
        activityA = this;
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
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        operaciones = new OperacionesBDInterna(getApplicationContext());
        conGen = new ConsultaGeneral();
        at = new ActualizarTablas(getBaseContext());
        fg = new FuncionesGenerales(getBaseContext());
        //fg.ultimaPantalla("Activacionestb");
        idC = fg.clienteActual();
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
            operaciones.queryNoData("UPDATE ACT SET VAL='3' WHERE VA='FTATID'");
            pregunt = (TextView) findViewById(R.id.tvPregunta);
            pregunt.setText(PREG);
            String queryActtb = "SELECT t8t.rta FROM t8t where idpreg = '" + idPREG + "'";
            ArrayList<String>[] Activacionestb = conGen.queryObjeto2val(getBaseContext(), queryActtb, null);
            valcampo = (EditText) findViewById(R.id.tbVal);
            String valortb = Activacionestb[0].get(0);
            if (!valortb.equals("0")) {
                valcampo.setText(valortb);
            }
            valcampo.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() == KeyEvent.ACTION_DOWN && ((keyCode > 6 && keyCode < 17) || (event.getKeyCode() == KeyEvent.KEYCODE_DEL))) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                            String XD = valcampo.getText().toString();

                            if (XD.length() > 1) {
                                String XDF = XD.substring(0, (XD.length())-1);
                                operaciones.queryNoData("UPDATE t8t SET rta='" + XDF + "' WHERE idpreg='" + idPREG + "'");
                                valcampo.setText(XDF);
                            } else {
                                operaciones.queryNoData("UPDATE t8t SET rta='' WHERE idpreg='" + idPREG + "'");
                                valcampo.setText("");
                            }
                            return true;
                        } else {
                            String XD = "";
                            if (valcampo.getText().length()==6){
                                XD = valcampo.getText().toString();
                            } else if (valcampo.getText().length()>6 || valcampo.getText().length()<6){
                                XD = valcampo.getText().toString() + (keyCode - 7);
                            }
                            operaciones.queryNoData("UPDATE t8t SET rta='" + XD + "' WHERE idpreg='" + idPREG + "'");
                            valcampo.setText(XD);
                            return true;
                        }
                    }
                    return false;
                }
            });
        } else {
            String queryActextx = "";
            if (TipoCar==1) {
                queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden>" + Orden + " and gr=" + Grupo + " and gr=" + Grupo;
            }else {
                queryActextx = "SELECT count(orden) as co,max(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden<" + Orden + " and gr=" + Grupo + " and gr=" + Grupo;
            }
            ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                String querymino = "SELECT tipo,r1,r2 FROM '302_PREGGEN' where orden=" + Integer.parseInt(actidexx[0].get(1));
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

    public void Volver(View v) {
        //Validar si todos los Activaciones están evaluados
        String rtax="";
        String queryminox = "SELECT ifnull(r1,0) as rx1 ,ifnull(r2,0) as rx2 FROM '302_PREGGEN' where orden=" + Orden ;
        ArrayList<String>[] minox = conGen.queryObjeto2val(getBaseContext(), queryminox, null);
        rtax = valcampo.getText().toString();
        if (rtax.equals("")) {
            rtax = "0";
        }
        if ((((Integer.parseInt(rtax) > Integer.parseInt(minox[0].get(0)) && Integer.parseInt(rtax) < Integer.parseInt(minox[0].get(1))) || Integer.parseInt(rtax)==99999) && ((fotoa.equals("1") && fg.getFototom(Orden).equals("1")) || fotoa.equals("0"))) || Integer.parseInt(rtax)==99999) {
            operaciones.queryNoData("UPDATE t8t SET rta='" + rtax + "' WHERE idpreg='" + idPREG + "'");
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='1' WHERE orden='" + Orden + "'");
        }else {
            operaciones.queryNoData("UPDATE t8t SET rta='" + rtax + "' WHERE idpreg='" + idPREG + "'");
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='0' WHERE orden='" + Orden + "'");
        }
        String queryActextx = "SELECT count(orden) as co,max(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden<" + Orden + " and gr=" + Grupo + " and gr=" + Grupo;
        ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);

        if (Integer.parseInt(actidexx[0].get(0))>0){
            Integer val = Integer.parseInt(actidexx[0].get(1));
            String queryActexty = "SELECT tipo FROM '302_PREGGEN' where  orden=" + val;
            ArrayList<String>[] actidexy = conGen.queryObjeto2val(getBaseContext(), queryActexty, null);
            operaciones.queryNoData("UPDATE ACT SET VAL='" + val + "' WHERE VA='ORDEN'");
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
        } else{
            Intent menu = new Intent(this, TipoASubMenu.class);
            startActivity(menu);
        }

    }

    public void Regresar(View v) {
        String rtax="";
        String queryminox = "SELECT ifnull(r1,0) as rx1 ,ifnull(r2,0) as rx2 FROM '302_PREGGEN' where orden=" + Orden ;
        ArrayList<String>[] minox = conGen.queryObjeto2val(getBaseContext(), queryminox, null);

            rtax = valcampo.getText().toString();
            if (rtax.equals("")) {
                rtax = "0";
            }
            if ((((Integer.parseInt(rtax) > Integer.parseInt(minox[0].get(0)) && Integer.parseInt(rtax) < Integer.parseInt(minox[0].get(1))) || Integer.parseInt(rtax) == 99999) && ((fotoa.equals("1") && fg.getFototom(Orden).equals("1")) || fotoa.equals("0"))) || Integer.parseInt(rtax) == 99999) {
                operaciones.queryNoData("UPDATE t8t SET rta='" + rtax + "' WHERE idpreg='" + idPREG + "'");
                operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='1' WHERE orden='" + Orden + "'");
                Intent menu = new Intent(this, TipoASubMenu.class);
                startActivity(menu);
            } else {
                operaciones.queryNoData("UPDATE t8t SET rta='" + rtax + "' WHERE idpreg='" + idPREG + "'");
                operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='0' WHERE orden='" + Orden + "'");
                Intent menu = new Intent(this, TipoASubMenu.class);
                startActivity(menu);
            }
    }

    public void Fototb(View v) {
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}
        //Validar si todos los Activaciones están evaluados
        String rtax = valcampo.getText().toString();
        if (rtax.equals("")) {
            rtax = "0";
        }
        operaciones.queryNoData("UPDATE t8t SET rta='" + rtax + "' WHERE idpreg='" + idPREG + "'");
        operaciones.queryNoData("UPDATE ACT SET VAL='11' WHERE VA='FOTO'");
        operaciones.queryNoData("UPDATE ACT SET VAL='0' WHERE VA='FTOM'");
        startActivity(new Intent(this, TomarFoto.class));
    }

    public void Siguientetb(View v) {
        String rtax="";
        String queryminox = "SELECT ifnull(r1,0) as rx1 ,ifnull(r2,0) as rx2 FROM '302_PREGGEN' where orden=" + Orden ;
        ArrayList<String>[] minox = conGen.queryObjeto2val(getBaseContext(), queryminox, null);
        rtax = valcampo.getText().toString();
        if (rtax.equals("")) {
            rtax = "0";
        }
        String Tipo="";
        if ((((Integer.parseInt(rtax) > Integer.parseInt(minox[0].get(0)) && Integer.parseInt(rtax) < Integer.parseInt(minox[0].get(1))) || Integer.parseInt(rtax)==99999) && ((fotoa.equals("1") && fg.getFototom(Orden).equals("1")) || fotoa.equals("0"))) || Integer.parseInt(rtax)==99999) {
            operaciones.queryNoData("UPDATE t8t SET rta='" + rtax + "' WHERE idpreg='" + idPREG + "'");
            operaciones.queryNoData("UPDATE '302_PREGGEN' SET eval='1' WHERE orden=" + Orden );
            String queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1 and orden>" + Orden + " and gr=" + Grupo ;
            ArrayList<String>[] actidexx = conGen.queryObjeto2val(getBaseContext(), queryActextx, null);
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                String querymino = "SELECT tipo,r1,r2 FROM '302_PREGGEN' where orden=" + Integer.parseInt(actidexx[0].get(1));
                ArrayList<String>[] mino = conGen.queryObjeto2val(getBaseContext(), querymino, null);
                Tipo = mino[0].get(0);
                operaciones.queryNoData("UPDATE ACT SET VAL='" + Integer.parseInt(actidexx[0].get(1)) + "' WHERE VA='ORDEN'");
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

    public static Activacionestb getInstance() {
        return activityA;
    }
}
