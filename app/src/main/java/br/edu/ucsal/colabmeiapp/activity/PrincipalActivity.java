package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.fragments.ColaborativoFragment;
import br.edu.ucsal.colabmeiapp.fragments.PerfilFragment;
import br.edu.ucsal.colabmeiapp.fragments.PesquisaFragment;
import br.edu.ucsal.colabmeiapp.fragments.PublicarFragment;
import br.edu.ucsal.colabmeiapp.fragments.SocialFeedFragment;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //configurar objetos
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();

        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("CollabMEI APP");
        setSupportActionBar(toolbar);

        //configurar botton navigation view
        configurarBottonNavigationView();
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new ColaborativoFragment()).commit();

    }

    private void configurarBottonNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottonNavigation);

        //configuracoes iniciais do botton navigation
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);

        //habilitar navegação
        habilitarNavegacao(bottomNavigationViewEx);

        //Seta home selecionado por padrão
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem =  menu.getItem(4);
        menuItem.setChecked(true);



    }

    //Método responsavel por tratar eventos de click na BottonNavigationView
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager =  getSupportFragmentManager();
                FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()){
                    case R.id.ic_home :
                        fragmentTransaction.replace(R.id.viewPager, new SocialFeedFragment()).commit();
                        return true;
                    case R.id.ic_pesquisa :
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        return true;
                    case R.id.ic_postagem :
                        fragmentTransaction.replace(R.id.viewPager, new PublicarFragment()).commit();
                        return true;
                    case R.id.ic_perfil:
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                    case R.id.ic_comunidade :
                        fragmentTransaction.replace(R.id.viewPager, new ColaborativoFragment()).commit();
                        return true;


                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {
            case R.id.meus_anuncios :
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

            case R.id.menu_perfil :
//                startActivity(new Intent(getApplicationContext(), MeuPerfilActivity.class));
                break;

            case R.id.menu_sair :
                autenticacao.signOut();
                finish();
                Toast.makeText(PrincipalActivity.this,
                        "Usuario deslogado com sucesso!",
                        Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
