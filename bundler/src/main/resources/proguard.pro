-dontnote

-target 1.8
#-keepattributes *Annotation*
-mergeinterfacesaggressively
#-adaptresourcefilecontents *.txt // dontobfuscate main
-dontpreverify
-allowaccessmodification
-optimizationpasses 2
#-overloadaggressively

-dontobfuscate

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Reflection {
    <methods>;
}
-assumenosideeffects public class java.lang.Thread {
    <methods>;
}

-flattenpackagehierarchy _
-repackageclasses _

-keep, allowoptimization class MAINCLASS
-keep, allowoptimization class MAINCLASS$**

-keepclassmembers, allowoptimization class ** {
    public void load(dev.xdark.clientapi.ClientApi);
    public void unload();
    public void onEnable();
}

#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}

-assumevalues class ru.cristalix.clientapi.JavaMod {
    # Тут нет ошибки!
    public static boolean isClientMod() return false;
}

-assumenosideeffects class ru.cristalix.clientapi.JavaMod {
    public static boolean isClientMod();
}
