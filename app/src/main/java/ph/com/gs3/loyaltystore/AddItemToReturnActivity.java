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
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Bryan-PC on 22/02/2016.
 */
public class AddItemToReturnActivity extends Activity implements
        AddItemToReturnViewFragment.AddItemToReturnViewFragmentEventListener {

    public static final String TAG = AddItemToReturnActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private AddItemToReturnViewFragment addItemToReturnViewFragment;

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
        addItemToReturnViewFragment.save(retailer);

        finish();
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

