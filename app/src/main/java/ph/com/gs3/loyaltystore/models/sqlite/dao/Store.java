package ph.com.gs3.loyaltystore.models.sqlite.dao;

import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "STORE".
 */
public class Store {

     @SerializedName("ID")
    private Long id;
    private String device_id;
    private Long device_web_id;
    private String name;
    private Integer is_active;
    private java.util.Date created_at;
    private java.util.Date updated_at;
     @SerializedName("list_approver")
    private String approver;

    public Store() {
    }

    public Store(Long id) {
        this.id = id;
    }

    public Store(Long id, String device_id, Long device_web_id, String name, Integer is_active, java.util.Date created_at, java.util.Date updated_at, String approver) {
        this.id = id;
        this.device_id = device_id;
        this.device_web_id = device_web_id;
        this.name = name;
        this.is_active = is_active;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.approver = approver;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Long getDevice_web_id() {
        return device_web_id;
    }

    public void setDevice_web_id(Long device_web_id) {
        this.device_web_id = device_web_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIs_active() {
        return is_active;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
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

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

}
