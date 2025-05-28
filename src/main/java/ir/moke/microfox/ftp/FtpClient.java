package ir.moke.microfox.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FtpClient {
    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);
    private final FTPClient ftpClient = new FTPClient();

    public void connect(String host) {
        try {
            ftpClient.connect(host);
        } catch (IOException e) {
            logger.error("Failed connect to ftp server {}", host, e);
            throw new RuntimeException(e);
        }
    }

    public void login(String username, String password) {
        try {
            ftpClient.login(username, password);
        } catch (IOException e) {
            logger.error("Login failed, username: {}", username, e);
            throw new RuntimeException(e);
        }
    }

    public void anonymous() {
        try {
            ftpClient.login("anonymous", "");
        } catch (IOException e) {
            logger.error("Login failed, username: anonymous", e);
            throw new RuntimeException(e);
        }
    }

    public boolean download(String downloadFilePath, File localFile) throws IOException {
        try {
            // Set binary transfer mode
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            // Download a file from FTP server
            try (OutputStream outputStream = new FileOutputStream(localFile)) {
                return ftpClient.retrieveFile(downloadFilePath, outputStream);
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public void upload(String remoteFilePath, InputStream localInputStream) throws IOException {
        ftpClient.storeFile(remoteFilePath, localInputStream);
    }

    public void delete(String remoteFilePath) throws IOException {
        ftpClient.deleteFile(remoteFilePath);
    }

    public FTPFile[] listFiles(String remotePath) throws IOException {
        return ftpClient.listFiles(remotePath);
    }

    public FTPFile[] listDirectories(String remotePath) throws IOException {
        return ftpClient.listDirectories(remotePath);
    }
}
