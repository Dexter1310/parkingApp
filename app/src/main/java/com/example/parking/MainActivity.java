package com.example.parking;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    ImageView aparca;
    MapView mapa;
    ListView listaAparca;ArrayList<String>listaAparcamiento;
    ArrayAdapter lisA;
    GoogleMap googleMap;
    String address,marca;
    TextView in,textDesti;
    Button salir,btn2,btn3,btn4,btn5,btnAction,btnClean;
    EditText matricula;
    List<Address> direccion;
    RequestQueue requestQueue;

    private final int respuest=0;
    int cierto;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        aparca=(ImageView)findViewById(R.id.ImgAparca);aparca.setVisibility(View.GONE);
        in=(TextView)findViewById(R.id.informa);in.setVisibility(View.GONE);
        textDesti=(TextView)findViewById(R.id.textDestinity);textDesti.setVisibility(View.GONE);
        salir=(Button)findViewById(R.id.btnSalir);
        btn2=(Button)findViewById(R.id.btn2);btn2.setText("ayuda");
        btn3=(Button)findViewById(R.id.btn3);
        btn5=(Button)findViewById(R.id.btn5);btn5.setVisibility(View.GONE);
        btn4=(Button)findViewById(R.id.btnOcultHistori);btn4.setVisibility(View.GONE);
        btnAction=(Button)findViewById(R.id.btnAction);btnAction.setVisibility(View.GONE);
        btnClean=(Button)findViewById(R.id.btnClean);btnClean.setVisibility(View.GONE);
        matricula=(EditText)findViewById(R.id.matricula);
        matricula.setFilters(new InputFilter[] {new InputFilter.AllCaps()});//Mayusculas
        marca=getIntent().getStringExtra("direccion");
        listaAparcamiento=new ArrayList<>();
        listaAparca=(ListView)findViewById(R.id.listAparca);listaAparca.setVisibility(View.GONE);
        listaAparca.setOnTouchListener(new View.OnTouchListener() {//hacer Scroll dentro del Listview
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        if(marca!=null){
            matricula.setVisibility(View.GONE);in.setVisibility(View.VISIBLE);
            in.setText("ÚLTIMO APARCAMIENTO  DE "+getIntent().getStringExtra("matricula")+"\n\n"+marca);
            btn2.setText("Nuevo aparcamiento");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matricula.setVisibility(View.VISIBLE);in.setVisibility(View.GONE);
                    matricula.setText(getIntent().getStringExtra("matricula"));
                }
            });
        }

//Todo:Select an item from a saved address
        listaAparca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String addressSelect=listaAparcamiento.get(position);
                textDesti.setVisibility(View.VISIBLE);textDesti.setText("Aparcamiento seleccionado\n "+addressSelect);
                btnClean.setVisibility(View.GONE);
                btnAction.setVisibility(View.VISIBLE);btnAction.setText("Ir al sitio");btnAction.setBackgroundColor(Color.parseColor("#4CAF50"));

            }
        });
//Todo:Delete an item from a saved address
        listaAparca.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String addressDelete=listaAparcamiento.get(position);
                textDesti.setVisibility(View.VISIBLE);textDesti.setText("Eliminar aparcamiento\n "+addressDelete);
                btnAction.setVisibility(View.VISIBLE);btnAction.setText("Elimina aparcamiento");btnAction.setBackgroundColor(Color.parseColor("#FF0000"));
                btnClean.setVisibility(View.VISIBLE);btnClean.setText("Limpiar Historial");
                btnClean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(MainActivity.this, matricula.getText().toString(), Toast.LENGTH_SHORT).show();
