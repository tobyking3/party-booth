package king.toby.partybooth.models;

public class User {

    private String user_id;
    private String display_name;

    public User(String user_id, String display_name) {
        this.user_id = user_id;
        this.display_name = display_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", display_name='" + display_name + '\'' +
                '}';
    }
}
