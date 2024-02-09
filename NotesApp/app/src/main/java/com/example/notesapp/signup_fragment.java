package com.example.notesapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signup_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signup_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText emailText, passwordText, confirmpasswordText;
    Button signupBtn;
    ProgressBar progressBar;
    TextView loginBtn;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public signup_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment signup_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static signup_fragment newInstance(String param1, String param2) {
        signup_fragment fragment = new signup_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);
        signupBtn = view.findViewById(R.id.signupBtn);
        confirmpasswordText = view.findViewById(R.id.confirmpasswordText);
        loginBtn = view.findViewById(R.id.login_text_view_btn);
        progressBar = view.findViewById(R.id.progress_bar);
        signupBtn.setOnClickListener(v -> createAccount());
        loginBtn.setOnClickListener((v) -> {
            // Assuming you are working with a FragmentTransaction in the hosting activity
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new login_fragment());
            transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
            transaction.commit();
        });




        return view;
    }




    void createAccount() {
        String emails = emailText.getText().toString();
        String pwd = passwordText.getText().toString();
        String confirm = confirmpasswordText.getText().toString();
        boolean isValidated = validateData(emails, pwd, confirm);
        if (!isValidated) {
            return;
        }
        createAccountInFirebase(emails, pwd);
    }

    void createAccountInFirebase(String email, String password) {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( requireActivity(),
              new  OnCompleteListener<AuthResult>()  {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);

                        if (task.isSuccessful()) {
                            Utility.showToast(getContext(),"Successfully create account,Check email to verify");

                            Objects.requireNonNull(firebaseAuth.getCurrentUser());
                            firebaseAuth.signOut();



                        }
                        else{
                            //failure
                            Utility.showToast(getContext(),task.getException().getLocalizedMessage());
                        }

                    }
                }
        );


    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email, String password, String confirmPassword) {
        //validate the data that are input by user.

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmpasswordText.setError("Password not matched");
            return false;

        }

        return true;
    }
}

