package com.wilson.foodorder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wilson.foodorder.Common.Common;
import com.wilson.foodorder.Interface.ItemClickListener;
import com.wilson.foodorder.Model.Category;
import com.wilson.foodorder.ViewHolder.MenuViewHolder;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseDatabase database;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    DatabaseReference category;
    NavigationView navigationView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    TextView fullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "CART SELECTED", Toast.LENGTH_SHORT).show();
            }
        });

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");


        drawer = (DrawerLayout)findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setNavigationViewListner();

        View headerView = navigationView.getHeaderView(0);
        fullName = (TextView) headerView.findViewById(R.id.txtFullName);
        fullName.setText(Common.currentUser.getName());


        recycler_menu = findViewById(R.id.recycler_view);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();

    }
//--------------------------------------------------------------------------------------
    private void loadMenu() {

        //For reference, IN CASE you named the variables different
        //info1 DatabaseReference mCategory;
        //info2 FirebaseRecyclerAdapter firebaseRecyclerAdapter;
        //info3 //RecyclerView (RV)
        //      RecyclerView recyclerView;
        //      RecyclerView.LayoutManager layoutManager;
        //info4 txtMenuName, imageView are the names set in MenuViewHolder.java from menu_item.xml

            FirebaseRecyclerOptions<Category> options1 =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(category, Category.class)
                        .build();

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options1) {


            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                // Bind the image_details object to the BlogViewHolder
                // ...
                final ProgressDialog mDialog = new ProgressDialog(Home.this);  //TODO change progress dialog
                mDialog.setMessage("Loading menu...");
                mDialog.show();
                //set views
                holder.txtMenuName.setText(model.getName());
                Picasso.get()
                        .load(model.getImage())
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                mDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(Home.this,"MESSAGE : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        });

                final Category clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodlist = new Intent(view.getContext(),FoodList.class);
                        foodlist.putExtra("CategoryId",firebaseRecyclerAdapter.getRef(position).getKey());
                        startActivity(foodlist);

                    }
                });
            }
        };
        recycler_menu.setAdapter(firebaseRecyclerAdapter);
    }




//Also for onStart and onStop
//one line needs to be added, so that the activity starts and stops listening to the firebaseRecyclerAdapter
//at the start and the stop, respectively

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
//------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_menu: {
                Toast.makeText(Home.this,"Menu Selected",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.nav_cart: {
                Toast.makeText(Home.this,"Cart Selected",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.nav_orders: {
                Toast.makeText(Home.this,"Orders Selected",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.nav_log_out: {
                Toast.makeText(Home.this,"LogOut Selected",Toast.LENGTH_SHORT).show();
                break;
            }

        }

        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setNavigationViewListner() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.START)){drawer.closeDrawer(Gravity.START);}
        else{super.onBackPressed();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }
}
//STEP 2 COMPLETE
