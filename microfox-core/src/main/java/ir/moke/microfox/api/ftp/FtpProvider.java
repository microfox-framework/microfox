package ir.moke.microfox.api.ftp;

import org.apache.commons.net.ftp.FTPFile;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface FtpProvider {
    void ftpDownload(MicroFoxFtpConfig config, Path remoteFilePath, Path localDownloadDir);

    void ftpBatchDownload(MicroFoxFtpConfig config, List<Path> remoteFilePath, Path localDownloadDir);

    void ftpUpload(MicroFoxFtpConfig config, Path remoteFilePath, Path file);

    void ftpBatchUpload(MicroFoxFtpConfig config, Path remoteFilePath, List<Path> files);

    void ftpDelete(MicroFoxFtpConfig config, Path remoteFilePath);

    void batchDelete(MicroFoxFtpConfig config, List<Path> remoteFilePath);

    void ftpList(MicroFoxFtpConfig config, Path remoteFilePath, Consumer<FTPFile[]> consumer);


}
