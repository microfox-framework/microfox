package ir.moke.microfox.ftp;

import ir.moke.microfox.api.ftp.FtpProvider;
import ir.moke.microfox.api.ftp.MicroFoxFtpConfig;
import org.apache.commons.net.ftp.FTPFile;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class FtpProviderImpl implements FtpProvider {

    @Override
    public void ftpDownload(MicroFoxFtpConfig config, Path remoteFilePath, Path localDownloadDir) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        ftpClient.ftpDownload(remoteFilePath, localDownloadDir);
        ftpClient.disconnect();
    }

    @Override
    public void ftpBatchDownload(MicroFoxFtpConfig config, List<Path> remoteFilePath, Path localDownloadDir) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        ftpClient.ftpBatchDownload(remoteFilePath, localDownloadDir);
        ftpClient.disconnect();
    }

    @Override
    public void ftpUpload(MicroFoxFtpConfig config, Path remoteFilePath, Path file) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        ftpClient.ftpUpload(remoteFilePath, file);
    }

    @Override
    public void ftpBatchUpload(MicroFoxFtpConfig config, Path remoteFilePath, List<Path> files) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        ftpClient.ftpBatchUpload(remoteFilePath, files);
        ftpClient.disconnect();
    }

    @Override
    public void ftpDelete(MicroFoxFtpConfig config, Path remoteFilePath) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        ftpClient.delete(remoteFilePath);
        ftpClient.disconnect();
    }

    @Override
    public void batchDelete(MicroFoxFtpConfig config, List<Path> remoteFilePath) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
            remoteFilePath.forEach(ftpClient::delete);
        }
        ftpClient.disconnect();
    }

    @Override
    public void ftpList(MicroFoxFtpConfig config, Path remoteFilePath, Consumer<FTPFile[]> consumer) {
        FtpClient ftpClient = new FtpClient();
        ftpClient.connect(config);
        ftpClient.login(config);
        FTPFile[] ftpFiles = ftpClient.listFiles(remoteFilePath);
        consumer.accept(ftpFiles);
        ftpClient.disconnect();
    }
}
