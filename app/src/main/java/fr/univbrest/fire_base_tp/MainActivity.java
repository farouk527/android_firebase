package fr.univbrest.fire_base_tp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void modifier(View view) {
        EditText nomEditText = findViewById(R.id.nom);
        String content_nom = nomEditText.getText().toString();

        EditText prenomEditText = findViewById(R.id.prenom);
        String content_pre = prenomEditText.getText().toString();

        EditText numEditText = findViewById(R.id.num);
        String content_num = numEditText.getText().toString();

        if (content_nom.isEmpty() || content_pre.isEmpty() || content_num.isEmpty()) {
            Toast.makeText(MainActivity.this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("joueurs");

            ref.child(content_num).child("nom").setValue(content_nom);
            ref.child(content_num).child("prenom").setValue(content_pre);

            nomEditText.setText("");
            prenomEditText.setText("");
            numEditText.setText("");

        }
    }

public void visualiser (View view) {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("joueurs");
    ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<String> listItems = new ArrayList<>();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String Name = ds.getKey();
                Log.i("data", "Clé : " + Name);
                listItems.add(Name);
            }
            String[] joueurKeysArray = listItems.toArray(new String[0]);

            if (listItems.size()>0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Liste des joueurs")
                        .setItems(joueurKeysArray, (dialog, which) -> {
                            String selectedJoueurKey = joueurKeysArray[which];
                            ref.child(selectedJoueurKey).removeValue();
                            Toast.makeText(MainActivity.this, "joueurs supprimé avec succés ", Toast.LENGTH_SHORT).show();


                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

            }



        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    });


}

    public void authentifier(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.passowrd);
        String emailContent = email.getText().toString();
        String passwordContent = password.getText().toString();
        if (emailContent.length() == 0 || passwordContent.length() == 0) {
            Toast.makeText(MainActivity.this, "Email et mot de passe requis", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail().equals(emailContent)) {
            Toast.makeText(MainActivity.this, "Utilisateur déjà connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailContent, passwordContent)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            /*email.setText("");
                            password.setText("");
                            */

                                Toast.makeText(MainActivity.this, "connexion réussie", Toast.LENGTH_SHORT).show();
                        } else
                         {
                            Toast.makeText(MainActivity.this, "erreur email ou password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void deconnecter(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.passowrd);


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
            /*email.setText("");
            password.setText("");
            */

            Toast.makeText(MainActivity.this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
        }
    }








}