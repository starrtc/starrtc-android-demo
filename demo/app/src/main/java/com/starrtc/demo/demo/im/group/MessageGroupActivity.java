package com.starrtc.demo.demo.im.group;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.database.CoreDB;
import com.starrtc.demo.database.HistoryBean;
import com.starrtc.demo.database.MessageBean;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.demo.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHGroupManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageGroupActivity extends Activity implements IEventListener, AdapterView.OnItemLongClickListener {
    public static String TYPE = "TYPE";
    public static String GROUP_NAME = "GROUP_NAME";
    public static String GROUP_ID = "GROUP_ID";
    public static String CREATER_ID = "CREATER_ID";

    private XHGroupManager groupManager;

    private EditText vEditText;
    private ListView vMsgList;
    private View vSendBtn;

    private String mGroupId;
    private String mGroupName;
    private String mCreaterId;
    private List<MessageBean> mDatas;
    private MyChatroomListAdapter mAdapter ;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        groupManager = XHClient.getInstance().getGroupManager();
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.title_right_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageGroupActivity.this,MessageGroupSettingActivity.class);
                intent.putExtra("groupId",mGroupId);
                intent.putExtra("creator",mCreaterId);
                startActivity(intent);
            }
        });
        addListener();


        type = getIntent().getStringExtra(TYPE);
        if(type.equals(GROUP_ID)){
            mGroupId = getIntent().getStringExtra(GROUP_ID);
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mCreaterId = getIntent().getStringExtra(CREATER_ID);
        }else if(type.equals(GROUP_NAME)){
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mCreaterId = MLOC.userId;
            groupManager.createGroup(mGroupName, new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    mGroupId = (String) data;
                    onResume();
                }

                @Override
                public void failed(final String errMsg) {
                    MLOC.showMsg(MessageGroupActivity.this,errMsg);
                    finish();
                }

            });
        }
        ((TextView)findViewById(R.id.title_text)).setText(mGroupName);
        vEditText = (EditText) findViewById(R.id.id_input);
        mDatas = new ArrayList<>();
        mAdapter = new MyChatroomListAdapter();

        vMsgList = (ListView) findViewById(R.id.msg_list);
        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        vMsgList.setOnItemLongClickListener(this);
