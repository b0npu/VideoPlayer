package com.b0npu.videoplayer

import android.content.Context
import android.database.Cursor
import android.graphics.{Bitmap, BitmapFactory}
import android.net.Uri
import android.provider.{BaseColumns, MediaStore}
import android.util.Log
import android.view.{View, ViewGroup}
import android.widget.{BaseAdapter, ImageView}

/**
  * 動画ファイルのサムネイルをGridViewに表示するためのAdapterクラス(BaseAdapterのサブクラス)
  *
  * GridViewを表示するActivityの情報(Context)を引数で受取り格子状にViewを表示する
  * 格子状に表示したViewにはImageViewを配置し動画ファイル(mp4ファイル)のサムネイルを表示する
  */
class GridViewAdapter(context: Context) extends BaseAdapter {

  /**
    * フィールドの定義
    *
    * コンストラクタの引数(Context)を格納する定数を定義
    * GridViewに表示するサムネイルの取得に必要な変数も定義する
    * (自クラスで使うだけのフィールドはprivateにして明示的に非公開にしてます)
    */
  private val videoPlayerContext: Context = context

  /* 動画ファイルのIDとファイル名を格納する配列を定義 */
  private var videoFileIdArray: Array[Long] = Array.empty
  private var videoFileNameArray: Array[String] = Array.empty

  /* SDカードの動画ファイルのURIに問い合わせをして検索結果をCursorに格納する */
  private val videoMediaStoreUri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
  private val videoPlayerContextResolver = videoPlayerContext.getContentResolver
  private val videoCursor: Cursor = videoPlayerContextResolver.query(videoMediaStoreUri, null, null, null, null)
  /* 動画ファイルが無かった場合にFATAL EXCEPTIONで異常終了したりするので例外処理の中でCursorの中身を取得する */
  try {
    /* Cursorに格納した動画ファイルの検索結果の先頭から順に動画ファイルのIDとファイル名を取得し配列に格納する */
    videoCursor.moveToFirst
    do {
      /* 動画ファイルのIDとファイル名を取得する */
      val videoFileId = videoCursor.getLong(videoCursor.getColumnIndex(BaseColumns._ID))
      val videoFileName = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.MediaColumns.TITLE))

      videoFileIdArray :+= videoFileId
      videoFileNameArray :+= videoFileName

    } while (videoCursor.moveToNext)

  } catch {
    case e: Exception ⇒ Log.v("Error", s"$e")
  }

  /**
    * getViewメソッドをオーバーライド
    *
    * このメソッドはGridViewの格子状の各ViewにImageViewを表示するメソッドで
    * 引数のconvertViewに表示可能なViewが無ければImageViewを生成して表示する
    * アプリの起動時等で表示可能なconvertViewが無ければ
    * 動画ファイル(mp4ファイル)のサムネイル(ビットマップ画像)を作成して
    * ImageViewに設置しGridViewに配置する
    */
  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    val imageView: ImageView = new ImageView(videoPlayerContext)

    if (convertView == null) {

      val bitmapThumbnail: Bitmap = MediaStore.Video.Thumbnails.getThumbnail(
        videoPlayerContextResolver,
        videoFileIdArray(position),
        MediaStore.Video.Thumbnails.MINI_KIND,
        new BitmapFactory.Options
      )
      val resizeThumbnail: Bitmap = Bitmap.createScaledBitmap(bitmapThumbnail, 320, 180, true)

      imageView.setImageBitmap(resizeThumbnail)
      imageView

    } else {
      convertView
    }
  }

  /**
    * getItemメソッドをオーバーライド
    *
    * このメソッドはGridViewのpositionにあるItemを取得するメソッドで
    * positionの位置にあるサムネイルの動画ファイル名を取得する
    */
  override def getItem(position: Int): AnyRef = {
    videoFileNameArray(position)
  }

  /**
    * getItemIdメソッドをオーバーライド
    *
    * このメソッドはGridViewのpositionにあるItemのIdを取得するメソッドで
    * positionの位置にあるサムネイルの動画ファイルのIdを取得する
    */
  override def getItemId(position: Int): Long = {
    videoFileIdArray(position)
  }

  /**
    * getCountメソッドをオーバーライド
    *
    * このメソッドはGridViewに配置されたViewの数を取得するメソッドで
    * videoFileIdArrayに格納した動画ファイルの数を取得する
    */
  override def getCount: Int = {
    videoFileIdArray.length
  }

}