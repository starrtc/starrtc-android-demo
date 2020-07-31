package com.starrtc.demo.demo.im.group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.im.c2c.C2CActivity;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.demo.voip.VoipActivity;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHGroupManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageGroupSettingActivity extends BaseActivity{

    private XHGroupManager groupManager;

    private String mGroupId;
    private String mGroupCreaterId;
    private RecyclerView vRecyclerView;
    private MyAdapter myAdapter;
    private List<Map<String,String>> mMembersDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group_setting);

        groupManager = XHClient.getInstance().getGroupManager();

        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.switch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.switch_btn).isSelected()) {
                    findViewById(R.id.switch_btn).setSelected(false);
                    groupManager.setPushEnable(mGroupId, true, new IXHResultCallback() {
                        @Override
                        public void success(Object data) {
                            MLOC.showMsg(MessageGroupSettingActivity.this,"设置成功");
                        }

                        @Override
                        public void failed(String errMsg) {
                            MLOC.showMsg(MessageGroupSettingActivity.this,"设置失败");
                        }
                    });
                } else {
                    findViewById(R.id.switch_btn).setSelected(true);
                    groupManager.setPushEnable(mGroupId, false, new IXHResultCallback() {
                        @Override
                        public void success(Object data) {
                            MLOC.showMsg(MessageGroupSettingActivity.this, "设置成功");
                        }

                        @Override
                        public void failed(String errMsg) {
                            MLOC.showMsg(MessageGroupSettingActivity.this, "设置失败");
                        }

                    });
                }
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MessageGroupSettingActivity.this).setCancelable(true)
                        .setTitle("是否要删除群?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteGroup();
                            }
                        }
                ).show();
            }
        });
        ((TextView)findViewById(R.id.title_text)).setText("群组信息");
        vRecyclerView = (RecyclerView) findViewById(R.id.recycler_list);
        myAdapter = new MyAdapter(this);
        mMembersDatas = new ArrayList<>();
        vRecyclerView.setAdapter(myAdapter);
        vRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(5,StaggeredGridLayoutManager.VERTICAL));
        mGroupId = getIntent().getStringExtra("groupId");
        mGroupCreaterId = getIntent().getStringExtra("creator");
        queryGroupMemberList();
    }

    private void queryGroupMemberList(){
        if(MLOC.AEventCenterEnable){
            InterfaceUrls.demoQueryImGroupInfo(MLOC.userId,mGroupId);
        }else{
            groupManager.queryGroupInfo(mGroupId, new IXHResultCallback() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void success(final Object data) {
                    MLOC.d("IM_GROUP","applyGetUserList success:"+data);
                    try {
                        JSONArray datas = ((JSONObject) data).getJSONArray("data");
                        int ignore = ((JSONObject) data).getInt("ignore");
                        findViewById(R.id.switch_btn).setSelected(ignore==1?true:false);
                        ArrayList<String> res = new ArrayList<String>();
                        for (int i = 0;i<datas.length();i++){
                            String uid = datas.getJSONObject(i).getString("userId");
                            res.add(uid);
                        }

                        mMembersDatas.clear();
                        for(int i=0;i<res.size();i++){
                            Map<String,String> user = new HashMap<>();
                            user.put("userId",res.get(i));
                            mMembersDatas.add(user);
                        }
                        if(mGroupCreaterId.equals(MLOC.userId)){
                            Map<String,String> user = new HashMap<>();
                            user.put("userId","btnAdd");
                            mMembersDatas.add(user);
                        }
                        myAdapter.notifyDataSetChanged();

                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void failed(String errMsg) {
                    MLOC.d("IM_GROUP","applyGetUserList failed:"+errMsg);
                }
            });
        }
    }

    @Override
    public  void onResume(){
        super.onResume();
        ((TextView)findViewById(R.id.group_id)).setText(mGroupId);
        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
    }

    @Override
    public  void onPause(){
        super.onPause();
        AEvent.removeListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID){
            case AEvent.AEVENT_GROUP_GOT_MEMBER_LIST:
                try {
                    String[] userIdList = ((JSONObject) eventObj).getString("userIdList").split(",");
                    int ignore = ((JSONObject) eventObj).getInt("isIgnore");
                    findViewById(R.id.switch_btn).setSelected(ignore==1?true:false);


                    mMembersDatas.clear();
                    for(int i=0;i<userIdList.length;i++){
                        Map<String,String> user = new HashMap<>();
                        user.put("userId",userIdList[i]);
                        mMembersDatas.add(user);
                    }
                    if(mGroupCreaterId.equals(MLOC.userId)){
                        Map<String,String> user = new HashMap<>();
                        user.put("userId","btnAdd");
                        mMembersDatas.add(user);
                    }
                    myAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }
    private void addUserToGroup(String addUserId){
        ArrayList<String> idList = new ArrayList<>();
        idList.add(addUserId);
        groupManager.addGroupMembers(mGroupId, idList, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.showMsg(MessageGroupSettingActivity.this, "成员添加成功");
                queryGroupMemberList();
            }

            @Override
            public void failed(String errMsg) {
                MLOC.showMsg(MessageGroupSettingActivity.this, "成员添加失败");
            }
        });
    }

    private void deleteUserFormGroup(String delUserId){
        ArrayList<String> idList = new ArrayList<>();
        idList.add(delUserId);
        groupManager.deleteGroupMembers(mGroupId, idList, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.showMsg(MessageGroupSettingActivity.this, "成员删除成功");
                queryGroupMemberList();
            }

            @Override
            public void failed(String errMsg) {
                MLOC.showMsg(MessageGroupSettingActivity.this, "成员删除失败");
            }
        });
    }

    private void deleteGroup(){
        groupManager.deleteGroup(mGroupId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.showMsg(MessageGroupSettingActivity.this, "群删除成功");
                MLOC.deleteGroup = true;
                finish();
            }

            @Override
            public void failed(String errMsg) {
                MLOC.showMsg(MessageGroupSettingActivity.this, "群删除失败");
            }
        });
    }

    private void showAddDialog(){
        final Dialog dialog = new Dialog(this,R.style.dialog_popup);
        dialog.setContentView(R.layout.dialog_group_add_user);
        Window win = dialog.getWindow();
        win.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addUser = ((EditText)dialog.findViewById(R.id.add_user_id)).getText().toString();
                if(TextUtils.isEmpty(addUser)){
                    MLOC.showMsg(MessageGroupSettingActivity.this,"用户ID不能为空");
                }else{
                    addUserToGroup(addUser);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void showManagerDialog(final String userId) {
        if(userId.equals(MLOC.userId))return;
        if(mGroupCreaterId.equals(MLOC.userId)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            final String[] Items={"视频通话","发消息","踢出群","取消"};
            builder.setTitle(userId);
            builder.setItems(Items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        Intent intent = new Intent(MessageGroupSettingActivity.this,VoipActivity.class);
                        intent.putExtra("targetId",userId);
                        intent.putExtra(VoipActivity.ACTION,VoipActivity.CALLING);
                        startActivity(intent);
                    }else if(i==1){
                        Intent intent = new Intent(MessageGroupSettingActivity.this,C2CActivity.class);
                        intent.putExtra("targetId",userId);
                        startActivity(intent);
                    }else if(i==2){
                        deleteUserFormGroup(userId);
                    }
                }
            });
            builder.setCancelable(true);
            AlertDialog dialog=builder.create();
            dialog.show();
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            final String[] Items={"视频通话","发消息","取消"};
            builder.setTitle(userId);
            builder.setItems(Items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        MLOC.saveVoipUserId(MessageGroupSettingActivity.this,userId);
                        Intent intent = new Intent(MessageGroupSettingActivity.this,VoipActivity.class);
                        intent.putExtra("targetId",userId);
                        intent.putExtra(VoipActivity.ACTION,VoipActivity.CALLING);
                        startActivity(intent);
                    }else if(i==1){
                        MLOC.saveC2CUserId(MessageGroupSettingActivity.this,userId);
                        Intent intent = new Intent(MessageGroupSettingActivity.this,C2CActivity.class);
                        intent.putExtra("targetId",userId);
                        startActivity(intent);
                    }
                }
            });
            builder.setCancelable(true);
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        Context context;

        public MyAdapter(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyAdapter.MyViewHolder holder = new MyAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group_member, parent, false));
            return holder;
        }

        @Override
        public int getItemCount ()
        {
            if (mMembersDatas == null)
                return 0;
            return mMembersDatas.size();
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
            Map item = mMembersDatas.get(position);
            final String id = (String) item.get("userId");
            if(id.equals("btnAdd")){
                holder.vUserId.setText("");
                holder.vHeadBg.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
                int cint = DensityUtils.dip2px(MessageGroupSettingActivity.this,26);
                holder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                holder.vHeadImage.setImageResource(R.drawable.menu_icon_add_gray);
                holder.vHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddDialog();
                    }
                });
            }else{
                holder.vUserId.setText(id);
                holder.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupSettingActivity.this,id));
                holder.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
                int cint = DensityUtils.dip2px(MessageGroupSettingActivity.this,26);
                holder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                holder.vHeadImage.setImageResource(MLOC.getHeadImage(MessageGroupSettingActivity.this,id));
                holder.vHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showManagerDialog(id);
                    }
                });
            }
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView vUserId;
            public View vHeadBg;
            public CircularCoverView vHeadCover;
            public ImageView vHeadImage;

            public MyViewHolder(View view) {
                super(view);
                vUserId = (TextView) view.findViewById(R.id.item_id);
                vHeadBg = view.findViewById(R.id.head_bg);
                vHeadCover = (CircularCoverView) view.findViewById(R.id.head_cover);
                vHeadImage = (ImageView) view.findViewById(R.id.head_img);
            }
        }
    }
}
