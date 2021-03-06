package com.example.weathertracking.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.Toast;

import com.example.weathertracking.adapters.FavoriteListAdapter;
import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.R;

import java.util.ArrayList;

import static com.example.weathertracking.Interfaces.InternetStateListener.isConecctedToInternet;
import static com.example.weathertracking.Utils.Favorites.getFavoriteLocationsFromSharedPref;


public class FavoriteLocationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteListAdapter adapter;
    private View view;
    private BroadcastReceiver networkActionReceiver;
    private BroadcastReceiver refreshReceiver;
    private Boolean refreshRequired=false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FavoriteLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        view=inflater.inflate(R.layout.fragment_favorite_locations,container,false);

        mSwipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayoutFavoritesList);
        recyclerView= view.findViewById(R.id.recyclerView);
        SearchView searchView = view.findViewById(R.id.favorite_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                uploadFavorites();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
                Toast.makeText(view.getContext(),"Favorites refreshed!",Toast.LENGTH_SHORT).show();
            }
        });
        IntentFilter filter = new IntentFilter("REFRESH_FAVORITES");
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshFavorites();
                Log.d("Location**"," Favorite locations refresh request received");
            }
        };
        view.getContext().registerReceiver(refreshReceiver, filter);

        networkActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if("DISCONNECTED".equals(intent.getStringExtra("TYPE"))){

                    mSwipeRefreshLayout.setEnabled(false);
                }
                else {
                    if("RECONNECTED".equals(intent.getStringExtra("TYPE"))){
                        mSwipeRefreshLayout.setEnabled(true);
                    }
                }
            }
        };
        IntentFilter networkActionFilter = new IntentFilter("NETWORK_ACTION");
        view.getContext().registerReceiver(networkActionReceiver,networkActionFilter);
        uploadFavorites();
        return view;
    }
    private void uploadFavorites(){
        //deleteWhenSwipe(adapter,recyclerView);

        ArrayList<FavoriteLocationObject> w=getFavoriteLocationsFromSharedPref(view.getContext());
        if(w==null){
            w= new ArrayList<>();
        }
        adapter = new FavoriteListAdapter(view.getContext(),w);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void refreshFavorites(){
        //deleteWhenSwipe(adapter,recyclerView);

        ArrayList<FavoriteLocationObject> w=getFavoriteLocationsFromSharedPref(view.getContext());//TODO view.getContext

        adapter.setFavorites(w);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    /*
        private void deleteWhenSwipe(final WordListAdapter adapter,RecyclerView recyclerView){
            // Add the functionality to swipe items in the
            // recycler view to delete that item
            ItemTouchHelper helper = new ItemTouchHelper(
                    new ItemTouchHelper.SimpleCallback(0,
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView,
                                              RecyclerView.ViewHolder viewHolder,
                                              RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                             int direction) {
                            int position = viewHolder.getAdapterPosition();
                            Word myWord = adapter.getWordAtPosition(position);
                            Toast.makeText(MainActivity.this, "Deleting " +
                                    myWord.getWord(), Toast.LENGTH_LONG).show();

                            // Delete the word
                            mfavoritesViewModel.deleteWord(myWord);
                        }
                    });

            helper.attachToRecyclerView(recyclerView);
        }*/
    @Override
    public void onResume() {
        super.onResume();
        if(refreshRequired){
            uploadFavorites();
        }
        if(!isConecctedToInternet()){
            Toast.makeText(view.getContext(),"No internet",Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        view.getContext().unregisterReceiver(refreshReceiver);
    }

}
