package me.lesonnnn.demo3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 123;
    private static final int Image_Capture_Code = 1;
    private String user, pass;
    private EditText mEdtUser, mEdtPass;
    private GridLayout mGridLayout;
    private TextView mTvInfo;
    private Button mBtnAddImage, mBtnSendMail, mBtnSave;
    private ImageView mImageView;
    private String mEmail;
    private Bitmap mBitmap;

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
        mBtnSave = findViewById(R.id.saveImage);

        mBtnAddImage.setOnClickListener(this);
        mBtnSendMail.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
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
            mBtnSave.setVisibility(View.VISIBLE);
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
                mBitmap = bitmap;
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
            case R.id.saveImage:
                if (mBitmap != null) {
                    //                    SaveImage(mBitmap);
                    saveBitMap(this, mBitmap);
                } else {
                    Toast.makeText(this, "Image is null", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void saveBitMap(Context context, Bitmap Final_bitmap) {
        initPermission();
        File pictureFileDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/leson");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated) Log.d("TAG", "Can't create directory to save the image");
            return;
        }
        String filename =
                pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            Final_bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
            Toast.makeText(LoginActivity.this, "Save Image Successfully..", Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Toast.makeText(LoginActivity.this, "Save Image Successfully..",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "There was an issue scanning gallery.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoginActivity.this, "Permision Write File is Granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Permision Write File is Denied",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(LoginActivity.this, "Permission isn't granted ",
                            Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(LoginActivity.this,
                            "Permisson don't granted and dont show dialog again ",
                            Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
            }
        }
    }
}
