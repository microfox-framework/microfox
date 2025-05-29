package ir.moke.microfox;

import ir.moke.microfox.http.HttpContainer;

public class MicroFoxServer {

    static {
        MicroFoxConfig.introduce();
    }
    public static void start() {
        HttpContainer.start();
    }
}
