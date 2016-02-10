package ph.com.gs3.loyaltystore.models.sqlite.tables;

/**
 * Created by Bryan-PC on 04/02/2016.
 */
public class RewardsTable {

    public static final String TABLE_NAME = "tblRewards";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WEB_ID = "web_id";
    public static final String COLUMN_REWARD_CONDITION = "reward_condition";
    public static final String COLUMN_CONDITION_PRODUCT_ID = "condition_product_id";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_CONDITION_VALUE = "condition_value";
    public static final String COLUMN_REWARD_TYPE = "reward_type";
    public static final String COLUMN_REWARD = "reward";
    public static final String COLUMN_REWARD_VALUE = "reward_value";
    public static final String COLUMN_VALID_FROM = "valid_from";
    public static final String COLUMN_VALID_UNTIL = "valid_until";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";


    public static final String CREATION_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_WEB_ID + " INTEGER, "

            + COLUMN_REWARD_CONDITION + " TEXT, "

            + COLUMN_CONDITION_PRODUCT_ID + " INTEGER DEFAULT 0, "

            + COLUMN_CONDITION + " TEXT, "
            + COLUMN_CONDITION_VALUE + " TEXT, "
            + COLUMN_REWARD_TYPE + " TEXT, "
            + COLUMN_REWARD + " TEXT, "
            + COLUMN_REWARD_VALUE + " TEXT, "

            + COLUMN_VALID_FROM + " TEXT NOT NULL, "
            + COLUMN_VALID_UNTIL + " TEXT NOT NULL, "

            + COLUMN_IS_ACTIVE + " INTEGER DEFAULT 0, "

            + COLUMN_CREATED_AT + " TEXT NOT NULL, "
            + COLUMN_UPDATED_AT + " TEXT NOT NULL "

            + ");";

}
