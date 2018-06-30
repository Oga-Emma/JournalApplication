package ng.com.oga_emma.journalapplication.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.model.JournalEntry;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryHolder>{

    private ArrayList<JournalEntry> journalEntryList;

    public EntryAdapter(ArrayList<JournalEntry> journalEntryList) {

        this.journalEntryList = journalEntryList;
    }

    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_rv_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EntryHolder holder, int position) {
        holder.bindView(journalEntryList.get(position));
    }

    @Override
    public int getItemCount() {

        Log.i("LIST SIZE", journalEntryList.size() + "");

        return journalEntryList.size();
    }

    public class EntryHolder extends RecyclerView.ViewHolder{

        TextView entryTitleTV, entryBodyTV, dateTV;

        public EntryHolder(View itemView) {
            super(itemView);

            entryTitleTV = itemView.findViewById(R.id.entry_title_tv);
            entryBodyTV = itemView.findViewById(R.id.entry_body_tv);
            dateTV = itemView.findViewById(R.id.date_tv);
        }

        void bindView(JournalEntry entry){
            entryTitleTV.setText(entry.getEntryTitle());
            entryBodyTV.setText(entry.getEntryBody());

            Date date = new Date(entry.getEntryDate());
            SimpleDateFormat dt1 = new SimpleDateFormat("EE dd-MMM-yyyy");
            dateTV.setText(dt1.format(date));
        }
    }
}
