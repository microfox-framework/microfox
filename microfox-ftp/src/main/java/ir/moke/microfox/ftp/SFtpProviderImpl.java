package ir.moke.microfox.ftp;

import com.jcraft.jsch.ChannelSftp;
import ir.moke.microfox.api.ftp.MicroFoxSftpConfig;
import ir.moke.microfox.api.ftp.SftpMode;
import ir.moke.microfox.api.ftp.SftpProvider;

import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class SFtpProviderImpl implements SftpProvider {
    @Override
    public void sftpDownload(MicroFoxSftpConfig config, Path remoteFilePath, Path localDownloadDir) {
        SftpClient client = new SftpClient(config);
        client.connect();
        client.sftpDownload(remoteFilePath, localDownloadDir);
        client.disconnect();
    }

    @Override
    public void sftpBatchDownload(MicroFoxSftpConfig config, List<Path> remoteFilePath, Path localDownloadDir) {
        SftpClient client = new SftpClient(config);
        client.connect();
        client.sftpBatchDownload(remoteFilePath, localDownloadDir);
        client.disconnect();
    }

    @Override
    public void sftpUpload(MicroFoxSftpConfig config, Path remoteDirPath, Path file, SftpMode mode) {
        SftpClient client = new SftpClient(config);
        client.connect();
        client.ftpUpload(remoteDirPath, file, mode);
        client.disconnect();
    }

    @Override
    public void sftpBatchUpload(MicroFoxSftpConfig config, Path remoteDirPath, List<Path> files, SftpMode mode) {
        SftpClient client = new SftpClient(config);
        client.connect();
        client.ftpBatchUpload(remoteDirPath, files, mode);
        client.disconnect();
    }

    @Override
    public void sftpDelete(MicroFoxSftpConfig config, Path remoteFilePath) {
        SftpClient client = new SftpClient(config);
        client.connect();
        client.delete(remoteFilePath);
        client.disconnect();
    }

    @Override
    public void sftpBatchDelete(MicroFoxSftpConfig config, List<Path> remoteFilePath) {
        SftpClient client = new SftpClient(config);
        client.connect();
        if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
            remoteFilePath.forEach(client::delete);
        }
        client.disconnect();
    }

    @Override
    public void sftpList(MicroFoxSftpConfig config, Path remoteFilePath, Consumer<Vector<ChannelSftp.LsEntry>> consumer) {
        SftpClient client = new SftpClient(config);
        client.connect();
        Vector<ChannelSftp.LsEntry> lsEntries = client.listFiles(remoteFilePath);
        consumer.accept(lsEntries);
        client.disconnect();
    }
}
