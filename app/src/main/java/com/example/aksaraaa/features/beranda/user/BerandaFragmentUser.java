package com.example.aksaraaa.features.beranda.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aksaraaa.R;
import com.example.aksaraaa.features.donasi.user.DonasiFragmentUser;
import com.example.aksaraaa.features.beranda.user.history.HistoryDonasiFragmentUser;
import com.example.aksaraaa.features.kabar.user.KabarDetailFragment;
import com.example.aksaraaa.features.kabar.user.KabarUserAdapter;
import com.example.aksaraaa.models.Kabar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BerandaFragmentUser extends Fragment implements KabarUserAdapter.OnKabarClickListener {

    // Views
    private ViewPager2 viewPagerHeader;
    private EditText etSearch;
    private CardView cardAksiSaya;
    private CardView donasiSaya;
    private TextView tvLihatSemua;
    private RecyclerView recyclerViewDonasi;

    // Firebase
    private FirebaseFirestore db;

    // Data
    private List<Kabar> kabarList;
    private KabarUserAdapter adapter;

    public BerandaFragmentUser() {
        // Required empty public constructor
    }

    public static BerandaFragmentUser newInstance() {
        BerandaFragmentUser fragment = new BerandaFragmentUser();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle arguments if needed
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize data
        kabarList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beranda_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadKabarData();
    }

    private void initViews(View view) {
        viewPagerHeader = view.findViewById(R.id.viewPagerHeader);
        etSearch = view.findViewById(R.id.etSearch);
        cardAksiSaya = view.findViewById(R.id.cardAksiSaya);
        donasiSaya = view.findViewById(R.id.DonasiSaya);
        tvLihatSemua = view.findViewById(R.id.tvLihatSemua);
        recyclerViewDonasi = view.findViewById(R.id.recyclerViewDonasi);
    }

    private void setupRecyclerView() {
        // Setup horizontal RecyclerView for articles
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDonasi.setLayoutManager(layoutManager);

        adapter = new KabarUserAdapter(kabarList, this);
        recyclerViewDonasi.setAdapter(adapter);

        // Add item decoration for spacing between items
        int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing); // Define this in dimens.xml
        recyclerViewDonasi.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
    }

    private void setupClickListeners() {
        // Aksi Berbagi - Navigate to DonasiFragmentUser
        cardAksiSaya.setOnClickListener(v -> {
            DonasiFragmentUser fragment = new DonasiFragmentUser();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.UserFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Donasi Saya - Navigate to HistoryDonasiFragment
        donasiSaya.setOnClickListener(v -> {
            HistoryDonasiFragmentUser fragment = new HistoryDonasiFragmentUser();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.UserFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Lihat Semua - Show all articles
        tvLihatSemua.setOnClickListener(v -> {
            // Navigate to all articles page
            // You can create a new fragment for showing all articles
            showAllArticles();
        });

        // Search functionality can be implemented here if needed
        // etSearch.setOnEditorActionListener(...);
    }

    private void loadKabarData() {
        db.collection("kabar")
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(10) // Limit to show only recent articles
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        kabarList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Kabar kabar = document.toObject(Kabar.class);
                            kabar.setId(document.getId());
                            kabarList.add(kabar);
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        Toast.makeText(getContext(), "Gagal memuat kabar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmptyState() {
        // Handle empty state if needed
        if (kabarList.isEmpty()) {
            // Show empty state
            Toast.makeText(getContext(), "Tidak ada artikel tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllArticles() {
        // Navigate to fragment that shows all articles
        // This is a placeholder - you can create KabarListFragment or similar
        Toast.makeText(getContext(), "Menampilkan semua artikel", Toast.LENGTH_SHORT).show();

        // Example implementation:
        // KabarListFragment fragment = KabarListFragment.newInstance();
        // requireActivity().getSupportFragmentManager()
        //         .beginTransaction()
        //         .replace(R.id.UserFragment, fragment)
        //         .addToBackStack(null)
        //         .commit();
    }

    @Override
    public void onKabarClick(Kabar kabar) {
        KabarDetailFragment fragment = KabarDetailFragment.newInstance(kabar);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.UserFragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Inner class for horizontal spacing between RecyclerView items
    private static class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalSpaceWidth;

        public HorizontalSpaceItemDecoration(int horizontalSpaceWidth) {
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.right = horizontalSpaceWidth;

            // Add space to the left of the first item
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 0;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible again
        loadKabarData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources if needed
    }
}