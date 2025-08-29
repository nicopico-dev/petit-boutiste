# Ensure our own code is not removed by shrinking
-keep,includedescriptorclasses class fr.nicopico.petitboutiste.** { *; }

# FileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.* { *; }
