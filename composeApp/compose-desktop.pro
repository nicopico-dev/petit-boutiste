# Documentation
# https://www.guardsquare.com/manual/configuration/usage
# https://www.guardsquare.com/manual/troubleshooting/troubleshooting

# Ensure our own code is not removed by shrinking
-keep,includedescriptorclasses class fr.nicopico.petitboutiste.** { *; }

# FileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.* { *; }

# TODO Process all warnings
