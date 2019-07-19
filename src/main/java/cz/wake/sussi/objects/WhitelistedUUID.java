package cz.wake.sussi.objects;

public class WhitelistedUUID {

    // UUID (String)
    private String uuid;

    // Description
    private String description;

    public WhitelistedUUID(String uuid, String description){
        this.uuid = uuid;
        this.description = description;
    }

    public String getUUID(){
        return uuid;
    }

    public String getDescription(){
        return description;
    }
}
