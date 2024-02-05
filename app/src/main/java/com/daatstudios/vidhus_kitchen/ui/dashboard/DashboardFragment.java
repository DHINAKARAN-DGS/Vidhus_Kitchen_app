package com.daatstudios.vidhus_kitchen.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daatstudios.vidhus_kitchen.OrdersAdapter;
import com.daatstudios.vidhus_kitchen.OrdersModel;
import com.daatstudios.vidhus_kitchen.R;
import com.daatstudios.vidhus_kitchen.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    RecyclerView recyclerView;
    List<OrdersModel> ordersModelList = new ArrayList<>();

    OrdersAdapter adapter;

    TextView top_txt;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        top_txt = root.findViewById(R.id.text_dashboard);


        firestore.collection("Users").document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = task.getResult().getString("Name");
                    top_txt.setText("Hi "+name + "!\nNithya Medicals here");
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OrdersAdapter(ordersModelList);

        firestore.collection("Orders").whereEqualTo("UID", FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        ordersModelList.add(new OrdersModel(documentSnapshot.getString("OrderID"),
                                documentSnapshot.getString("Products")+"\nPoints to remember: "+documentSnapshot.getString("ADD ONs"),
                                documentSnapshot.getString("Price"),
                                documentSnapshot.getString("Status"),
                                documentSnapshot.getString("url"),
                                documentSnapshot.getString("Ordered Date"),documentSnapshot.getId()));

                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }

                }
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}