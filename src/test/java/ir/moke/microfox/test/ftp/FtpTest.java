package ir.moke.microfox.test.ftp;

import ir.moke.microfox.ftp.FtpConfig;
import org.junit.jupiter.api.*;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import static ir.moke.microfox.MicroFox.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class FtpTest {
    private static final Path sample_file = Path.of("/tmp/sample_file");
    private static final FakeFtpServer fakeFtpServer = new FakeFtpServer();
    private static final FtpConfig ftpConfig = new FtpConfig("localhost", 2121, "admin", "adminpass");

    @BeforeAll
    public static void init() {
        createSampleFile();
        fakeFtpServer.setServerControlPort(2121);
        fakeFtpServer.addUserAccount(new UserAccount("admin", "adminpass", "/"));
        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/d1"));
        fileSystem.add(new DirectoryEntry("/d2"));
        fileSystem.add(new FileEntry("/F1"));
        fileSystem.add(new FileEntry("/F2"));
        fileSystem.add(new FileEntry("/d1/F3"));

        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.start();
    }

    @Test
    @Order(0)
    public void upload() {
        try {
            ftpUpload(ftpConfig, "/sample_file", sample_file.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void list() {
        try (FileInputStream fileInputStream = new FileInputStream(sample_file.toFile())) {
            ftpList(ftpConfig, "/", ftpFiles -> Arrays.stream(ftpFiles).forEach(System.out::println));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void download() {
        ftpDownload(ftpConfig, "/sample_file", Path.of("/tmp/download_dir/"));
    }

    @Test
    @Order(3)
    public void delete() {
        ftpDelete(ftpConfig, "/sample_file");
        list();
    }

    private static void createSampleFile() {
        try {
            Files.deleteIfExists(sample_file);
            byte[] b = new byte[200];
            new Random().nextBytes(b);
            Files.createFile(sample_file);
            Files.write(sample_file, b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @AfterAll
    public static void shutdown() {
        fakeFtpServer.stop();
    }
}
