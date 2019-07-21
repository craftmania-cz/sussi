package cz.wake.sussi.objects;

public class WhitelistedNick {

    // UUID (String)
    private String nick;

    // Description
    private String description;

    public WhitelistedNick(String nick, String description){
        this.nick = nick;
        this.description = description;
    }

    public String getNick(){
        return nick;
    }

    public String getDescription(){
        return description;
    }
}
