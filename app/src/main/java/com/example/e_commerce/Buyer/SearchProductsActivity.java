package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.e_commerce.Model.Product;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolders.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchProductsActivity extends AppCompatActivity {

    EditText inputText;
    Button search_btn;
    RecyclerView searchList;
    String searchText;
    DatabaseReference reference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        reference = FirebaseDatabase.getInstance().getReference().child("products");

        inputText=findViewById(R.id.search_text);
        search_btn=findViewById(R.id.text_search_btn);
        searchList=findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchList.setHasFixedSize(true);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText=inputText.getText().toString().toLowerCase();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

            FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                    .setQuery(reference.orderByChild("pName").startAt(searchText) , Product.class)
                    .build();
            FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                    holder.product_name.setText(model.getpName());
                    holder.product_description.setText(model.getDescription());
                    holder.product_price.setText("price: " + model.getPrice() + "$");
                    Picasso.get().load(model.getImageUrl()).into(holder.product_image);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchProductsActivity.this, Product_DetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_view, parent, false);
                    return new ProductViewHolder(view);
                }
            };
            searchList.setAdapter(adapter);
            adapter.startListening();
        }

}
