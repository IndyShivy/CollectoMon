package com.example.collectomon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    BottomNavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
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
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private CollectionFragment collectionFragment;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //navigationView.setOnItemSelectedListener(this);
        addArtistButton = view.findViewById(R.id.addArtistButton);
        deleteArtistButton = view.findViewById(R.id.deleteArtistButton);
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        collectionFragment = new CollectionFragment();
        addArtist = view.findViewById(R.id.searchCard);
        backup = view.findViewById(R.id.backupButton);
        restore = view.findViewById(R.id.restoreButton);
        context = requireContext();
        db = new CardDatabase(context);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        artistNames = new ArrayList<>();
        artistNames.add("Yuka Morii");
        artistNames.add("Saya Tsuruta");

        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }
        saveArtistList(artistNames);

        listViewArtists = view.findViewById(R.id.listViewArtists);  // Find the ListView
        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item_artist, artistNames);
        listViewArtists.setAdapter(arrayAdapter);
        listViewArtists.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedPosition = position;
            }
        });

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.saveBackup();
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.restoreBackup();
            }
        });

        deleteArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition != -1) {
                    artistNames.remove(checkedPosition);
                    arrayAdapter.notifyDataSetChanged();
                    listViewArtists.setItemChecked(checkedPosition, false);
                    checkedPosition = -1;
                    Toast.makeText(requireContext(), "Artist deleted", Toast.LENGTH_SHORT).show();
                    saveArtistList(artistNames);
                } else {
                    Toast.makeText(requireContext(), "No artist selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addArtist.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "No artist name", Toast.LENGTH_SHORT).show();
                } else {
                    String name = addArtist.getText().toString();
                    addArtistToList(name);
                }
            }
        });

        return view;
    }


    private void closeDrawer() {
        drawerLayout.closeDrawer(navigationView);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addArtistToList(String name) {
        artistNames.add(name);
        arrayAdapter.notifyDataSetChanged();
        addArtist.setText("");
        listViewArtists.setItemChecked(artistNames.size() - 1, true);
        checkedPosition = artistNames.size() - 1;
        saveArtistList(artistNames);
    }

    private void saveArtistList(List<String> artistList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(artistList);
        editor.putStringSet(ARTIST_KEY, set);
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            // Handle Home menu item click
        } else if (id == R.id.menu_search) {
            SearchFragment searchFragment = new SearchFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, searchFragment);
            transaction.commit();
        } else if (id == R.id.menu_collection) {
            // Handle Collection menu item click
        }
        return true;
    }

}