//                        deleteList("https://transpilas.000webhostapp.com/appAparca/delete.php");
                    }
                });
                return true;
            }
        });


        matricula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(matricula.getText().length() > 6 && matricula.getText().length()<8 ) {
                    btn2.setVisibility(View.VISIBLE);btn3.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//oculta teclado
                    imm.hideSoftInputFromWindow(matricula.getWindowToken(), 0);
                    in.setVisibility(View.VISIBLE);in.setText("¿Es correcta la matrícula de su vehículo?\n Puede añadir otro vehículo si lo desea.");
                    btn2.setText("SI");btn3.setText("NO");
                    //TODO:NO:
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            in.setText("Modifique su matrícula");
                            matricula.setText("");
                        }
                    });
                    //TODO:SI:
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //consulta si existe la matricula en json si no hacer el insert:
                            consulta("https://transpilas.000webhostapp.com/appAparca/vehiculo.json");

                            aparca.setVisibility(View.VISIBLE);
                            btn3.setText("AYUDA");btn2.setText("historial");
                            in.setText(matricula.getText().toString() + " Pulse P al aparcar.");
                            btn5.setVisibility(View.VISIBLE);btn5.setText("Nueva matrícula");
                            btn5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            });
                            matricula.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//oculta teclado
                            imm.hideSoftInputFromWindow(matricula.getWindowToken(), 0);
                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listaAparca.setVisibility(View.VISIBLE); btn4.setVisibility(View.VISIBLE);btn2.setVisibility(View.GONE);
                                    historialAparcamiento("https://transpilas.000webhostapp.com/appAparca/vehiculo.json");
                                }
                            });
                        }
                    });


                }else if(matricula.getText().length()==0||matricula.getText().length()<7){// si esta vacia
                    btn2.setVisibility(View.GONE);btn3.setVisibility(View.GONE);
                }
                if(matricula.getText().length()>=8){
                    Toast.makeText(getApplicationContext(),"Matrícula demasiada larga.", Toast.LENGTH_SHORT).show();
                    btn2.setVisibility(View.GONE);btn3.setVisibility(View.GONE);

                }
            }
        });
        //Todo: boton de ocultar el listView de historial de aparcamientos
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn4.setVisibility(View.GONE);btn2.setVisibility(View.VISIBLE);
                listaAparca.setVisibility(View.GONE);lisA.clear();
            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);//inicializamos LocationServices
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},respuest);
        }else{
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess( Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
//                                diEntra="latitud: "+location.getLatitude() +"longitud: "+location.getLongitude();
//                                diSale="latitud: "+location.getLatitude() +"longitud: "+location.getLongitude();
                        Geocoder geo;

                        geo = new Geocoder(getApplicationContext());
                        try {
                            direccion=  geo.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            address = direccion.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = direccion.get(0).getLocality();
                            String state = direccion.get(0).getAdminArea();
                            String country = direccion.get(0).getCountryName();
                            String postalCode = direccion.get(0).getPostalCode();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        in.setText("No ha sido posible la ubicación");
                    }
                }
            });
        }
        aparca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"Ubicación guardada latitud:"+direccion.get(0).getLatitude()+" longitud:"+
                        direccion.get(0).getLongitude(), Toast.LENGTH_SHORT).show();
                Intent mapa=new Intent(MainActivity.this,map.class);
                Intent longitud = mapa.putExtra("longitud", direccion.get(0).getLongitude());
                mapa.putExtra("latitud", direccion.get(0).getLatitude());
                mapa.putExtra("matricula",matricula.getText().toString());
                mapa.putExtra("direc", address);
                //Todo: añadir en la tabla ubicación el destino nuevo con esa matricula.
                insertUbicacion("https://transpilas.000webhostapp.com/appAparca/insertUbicacion.php");
                startActivity(mapa);
            }
        });

        //TODO:salir de la app
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salir=new Intent(Intent.ACTION_MAIN);
                salir.addCategory(Intent.CATEGORY_HOME);
                salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(salir);
            }
        });
    }

    //Todo:inserta matricula
    public void insertMatricula(String URL){
        StringRequest registroTurno=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Matrícula: "+matricula.getText().toString()+ " guardada.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se ha registrado la matrícula "+ matricula.getText().toString()+"REVISA GPS EN SU MOVIL", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("matricula",matricula.getText().toString());
                para.put("ubicacion",address);
                return para;
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(registroTurno);
    }

    //Todo:inserta ubicacion
    public void insertUbicacion(String URL){
        StringRequest registroTurno=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Ubicacion guardada.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se ha registrado la ubicacion "+ matricula.getText().toString()+"REVISA GPS EN SU MOVIL", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("matricula",matricula.getText().toString());
                para.put("ubicacion",address);
                return para;
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(registroTurno);
    }
    //Todo:Delete list parking
    public void deleteList(String URL){
        StringRequest delete=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listaAparca.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Historial Eliminado", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se ha eliminado  la solicitud", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("matricula",matricula.getText().toString());
//                para.put("ubicacion",addAddres);
                return para;
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(delete);
    }
    //TODO:Comprobar matricula:
    private   void consulta(String URL){
        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String matric= jsonObject.getString("matricula");

                        if(matricula.getText().toString().equals(matric)){
                            cierto=1;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                if(cierto!=1){
                    btn2.setVisibility(View.GONE);//oculta historial por que es nueva matricula
                    insertMatricula("https://transpilas.000webhostapp.com/appAparca/registro.php");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                insertMatricula("https://transpilas.000webhostapp.com/appAparca/registro.php");
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    //TODO:Historial de aparcamiento:
    private   void historialAparcamiento(String URL){
        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;listaAparca.setVisibility(View.VISIBLE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String direc= jsonObject.getString("direccion");
                        String matric= jsonObject.getString("matricula");
                        if(matricula.getText().toString().equals(matric)){
                            listaAparcamiento.add(direc);//ArrayLisT
                            lisA = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, listaAparcamiento);
                            listaAparca.setAdapter(lisA);//ListView
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
