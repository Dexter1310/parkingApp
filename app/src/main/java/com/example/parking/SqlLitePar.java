package com.example.parking;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SqlLitePar extends SQLiteOpenHelper {
    public   final String NAME_BD = "parking";
    public   final  int VERSION_BD = 1;

    private  static  final  String TABLE_PARKING="CREATE TABLE parking (matricula text primary key, recordatorio text)";

    public SqlLitePar(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_PARKING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PARKING);
        sqLiteDatabase.execSQL(TABLE_PARKING);
    }

//    public  void  showRecord(){
//        SQLiteDatabase bd = getWritableDatabase();
//
//        if(bd!=null){
//        Cursor c =  bd.rawQuery("SELECT matricula,recordatorio FROM parking",null);
//            if (c != null) {
//                c.moveToFirst();
//                do {
//                    String matricula = c.getString(c.getColumnIndex("matricula"));
//                    String recordatorie = c.getString(c.getColumnIndex("recordatorio"));
//                } while (c.moveToNext());
//
//            }
//            c.close();
//            bd.close();
//
//        }


//    }
}
