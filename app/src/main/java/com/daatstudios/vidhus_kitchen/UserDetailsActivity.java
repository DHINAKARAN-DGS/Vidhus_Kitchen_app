package com.daatstudios.vidhus_kitchen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserDetailsActivity extends AppCompatActivity {

    Button btn;
    EditText name, address, no1, no2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        btn = findViewById(R.id.button4);
        name = findViewById(R.id.name_ip);
        address = findViewById(R.id.address_ip);
        no1 = findViewById(R.id.c1);
        no2 = findViewById(R.id.c2);



        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        btn.setOnClickListener(v -> {
            if (!name.getText().toString().isEmpty()) {
                if (!address.getText().toString().isEmpty()) {
                    if (!no1.getText().toString().isEmpty()) {
                        if (!no2.getText().toString().isEmpty()) {
                            Map<String, Object> usermap = new HashMap<>();
                            usermap.put("Name", name.getText().toString());
                            usermap.put("Address", address.getText().toString());
                            usermap.put("Cont1", no1.getText().toString());
                            usermap.put("Cont2", no2.getText().toString());
                            firestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid())).update(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(UserDetailsActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(this, "Please enter the details | All are mandatory", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please enter the details | All are mandatory", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please enter the details | All are mandatory", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter the details | All are mandatory", Toast.LENGTH_SHORT).show();
            }
        });

    }
}