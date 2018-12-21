package cz.wake.sussi.objects;

public class ConnectTask {

    private String userID, code;
    private Long expire;

    public ConnectTask(String user, String code, Long expire) {
        this.userID = user;
        this.code = code;
        this.expire = expire;
    }

    public Long getExpire() {
        return expire;
    }

    public String getCode() {
        return code;
    }

    public String getUserID() {
        return userID;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
