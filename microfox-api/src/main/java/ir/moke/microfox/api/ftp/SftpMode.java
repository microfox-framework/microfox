package ir.moke.microfox.api.ftp;

public enum SftpMode {
    OVERWRITE(0),
    RESUME(1),
    APPEND(2);

    private final int value;

    SftpMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
