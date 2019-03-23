package king.toby.partybooth.models;

public class Party {
    private String partyName;

    public Party(String partyName) {
        this.partyName = partyName;
    }

    public Party(){}

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    @Override
    public String toString() {
        return "Party{" + ", party_id='" + partyName + '\'' + '}';
    }
}
