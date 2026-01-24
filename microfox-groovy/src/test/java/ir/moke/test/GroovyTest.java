package ir.moke.test;

import ir.moke.microfox.MicroFox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GroovyTest {

    @Test
    public void checkScript() {
        String script = """
                String name = "mahdi"
                def sayHello(String name) {
                    println "Hello " + name
                }
                
                sayHello(name)
                """;
        Assertions.assertDoesNotThrow(() -> MicroFox.groovyEval(script, null));
    }
}
