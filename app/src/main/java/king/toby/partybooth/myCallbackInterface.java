package king.toby.partybooth;

import king.toby.partybooth.models.Party;

public interface myCallbackInterface {
    void onCallback(String value);
    void onCallback(Party party);
}
