package ir.moke.microfox.ftp;

import com.jcraft.jsch.ChannelSftp;
import ir.moke.microfox.api.ftp.MicroFoxSftpConfig;
import ir.moke.microfox.api.ftp.SftpProvider;

import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class SFtpProviderImpl implements SftpProvider {
    @Override
    public void sftpDownload(MicroFoxSftpConfig config, Path remoteFilePath, Path localDownloadDir) {
        SftpClient client = new SftpClient(config);
        client.sftpDownload(remoteFilePath, localDownloadDir);
    }

    @Override
    public void sftpBatchDownload(MicroFoxSftpConfig config, List<Path> remoteFilePath, Path localDownloadDir) {
        SftpClient client = new SftpClient(config);
        client.sftpBatchDownload(remoteFilePath, localDownloadDir);
    }

    @Override
    public void sftpUpload(MicroFoxSftpConfig config, Path remoteFilePath, Path file) {
        SftpClient client = new SftpClient(config);
        client.ftpUpload(remoteFilePath, file);
    }

    @Override
    public void sftpBatchUpload(MicroFoxSftpConfig config, Path remoteFilePath, List<Path> files) {
        SftpClient client = new SftpClient(config);
        client.ftpBatchUpload(remoteFilePath, files);
    }

    @Override
    public void sftpDelete(MicroFoxSftpConfig config, Path remoteFilePath) {
        SftpClient client = new SftpClient(config);
        client.delete(remoteFilePath);
    }

    @Override
    public void sftpBatchDelete(MicroFoxSftpConfig config, List<Path> remoteFilePath) {
        SftpClient client = new SftpClient(config);
        if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
            remoteFilePath.forEach(client::delete);
        }
    }

    @Override
    public void sftpList(MicroFoxSftpConfig config, Path remoteFilePath, Consumer<Vector<ChannelSftp.LsEntry>> consumer) {
        SftpClient client = new SftpClient(config);
        Vector<ChannelSftp.LsEntry> lsEntries = client.listFiles(remoteFilePath);
        consumer.accept(lsEntries);
    }
}
