
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class **.R$*


-dontwarn okio.**

# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8


-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**

-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

-dontwarn android.arch.**


-dontwarn org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck


-keepclassmembers class me.carc.btown.data.** { <fields>; }
-keep public class me.carc.btown.data.** {
  public void set*(***);
  public *** get*();
}
