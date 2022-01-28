package com.example.hbs.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbs.BuildConfig;
import com.example.hbs.Fragments.DashboardFragment;
import com.example.hbs.Fragments.IncomeFragment;
import com.example.hbs.Model.Data;
import com.example.hbs.R;
import com.example.hbs.Utilities.Constants;
import com.example.hbs.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private CardView month, transport, grocery, education, health, entertainment, cloth, rent, fastFood, other, shoe;
    TextView incomeMonth, expensesMonth;
    FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    int totalIncomeData, totalExpensesData;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //floating button textview
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen = false;
    private Animation fabOpen, fabClose;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        preferenceManager = new PreferenceManager(getApplicationContext());
        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.drawer_main_layout);
        navigationView = findViewById(R.id.main_navView);
        month = findViewById(R.id.monthly);
        transport = findViewById(R.id.transport);
        grocery = findViewById(R.id.grocery);
        education = findViewById(R.id.education);
        health = findViewById(R.id.health);
        entertainment = findViewById(R.id.entertainment);
        cloth = findViewById(R.id.clothing);
        rent = findViewById(R.id.rent);
        fastFood = findViewById(R.id.fastFood);
        other = findViewById(R.id.other);
        shoe = findViewById(R.id.shoe);
        incomeMonth = findViewById(R.id.income_month);
        expensesMonth = findViewById(R.id.expense_month);
        fab_main_btn = findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = findViewById(R.id.income_ft_btn);
        fab_expense_btn = findViewById(R.id.expense_ft_btn);
        fab_expense_txt = findViewById(R.id.expense_ft_text);
        fab_income_txt = findViewById(R.id.income_ft_text);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_view, R.string.close_nav_view);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fabOpen = AnimationUtils.loadAnimation(CategoryActivity.this, R.anim.fade_open);
        fabClose = AnimationUtils.loadAnimation(CategoryActivity.this, R.anim.fade_close);


        fab_main_btn.setOnClickListener(v -> {
            if (isOpen) {
                fab_income_btn.startAnimation(fabClose);
                fab_expense_btn.startAnimation(fabClose);
                fab_income_btn.setClickable(false);
                fab_expense_btn.setClickable(false);

                fab_income_txt.startAnimation(fabClose);
                fab_expense_txt.startAnimation(fabClose);
                fab_income_txt.setClickable(false);
                fab_expense_txt.setClickable(false);
                isOpen = false;
            } else {
                fab_income_btn.startAnimation(fabOpen);
                fab_expense_btn.startAnimation(fabOpen);
                fab_income_btn.setClickable(true);
                fab_expense_btn.setClickable(true);

                fab_income_txt.startAnimation(fabOpen);
                fab_expense_txt.startAnimation(fabOpen);
                fab_income_txt.setClickable(true);
                fab_expense_txt.setClickable(true);
                isOpen = true;
            }
        });
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(CategoryActivity.this);
        setTotalData();
        onclickCards();
        addData();

