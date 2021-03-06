package ph.com.gs3.loyaltystore.models.sqlite.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "CUSTOMER".
 */
public class Customer {

    private Long id;
    private String name;
    private String device_id;
    private String contact_number;
    private String location;
    private String gender;
    private java.util.Date birth_date;
    private Integer points;
    private java.util.Date created_at;
    private java.util.Date updated_at;

    public Customer() {
    }

    public Customer(Long id) {
        this.id = id;
    }

    public Customer(Long id, String name, String device_id, String contact_number, String location, String gender, java.util.Date birth_date, Integer points, java.util.Date created_at, java.util.Date updated_at) {
        this.id = id;
        this.name = name;
        this.device_id = device_id;
        this.contact_number = contact_number;
        this.location = location;
        this.gender = gender;
        this.birth_date = birth_date;
        this.points = points;
        this.created_at = created_at;
        this.updated_at = updated_at;
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

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public java.util.Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(java.util.Date birth_date) {
        this.birth_date = birth_date;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public java.util.Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(java.util.Date updated_at) {
        this.updated_at = updated_at;
    }

}
