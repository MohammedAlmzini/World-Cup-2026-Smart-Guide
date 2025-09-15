package com.ahmmedalmzini783.wcguide.ui.home.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.home.PlaceAdapter;
import com.ahmmedalmzini783.wcguide.ui.home.HomeViewModel;

public class HotelsFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private HomeViewModel viewModel;

    public static HotelsFragment newInstance(HomeViewModel viewModel) {
        HotelsFragment fragment = new HotelsFragment();
        fragment.viewModel = viewModel;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section_grid, container, false);
        
        recyclerView = view.findViewById(R.id.section_recycler);
        adapter = new PlaceAdapter(place -> {
            // Handle place click
        });
        
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        
        // Observe data if viewModel is available
        if (viewModel != null && viewModel.getHotels() != null) {
            viewModel.getHotels().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.getData() != null) {
                    adapter.submitList(resource.getData());
                }
            });
        }
        
        return view;
    }
}
