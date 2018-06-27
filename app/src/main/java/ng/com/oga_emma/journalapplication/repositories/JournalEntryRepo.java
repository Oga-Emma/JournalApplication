package ng.com.oga_emma.journalapplication.repositories;

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

import java.util.HashMap;
import java.util.Map;

import ng.com.oga_emma.journalapplication.interfaces.Entry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;

import static ng.com.oga_emma.journalapplication.utils.FirebaseStringUtils.JOURNAL_ENTRY_NODE;
import static ng.com.oga_emma.journalapplication.utils.FirebaseStringUtils.USER_NODE;

public class JournalEntryRepo implements Entry.Repository {

    private String TAG = JournalEntryRepo.class.getSimpleName();

    private static JournalEntryRepo repository;

    private ChildEventListener childEventListener = null;
    private ValueEventListener singleValueEventListener = null;

    final FirebaseDatabase database;
    private DatabaseReference ref = null;

    private JournalEntryRepo(String userId) {
        this.database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(USER_NODE).child(userId);
    }

    public static JournalEntryRepo getInstance(String userId) {
        if (repository == null)
            repository = new JournalEntryRepo(userId);

        return repository;
    }


    @Override
    public void fetchJournalEntries(final Entry.JournalEntriesFetchListener listener) {
        if (singleValueEventListener == null) {
            singleValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    Map<String, JournalEntry> entryMap = new HashMap<>();

                    Log.i(TAG, "datasnapshot size: " + snapshot.getChildrenCount());

                    JournalEntry entry = null;

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        entry = postSnapshot.getValue(JournalEntry.class);

                        entryMap.put(entry.getId(), entry);
                    }

                    listener.onJournalEntryFetchSuccess(entryMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error loading data");
                    listener.onJournalEntryFetchFailed();
                }
            };
        }

        ref.child(JOURNAL_ENTRY_NODE)
                .addListenerForSingleValueEvent(singleValueEventListener);
    }

    @Override
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

            ref.child(JOURNAL_ENTRY_NODE)
                    .addChildEventListener(childEventListener);

        }
    }

    @Override
    public void addJournalEntry(JournalEntry entry) {
        ref.child("journal_entries").child(entry.getId()).setValue(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        listener.onEntryAddSuccess();
                        Log.i(TAG, "Entry added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
//                listener.onEntryAddFailure(e);
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    @Override
    public void updateJournalEntry(JournalEntry entry) {
        ref.child("journal_entries").child(entry.getId()).setValue(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        listener.onEntryAddSuccess();
                        Log.i(TAG, "Entry added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
//                listener.onEntryAddFailure(e);
                Log.e(TAG, e.getMessage(), e);
            }
        });

    }

    @Override
    public void deleteJournalEntry(JournalEntry entry) {
        ref.child("journal_entries").child(entry.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        listener.onEntryAddSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
//                listener.onEntryAddFailure(e);
                Log.e(TAG, e.getMessage(), e);
            }
        });

    }

    public void clearListners() {
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
