package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    //Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;


    //Update edit text
    private EditText edtAmount;
    private  EditText edType;
    private EditText edtNote;

    //button for update and delete

    private Button btnUpdate;
    private Button btnDelete;

    //Data item value

    private String type;
    private String note;
    private int amount;

    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("Expense data").child(uid);

        recyclerView = myview.findViewById(R.id.recyler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull IncomeFragment.MyViewHolder holder, int position, @NonNull Data model) {

            }

            public IncomeFragment.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new IncomeFragment.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false));
            }

            protected void onBindViewHolder(ExpenseFragment.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();

                        updateDateItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);


    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_expense);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }
    }


    private void updateDateItem() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnDelete);

        //Set data to edit text

        edType.setText(type);
        edType.setSelection(type.length());

        edType.setText(note);
        edType.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        final AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String mdamount = String.valueOf(amount);

                mdamount = edtAmount.getText().toString().trim();

                int myAmount = Integer.parseInt(mdamount);

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(myAmount,type,note,post_key);

                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();

            }
        });

        dialog.show();
    }
}