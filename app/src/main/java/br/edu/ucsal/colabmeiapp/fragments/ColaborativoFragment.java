package br.edu.ucsal.colabmeiapp.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.activity.AnunciosDetalhesActivity;
import br.edu.ucsal.colabmeiapp.activity.LoginActivity;
import br.edu.ucsal.colabmeiapp.activity.MainActivity;
import br.edu.ucsal.colabmeiapp.activity.PrincipalActivity;
import br.edu.ucsal.colabmeiapp.adapter.AdapterAnuncios;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.RecyclerItemClickListener;
import br.edu.ucsal.colabmeiapp.model.Anuncio;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColaborativoFragment extends Fragment {

    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private FirebaseAuth autenticacao;
    private AlertDialog dialog;
    private String filtroRegiao = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorRegiao = false;
    private TextView limparFiltros;


    public ColaborativoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colaborativo, container, false);


        //configs iniciais
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();
        recyclerAnunciosPublicos = view.findViewById(R.id.recyclerAnunciosPublicos);
        buttonCategoria =  view.findViewById(R.id.button_categoria);
        limparFiltros =  view.findViewById(R.id.textView_LimparFiltros);
        limparFiltros.setVisibility(View.GONE);
        buttonRegiao = view.findViewById(R.id.button_regiao);
        anunciosPublicosRef =  FirebaseConfig.getFirebaseDatabase()
                .child("anuncios");

        //onClick listener dos filtros
        buttonRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroPorRegiao(v);
            }
        });

        buttonCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                filtroPorCategoria(vi);
            }
        });

        limparFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrincipalActivity.class));
                getActivity().finish();
            }
        });


        //Config do recycler view
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios =  new AdapterAnuncios(listaAnuncios, getActivity());
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();
        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerAnunciosPublicos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Anuncio anuncioSelecionado = listaAnuncios.get(position);
                        Intent i = new Intent(getActivity(), AnunciosDetalhesActivity.class);
                        i.putExtra("anuncioSelecionado", anuncioSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        return view;
    }

    public void filtroPorRegiao(View view){

        AlertDialog.Builder dialogRegiao = new AlertDialog.Builder(getActivity());
        dialogRegiao.setTitle("Selecione a região desejada");

        //configurar spinner do dialog
        View viewSpinner =  getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinnerRegiao = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] regiao = getResources().getStringArray(R.array.regiao);
        ArrayAdapter<String> adapterR = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item,
                regiao
        );
        adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(adapterR);

        dialogRegiao.setView(viewSpinner);

        dialogRegiao.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filtroRegiao = spinnerRegiao.getSelectedItem().toString();
                recuperarAnunciosPorRegiao();
                filtrandoPorRegiao =  true;
                limparFiltros.setVisibility(View.VISIBLE);

            }
        });

        dialogRegiao.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogRegiao.create();
        dialog.show();

    }

    public void filtroPorCategoria(View view){

        if (filtrandoPorRegiao == true){

            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(getActivity());
            dialogCategoria.setTitle("Selecione a categoria desejada");

            //configurar spinner do dialog
            View viewSpinner =  getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            final Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            String[] categorias = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapterR = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    categorias
            );
            adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapterR);

            dialogCategoria.setView(viewSpinner);

            dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            });

            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategoria.create();
            dialog.show();

        } else {
            Toast.makeText(getActivity(), "Escolha primeiro uma região!", Toast.LENGTH_SHORT).show();
        }



    }


    public void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot estados : dataSnapshot.getChildren()){
                    for (DataSnapshot categorias : estados.getChildren()){
                        for (DataSnapshot anuncios : categorias.getChildren()){
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);
                        }

                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarAnunciosPorRegiao(){

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //configura nó por regiao
        anunciosPublicosRef = FirebaseConfig.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroRegiao);
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaAnuncios.clear();

                for (DataSnapshot categorias : dataSnapshot.getChildren()){
                    for (DataSnapshot anuncios : categorias.getChildren()){
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);
                    }

                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarAnunciosPorCategoria(){

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //configura nó por categoria
        anunciosPublicosRef = FirebaseConfig.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroRegiao)
                .child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaAnuncios.clear();

                    for (DataSnapshot anuncios : dataSnapshot.getChildren()){
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);
                    }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
