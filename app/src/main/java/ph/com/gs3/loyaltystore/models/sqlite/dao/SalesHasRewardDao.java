package ph.com.gs3.loyaltystore.models.sqlite.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SALES_HAS_REWARD".
*/
public class SalesHasRewardDao extends AbstractDao<SalesHasReward, Long> {

    public static final String TABLENAME = "SALES_HAS_REWARD";

    /**
     * Properties of entity SalesHasReward.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Reward_id = new Property(1, Long.class, "reward_id", false, "REWARD_ID");
        public final static Property Sales_transaction_number = new Property(2, String.class, "sales_transaction_number", false, "SALES_TRANSACTION_NUMBER");
    };


    public SalesHasRewardDao(DaoConfig config) {
        super(config);
    }
    
    public SalesHasRewardDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SALES_HAS_REWARD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"REWARD_ID\" INTEGER," + // 1: reward_id
                "\"SALES_TRANSACTION_NUMBER\" TEXT);"); // 2: sales_transaction_number
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SALES_HAS_REWARD\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SalesHasReward entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long reward_id = entity.getReward_id();
        if (reward_id != null) {
            stmt.bindLong(2, reward_id);
        }
 
        String sales_transaction_number = entity.getSales_transaction_number();
        if (sales_transaction_number != null) {
            stmt.bindString(3, sales_transaction_number);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SalesHasReward readEntity(Cursor cursor, int offset) {
        SalesHasReward entity = new SalesHasReward( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // reward_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // sales_transaction_number
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SalesHasReward entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setReward_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setSales_transaction_number(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SalesHasReward entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SalesHasReward entity) {
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
