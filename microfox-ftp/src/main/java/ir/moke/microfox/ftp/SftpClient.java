package ir.moke.microfox.ftp;

import com.jcraft.jsch.*;
import ir.moke.microfox.api.ftp.MicroFoxSftpConfig;
import ir.moke.microfox.api.ftp.SftpMode;
import ir.moke.microfox.exception.MicroFoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

public class SftpClient {
    private static final Logger logger = LoggerFactory.getLogger(SftpClient.class);
    private ChannelSftp channel;
    private final MicroFoxSftpConfig config;

    public SftpClient(MicroFoxSftpConfig config) {
        this.config = config;
    }

    public void connect() {

        try {
            final JSch jsch = new JSch();
            if (config.knownHostsFile() != null) jsch.setKnownHosts(config.knownHostsFile());
            Session jschSession = jsch.getSession(config.username(), config.host(), config.port());
            jschSession.setPassword(config.password());
            jschSession.setConfig("StrictHostKeyChecking", String.valueOf(config.strictHostKeyChecking()));
            jschSession.connect();
            this.channel = (ChannelSftp) jschSession.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            throw new MicroFoxException(e);
        }
    }

    public void sftpDownload(Path downloadFile, Path localDir) {
        try {
            if (Files.isRegularFile(localDir))
                throw new RuntimeException("%s is not directory".formatted(localDir));

            channel.get(downloadFile.toString(), localDir.toString());
        } catch (SftpException e) {
            logger.error("sftp error", e);
        }
    }

    public void sftpBatchDownload(List<Path> listFilePaths, Path localDir) {
        if (Files.isRegularFile(localDir))
            throw new RuntimeException("%s is not directory".formatted(localDir));

        // Download a file from FTP server
        for (Path downloadFile : listFilePaths) {
            boolean exists = exists(downloadFile);
            if (exists) {
                try (OutputStream outputStream = new FileOutputStream(localDir.resolve(downloadFile.getFileName()).toFile())) {
                    channel.get(downloadFile.toString(), outputStream);
                } catch (Exception e) {
                    logger.error("sftp error", e);
                }
            }
        }
    }

    public void ftpUpload(Path remoteDir, Path file, SftpMode mode) {
        try {
            if (file.toFile().isDirectory()) return;
            channel.cd(remoteDir.toString());
            channel.put(file.toString(), remoteDir.resolve(file.getFileName()).toString(), mode.getValue());
        } catch (SftpException e) {
            logger.error("sftp error", e);
        }
    }

    public void ftpBatchUpload(Path remoteDir, List<Path> files, SftpMode mode) {
        try {
            for (Path file : files) {
                if (!file.toFile().isDirectory()) {
                    channel.put(file.toString(), remoteDir.resolve(file.getFileName()).toString(), mode.getValue());
                }
            }
        } catch (SftpException e) {
            logger.error("sftp error", e);
        }
    }

    public void delete(Path remoteFilePath) {
        try {
            channel.rm(remoteFilePath.toString());
        } catch (SftpException e) {
            logger.error("sftp error", e);
        }
    }

    public void disconnect() {
        if (channel != null && channel.isConnected()) channel.disconnect();
    }

    private boolean exists(Path remoteFilePath) {
        Vector<ChannelSftp.LsEntry> vector = listFiles(remoteFilePath);
        if (vector.isEmpty()) return false;
        return vector.stream()
                .map(ChannelSftp.LsEntry::getFilename)
                .anyMatch(item -> remoteFilePath.getFileName().toString().equals(item));
    }

    public Vector<ChannelSftp.LsEntry> listFiles(Path remotePath) {
        try {
            return channel.ls(remotePath.toString());
        } catch (SftpException e) {
            logger.error("sftp error", e);
        }
        return null;
    }
}
