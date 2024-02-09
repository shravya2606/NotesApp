package com.example.notesapp;

import android.content.Intent;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link login_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class login_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    EditText emailText1, passwordText1;
    Button login_Btn1;
    ProgressBar progressBar1;
    TextView signup_Btn;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public login_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment login_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static login_fragment newInstance(String param1, String param2) {
        login_fragment fragment = new login_fragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        emailText1 = view.findViewById(R.id.emailText1);
        passwordText1 = view.findViewById(R.id.passwordText2);
        login_Btn1 = view.findViewById(R.id.login_Btn1);
        signup_Btn = view.findViewById(R.id.signup_text_view_btn);

        progressBar1 = view.findViewById(R.id.progress_bar1);
        login_Btn1.setOnClickListener(v -> {
            loginUser();

        });
        signup_Btn.setOnClickListener((v) -> {
            // Assuming you are working with a FragmentTransaction in the hosting activity
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new signup_fragment());
            transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
            transaction.commit();
        });


        return view;

    }
    void loginUser(){

    String email  = emailText1.getText().toString();
    String password  = passwordText1.getText().toString();


    boolean isValidated = validateData(email,password);
        if(!isValidated){
        return;
    }
        loginAccountInFirebase(email,password);


}

    void loginAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    //login is success
                        startActivity(new Intent(requireContext(), MainActivity2.class));
                    }
                else{
                    //login failed
                    Utility.showToast(getContext(),task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar1.setVisibility(View.VISIBLE);
            login_Btn1.setVisibility(View.GONE);
        }else{
            progressBar1.setVisibility(View.GONE);
            login_Btn1.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String password){
        //validate the data that are input by user.

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText1.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordText1.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}