package com.eldorado.myMusicPlayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eldorado.myMusicPlayer.model.Music;
import com.eldorado.myMusicPlayer.model.MusicDAO;

public class CreatePlaylist extends Activity {

	private static final String MEDIA_PATH = "/sdcard/";
	
	private ArrayAdapter<String> mListAdapterAllSongs;
	private List<String> mSelectedSongs;
	private EditText mPlaylistNameEdit;

	//Validar campos em branco ou nulos(nome da playlist por exemplo)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);
		
		adaptToListView(getAllSongsNamesFromSDCard());
		
		mPlaylistNameEdit = (EditText) findViewById(R.id.playlist_name_edit);
		Button savePlayList = (Button) findViewById(R.id.save_playlist_button);
		savePlayList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Integer position = 0;
				for (String musicName : mSelectedSongs){
					Music music = new Music(musicName, mPlaylistNameEdit.getText().toString(), position);
					MusicDAO musicDAO = new MusicDAO(CreatePlaylist.this);
					musicDAO.insert(music);
					musicDAO.close();	
					position++;
				}
				Toast.makeText(CreatePlaylist.this, "Playlist saved!", Toast.LENGTH_SHORT).show();
				CreatePlaylist.this.finish();
			}
		});
	}

	public void adaptToListView(List<String> songList){
		mSelectedSongs = new ArrayList<String>();
		mListAdapterAllSongs = new ArrayAdapter<String>(this,	android.R.layout.simple_list_item_multiple_choice, songList);

		ListView listAllSongs = (ListView) findViewById(R.id.musicListSelection);
		listAllSongs.setClickable(true);
		listAllSongs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listAllSongs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				mSelectedSongs.add(adapter.getItemAtPosition(position).toString());
				Toast.makeText(CreatePlaylist.this, "Song added to the playlist!", Toast.LENGTH_SHORT).show();
			}
		});

		listAllSongs.setAdapter(mListAdapterAllSongs);
	}

	// returns a list of music names
	public List<String> getAllSongsNamesFromSDCard() {
		List<String> allSongsFromSD = new ArrayList<String>();
		File home = new File(MEDIA_PATH);
		if (home.listFiles(new Mp3Filter()).length > 0) {
			for (File file : home.listFiles(new Mp3Filter())) {
				allSongsFromSD.add(file.getName());
			}
		}
		return allSongsFromSD;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	// returns a list of music names that ands with .mp3
	class Mp3Filter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3"));
		}
	}
}
