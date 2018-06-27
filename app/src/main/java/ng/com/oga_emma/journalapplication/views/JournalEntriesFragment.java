package ng.com.oga_emma.journalapplication.views;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.adapters.EntryAdapter;
import ng.com.oga_emma.journalapplication.model.JournalEntry;

public class JournalEntriesFragment extends Fragment {

    private RecyclerView entrieRecyclerView;
    private List<JournalEntry> journalEntryList;
    private EntryAdapter adapter;

    private ConstraintLayout titleLayout;


    public JournalEntriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_journal_entries, container, false);

        titleLayout = v.findViewById(R.id.constraintLayout);

        entrieRecyclerView = v.findViewById(R.id.entries_recycler_view);
        entrieRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntryAdapter(Collections.EMPTY_LIST);
        entrieRecyclerView.setAdapter(adapter);

        journalEntryList = new ArrayList<>();
//        setupRecyclerView(journalEntryList);

        /*entrieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    if(titleLayout.getVisibility() == View.VISIBLE)
                    titleLayout.setVisibility(View.GONE);

                } else {
                    // Scrolling down
                    if(titleLayout.getVisibility() == View.GONE)
                    titleLayout.setVisibility(View.VISIBLE);
                }
            }
        });*/

        return v;
    }

    private void setupRecyclerView(List<JournalEntry> journalEntryList) {

    }

}
