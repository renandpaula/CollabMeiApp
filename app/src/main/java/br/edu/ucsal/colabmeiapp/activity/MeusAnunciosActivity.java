package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.adapter.AdapterAnuncios;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.model.Anuncio;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //Configuracoes iniciais
        anuncioUsuarioRef = FirebaseConfig.getFirebaseDatabase()
                .child("meus anuncios")
                .child(FirebaseConfig.getIdUsuario());

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Config do recycler view
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios =  new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);

        //Recupera anuncios do usuario
        recuperarAnuncios();

    }

    private void recuperarAnuncios(){

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anuncios.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren() ){
                    anuncios.add(ds.getValue(Anuncio.class) );
                }

                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void inicializarComponentes(){

        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);

    }

}
