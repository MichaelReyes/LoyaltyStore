package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ph.com.gs3.loyaltystore.fragments.AddItemToReturnViewFragment;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Bryan-PC on 22/02/2016.
 */
public class AddItemToReturnActivity extends Activity implements
        AddItemToReturnViewFragment.AddItemToReturnViewFragmentEventListener {

    public static final String TAG = AddItemToReturnActivity.class.getSimpleName();

    public static final String EXTRA_ITEM_RETURN_VALUE = "ITEM_RETURN";
    public static final String EXTRA_ITEM_RETURN_ID = "ITEM_RETURN_ID";

    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private AddItemToReturnViewFragment addItemToReturnViewFragment;

    private long itemReturnId;
    private String itemReturnValue;

    private Retailer retailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_return);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        addItemToReturnViewFragment = (AddItemToReturnViewFragment)
                getFragmentManager().findFragmentByTag(AddItemToReturnViewFragment.TAG);

        if (addItemToReturnViewFragment == null) {
            addItemToReturnViewFragment = new AddItemToReturnViewFragment();
            addItemToReturnViewFragment = AddItemToReturnViewFragment.createInstance();
            getFragmentManager().beginTransaction().add(
                    R.id.container_items_to_return,
                    addItemToReturnViewFragment, AddItemToReturnViewFragment.TAG).commit();
        }

        getExtras();

    }

    private void getExtras(){

        Intent intent = this.getIntent();

        itemReturnId = intent.getLongExtra(EXTRA_ITEM_RETURN_ID,-1);
        itemReturnValue = intent.getStringExtra(EXTRA_ITEM_RETURN_VALUE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelect(String item) {
        addItemToReturnViewFragment.setFieldsVisibilityByItem(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Log.d(TAG, "SELECT FILE");
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
                Log.d(TAG, "REQUEST CAMERA");
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addItemToReturnViewFragment.setImage(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        addItemToReturnViewFragment.setImage(bm);
    }

    @Override
    public void onSelectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemToReturnActivity.this);
        builder.setTitle("Select or Take Image!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    public void onSave() {

        String item = addItemToReturnViewFragment.getItem();

        ItemReturnDao itemReturnDao =
                LoyaltyStoreApplication.getInstance().getSession().getItemReturnDao();

        ItemReturn itemReturn = new ItemReturn();

        CashReturnDao cashReturnDao =
                LoyaltyStoreApplication.getInstance().getSession().getCashReturnDao();

        CashReturn cashReturn = new CashReturn();

        if (addItemToReturnViewFragment.validate(item)) {


            switch (item) {
                case AddItemToReturnViewFragment.ITEM_VALUE_TRAY:

                    if(itemReturnId != -1){

                        itemReturn.setId(itemReturnId);

                    }

                    itemReturn.setItem(item);
                    itemReturn.setQuantity(Float.valueOf(addItemToReturnViewFragment.getQuantityOrAmount()));
                    itemReturn.setRemarks(addItemToReturnViewFragment.getRemarks());
                    itemReturn.setStore_id(retailer.getStoreId());
                    itemReturn.setIs_synced(false);

                    itemReturnDao.insertOrReplaceInTx(itemReturn);

                    break;
                case AddItemToReturnViewFragment.ITEM_VALUE_SPOILAGE:

                    itemReturn.setItem(item);
                    itemReturn.setProduct_name(addItemToReturnViewFragment.getProduct());
                    itemReturn.setQuantity(Float.valueOf(addItemToReturnViewFragment.getQuantityOrAmount()));
                    itemReturn.setRemarks(addItemToReturnViewFragment.getRemarks());
                    itemReturn.setIs_synced(false);
                    itemReturn.setStore_id(retailer.getStoreId());

                    if(itemReturnId != -1){
                        itemReturn.setId(itemReturnId);
                    }

                    itemReturnDao.insertOrReplaceInTx(itemReturn);

                    break;
                case AddItemToReturnViewFragment.ITEM_VALUE_CASH:

                    cashReturn.setItem(item);
                    cashReturn.setAmount(Float.valueOf(addItemToReturnViewFragment.getQuantityOrAmount()));
                    cashReturn.setRemarks(addItemToReturnViewFragment.getRemarks());
                    cashReturn.setType(addItemToReturnViewFragment.getCashType());
                    cashReturn.setIs_synced(false);
                    cashReturn.setStore_id(retailer.getStoreId());

                    if(addItemToReturnViewFragment.getCashType().equals(AddItemToReturnViewFragment.CASH_TYPE_VALUE_CASH_ON_BANK)) {

                        cashReturn.setDeposited_to_bank(addItemToReturnViewFragment.getBank());
                        cashReturn.setTime_of_deposit(addItemToReturnViewFragment.getDepositDateTime());

                    }

                    if(itemReturnId != -1){

                        cashReturn.setId(itemReturnId);

                    }

                    cashReturnDao.insertOrReplaceInTx(cashReturn);

                    break;
            }

            finish();

        }


    }

    @Override
    public void onCancel() {
        finish();
    }

    @Override
    public void onCashTypeSelect(String cashType) {
        addItemToReturnViewFragment.setBankDetailsVisibility(cashType);
    }

    @Override
    public void onViewReady() {

    }
}

