package com.example.calendario;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SQLite extends SQLiteOpenHelper {
    public SQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE eventos (id integer primary key, titulo text, fecha text, descripcion text)");
        db.execSQL("CREATE TABLE listaCompra (id integer primary key, articulo text, cantidad integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void guardarProducto(String nomProd, int cantidad){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenidoListaCompra = new ContentValues();
        contenidoListaCompra.put("articulo",nomProd);
        contenidoListaCompra.put("cantidad",cantidad);
        db.insert("listaCompra",null,contenidoListaCompra);
    }

    public void borrarProductos(ArrayList<Integer> productos){
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i = productos.size()-1; i >= 0; i--){
            db.delete("listaCompra", "id = "+productos.get(i),null);
        }
    }

    public void borrarTodosProductos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("listaCompra", "",null);
    }

    public void guardarEvento(String titulo, String fecha, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenidoEventos = new ContentValues();
        contenidoEventos.put("titulo",titulo);
        contenidoEventos.put("fecha",fecha);
        contenidoEventos.put("descripcion",descripcion);
        db.insert("eventos",null,contenidoEventos);
    }

    public void modificarEvento(int id, String titulo, String fecha, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenidoEventos = new ContentValues();
        contenidoEventos.put("titulo",titulo);
        contenidoEventos.put("fecha",fecha);
        contenidoEventos.put("descripcion",descripcion);
        db.update("eventos",contenidoEventos,"id = "+id,null);
    }

    public void borrarEvento(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("eventos", "id = "+id,null);
    }

    public void borrarTodosEventos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("eventos", "",null);
    }
}