//        if(totalExpensesData>totalIncomeData){
//            Toast.makeText(this, "Your Expenses Is Incressed From Income", Toast.LENGTH_SHORT).show();
//        }else if(totalIncomeData==totalExpensesData){
//            Toast.makeText(this, "Your Income and Expenses is Equal", Toast.LENGTH_SHORT).show();
//        }else if(totalIncomeData>totalExpensesData){
//
//        }

    }
    private void addData() {
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInserted();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void incomeDataInserted() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(CategoryActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(CategoryActivity.this);
        View view = layoutInflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(view);
        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText edtAmount = view.findViewById(R.id.amount_edt);
        EditText edtType = view.findViewById(R.id.type_edt);
        view.findViewById(R.id.txt_type).setVisibility(View.GONE);
        edtType.setVisibility(View.GONE);
        EditText edtNote = view.findViewById(R.id.note_edt);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                btnSave.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                String type = edtType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Enter amount here");
                    return;
                } else if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Enter note here");
                    return;
                }
                int amoutInt = Integer.parseInt(amount);
                String id = preferenceManager.getString(Constants.KEY_USER_ID);
                String mDate = DateFormat.getDateInstance().format(new Date());
                String document = database.collection(Constants.KEY_USER_INCOME).document().getId();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
                LocalDateTime now = LocalDateTime.now();
                Data data = new Data(amoutInt, type, note, id, mDate, document);
                data.setDataStatus(now.format(dtf));

                database.collection(Constants.KEY_USER_INCOME).document(document).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CategoryActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                        ftAnimation();
                        dialog.dismiss();
                        btnSave.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);

                    }
                });

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(fabClose);
            fab_expense_btn.startAnimation(fabClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fabClose);
            fab_expense_txt.startAnimation(fabClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {
            fab_income_btn.startAnimation(fabOpen);
            fab_expense_btn.startAnimation(fabOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fabOpen);
            fab_expense_txt.startAnimation(fabOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTotalData() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
        LocalDateTime now = LocalDateTime.now();
        String id = preferenceManager.getString(Constants.KEY_USER_ID);
        database.collection(Constants.KEY_USER_INCOME).whereEqualTo(Constants.KEY_USER_ID, id)
                .whereEqualTo("dataStatus", now.format(dtf)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int totalSum = 0;

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Data data = documentSnapshot.toObject(Data.class);
                    totalSum += data.getAmount();
                   // incomeMonth.setText(String.valueOf("Rs:" + totalSum + ".00"));
                    totalIncomeData = totalSum;
                    toolbar.setTitle("Total Income: "+totalIncomeData);
                }


            }
        });


//        if (task.isSuccessful()) {
//            int totalSum=0;
//            for (QueryDocumentSnapshot document : task.getResult()) {
//                Data data=document.toObject(Data.class);
//
//                totalSum+=data.getAmount();
//                totalIncomeResult.setText(String.valueOf("Rs:"+totalSum+".00"));
//                totalIncomeData=totalSum;
//
//
//            }
//        } else {
//
//        }


        database.collection(Constants.KEY_USER_EXPENSE).whereEqualTo(Constants.KEY_USER_ID, id)
                .whereEqualTo("dataStatus", now.format(dtf)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int totalSum = 0;

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Data data = documentSnapshot.toObject(Data.class);
                    totalSum += data.getAmount();
                    expensesMonth.setText(String.valueOf("Rs: " + totalSum + ".00"));
                    totalExpensesData = totalSum;
                }
                incomeMonth.setText(String.valueOf("Rs: " + (totalIncomeData-totalExpensesData+ ".00")));
                if(totalExpensesData>totalIncomeData){
                    new AlertDialog.Builder(CategoryActivity.this)
                            .setTitle("Warning")
                            .setMessage("Your Expenses are Exceeding Your Income.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }


            }
        });



    }

    public void onclickCards(){
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Bill"));

            }
        });

        transport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Transport"));

            }
        });

        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Grocery"));

            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Education"));

            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Health"));

            }
        });

        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Entertainment"));

            }
        });

        cloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Cloth"));

            }
        });
        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Rent"));

            }
        });

        fastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Fast Food"));

            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Other"));

            }
        });

        shoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoryActivity.this,Dashboard.class).putExtra("type","Shoe"));

            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }

    public void displaySelectedListener(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.dashboard:
                break;
            case R.id.income:
                // fragment = new IncomeFragment();
                startActivity(new Intent(CategoryActivity.this,OldRecordAcivity.class).putExtra("name","incomeData"));
                break;
            case R.id.expense:
                // fragment = new ExpenseFragment();
                startActivity(new Intent(CategoryActivity.this,OldRecordAcivity.class).putExtra("name","expenseData"));

                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle=new Bundle();
            bundle.putString("type","type");
            fragment.setArguments(bundle);
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                signOut();
                break;
            case R.id.checkIncome:
                startActivity(new Intent(CategoryActivity.this,OldRecordAcivity.class).putExtra("name","incomeData"));
                break;
            case R.id.checkExpenses:
                startActivity(new Intent(CategoryActivity.this,OldRecordAcivity.class).putExtra("name","expenseData"));
                break;
        }
        return true;
    }
    public void signOut() {
        preferenceManager.clearPreference();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();

        }

    }
}