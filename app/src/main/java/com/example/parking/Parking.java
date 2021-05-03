package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.android.gms.tasks.OnCompleteListener;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Parking extends AppCompatActivity {
    private final   String CARPETA_RAIZ="Android/data/com.example.proba/files/Pictures/";
    private final   String RUTA_IMAGEN=CARPETA_RAIZ+"foto";
    final int COD_FOTO=20;
    Button btnVolver2, btn_camara,btn_recordatori;
    TextView textMat,textAudio;
    EditText text_recordatori;
    private MagicalCamera cam;
    private MagicalPermissions magicalPermissions;
    private ImageView imgMostra,imgRec,imgPlay;
    private final static int RESIZE_FHOTO=100;
    private MediaRecorder grabacion;
    private String fileSound = null;
//    timer:
    CountDownTimer countDownTimer;
    CountDownTimer countReproduct;
    long maxCounter = 10000000;
    long diff = 1000;
    int minut= 0;
    String time= "";
    boolean pause= false;

    SqlLitePar bd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        btnVolver2=(Button)findViewById(R.id.btnVolver2);
        textAudio=(TextView)findViewById(R.id.textView2);
        textMat=(TextView)findViewById(R.id.textMat);
        imgMostra = (ImageView)this.findViewById(R.id.imgMostrar);
        imgRec = (ImageView)this.findViewById(R.id.rec);
        imgPlay = (ImageView)this.findViewById(R.id.play);
        btn_camara = (Button) this.findViewById(R.id.btn_camara);
        btn_recordatori = (Button)this.findViewById(R.id.btn_recordatori);
        text_recordatori = (EditText)this.findViewById(R.id.text_recordatori);text_recordatori.setVisibility(View.GONE);

        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO
        };
        textMat.setText(getIntent().getExtras().getString("mat"));
        showRecord();
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


        //Todo: go menu return=>
        btnVolver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parking = new Intent(Parking.this,MainActivity.class);
                parking.putExtra("mat",textMat.getText().toString());
                startActivity(parking);

            }
        });

        //Todo: clean Recordatori
        if(btn_recordatori.getText().toString() == "Nuevo recordatorio"){
            btn_recordatori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text_recordatori.setText("");

                }
            });
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat .requestPermissions(Parking.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }

        imgRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileSound=getExternalFilesDir("/").getAbsolutePath()+textMat.getText().toString()+".mp3";
                recorder(view);
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproducir(view);
            }
        });





    }
    private void changeTextRecordatori(){
        if(text_recordatori.getText().length()>0){
            btn_recordatori.setText("Guardar Recordatorio:)");
            btn_recordatori.setBackgroundColor(Color.parseColor("#FFC300"));
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
        File file = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN+"/"+textMat.getText().toString()+".jpg");
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

         //Todo: si existe el registro:
         String matricula= textMat.getText().toString();
         String[] args = new String[] {matricula};
         Cursor f= base.rawQuery(" SELECT * from parking where matricula=? " ,args);
         if(f.moveToLast()){//Update if exists row
             ContentValues add = new ContentValues();
             add.put("matricula",mat);
             add.put("recordatorio",record);
             base.update("parking", add, "recordatorio=?", new String[]{f.getString(1)});
             base.close();
         }else{//insert
             ContentValues add = new ContentValues();
             add.put("matricula",mat);
             add.put("recordatorio",record);
             base.insert("parking",null,add);
             base.close();
         }


         text_recordatori.setText(record);
         Toast.makeText(this, "GUARDADO:\n\n "+record, Toast.LENGTH_SHORT).show();

     }else {
         Toast.makeText(this, "Escriba recordatorio", Toast.LENGTH_SHORT).show();

     }

    }
    //todo: SHOW RECORDATORIE

        public  void  showRecord(){
      SqlLitePar bda = new SqlLitePar(this,"parking.db",null,1);
      SQLiteDatabase bs =bda.getWritableDatabase();
      String matricula= textMat.getText().toString();

      if(!matricula.isEmpty()){
          String[] args = new String[] {matricula};
          Cursor f= bs.rawQuery(" SELECT * from parking where matricula=? " ,args);
          if(f.moveToLast()){
             text_recordatori.setText(f.getString(1));
              bs.close();
              text_recordatori.setVisibility(View.VISIBLE);
              btn_recordatori.setText("Nuevo recordatorio");

          }

      }else {
          text_recordatori.setVisibility(View.GONE);
          Toast.makeText(this, "no hay registros", Toast.LENGTH_SHORT).show();
      }


        }





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
            nombreImagen=textMat.getText().toString()+".jpg";
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
    public void recorder(View view){
        if(grabacion == null){
            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            grabacion.setOutputFile(fileSound);
            try{
                grabacion.prepare();
                grabacion.start();

            } catch (IOException e){
                e.printStackTrace();
            }
            imgRec.setImageResource(R.drawable.rec);

            countDownTimer =  new CountDownTimer(maxCounter , diff ) {
                public void onTick(long millisUntilFinished) {
                    long diff = maxCounter - millisUntilFinished;
                    long second= diff  / 1000;
                    if(second>59){
                        minut++;
                        countDownTimer.onFinish();
                        countDownTimer.start();
                    }
                    time=String.format("%02d", minut)+":" +String.format("%02d", second);
                    textAudio.setText( "Grabando ... "+time);
                }
                public void onFinish() {
                    textAudio.setText("stop "+ time);
                    countDownTimer.cancel();
                }

            }.start();

        } else if(grabacion != null){
            grabacion.stop();
            grabacion.release();
            grabacion = null;
            imgRec.setImageResource(R.drawable.stop_rec);
            countDownTimer.onFinish();
        }
    }

    public void reproducir(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            if(fileSound==null){
                Toast.makeText(this, "No hay ninguna grabación.", Toast.LENGTH_SHORT).show();
            }else{
                mediaPlayer.setDataSource(fileSound);
                    mediaPlayer.prepare();
                countReproduct =  new CountDownTimer(maxCounter , diff ) {
                    public void onTick(long millisUntilFinished) {
                        long diff = maxCounter - millisUntilFinished;
                        long second= diff  / 1000;
                        if(second>59){
                            minut++;
                            countReproduct.onFinish();
                            countReproduct.start();
                        }
                        time=String.format("%02d", minut)+":" +String.format("%02d", second);
                        textAudio.setText( "Reproduciendo ... "+time);
                    }
                    public void onFinish() {
                        textAudio.setText(time);
                        countReproduct.cancel();
                    }
                }.start();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//detected finish audio
                public void onCompletion(MediaPlayer mp) {
                    countReproduct.onFinish();
                }
            });

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        countReproduct.onFinish();
                    } else {
                        mediaPlayer.start();
                        countReproduct.start();
                    }
                }
            });

        } catch (IOException e){
            e.printStackTrace();
        }

        mediaPlayer.start();
    }



}