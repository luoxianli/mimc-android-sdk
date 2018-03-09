package com.xiaomi.mimcdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaomi.mimc.MIMCConstant;
import com.xiaomi.mimcdemo.R;
import com.xiaomi.mimcdemo.common.NetWorkUtils;
import com.xiaomi.mimcdemo.common.UserManager;

public class UpdateGroupDialog extends Dialog {

    public UpdateGroupDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_group_dialog);
        setCancelable(true);
        setTitle(R.string.update_group);
        final EditText etGroupId = (EditText)findViewById(R.id.et_group_id);
        final EditText etOwnerAccount = (EditText)findViewById(R.id.et_owner_account);
        final EditText etGroupName =(EditText)findViewById(R.id.et_group_name);
        final EditText etGroupBulletin = (EditText)findViewById(R.id.et_group_bulletin);

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String groupId = etGroupId.getText().toString();
                String ownerAccount = etOwnerAccount.getText().toString();
                String groupName = etGroupName.getText().toString();
                String groupBulletin = etGroupBulletin.getText().toString();

                if (!NetWorkUtils.isNetwork(getContext())) {
                    Toast.makeText(getContext(), getContext().getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                    return;
                } else if (UserManager.getInstance().getStatus() != MIMCConstant.STATUS_LOGIN_SUCCESS) {
                    Toast.makeText(getContext(), getContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    return;
                } else if (groupId.isEmpty()) {
                    Toast.makeText(getContext(), getContext().getString(R.string.input_id_of_group), Toast.LENGTH_SHORT).show();
                    return;
                }

                UserManager.getInstance().updateGroup(groupId, ownerAccount, groupName, groupBulletin);
                dismiss();
            }
        });
    }
}
