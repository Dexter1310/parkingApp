package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class Parking extends AppCompatActivity {
    private final   String CARPETA_RAIZ="Android/data/com.example.proba/files/Pictures/";
    private final   String RUTA_IMAGEN=CARPETA_RAIZ+"foto";
    final int COD_FOTO=20;
    Button btnVolver2, btn_camara,btn_recordatori;
    TextView textMat;
    EditText text_recordatori;
    private MagicalCamera cam;
    private MagicalPermissions magicalPermissions;
    private ImageView imgMostra;
    private final static int RESIZE_FHOTO=100;
    SqlLitePar bd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        btnVolver2=(Button)findViewById(R.id.btnVolver2);
        textMat=(TextView)findViewById(R.id.textMat);
        imgMostra = (ImageView)this.findViewById(R.id.imgMostrar);
        btn_camara = (Button) this.findViewById(R.id.btn_camara);
        btn_recordatori = (Button)this.findViewById(R.id.btn_recordatori);
        text_recordatori = (EditText)this.findViewById(R.id.text_recordatori);text_recordatori.setVisibility(View.GONE);

        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

//        showRecord();
        magicalPermissions = new MagicalPermissions(this, permissions);
        cam=new MagicalCamera(this,RESIZE_FHOTO,magicalPermissions);
        loadImage();
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cam.takePhoto();
            }
        });


        //Todo :Recordatori EditText visible
        btn_recordatori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_recordatori.setVisibility(View.VISIBLE);

            }
        });
        text_recordatori.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                changeTextRecordatori();

            }
        });

        textMat.setText(getIntent().getExtras().getString("mat"));
        //Todo: go menu return=>
        btnVolver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parking = new Intent(Parking.this,MainActivity.class);
                parking.putExtra("mat",textMat.getText().toString());
                startActivity(parking);

            }
        });


    }
    private void changeTextRecordatori(){
        if(text_recordatori.getText().length()>0){
            btn_recordatori.setText("Guardar :)");
            btn_recordatori.setBackgroundColor(Color.parseColor("#4CAF50"));
            btn_recordatori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addRecord(textMat.getText().toString(),text_recordatori.getText().toString());
                }
            });
        }else{
            btn_recordatori.setBackgroundColor(Color.parseColor("#FF6200EE"));
            btn_recordatori.setText("Añadir recordatorio");
        }
    }

    private void loadImage(){
        File file = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN+"/foto.jpg");
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bmp == null) {
            imgMostra.setImageResource(R.drawable.parking_4); //Image default
        } else {
            imgMostra.setImageBitmap(bmp);
        }
    }
//todo: ADD RECORDATORIE
    public  void  addRecord(String mat,String record){
   SqlLitePar  bd =new SqlLitePar(this,"parking.db",null, 1);
     SQLiteDatabase base = bd.getWritableDatabase();
     if(!mat.isEmpty() && !record.isEmpty()){
         ContentValues add = new ContentValues();
         add.put("matricula",mat);
         add.put("recordatorio",record);
         base.insert("parking",null,add);
         base.close();
         text_recordatori.setText("");
         Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();

     }else {
         Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show();
     }

    }
    //todo: SHOW RECORDATORIE


        public  void  showRecord(){
      SqlLitePar bda = new SqlLitePar(this,"parking.bd",null,1);
      SQLiteDatabase bs =bda.getWritableDatabase();
      String matricula= textMat.getText().toString();
      if(!matricula.isEmpty()){
          Cursor f= bs.rawQuery("SELECT recordatorio from parking where matricula="+matricula,null);
          if(f.moveToFirst()){
              text_recordatori.setText(f.getString(1));
              bs.close();
          }
      }else {
          Toast.makeText(this, "no hay registros", Toast.LENGTH_SHORT).show();
      }

        }


//    }

//    private void options() {
//
//        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
//        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(Parking.this);
//        alertOpciones.setTitle("Seleccione una Opción");
//        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (opciones[i].equals("Tomar Foto")){
//                    cam.takePhoto();
//                }else{
//                    if (opciones[i].equals("Cargar Imagen")){
//                        loadImage();
//
//                    }else{
//                        dialogInterface.dismiss();
//                    }
//                }
//            }
//        });
//        alertOpciones.show();
//
//    }
//



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Map<String, Boolean> map = magicalPermissions.permissionResult(requestCode, permissions, grantResults);
        for (String permission : map.keySet()) {
            Log.d("PERMISSIONS", permission + " was: " + map.get(permission));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        btn_camara.setVisibility(View.VISIBLE);
        cam.resultPhoto(requestCode, resultCode, data);

        imgMostra.setImageBitmap(cam.getPhoto());
        imgMostra.setDrawingCacheEnabled(true);
        Bitmap bitmap = imgMostra.getDrawingCache();
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }
        if(isCreada==true){
            nombreImagen="foto.jpg";
        }
        File file = new File(fileImagen, nombreImagen);
        String s = file.getAbsolutePath();
        System.err.print("Path of saved image." + s);
        try {
            Toast.makeText(getApplicationContext(), "Captura guardada.", Toast.LENGTH_SHORT).show();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception ignored) {

        }

        cam.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);
        String path = cam.savePhotoInMemoryDevice(cam.getPhoto(), "myTestPhotoName", MagicalCamera.PNG, true);

    }



}