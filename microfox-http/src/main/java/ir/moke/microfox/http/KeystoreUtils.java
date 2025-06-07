package ir.moke.microfox.http;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static ir.moke.microfox.utils.TtyAsciiCodecs.GREEN;
import static ir.moke.microfox.utils.TtyAsciiCodecs.RESET;

public class KeystoreUtils {

    private static final Provider BC_PROVIDER = new BouncyCastleProvider();
    private static final Logger logger = LoggerFactory.getLogger(KeystoreUtils.class);

    static {
        Security.addProvider(BC_PROVIDER);
    }

    public static void createPKCS12(Path keystorePath, String keystorePassword, String alias, char[] keyPassword) {
        try {
            // 1. Generate RSA Key Pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 2. Create Self-Signed Certificate
            X509Certificate certificate = generateSelfSignedCertificate(keyPair);

            // 3. Create and Initialize PKCS12 Keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null); // Create an empty keystore

            // 4. Store Private Key & Certificate in Keystore
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPassword, new Certificate[]{certificate});

            // 5. Save Keystore to File
            try (FileOutputStream fos = new FileOutputStream(keystorePath.toFile())) {
                keyStore.store(fos, keystorePassword.toCharArray());
            }

            logger.info("{}✅ JKS Keystore created at{}: {}", GREEN, RESET, keystorePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static X509Certificate generateSelfSignedCertificate(KeyPair keyPair) throws Exception {
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date expiryDate = new Date(now + TimeUnit.DAYS.toMillis(365)); // Valid for 1 year

        X500Name dnName = new X500Name("CN=localhost, O=moke.ir, C=IR");
        BigInteger serialNumber = BigInteger.valueOf(now); // Unique serial number

        // Create Certificate
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, serialNumber, startDate, expiryDate, dnName, keyPair.getPublic());
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(certBuilder.build(signer));
    }
}
