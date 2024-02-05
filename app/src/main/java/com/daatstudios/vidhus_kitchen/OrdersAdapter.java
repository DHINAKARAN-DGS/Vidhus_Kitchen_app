package com.daatstudios.vidhus_kitchen;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VIEWHOLDER> {

    List<OrdersModel> ordersModelList;

    public OrdersAdapter(List<OrdersModel> ordersModelList) {
        this.ordersModelList = ordersModelList;
    }

    @NonNull
    @Override
    public OrdersAdapter.VIEWHOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_view, parent, false);
        return new VIEWHOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.VIEWHOLDER holder, int position) {
        String id = ordersModelList.get(position).getOrderID();
        String products = ordersModelList.get(position).getProducts();
        String price = ordersModelList.get(position).getPrice();
        String status = ordersModelList.get(position).getStatus();
        String image = ordersModelList.get(position).getPrescription();
        String date = ordersModelList.get(position).getDate();
        String docID = ordersModelList.get(position).getDocID();
        holder.setData(id, products, price, status, image, date,docID);

    }

    @Override
    public int getItemCount() {
        return ordersModelList.size();
    }

    public static class VIEWHOLDER extends RecyclerView.ViewHolder {

        TextView OID, Products, Price, Status;
        Button makePayment, ImageBtn;

        public VIEWHOLDER(@NonNull View itemView) {
            super(itemView);
            OID = itemView.findViewById(R.id.order_id);
            Products = itemView.findViewById(R.id.order_items);
            Price = itemView.findViewById(R.id.order_price);
            Status = itemView.findViewById(R.id.order_status);
            makePayment = itemView.findViewById(R.id.pay_now_btn);
            ImageBtn = itemView.findViewById(R.id.view_pres_btn);
        }

        public void setData(String id, String products, String price, String status, String img, String date,String docID) {
            OID.setText("Order ID: " + id);
            Products.setText(products);
            Price.setText("₹" + price);
            Status.setText("Order Status: " + status);

            if (status.equals("Waiting for approval")) {
                makePayment.setOnClickListener(v -> {
                    Toast.makeText(itemView.getContext(), "Please wait for order acceptance...", Toast.LENGTH_SHORT).show();
                });
            } else {
                makePayment.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), PaymentsActivity.class);
                    intent.putExtra("OID", id);
                    intent.putExtra("Products", products);
                    intent.putExtra("Price", price);
                    intent.putExtra("DOCID", docID);
                    itemView.getContext().startActivity(intent);
                });

            }

            if (price.equals("")) {
                Price.setText("Ordered date: " + date);
            }

            ImageBtn.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                intent.putExtra("img", img);
                itemView.getContext().startActivity(intent);
            });
        }
    }


}
