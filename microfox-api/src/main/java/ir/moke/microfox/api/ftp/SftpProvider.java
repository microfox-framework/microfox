package ir.moke.microfox.api.ftp;

import com.jcraft.jsch.ChannelSftp;

import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public interface SftpProvider {
    void sftpDownload(MicroFoxSftpConfig config, Path remoteFilePath, Path localDownloadDir);

    void sftpBatchDownload(MicroFoxSftpConfig config, List<Path> remoteFilePath, Path localDownloadDir);

    void sftpUpload(MicroFoxSftpConfig config, Path remoteDirPath, Path file, SftpMode mode);

    void sftpBatchUpload(MicroFoxSftpConfig config, Path remoteDirPath, List<Path> files, SftpMode mode);

    void sftpDelete(MicroFoxSftpConfig config, Path remoteFilePath);

    void sftpBatchDelete(MicroFoxSftpConfig config, List<Path> remoteFilePath);

    void sftpList(MicroFoxSftpConfig config, Path remoteFilePath, Consumer<Vector<ChannelSftp.LsEntry>> consumer);
}
