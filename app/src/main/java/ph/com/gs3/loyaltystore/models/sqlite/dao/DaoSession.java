package ph.com.gs3.loyaltystore.models.sqlite.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Customer;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;

import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CustomerDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasRewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig productDaoConfig;
    private final DaoConfig rewardDaoConfig;
    private final DaoConfig salesDaoConfig;
    private final DaoConfig customerDaoConfig;
    private final DaoConfig salesHasRewardDaoConfig;
    private final DaoConfig salesProductDaoConfig;
    private final DaoConfig storeDaoConfig;
    private final DaoConfig itemReturnDaoConfig;
    private final DaoConfig cashReturnDaoConfig;
    private final DaoConfig expensesDaoConfig;

    private final ProductDao productDao;
    private final RewardDao rewardDao;
    private final SalesDao salesDao;
    private final CustomerDao customerDao;
    private final SalesHasRewardDao salesHasRewardDao;
    private final SalesProductDao salesProductDao;
    private final StoreDao storeDao;
    private final ItemReturnDao itemReturnDao;
    private final CashReturnDao cashReturnDao;
    private final ExpensesDao expensesDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        productDaoConfig = daoConfigMap.get(ProductDao.class).clone();
        productDaoConfig.initIdentityScope(type);

        rewardDaoConfig = daoConfigMap.get(RewardDao.class).clone();
        rewardDaoConfig.initIdentityScope(type);

        salesDaoConfig = daoConfigMap.get(SalesDao.class).clone();
        salesDaoConfig.initIdentityScope(type);

        customerDaoConfig = daoConfigMap.get(CustomerDao.class).clone();
        customerDaoConfig.initIdentityScope(type);

        salesHasRewardDaoConfig = daoConfigMap.get(SalesHasRewardDao.class).clone();
        salesHasRewardDaoConfig.initIdentityScope(type);

        salesProductDaoConfig = daoConfigMap.get(SalesProductDao.class).clone();
        salesProductDaoConfig.initIdentityScope(type);

        storeDaoConfig = daoConfigMap.get(StoreDao.class).clone();
        storeDaoConfig.initIdentityScope(type);

        itemReturnDaoConfig = daoConfigMap.get(ItemReturnDao.class).clone();
        itemReturnDaoConfig.initIdentityScope(type);

        cashReturnDaoConfig = daoConfigMap.get(CashReturnDao.class).clone();
        cashReturnDaoConfig.initIdentityScope(type);

        expensesDaoConfig = daoConfigMap.get(ExpensesDao.class).clone();
        expensesDaoConfig.initIdentityScope(type);

        productDao = new ProductDao(productDaoConfig, this);
        rewardDao = new RewardDao(rewardDaoConfig, this);
        salesDao = new SalesDao(salesDaoConfig, this);
        customerDao = new CustomerDao(customerDaoConfig, this);
        salesHasRewardDao = new SalesHasRewardDao(salesHasRewardDaoConfig, this);
        salesProductDao = new SalesProductDao(salesProductDaoConfig, this);
        storeDao = new StoreDao(storeDaoConfig, this);
        itemReturnDao = new ItemReturnDao(itemReturnDaoConfig, this);
        cashReturnDao = new CashReturnDao(cashReturnDaoConfig, this);
        expensesDao = new ExpensesDao(expensesDaoConfig, this);

        registerDao(Product.class, productDao);
        registerDao(Reward.class, rewardDao);
        registerDao(Sales.class, salesDao);
        registerDao(Customer.class, customerDao);
        registerDao(SalesHasReward.class, salesHasRewardDao);
        registerDao(SalesProduct.class, salesProductDao);
        registerDao(Store.class, storeDao);
        registerDao(ItemReturn.class, itemReturnDao);
        registerDao(CashReturn.class, cashReturnDao);
        registerDao(Expenses.class, expensesDao);
    }
    
    public void clear() {
        productDaoConfig.getIdentityScope().clear();
        rewardDaoConfig.getIdentityScope().clear();
        salesDaoConfig.getIdentityScope().clear();
        customerDaoConfig.getIdentityScope().clear();
        salesHasRewardDaoConfig.getIdentityScope().clear();
        salesProductDaoConfig.getIdentityScope().clear();
        storeDaoConfig.getIdentityScope().clear();
        itemReturnDaoConfig.getIdentityScope().clear();
        cashReturnDaoConfig.getIdentityScope().clear();
        expensesDaoConfig.getIdentityScope().clear();
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public RewardDao getRewardDao() {
        return rewardDao;
    }

    public SalesDao getSalesDao() {
        return salesDao;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public SalesHasRewardDao getSalesHasRewardDao() {
        return salesHasRewardDao;
    }

    public SalesProductDao getSalesProductDao() {
        return salesProductDao;
    }

    public StoreDao getStoreDao() {
        return storeDao;
    }

    public ItemReturnDao getItemReturnDao() {
        return itemReturnDao;
    }

    public CashReturnDao getCashReturnDao() {
        return cashReturnDao;
    }

    public ExpensesDao getExpensesDao() {
        return expensesDao;
    }

}
