package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class noteDetailsActivity extends Fragment {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title, content, docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;

    public noteDetailsActivity() {
        // Required empty public constructor
    }

    public static noteDetailsActivity newInstance(String title, String content, String docId) {
        noteDetailsActivity fragment = new noteDetailsActivity();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putString("docId", docId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            title = getArguments().getString("title");
            content = getArguments().getString("content");
            docId = getArguments().getString("docId");

            if (docId != null && !docId.isEmpty()) {
                isEditMode = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_details_activity, container, false);

        titleEditText = view.findViewById(R.id.notes_title_text);
        contentEditText = view.findViewById(R.id.notes_content_text);
        saveNoteBtn = view.findViewById(R.id.save_note_btn);
        pageTitleTextView = view.findViewById(R.id.page_title);
        deleteNoteTextViewBtn = view.findViewById(R.id.delete_note_text_view_btn);

        titleEditText.setText(title);
        contentEditText.setText(content);

        if (isEditMode) {
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v) -> {
            saveNote();
            Intent intent = new Intent(getActivity(), MainActivity2.class);
            startActivity(intent);
            
                }
                

        );
        deleteNoteTextViewBtn.setOnClickListener((v) ->
        {
            deleteNoteFromFirebase();
            Intent intent = new Intent(getActivity(), MainActivity2.class);
            startActivity(intent);
        });

        return view;
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;

        if (isEditMode) {
            // Update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            // Create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Note is added or updated
                    Utility.showToast(requireContext(), "Note saved successfully");
                    requireActivity().finish();
                } else {
                    Utility.showToast(requireContext(), "Failed while saving note");
                }
            }
        });
    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Note is deleted
                    Utility.showToast(requireContext(), "Note deleted successfully");
                    requireActivity().finish();
                } else {
                    Utility.showToast(requireContext(), "Failed while deleting note");
                }
            }
        });
    }
}
