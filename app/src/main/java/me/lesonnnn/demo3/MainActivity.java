package me.lesonnnn.demo3;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int RESULT_CODE = 111;
    private EditText mEdtAge, mEdtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        TextView tvMyName = findViewById(R.id.myName);
        mEdtAge = findViewById(R.id.myAge);
        mEdtEmail = findViewById(R.id.myEmail);
        Button btnBack = findViewById(R.id.back);

        mEdtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    String age = mEdtAge.getText().toString();
                    String mail = mEdtEmail.getText().toString();
                    if (isEmailValid(mail) && !age.equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra("age", age);
                        intent.putExtra("mail", mail);
                        setResult(RESULT_CODE, intent);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
        btnBack.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            String user = intent.getStringExtra("username");
            tvMyName.setText(user);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                String age = mEdtAge.getText().toString();
                String mail = mEdtEmail.getText().toString();
                if (isEmailValid(mail) && !age.equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra("age", age);
                    intent.putExtra("mail", mail);
                    setResult(RESULT_CODE, intent);
                    finish();
                }
                break;
        }
    }
}
