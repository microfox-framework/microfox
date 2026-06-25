package ir.moke.microfox.api.ftp;

public record MicroFoxSftpConfig(String host,
                                 Integer port,
                                 String username,
                                 String password,
                                 String knownHostsFile,
                                 boolean strictHostKeyChecking) {
    public MicroFoxSftpConfig(String host, Integer port, String username, String password) {
        this(host, port, username, password, null, false);
    }

    public MicroFoxSftpConfig(String host, String username, String password) {
        this(host, 22, username, password, "", false);
    }
}
