package ir.archroid.ponyexpress.Model;

public class Chat {

    private String lastMessage;
    private String lastSeen;
    private long lastTime;
    private String lastSender;
    private int notSeenMSG;
    private String typing;

    public Chat(String lastMessage, String lastSeen, long lastTime, String lastSender, int notSeenMSG, String typing) {
        this.lastMessage = lastMessage;
        this.lastSeen = lastSeen;
        this.lastTime = lastTime;
        this.lastSender = lastSender;
        this.notSeenMSG = notSeenMSG;
        this.typing = typing;
    }

    public Chat() {
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public int getNotSeenMSG() {
        return notSeenMSG;
    }

    public void setNotSeenMSG(int notSeenMSG) {
        this.notSeenMSG = notSeenMSG;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }
}
