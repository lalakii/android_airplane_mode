# Android Airplane Mode
![API: 27 or Or older. (shields.io)](https://img.shields.io/badge/API-27%20Or%20earlier-yellow)
+ Requires shizuku permission：[Get Shizuku App](https://github.com/RikkaApps/Shizuku/releases/)
+ TargetSdk <= 27 （Android 8.1）

## README.en

Implement flight mode switch through reflection, `setAirplaneMode` method has been removed from the source code since Android 8.1, the targetSdk version at compile time must not be too high, or else you can't find the “**setAirplaneMode**” method through the reflection class!

## README.zh_CN

通过反射实现飞行模式开关，`setAirplaneMode`方法自Android8.1之后的源码中已移除，编译时的targetSdk版本不能太高，否则无法通过反射类找到"**setAirplaneMode**"方法！

## Demo
<img src="https://cdn.jsdelivr.net/gh/iamverycute/android_airplane_mode/video/demo.gif" width="300" alt="demo">

## About
Old version: https://github.com/iamverycute/TAM
