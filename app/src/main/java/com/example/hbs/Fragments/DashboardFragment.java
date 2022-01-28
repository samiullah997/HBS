package com.example.hbs.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.provider.AlarmClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hbs.Firebase.AlertReceiver;
import com.example.hbs.Model.Data;
import com.example.hbs.R;
import com.example.hbs.Utilities.Constants;
import com.example.hbs.Utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.geo.type.Viewport;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class DashboardFragment extends Fragment {

    //floating action button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;


    //floating button textview
    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    private RecyclerView incomeRecyclerView, expenseRecyclerView;
    private EditText edtAmount, edtType, edtNote;
    private Button btnUpdate, btnDelete;
    private String type, note, status;
    private int amount = 0;

    private String post_key;
    FirestoreRecyclerAdapter incomeAdapter;
    FirestoreRecyclerAdapter expenseAdapter;

    private boolean isOpen = false;
    private Animation fabOpen, fabClose;

    FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    String document;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;
    int totalIncomeData, totalExpensesData;
    int count = 0;
    int tyear, tmonth, tdate, thour, tminute;
    String dataType;

    private TextView totalIncomeResult, totalExpenseResult;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        fab_main_btn = view.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = view.findViewById(R.id.income_ft_btn);
        fab_expense_btn = view.findViewById(R.id.expense_ft_btn);
        fab_expense_txt = view.findViewById(R.id.expense_ft_text);
        fab_income_txt = view.findViewById(R.id.income_ft_text);
        totalIncomeResult = view.findViewById(R.id.income_set_result);
        totalExpenseResult = view.findViewById(R.id.expense_set_result);
        barChart = view.findViewById(R.id.barChart);

//        incomeRecyclerView=view.findViewById(R.id.income_recycler);
//        expenseRecyclerView=view.findViewById(R.id.expense_recyler);

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fade_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fade_close);
        database = FirebaseFirestore.getInstance();
        dataType=getArguments().getString("type");
        preferenceManager = new PreferenceManager(getContext());
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

        createNotificationChanne();


//        LinearLayoutManager incomeLinearLayoutManager;
//        LinearLayoutManager expenseLinearLayoutManager;
//
//        incomeLinearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true);
//        incomeLinearLayoutManager.setReverseLayout(true);
//        incomeLinearLayoutManager.setStackFromEnd(true);
//
//        expenseLinearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true);
//        expenseLinearLayoutManager.setReverseLayout(true);
//        expenseLinearLayoutManager.setStackFromEnd(true);
//
//        setIncomeData();
//        incomeRecyclerView.setHasFixedSize(true);
//        incomeRecyclerView.setLayoutManager(incomeLinearLayoutManager);
//        incomeRecyclerView.setAdapter(incomeAdapter);
//
//        setExpenseData();
//        expenseRecyclerView.setHasFixedSize(true);
//        expenseRecyclerView.setLayoutManager(expenseLinearLayoutManager);
//        expenseRecyclerView.setAdapter(expenseAdapter);

        addData();
        setTotalData();


        getEntries();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        incomeAdapter.startListening();
