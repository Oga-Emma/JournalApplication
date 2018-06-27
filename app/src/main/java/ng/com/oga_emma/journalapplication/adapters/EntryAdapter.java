package ng.com.oga_emma.journalapplication.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.model.JournalEntry;

public class EntryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<JournalEntry> emptyList;

    public EntryAdapter(List<JournalEntry> emptyList) {
        this.emptyList = emptyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_rv_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class EntryHolder extends RecyclerView.ViewHolder{

        public EntryHolder(View itemView) {
            super(itemView);
        }
    }
}
