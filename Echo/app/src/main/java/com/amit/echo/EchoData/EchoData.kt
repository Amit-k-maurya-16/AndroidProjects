package com.amit.echo.EchoData

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.amit.echo.Songs

class EchoData: SQLiteOpenHelper{

    var _songList = ArrayList<Songs>()



    object Staticated{
        var DB_VERSION =1
        val DB_NAME = "Favourite"
        val TABLE_NAME = "FavouriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"

    }
    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
           sqliteDatabase?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING,"
                            + Staticated.COLUMN_SONG_PATH + " STRING);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    fun storeAsFavourite(id: Int?, artist:String? , songTitle: String?, path: String? ){
        val db = this.writableDatabase
          var contentValue = ContentValues()
        contentValue.put(Staticated.COLUMN_ID, id)
        contentValue.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValue.put(Staticated.COLUMN_SONG_PATH, songTitle )
        contentValue.put(Staticated.COLUMN_SONG_PATH, path)
        db.insert(Staticated.TABLE_NAME, null, contentValue)
        db.close()
    }

    fun queryDBList(): ArrayList<Songs>?{

        try {
            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params, null)
            if(cSor.moveToFirst()){
              do{
                var _id = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                var _title = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                var _SongPath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                _songList.add(Songs(_id.toLong(),_title, _artist,_SongPath, 0))
                }while(cSor.moveToNext())
            }else{
            return null
              }
            }catch (e:Exception){
                e.printStackTrace()
        }

        return _songList
    }

    fun checkIfIdExists( _id : Int): Boolean{
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongId = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()){
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            }while (cSor.moveToNext())
        } else{
            return false
        }

        return storeId != -1090
    }

    fun deleteFavourite(_id: Int){
        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        var query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()){
            do {
                counter = counter + 1
            } while (cSor.moveToNext())
        }else{
            return 0
        }
        return counter
    }

}