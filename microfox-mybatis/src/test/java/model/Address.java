package model;

import ir.moke.microfox.api.mybatis.Column;

public class Address {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;

    @Column("postal_code")
    private String postalCode;
    @Column("phone_number")
    private String phoneNumber;

    public Address() {
    }

    public Address(String country, String state, String city, String street, String postalCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Address{" +
               "id=" + id +
               ", street='" + street + '\'' +
               ", city='" + city + '\'' +
               ", state='" + state + '\'' +
               ", country='" + country + '\'' +
               ", postalCode='" + postalCode + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               '}';
    }
}
