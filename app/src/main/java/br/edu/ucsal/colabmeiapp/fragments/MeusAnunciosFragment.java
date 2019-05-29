package br.edu.ucsal.colabmeiapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import br.edu.ucsal.colabmeiapp.activity.CadastrarAnuncioActivity;
import br.edu.ucsal.colabmeiapp.adapter.AdapterAnuncios;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.RecyclerItemClickListener;
import br.edu.ucsal.colabmeiapp.model.Anuncio;
import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeusAnunciosFragment extends Fragment {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;


    public MeusAnunciosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meus_anuncios, container, false);

        //Configuracoes iniciais
        recyclerAnuncios = view.findViewById(R.id.recyclerAnuncios);
        anuncioUsuarioRef = FirebaseConfig.getFirebaseDatabase()
                .child("meus anuncios")
                .child(FirebaseConfig.getIdUsuario());


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CadastrarAnuncioActivity.class));
            }
        });

        //Config do recycler view
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios =  new AdapterAnuncios(anuncios, getActivity());
        recyclerAnuncios.setAdapter(adapterAnuncios);

        //Recupera anuncios do usuario
        recuperarAnuncios();

        //Adicionar evento de clique no recyclerview
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                //metodo para deletar um anuncio quando

                                final Anuncio anuncioSelecionado = anuncios.get(position);

                                AlertDialog.Builder dialogExcluir = new AlertDialog.Builder(getActivity());

                                //configura o titulo da mensagem
                                dialogExcluir.setTitle("Confirmar exclusão");
                                dialogExcluir.setMessage("Deseja excluir o anúncio: " + anuncioSelecionado.getTitulo() + " ?");

                                dialogExcluir.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        anuncioSelecionado.remover();
                                        Toast.makeText(getActivity(), "Anúncio deletado com sucesso!",
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



        return view;
    }

    private void recuperarAnuncios(){

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
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


}
