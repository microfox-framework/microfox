import ir.moke.microfox.api.elastic.Index;

@Index("person")
public class Person {
    private long id;
    private String name;
    private String family;
    private int age;

    public Person() {
    }

    public Person(long id, String name, String family, int age) {
        this.id = id;
        this.name = name;
        this.family = family;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", family='" + family + '\'' +
               ", age=" + age +
               '}';
    }
}
