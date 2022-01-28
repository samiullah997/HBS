package com.example.hbs.Fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hbs.Model.Data;
import com.example.hbs.R;
import com.example.hbs.Utilities.Constants;
import com.example.hbs.Utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class IncomeFragment extends Fragment {

    private FirebaseFirestore database;
    PreferenceManager preferenceManager;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    TextView incomeTotalSum;
    private EditText edtAmount,edtType,edtNote;
    private Button btnUpdate,btnDelete;
    private String type,note,status;
    private int amount=0;
    private String post_key;

    public IncomeFragment() {
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_income, container, false);
        database=FirebaseFirestore.getInstance();
        preferenceManager=new PreferenceManager(getContext());
        recyclerView=view.findViewById(R.id.recycler_id_income);
        incomeTotalSum=view.findViewById(R.id.income_txt_result);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        setData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
        LocalDateTime now = LocalDateTime.now();
        final int[] totalIncome = {0};
        String userId=preferenceManager.getString(Constants.KEY_USER_ID);
        Query query = database.collection(Constants.KEY_USER_INCOME).whereEqualTo(Constants.KEY_USER_ID,userId)
                .whereEqualTo("dataStatus",now.format(dtf));

        FirestoreRecyclerOptions<Data> options = new FirestoreRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            public void onBindViewHolder(MyViewHolder holder, int position, Data model) {
                totalIncome[0] +=model.getAmount();
                String totalValue= String.valueOf(totalIncome[0]);
                incomeTotalSum.setText("Rs:"+totalValue+".00");
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setType(model.getType());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=model.getDocumentId();

                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();
                        status=model.getDataStatus();
                        updateDataItems();

                    }
                });
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mview = LayoutInflater.from(group.getContext()).inflate(R.layout.income_recycler_data, group, false);

                return new MyViewHolder(mview);
            }
        };
//Final step, where "mRecyclerView" is defined in your xml layout as
//the recyclerview
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

    private void updateDataItems(){
        AlertDialog.Builder mydailog=new AlertDialog.Builder(getContext());
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View myview=inflater.inflate(R.layout.update_data_items,null);
        mydailog.setView(myview);
        edtAmount=myview.findViewById(R.id.amount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        btnUpdate=myview.findViewById(R.id.btn_update);
        btnDelete=myview.findViewById(R.id.btn_delete);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());


        AlertDialog dialog=mydailog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                String valueAmount=String.valueOf(amount);
                valueAmount=edtAmount.getText().toString().trim();
                int myAmount=Integer.parseInt(valueAmount);
                String mDate= DateFormat.getDateInstance().format(new Date());
                String id=preferenceManager.getString(Constants.KEY_USER_ID);

                Data data=new Data(myAmount,type,note,id,mDate,post_key);
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
                        Toast.makeText(getContext(), "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
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

}