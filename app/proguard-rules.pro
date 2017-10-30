


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


-dontwarn okio.**

# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8


-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**


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
