package com.eldorado.myMusicPlayer.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDAO extends SQLiteOpenHelper{

	private static final String TABLE = "MUSIC";
	private static final String ID = "id";
	private static final String MUSIC_NAME = "musicname";
	private static final String PLAY_LIST_NAME = "playlistname";
	private static final String POSITION = "position";
	private static final String[] COLS = {ID, MUSIC_NAME, PLAY_LIST_NAME, POSITION};
	
	private static final int VERSION = 1;
	
	public MusicDAO(Context context) {
		super(context, TABLE, null, VERSION);
	}
	
	public MusicDAO(Context context, String name, CursorFactory factory, int version) {
		super(context, TABLE, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE + " (" + ID + " integer primary key AUTOINCREMENT , " + MUSIC_NAME + 
				" text not null, " + PLAY_LIST_NAME + " text not null, " + POSITION + " position integer not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}
	
	public void insert(Music music){
		ContentValues values = new ContentValues();
		values.put(MUSIC_NAME, music.getMusicName());
		values.put(PLAY_LIST_NAME, music.getPlayListName());
		values.put(POSITION, music.getPosition());
		getWritableDatabase().insert(TABLE, null, values);
	}
	
	public List<Music> findAllMusics(){
		List<Music> musics = new ArrayList<Music>();
		Cursor c = getWritableDatabase().query(TABLE, COLS, null, null, null, null, null);
		while(c.moveToNext()){
			getMusicsFromCursor(musics, c);
		}
		c.close();
		return musics;
	}
	
	public List<Music> findMusicsByPlaylist(String playListName){
		List<Music> musics = new ArrayList<Music>();
		Cursor c = getWritableDatabase().query(TABLE, COLS, PLAY_LIST_NAME + "=?", new String[]{playListName}, null, null, null);
		while(c.moveToNext()){
			getMusicsFromCursor(musics, c);
		}
		c.close();
		return musics;
	}

	private void getMusicsFromCursor(List<Music> musics, Cursor c) {
		Music music = new Music();
		music.setId(c.getLong(0));
		music.setMusicName(c.getString(1));
		music.setPlayListName(c.getString(2));
		music.setPosition(c.getInt(c.getInt(3)));
		musics.add(music);
	}
}
