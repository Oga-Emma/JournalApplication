package ng.com.oga_emma.journalapplication.database;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ng.com.oga_emma.journalapplication.interfaces.AddEntry;
import ng.com.oga_emma.journalapplication.interfaces.Entry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;

import static ng.com.oga_emma.journalapplication.utils.FirebaseStringUtils.JOURNAL_ENTRY_NODE;
import static ng.com.oga_emma.journalapplication.utils.FirebaseStringUtils.USER_NODE;

public class JournalEntryFirebaseDB implements Entry.Repository {

    private String TAG = JournalEntryFirebaseDB.class.getSimpleName();

    private static JournalEntryFirebaseDB repository;

    private ChildEventListener childEventListener = null;
    private ValueEventListener singleValueEventListener = null;

    final FirebaseDatabase database;
    private DatabaseReference ref = null;

    private JournalEntryFirebaseDB(String userId) {
        this.database = FirebaseDatabase.getInstance();
        ref = database.getReference(USER_NODE).child(userId).child(JOURNAL_ENTRY_NODE);

    }

    public static JournalEntryFirebaseDB getInstance(String userId) {
        if (repository == null)
            repository = new JournalEntryFirebaseDB(userId);

        return repository;
    }


    @Override
    public void fetchJournalEntries(final Entry.JournalEntriesFetchOnceListener listener) {

        Log.i(TAG, "got here waiting for data");

        if (singleValueEventListener == null) {
            singleValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    Map<String, JournalEntry> entryMap = new LinkedHashMap<>();

                    Log.i(TAG, "datasnapshot size: " + snapshot.getChildrenCount());

                    JournalEntry entry = null;

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        entry = postSnapshot.getValue(JournalEntry.class);

                        if(entry != null)
                            entryMap.put(entry.getUUID(), entry);
                    }

                    LiveData<List<JournalEntry>> listLiveData = new LiveData<List<JournalEntry>>() {
                        @Override
                        protected void setValue(List<JournalEntry> value) {
                            super.setValue(value);
                        }

                        @Override
                        protected void postValue(List<JournalEntry> value) {
                            super.postValue(value);
                        }
                    };

                    listener.onJournalEntryFetchSuccess(entryMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error loading data");
                    listener.onJournalEntryFetchFailed();
                }
            };
        }

        ref.addValueEventListener(singleValueEventListener);
    }

    /*@Override
    public void setupFirebaseChildEventListeners(final Entry.JournalEntriesFetchListener listener) {

        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    listener.onJournalEntryAdded(dataSnapshot.getValue(JournalEntry.class));
                    // ...
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    String entryKey = dataSnapshot.getKey();


                    listener.onJournalEntryModified(dataSnapshot.getValue(JournalEntry.class), entryKey);

                    // ...
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    String entryKey = dataSnapshot.getKey();


                    listener.onJournalEntryRemoved(dataSnapshot.getValue(JournalEntry.class), entryKey);

                    // ...
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            ref.addChildEventListener(childEventListener);
        }
    }*/

    @Override
    public void addJournalEntry(JournalEntry entry, AddEntry.AddEntryListener listener) {
        ref.child(entry.getUUID()).setValue(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onEntryAddSuccess();
                        Log.i(TAG, "Entry added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onEntryAddFail();
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    @Override
    public void updateJournalEntry(JournalEntry entry, AddEntry.UpdateEntryListener listener) {
        ref.child(entry.getUUID()).setValue(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onUpdateEntrySuccess();
                        Log.i(TAG, "Entry added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onUpdateEntryFail();
                Log.e(TAG, e.getMessage(), e);
            }
        });

    }

    @Override
    public void deleteJournalEntry(JournalEntry entry, AddEntry.DeleteEntryListener listener) {
        ref.child(entry.getUUID()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onDeleteEntrySuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onDeleteEntryFail();
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    public void clearListeners() {

        if (null != childEventListener) {
            ref.child(JOURNAL_ENTRY_NODE).removeEventListener(childEventListener);
            childEventListener = null;
        }

        if(null != singleValueEventListener) {
            ref.child(JOURNAL_ENTRY_NODE).removeEventListener(singleValueEventListener);
            childEventListener = null;
        }
    }
}