//        vMsgList.setStackFromBottom(true);

        mAdapter = new MyChatroomListAdapter();
        vMsgList.setAdapter(mAdapter);

        vSendBtn = findViewById(R.id.send_btn);
        vSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = vEditText.getText().toString();
                if(!TextUtils.isEmpty(txt)){
                    sendMsg(txt);
                    vEditText.setText("");
                }
            }
        });
    }

    private void sendMsg(String msg){
        XHIMMessage imMessage = groupManager.sendMessage(mGroupId, new ArrayList<String>(), msg, null);

        HistoryBean historyBean = new HistoryBean();
        historyBean.setGroupName(mGroupName);
        historyBean.setGroupCreaterId(mCreaterId);
        historyBean.setType(CoreDB.HISTORY_TYPE_GROUP);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setLastMsg(imMessage.contentData);
        historyBean.setConversationId(imMessage.targetId);
        historyBean.setNewMsgCount(1);
        MLOC.addHistory(historyBean,true);

        MessageBean messageBean = new MessageBean();
        messageBean.setConversationId(imMessage.targetId);
        messageBean.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        messageBean.setMsg(imMessage.contentData);
        messageBean.setFromId(imMessage.fromId);
        MLOC.saveMessage(messageBean);

        mDatas.add(messageBean);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(MLOC.deleteGroup){
            MLOC.deleteGroup = false;
            finish();
        }

        if(mGroupId!=null){
            queryGroupMemberList();

            mDatas.clear();
            List<MessageBean> list =  MLOC.getMessageList(mGroupId);
            if(list!=null&&list.size()>0){
                mDatas.addAll(list);
            }
            mAdapter.notifyDataSetChanged();
        }
    }



    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
        super.onStop();
    }


    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        MLOC.d("IM_GROUP",aEventID+"||"+eventObj);
        switch (aEventID){
            case AEvent.AEVENT_GROUP_REV_MSG:
                final XHIMMessage revMsg = (XHIMMessage) eventObj;
                if(revMsg.targetId.equals(mGroupId)){
                    HistoryBean historyBean = new HistoryBean();
                    historyBean.setType(CoreDB.HISTORY_TYPE_GROUP);
                    historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
                    historyBean.setLastMsg(revMsg.contentData);
                    historyBean.setConversationId(revMsg.targetId);
                    historyBean.setNewMsgCount(1);
                    MLOC.addHistory(historyBean,true);

                    MessageBean messageBean = new MessageBean();
                    messageBean.setConversationId(revMsg.fromId);
                    messageBean.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
                    messageBean.setMsg(revMsg.contentData);
                    messageBean.setFromId(revMsg.fromId);

                    mDatas.add(messageBean);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case AEvent.AEVENT_GROUP_GOT_MEMBER_LIST:
                if(!success){
                    finish();
                    MLOC.showMsg(MessageGroupActivity.this,"群信息获取失败");
                }
                break;
        }
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
                        ArrayList<String> res = new ArrayList<String>();
                        for (int i = 0;i<datas.length();i++){
                            String uid = datas.getJSONObject(i).getString("userId");
                            res.add(uid);
                        }
                        AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,true,res);
                        return;
                    } catch (JSONException e) {
                        AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,false,"数据解析失败");
                        e.printStackTrace();
                    }
                    return;
            }

                @Override
                public void failed(String errMsg) {
                    MLOC.d("IM_GROUP","applyGetUserList failed:"+errMsg);
                }
            });
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(mDatas.get(position).getMsg());
        Toast.makeText(this,"消息已复制",Toast.LENGTH_LONG).show();;
        return false;
    }

    public class MyChatroomListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyChatroomListAdapter(){
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            if(mDatas ==null) return 0;
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            if(mDatas ==null)
                return null;
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mDatas ==null)
                return 0;
            return position;
        }

        @Override
        public int getViewTypeCount(){
            return 2;
        }

        @Override
        public int getItemViewType(int position){
            return mDatas.get(position).getFromId().equals(MLOC.userId)?0:1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int currLayoutType = getItemViewType(position);
            if(currLayoutType == 0){ //自己的信息
                final ViewHolder itemSelfHolder;
                if(convertView == null){
                    itemSelfHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_chat_msg_list_right,null);
                    itemSelfHolder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                    itemSelfHolder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                    itemSelfHolder.vHeadBg = convertView.findViewById(R.id.head_bg);
                    itemSelfHolder.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                    itemSelfHolder.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                    convertView.setTag(itemSelfHolder);
                }else{
                    itemSelfHolder = (ViewHolder)convertView.getTag();
                }
                itemSelfHolder.vUserId.setText(mDatas.get(position).getFromId());
                itemSelfHolder.vMsg.setText(mDatas.get(position).getMsg());
                itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupActivity.this,mDatas.get(position).getFromId()));
                itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                int cint = DensityUtils.dip2px(MessageGroupActivity.this,20);
                itemSelfHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                itemSelfHolder.vHeadImage.setImageResource(MLOC.getHeadImage(MessageGroupActivity.this,mDatas.get(position).getFromId()));
            }else if(currLayoutType == 1){//别人的信息
                final ViewHolder itemOtherHolder;
                if(convertView == null){
                    itemOtherHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_chat_msg_list_left,null);
                    itemOtherHolder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                    itemOtherHolder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                    itemOtherHolder.vHeadBg = convertView.findViewById(R.id.head_bg);
                    itemOtherHolder.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                    itemOtherHolder.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);

                    convertView.setTag(itemOtherHolder);
                }else{
                    itemOtherHolder = (ViewHolder)convertView.getTag();
                }
                itemOtherHolder.vUserId.setText(mDatas.get(position).getFromId());
                itemOtherHolder.vMsg.setText(mDatas.get(position).getMsg());
                itemOtherHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupActivity.this,mDatas.get(position).getFromId()));
                itemOtherHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                int cint = DensityUtils.dip2px(MessageGroupActivity.this,20);
                itemOtherHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                itemOtherHolder.vHeadImage.setImageResource(MLOC.getHeadImage(MessageGroupActivity.this,mDatas.get(position).getFromId()));
            }
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public TextView vMsg;
        public View vHeadBg;
        public CircularCoverView vHeadCover;
        public ImageView vHeadImage;
    }

}
