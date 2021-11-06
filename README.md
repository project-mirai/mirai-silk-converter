# Silk Converter

----
[![Maven Central](https://img.shields.io/maven-central/v/net.mamoe/mirai-silk-converter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.mamoe%22%20AND%20a:%22mirai-silk-converter%22)

这是 `mirai` 的可选模块, 自 `2.8.0` 起可以用, 用于自动将不支持的音频格式转换为受支持的音频格式

## System requirements

在使用该模块之前, 你必须先安装 ffmpeg, 你可以使用 `ffmpeg -version` 来验证是否安装成功 ffmpeg

> *This tip only for Windows system* 
> 
> FFmpeg 并非常规的类 GUI 软件, FFmpeg 完全是一种命令程序, 这意味着该软件的安装方法不同于一般软件
> 
> 如果您的计算机中还没有 FFmpeg, 您可以按照以下方法按照
> 
> 1. 打开 [FFmpeg Download](https://ffmpeg.org/download.html#build-windows) 
> 2. 创建一个文件夹, 名字随意 (这里为 `E:/Exes/FFmpeg`), 将二进制解压到该文件夹
>    - Tips: 确认 `E:/Exes/FFmpeg/ffmpeg.exe` 存在
> 3. 按下 `Windows + Break` (`Control Panel\System and Security\System`)(控制面板 > 系统&安全 > 系统), 
>    选择左边的 `Advanced system settings`(高级系统设置)
> 4. 找到 `Environment Variables` (环境变量)
> 5. 找到 `Path` (建议修改 `System variables`(系统变量))
> 6. 如果弹出窗口为列表状窗口, 点击 `New`, 将 `E:/Exes/FFmpeg` 添加进去
> 7. 如果弹出窗口只有一个值字段输入, 将 `;E:/Exes/FFmpeg` 添加至末尾 (不要忘记 `;`)
> 8. 刷新相关的环境变量, 如关闭应用程序(一般为命令行窗口(`cmd.exe`))然后重新打开, 如果嫌刷新麻烦可以直接重启系统

## Linkage

### mirai-core

将该模块加入运行时 classpath 即可

```groovy
dependencies {
    api "net.mamoe:mirai-silk-converter:<LATEST_VERSION>"
}
```

### mirai-console

将该模块放入 `plugins` 即可

#### Download

在 https://repo1.maven.org/maven2/net/mamoe/mirai-silk-converter/ 下载最新版本的 `-all.jar` 文件

```shell
#!/usr/bin/env bash
VER=0.0.3
curl -L https://repo1.maven.org/maven2/net/mamoe/mirai-silk-converter/$VER/mirai-silk-converter-$VER-all.jar --output mirai-silk-converter-$VER-all.jar
```

#### 使用 [Mirai Console Loader](https://github.com/iTXTech/mirai-console-loader) 安装 `mirai-silk-converter`

* `MCL` 支持自动更新插件，支持设置插件更新频道等功能

`./mcl --update-package net.mamoe:mirai-silk-converter --channel beta --type plugin`

