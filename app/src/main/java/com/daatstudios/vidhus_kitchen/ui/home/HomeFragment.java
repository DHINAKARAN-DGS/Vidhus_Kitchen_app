package com.daatstudios.vidhus_kitchen.ui.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.daatstudios.vidhus_kitchen.R;
import com.daatstudios.vidhus_kitchen.UserDetailsActivity;
import com.daatstudios.vidhus_kitchen.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String name = "", imgUrl = "";

    TextView toptxt, cname, caddress;

    EditText addons;

    ImageView pres,edit;

    Button upload, placeOrder;

    ProgressDialog loadingDialog ;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadingDialog =  new ProgressDialog(getActivity());
        loadingDialog.setTitle("Loading....");
        loadingDialog.setMessage("Prescription Uploading....");
        loadingDialog.setCancelable(false);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        toptxt = root.findViewById(R.id.text_home2);
        cname = root.findViewById(R.id.card_name);
        caddress = root.findViewById(R.id.card_address);
        upload = root.findViewById(R.id.button2);
        placeOrder = root.findViewById(R.id.button3);
        addons = root.findViewById(R.id.medicine_details);
        pres = root.findViewById(R.id.preview);
        edit = root.findViewById(R.id.editInfo);

        edit.setOnClickListener(v->{
            Intent in = new Intent(root.getContext(), UserDetailsActivity.class);
            in.putExtra("from","HOME");
            startActivity(in);
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        firestore.collection("Users").document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    name = task.getResult().getString("Name");
                    toptxt.setText("Hi "+name + "!\nHave a Good Day");
                    cname.setText(name);
                    caddress.setText(task.getResult().getString("Address"));
                }
            }
        });


        upload.setOnClickListener(v -> {
            pickImage();
        });

        placeOrder.setOnClickListener(v -> {

            UUID uuid = UUID.randomUUID();
            if (!imgUrl.equals("")) {
                Map<String, Object> map = new HashMap<>();
                map.put("url", imgUrl);
                map.put("ADD ONs", addons.getText().toString());
                map.put("UID",FirebaseAuth.getInstance().getUid());
                map.put("OrderID",uuid.toString());
                map.put("Status","Waiting for approval");
                map.put("Price","");
                map.put("Products","");
                map.put("Ordered Date",formattedDate);
                firestore.collection("Orders").document().set(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Order Placed", Toast.LENGTH_SHORT).show();
                        addons.setText("");
                        imgUrl = "";
                        pres.setImageURI(Uri.parse(imgUrl));
                    }else{
                        Toast.makeText(getActivity(), "Error :( Retry...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please Upload Prescription", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }


    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                System.out.printf("error");
                return;
            }loadingDialog.show();
            System.out.println("success");
            Uri uri = data.getData();
            String src = uri.getPath();
            System.out.println("B" + uri);
            pres.setImageURI(uri);

            FirebaseStorage storage = FirebaseStorage.getInstance();

            UUID uuid = UUID.randomUUID();

            String name = FirebaseAuth.getInstance().getUid() + " UUID: " + uuid.toString();
            StorageReference ref = storage.getReference().child("Prescriptions").child(name);

            UploadTask uploadTask = ref.putFile(uri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imgUrl = downloadUri.toString();
                        loadingDialog.setTitle("Done...:) !");
                        loadingDialog.setMessage(":)");
                        Toast.makeText(getActivity(), "Image Uploaded.. :)", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    } else {
                        loadingDialog.setTitle("Failed.. ");
                        loadingDialog.setMessage(":( Retry Image uploading...");
                        Toast.makeText(getActivity(), "Failed.. :( Retry Image uploading...", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}