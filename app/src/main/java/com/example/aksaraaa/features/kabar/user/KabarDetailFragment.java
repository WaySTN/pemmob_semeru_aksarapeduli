package com.example.aksaraaa.features.kabar.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Kabar;
import com.google.android.material.appbar.MaterialToolbar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class KabarDetailFragment extends Fragment {
    private static final String ARG_KABAR = "kabar";

    public static KabarDetailFragment newInstance(Kabar kabar) {
        KabarDetailFragment fragment = new KabarDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_KABAR, kabar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable options menu in fragment
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_kabar_detail, container, false);

        // Setup Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            // Handle back button click
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            Kabar kabar = (Kabar) getArguments().getSerializable(ARG_KABAR);
            if (kabar != null) {
                TextView tvJudul = view.findViewById(R.id.tvJudulDetail);
                TextView tvTanggal = view.findViewById(R.id.tvTanggalDetail);
                TextView tvIsi = view.findViewById(R.id.tvIsiDetail);

                tvJudul.setText(kabar.getJudul());
                tvIsi.setText(kabar.getIsi());

                if (kabar.getTanggal() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    tvTanggal.setText(sdf.format(kabar.getTanggal().toDate()));
                }
            }
        }

        return view;
    }
}