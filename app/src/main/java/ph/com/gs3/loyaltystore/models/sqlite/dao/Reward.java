package ph.com.gs3.loyaltystore.models.sqlite.dao;

import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "REWARD".
 */
public class Reward {

     @SerializedName("ID")
    private Long id;
    private String reward_condition;
    private Integer condition_product_id;
    private String condition;
    private Float condition_value;
    private String reward_type;
    private String reward;
    private String reward_value;
    private java.util.Date valid_from;
    private java.util.Date valid_until;
     @SerializedName("DateCreated")
    private java.util.Date created_at;
     @SerializedName("DateUpdated")
    private java.util.Date updated_at;

    public Reward() {
    }

    public Reward(Long id) {
        this.id = id;
    }

    public Reward(Long id, String reward_condition, Integer condition_product_id, String condition, Float condition_value, String reward_type, String reward, String reward_value, java.util.Date valid_from, java.util.Date valid_until, java.util.Date created_at, java.util.Date updated_at) {
        this.id = id;
        this.reward_condition = reward_condition;
        this.condition_product_id = condition_product_id;
        this.condition = condition;
        this.condition_value = condition_value;
        this.reward_type = reward_type;
        this.reward = reward;
        this.reward_value = reward_value;
        this.valid_from = valid_from;
        this.valid_until = valid_until;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReward_condition() {
        return reward_condition;
    }

    public void setReward_condition(String reward_condition) {
        this.reward_condition = reward_condition;
    }

    public Integer getCondition_product_id() {
        return condition_product_id;
    }

    public void setCondition_product_id(Integer condition_product_id) {
        this.condition_product_id = condition_product_id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Float getCondition_value() {
        return condition_value;
    }

    public void setCondition_value(Float condition_value) {
        this.condition_value = condition_value;
    }

    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getReward_value() {
        return reward_value;
    }

    public void setReward_value(String reward_value) {
        this.reward_value = reward_value;
    }

    public java.util.Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(java.util.Date valid_from) {
        this.valid_from = valid_from;
    }

    public java.util.Date getValid_until() {
        return valid_until;
    }

    public void setValid_until(java.util.Date valid_until) {
        this.valid_until = valid_until;
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
