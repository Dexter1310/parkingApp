package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Locale;
import java.util.Map;

public class Parking extends AppCompatActivity {
    private final   String CARPETA_RAIZ="Android/data/com.example.proba/files/Pictures/";
    private final   String RUTA_IMAGEN=CARPETA_RAIZ+"foto";
    final int COD_FOTO=20;
    Button btnVolver2, btn_camara;
    TextView textMat;
   private MagicalCamera cam;
   private MagicalPermissions magicalPermissions;
   private ImageView imgMostra;
    private final static int RESIZE_FHOTO=100;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        btnVolver2=(Button)findViewById(R.id.btnVolver2);
        textMat=(TextView)findViewById(R.id.textMat);
        imgMostra = (ImageView)this.findViewById(R.id.imgMostrar);
        btn_camara = (Button) this.findViewById(R.id.btn_camara);

        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        magicalPermissions = new MagicalPermissions(this, permissions);
        cam=new MagicalCamera(this,RESIZE_FHOTO,magicalPermissions);


        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cam.takePhoto();
//          cargarImagen();

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

//Drawable d = Drawable.createFromPath(RUTA_IMAGEN+"/foto.png" );
//imgMostra.setImageDrawable(d);


        File imgFile = new File(RUTA_IMAGEN+"/foto.jpg" );
        Log.e("RUTA",imgFile.toString());

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getName());
            imgMostra.setImageBitmap(myBitmap);

        }else{
            Toast.makeText(this, "No hay imagen guardada", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, CARPETA_RAIZ+RUTA_IMAGEN+"/foto.jpg", Toast.LENGTH_SHORT).show();
        }








    }


//
//
//    private void cargarImagen() {
//
//        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
//        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(Parking.this);
//        alertOpciones.setTitle("Seleccione una Opción");
//        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (opciones[i].equals("Tomar Foto")){
//                    shotPhoto();
//                }else{
//                    if (opciones[i].equals("Cargar Imagen")){
//                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        intent.setType("image/");
//                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
//                    }else{
//                        dialogInterface.dismiss();
//                    }
//                }
//            }
//        });
//        alertOpciones.show();
//
//    }





    private void shotPhoto(String path) {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }
        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }
        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File file=new File(path);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri=FileProvider.getUriForFile(this,authorities,file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(intent,COD_FOTO);

    }

//        @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//            if (resultCode==RESULT_OK) {
//
//                switch (requestCode) {
//                    case COD_SELECCIONA:
//                        Uri miPath = data.getData();
//                        imgMostra.setImageURI(miPath);
//                        break;
//
//                    case COD_FOTO:
//                        MediaScannerConnection.scanFile(this, new String[]{path}, null,
//                                new MediaScannerConnection.OnScanCompletedListener() {
//                                    @Override
//                                    public void onScanCompleted(String path, Uri uri) {
//                                        Log.i("Ruta de almacenamiento", "Path: " + path);
//                                    }
//                                });
//
//                        Bitmap bitmap = BitmapFactory.decodeFile(path);
//                        imgMostra.setImageBitmap(bitmap);
//
//                        break;
//                }
//
//
//            }
//    }
//
//
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
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception ignored) {

        }


        cam.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);
        String path = cam.savePhotoInMemoryDevice(cam.getPhoto(), "myTestPhotoName", MagicalCamera.PNG, true);

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode==RESULT_OK){
//            cam.resultPhoto(requestCode,resultCode,data);
//
////            Toast.makeText(this, requestCode, Toast.LENGTH_SHORT).show();
//
//            imgMostra.setImageBitmap(cam.getPhoto());
//            String path= cam.savePhotoInMemoryDevice(cam.getPhoto(),"photo","ff",MagicalCamera.JPEG,true);
//
//            if(path!=null){
//                Toast.makeText(this, "the photo is save", Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(this, "The photo dont write", Toast.LENGTH_SHORT).show();
//            }
//
//        }else{
//            Toast.makeText(this, "No realizada", Toast.LENGTH_SHORT).show();
//        }
//    }






}