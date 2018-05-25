# roselia-control
A naive remote control server

一个简单的远程控制应用，可以远程通过手机等其他设备操控电脑（例如：幻灯片放映等）

## 目录说明：

### RoseliaControlAPP

说来惭愧，我一开始的想法是直接用Scala开发客户端APP，然后最后打包的尺寸上天了，于是我切换到了C#（~~微软大法好~~），这便是RoseliaControlWindows。

但是这个仍然可以使用，并且可以跨平台。

### RoseliaControlWindows

用了C#以后，急速开发，很快就写完了（），然后同时支持了扫码登录。

### RoseliaControlServer

RoseliaServer 使用Scala+Play编写，和客户端的通讯采用WebSocket。（Actor模型真好用）

但是Actor的使用方法好像不是官方推荐的最佳实践……