package br.com.bossini.usjt_chat_firebase_authentication_firestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}

class ChatViewHolder extends RecyclerView.ViewHolder{
    TextView dataNomeTextView;
    TextView mensagemTextView;

    public ChatViewHolder (View v){
        super (v);
        dataNomeTextView = v.findViewById(R.id.dataNomeTextView);
        mensagemTextView = v.findViewById(R.id.mensagemTextView);
    }

}
