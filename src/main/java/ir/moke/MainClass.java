package ir.moke;

import static ir.moke.MicroFox.*;

public class MainClass {
    public static void main(String[] args) {
        get("/hello",(req,resp) -> System.out.println("Hello"));
        post("/import",(req,resp) -> System.out.println(req.body()));
    }
}
