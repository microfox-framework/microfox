package ir.moke.microfox.jpa;

public class JpaActiveRecord<T> {
    private final String identity;

    public JpaActiveRecord(String identity) {
        this.identity = identity;
    }

    public void save() {
        Crud.insert(identity, this);
    }

    public void update() {
        Crud.update(identity, this);
    }

    public void remove(T t) {
        Crud.delete(identity, this);
    }
}
