package ph.com.gs3.loyaltystore.models.sqlite.dao;

import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PRODUCT".
 */
public class Product {

     @SerializedName("ID")
    private Long id;
    private String name;
     @SerializedName("rad_type")
    private String type;
    private Float unit_cost;
    private String sku;
     @SerializedName("TS")
    private String ts;
     @SerializedName("txt_deducted_id")
    private Long deduct_product_to_id;
     @SerializedName("txt_deduct_description")
    private String deduct_product_to_name;
     @SerializedName("txt_quantity_deduct")
    private Double deduct_product_to_quantity;
    private Boolean is_active;

    public Product() {
    }

    public Product(Long id) {
        this.id = id;
    }

    public Product(Long id, String name, String type, Float unit_cost, String sku, String ts, Long deduct_product_to_id, String deduct_product_to_name, Double deduct_product_to_quantity, Boolean is_active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unit_cost = unit_cost;
        this.sku = sku;
        this.ts = ts;
        this.deduct_product_to_id = deduct_product_to_id;
        this.deduct_product_to_name = deduct_product_to_name;
        this.deduct_product_to_quantity = deduct_product_to_quantity;
        this.is_active = is_active;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getUnit_cost() {
        return unit_cost;
    }

    public void setUnit_cost(Float unit_cost) {
        this.unit_cost = unit_cost;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Long getDeduct_product_to_id() {
        return deduct_product_to_id;
    }

    public void setDeduct_product_to_id(Long deduct_product_to_id) {
        this.deduct_product_to_id = deduct_product_to_id;
    }

    public String getDeduct_product_to_name() {
        return deduct_product_to_name;
    }

    public void setDeduct_product_to_name(String deduct_product_to_name) {
        this.deduct_product_to_name = deduct_product_to_name;
    }

    public Double getDeduct_product_to_quantity() {
        return deduct_product_to_quantity;
    }

    public void setDeduct_product_to_quantity(Double deduct_product_to_quantity) {
        this.deduct_product_to_quantity = deduct_product_to_quantity;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

}
