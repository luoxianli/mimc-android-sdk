package com.xiaomi.mimcdemo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomi.mimc.MIMCConstant;
import com.xiaomi.mimc.MIMCGroupMessage;
import com.xiaomi.mimc.MIMCMessage;
import com.xiaomi.mimc.MIMCServerAck;
import com.xiaomi.mimc.MIMCUser;
import com.xiaomi.mimcdemo.common.ChatAdapter;
import com.xiaomi.mimcdemo.common.NetWorkUtils;
import com.xiaomi.mimcdemo.common.ParseJson;
import com.xiaomi.mimcdemo.common.TimeUtils;
import com.xiaomi.mimcdemo.common.UserManager;
import com.xiaomi.mimcdemo.dialog.CreateGroupDialog;
import com.xiaomi.mimcdemo.dialog.DismissGroupDialog;
import com.xiaomi.mimcdemo.dialog.GroupInfoDialog;
import com.xiaomi.mimcdemo.dialog.JoinGroupDialog;
import com.xiaomi.mimcdemo.dialog.KickGroupDialog;
import com.xiaomi.mimcdemo.dialog.LoginDialog;
import com.xiaomi.mimcdemo.dialog.PullP2PHistoryMsgDialog;
import com.xiaomi.mimcdemo.dialog.PullP2THistoryMsgDialog;
import com.xiaomi.mimcdemo.dialog.QueryGroupInfoDialog;
import com.xiaomi.mimcdemo.dialog.QuitGroupDialog;
import com.xiaomi.mimcdemo.dialog.SendGroupMsgDialog;
import com.xiaomi.mimcdemo.dialog.SendMsgDialog;
import com.xiaomi.mimcdemo.dialog.UpdateGroupDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements UserManager.OnHandleMIMCMsgListener {
    private ChatAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<MIMCGroupMessage> mDatas = new ArrayList<>();
    GroupInfoDialog groupInfoDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupInfoDialog = new GroupInfoDialog(this);
        // 设置处理MIMC消息监听器
        UserManager.getInstance().setHandleMIMCMsgListener(this);

        // 登录
        findViewById(R.id.mimc_login).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog LoginDialog = new LoginDialog(MainActivity.this);
                    LoginDialog.show();
                }
            });

        // 注销
        findViewById(R.id.mimc_logout).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MIMCUser user = UserManager.getInstance().getUser();
                    if (user != null) {
                        user.logout();
                    }
                }
            });

        // 发送消息
        findViewById(R.id.mimc_sendMsg).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog SendMsgDialog = new SendMsgDialog(MainActivity.this);
                    SendMsgDialog.show();
                }
            });

        // 创建群
        findViewById(R.id.btn_create_group).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgCreateGroup = new CreateGroupDialog(MainActivity.this);
                    dlgCreateGroup.show();
                }
            });

        // 查询群信息
        findViewById(R.id.btn_query_group_info).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgQueryGroupInfo = new QueryGroupInfoDialog(MainActivity.this);
                    dlgQueryGroupInfo.show();
                }
            });

        // 查询用户已加入的群信息
        findViewById(R.id.btn_query_all_group_info).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetWorkUtils.isNetwork(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserManager.getInstance().getStatus() != MIMCConstant.STATUS_LOGIN_SUCCESS) {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UserManager.getInstance().queryGroupsOfAccount();
                }
            });

        // 邀请用户加入群
        findViewById(R.id.btn_join_group).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgJoinGroup = new JoinGroupDialog(MainActivity.this);
                    dlgJoinGroup.show();
                }
            });

        // 非群主用户退出群
        findViewById(R.id.btn_quit_group).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgQuitGroup = new QuitGroupDialog(MainActivity.this);
                    dlgQuitGroup.show();
                }
            });

        // 群主踢用户出群
        findViewById(R.id.btn_kick_group).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgKickGroup = new KickGroupDialog(MainActivity.this);
                    dlgKickGroup.show();
                }
            });

        // 群主更新群信息
        findViewById(R.id.btn_update_group).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgUpdateGroup = new UpdateGroupDialog(MainActivity.this);
                    dlgUpdateGroup.show();
                }
            });

        // 群主销毁群
        findViewById(R.id.btn_dismiss_group).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgDismissGroup = new DismissGroupDialog(MainActivity.this);
                    dlgDismissGroup.show();
                }
            });

        // 发送群消息
        findViewById(R.id.btn_send_group_msg).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgSendGroupMsg = new SendGroupMsgDialog(MainActivity.this);
                    dlgSendGroupMsg.show();
                }
            });

        // 拉取单聊休息记录
        findViewById(R.id.btn_p2p_history).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dlgP2PHistory = new PullP2PHistoryMsgDialog(MainActivity.this);
                    dlgP2PHistory.show();
                }
            });

        // 拉取群聊消息记录
        findViewById(R.id.btn_p2t_history).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dlgP2THistory = new PullP2THistoryMsgDialog(MainActivity.this);
                        dlgP2THistory.show();
                    }
                });


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_chat);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateOnlineStatus(int status) {
        TextView textView = (TextView) findViewById(R.id.mimc_status);
        Drawable drawable;
        if (status == MIMCConstant.STATUS_LOGIN_SUCCESS) {
            drawable = getResources().getDrawable(R.drawable.point_h);
        } else {
            drawable = getResources().getDrawable(R.drawable.point);
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
                null);
    }

    // 处理单聊消息
    @Override
    public void onHandleMessage(final MIMCMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MIMCGroupMessage groupMessage = new MIMCGroupMessage();
                groupMessage.setGroupId(-1);
                groupMessage.setTimestamp(message.getTimestamp());
                groupMessage.setPayload(message.getPayload());
                groupMessage.setFromAccount(message.getFromAccount());
                mDatas.add(groupMessage);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    // 处理群消息
    @Override
    public void onHandleGroupMessage(final MIMCGroupMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDatas.add(message);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    // 处理登录状态
    @Override
    public void onHandleStatusChanged(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateOnlineStatus(status);
            }
        });
    }

    // 处理服务端消息确认
    @Override
    public void onHandleServerAck(final MIMCServerAck serverAck) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Server has received packetId: "
                    + serverAck.getPacketId()
                    + "\n" + TimeUtils.utc2Local(serverAck.getTimestamp()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 处理创建群
    @Override
    public void onHandleCreateGroup(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseCreateGroupJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理查询群信息
    @Override
    public void onHandleQueryGroupInfo(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseQueryGroupInfoJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理查询已加入的群信息
    @Override
    public void onHandleQueryGroupsOfAccount(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseQueryGroupsOfAccountJson(this, json);
        }
        final String info = json;
                runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理加入群
    @Override
    public void onHandleJoinGroup(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseJoinGroupJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理非群主退群
    @Override
    public void onHandleQuitGroup(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseQuitGroupJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理群主踢人出群
    @Override
    public void onHandleKickGroup(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseKickGroupJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理群主更新群信息
    @Override
    public void onHandleUpdateGroup(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseUpdateGroupJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理群主销毁群
    @Override
    public void onHandleDismissGroup(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseDismissGroupJson(json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理拉取单聊消息
    @Override
    public void onHandlePullP2PHistory(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseP2PHistoryJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理拉取群聊消息
    @Override
    public void onHandlePullP2THistory(String json, boolean isSuccess) {
        if (isSuccess) {
            json = ParseJson.parseP2THistoryJson(this, json);
        }
        final String info = json;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupInfoDialog.show();
                groupInfoDialog.setContent(info);
            }
        });
    }

    // 处理发送消息超时
    @Override
    public void onHandleSendMessageTimeout(final MIMCMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Send message timeout: " +
                    new String(message.getPayload()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 处理发送群消息超时
    @Override
    public void onHandleSendGroupMessageTimeout(final MIMCGroupMessage groupMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Send group message timeout: " +
                    new String(groupMessage.getPayload()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}