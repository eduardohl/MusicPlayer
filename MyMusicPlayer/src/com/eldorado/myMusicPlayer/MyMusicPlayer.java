package com.eldorado.myMusicPlayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.eldorado.myMusicPlayer.model.Music;
import com.eldorado.myMusicPlayer.model.MusicDAO;

public class MyMusicPlayer extends Activity implements MediaPlayer.OnCompletionListener  {

	private static final String PL = "PL";
	public static final String LOG_TAG = "MYMEDIAPLAYER";
	private static final String MEDIA_PATH = new String("/sdcard/");
	
	private static final int PLAY_LIST = 0;

	/** Called when the activity is first created. */
	private Integer mCurrentlyPlayingPosition;
	private SeekBar mSeekBar;
	private Button mPlaypause;
	private MediaPlayer mMediaPlayer;
	private List<Music> mSongList = new ArrayList<Music>();
	private ArrayAdapter<Music> mListAdapterAllSongs; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		mSeekBar = (SeekBar) findViewById(R.id.seekBar1);

		mPlaypause = (Button) findViewById(R.id.ButtonPlayPause);
		Button stop = (Button) findViewById(R.id.ButtonStop);

		mPlaypause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playPause();
			}
		});

		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop();
			}
		});

		mSeekBar.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mMediaPlayer.isPlaying()){
					mMediaPlayer.seekTo(mSeekBar.getProgress());
				}
				return false;
			}
		});
	}

	public void playPause() {
		if(mMediaPlayer == null){
			mCurrentlyPlayingPosition = 0;
			playListItem();
		}
		if (mPlaypause.getText().equals("Play")) {
			mMediaPlayer.start();
			mPlaypause.setText("Pause");
		} else {
			mMediaPlayer.pause();
			mPlaypause.setText("Play");
		}
	}

	public void stop() {
		if( mMediaPlayer != null){
			mMediaPlayer.stop();
			try {
				mMediaPlayer.prepare();
				mMediaPlayer.seekTo(0);
				mPlaypause.setText("Play");
			} catch (Exception e) {
				playStopException();
			}
		}
	}

	
	private void loadPlaylist(String playListName) {
		MusicDAO musicDAO = new MusicDAO(this);
		mSongList = musicDAO.findMusicsByPlaylist(playListName);
		musicDAO.close();
		adaptToListView();
	}
	
	public void adaptToListView(){
		mListAdapterAllSongs = new ArrayAdapter<Music>(this,	android.R.layout.simple_list_item_1, mSongList);

		ListView listAllSongs = (ListView) findViewById(R.id.listViewPlaylist);
		listAllSongs.setClickable(true);
		// listAllSongs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listAllSongs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				stop();
				mCurrentlyPlayingPosition = position;
				playListItem();
				try {
					playPause();
				} catch (Exception e) {
					playStopException();
				}
			}
		});

		listAllSongs.setAdapter(mListAdapterAllSongs);
	}


	// this is event handler for Create Playlist button
	public void openCreatePlaylist(View view) {
		Intent intent = new Intent("com.eldorado.myMusicPlayer.OPEMCREATEPLAYLIST");
		startActivity(intent);
	}

	// this is event handler for Edit Playlist button
	public void openEditPlaylist(View view) {
		Intent intent = new Intent("com.eldorado.myMusicPlayer.OPEMEDITPLAYLIST");
		startActivityForResult(intent, PLAY_LIST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PLAY_LIST && resultCode == RESULT_OK){ 
			loadPlaylist(data.getStringExtra(PL).toString());
		}
	}

	private void playListItem() {
		if(mSongList.size() != 0){
			Music music = mListAdapterAllSongs.getItem(mCurrentlyPlayingPosition);
			mMediaPlayer=MediaPlayer.create(MyMusicPlayer.this, Uri.parse("file:///" + MEDIA_PATH + "/" + music.getMusicName()));
			mMediaPlayer.setOnCompletionListener(MyMusicPlayer.this);
			mMediaPlayer.start();

			new SeekBarUpdater().execute();
			mSeekBar.setMax(mMediaPlayer.getDuration());
		} else {
			Toast.makeText(this, "No playlist selected.", Toast.LENGTH_SHORT).show();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		stop();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mMediaPlayer = mp;
		stop();
		mCurrentlyPlayingPosition++;
		playListItem();
	}

	private void playStopException() {
		Log.e(LOG_TAG, "Error play/stop");
	}

	public class SeekBarUpdater extends AsyncTask<Object, Integer, Object>{

		@Override
		protected Object doInBackground(Object... params) {
			mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if(mMediaPlayer.isPlaying()){
				new SeekBarUpdater().execute();
			}
		}
	}
}