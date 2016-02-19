package ph.com.gs3.loyaltystore.models.sqlite.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SALES_PRODUCT".
*/
public class SalesProductDao extends AbstractDao<SalesProduct, Long> {

    public static final String TABLENAME = "SALES_PRODUCT";

    /**
     * Properties of entity SalesProduct.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sales_transaction_number = new Property(1, String.class, "sales_transaction_number", false, "SALES_TRANSACTION_NUMBER");
        public final static Property Product_id = new Property(2, Long.class, "product_id", false, "PRODUCT_ID");
        public final static Property Quantity = new Property(3, Integer.class, "quantity", false, "QUANTITY");
        public final static Property Sub_total = new Property(4, Float.class, "sub_total", false, "SUB_TOTAL");
        public final static Property Sale_type = new Property(5, String.class, "sale_type", false, "SALE_TYPE");
    };


    public SalesProductDao(DaoConfig config) {
        super(config);
    }
    
    public SalesProductDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SALES_PRODUCT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SALES_TRANSACTION_NUMBER\" TEXT," + // 1: sales_transaction_number
                "\"PRODUCT_ID\" INTEGER," + // 2: product_id
                "\"QUANTITY\" INTEGER," + // 3: quantity
                "\"SUB_TOTAL\" REAL," + // 4: sub_total
                "\"SALE_TYPE\" TEXT);"); // 5: sale_type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SALES_PRODUCT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SalesProduct entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sales_transaction_number = entity.getSales_transaction_number();
        if (sales_transaction_number != null) {
            stmt.bindString(2, sales_transaction_number);
        }
 
        Long product_id = entity.getProduct_id();
        if (product_id != null) {
            stmt.bindLong(3, product_id);
        }
 
        Integer quantity = entity.getQuantity();
        if (quantity != null) {
            stmt.bindLong(4, quantity);
        }
 
        Float sub_total = entity.getSub_total();
        if (sub_total != null) {
            stmt.bindDouble(5, sub_total);
        }
 
        String sale_type = entity.getSale_type();
        if (sale_type != null) {
            stmt.bindString(6, sale_type);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SalesProduct readEntity(Cursor cursor, int offset) {
        SalesProduct entity = new SalesProduct( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sales_transaction_number
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // product_id
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // quantity
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // sub_total
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // sale_type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SalesProduct entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSales_transaction_number(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setProduct_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setQuantity(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setSub_total(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setSale_type(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SalesProduct entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SalesProduct entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
