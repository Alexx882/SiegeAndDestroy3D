package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

public class User {
    private String id;
    private String ip;
    private String name;
    private BattleArea area;

    public User(String uId, String uIp, String uName)
    {
        id=uId;
        ip=uIp;
        name=uName;
        area=null;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BattleArea getArea() {
        return area;
    }

    public void setArea(BattleArea area) {
        this.area = area;
    }
}
