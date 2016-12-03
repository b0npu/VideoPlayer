package com.b0npu.videoplayer

import android.Manifest
import android.content.pm.PackageManager
import android.content.{DialogInterface, Intent}
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.{AlertDialog, AppCompatActivity}
import android.view.{Gravity, View}
import android.widget.{AdapterView, TextView}

/**
  * アプリ起動時の画面を表示するクラス
  *
  * アプリの画面を生成するonCreateメソッドでviewVideoThumbnailメソッドを呼び
  * SDカードに保存されている動画ファイル(mp4ファイル) のサムネイルをGridViewで表示する
  */
class MainActivity extends AppCompatActivity with TypedFindView {

  /**
    * フィールドの定義
    *
    * requestPermissionsメソッドで権限を要求した際に
    * コールバックメソッドのonRequestPermissionsResultメソッドに渡す定数を定義
    * (自クラスで使うだけのフィールドはprivateにして明示的に非公開にしてます)
    */
  private val REQUEST_READ_STORAGE_PERMISSION_CODE: Int = 0x01

  /**
    * アプリの画面を生成
    *
    * アプリを起動するとonCreateが呼ばれてActivityが初期化される
    * 動画の表示に必要なパーミッション(SDカードのデータの読み込み)を確認して
    * パーミッションが許可されていない場合はrequestReadStoragePermissionメソッドで
    * パーミッションの許可を要求する
    * パーミッションが許可されていればviewVideoThumbnailメソッドで
    * 動画ファイル(mp4ファイル)のサムネイルを表示する
    */
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
      != PackageManager.PERMISSION_GRANTED) {
      requestReadStoragePermission
    } else {
      viewVideoThumbnail
    }
  }

  /**
    * viewVideoThumbnailメソッドの定義
    *
    * SDカードの動画ファイル(mp4ファイル)を読み込んでGridViewにサムネイルを表示し
    * サムネイルを選択すると動画を再生する
    * GridViewへのサムネイルの配置はGridViewAdapter(BaseAdapterを継承したサブクラス)を
    * 使うのでGridViewにGridViewAdapterを設置してサムネイルを表示する
    */
  private def viewVideoThumbnail: Unit = {

    /* レイアウトに設置したgridViewのidを変数に格納する */
    val gridView = findView(TR.gridView)

    /* GridViewにGridViewAdapterを設置して画面にサムネイルを表示する */
    val gridViewAdapter = new GridViewAdapter(MainActivity.this)

    if (gridViewAdapter.getCount > 0) {
      /* SDカードに動画ファイルがあればサムネイルを表示する */
      gridView.setAdapter(gridViewAdapter)

    } else {
      /* SDカードに動画ファイルが無くて表示するサムネイルが無い場合は通知する */
      val textView = new TextView(MainActivity.this)
      textView.setGravity(Gravity.CENTER)
      textView.setTextSize(48)
      textView.setText("No Media File")
      setContentView(textView)
    }

    /* サムネイルを選択したら動画ファイル(mp4ファイル)をVideoPlayerActivityで再生する */
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener {

      override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
        /* インテントにVideoPlayerActivityクラスと動画のIDを指定してVideoPlayerの画面を開く */
        val videoPlayerIntent = new Intent(MainActivity.this, classOf[VideoPlayerActivity])
        videoPlayerIntent.putExtra("id", parent.getItemIdAtPosition(position))
        startActivity(videoPlayerIntent)
      }
    })

  }

  /**
    * openSettingsメソッドの定義
    *
    * インテントを使ってアプリの設定画面を開く
    */
  private def openSettings: Unit = {

    val appSettingsIntent: Intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val appPackageUri: Uri = Uri.fromParts("package", getPackageName, null)

    /* インテントにアプリのURIを指定してアプリ情報の画面を開く */
    appSettingsIntent.setData(appPackageUri)
    startActivity(appSettingsIntent)
  }

  /**
    * requestReadStoragePermissionメソッドの定義
    *
    * READ_EXTERNAL_STORAGEのパーミッションの許可(権限取得)を要求する
    * shouldShowRequestPermissionRationaleメソッドを使って
    * 以前にパーミッションの許可を拒否されたことがあるか確認し
    * 拒否されたことがある場合はパーミッションの許可が必要な理由を
    * ダイアログに表示してからパーミッションの許可を要求する
    */
  private def requestReadStoragePermission: Unit = {

    /* パーミッションの許可を拒否されたことがあるか確認する */
    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

      /* パーミッションの許可を拒否されたことがあれば許可が必要な理由を説明してから許可を要求する */
      new AlertDialog.Builder(MainActivity.this)
        .setTitle("パーミッションの追加説明")
        .setMessage("このアプリで動画を表示するにはパーミッションが必要です")
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener {

          override def onClick(dialogInterface: DialogInterface, i: Int): Unit = {
            /* パーミッションの許可を要求 */
            ActivityCompat.requestPermissions(
              MainActivity.this,
              Array[String](Manifest.permission.READ_EXTERNAL_STORAGE),
              REQUEST_READ_STORAGE_PERMISSION_CODE
            )
          }
        })
        .create
        .show

    } else {
      /* 初回要求時か「今後は確認しない」を選択されている場合のパーミッションの許可の要求 */
      ActivityCompat.requestPermissions(
        MainActivity.this,
        Array[String](Manifest.permission.READ_EXTERNAL_STORAGE),
        REQUEST_READ_STORAGE_PERMISSION_CODE
      )
    }
  }

  /**
    * onRequestPermissionsResultメソッドをオーバーライド
    *
    * このメソッドはrequestPermissionsメソッドのコールバックメソッドで
    * requestPermissionsメソッドでパーミッションの許可を要求した結果を取得する
    * 引数のrequestCodeで要求されたパーミッションを区別し
    * grantResultの要素でパーミッションの許可・不許可を確認する
    */
  override def onRequestPermissionsResult(requestCode: Int, permissions: Array[_root_.java.lang.String], grantResults: Array[Int]): Unit = {

    /* 要求されたパーミッションによって対応が変わるので何のパーミッションか確認する */
    requestCode match {

      case REQUEST_READ_STORAGE_PERMISSION_CODE ⇒
        /* パーミッションの要求が拒否されていた場合はダイアログに表示する */
        if (grantResults.length != 1 || grantResults(0) != PackageManager.PERMISSION_GRANTED) {

          /* 「今後は確認しない」が選択されていなければ再度パーミッションの許可を要求する */
          if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(MainActivity.this)
              .setTitle("パーミッション取得エラー")
              .setMessage("動画の表示に必要なパーミッションが取得できませんでした")
              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener {

                override def onClick(dialogInterface: DialogInterface, i: Int): Unit = {
                  requestReadStoragePermission
                }
              })
              .create
              .show

          } else {
            /* 「今後は確認しない」を選択されている場合はアプリの設定画面を開く */
            new AlertDialog.Builder(MainActivity.this)
              .setTitle("パーミッション取得エラー")
              .setMessage("今後は許可しないが選択されました！！アプリ設定＞権限を確認してください（権限をON/OFFすることで状態はリセットされます）")
              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener {

                override def onClick(dialogInterface: DialogInterface, i: Int): Unit = {
                  /* アプリの設定画面を開いて手動で許可してもらう */
                  openSettings
                }
              })
              .create
              .show
          }

        } else {
          /* パーミッションが許可された場合は動画のサムネイルを表示する */
          viewVideoThumbnail
        }
    }
  }
}
