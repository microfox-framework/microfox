package ir.moke.microfox.ftp;

import ir.moke.microfox.api.ftp.MicroFoxFtpConfig;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class FtpClient {
    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);
    private final FTPClient ftpClient = new FTPClient();

    public void connect(MicroFoxFtpConfig config) {
        try {
            ftpClient.connect(config.host(), config.port());
            if (config.dataTransferPortRangeMin() != null && config.dataTransferPortRangeMax() != null) {
                ftpClient.setActivePortRange(config.dataTransferPortRangeMin(), config.dataTransferPortRangeMax());
                ftpClient.enterLocalActiveMode();
            }
        } catch (IOException e) {
            throw new MicroFoxException(e);
        }
    }

    public void login(MicroFoxFtpConfig config) {
        try {
            ftpClient.login(config.username(), config.password());
        } catch (IOException e) {
            throw new MicroFoxException(e);
        }
    }

    public void ftpDownload(Path downloadFile, Path targetDir) {
        try {
            if (Files.isRegularFile(targetDir))
                throw new RuntimeException("%s is not directory".formatted(targetDir));
            // Set binary transfer mode
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(downloadFile.getParent().toString());
            // Download a file from FTP server
            try (OutputStream outputStream = new FileOutputStream(targetDir.resolve(downloadFile.getFileName()).toFile())) {
                boolean done = ftpClient.retrieveFile(downloadFile.toString(), outputStream);
                if (!done) {
                    logger.warn("download failed for {}", downloadFile);
                }
            }
        } catch (IOException e) {
            logger.error("ftp error", e);
        }
    }

    public void ftpBatchDownload(List<Path> listFilePaths, Path targetDir) {
        try {
            if (Files.isRegularFile(targetDir))
                throw new RuntimeException("%s is not directory".formatted(targetDir));
            // Set binary transfer mode
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            // Download a file from FTP server
            for (Path downloadFile : listFilePaths) {
                File file = downloadFile.toFile();
                ftpClient.changeWorkingDirectory(file.getParent());
                boolean exists = exists(downloadFile);
                if (exists) {
                    try (OutputStream outputStream = new FileOutputStream(targetDir.resolve(downloadFile.getFileName()).toFile())) {
                        boolean done = ftpClient.retrieveFile(downloadFile.toString(), outputStream);
                        if (!done) {
                            logger.warn("download failed for {}", downloadFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("ftp error", e);
        }
    }

    public void ftpUpload(Path remoteDir, Path file) {
        try {
            if (file.toFile().isDirectory()) return;
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            boolean done = ftpClient.storeFile(remoteDir.resolve(file.getFileName()).toString(), new FileInputStream(file.toFile()));
            if (!done) {
                logger.warn("Upload failed for {}", file);
            }
        } catch (IOException e) {
            logger.error("ftp error", e);
        }
    }

    public void ftpBatchUpload(Path remoteDir, List<Path> files) {
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            for (Path file : files) {
                if (!file.toFile().isDirectory()) {
                    boolean done = ftpClient.storeFile(remoteDir.resolve(file.getFileName()).toString(), new FileInputStream(file.toFile()));
                    if (!done) {
                        logger.warn("Upload failed for {}", file);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("ftp error", e);
        }
    }

    public void delete(Path remoteFilePath) {
        try {
            ftpClient.deleteFile(remoteFilePath.toString());
        } catch (IOException e) {
            logger.error("ftp error", e);
        }
    }

    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            logger.error("ftp error", e);
        }
    }

    private boolean exists(Path remoteFilePath) {
        FTPFile[] ftpFiles = listFiles(remoteFilePath);
        if (ftpFiles.length == 0) return false;
        return Arrays.stream(ftpFiles).anyMatch(item -> item.getName().equals(remoteFilePath.getFileName().toString()));
    }

    public FTPFile[] listFiles(Path remotePath) {
        try {
            return ftpClient.listFiles(remotePath.toString());
        } catch (IOException e) {
            logger.error("ftp error", e);
        }
        return new FTPFile[0];
    }
}
