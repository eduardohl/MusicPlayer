package com.eldorado.myMusicPlayer.model;

public class Music {

	private Long id;
	private String musicName;
	private String playListName;
	private Integer position;
	
	public Music(){}
	
	public Music(String musicName, String playListName, Integer position) {
		this.musicName = musicName;
		this.playListName = playListName;
		this.position = position;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMusicName() {
		return musicName;
	}
	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	public String getPlayListName() {
		return playListName;
	}
	public void setPlayListName(String playListName) {
		this.playListName = playListName;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		return musicName;
	}
}