//        expenseAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
//        incomeAdapter.stopListening();
//        expenseAdapter.stopListening();

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
                expenseDataInserted();
            }
        });
    }

    public void incomeDataInserted() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
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
                data.setType(dataType);
                database.collection(Constants.KEY_USER_INCOME).document(document).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Data Added Successfully", Toast.LENGTH_SHORT).show();
                        ftAnimation();
                        dialog.dismiss();
                        btnSave.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);
                        refresh();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void expenseDataInserted() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(view);
        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText edtAmount = view.findViewById(R.id.amount_edt);
        EditText edtType = view.findViewById(R.id.type_edt);
        EditText edtNote = view.findViewById(R.id.note_edt);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        dialog.show();

        btnSave.setOnClickListener(v -> {
            btnSave.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            String type = edtType.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String note = edtNote.getText().toString().trim();
            if (dataType.equals("bill") || dataType.equals("Bill") || dataType.equals("BILL")) {
                setTimePicker();
            }  else if (TextUtils.isEmpty(amount)) {
                edtAmount.setError("Enter amount here");
                return;
            } else if (TextUtils.isEmpty(note)) {
                edtNote.setError("Enter note here");
                return;
            }
            int amoutInt = Integer.parseInt(amount);
            String id = preferenceManager.getString(Constants.KEY_USER_ID);
            String mDate = DateFormat.getDateInstance().format(new Date());
            String document = database.collection(Constants.KEY_USER_EXPENSE).document().getId();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
            LocalDateTime now = LocalDateTime.now();
            Data data = new Data(amoutInt, type, note, id, mDate, document);
            data.setDataStatus(now.format(dtf));
            data.setDataType(dataType);
            database.collection(Constants.KEY_USER_EXPENSE).document(document).set(data).addOnSuccessListener(aVoid -> {
                btnSave.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Data Added Successfully", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
                refresh();
            });
        });
        btnCancel.setOnClickListener(v -> {
            ftAnimation();
            dialog.dismiss();
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
                    totalIncomeResult.setText(String.valueOf("Rs:" + totalSum + ".00"));
                    totalIncomeData = totalSum;
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
                .whereEqualTo("dataStatus", now.format(dtf)).whereEqualTo("dataType",dataType).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int totalSum = 0;

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Data data = documentSnapshot.toObject(Data.class);
                    totalSum += data.getAmount();
                    totalExpenseResult.setText(String.valueOf("Rs:" + totalSum + ".00"));
                    totalExpensesData = totalSum;
                }


            }
        });


    }

    public void setIncomeData() {

        final int[] totalIncome = {0};
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        Query query = database.collection(Constants.KEY_USER_INCOME).whereEqualTo(Constants.KEY_USER_ID, userId);

        FirestoreRecyclerOptions<Data> options = new FirestoreRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        incomeAdapter = new FirestoreRecyclerAdapter<Data, DashboardFragment.IncomeViewHolder>(options) {
            @Override
            public void onBindViewHolder(DashboardFragment.IncomeViewHolder holder, int position, Data model) {
                totalIncome[0] += model.getAmount();
                String totalValue = String.valueOf(totalIncome[0]);
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());
                holder.setType(model.getType());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = model.getDocumentId();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        status = model.getDataStatus();
                        updateIncomeDataItems();

                    }
                });

            }

            @Override
            public DashboardFragment.IncomeViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mview = LayoutInflater.from(group.getContext()).inflate(R.layout.dashboard_income_item_recycler, group, false);

                return new IncomeViewHolder(mview);
            }


        };

//Final step, where "mRecyclerView" is defined in your xml layout as
//the recyclerview
    }

    public void setExpenseData() {

        final int[] totalExpense = {0};
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        Query query = database.collection(Constants.KEY_USER_EXPENSE).whereEqualTo(Constants.KEY_USER_ID, userId);

        FirestoreRecyclerOptions<Data> options = new FirestoreRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        expenseAdapter = new FirestoreRecyclerAdapter<Data, DashboardFragment.ExpenseViewHolder>(options) {
            @Override
            public void onBindViewHolder(DashboardFragment.ExpenseViewHolder holder, int position, Data model) {
                totalExpense[0] += model.getAmount();
                String totalValue = String.valueOf(totalExpense[0]);
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());
                holder.setType(model.getType());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = model.getDocumentId();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        updateExpenseDataItems();

                    }
                });


            }

            @Override
            public DashboardFragment.ExpenseViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mview = LayoutInflater.from(group.getContext()).inflate(R.layout.dashboard_expense_item_recycler, group, false);

                return new ExpenseViewHolder(mview);
            }
        };
