package ph.com.gs3.loyaltystore.models.api.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bryan-PC on 24/05/2016.
 */
public class FormalisticsGetProductsResponse {

    @SerializedName("ID")
    private Long id;
    private String name;
    @SerializedName("rad_type")
    private String type;
    @SerializedName("rad_category")
    private String category;
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
    private Double txt_stock;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Double getTxt_stock() {
        return txt_stock;
    }

    public void setTxt_stock(Double txt_stock) {
        this.txt_stock = txt_stock;
    }
}
