package cl.globallogic.testssw.dto;

import cl.globallogic.testssw.entity.Phone;

import java.util.List;

public class UserDto {

    private String name;
    private String email;
    private String password;
    private List<Phone> phones;

    public UserDto() {}

    public UserDto(String name, String email, String password, List<Phone> phones) {
        setName(name);
        setEmail(email);
        setPassword(password);
        setPhones(phones);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }
}
