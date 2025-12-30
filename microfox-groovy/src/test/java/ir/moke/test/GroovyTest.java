package ir.moke.test;

import ir.moke.microfox.MicroFox;
import org.junit.Test;

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
        MicroFox.groovyEval(script, null);
    }
}
