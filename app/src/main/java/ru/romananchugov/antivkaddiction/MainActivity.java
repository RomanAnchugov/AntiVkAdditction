package ru.romananchugov.antivkaddiction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.romananchugov.antivkaddiction.fragments.ChatsListFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView bnvMainMenu;
    private FrameLayout fragmentContainer;
    public MainActivity mainActivity;

    private String[] scope = new String[]{
            VKScope.MESSAGES,
            VKScope.FRIENDS,
            VKScope.GROUPS,
            VKScope.WALL,
            VKScope.GROUPS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        //TODO: save access token
        VKSdk.login(this, scope);

        fragmentContainer = findViewById(R.id.fragment_container);
        bnvMainMenu = findViewById(R.id.bnv_main_navigation);
        bnvMainMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mi_messages:
                        addFragment(new ChatsListFragment(mainActivity), false);
                        break;
                    case R.id.mi_friends:
                        break;
                    case R.id.mi_groups:
                        break;
                    case R.id.mi_news:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(final VKAccessToken res) {
                // Пользователь успешно авторизовался
                Toast.makeText(getApplicationContext(), "Logged", Toast.LENGTH_SHORT).show();
                addFragment(new ChatsListFragment(mainActivity), false);
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getSupportFragmentManager().popBackStackImmediate();
                resetOnBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        resetOnBack();
    }

    public void addFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if(addToBackStack) {
            ft.addToBackStack(null);
        }
        ft = ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    public void hideBottomNav(){
        bnvMainMenu.setVisibility(View.GONE);
        fragmentContainer.setPadding(0, 0, 0, 0);
    }

    public void resetOnBack(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 0
                && getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        bnvMainMenu.setVisibility(View.VISIBLE);
        this.setTitle(getResources().getString(R.string.app_name));
        fragmentContainer.setPadding(0, 0, 0, 60);
    }
}
