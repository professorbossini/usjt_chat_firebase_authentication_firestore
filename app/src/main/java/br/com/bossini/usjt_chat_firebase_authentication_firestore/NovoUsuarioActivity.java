package br.com.bossini.usjt_chat_firebase_authentication_firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NovoUsuarioActivity extends AppCompatActivity {

    private EditText loginNovoUsuarioEditText;
    private EditText senhaNovoUsuarioEditText;
    private FirebaseAuth mAuth;
    private static final int REQ_CODE_CAMERA = 1001;
    private StorageReference pictureStorageReference;

    private ImageView pictureImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);
        this.loginNovoUsuarioEditText = findViewById(R.id.loginNovoUsuarioEditText);
        this.senhaNovoUsuarioEditText = findViewById(R.id.senhaNovoUsuarioEditText);
        this.pictureImageView = findViewById(R.id.pictureImageView);
        mAuth = FirebaseAuth.getInstance();
    }

    private void uploadPicture (Bitmap picture){
        pictureStorageReference =
                FirebaseStorage.getInstance().getReference(
                        String.format(
                                Locale.getDefault(),
                                "images/%s/profilePic.jpg",
                                loginNovoUsuarioEditText.
                                        getText().
                                        toString().
                                        replace("@", "")
                        )
                );
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] bytes = baos.toByteArray();
        pictureStorageReference.putBytes(bytes);
    }


    public void criarNovoUsuario(View view) {
        String login = loginNovoUsuarioEditText.getText().toString();
        String senha = senhaNovoUsuarioEditText.getText().toString();
        mAuth.createUserWithEmailAndPassword(login, senha).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(NovoUsuarioActivity.this,
                                authResult.getUser().toString(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
        });
    }

    public void tirarFoto(View view) {
        if (loginNovoUsuarioEditText.getText() != null &&
            !loginNovoUsuarioEditText.getText().toString().isEmpty()){
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQ_CODE_CAMERA);
            }
            else{
                Toast.makeText(
                        this,
                        getString(
                                R.string.cant_take_pic
                        ),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        else{
            Toast.makeText(
                    this,
                    getString(R.string.email_obrigatorio),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQ_CODE_CAMERA:
                //sim, o usuário tirou a foto
                if (resultCode == Activity.RESULT_OK){
                    Bitmap picture = (Bitmap) data.getExtras().get("data");
                    pictureImageView.setImageBitmap(picture);
                    uploadPicture(picture);
                }
                else{
                    Toast.makeText(
                            this,
                            getString(R.string.no_pic_taken),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
