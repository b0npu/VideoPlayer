package com.b0npu.videoplayer

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.{MediaController, VideoView}

/**
  * ビデオプレーヤーの画面を表示するクラス
  *
  * アプリの画面を生成するonCreateメソッドでMainActivityからIntentを受取り
  * GridViewで選択された動画ファイル(mp4ファイル)を再生する
  */
class VideoPlayerActivity extends AppCompatActivity with TypedFindView {

  /**
    * アプリの画面を生成
    *
    * アプリを起動するとonCreateが呼ばれてActivityが初期化される
    * 選択された動画ファイル(mp4ファイル)のIDをIntentから取得し
    * VideoViewで動画を再生する
    */
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_videoplayer)

    /* レイアウトに設置したvideoViewのidを取得してVideoViewにコントローラを配置する */
    val videoView: VideoView = findView(TR.videoView)
    videoView.setMediaController(new MediaController(VideoPlayerActivity.this))

    /* Intentから動画ファイルのIdを取得し再生する動画ファイルのURIをVideoViewに渡す */
    val videoFileIntent: Intent = getIntent
    val videoFileId = videoFileIntent.getLongExtra("id", 0)
    val videoFileUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoFileId.toString)
    videoView.setVideoURI(videoFileUri)

    /* VideoViewが動画ファイルの読込みを終えたら動画を再生する */
    videoView.setOnPreparedListener(new OnPreparedListener {
      override def onPrepared(mediaPlayer: MediaPlayer): Unit = {
        mediaPlayer.start
      }
    })
  }
}

