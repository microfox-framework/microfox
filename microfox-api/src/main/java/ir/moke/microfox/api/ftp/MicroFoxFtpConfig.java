package ir.moke.microfox.api.ftp;

public record MicroFoxFtpConfig(String host,
                                Integer port,
                                String username,
                                String password,
                                Integer dataTransferPortRangeMin,
                                Integer dataTransferPortRangeMax) {
    public MicroFoxFtpConfig(String host, Integer port, String username, String password) {
        this(host, port, username, password, null, null);
    }

    public MicroFoxFtpConfig(String host, String username, String password) {
        this(host, 21, username, password, null, null);
    }

    public MicroFoxFtpConfig(String host) {
        this(host, 21, "anonymous", "", null, null);
    }
}
