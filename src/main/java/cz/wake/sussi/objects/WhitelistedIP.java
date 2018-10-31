package cz.wake.sussi.objects;

public class WhitelistedIP {

    // IP (String)
    private String address;

    // Description
    private String description;

    public WhitelistedIP(String address, String description){
        this.address = address;
        this.description = description;
    }

    public String getAddress(){
        return address;
    }

    public String getDescription(){
        return description;
    }
}
