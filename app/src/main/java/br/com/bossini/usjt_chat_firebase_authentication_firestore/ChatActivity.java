package br.com.bossini.usjt_chat_firebase_authentication_firestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {



    private EditText mensagemEditText;
    private RecyclerView mensagensRecyclerView;
    private ChatAdapter chatAdapter;
    private List <Mensagem> mensagens;
    private FirebaseUser fireUser;
    private CollectionReference mMsgsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mensagemEditText = findViewById(R.id.mensagemEditText);
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, mensagens);
        mensagensRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setupFirebase (){
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        mMsgsReference =
                FirebaseFirestore.
                getInstance().
                collection("mensagens");
        getRemoteMsgs();
    }
    private void getRemoteMsgs (){
        mMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                mensagens.clear();
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Mensagem m = doc.toObject(Mensagem.class);
                    mensagens.add(m);
                }
                Collections.sort(mensagens);
                chatAdapter.notifyDataSetChanged();
            }
        });
    }
    private void esconderTeclado (View v){
        InputMethodManager ims =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirebase();
    }

    public void enviarMensagem(View view) {
        String texto = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem (fireUser.getEmail(), new Date(), texto);
        mMsgsReference.add(m);
        mensagemEditText.setText("");
        esconderTeclado(view);
    }
}

class ChatViewHolder extends RecyclerView.ViewHolder{
    TextView dataNomeTextView;
    TextView mensagemTextView;
    ImageView profilePicImageView;

    public ChatViewHolder (View v){
        super (v);
        dataNomeTextView = v.findViewById(R.id.dataNomeTextView);
        mensagemTextView = v.findViewById(R.id.mensagemTextView);
        profilePicImageView = v.findViewById(R.id.profilePicImageView);
    }
}

class ChatAdapter extends RecyclerView.Adapter <ChatViewHolder>{

    private List<Mensagem> mensagens;
    private Context context;

    public ChatAdapter (Context context, List <Mensagem> mensagens){
        this.context = context;
        this.mensagens = mensagens;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(
                R.layout.list_item,
                parent,
                false
        );
        return new ChatViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        holder.dataNomeTextView.setText(
                context.getString(
                        R.string.data_nome,
                        DateHelper.format(m.getData()),
                        m.getUsuario()
                )
        );
        holder.mensagemTextView.setText(m.getTexto());
        StorageReference profilePicStorageReference =
                FirebaseStorage.getInstance().getReference(
                        String.format(
                                Locale.getDefault(),
                                "images/%s/profilePic.jpg",
                                m.
                                getUsuario().
                                replace("@", "")
                        )
                );

        profilePicStorageReference.getDownloadUrl().addOnSuccessListener(
                (result) -> {
                    Glide.
                            with(context).
                            load(profilePicStorageReference).
                            into(holder.profilePicImageView);
                }
        ).addOnFailureListener((failure) -> {

            holder.profilePicImageView.setImageResource(
                    R.drawable.ic_person_black_50dp);
        });

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }
}
