package king.toby.partybooth.models;

public class Party {

    private String partyName;

    private String partyDescription;

    public Party(String partyName, String partyDescription) {
        this.partyName = partyName;
        this.partyDescription = partyDescription;
    }

    public Party(){}


    public String getPartyName() {
        return partyName;
    }

    public String getPartyDescription() {
        return partyDescription;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setPartyDescription(String partyDescription) {
        this.partyDescription = partyDescription;
    }

    @Override
    public String toString() {
        return "Party{" +
                "partyName='" + partyName + '\'' +
                ", partyDescription='" + partyDescription + '\'' +
                '}';
    }
}
