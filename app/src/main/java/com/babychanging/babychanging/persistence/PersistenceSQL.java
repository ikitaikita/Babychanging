package com.babychanging.babychanging.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.babychanging.babychanging.model.BChanging;


import java.util.ArrayList;

/**
 * Clase para la gestion de la base de datos 
 * @version 1.0
 * @author Victoria Marcos
 */
public class PersistenceSQL {

	private static final String DBNAME = "DBBCHANGING";
	
	
	private static int CURRENT_BBDD_VERSION = 1;




	/**
	 * Insert nre favourite dip into TABLE_FAVOURITE
	 * @param context
	 * @param d, dip to insert
	 */
	public static void insertDip(Context context, BChanging d) {

		
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(context,
				DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getWritableDatabase();

		// Si hemos abierto correctamente la base de datos
		if (db != null) {
			db.beginTransaction();
			try {
					ContentValues cv = new ContentValues();
					
				

					cv.put("id", d.getId());
					cv.put("nameplace", d.getNameplace());
					cv.put("latitude", d.getLatitude());
					cv.put("longitude", d.getLongitude());
					cv.put("urlpic", d.getUrlpic());
					cv.put("province", d.getProvince());
					cv.put("state", d.getState());
                    cv.put("address", d.getAddress());

					
					db.insertWithOnConflict("TABLE_FAV", null, cv, SQLiteDatabase.CONFLICT_REPLACE);

					db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			// Cerramos la base de datos
			db.close();
		}
	}

	public static void deleteDip(String id, Context context)
	{
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(context,
				DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getWritableDatabase();
		db.execSQL("delete from TABLE_FAV where id='"+id+"'");
		db.close();
	}


	/*
	 * get Dip list from TABLE_FAV which are the favourite dips
	 * @param context
	 * @return list
	 */
	public static ArrayList<BChanging> getFavourites(Context ctx) {
		// Creamos una lista de enteros
		
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(
				ctx, DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getReadableDatabase();
		ArrayList<BChanging> l_favourites = new ArrayList<BChanging>();

		// Selcccion de todas las Query
		Cursor cursor = db.rawQuery("select * from TABLE_FAV ", null);

		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(0);
				String nameplace = cursor.getString(1);
				String latitude = cursor.getString(2);
				String longitude = cursor.getString(3);
				String urlpic = cursor.getString(4);
				String province = cursor.getString(5);
				String state = cursor.getString(6);
                String address = cursor.getString(7);


				BChanging d = new BChanging();
				d.setId(id);
				d.setNameplace(nameplace);

				d.setLatitude(latitude);
				d.setLongitude(longitude);

				d.setProvince(province);
				d.setUrlpic(urlpic);
				d.setState(state);
                d.setAddress(address);
				l_favourites.add(d);
			} while (cursor.moveToNext());
		}
		db.close();

		return l_favourites;

	}

	public static boolean isFavourite(String id, Context context) {
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(
				context, DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getReadableDatabase();


		Cursor cursor = db.rawQuery("select * from TABLE_FAV where id="+ id, null);

		if (cursor != null) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				db.close();
				return true;
			} else {
				db.close();
				return false;
			}
		} else{
			db.close();
			return false;}

	}
	

	



}
