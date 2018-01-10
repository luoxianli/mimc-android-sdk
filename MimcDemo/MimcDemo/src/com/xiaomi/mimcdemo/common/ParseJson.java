package com.xiaomi.mimcdemo.common;

import android.content.Context;

import com.xiaomi.mimcdemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by houminjiang on 18-1-8.
 */

public class ParseJson {
    public static String parseCreateGroupJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            object = object.getJSONObject("data");
            JSONObject topicInfo = object.getJSONObject("topicInfo");
            info += context.getString(R.string.group_id) + topicInfo.getString("topicId") + "\n";
            info += context.getString(R.string.group_name) + topicInfo.getString("topicName") + "\n";
            JSONArray members = object.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.members) + member.getString("account")
                        + "    " + context.getString(R.string.uuid) + member.getString("uuid") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String parseQueryGroupInfoJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            object = object.getJSONObject("data");
            JSONObject topicInfo = object.getJSONObject("topicInfo");
            info += context.getString(R.string.group_id) + topicInfo.getString("topicId") + "\n";
            info += context.getString(R.string.group_name) + topicInfo.getString("topicName") + "\n";
            JSONArray members = object.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.members) + member.getString("account")
                        + "    " + context.getString(R.string.uuid) + member.getString("uuid") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String parseQueryGroupsOfAccountJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            JSONArray members = object.getJSONArray("data");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.group_id) + member.getString("topicId") + "\n";
                info += context.getString(R.string.group_name) + member.getString("topicName") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String parseJoinGroupJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            object = object.getJSONObject("data");
            JSONObject topicInfo = object.getJSONObject("topicInfo");
            info += context.getString(R.string.group_id) + topicInfo.getString("topicId") + "\n";
            info += context.getString(R.string.group_name) + topicInfo.getString("topicName") + "\n";
            JSONArray members = object.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.members) + member.getString("account")
                        + "    " + context.getString(R.string.uuid) + member.getString("uuid") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String parseQuitGroupJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            object = object.getJSONObject("data");
            JSONObject topicInfo = object.getJSONObject("topicInfo");
            info += context.getString(R.string.group_id) + topicInfo.getString("topicId") + "\n";
            info += context.getString(R.string.group_name) + topicInfo.getString("topicName") + "\n";
            JSONArray members = object.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.members) + member.getString("account")
                        + "    " + context.getString(R.string.uuid) + member.getString("uuid") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String parseKickGroupJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            object = object.getJSONObject("data");
            JSONObject topicInfo = object.getJSONObject("topicInfo");
            info += context.getString(R.string.group_id) + topicInfo.getString("topicId") + "\n";
            info += context.getString(R.string.group_name) + topicInfo.getString("topicName") + "\n";
            JSONArray members = object.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.members) + member.getString("account")
                        + "    " + context.getString(R.string.uuid) + member.getString("uuid") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String parseUpdateGroupJson(Context context, final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            object = object.getJSONObject("data");
            JSONObject topicInfo = object.getJSONObject("topicInfo");
            info += context.getString(R.string.group_id) + topicInfo.getString("topicId") + "\n";
            info += context.getString(R.string.uuid_owner_of_group) + topicInfo.getString("ownerUuid") + "\n";
            info += context.getString(R.string.group_name) + topicInfo.getString("topicName") + "\n";
            info += context.getString(R.string.group_bulletin) + topicInfo.getString("bulletin") + "\n";
            JSONArray members = object.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                info += context.getString(R.string.members) + member.getString("account")
                        + "    " + context.getString(R.string.uuid) + member.getString("uuid") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String parseDismissGroupJson(final String json) {
        String info = "";
        if (json == null || json.isEmpty()) {
            return info;
        }

        try {
            JSONObject object = new JSONObject(json);
            info = object.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }
}