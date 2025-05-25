package ir.moke.microfox;

import ir.moke.microfox.http.HttpContainer;

import java.io.IOException;
import java.io.InputStream;

public class MicroFoxServer {

    static {
        try (InputStream inputStream = MicroFoxServer.class.getClassLoader().getResourceAsStream("logo")) {
            if (inputStream != null) {
                System.out.println(new String(inputStream.readAllBytes()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MicroFoxConfig.print();
    }

    public static void start() {
        HttpContainer.start();
    }
}
