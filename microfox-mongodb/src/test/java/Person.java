import ir.moke.microfox.mongodb.Collection;

@Collection("persons")
public class Person {
    private String name;
    private String family;
    private Long id;

    public Person() {
    }

    public Person(String name, String family, Long id) {
        this.name = name;
        this.family = family;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
