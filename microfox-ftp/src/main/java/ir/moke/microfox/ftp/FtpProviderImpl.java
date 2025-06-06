package ir.moke.microfox.ftp;

import ir.moke.microfox.api.ftp.FtpFile;
import ir.moke.microfox.api.ftp.FtpProvider;
import ir.moke.microfox.api.ftp.MicroFoxFtpConfig;
import ir.moke.microfox.exception.MicrofoxException;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FtpProviderImpl implements FtpProvider {

    @Override
    public void ftpDownload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, Path localDownloadDir) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            ftpClient.ftpDownload(remoteFilePath, localDownloadDir);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }

    }

    @Override
    public void ftpBatchDownload(MicroFoxFtpConfig microFoxFtpConfig, List<String> remoteFilePath, Path localDownloadDir) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            ftpClient.ftpBatchDownload(remoteFilePath, localDownloadDir);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void ftpUpload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, File file) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            ftpClient.ftpUpload(remoteFilePath, file);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }

    }

    @Override
    public void ftpBatchUpload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, List<File> files) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            ftpClient.ftpBatchUpload(remoteFilePath, files);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }

    }

    @Override
    public void ftpDelete(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            ftpClient.delete(remoteFilePath);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void batchDelete(MicroFoxFtpConfig microFoxFtpConfig, List<String> remoteFilePath) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
                for (String item : remoteFilePath) {
                    ftpClient.delete(item);
                }
            }
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void ftpList(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, Consumer<List<FtpFile>> consumer) {
        try {
            FtpClient ftpClient = new FtpClient();
            ftpClient.connect(microFoxFtpConfig.host(), microFoxFtpConfig.port());
            ftpClient.login(microFoxFtpConfig.username(), microFoxFtpConfig.password());
            FTPFile[] ftpFiles = ftpClient.listFiles(remoteFilePath);
            List<FtpFile> list = new ArrayList<>();
            for (FTPFile ftpFile : ftpFiles) {
                FtpFile file = new FtpFile(ftpFile.getType(), ftpFile.getHardLinkCount(), ftpFile.getSize(), ftpFile.getRawListing(), ftpFile.getUser(), ftpFile.getGroup(), ftpFile.getName(), ftpFile.getLink(), ftpFile.getTimestamp());
                list.add(file);
            }
            consumer.accept(list);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }

    }
}
