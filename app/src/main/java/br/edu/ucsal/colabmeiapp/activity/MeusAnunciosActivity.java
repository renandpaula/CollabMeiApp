package br.edu.ucsal.colabmeiapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
import br.edu.ucsal.colabmeiapp.helper.RecyclerItemClickListener;
import br.edu.ucsal.colabmeiapp.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //Configuracoes iniciais
        anuncioUsuarioRef = FirebaseConfig.getFirebaseDatabase()
                .child("meus anuncios")
                .child(FirebaseConfig.getIdUsuario());

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Meus Anúncios");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

            }
        });

        //Config do recycler view
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios =  new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);

        //Recupera anuncios do usuario
        recuperarAnuncios();

        //Adicionar evento de clique no recyclerview
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                //metodo para deletar um anuncio quando

                                final Anuncio anuncioSelecionado = anuncios.get(position);

                                AlertDialog.Builder dialogExcluir = new AlertDialog.Builder(MeusAnunciosActivity.this);

                                //configura o titulo da mensagem
                                dialogExcluir.setTitle("Confirmar exclusão");
                                dialogExcluir.setMessage("Deseja excluir o anúncio: " + anuncioSelecionado.getTitulo() + " ?");

                                dialogExcluir.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        anuncioSelecionado.remover();
                                        Toast.makeText(MeusAnunciosActivity.this, "Anúncio deletado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialogExcluir.setNegativeButton("Não", null);

                                //exibir dialog
                                dialogExcluir.create();
                                dialogExcluir.show();

                                adapterAnuncios.notifyDataSetChanged();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarAnuncios(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anuncios.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren() ){
                    anuncios.add(ds.getValue(Anuncio.class) );
                }

                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
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
