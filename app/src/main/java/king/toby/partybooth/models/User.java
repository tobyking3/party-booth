package king.toby.partybooth.models;

public class User {

    private String display_name;
    private String party_id;

    public User(){}

    public User(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    @Override
    public String toString() {
        return "User{" + ", display_name='" + display_name + '\'' + '}';
    }

    public String getParty_id() {
        return party_id;
    }

    public void setParty_id(String party_id) {
        this.party_id = party_id;
    }
}
