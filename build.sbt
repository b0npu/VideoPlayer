androidBuild

fork in Test := true

// Enforce Java 7 compilation (in case you have the JDK 8 installed)
javacOptions ++=
    "-source" :: "1.7" ::
    "-target" :: "1.7" ::
    Nil

libraryDependencies ++=
    "com.android.support" % "appcompat-v7" % "24.1.1" ::
    "com.android.support" % "cardview-v7" % "24.1.1" ::
    "com.android.support" % "design" % "24.1.1" ::
    "com.android.support" % "gridlayout-v7" % "24.1.1" ::
    "com.android.support" % "recyclerview-v7" % "24.1.1" ::
    "com.android.support" % "support-v4" % "24.1.1" ::
    // Version 2.4.x requires Java 8
    "com.typesafe.play" %% "play-json" % "2.3.10" ::
    "com.geteit" %% "robotest" % "0.12" % "test" ::
    "org.scalatest" %% "scalatest" % "2.2.6" % "test" ::
    Nil

name := "VideoPlayer"

// Predefined as IceCreamSandwich (4.0), nothing stops you from going below
minSdkVersion := "14"

// Prevent common com.android.builder.packaging.DuplicateFileException.
// Add further file names if you experience the exception after adding new dependencies
packagingOptions := PackagingOptions(
    excludes =
        "META-INF/LICENSE" ::
        "META-INF/LICENSE.txt" ::
        "META-INF/NOTICE" ::
        "META-INF/NOTICE.txt" ::
        Nil
)

platformTarget := "android-24"

proguardCache ++=
    "android.support" ::
    "play" ::
    Nil

proguardOptions ++=
    "-keepattributes EnclosingMethod,InnerClasses,Signature" ::
    "-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry" ::
    "-dontwarn javax.xml.bind.DatatypeConverter" ::
    "-dontnote org.joda.time.DateTimeZone" ::
    "-dontnote scala.concurrent.stm.impl.STMImpl$" ::
    Nil

// Shortcut: allows you to execute "sbt run" instead of "sbt android:run"
run <<= run in Android

scalacOptions ++=
    // Print detailed deprecation warnings to the console
    "-deprecation" ::
    // Print detailed feature warnings to the console
    "-feature" ::
    Nil

// Don't upgrade to 2.12.x as it requires Java 8 which does not work with Android
scalaVersion := "2.11.8"

targetSdkVersion := "24"

versionCode := Some( 0 )

versionName := Some( "0.0.0" )
