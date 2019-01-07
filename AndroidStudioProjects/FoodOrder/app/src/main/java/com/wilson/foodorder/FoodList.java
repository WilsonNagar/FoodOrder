package com.wilson.foodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wilson.foodorder.Interface.ItemClickListener;
import com.wilson.foodorder.Model.Food;
import com.wilson.foodorder.ViewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutFoodManager;

    FirebaseDatabase database2;
    DatabaseReference foodList;

    String categoryId = "01";
    FirebaseRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

       //Firebase
        database2 = FirebaseDatabase.getInstance();
        foodList = database2.getReference("Food");

        recycler_food = (RecyclerView)findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutFoodManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutFoodManager);

        if(getIntent()!=null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId!=null){
            loadListFood();
        }


    }

    private void loadListFood() {

        //For reference, IN CASE you named the variables different
        //info1 DatabaseReference mCategory;
        //info2 FirebaseRecyclerAdapter firebaseRecyclerAdapter;
        //info3 //RecyclerView (RV)
        //      RecyclerView recyclerView;
        //      RecyclerView.LayoutManager layoutManager;
        //info4 txtMenuName, imageView are the names set in MenuViewHolder.java from menu_item.xml

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(foodList.orderByChild("MenuId").equalTo(categoryId), Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, int position, @NonNull Food model) {
                // Bind the image_details object to the BlogViewHolder
                // ...
                final ProgressDialog mDialog = new ProgressDialog(FoodList.this);  //TODO change progress dialog
                mDialog.setMessage("Loading fooditems...");
                mDialog.show();
                //set views
                holder.food_name.setText(model.getName());
                Picasso.get()
                        .load(model.getImage())
                        .into(holder.food_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                mDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(FoodList.this,"MESSAGE : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        });

                final Food clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(FoodList.this,clickItem.getName(),Toast.LENGTH_SHORT).show();
                        /*Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);*/
                    }
                });
            }
        };
        recycler_food.setAdapter(adapter);
    }

//Also for onStart and onStop
//one line needs to be added, so that the activity starts and stops listening to the firebaseRecyclerAdapter
//at the start and the stop, respectively

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
       adapter.stopListening();
    }
}
