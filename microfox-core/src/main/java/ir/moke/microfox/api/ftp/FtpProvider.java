package ir.moke.microfox.api.ftp;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface FtpProvider {
    void ftpDownload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, Path localDownloadDir);

    void ftpBatchDownload(MicroFoxFtpConfig microFoxFtpConfig, List<String> remoteFilePath, Path localDownloadDir);

    void ftpUpload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, File file);

    void ftpBatchUpload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, List<File> files);

    void ftpDelete(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath);
    void batchDelete(MicroFoxFtpConfig microFoxFtpConfig, List<String> remoteFilePath);

    void ftpList(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, Consumer<List<FtpFile>> consumer);


}