//Final step, where "mRecyclerView" is defined in your xml layout as
//the recyclerview
    }

    private void updateIncomeDataItems() {
        AlertDialog.Builder mydailog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View myview = inflater.inflate(R.layout.update_data_items, null);
        mydailog.setView(myview);
        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);
        btnUpdate = myview.findViewById(R.id.btn_update);
        btnDelete = myview.findViewById(R.id.btn_delete);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());


        AlertDialog dialog = mydailog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();
                String valueAmount = String.valueOf(amount);
                valueAmount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(valueAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                String id = preferenceManager.getString(Constants.KEY_USER_ID);

                Data data = new Data(myAmount, type, note, id, mDate, post_key);
                data.setDataStatus(status);
                database.collection(Constants.KEY_USER_INCOME).document(post_key)
                        .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Data Update Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.collection(Constants.KEY_USER_INCOME).document(post_key).delete();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void updateExpenseDataItems() {
        AlertDialog.Builder mydailog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View myview = inflater.inflate(R.layout.update_data_items, null);
        mydailog.setView(myview);
        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);
        btnUpdate = myview.findViewById(R.id.btn_update);
        btnDelete = myview.findViewById(R.id.btn_delete);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());


        AlertDialog dialog = mydailog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String valueAmount = String.valueOf(amount);
                valueAmount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(valueAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                String id = preferenceManager.getString(Constants.KEY_USER_ID);

                Data data = new Data(myAmount, type, note, id, mDate, post_key);
                database.collection(Constants.KEY_USER_EXPENSE).document(post_key)
                        .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Data Update Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.collection(Constants.KEY_USER_EXPENSE).document(post_key).delete();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private static class IncomeViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        void setType(String type) {
            TextView mType = mView.findViewById(R.id.txt_type);
            mType.setText(type);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.txt_date);
            mDate.setText(date);
        }

        void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.txt_amount);
            mAmount.setText(String.valueOf(amount));
        }

    }

    private static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.txt_expense_type);
            mType.setText(type);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.txt_expense_date);
            mDate.setText(date);
        }

        void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.txt_expense_amount);
            mAmount.setText(String.valueOf(amount));
        }

    }

    private void getEntries() {
        count++;
        barEntries = new ArrayList<>();
        barChart.refreshDrawableState();


        barEntries.add(new BarEntry(1f, totalIncomeData));
        barEntries.add(new BarEntry(2f, totalExpensesData));
        barDataSet = new BarDataSet(barEntries, "");
        barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");
        barChart.setMinimumHeight(2);
        barChart.setFadingEdgeLength(3);
        barChart.animateY(2000);
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        if (count > 1) {

        } else {
            refresh();

        }
    }

    public void refresh() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                getEntries();

            }
        };
        handler.postDelayed(runnable, 3000);
    }

    public void setDatePicker() {
        Calendar calendar = Calendar.getInstance();
        tyear = calendar.get(Calendar.YEAR);
        tmonth = calendar.get(Calendar.MONTH);
        tdate = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Toast.makeText(getContext(), "Year: " + year + "/" + month + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        }, tyear, tmonth, tdate);
        datePickerDialog.show();

    }

    public void setTimePicker() {

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        thour = hourOfDay;
                        tminute = minute;
                        Calendar calendar = Calendar.getInstance();

                        calendar.set(tyear,
                                tmonth
                                , tdate
                                , thour, tminute);
                        setAlarm(calendar.getTimeInMillis());

                    }


                }, 12, 0, false);


        timePickerDialog.updateTime(thour, tminute);
        timePickerDialog.show();
        setDatePicker();

    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

//        Intent intent=new Intent(AlarmClock.ACTION_SET_ALARM);
//        intent.putExtra(AlarmClock.EXTRA_HOUR,thour);
//        intent.putExtra(AlarmClock.EXTRA_MINUTES,tminute);
//        intent.putExtra(AlarmClock.EXTRA_MESSAGE,"Set Alarm For Bill Payment");
//        startActivity(intent);

        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getContext(), "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChanne() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pay Your Bill";
            String descirption = "Alarm for Reminder To Pay Your Bill";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Sami", name, importance);
            channel.setDescription(descirption);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


        }
    }


}