package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class LoyaltyStoreDAOGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ph.com.gs3.loyaltystore.models.sqlite.dao");

        Entity product = schema.addEntity("Product");
        product.addIdProperty();
        product.addStringProperty("name");
        product.addFloatProperty("unit_cost");
        product.addStringProperty("sku");
        product.addBooleanProperty("is_active");

        Entity reward = schema.addEntity("Reward");
        reward.addIdProperty();
        reward.addStringProperty("reward_condition");
        reward.addIntProperty("condition_product_id");
        reward.addStringProperty("condition");
        reward.addFloatProperty("condition_value");
        reward.addStringProperty("reward_type");
        reward.addStringProperty("reward");
        reward.addStringProperty("reward_value");
        reward.addDateProperty("valid_from");
        reward.addDateProperty("valid_until");
        reward.addDateProperty("created_at");
        reward.addDateProperty("updated_at");

        Entity sales = schema.addEntity("Sales");
        sales.addIdProperty().autoincrement().primaryKey();
        sales.addStringProperty("transaction_number");
        sales.addLongProperty("store_id");
        sales.addLongProperty("customer_id");
        sales.addFloatProperty("amount");
        sales.addFloatProperty("total_discount");
        sales.addBooleanProperty("is_synced");
        sales.addDateProperty("transaction_date");

        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name");
        customer.addStringProperty("device_id");
        customer.addStringProperty("contact_number");
        customer.addStringProperty("location");
        customer.addStringProperty("gender");
        customer.addDateProperty("birth_date");
        customer.addIntProperty("points");
        customer.addDateProperty("created_at");
        customer.addDateProperty("updated_at");

        Entity salesHasReward = schema.addEntity("SalesHasReward");
        salesHasReward.addIdProperty().autoincrement();
        salesHasReward.addLongProperty("reward_id");
        salesHasReward.addStringProperty("sales_transaction_number");

        Entity salesProduct = schema.addEntity("SalesProduct");
        salesProduct.addIdProperty().autoincrement().primaryKey();
        salesProduct.addStringProperty("sales_transaction_number");
        salesProduct.addLongProperty("product_id");
        salesProduct.addIntProperty("quantity");
        salesProduct.addFloatProperty("sub_total");
        salesProduct.addStringProperty("sale_type");

        Entity store = schema.addEntity("Store");
        store.addIdProperty();
        store.addStringProperty("device_id");
        store.addIntProperty("device_web_id");
        store.addStringProperty("name");
        store.addIntProperty("is_active");
        store.addDateProperty("created_at");
        store.addDateProperty("updated_at");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }

}
