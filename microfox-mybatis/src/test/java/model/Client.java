package model;

import java.util.List;

public class Client {
    private Long id;
    private String name;
    private String family;
    private List<Address> addresses;

    public Client() {
    }

    public Client(String name, String family, List<Address> addresses) {
        this.name = name;
        this.family = family;
        this.addresses = addresses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
