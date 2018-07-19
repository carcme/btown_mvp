# Strip `Log.v`, `Log.d`, and `Log.i` statements, leave `Log.w` and `Log.e` intact.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

-keep class com.android.vending.billing.**
-dontnote com.google.vending.licensing.**
-dontnote com.android.vending.licensing.**

#-keep class **.R$*
#-keepclassmembers class **.R$* { public static <fields>; }

-keep class .R
-keep class **.R$* { <fields>; }
-keepclasseswithmembers class **.R$* { public static final int define_*; }

-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

-dontnote com.mikepenz.fastadapter.items.**


-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8

-dontwarn com.squareup.okhttp.**

-keepattributes Signature

-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

-dontwarn android.arch.**

# OSM specific
-dontwarn org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck

# Keep data classes
-keepclassmembers class me.carc.btown.data.** { <fields>; }
-keep public class me.carc.btown.data.** {
  public void set*(***);
  public *** get*();
}

-dontwarn me.carc.btown.tours.attractionPager.AttractionPagerActivity

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception


