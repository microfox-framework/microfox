import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.ftp.MicroFoxSftpConfig;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1) Install openssh : apt install openssh-server
 * 2) add new user: useradd -mc test -s /bin/bash test ; sudo passwd test
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SftpTest {
    private static final Logger logger = LoggerFactory.getLogger(SftpTest.class);
    private static final MicroFoxSftpConfig config = new MicroFoxSftpConfig("127.0.0.1", "test", "testpass");
    private static final Path HOME_PATH = Path.of(System.getProperty("user.home"));
    private static final Path FTP_CHROOT_DIR = Path.of("/");
    private static final Path FTP_BASE_DIR = Path.of("/srv/ftp");
    private static final Path SINGLE_FILE = Path.of("tu-single-file");
    private static final Path MULTIPLE_FILE_1 = Path.of("tu-multiple-file1");
    private static final Path MULTIPLE_FILE_2 = Path.of("tu-multiple-file2");
    private static final Path MULTIPLE_FILE_3 = Path.of("tu-multiple-file3");
    private static final Path SINGLE_UPLOAD = Path.of("tu-single-upload-file");
    private static final Path MULTIPLE_UPLOAD_FILE_1 = Path.of("tu-multiple-upload-file1");
    private static final Path MULTIPLE_UPLOAD_FILE_2 = Path.of("tu-multiple-upload-file2");
    private static final Path MULTIPLE_UPLOAD_FILE_3 = Path.of("tu-multiple-upload-file3");

    @BeforeAll
    public static void init() {
        try {
            // create sample file
            Files.createFile(FTP_BASE_DIR.resolve(SINGLE_FILE));
            Files.createFile(FTP_BASE_DIR.resolve(MULTIPLE_FILE_1));
            Files.createFile(FTP_BASE_DIR.resolve(MULTIPLE_FILE_2));
            Files.createFile(FTP_BASE_DIR.resolve(MULTIPLE_FILE_3));
            Files.createFile(HOME_PATH.resolve(SINGLE_UPLOAD));
            Files.createFile(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_1));
            Files.createFile(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_2));
            Files.createFile(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_3));
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }

    @AfterAll
    public static void shutdown() {
        try {
            // create sample file
            Files.delete(FTP_BASE_DIR.resolve(SINGLE_FILE));
            Files.delete(FTP_BASE_DIR.resolve(MULTIPLE_FILE_1));
            Files.delete(FTP_BASE_DIR.resolve(MULTIPLE_FILE_2));
            Files.delete(FTP_BASE_DIR.resolve(MULTIPLE_FILE_3));

            Files.delete(HOME_PATH.resolve(SINGLE_FILE));
            Files.delete(HOME_PATH.resolve(MULTIPLE_FILE_1));
            Files.delete(HOME_PATH.resolve(MULTIPLE_FILE_2));
            Files.delete(HOME_PATH.resolve(MULTIPLE_FILE_3));


            Files.delete(FTP_BASE_DIR.resolve(SINGLE_UPLOAD));
            Files.delete(FTP_BASE_DIR.resolve(MULTIPLE_UPLOAD_FILE_1));
            Files.delete(FTP_BASE_DIR.resolve(MULTIPLE_UPLOAD_FILE_2));
            Files.delete(FTP_BASE_DIR.resolve(MULTIPLE_UPLOAD_FILE_3));

            Files.delete(HOME_PATH.resolve(SINGLE_UPLOAD));
            Files.delete(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_1));
            Files.delete(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_2));
            Files.delete(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_3));
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }

    @Test
    @Order(0)
    public void checkListFiles() {
        AtomicInteger count = new AtomicInteger();
        MicroFox.sftpList(config, FTP_CHROOT_DIR, ftpFiles -> count.set(ftpFiles.size()));
        Assertions.assertTrue(count.get() > 0);
    }

    @Test
    @Order(1)
    public void checkDownloadFile() {
        MicroFox.sftpDownload(config, FTP_CHROOT_DIR.resolve(SINGLE_FILE), HOME_PATH);
        Assertions.assertTrue(HOME_PATH.resolve(SINGLE_FILE).toFile().exists());
    }

    @Test
    @Order(2)
    public void checkDownloadBatchFiles() {
        List<Path> files = List.of(MULTIPLE_FILE_1, MULTIPLE_FILE_2, MULTIPLE_FILE_3);
        MicroFox.sftpBatchDownload(config, files, HOME_PATH);
        Assertions.assertTrue(HOME_PATH.resolve(MULTIPLE_FILE_1).toFile().exists());
        Assertions.assertTrue(HOME_PATH.resolve(MULTIPLE_FILE_2).toFile().exists());
        Assertions.assertTrue(HOME_PATH.resolve(MULTIPLE_FILE_3).toFile().exists());
    }

    @Test
    @Order(3)
    public void checkUpload() {
        MicroFox.sftpUpload(config, FTP_CHROOT_DIR, HOME_PATH.resolve(SINGLE_UPLOAD));
        Assertions.assertTrue(FTP_BASE_DIR.resolve(SINGLE_UPLOAD).toFile().exists());
    }

    @Test
    @Order(4)
    public void checkMultipleUpload() {
        List<Path> files = List.of(HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_1), HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_2), HOME_PATH.resolve(MULTIPLE_UPLOAD_FILE_3));
        MicroFox.sftpBatchUpload(config, FTP_CHROOT_DIR, files);
        Assertions.assertTrue(FTP_BASE_DIR.resolve(MULTIPLE_UPLOAD_FILE_1).toFile().exists());
        Assertions.assertTrue(FTP_BASE_DIR.resolve(MULTIPLE_UPLOAD_FILE_2).toFile().exists());
        Assertions.assertTrue(FTP_BASE_DIR.resolve(MULTIPLE_UPLOAD_FILE_3).toFile().exists());
    }
}
