# MIMC官方详细文档点击此链接：[详细文档](https://github.com/Xiaomi-mimc/operation-manual)

# 快速开始

## 1) 在应用的AndroidManifest.xml里添加以下配置：

``` xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="com.xiaomi.xmsf.permission.LOG_PROVIDER" />

<!-- 这里的包名"com.xiaomi.mimcdemo"必须替换成App自己的包名 --> 
<permission
    android:name="com.xiaomi.mimcdemo.permission.MIMC_RECEIVE"
    android:protectionLevel="signature" />
<uses-permission android:name="com.xiaomi.mimcdemo.permission.MIMC_RECEIVE" />

<service
    android:name="com.xiaomi.mimc.MimcService"
    android:enabled="true"
    android:exported="false" />

<service
    android:name="com.xiaomi.mimc.MimcCoreService"
    android:enabled="true"
    android:exported="false"
    android:process=":mimc"/>

<service
    android:name="com.xiaomi.mimc.MimcJobService"
    android:enabled="true"
    android:exported="false"
    android:permission="android.permission.BIND_JOB_SERVICE"
    android:process=":mimc" />

<receiver android:name="com.xiaomi.mimc.receivers.PingReceiver">
    <intent-filter>
	<action android:name="com.xiaomi.push.PING_TIMER" />
    </intent-filter>
</receiver>

<receiver
    android:name="com.xiaomi.mimc.receivers.MimcReceiver"
    android:exported="true">
    <intent-filter>
	<action android:name="com.xiaomi.channel.PUSH_STARTED" />
	<action android:name="com.xiaomi.push.service_started" />
	<action android:name="com.xiaomi.push.channel_opened" />
	<action android:name="com.xiaomi.push.channel_closed" />
	<action android:name="com.xiaomi.push.new_msg" />
	<action android:name="com.xiaomi.push.kicked" />
    </intent-filter>
</receiver>
```
#### 注意：
我们将MimcCoreService和MimcJobService定义在了mimc进程中，您也可以配置其运行在任意进程。如果没有配置android:process这个属性，那么它们将运行在应用的主进程中。

## 2) 初始化

``` java 
MimcClient.initialize(this);
User user = new User(appId, username);
```

## 3) 请求Token
#### 参考 [详细文档](https://github.com/Xiaomi-mimc/operation-manual) 如何接入 & 安全认证
``` java 
user.registerTokenFetcher(MIMCTokenFetcher fetcher); 
interface MIMCTokenFetcher {
	/**
	 * @return: 小米TokenService服务下发的原始数据
	 * @note: fetchToken()访问APP应用方自行实现的AppProxyService服务，该服务实现以下功能：
			1. 存储appId/appKey/appSec（不应当存储在APP客户端）
			2. 用户在APP系统内的合法鉴权
			3. 调用小米TokenService服务，并将小米TokenService服务返回结果通过fetchToken()原样返回，参考3）获取Token
	 **/
	public String fetchToken();
}
```

## 4) 获得连接状态

``` java 
user.registerOnlineStatusHandler(MIMCOnlineStatusHandler handler);
interface MIMCOnlineStatusHandler {
	public void statusChange();
}
```

## 5) 接收消息

``` java 
user.registerMessageHandler(MIMCMessageHandler handler);
interface MIMCMessageHandler {
	public void handleMessage(List<MIMCMessage> packets);        
	public void handleGroupMessage(List<MIMCGroupMessage> packets); 
	
	// 参数serverAck.packetId与9)、10）对应
	public void handleServerAck(MIMCServerAck serverAck);
	
	public void handleSendMessageTimeout(MIMCMessage message);
	public void handleSendGroupMessageTimeout(MIMCGroupMessage groupMessage);
}
```

## 6) 登录

``` java 
// 建议App从后台切换到前台时，调用一次登录。
user.login();
```
		
## 7) 发送单聊消息

``` java 
String packetId = user.sendMessage(String toUserName, byte[] payload);
```

## 8) 发送群聊消息

``` java
String packetId = user.sendGroupMessage(long groupID, byte[] payload); 
```

## 9) 拉取消息

``` java
// 从服务端拉取未下发的消息，建议App从后台切换到前台时拉一下。
user.pull();
```

## 10) 注销

``` java 
user.logout();
```

[回到顶部](#readme)
