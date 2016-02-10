package ph.com.gs3.loyaltystore.models.values;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class UnitOfMeasure implements Parcelable {

    private int id;
    private int web_id;
    private String name;

    public UnitOfMeasure() {
        super();
    }

    private UnitOfMeasure(Parcel in) {
        super();
        this.id = in.readInt();
        this.web_id = in.readInt();
        this.name = in.readString();
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

    @Override
    public String toString() {
        return "UNIT OF MEASURE INFO [id=" + id + ",web_id=" + web_id + ", name=" + name + "]";
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
        UnitOfMeasure other = (UnitOfMeasure) obj;
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
    }

    public static final Parcelable.Creator<UnitOfMeasure> CREATOR = new Parcelable.Creator<UnitOfMeasure>() {
        public UnitOfMeasure createFromParcel(Parcel in) {
            return new UnitOfMeasure(in);
        }

        public UnitOfMeasure[] newArray(int size) {
            return new UnitOfMeasure[size];
        }
    };

}
