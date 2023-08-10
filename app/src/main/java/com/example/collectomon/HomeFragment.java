package com.example.collectomon;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeFragment extends Fragment{
    private List<String> artistNames;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private SharedPreferences sharedPreferences;
    Button backup, restore;
    ImageButton addArtistButton, deleteArtistButton;

    CardDatabase db;
    Context context;
    private ListView listViewArtists;
    private ArrayAdapter<String> arrayAdapter;
    private int checkedPosition = -1;
    EditText addArtist;


    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addArtistButton = view.findViewById(R.id.addArtistButton);
        deleteArtistButton = view.findViewById(R.id.deleteArtistButton);
        addArtist = view.findViewById(R.id.searchCard);
        backup = view.findViewById(R.id.backupButton);
        restore = view.findViewById(R.id.restoreButton);
        context = requireContext();
        db = new CardDatabase(context);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        artistNames = new ArrayList<>();
        addArtistButton.getDrawable().setAlpha(200);
        deleteArtistButton.getDrawable().setAlpha(200);


        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item_artist, artistNames);

        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }
        saveArtistList(artistNames);


        listViewArtists = view.findViewById(R.id.listViewArtists);  // Find the ListView
        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item_artist, artistNames);
        listViewArtists.setAdapter(arrayAdapter);
        listViewArtists.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewArtists.setOnItemClickListener((parent, view1, position, id) -> checkedPosition = position);

        backup.setOnClickListener(v -> db.saveBackup());

        restore.setOnClickListener(v -> db.restoreBackup());

        deleteArtistButton.setOnClickListener(v -> {
            if (checkedPosition != -1) {
                artistNames.remove(checkedPosition);
                arrayAdapter.notifyDataSetChanged();
                listViewArtists.setItemChecked(checkedPosition, false);
                checkedPosition = -1;
                Toast.makeText(requireContext(), "Artist deleted", Toast.LENGTH_SHORT).show();
                pulseAnimation(deleteArtistButton);
                saveArtistList(artistNames);

            } else {
                Toast.makeText(requireContext(), "No artist selection", Toast.LENGTH_SHORT).show();
                pulseAnimation(deleteArtistButton);
            }
        });

        addArtistButton.setOnClickListener(v -> {
            if (addArtist.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "No artist name", Toast.LENGTH_SHORT).show();
                pulseAnimation(addArtistButton);
            } else {

                String name = addArtist.getText().toString();
                addArtistToList(name);
                pulseAnimation(addArtistButton);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addArtistToList(String name) {
        if (!artistNames.contains(name)) {
            artistNames.add(name);
            arrayAdapter.notifyDataSetChanged();
            addArtist.setText("");
            listViewArtists.setItemChecked(artistNames.size() - 1, true);
            checkedPosition = artistNames.size() - 1;
            saveArtistList(artistNames);
        } else {
            Toast.makeText(context, "Artist " + name + " is already in the list.", Toast.LENGTH_SHORT).show();
        }
    }




    private void saveArtistList(List<String> artistList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(artistList);
        editor.putStringSet(ARTIST_KEY, set);
        editor.apply();
    }

    private void pulseAnimation(ImageButton button) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                button,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        );
        scaleDown.setDuration(500);
        scaleDown.setRepeatCount(ObjectAnimator.RESTART);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

    }
}
