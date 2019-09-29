package com.example.mdbinventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DataActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int DELETE_TRANSACTION = 123;
    private EditText cost, description, suppliers, dateText;
    private EditText[] textArr;
    private Button upload, chooseImage, show, logout;
    private ImageView imageView;
    CardView entryMenu, entryBody;
    AutoCompleteTextView search;
    Spinner toggle_order;

    private final int PICK_IMAGE_REQUEST = 71;

    private Uri filePath;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    SharedPreferences sharedPref;

    // recycler stuff
    RecyclerView recycler;
    RecyclerView.LayoutManager linearManager;
    Adapter adapter;

    List<Transaction> data;
    ArrayList<String> description_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        show = findViewById(R.id.menuButton);
        cost = findViewById(R.id.cost);
        description = findViewById(R.id.description);
        suppliers = findViewById(R.id.supplier);
        dateText = findViewById(R.id.date);
        imageView = findViewById(R.id.imageView);
        upload = findViewById(R.id.upload);
        chooseImage = findViewById(R.id.imageButton);
        entryMenu = findViewById(R.id.entry_title);
        entryBody = findViewById(R.id.transactionMenu);
        search = findViewById(R.id.search);
        logout = findViewById(R.id.logout);
        toggle_order = findViewById(R.id.date_spinner);

        upload.setOnClickListener(this);
        chooseImage.setOnClickListener(this);
        show.setOnClickListener(this);
        logout.setOnClickListener(this);
        toggle_entry(false); // hide entry

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("transactions");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = DataManagement.readSnapshot(dataSnapshot);
                setUpRecyclerView();
                Collections.sort(data);
                resetDescriptionList();
                resort_description_list();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        initFireBaseDataChange();

        sharedPref = getPreferences(MODE_PRIVATE);
        Log.d("h", getIntent().getStringExtra("email"));
        Log.d("hi", "asldjfka;lsdjf");
        String last_search = sharedPref.getString(getIntent().getStringExtra("email"), "");
        if (!last_search.isEmpty()) search.setText(last_search);

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = adapterView.getItemAtPosition(i);
                if (o instanceof String) {
                    // go to next fragment
                    String target_description = (String) o;
                    Log.d("i", target_description);

                    resetDescriptionList(); // to match indices of data
                    int idx = -1;
                    for (int x=0; x<description_list.size(); x++) {
                        if (description_list.get(x).equals(target_description)) {
                            idx = x;
                            break;
                        }
                    }
                    if (idx == -1) Log.d("e", "index should not be -1");
                    resort_description_list(); // to reset alphabetically

                    Transaction t = data.get(idx);
                    Intent intent = new Intent(DataActivity.this, Transaction_Info.class);
                    intent.putExtra("cost", t.cost);
                    intent.putExtra("description", t.description);
                    intent.putExtra("supplier", t.suppliers);
                    intent.putExtra("date", t.date);
                    intent.putExtra("url", t.url);

                    sharedPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getIntent().getStringExtra("email"), t.description);
                    editor.commit();

                    // search.setText("");
                    startActivity(intent);
                } else {
                    Log.d("i", "Error: Unable to select item");
                }
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                toggle_entry(true);
            }
        });
        toggle_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (data != null) {
                    reverseOrder();
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                chooseImage();
                break;
            case R.id.upload:
                if (checkValidity()) {
                    uploadData();
                }
                break;
            case R.id.menuButton:
                toggle_entry(false);
                break;
            case R.id.logout:
                finish();
                break;
        }
    }

    private void setUpRecyclerView() {
        recycler = findViewById(R.id.recycler);
        linearManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearManager); // default layout is linear
        adapter = new Adapter(this, data);
        recycler.setAdapter(adapter);
    }

    void resetDescriptionList() {
        description_list = new ArrayList<>();
        for (Transaction t : data) {
            description_list.add(t.description);
        }
    }

    void resort_description_list() {
        Collections.sort(description_list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    void reverseOrder() {
        Collections.reverse(data);
        adapter.updateData(data);
    }

    void toggle_entry(boolean keep_hidden) {
        if (keep_hidden || entryBody.getVisibility() == View.VISIBLE) { // hide it
            show.setText(R.string.show);
            entryBody.setVisibility(View.GONE);
        } else { // show it
            show.setText(R.string.hide);
            entryBody.setVisibility(View.VISIBLE);
        }
    }

    private void initFireBaseDataChange() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = DataManagement.readSnapshot(dataSnapshot);
                adapter.updateData(data);
                resetDescriptionList();
                resort_description_list();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DataActivity.this, android.R.layout.simple_dropdown_item_1line, description_list);
                search.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(MainActivity.TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public static boolean isEmpty(EditText text) {
        return text.getText().toString().trim().isEmpty();
    }

    /**
     * Checks if date is formated like mm/dd/yyyy
     */
    private static boolean isDateFormatted(String date) {
        // check if the format is ##/##/####
        if (date.length() == 10) {
            for (int i = 0; i < date.length(); i++) {
                if (i == 2 || i == 5) {
                    if (date.charAt(i) != '/') {
                        return false;
                    }
                } else if (!Character.isDigit(date.charAt(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }

        int month = Integer.parseInt(date.substring(0, 2));
        int day = Integer.parseInt(date.substring(3, 5));
        int days[] = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // final check to see if month and days are in valid ranges
        return (month > 0 && month < 13) && (day > 0 && day <= days[month]);
    }


    private boolean checkValidity() {

        boolean returnVal = true;

        if (isEmpty(cost)) {
            cost.setError("Must enter a cost");
            returnVal = false;
        }

        if (isEmpty(suppliers)) {
            suppliers.setError("Must enter a supplier");
            returnVal = false;
        }

        if (isEmpty(description)) {
            description.setError("Must enter a description");
            returnVal = false;
        }

        if (isEmpty(dateText)) {
            dateText.setError("Must enter a date");
            returnVal = false;
        }

        if (!isDateFormatted(dateText.getText().toString())) {
            dateText.setError("Enter a date as mm/dd/yyyy");
            returnVal = false;
        }

        return returnVal;
    }

    public Transaction findTransaction(String supplier, String date) {
        for (Transaction t : data) {
            if (t.suppliers.equals(supplier) && t.date.equals(date)) {
                return t;
            }
        }
        Log.d("ERROR:","TRANSACTION NOT IN DATA LIST");
        return null;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        } else if (requestCode == DELETE_TRANSACTION && resultCode == RESULT_OK) {
            DataManagement.delete(data.getStringExtra("key"), databaseReference);
        }
    }


    private void clear() {
        cost.setText("");
        description.setText("");
        suppliers.setText("");
        dateText.setText("");
        imageView.setImageResource(R.drawable.transaction);
    }

    /**
     * Uploads the current image and returns a downloadable (read-only) link.
     */
    private void uploadData() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID() + ".png");

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(DataActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(DataActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+ (int) progress+"%");
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(DataActivity.this, new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                //this is the download url for the image -- save it with the Transaction object so that we can later display the image again
                                String downloadUrl = task.getResult().toString();

                                Transaction currTransaction =
                                        new Transaction(DataManagement.getKey(databaseReference), cost.getText().toString(), description.getText().toString(),
                                                suppliers.getText().toString(), dateText.getText().toString(), downloadUrl);

                                DataManagement.writeNewTransaction(currTransaction, FirebaseDatabase.getInstance().getReference());
                                clear();
                            }
                        }
                    }
            );
        } else {
            Toast.makeText(DataActivity.this, "Please Choose an Image", Toast.LENGTH_SHORT).show();
        }
    }
}
