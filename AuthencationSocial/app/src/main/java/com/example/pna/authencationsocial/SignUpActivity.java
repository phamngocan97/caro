package com.example.pna.authencationsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText id, pass, repass;
    Button btn_ok, btn_cancel;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        action();
    }
    private void action(){
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String s_id = id.getText().toString().trim();
                String s_pass = pass.getText().toString();
                String s_repass = repass.getText().toString();
                if (s_id.length() != 0 && s_pass.length() != 0 && s_pass.equals(s_repass)) {

                    mAuth.createUserWithEmailAndPassword(s_id,s_pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(SignUpActivity.this, "Input empty or pass not equals", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                finish();
            }
        });
    }
    private void init(){
        id = findViewById(R.id.menu_signup_editEmail);
        pass = findViewById(R.id.menu_signup_editPass);
        repass = findViewById(R.id.menu_signup_editRePass);
        btn_ok = findViewById(R.id.menu_signup_btnOk);
        btn_cancel = findViewById(R.id.menu_signup_btnCancel);
        progressBar = findViewById(R.id.progressBar);


        mAuth = FirebaseAuth.getInstance();
    }
}
