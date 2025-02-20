# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#处理support包
-dontnote android.support.**
-dontwarn android.support.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class * extends android.os.Parcel
-keep public class * implements android.os.Parcelable

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 自定义接口
-keepclassmembers interface * {
    void on*Succeed(**);
    void on*Failed(**);
}

-keepclassmembers interface * {
    void onSucceed();
    void onSucceed(**);
    void onFailed(**);
    void onCancel(**);
}

-optimizationpasses 5
-dontpreverify
-dontoptimize
-verbose
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

# OkHttp3
-keep class com.squareup.okhttp3.** { *;}
-keep,includedescriptorclasses class okhttp3.** { *; }
-keep,includedescriptorclasses interface okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontnote okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes Signature-keepattributes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }

-keep public class * extends android.app.Activity{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepattributes *Annotation*

-keepclasseswithmembernames class *{
	native <methods>;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

## ----------------------------------
##      OkHttp相关
## ----------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**

## ----------------------------------
##      Okio相关
## ----------------------------------
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

## ----------------------------------
##      Glide相关
## ----------------------------------
-keep class com.bumptech.glide.Glide { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class com.shuyu.gsyvideoplayer.player.** {*;}
-dontwarn com.shuyu.gsyvideoplayer.player.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class androidx.media3.** {*;}
-keep interface androidx.media3.**

-keep class com.shuyu.alipay.** {*;}
-keep interface com.shuyu.alipay.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.qtk.kotlintest.domain.data.db.CityForecast {*;}
-keep class com.qtk.kotlintest.domain.data.db.DayForecast {*;}
-keep class com.qtk.kotlintest.domain.data.room.CityForecastRoom {*;}
-keep class com.qtk.kotlintest.domain.data.room.DayForecastRoom {*;}
-keep class com.qtk.kotlintest.domain.data.server.ForecastResult {*;}
-keep class com.qtk.kotlintest.domain.data.server.City {*;}
-keep class com.qtk.kotlintest.domain.data.server.Coordinates {*;}
-keep class com.qtk.kotlintest.domain.data.server.Forecast {*;}
-keep class com.qtk.kotlintest.domain.data.server.Temperature {*;}
-keep class com.qtk.kotlintest.domain.data.server.Weather {*;}
-keep class com.qtk.kotlintest.domain.model.** {*;}
-keep class com.qtk.kotlintest.retrofit.data.* {*;}
-keep class **.*entity*.** {*;}
-keepclassmembers class com.qtk.kotlintest.base.base.AdapterProxy {
    public ** onCreateViewHolder(android.view.ViewGroup, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1);
}
-keepclassmembers public class * extends androidx.viewbinding.ViewBinding {
    public static * inflate(android.view.LayoutInflater);
    public static * inflate(android.view.LayoutInflater,android.view.ViewGroup,boolean);
    public static * inflate(android.view.LayoutInflater,android.view.ViewGroup,java.lang.Boolean);
    public static * bind(android.view.View);
}
-keepclassmembers public class * extends androidx.lifecycle.ViewModelProvider {
    public ** get(java.lang.Class);
}
-keepclassmembers public class * extends androidx.lifecycle.ViewModel {
    public <init>(***);
}
-keep class androidx.recyclerview.widget.RecyclerView$ViewFlinger{*;}
-keepclassmembers class androidx.recyclerview.widget.RecyclerView {
    private androidx.recyclerview.widget.RecyclerView$ViewFlinger mViewFlinger;
}
-keep class android.widget.OverScroller$SplineOverScroller{*;}
-keepclassmembers class android.widget.OverScroller {
    public android.widget.OverScroller$SplineOverScroller mScrollerY;
}
-keep class androidx.viewpager2.widget.ViewPager2 {*;}

-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }