# QuestCraft | Base provided by the PojavLauncher team free of charge.

## Note
- We do not exist on TikTok. No one from the dev team makes TikTok videos. 
- QuestCraft (QCXR) is developed and maintained by the QCXR and PojavLauncher team including public and or anonymous outside contributors.
- All support questions should be asked inside of the QuestCraft Discord or PojavLauncher Discord servers for the best experience.

## Navigation
- [Introduction](#introduction)  
- [Building](#building) 
- [Current status](#current-status) 
- [License](#license) 
- [Contributing](#contributing) 
- [Credits & Third party components and their licenses](Credits-&-Third-party-components-and-their-licenses-if-available)

## Introduction 
PojavLauncher is a Minecraft: Java Edition launcher for Android and iOS based on [Boardwalk](https://github.com/zhuowei/Boardwalk). This launcher has been modified for use in QuestCraft (QCXR) and all underlying base code is supplied for free by the PojavLauncher Team. 

## Building
### Java Runtime Environment (JRE)
- JRE for Android is [here](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u), also the build script [here](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch).
- Follow build instruction on build script [README.md](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/blob/buildjre8/README.md).
- You can also get [CI auto builds](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/actions).
- Either get `jre8-pojav` artifact from auto builds, or do splitting by yourself:</br>
        - Get JREs for all of 4 supported architectures (arm, arm64, x86, x86_64) </br> 
        - Split JRE into parts:</br>
                Platform-independent: .jar files, libraries, configs, etc...</br>
                Platform-dependent: .so files, etc...</br>
        - Create:</br>
                file named `universal.tar.xz` with all platform-independent files</br>
                4 files named `bin-<arch>.tar.xz` with all platform-dependent files per-architecture</br>
        - Put these in `assets/components/jre/` folder</br>
        - (If needed) update the Version file with the current date</br>

### LWJGL
- **Coming soon**

### The Launcher
- Because languages are auto added by Crowdin, so need to run language list generator before building. In this directory, run:
```
# On Linux, Mac OS:
chmod +x scripts/languagelist_updater.sh
bash scripts/languagelist_updater.sh

# On Windows:
scripts\languagelist_updater.bat
```
- Then, run these commands ~~build use Android Studio~~.
```
# Build GLFW stub
./gradlew :jre_lwjgl3glfw:build
# mkdir app_pojavlauncher/src/main/assets/components/internal_libs
rm app_pojavlauncher/src/main/assets/components/lwjgl3/lwjgl-glfw-classes.jar
cp jre_lwjgl3glfw/build/libs/jre_lwjgl3glfw-3.2.3.jar app_pojavlauncher/src/main/assets/components/lwjgl3/lwjgl-glfw-classes.jar
        
# Build the launcher
./gradlew :app_pojavlauncher:assembleDebug
```
(Replace `gradlew` to `gradlew.bat` if you are building on Windows)

## Current status
- Coming Soon

## Known Issues
- Coming Soon

## License
- PojavLauncher is licensed under [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE).
- QuestCraft (QCXR) is a fork of PojavLauncher and is licensed under the same [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE) license.

## Contributing
Contributions are welcome! We welcome any type of contribution, not only code.
Any code change should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Credits & Third party components and their licenses if available
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [GL4ES](https://github.com/PojavLauncherTeam/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- [LWJGL3](https://github.com/PojavLauncherTeam/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [LWJGLX](https://github.com/PojavLauncherTeam/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.<br>
- [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
- [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).
- [xHook](https://github.com/iqiyi/xHook) (Used for exit code trapping): [MIT and BSD-style licenses](https://github.com/iqiyi/xHook/blob/master/LICENSE).
- [libepoxy](https://github.com/anholt/libepoxy): [MIT License](https://github.com/anholt/libepoxy/blob/master/COPYING).
- [virglrenderer](https://github.com/PojavLauncherTeam/virglrenderer): [MIT License](https://gitlab.freedesktop.org/virgl/virglrenderer/-/blob/master/COPYING).
- [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher) (QCXR Base application): [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE).
