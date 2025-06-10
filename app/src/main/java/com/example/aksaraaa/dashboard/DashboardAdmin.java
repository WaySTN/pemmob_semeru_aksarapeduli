package com.example.aksaraaa.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aksaraaa.features.beranda.admin.BerandaFragmentAdmin;
import com.example.aksaraaa.features.donasi.admin.DonasiFragmentAdmin;
import com.example.aksaraaa.features.kabar.admin.KabarAdminFragment;
import com.example.aksaraaa.features.profil.admin.ProfilFragmentAdmin;
import com.example.aksaraaa.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardAdmin extends AppCompatActivity {

    private static final String TAG = "DashboardAdmin";
    private BottomNavigationView adminBottomView;
    private final BerandaFragmentAdmin berandaAdmin = new BerandaFragmentAdmin();
    private final KabarAdminFragment kabarAdmin = new KabarAdminFragment();
    private final DonasiFragmentAdmin donasiAdmin = new DonasiFragmentAdmin();
    private final ProfilFragmentAdmin profilAdmin = new ProfilFragmentAdmin();
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_dashboard_admin);

        // 2. Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // 3. Apply padding to handle system bars
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            // 4. Apply padding only where needed (adjust according to your layout)
            v.setPadding(0, top, 0, 0); // Top padding for status bar

            // 5. Handle bottom navigation bar inset
            if (adminBottomView != null) {
                adminBottomView.setPadding(
                        adminBottomView.getPaddingLeft(),
                        adminBottomView.getPaddingTop(),
                        adminBottomView.getPaddingRight(),
                        bottom // Add bottom inset to bottom navigation
                );
            }

            return insets;
        });

        initializeViews();
        setupNavigation();

        // Set default fragment
        if (savedInstanceState == null) {
            setCurrentFragment(berandaAdmin, false);
        }
    }

    private void initializeViews() {
        adminBottomView = findViewById(R.id.AdminbottomView);
        if (adminBottomView == null) {
            throw new IllegalStateException("BottomNavigationView not found");
        }
    }

    private void setupNavigation() {
        adminBottomView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.berandaAdmin) {
                return setCurrentFragment(berandaAdmin, true);
            } else if (id == R.id.newspaperAdmin) {
                return setCurrentFragment(kabarAdmin, true);
            } else if (id == R.id.donationAdmin) {
                return setCurrentFragment(donasiAdmin, true);
            } else if (id == R.id.profilAdmin) {
                return setCurrentFragment(profilAdmin, true);
            }
            return false;
        });
    }

    private boolean setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null || fragment.equals(currentFragment)) {
            return false;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.AdminFragment, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        transaction.commit();
        currentFragment = fragment;
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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