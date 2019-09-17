package me.lesonnnn.demo3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 123;
    private static final int Image_Capture_Code = 1;
    private String user, pass;
    private EditText mEdtUser, mEdtPass;
    private GridLayout mGridLayout;
    private TextView mTvInfo;
    private Button mBtnAddImage, mBtnSendMail;
    private ImageView mImageView;
    private String mEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mEdtUser = findViewById(R.id.myUsername);
        mEdtPass = findViewById(R.id.myPassword);
        Button btnLogin = findViewById(R.id.login);
        mGridLayout = findViewById(R.id.myForm);
        mTvInfo = findViewById(R.id.info);
        mBtnAddImage = findViewById(R.id.openCamera);
        mBtnSendMail = findViewById(R.id.sendMail);
        mImageView = findViewById(R.id.image);

        mBtnAddImage.setOnClickListener(this);
        mBtnSendMail.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        mEdtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    pass = mEdtPass.getText().toString();
                    user = mEdtUser.getText().toString();
                    if (user.equals("admin") && pass.equals("leson")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", user);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == MainActivity.RESULT_CODE) {
            mGridLayout.setVisibility(View.GONE);
            mTvInfo.setVisibility(View.VISIBLE);
            mBtnAddImage.setVisibility(View.VISIBLE);
            mBtnSendMail.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            assert data != null;
            mEmail = data.getStringExtra("mail");
            mTvInfo.setText(Html.fromHtml("<b>Your Age:  </b>"
                    + data.getStringExtra("age")
                    + "<br><br>"
                    + "<b>Your Mail: </b>"
                    + data.getStringExtra("mail")));
        }

        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                mImageView.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                pass = mEdtPass.getText().toString();
                user = mEdtUser.getText().toString();
                if (user.equals("admin") && pass.equals("leson")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", user);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            case R.id.openCamera:
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
                break;
            case R.id.sendMail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[] { mEmail });
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this, "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
