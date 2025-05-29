package ir.moke.microfox.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FtpClient {
    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);
    private final FTPClient ftpClient = new FTPClient();

    public void connect(String host, int port) {
        try {
            ftpClient.connect(host, port);
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

    public boolean ftpDownload(String downloadFile, Path downloadDir) throws IOException {
        try {
            if (Files.isRegularFile(downloadDir)) throw new RuntimeException("%s is not directory".formatted(downloadDir));
            // Set binary transfer mode
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            // Download a file from FTP server
            try (OutputStream outputStream = new FileOutputStream(downloadDir.resolve(downloadFile).toFile())) {
                return ftpClient.retrieveFile(downloadFile, outputStream);
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public void ftpBatchDownload(List<String> listFilePaths, Path downloadDir) throws IOException {
        try {
            if (Files.isRegularFile(downloadDir)) throw new RuntimeException("%s is not directory".formatted(downloadDir));
            // Set binary transfer mode
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            // Download a file from FTP server
            for (String downloadFile : listFilePaths) {
                Path file = Path.of(downloadFile);
                try (OutputStream outputStream = new FileOutputStream(downloadDir.resolve(file.getFileName()).toFile())) {
                    ftpClient.retrieveFile(downloadFile, outputStream);
                }
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public void ftpUpload(String remotePath, File file) throws IOException {
        try {
            if (file.isDirectory()) return;
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile(remotePath, new FileInputStream(file));
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public void ftpBatchUpload(String remotePath, List<File> files) throws IOException {
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            for (File file : files) {
                if (!file.isDirectory()) {
                    ftpClient.storeFile(remotePath, new FileInputStream(file));
                }
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public void delete(String remoteFilePath) throws IOException {
        try {
            ftpClient.deleteFile(remoteFilePath);
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public FTPFile[] listFiles(String remotePath) throws IOException {
        try {
            return ftpClient.listFiles(remotePath);
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

    public FTPFile[] listDirectories(String remotePath) throws IOException {
        try {
            return ftpClient.listDirectories(remotePath);
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }
    }

}
