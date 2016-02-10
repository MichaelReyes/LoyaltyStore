package ph.com.gs3.loyaltystore.models.sqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ph.com.gs3.loyaltystore.models.sqlite.DBDAO.LoyaltyDBDAO;
import ph.com.gs3.loyaltystore.models.sqlite.tables.UnitsOfMeasureTable;
import ph.com.gs3.loyaltystore.models.values.UnitOfMeasure;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class UnitsOfMeasureDAO extends LoyaltyDBDAO {

    private static final String WHERE_ID_EQUALS = UnitsOfMeasureTable.COLUMN_ID
            + " =?";
    private static final String WHERE_WEB_ID_EQUALS = UnitsOfMeasureTable.COLUMN_WEB_ID
            + " =?";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public UnitsOfMeasureDAO(Context context) throws SQLException {
        super(context);
    }

    public long save(UnitOfMeasure unitOfMeasure) {

        if(!unitExist(unitOfMeasure.getName())){

            ContentValues values = new ContentValues();
            values.put(UnitsOfMeasureTable.COLUMN_ID, unitOfMeasure.getWeb_id());
            values.put(UnitsOfMeasureTable.COLUMN_WEB_ID, unitOfMeasure.getWeb_id());
            values.put(UnitsOfMeasureTable.COLUMN_NAME, unitOfMeasure.getName());
            return database.insert(UnitsOfMeasureTable.TABLE_NAME, null, values);

        }else{
            return update(unitOfMeasure);
        }

    }

    public long saveMultipleData(ArrayList<UnitOfMeasure> unitOfMeasures) {

        for(int i=0;i<unitOfMeasures.size();i++){

            UnitOfMeasure unitOfMeasure = unitOfMeasures.get(i);

            if(!unitExist(unitOfMeasure.getName())){
                ContentValues values = new ContentValues();
                values.put(UnitsOfMeasureTable.COLUMN_ID, unitOfMeasure.getWeb_id());
                values.put(UnitsOfMeasureTable.COLUMN_WEB_ID, unitOfMeasure.getWeb_id());
                values.put(UnitsOfMeasureTable.COLUMN_NAME, unitOfMeasure.getName());

                database.insert(UnitsOfMeasureTable.TABLE_NAME, null, values);
            }else{

                return update(unitOfMeasure);

            }

        }
        return 1;
    }

    public boolean unitExist(String name) {

        String query = "SELECT * FROM " + UnitsOfMeasureTable.TABLE_NAME +
                " WHERE " + UnitsOfMeasureTable.COLUMN_NAME + "='" + name + "'";

        Cursor cursor = database.rawQuery(query, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public long update(UnitOfMeasure unitOfMeasure) {
        ContentValues values = new ContentValues();
        values.put(UnitsOfMeasureTable.COLUMN_NAME, unitOfMeasure.getName());

        long result = database.update(UnitsOfMeasureTable.TABLE_NAME, values,
                WHERE_WEB_ID_EQUALS,
                new String[]{String.valueOf(unitOfMeasure.getWeb_id())});
        Log.d("Update Result:", "=" + result);
        return result;

    }

    public int delete(UnitOfMeasure unitOfMeasure) {
        return database.delete(UnitsOfMeasureTable.TABLE_NAME, WHERE_ID_EQUALS,
                new String[]{unitOfMeasure.getId() + ""});
    }

    public void clearRecords(){
        database.execSQL("delete from "+ UnitsOfMeasureTable.TABLE_NAME);
    }

    //USING query() method
    public ArrayList<UnitOfMeasure> getUnitOfMeasures() {
        ArrayList<UnitOfMeasure> unitOfMeasures = new ArrayList<UnitOfMeasure>();

        Cursor cursor = database.query(UnitsOfMeasureTable.TABLE_NAME,
                new String[]{UnitsOfMeasureTable.COLUMN_ID,
                        UnitsOfMeasureTable.COLUMN_WEB_ID,
                        UnitsOfMeasureTable.COLUMN_NAME}, null, null,
                        null, null, null);

        while (cursor.moveToNext()) {
            UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
            unitOfMeasure.setId(cursor.getInt(0));
            unitOfMeasure.setWeb_id(cursor.getInt(1));
            unitOfMeasure.setName(cursor.getString(2));
            unitOfMeasures.add(unitOfMeasure);
        }
        return unitOfMeasures;
    }

    public UnitOfMeasure getUnitOfMeasureByName(String name) {
        UnitOfMeasure unitOfMeasure = null;

        String sql = "SELECT * FROM " + UnitsOfMeasureTable.TABLE_NAME
                + " WHERE " + UnitsOfMeasureTable.COLUMN_NAME + " = ?";

        Cursor cursor = database.rawQuery(sql, new String[]{name});

        if (cursor.moveToNext()) {
            unitOfMeasure = new UnitOfMeasure();
            unitOfMeasure.setId(cursor.getInt(0));
            unitOfMeasure.setWeb_id(cursor.getInt(1));
            unitOfMeasure.setName(cursor.getString(2));
        }

        return unitOfMeasure;
    }

    public UnitOfMeasure getUnitOfMeasureByWebID(int web_id) {
        UnitOfMeasure unitOfMeasure = null;

        String sql = "SELECT * FROM " + UnitsOfMeasureTable.TABLE_NAME
                + " WHERE " + UnitsOfMeasureTable.COLUMN_WEB_ID + " = ?";

        Cursor cursor = database.rawQuery(sql, new String[]{web_id + ""});

        if (cursor.moveToNext()) {
            unitOfMeasure = new UnitOfMeasure();
            unitOfMeasure.setId(cursor.getInt(0));
            unitOfMeasure.setWeb_id(cursor.getInt(1));
            unitOfMeasure.setName(cursor.getString(2));
        }

        return unitOfMeasure;
    }

}
