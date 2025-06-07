package ir.moke.microfox.utils;

public interface TtyAsciiCodecs {
    char ESC = (char) 27;
    String RESET = ESC + "[0m";
    String BOLD = ESC + "[1m";
    String BLINK = ESC + "[5m";

    String RED = ESC + "[31m";
    String GREEN = ESC + "[32m";
    String YELLOW = ESC + "[33m";
    String BLUE = ESC + "[34m";
    String PURPLE = ESC + "[35m";

    String BACKGROUND_RED = ESC + "[41m";
    String BACKGROUND_GREEN = ESC + "[42m";
    String BACKGROUND_YELLOW = ESC + "[43m";
    String BACKGROUND_BLUE = ESC + "[43m";
}
