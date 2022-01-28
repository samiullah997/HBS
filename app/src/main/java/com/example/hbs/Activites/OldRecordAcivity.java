package com.example.hbs.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbs.Fragments.IncomeFragment;
import com.example.hbs.Model.Data;
import com.example.hbs.R;
import com.example.hbs.Utilities.Constants;
import com.example.hbs.Utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OldRecordAcivity extends AppCompatActivity {
    private FirebaseFirestore database;
    PreferenceManager preferenceManager;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    TextView incomeTotalSum,income;
    private EditText edtAmount,edtType,edtNote;
    private Button btnUpdate,btnDelete;
    private String type,note;
    private int amount=0;
    private String post_key;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_record_acivity);
        database=FirebaseFirestore.getInstance();
        income=findViewById(R.id.income);
        preferenceManager=new PreferenceManager(OldRecordAcivity.this);
        recyclerView=findViewById(R.id.recycler_id_income);
        incomeTotalSum=findViewById(R.id.income_txt_result);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(OldRecordAcivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        setData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        setTotalData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(){
        String data=getIntent().getStringExtra("name");
        income.setText(data);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
        LocalDateTime now = LocalDateTime.now();
        final int[] totalIncome = {0};
        String userId=preferenceManager.getString(Constants.KEY_USER_ID);
        Query query = database.collection(data)
                .whereEqualTo(Constants.KEY_USER_ID,userId).orderBy("date");
        FirestoreRecyclerOptions<Data> options = new FirestoreRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Data, OldRecordAcivity.MyViewHolder>(options) {
            @Override
            public void onBindViewHolder(OldRecordAcivity.MyViewHolder holder, int position, Data model) {
               // totalIncome[0] +=model.getAmount();
                String totalValue= String.valueOf(totalIncome[0]);
              //  incomeTotalSum.setText("Rs:"+totalValue+".00");
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setType(model.getType());

            }

            @Override
            public OldRecordAcivity.MyViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mview = LayoutInflater.from(group.getContext()).inflate(R.layout.income_recycler_data, group, false);
                return new MyViewHolder(mview);
            }
        };
//Final step, where "mRecyclerView" is defined in your xml layout as
//the recyclerview
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        void setAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_income);
            mAmount.setText(String.valueOf(amount));
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTotalData() {

        String data=getIntent().getStringExtra("name");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
        LocalDateTime now = LocalDateTime.now();
        String id = preferenceManager.getString(Constants.KEY_USER_ID);
        database.collection(data).whereEqualTo(Constants.KEY_USER_ID, id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int totalSum = 0;

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Data data = documentSnapshot.toObject(Data.class);
                    totalSum += data.getAmount();
                    incomeTotalSum.setText(String.valueOf("Rs:" + totalSum + ".00"));
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




    }
}