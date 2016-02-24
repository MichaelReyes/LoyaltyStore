package ph.com.gs3.loyaltystore.models.sqlite.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CASH_RETURN".
*/
public class CashReturnDao extends AbstractDao<CashReturn, Long> {

    public static final String TABLENAME = "CASH_RETURN";

    /**
     * Properties of entity CashReturn.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Store_id = new Property(1, Long.class, "store_id", false, "STORE_ID");
        public final static Property Item = new Property(2, String.class, "item", false, "ITEM");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
        public final static Property Amount = new Property(4, Float.class, "amount", false, "AMOUNT");
        public final static Property Remarks = new Property(5, String.class, "remarks", false, "REMARKS");
        public final static Property Deposited_to_bank = new Property(6, String.class, "deposited_to_bank", false, "DEPOSITED_TO_BANK");
        public final static Property Time_of_deposit = new Property(7, java.util.Date.class, "time_of_deposit", false, "TIME_OF_DEPOSIT");
        public final static Property Image = new Property(8, String.class, "Image", false, "IMAGE");
        public final static Property Is_synced = new Property(9, Boolean.class, "is_synced", false, "IS_SYNCED");
    };


    public CashReturnDao(DaoConfig config) {
        super(config);
    }
    
    public CashReturnDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CASH_RETURN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"STORE_ID\" INTEGER," + // 1: store_id
                "\"ITEM\" TEXT," + // 2: item
                "\"TYPE\" TEXT," + // 3: type
                "\"AMOUNT\" REAL," + // 4: amount
                "\"REMARKS\" TEXT," + // 5: remarks
                "\"DEPOSITED_TO_BANK\" TEXT," + // 6: deposited_to_bank
                "\"TIME_OF_DEPOSIT\" INTEGER," + // 7: time_of_deposit
                "\"IMAGE\" TEXT," + // 8: Image
                "\"IS_SYNCED\" INTEGER);"); // 9: is_synced
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CASH_RETURN\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CashReturn entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long store_id = entity.getStore_id();
        if (store_id != null) {
            stmt.bindLong(2, store_id);
        }
 
        String item = entity.getItem();
        if (item != null) {
            stmt.bindString(3, item);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        Float amount = entity.getAmount();
        if (amount != null) {
            stmt.bindDouble(5, amount);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(6, remarks);
        }
 
        String deposited_to_bank = entity.getDeposited_to_bank();
        if (deposited_to_bank != null) {
            stmt.bindString(7, deposited_to_bank);
        }
 
        java.util.Date time_of_deposit = entity.getTime_of_deposit();
        if (time_of_deposit != null) {
            stmt.bindLong(8, time_of_deposit.getTime());
        }
 
        String Image = entity.getImage();
        if (Image != null) {
            stmt.bindString(9, Image);
        }
 
        Boolean is_synced = entity.getIs_synced();
        if (is_synced != null) {
            stmt.bindLong(10, is_synced ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CashReturn readEntity(Cursor cursor, int offset) {
        CashReturn entity = new CashReturn( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // store_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // item
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // amount
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // remarks
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // deposited_to_bank
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // time_of_deposit
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // Image
            cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0 // is_synced
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CashReturn entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStore_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setItem(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAmount(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setRemarks(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDeposited_to_bank(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTime_of_deposit(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setImage(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setIs_synced(cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CashReturn entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CashReturn entity) {
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