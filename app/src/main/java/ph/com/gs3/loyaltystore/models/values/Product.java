package ph.com.gs3.loyaltystore.models.values;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bryan-PC on 27/01/2016.
 */
public class Product implements Parcelable {

    private int id;
    private int web_id;
    private String name;
    private int uom_id;
    private float unit_cost;
    private String sku;
    private String created_at;
    private String updated_at;

    public Product() {
        super();
    }

    private Product(Parcel in) {
        super();
        this.id = in.readInt();
        this.web_id = in.readInt();
        this.name = in.readString();
        this.uom_id = in.readInt();
        this.unit_cost = in.readFloat();
        this.sku = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeb_id() {
        return web_id;
    }

    public void setWeb_id(int web_id) {
        this.web_id = web_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUom_id() {
        return uom_id;
    }

    public void setUom_id(int uom_id) {
        this.uom_id = uom_id;
    }

    public float getUnit_cost() {
        return unit_cost;
    }

    public void setUnit_cost(float unit_cost) {
        this.unit_cost = unit_cost;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "PRODUCT INFO [id=" + id + ",web_id=" + web_id +", name=" + name + ", uom_id=" + uom_id +
                ", unit_cost=" + unit_cost + ", sku=" + sku + ", created_at=" + created_at +
                ", updated_at=" + updated_at + " ]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeInt(getWeb_id());
        parcel.writeString(getName());
        parcel.writeInt(getUom_id());
        parcel.writeFloat(getUnit_cost());
        parcel.writeString(getSku());
        parcel.writeString(getCreated_at());
        parcel.writeString(getUpdated_at());
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
