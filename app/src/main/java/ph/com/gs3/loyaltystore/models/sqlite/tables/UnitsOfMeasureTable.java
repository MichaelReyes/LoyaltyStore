package ph.com.gs3.loyaltystore.models.sqlite.tables;

import ph.com.gs3.loyaltystore.models.sqlite.DatabaseHelperDefault;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class UnitsOfMeasureTable {

    private DatabaseHelperDefault databaseHelperDefault;

    public static final String TABLE_NAME = "tblUnitsOfMeasure";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WEB_ID = "web_id";
    public static final String COLUMN_NAME = "name";

    public static final String CREATION_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_WEB_ID + " INTEGER, "

            + COLUMN_NAME + " TEXT "

            + ");";

    /*public UnitsOfMeasureTable(Context context) {
        databaseHelperDefault = new DatabaseHelperDefault(context);
    }

    public boolean insertData(int web_id, String name) {

        if (!productExist(web_id)) {

            SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_WEB_ID, web_id);
            contentValues.put(COLUMN_NAME, name);

            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                return false;
            } else {
                return true;
            }

        }else{
            return false;
        }

    }

    public boolean productExist(int web_id) {
        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_WEB_ID + "=" + web_id;

        Cursor cursor = db.rawQuery(query, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        return cursor;
    }

    public Cursor getUnitOfMeasureByWebId(int web_id){
        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                    " WHERE " + COLUMN_ID + "=" + web_id ,null);
        return  cursor;
    }

    public boolean updateData(int web_id, String name, int uom_id,
                              float unit_cost, String sku) {

        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        db.update(TABLE_NAME,
                contentValues,
                COLUMN_WEB_ID + " = ?",
                new String[]{Integer.toString(web_id)});
        return true;
    }

    public Integer deleteData(int web_id) {

        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_WEB_ID + " = ?", new String[]{Integer.toString(web_id)});

    }
*/
}
