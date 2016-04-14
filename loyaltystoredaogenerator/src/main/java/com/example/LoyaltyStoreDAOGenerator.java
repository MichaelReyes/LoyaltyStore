package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class LoyaltyStoreDAOGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ph.com.gs3.loyaltystore.models.sqlite.dao");

        /*
        Entity product = schema.addEntity("Product");
        product.addImport("com.google.gson.annotations.SerializedName");
        product.addIdProperty().codeBeforeField("@SerializedName(\"ID\")");
        product.addStringProperty("name");
        product.addFloatProperty("unit_cost");
        product.addStringProperty("sku");
        product.addBooleanProperty("is_active");
        */

        Entity product = schema.addEntity("Product");
        product.addImport("com.google.gson.annotations.SerializedName");
        product.addIdProperty().codeBeforeField("@SerializedName(\"ID\")");
        product.addStringProperty("name");
        product.addStringProperty("type").codeBeforeField("@SerializedName(\"rad_type\")");
        product.addFloatProperty("unit_cost");
        product.addStringProperty("sku");
        product.addStringProperty("ts").codeBeforeField("@SerializedName(\"TS\")");
        product.addLongProperty("deduct_product_to_id").codeBeforeField("@SerializedName(\"txt_deducted_id\")");
        product.addStringProperty("deduct_product_to_name").codeBeforeField("@SerializedName(\"txt_deduct_description\")");
        product.addDoubleProperty("deduct_product_to_quantity").codeBeforeField("@SerializedName(\"txt_quantity_deduct\")");
        product.addBooleanProperty("is_active");

        Entity productBreakDown = schema.addEntity("ProductBreakdown");
        productBreakDown.addImport("com.google.gson.annotations.SerializedName");
        productBreakDown.addIdProperty().codeBeforeField("@SerializedName(\"ID\")");
        productBreakDown.addLongProperty("product_id").codeBeforeField("@SerializedName(\"txt_id\")");
        productBreakDown.addStringProperty("ts").codeBeforeField("@SerializedName(\"TS\")");
        productBreakDown.addDoubleProperty("quantity").codeBeforeField("@SerializedName(\"txt_quantity\")");
        productBreakDown.addStringProperty("name").codeBeforeField("@SerializedName(\"txt_description\")");

        Entity reward = schema.addEntity("Reward");
        reward.addImport("com.google.gson.annotations.SerializedName");
        reward.addIdProperty().codeBeforeField("@SerializedName(\"ID\")");
        reward.addStringProperty("reward_condition");
        reward.addIntProperty("condition_product_id");
        reward.addStringProperty("condition");
        reward.addFloatProperty("condition_value");
        reward.addStringProperty("reward_type");
        reward.addStringProperty("reward");
        reward.addStringProperty("reward_value");
        reward.addDateProperty("valid_from");
        reward.addDateProperty("valid_until");
        reward.addDateProperty("created_at").codeBeforeField("@SerializedName(\"DateCreated\")");
        reward.addDateProperty("updated_at").codeBeforeField("@SerializedName(\"DateUpdated\")");

        Entity sales = schema.addEntity("Sales");
        sales.addIdProperty().autoincrement().primaryKey();
        sales.addStringProperty("transaction_number");
        sales.addLongProperty("store_id");
        sales.addLongProperty("customer_id");
        sales.addFloatProperty("amount");
        sales.addFloatProperty("total_discount");
        sales.addBooleanProperty("is_synced");
        sales.addDateProperty("transaction_date");
        sales.addStringProperty("remarks");

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
        store.addImport("com.google.gson.annotations.SerializedName");
        store.addIdProperty().codeBeforeField("@SerializedName(\"ID\")");
        store.addStringProperty("device_id");
        store.addLongProperty("device_web_id");
        store.addStringProperty("name");
        store.addIntProperty("is_active");
        store.addDateProperty("created_at");
        store.addDateProperty("updated_at");

        Entity itemsReturn = schema.addEntity("ItemReturn");
        itemsReturn.addIdProperty().autoincrement();
        itemsReturn.addLongProperty("store_id");
        itemsReturn.addStringProperty("item");
        itemsReturn.addStringProperty("product_name");
        itemsReturn.addFloatProperty("quantity");
        itemsReturn.addStringProperty("remarks");
        itemsReturn.addBooleanProperty("is_synced");

        Entity cashReturn = schema.addEntity("CashReturn");
        cashReturn.addIdProperty().autoincrement();
        cashReturn.addLongProperty("store_id");
        cashReturn.addStringProperty("item");
        cashReturn.addStringProperty("type");
        cashReturn.addFloatProperty("amount");
        cashReturn.addStringProperty("remarks");
        cashReturn.addStringProperty("deposited_to_bank");
        cashReturn.addDateProperty("time_of_deposit");
        cashReturn.addStringProperty("Image");
        cashReturn.addBooleanProperty("is_synced");

        Entity expenses = schema.addEntity("Expenses");
        expenses.addIdProperty().autoincrement();
        expenses.addLongProperty("store_id");
        expenses.addStringProperty("description");
        expenses.addFloatProperty("amount");
        expenses.addDateProperty("date");
        expenses.addBooleanProperty("is_synced");

        Entity productDelivery = schema.addEntity("ProductDelivery");
        productDelivery.addImport("com.google.gson.annotations.SerializedName");
        productDelivery.addIdProperty().autoincrement();
        productDelivery.addStringProperty("distribution_type").codeBeforeField("@SerializedName(\"rad_type\")");
        productDelivery.addLongProperty("product_id");
        productDelivery.addFloatProperty("cash").codeBeforeField("@SerializedName(\"txt_cash\")");
        productDelivery.addStringProperty("name");
        productDelivery.addLongProperty("delivered_to_store_id");
        productDelivery.addStringProperty("delivered_to_store_name");
        productDelivery.addIntProperty("delivered_by_agent_id");
        productDelivery.addStringProperty("delivered_by_agent_name");
        productDelivery.addDoubleProperty("quantity");
        productDelivery.addDateProperty("date_delivered");
        productDelivery.addStringProperty("status");
        productDelivery.addBooleanProperty("is_synced");

        Entity itemInventory = schema.addEntity("ItemInventory");
        itemInventory.addIdProperty().autoincrement();
        itemInventory.addLongProperty("product_id");
        itemInventory.addStringProperty("name");
        itemInventory.addDoubleProperty("quantity");

        Entity itemInventoryStockCount = schema.addEntity("ItemStockCount");
        itemInventoryStockCount.addIdProperty().autoincrement();
        itemInventoryStockCount.addLongProperty("product_id");
        itemInventoryStockCount.addStringProperty("name");
        itemInventoryStockCount.addDoubleProperty("expectedQuantity");
        itemInventoryStockCount.addDoubleProperty("quantity");
        itemInventoryStockCount.addStringProperty("remarks");
        itemInventoryStockCount.addDateProperty("date_counted");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }

}
