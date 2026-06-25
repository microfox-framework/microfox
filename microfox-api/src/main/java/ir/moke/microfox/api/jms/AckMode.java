package ir.moke.microfox.api.jms;

public enum AckMode {
    AUTO_ACKNOWLEDGE (1),
    CLIENT_ACKNOWLEDGE (2),
    DUPS_OK_ACKNOWLEDGE (3),
    SESSION_TRANSACTED (0);
    private final int mode ;

    AckMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
