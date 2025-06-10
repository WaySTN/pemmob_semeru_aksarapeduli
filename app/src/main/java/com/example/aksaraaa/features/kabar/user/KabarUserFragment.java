package com.example.aksaraaa.features.kabar.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Kabar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class KabarUserFragment extends Fragment implements KabarUserAdapter.OnKabarClickListener {
    private RecyclerView rvKabar;
    private KabarUserAdapter adapter;
    private List<Kabar> kabarList = new ArrayList<>();
    private FirebaseFirestore db;
    private View emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kabar_user, container, false);

        db = FirebaseFirestore.getInstance();
        rvKabar = view.findViewById(R.id.rvKabar);
        emptyView = view.findViewById(R.id.layout_empty);

        setupRecyclerView();
        loadKabarData();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new KabarUserAdapter(kabarList, this);
        rvKabar.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKabar.setAdapter(adapter);
    }

    private void loadKabarData() {
        db.collection("kabar")
                .orderBy("tanggal", Query.Direction.DESCENDING)
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
        if (emptyView != null) {
            emptyView.setVisibility(kabarList.isEmpty() ? View.VISIBLE : View.GONE);
        }
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
}