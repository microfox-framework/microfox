package ir.moke.microfox.api.ibmmq;

public enum AcknowledgeType {
    AUTO_ACKNOWLEDGE(1),
    CLIENT_ACKNOWLEDGE(2),
    DUPS_OK_ACKNOWLEDGE(3),
    SESSION_TRANSACTED(0);

    private final int type;

    AcknowledgeType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
