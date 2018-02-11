package com.xiaomi.mimcdemo.common;

import com.xiaomi.mimc.MIMCGroupMessage;
import com.xiaomi.mimc.MIMCLogger;
import com.xiaomi.mimc.MIMCMessage;
import com.xiaomi.mimc.MIMCMessageHandler;
import com.xiaomi.mimc.MIMCOnlineStatusListener;
import com.xiaomi.mimc.MIMCServerAck;
import com.xiaomi.mimc.MIMCTokenFetcher;
import com.xiaomi.mimc.MIMCUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserManager {
    /**
     * appId/appKey/appSecret，小米开放平台(https://dev.mi.com/console/appservice/mimc.html)申请
     * 其中appKey和appSecret不可存储于APP端，应存储于APP自己的服务器，以防泄漏。
     *
     * 此处appId/appKey/appSec为小米MimcDemo所有，会在一定时间后失效，建议开发者自行申请
     **/
    private long appId = 2882303761517669588L;
    private String appKey = "5111766983588";
    private String appSecret = "b0L3IOz/9Ob809v8H2FbVg==";
    // 用户登录APP的帐号
    private String appAccount = "";
    private String url;
    private MIMCUser mUser;
    private int mStatus;
    private final static UserManager instance = new UserManager();
    private OnHandleMIMCMsgListener onHandleMIMCMsgListener;

    // 设置消息监听
    public void setHandleMIMCMsgListener(OnHandleMIMCMsgListener onHandleMIMCMsgListener) {
        this.onHandleMIMCMsgListener = onHandleMIMCMsgListener;
    }

    public interface OnHandleMIMCMsgListener {
        void onHandleMessage(MIMCMessage message);
        void onHandleGroupMessage(MIMCGroupMessage message);
        void onHandleStatusChanged(int status);
        void onHandleServerAck(MIMCServerAck serverAck);
        void onHandleCreateGroup(String json, boolean isSuccess);
        void onHandleQueryGroupInfo(String json, boolean isSuccess);
        void onHandleQueryGroupsOfAccount(String json, boolean isSuccess);
        void onHandleJoinGroup(String json, boolean isSuccess);
        void onHandleQuitGroup(String json, boolean isSuccess);
        void onHandleKickGroup(String json, boolean isSuccess);
        void onHandleUpdateGroup(String json, boolean isSuccess);
        void onHandleDismissGroup(String json, boolean isSuccess);
        void onHandlePullP2PHistory(String json, boolean isSuccess);
        void onHandlePullP2THistory(String json, boolean isSuccess);
        void onHandleSendMessageTimeout(MIMCMessage message);
        void onHandleSendGroupMessageTimeout(MIMCGroupMessage groupMessage);
    }

    public static UserManager getInstance() {
        return instance;
    }

    /**
     * 获取用户帐号
     * @return 成功返回用户帐号，失败返回""
     */
    public String getAccount() {
        return appAccount;
    }

    /**
     * 获取用户在线状态
     * @return STATUS_LOGIN_SUCCESS 在线，STATUS_LOGOUT 下线，STATUS_LOGIN_FAIL 登录失败
     */
    public int getStatus() {
        return mStatus;
    }

    public void addMsg(MIMCMessage message) {
        onHandleMIMCMsgListener.onHandleMessage(message);
    }

    public void addMsg(MIMCGroupMessage message) {
        onHandleMIMCMsgListener.onHandleGroupMessage(message);
    }

    /**
     * 获取用户
     * @return  返回已创建用户
     */
    public MIMCUser getUser() {
        return mUser;
    }

    /**
     * 创建用户
     * @param appAccount APP自己维护的用户帐号，不能为null
     * @return 返回新创建的用户
     */
    public MIMCUser newUser(String appAccount){
        if (appAccount == null) return null;

        mUser = new MIMCUser(appId, appAccount);
        // 注册相关监听，必须
        mUser.registerTokenFetcher(new TokenFetcher());
        mUser.registerMessageHandler(new MessageHandler());
        mUser.registerOnlineStatusListener(new OnlineStatusListener());
        this.appAccount = appAccount;

        return mUser;
    }

    class OnlineStatusListener implements MIMCOnlineStatusListener {
        /**
         * 在线状态发生改变
         * @param status 在线状态：STATUS_LOGIN_SUCCESS 在线，STATUS_LOGOUT 下线，STATUS_LOGIN_FAIL 登录失败
         * @param code 状态码
         * @param msg 状态描述
         */
        @Override
        public void onStatusChanged(int status, int code, String msg) {
            mStatus = status;
            onHandleMIMCMsgListener.onHandleStatusChanged(status);
        }
    }

    class MessageHandler implements MIMCMessageHandler {
        /**
         * 接收单聊消息
         * MIMCMessage类
         * String packetId 消息ID
         * long sequence 序列号
         * String fromAccount 发送方帐号
         * String toAccount 接收方帐号
         * byte[] payload 消息体
         * long timestamp 时间戳
         */
        @Override
        public void handleMessage(List<MIMCMessage> packets) {
            for (int i = 0; i < packets.size(); ++i) {
                addMsg(packets.get(i));
            }
        }

        /**
         * 接收群聊消息
         * MIMCGroupMessage类
         * String packetId 消息ID
         * long groupId 群ID
         * long sequence 序列号
         * String fromAccount 发送方帐号
         * byte[] payload 消息体
         * long timestamp 时间戳
         */
        @Override
        public void handleGroupMessage(List<MIMCGroupMessage> packets) {
            for (int i = 0; i < packets.size(); i++) {
                if (!getAccount().equals(packets.get(i).getFromAccount())) {
                    addMsg(packets.get(i));
                }
            }
        }

        /**
         * 接收服务端已收到发送消息确认
         * MIMCServerAck类
         * String packetId 消息ID
         * long sequence 序列号
         * long timestamp 时间戳
         */
        @Override
        public void handleServerAck(MIMCServerAck serverAck) {
            onHandleMIMCMsgListener.onHandleServerAck(serverAck);
        }

        /**
         * 接收单聊超时消息
         * @param message 单聊消息类
         */
        @Override
        public void handleSendMessageTimeout(MIMCMessage message) {
            onHandleMIMCMsgListener.onHandleSendMessageTimeout(message);
        }

        /**
         *接收发送群聊超时消息
         * @param groupMessage 群聊消息类
         */
        @Override
        public void handleSendGroupMessageTimeout(MIMCGroupMessage groupMessage) {
            onHandleMIMCMsgListener.onHandleSendGroupMessageTimeout(groupMessage);
        }
    }

    class TokenFetcher implements MIMCTokenFetcher {
        @Override
        public String fetchToken() {
            /**
             * fetchToken()由SDK内部线程调用，获取小米Token服务器返回的JSON字符串
             * 本MimcDemo直接从小米Token服务器获取JSON串，只解析出键data对应的值返回即可，切记！！！
             * 强烈建议，APP从自己服务器获取data对应的JSON串，APP自己的服务器再从小米Token服务器获取，以防appKey和appSecret泄漏
             */
            url = "https://mimc.chat.xiaomi.net/api/account/token";
            String json = "{\"appId\":" + appId + ",\"appKey\":\"" + appKey + "\",\"appSecret\":\"" + appSecret + "\",\"appAccount\":\"" + appAccount + "\"}";
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request
                    .Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, json))
                    .build();
            Call call = client.newCall(request);
            JSONObject data = null;
            try {
                Response response = call.execute();
                JSONObject object = new JSONObject(response.body().string());
                if (!object.getString("message").equals("success")) {
                    MIMCLogger.w("data failure");
                }
                data = object.getJSONObject("data");
            } catch (Exception e) {
                MIMCLogger.w("Get token exception: " + e.getMessage());
            }

            return data != null ? data.toString() : null;
        }
    }

    /**
     * 创建群
     * @param groupName 群名
     * @param users 群成员，多个成员之间用英文逗号(,)分隔
     */
    public void createGroup(final String groupName, final String users) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId;
        String json = "{\"topicName\":\"" + groupName + "\", \"accounts\":\"" + users + "\"}";
        MediaType JSON = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .post(RequestBody.create(JSON, json))
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleCreateGroup(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询指定群信息
     * @param groupId 群ID
     */
    public void queryGroupInfo(final String groupId) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/" + groupId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .get()
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleQueryGroupInfo(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所属群信息
     */
    public void queryGroupsOfAccount() {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/account";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .get()
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleQueryGroupsOfAccount(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 邀请用户加入群
     * @param groupId 群ID
     * @param users 加入成员，多个成员之间用英文逗号(,)分隔
     */
    public void joinGroup(final String groupId, final String users) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/" + groupId + "/accounts";
        String json = "{\"accounts\":\"" + users + "\"}";
        MediaType JSON = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .post(RequestBody.create(JSON, json))
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleJoinGroup(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 非群主成员退群
     * @param groupId 群ID
     */
    public void quitGroup(final String groupId) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/" + groupId + "/account";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .delete()
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleQuitGroup(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群主踢成员出群
     * @param groupId 群ID
     * @param users 群成员，多个成员之间用英文逗号(,)分隔
     */
    public void kickGroup(final String groupId, final String users) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/" + groupId + "/accounts?accounts=" + users;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .delete()
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleKickGroup(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群主更新群信息
     * @param groupId 群ID
     * @param newOwnerUuid 若为群主UUID则更新群，若为群成员UUID则指派新的群主
     * @param newGroupName 群名
     * @param newGroupBulletin 群公告
     */
    public void updateGroup(final String groupId, final String newOwnerUuid,  final String newGroupName, final String newGroupBulletin) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/" + groupId;
        final String json = "{\"topicId\":\"" + groupId + "\", \"ownerUuid\":\"" + newOwnerUuid + "\", \"topicName\":\""
                + newGroupName + "\", \"bulletin\":\"" + newGroupBulletin + "\"}";
        MediaType JSON = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .put(RequestBody.create(JSON, json))
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleUpdateGroup(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *群主销毁群
     * @param groupId 群ID
     */
    public void dismissGroup(final String groupId) {
        url = "https://mimc.chat.xiaomi.net/api/topic/" + appId + "/" + groupId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("token", mUser.getToken())
                .delete()
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandleCreateGroup(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandleDismissGroup(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拉取单聊消息记录
     * @param toAccount 接收方帐号
     * @param fromAccount 发送方帐号
     * @param utcFromTime 开始时间
     * @param utcToTime 结束时间
     * 注意：utcFromTime和utcToTime的时间间隔不能超过24小时，查询状态为[utcFromTime,utcToTime)，单位毫秒，UTC时间
     */
    public void pullP2PHistory(String toAccount, String fromAccount, String utcFromTime, String utcToTime) {
        url = "https://mimc.chat.xiaomi.net/api/msg/p2p/query/";
        String json = "{\"appId\":\"" + appId + "\", \"toAccount\":\"" + toAccount + "\", \"fromAccount\":\""
                + fromAccount + "\", \"utcFromTime\":\"" + utcFromTime + "\", \"utcToTime\":\"" +
                utcToTime + "\"}";
        MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("Accept", "application/json;charset=UTF-8")
                .addHeader("token", mUser.getToken())
                .post(RequestBody.create(JSON, json))
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandlePullP2PHistory(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandlePullP2PHistory(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拉取群聊消息记录
     * @param account 拉取者帐号
     * @param topicId 群ID
     * @param utcFromTime 开始时间
     * @param utcToTime 结束时间
     * 注意：utcFromTime和utcToTime的时间间隔不能超过24小时，查询状态为[utcFromTime,utcToTime)，单位毫秒，UTC时间
     */
    public void pullP2THistory(String account, String topicId, String utcFromTime, String utcToTime) {
        url = "https://mimc.chat.xiaomi.net/api/msg/p2t/query/";
        String json = "{\"appId\":\"" + appId + "\", \"account\":\"" + account + "\", \"topicId\":\""
                + topicId + "\", \"utcFromTime\":\"" + utcFromTime + "\", \"utcToTime\":\"" + utcToTime + "\"}";
        MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("Accept", "application/json;charset=UTF-8")
                .addHeader("token", mUser.getToken())
                .post(RequestBody.create(JSON, json))
                .build();
        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onHandleMIMCMsgListener.onHandlePullP2THistory(e.getMessage(), false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onHandleMIMCMsgListener.onHandlePullP2THistory(response.body().string(), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}