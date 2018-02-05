# MIMC官方详细文档点击此链接：[详细文档](https://github.com/Xiaomi-mimc/operation-manual)

# 快速开始

## 1) 在应用的AndroidManifest.xml里添加以下配置：
#### 包名"com.xiaomi.mimcdemo"必须替换成APP自己的包名
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
#### 备注：
```
我们将MimcCoreService / MimcJobService定义在了mimc进程中。
开发者也可以配置其运行在任意进程，如果没有配置android:process这个属性，那么它们将运行在应用的主进程中。
```

## 2) 初始化

``` java 
MimcClient.initialize(this);

/**
 * @param[appId]: 开发者在小米开放平台申请的appId
 * @param[appAccount]: 用户在APP帐号系统内的唯一帐号ID
 **/
User user = new User(appId, appAccount);
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
			3. 调用小米TokenService服务，并将小米TokenService服务返回结果通过fetchToken()原样返回
	 **/
	public String fetchToken();
}
```

## 4) 在线状态变化回调

``` java 
user.registerOnlineStatusHandler(MIMCOnlineStatusHandler handler);
interface MIMCOnlineStatusHandler {
    /**
     * @param[isOnline]: true 在线，false 离线
     * @param[errType]: 登录失败类型
     * @param[errReason]: 登录失败原因
     * @param[errDescription]: 登录失败原因详细描述
     **/
    public void statusChange(boolean isOnline, String errType, String errReason, String errDescription);
}
```

## 5) 接收消息回调

``` java 
user.registerMessageHandler(MIMCMessageHandler handler);
interface MIMCMessageHandler {
	public void handleMessage(List<MIMCMessage> packets);        
	public void handleGroupMessage(List<MIMCGroupMessage> packets); 
	
	/**
	 * @param[serverAck]: 服务器返回的serverAck对象
	 *        serverAck.packetId: 客户端生成的消息ID
	 *        serverAck.timestamp: 消息发送到服务器的时间(单位:ms)
	 *        serverAck.sequence: 服务器为消息分配的递增ID，可用于去重/排序
	 **/ 
	public void handleServerAck(MIMCServerAck serverAck);
	
	public void handleSendMessageTimeout(MIMCMessage message);
	public void handleSendGroupMessageTimeout(MIMCGroupMessage groupMessage);
}
```

## 6) 登录

``` java 
/**
 * @note: 用户登录接口，除在APP初始化时调用，APP从后台切换到前台时也建议调用一次
 **/ 
user.login();
```
		
## 7) 发送单聊消息

``` java 
/**
 * @param[toAppAccount]: 消息接收者在APP帐号系统内的唯一帐号ID
 * @param[payload]: 开发者自定义消息体
 * @return: 客户端生成的消息ID
 **/ 
String packetId = user.sendMessage(String toAppAccount, byte[] payload);
```

## 8) 发送群聊消息

``` java
/**
 * @param[groupId]: 群ID，也称为topicId
 * @param[payload]: 开发者自定义消息体
 * @return: 客户端生成的消息ID
 **/ 
String packetId = user.sendGroupMessage(long groupID, byte[] payload); 
```

## 9) 注销

``` java 
user.logout();
```

[回到顶部](#readme)
