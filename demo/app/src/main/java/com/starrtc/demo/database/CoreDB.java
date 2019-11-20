package com.starrtc.demo.database;

import android.database.Cursor;

import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.CoreDBManager;
import com.starrtc.demo.utils.IEventListener;

import java.util.ArrayList;
import java.util.List;

public class CoreDB implements IEventListener {
    public static String APP_DB_PATH = "/data/data/com.starrtc.demo/";
    private final String TEXTTAG = "CoreDB";
    private static final String HISTORY_TABLE = "historyListTable";
    private static final String MSG_TABLE = "allMsgTable";

    public static final String HISTORY_TYPE_VOIP = "voip";
    public static final String HISTORY_TYPE_C2C = "c2c";
    public static final String HISTORY_TYPE_GROUP = "group";



    public static CoreDBManager coreDBM = new CoreDBManager();

    public CoreDB(){
        AEvent.removeListener(AEvent.AEVENT_RESET,this);
        AEvent.addListener(AEvent.AEVENT_RESET,this);
        MLOC.d(TEXTTAG,"reset DB:"+MLOC.userId);
        coreDBM.initCoreDB(APP_DB_PATH +"databases/", MLOC.userId);
        //历史表
        coreDBM.execSQL("create table if not exists "+HISTORY_TABLE+"(" +
                "id INTEGER PRIMARY KEY," +
                "type TEXT ," +
                "conversationId TEXT ," +
                "newMsg INTEGER," +
                "groupName TEXT," +
                "groupCreaterId TEXT," +
                "lastMsg TEXT," +
                "lastTime TEXT)");
        //消息表
        coreDBM.execSQL("create table if not exists "+MSG_TABLE+"(" +
                "id INTEGER PRIMARY KEY," +
                "conversationId TEXT," +
                "fromId TEXT," +
                "atId TEXT," +
                "msg TEXT," +
                "time TEXT)");

    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {

        switch (aEventID) {
            case AEvent.AEVENT_RESET : {
                if(coreDBM != null){
                    coreDBM.close();
                }
                break;
            }
        }
    }

    public List<HistoryBean> getHistory(String type){
        Cursor cursor = coreDBM.rawQuery("select * from "+HISTORY_TABLE+" where type=? order by id desc",new String[]{type});
        List<HistoryBean> list = new ArrayList<HistoryBean>();
        while (cursor != null && cursor.moveToNext()) {
            HistoryBean bean = new HistoryBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bean.setConversationId(cursor.getString(cursor.getColumnIndex("conversationId")));
            bean.setNewMsgCount(cursor.getInt(cursor.getColumnIndex("newMsg")));
            bean.setLastMsg(cursor.getString(cursor.getColumnIndex("lastMsg")));
            bean.setLastTime(cursor.getString(cursor.getColumnIndex("lastTime")));
            bean.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
            bean.setGroupCreaterId(cursor.getString(cursor.getColumnIndex("groupCreaterId")));
            bean.setType(type);
            list.add(bean);
        }
        if (cursor != null) cursor.close();
        return list;
    }

    public void updateHistory(HistoryBean historyBean){
        if(historyBean.getConversationId()==null||historyBean.getType()==null)return;
        Cursor cursor = coreDBM.rawQuery("select * from " + HISTORY_TABLE + " where type=? and conversationId=?",
                new String[]{historyBean.getType(), historyBean.getConversationId()});
        if(cursor!=null&&cursor.moveToNext()){
            if (cursor != null) cursor.close();
            coreDBM.execSQL("UPDATE "+HISTORY_TABLE+" SET newMsg = ?," +
                            " lastMsg = ?," +
                            " lastTime = ?" +
                            " where type=? and conversationId=?",
                    new Object[]{
                            historyBean.getNewMsgCount(), historyBean.getLastMsg(),
                            historyBean.getLastTime(),
                            historyBean.getType(), historyBean.getConversationId()});
        }
    }

    public void addHistory(HistoryBean historyBean, Boolean hasRead){
        if(historyBean.getConversationId()==null||historyBean.getType()==null)return;
        Cursor cursor = coreDBM.rawQuery("select * from " + HISTORY_TABLE + " where type=? and conversationId=?",
                new String[]{historyBean.getType(), historyBean.getConversationId()});
        if(cursor!=null&&cursor.moveToNext()){
            historyBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            if(!hasRead){
                historyBean.setNewMsgCount(cursor.getInt(cursor.getColumnIndex("newMsg"))+1);
            }else{
                historyBean.setNewMsgCount(0);
            }
            if (cursor != null) cursor.close();
            coreDBM.execSQL("UPDATE "+HISTORY_TABLE+" SET newMsg = ?," +
                    " lastMsg = ?," +
                    " lastTime = ?" +
                    " where type=? and conversationId=?",
                    new Object[]{
                            historyBean.getNewMsgCount(), historyBean.getLastMsg(),
                            historyBean.getLastTime(),
                            historyBean.getType(), historyBean.getConversationId()});
        }else{
            if(hasRead){
                historyBean.setNewMsgCount(0);
            }
            coreDBM.execSQL("insert into " + HISTORY_TABLE + "(type,conversationId,newMsg,lastMsg,lastTime,groupName,groupCreaterId) values(?,?,?,?,?,?,?)",
                    new Object[]{historyBean.getType(), historyBean.getConversationId(),
                            historyBean.getNewMsgCount(), historyBean.getLastMsg(),
                            historyBean.getLastTime(),historyBean.getGroupName(),
                            historyBean.getGroupCreaterId()});
        }

    }

    public void removeHistory(HistoryBean historyBean){
        if(historyBean.getConversationId()==null||historyBean.getType()==null)return;
        coreDBM.rawQuery("delete from " + HISTORY_TABLE + " where type=? and conversationId=?",
                new String[]{historyBean.getType(), historyBean.getConversationId()});
        coreDBM.rawQuery("delete from " + MSG_TABLE + " where conversationId=?", new String[]{historyBean.getConversationId()});
    }

    public List<MessageBean> getMessageList(String conversationId){
        List<MessageBean> list = new ArrayList<>();
        Cursor cursor = coreDBM.rawQuery("select * from " + MSG_TABLE + " where conversationId=? order by id desc limit 5", new String[]{conversationId});
        while (cursor!=null&&cursor.moveToNext()){
            MessageBean bean = new MessageBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bean.setConversationId(cursor.getString(cursor.getColumnIndex("conversationId")));
            bean.setFromId(cursor.getString(cursor.getColumnIndex("fromId")));
            bean.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
            bean.setTime(cursor.getString(cursor.getColumnIndex("time")));
            list.add(0,bean);
        }
        if (cursor != null) cursor.close();
        return list;
    }
    public void setMessage(MessageBean messageBean){
        coreDBM.execSQL("insert into "+MSG_TABLE+" (conversationId,fromId,msg,time) values(?,?,?,?)",
                new Object[]{messageBean.getConversationId(),messageBean.getFromId(),
                        messageBean.getMsg(),messageBean.getTime()});
    }

}
