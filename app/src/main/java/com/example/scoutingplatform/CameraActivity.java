package com.example.scoutingplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

public class CameraActivity extends AppCompatActivity {

//    Button addImage;
//    ArrayList<Contact> imageArry = new ArrayList<Contact>();
//    ContactImageAdapter imageAdapter;
//    private static final int CAMERA_REQUEST = 1;
//
//    ListView dataList;
//    byte[] imageName;
//    int imageId;
//    Bitmap theImage;
//    DataBaseHandler db;
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        dataList = (ListView) findViewById(R.id.list);
//        /**
//         * create DatabaseHandler object
//         */
//        db = new DataBaseHandler(this);
//        /**
//         * Reading and getting all records from database
//         */
//        List<Contact> contacts = db.getAllContacts();
//        for (Contact cn : contacts) {
//            String log = "ID:" + cn.getID() + " Name: " + cn.getName()
//                    + " ,Image: " + cn.getImage();
//
//            // Writing Contacts to log
//            Log.d("Result: ", log);
//            // add contacts data in arrayList
//            imageArry.add(cn);
//
//        }
//        /**
//         * Set Data base Item into listview
//         */
//        imageAdapter = new ContactImageAdapter(this, R.layout.screen_list,
//                imageArry);
//        dataList.setAdapter(imageAdapter);
//
//        /**
//         * open dialog for choose camera
//         */
//
//        final String[] option = new String[] {"Take from Camera"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.select_dialog_item, option);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Select Option");
//        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                Log.e("Selected Item", String.valueOf(which));
//                if (which == 0) {
//                    callCamera();
//                }
//
//
//            }
//        });
//        final AlertDialog dialog = builder.create();
//
//        addImage = (Button) findViewById(R.id.btnAdd);
//
//        addImage.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.show();
//            }
//        });
//
//    }
//
//    /**
//     * On activity result
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK)
//            return;
//
//        switch (requestCode) {
//            case CAMERA_REQUEST:
//
//                Bundle extras = data.getExtras();
//
//                if (extras != null) {
//                    Bitmap yourImage = extras.getParcelable("data");
//                    // convert bitmap to byte
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    yourImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte imageInByte[] = stream.toByteArray();
//
//                    // Inserting Contacts
//                    Log.d("Insert: ", "Inserting ..");
//                    db.addContact(new Contact("Android", imageInByte));
//                    Intent i = new Intent(CameraPictureActivity.this,
//                            CameraPictureActivity.class);
//                    startActivity(i);
//                    finish();
//
//                }
//                break;
//        }
//    }
//
//    /**
//     * open camera method
//     */
//    public void callCamera()
//    {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA_REQUEST);
//        intent.setType("image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 0);
//        intent.putExtra("aspectY", 0);
//        intent.putExtra("outputX", 250);
//        intent.putExtra("outputY", 200);
//    }

}
