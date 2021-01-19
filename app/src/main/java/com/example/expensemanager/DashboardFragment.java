package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class DashboardFragment extends Fragment {

    // Floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textView

    private TextView fab_income_txt;
    private TextView fab_expense_txt;


    //Boolean

    private boolean isOpen = false;


    //Animation object

    private Animation FadeOpen,FadeClose;

    //Dashboard total income and expense

    private TextView totalIncome;
    private TextView totalExpense;


    //Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);


        //Connect floating button to layout

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);


        //Connect floating text

        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //Dashboard Income and Expense display

        totalIncome = myview.findViewById(R.id.income_set_result);
        totalExpense = myview.findViewById(R.id.expense_set_result);


        //Animation connect

        FadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isOpen) {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                }else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

            }
        });

        // Calculate total income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnap:snapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);

                    totalsum += data.getAmount();

                    String stResults = String.valueOf(totalsum);

                    totalIncome.setText(stResults);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for(DataSnapshot mysnapshot: snapshot.getChildren()) {

                    Data data = mysnapshot.getValue(Data.class);

                    totalsum += data.getAmount();

                    String strTotalSum = String.valueOf(totalsum);

                    totalExpense.setText(strTotalSum);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    private void addData() {

        // Fade button income

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();

            }
        });

        //Fade expense button
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();


            }
        });
    }


    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_data_insertion,null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();


        final EditText edtAmount = myview.findViewById(R.id.amount_edt);
        final EditText edType = myview.findViewById(R.id.type_edt);
        final EditText edNote = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = edType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edType.setError("Required Field");
                    return;
                }

                int ouramountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Required Field");
                    return;
                }

                if (TextUtils.isEmpty(note)) {
                    edNote.setError("Required Field");
                }

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ouramountint,type,note,id,mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"DATA ADDED",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void expenseDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_data_insertion,null);
        mydialog.setView(myview);

       final AlertDialog dialog = mydialog.create();

       EditText amount = myview.findViewById(R.id.amount_edt);
       EditText type = myview.findViewById(R.id.type_edt);
       EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required field");
                    return;
                }

                int inamount = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmType)) {
                    type.setError("Required field");
                    return;
                }

                if (TextUtils.isEmpty(tmNote)) {
                    note.setError("Required field");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount,tmType,tmNote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"DATA ADDED",Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}