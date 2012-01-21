package com.eldorado.myMusicPlayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.eldorado.myMusicPlayer.model.Music;
import com.eldorado.myMusicPlayer.model.MusicDAO;

public class EditPlaylist extends Activity{

	protected static final String PL = "PL";
	
	ArrayAdapter<String> mPlaylistsAdapter;
	ListView mPlaylistsView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlists);
		
		MusicDAO musicDAO = new MusicDAO(this);
		List<Music> musics = musicDAO.findAllMusics();
		musicDAO.close();
		List<String> playlistsNames = getPlaylists(musics);
		
		mPlaylistsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlistsNames);
		
		mPlaylistsView = (ListView) findViewById(R.id.playListSelection);
		mPlaylistsView.setAdapter(mPlaylistsAdapter);
		mPlaylistsView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra(PL, adapter.getItemAtPosition(position).toString());
				EditPlaylist.this.setResult(Activity.RESULT_OK, resultIntent);
				EditPlaylist.this.finish();
			}
		});
	}


	private List<String> getPlaylists(List<Music> musics) {
		List<String> playlistsNames = new ArrayList<String>();
		for(Music music : musics){
			if(!playlistsNames.contains(music.getPlayListName())){
				playlistsNames.add(music.getPlayListName());
			}
		}
		return playlistsNames;
	}
}
