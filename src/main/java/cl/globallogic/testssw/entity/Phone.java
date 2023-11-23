package cl.globallogic.testssw.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "phone")
public class Phone {

    @Id
    private Long number;
    private int citycode;
    private String countrycode;

    protected Phone() {}

    public Phone(Long number, int citycode, String countrycode) {
        setNumber(number);
        setCitycode(citycode);
        setCountrycode(countrycode);
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public int getCitycode() {
        return citycode;
    }

    public void setCitycode(int citycode) {
        this.citycode = citycode;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }
}
