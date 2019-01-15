package com.starrtc.demo.database;

public class HistoryBean {
    private int id = 0;
    private String type = "";
    private String conversationId = "";
    private int newMsgCount = 0;
    private String lastMsg = "";
    private String lastTime = "";
    private String groupName = "";
    private String groupCreaterId = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getNewMsgCount() {
        return newMsgCount;
    }

    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }


    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCreaterId() {
        return groupCreaterId;
    }

    public void setGroupCreaterId(String groupCreaterId) {
        this.groupCreaterId = groupCreaterId;
    }
}
