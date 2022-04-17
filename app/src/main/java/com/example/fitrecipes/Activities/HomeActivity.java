package com.example.fitrecipes.Activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.example.fitrecipes.Models.RecipeAdapter;
import com.example.fitrecipes.Models.RecipeModel;
import com.example.fitrecipes.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    private RecipeModel recipeModel;
    private SliderLayout sliderLayout;
    private RecyclerView recyclerView;
    ArrayList<RecipeModel> recipeModelArrayList;
    ArrayList<RecipeModel> sliderRecipeList;
    SlidingRootNav slidingRootNav;
    TextView name,phone,emailAddress,tv_changePass;
    ImageView iv_pic,iv_edPic;
    RecipeAdapter recipeAdapter;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    Context context;
    private EditText et_search;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,databaseReference3;
    private static final String USERS = "users";
    private String myUri = "";
    private String uuid = "";
    private String USERID = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("recipes");

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.activity_drawer)
                .withMenuOpened(false)
                .inject();

        context = this;
        uuid = LoginActivity.UUID;
        USERID= getIntent().getExtras().getString("uuid");
        TextView name1 = findViewById(R.id.name);
        et_search = findViewById(R.id.et_search);
        sliderLayout = findViewById(R.id.slider);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3333);
        recyclerView = findViewById(R.id.recyclerview);
        recipeModelArrayList = new ArrayList();
        sliderRecipeList = new ArrayList();

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(USERS);
        setListeners();
        init();
        //things added
        FirebaseRecyclerOptions<RecipeModel> options2 = new FirebaseRecyclerOptions.Builder<RecipeModel>().setQuery(databaseReference3, RecipeModel.class).build();
        recipeAdapter=new RecipeAdapter(options2);
        recyclerView.setAdapter(recipeAdapter);
        //things added stop

        StorageReference riversRef = storageReference.child("M1.jpg");
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(imageUri).into(iv_pic);

            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(uuid.equals(ds.getKey()))
                    {
                        emailAddress.setText(ds.child("email").getValue(String.class));
                        name.setText(ds.child("name").getValue(String.class));
                        phone.setText(ds.child("phone").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void setListeners(){
        findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutUser();
            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddRecipeActivity.class).putExtra("uuid",USERID));
            }
        });
        findViewById(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.openMenu();
            }
        });
        findViewById(R.id.btn_my_recipes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MyRecipesActivity.class));
            }
        });

        findViewById(R.id.btn_edit_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });
        findViewById(R.id.iv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

    }
    private void init(){

        name = findViewById(R.id.tv_name);
        emailAddress = findViewById(R.id.tv_email);
        phone = findViewById(R.id.tv_phone);
        iv_pic = findViewById(R.id.iv_profilePic);
        storage = FirebaseStorage.getInstance();
        iv_edPic = findViewById(R.id.iv_edit);
        storageReference = storage.getReference();

    }
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            iv_pic.setImageURI(imageUri);
            uploadPicture();
        }
    }
    private void uploadPicture() {
       final ProgressDialog pd =new ProgressDialog(this);
       pd.setTitle("Uploading Image.....");
       pd.show();
       final String randomKey = UUID.randomUUID().toString();
      StorageReference riversRef = storageReference.child("images/" + randomKey);

       try {

           riversRef.putFile(imageUri)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           pd.dismiss();
                           Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                           Picasso.get().load(imageUri).into(iv_pic);

                          /* Bitmap bitmap  = BitmapFactory.decodeFile(imageUri.getPath());
                           ((ImageView)findViewById(R.id.iv_profilePic)).setImageBitmap(bitmap);*/
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           pd.dismiss();
                           Toast.makeText(context, "Failed To Upload", Toast.LENGTH_SHORT).show();
                       }
                   })
                   .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                           double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                           pd.setMessage(" Progress:" + (int) progressPercent + "%");

                       }
                   });
       }catch (Exception e){

       }
    }

    private void signOutUser() {
        mAuth.signOut();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        uuid = LoginActivity.UUID;
    }
    public void onStart() {
        super.onStart();
        recipeAdapter.startListening();
    }
    public void onStop(){
        super.onStop();
        FirebaseAuth.getInstance().signOut();
        recipeAdapter.stopListening();

        }

}