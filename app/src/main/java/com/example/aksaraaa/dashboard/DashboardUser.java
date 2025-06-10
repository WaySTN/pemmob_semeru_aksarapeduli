package com.example.aksaraaa.dashboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aksaraaa.features.beranda.user.BerandaFragmentUser;
import com.example.aksaraaa.features.donasi.user.DonasiFragmentUser;
import com.example.aksaraaa.features.kabar.user.KabarUserFragment;
import com.example.aksaraaa.features.profil.user.ProfilUserFragment;
import com.example.aksaraaa.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardUser extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final BerandaFragmentUser berandaFragment = new BerandaFragmentUser();
    private final KabarUserFragment kabarFragment = new KabarUserFragment();
    private final DonasiFragmentUser donasiFragment = new DonasiFragmentUser();
    private final ProfilUserFragment profilFragment = new ProfilUserFragment();
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_dashboard_user);

        // 2. Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // 3. Get system bars insets
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            // 4. Apply padding to main container (top only)
            v.setPadding(0, top, 0, 0);

            // 5. Handle bottom navigation bar inset
            if (bottomNavigationView != null) {
                bottomNavigationView.setPadding(
                        bottomNavigationView.getPaddingLeft(),
                        bottomNavigationView.getPaddingTop(),
                        bottomNavigationView.getPaddingRight(),
                        bottom // Add bottom inset to bottom navigation
                );
            }

            return insets;
        });

        // Inisialisasi BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomView);

        // Set fragment default saat pertama kali dibuka
        if (savedInstanceState == null) {
            setFragment(berandaFragment, false);
        }

        // Listener untuk menu navigasi
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                return setFragment(berandaFragment, true);
            } else if (id == R.id.newspaper) {
                return setFragment(kabarFragment, true);
            } else if (id == R.id.donation) {
                return setFragment(donasiFragment, true);
            } else if (id == R.id.profil) {
                return setFragment(profilFragment, true);
            }

            return false;
        });
    }

    // Fungsi untuk menampilkan fragment ke FrameLayout
    private boolean setFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null || fragment.equals(currentFragment)) {
            return false;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.UserFragment, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        fragmentTransaction.commit();
        currentFragment = fragment;
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}