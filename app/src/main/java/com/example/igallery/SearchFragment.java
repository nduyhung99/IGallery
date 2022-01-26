package com.example.igallery;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.igallery.adapter.ButketAdapter;
import com.example.igallery.adapter.ButketAdapter1;
import com.example.igallery.adapter.RecentlySearchAdapter;
import com.example.igallery.adapter.SearchAdapter;
import com.example.igallery.adapter.SuggestionsSearchAdapter;
import com.example.igallery.albumsfragment.ItemInAlbumsFragment;
import com.example.igallery.database.Database;
import com.example.igallery.model.Butket;
import com.example.igallery.model.Item;
import com.ldt.springback.view.SpringBackLayout;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {
    private TextView txtSearch, txtCloseSearch, txtSeeAll, txtCountSuggesstion, txtClear, txtNoResult;
    private SearchView srcView;
    private RecyclerView rcvAlbumSearch, rcvPlaceSearch, rcvYearSearch, rcvSuggestions;
    private RecyclerView rcvRecentlySearch, rcvAlbumSearch1, rcvPlaceSearch1, rcvYearSearch1;
    private List<Butket> listButket = new ArrayList<>();
    private List<Butket> listYear = new ArrayList<>();
    private List<Butket> listLocation = new ArrayList<>();
    private ItemInAlbumsFragment.ISendDataListener iSendDataListener;
    TextView txtAlbum;
    private SpringBackLayout nesView, nesView1;
    private LinearLayout layoutSuggestions, layoutTest1;
    private List<Butket> listSend = new ArrayList<>();
    private List<Butket> listSearch = new ArrayList<>();
    private SuggestionsSearchAdapter suggestionsAdapter;
    private List<Item> listLocationSend = new ArrayList<>();

    private ButketAdapter1 butketAdapter1, butketAdapter11, butketAdapter12;

    private SearchAdapter searchAdapter, searchAdapter1, searchAdapter2;

    private TextView txtSeeAll1, txtSeeAll2, txtSeeAll3, txtColapse1, txtColapse2, txtColapse3;

    int checkSeeOrCollapse1 = 0, checkSeeOrCollapse2 = 0, checkSeeOrCollapse3 = 0, onTouch = 0;

    private LinearLayout layoutAlbumSeaarch, layoutPlaceSearch, layoutMonthSearch, layoutRecentlySearch;
    private LinearLayout layoutAlbumSeaarch1, layoutPlaceSearch1, layoutMonthSearch1, layoutRecently, layoutResult;
    private RelativeLayout layoutNoResult, layoutProgressBar;

    private CursorAdapter cursorAdapter;

    Database database;
    private List<String> listRecentlySearch = new ArrayList<>();
    private RecentlySearchAdapter recentlySearchAdapter;

    long onClickTime = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iSendDataListener = (ItemInAlbumsFragment.ISendDataListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        try {
            addControls(view);
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.queryData("delete from Search");
                listRecentlySearch = getListRecently();
                recentlySearchAdapter.setData(listRecentlySearch);
                checkSizeRcvRecentlySearch(listRecentlySearch.size());
            }
        });

        recentlySearchAdapter.setmOnClickItemRecentlySearch(new RecentlySearchAdapter.OnClickItemRecentlySearch() {
            @Override
            public void onClick(String text) {
                srcView.setQuery(text, false);
            }
        });

        txtSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srcView.clearFocus();
                onTouch = 1;
                listSend = suggestionsAdapter.getListSearch();
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", "");
                bundle.putInt("TYPE", 4);
                bundle.putSerializable("LIST_BUTKET", (Serializable) listSend);

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        txtSeeAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtColapse1.setVisibility(View.VISIBLE);
                txtSeeAll1.setVisibility(View.GONE);
                searchAdapter.setCollapse(false);
                checkSeeOrCollapse1 = 1;
            }
        });

        txtColapse1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtColapse1.setVisibility(View.GONE);
                txtSeeAll1.setVisibility(View.VISIBLE);
                searchAdapter.setCollapse(true);
                checkSeeOrCollapse1 = 0;
            }
        });

        txtSeeAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtColapse2.setVisibility(View.VISIBLE);
                txtSeeAll2.setVisibility(View.GONE);
                searchAdapter2.setCollapse(false);
                checkSeeOrCollapse2 = 1;
            }
        });

        txtColapse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtColapse2.setVisibility(View.GONE);
                txtSeeAll2.setVisibility(View.VISIBLE);
                searchAdapter2.setCollapse(true);
                checkSeeOrCollapse2 = 0;
            }
        });

        txtSeeAll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtColapse3.setVisibility(View.VISIBLE);
                txtSeeAll3.setVisibility(View.GONE);
                searchAdapter1.setCollapse(false);
                checkSeeOrCollapse3 = 1;
            }
        });

        txtColapse3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtColapse3.setVisibility(View.GONE);
                txtSeeAll3.setVisibility(View.VISIBLE);
                searchAdapter1.setCollapse(true);
                checkSeeOrCollapse3 = 0;
            }
        });

//        suggestionsAdapter.setLol(new SuggestionsSearchAdapter.ISendDataSuggesstion() {
//            @Override
//            public void iSenddata(List<Butket> list) {
//                listSend = list;
//            }
//        });

        suggestionsAdapter.setOnIClick(new SuggestionsSearchAdapter.OnItemClickListenerSuggesstion() {
            @Override
            public void onItemClick(int position) {
                srcView.clearFocus();
                onTouch = 1;
                listSend = suggestionsAdapter.getListSearch();
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", "");
                bundle.putInt("TYPE", 4);
                bundle.putSerializable("LIST_BUTKET", (Serializable) listSend);

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        txtAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), String.valueOf(listButket.size()), Toast.LENGTH_SHORT).show();
            }
        });

        srcView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.search(query);
                searchAdapter1.search(query);
                searchAdapter2.search(query);
                suggestionsAdapter.search(query);
                if (query.equals("")) {
                    layoutSuggestions.setVisibility(View.GONE);
                    nesView.setVisibility(View.GONE);
                    nesView1.setVisibility(View.VISIBLE);
                } else {
                    nesView.setVisibility(View.VISIBLE);
                    nesView1.setVisibility(View.GONE);
                }
                if (!listRecentlySearch.contains(query)) {
                    if (listRecentlySearch.size() < 3) {
                        database.queryData("insert into Search values(null,'" + query + "')");
                        listRecentlySearch = getListRecently();
                        recentlySearchAdapter.setData(listRecentlySearch);
                        checkSizeRcvRecentlySearch(listRecentlySearch.size());
                    } else {
                        String path = listRecentlySearch.get(2);
                        database.queryData("delete from Search where Text='" + path + "'");
                        database.queryData("insert into Search values(null,'" + query + "')");
                        listRecentlySearch = getListRecently();
                        recentlySearchAdapter.setData(listRecentlySearch);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    layoutSuggestions.setVisibility(View.GONE);
                    nesView.setVisibility(View.GONE);
                    nesView1.setVisibility(View.VISIBLE);
                } else {
                    nesView.setVisibility(View.VISIBLE);
                    nesView1.setVisibility(View.GONE);
                }

                searchAdapter.search(newText);
                searchAdapter1.search(newText);
                searchAdapter2.search(newText);
                suggestionsAdapter.search(newText);
                if (newText.equals("")) {
                    layoutSuggestions.setVisibility(View.GONE);
                    nesView.setVisibility(View.GONE);
                    nesView1.setVisibility(View.VISIBLE);
                } else {
                    nesView.setVisibility(View.VISIBLE);
                    nesView1.setVisibility(View.GONE);
                }
                checkSizeRcvAlbum(searchAdapter.getSize());
                checkSizeRcvPlace(searchAdapter2.getSize());
                checkSizeRcvMonth(searchAdapter1.getSize());
                checkSizeRcvSuggestion(suggestionsAdapter.getSize());
                return false;
            }
        });

        srcView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    txtSearch.setVisibility(View.GONE);
                    txtCloseSearch.setVisibility(View.VISIBLE);
//                    LinearLayout ll = new LinearLayout(getContext());
//                    ll.setOrientation(LinearLayout.VERTICAL);
//
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                    layoutParams.setMargins(0, 0, 0, 0);
//                    nesView.setLayoutParams(layoutParams);
                } else {
                    if (srcView.getQuery().length() == 0) {
                        txtSearch.setVisibility(View.VISIBLE);
                        txtCloseSearch.setVisibility(View.GONE);
                    }
                }
            }
        });

        txtCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSuggestions.setVisibility(View.GONE);
                srcView.setQuery("", false);
                srcView.clearFocus();
            }
        });

        butketAdapter1.setOnItemClickListener(new ButketAdapter1.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (onClickTime == 0 || onClickTime + 1000 < System.currentTimeMillis()) {
                    onClickTime = System.currentTimeMillis();
                    String name = listButket.get(position).getName();
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME_FOLDER", name);
                    bundle.putInt("TYPE", 1);

                    ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                    itemInAlbumsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                    fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
                }
            }
        });

        butketAdapter11.setOnItemClickListener(new ButketAdapter1.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (onClickTime == 0 || onClickTime + 1000 < System.currentTimeMillis()) {
                    onClickTime = System.currentTimeMillis();
                    List<Item> list = new ArrayList<>();
                    String name = listLocation.get(position).getName();
                    for (int i = 0; i < listLocationSend.size(); i++) {
                        if (listLocationSend.get(i).getButket().equals(name)) {
                            list.add(listLocationSend.get(i));
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME_FOLDER", name);
                    bundle.putInt("TYPE", 3);
                    bundle.putSerializable("LIST_LOCATION1", (Serializable) list);

                    ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                    itemInAlbumsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                    fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
                }
            }
        });

        butketAdapter12.setOnItemClickListener(new ButketAdapter1.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (onClickTime == 0 || onClickTime + 1000 < System.currentTimeMillis()) {
                    onClickTime = System.currentTimeMillis();
                    String name = listYear.get(position).getName();
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME_FOLDER", name);
                    bundle.putInt("TYPE", 2);

                    ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                    itemInAlbumsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                    fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
                }
            }
        });

        searchAdapter.setOnItemClickListener(new SearchAdapter.IOnClickItemSearch() {
            @Override
            public void onItemClickSearch(String name) {
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", name);
                bundle.putInt("TYPE", 1);
                onTouch = 1;

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        searchAdapter1.setOnItemClickListener(new SearchAdapter.IOnClickItemSearch() {
            @Override
            public void onItemClickSearch(String name) {
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", name);
                bundle.putInt("TYPE", 2);
                onTouch = 1;

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        searchAdapter2.setOnItemClickListener(new SearchAdapter.IOnClickItemSearch() {
            @Override
            public void onItemClickSearch(String name) {
                List<Item> list = new ArrayList<>();
                for (int i = 0; i < listLocationSend.size(); i++) {
                    if (listLocationSend.get(i).getButket().equals(name)) {
                        list.add(listLocationSend.get(i));
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", name);
                bundle.putInt("TYPE", 3);
                bundle.putSerializable("LIST_LOCATION1", (Serializable) list);
                onTouch = 1;

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainerSearch, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        return view;
    }

    private void checkSizeRcvSuggestion(int size) {
        if (size == 0 || size == listSearch.size()) {
            layoutSuggestions.setVisibility(View.GONE);
            if (size == 0) {
                layoutNoResult.setVisibility(View.VISIBLE);
                layoutResult.setVisibility(View.GONE);
                txtNoResult.setText(getString(R.string.no_results_1) + " '" + srcView.getQuery() + "'" + getString(R.string.no_results_2));
            }

        } else if (0 < size && size < listSearch.size()) {
            layoutNoResult.setVisibility(View.GONE);
            layoutResult.setVisibility(View.VISIBLE);
            layoutSuggestions.setVisibility(View.VISIBLE);
            txtCountSuggesstion.setText(getString(R.string.photo_and_video) + " (" + size + ")");
        }
    }

//    private void checkSizeSuggestion(String newText) {
//        if (suggestionsAdapter.getSize()==0){
//            layoutNoResult.setVisibility(View.VISIBLE);
//            layoutResult.setVisibility(View.GONE);
//            txtNoResult.setText(getString(R.string.no_results)+getString(R.string.no_results_1)+" '"+newText+"'"+getString(R.string.no_results_2));
//        }else {
//            layoutNoResult.setVisibility(View.GONE);
//            layoutResult.setVisibility(View.VISIBLE);
//        }
//    }

    private void addControls(View view) throws IOException {
        txtCloseSearch = view.findViewById(R.id.txtCloseSearch);
        txtSearch = view.findViewById(R.id.txtSearch);
        srcView = view.findViewById(R.id.srcView);
        rcvAlbumSearch = view.findViewById(R.id.rcvAlbumSearch);
        rcvPlaceSearch = view.findViewById(R.id.rcvPlaceSearch);
        rcvYearSearch = view.findViewById(R.id.rcvYearSearch);
        txtAlbum = view.findViewById(R.id.txtAlbum);
        nesView = view.findViewById(R.id.nesView);
        nesView1 = view.findViewById(R.id.nesView1);
        layoutSuggestions = view.findViewById(R.id.layoutSuggestions);
        rcvSuggestions = view.findViewById(R.id.rcvSuggestions);
        txtSeeAll = view.findViewById(R.id.txtSeeAll);
        txtCountSuggesstion = view.findViewById(R.id.txtCountSuggesstion);
        txtNoResult = view.findViewById(R.id.txtNoResult);

        layoutProgressBar = view.findViewById(R.id.layoutProgressBar);
        layoutTest1 = view.findViewById(R.id.layoutTest1);

        txtColapse1 = view.findViewById(R.id.txtCollapse1);
        txtColapse2 = view.findViewById(R.id.txtCollapse2);
        txtColapse3 = view.findViewById(R.id.txtCollapse3);
        txtSeeAll1 = view.findViewById(R.id.txtSeeAll1);
        txtSeeAll2 = view.findViewById(R.id.txtSeeAll2);
        txtSeeAll3 = view.findViewById(R.id.txtSeeAll3);

        layoutAlbumSeaarch = view.findViewById(R.id.layoutAlbumSearch);
        layoutMonthSearch = view.findViewById(R.id.layoutMonthSearch);
        layoutPlaceSearch = view.findViewById(R.id.layoutPlaceSearch);

        layoutAlbumSeaarch1 = view.findViewById(R.id.layoutAlbumSearch1);
        layoutMonthSearch1 = view.findViewById(R.id.layoutMonthSearch1);
        layoutPlaceSearch1 = view.findViewById(R.id.layoutPlaceSearch1);

        txtClear = view.findViewById(R.id.txtClear);
        rcvRecentlySearch = view.findViewById(R.id.rcvRecentlySearch);
        layoutRecentlySearch = view.findViewById(R.id.layoutRecentlySearch);
        layoutRecently = view.findViewById(R.id.layoutRecently);
        layoutResult = view.findViewById(R.id.layoutResult);
        layoutNoResult = view.findViewById(R.id.layoutNoResult);

        rcvAlbumSearch1 = view.findViewById(R.id.rcvAlbumSearch1);
        rcvPlaceSearch1 = view.findViewById(R.id.rcvPlaceSearch1);
        rcvYearSearch1 = view.findViewById(R.id.rcvYearSearch1);

        database = new Database(getActivity());

//        database.queryData("CREATE TABLE IF NOT EXISTS Search(Id INTEGER PRIMARY KEY AUTOINCREMENT, Text NVARCHAR(200))");
        listRecentlySearch = getListRecently();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvRecentlySearch.setLayoutManager(layoutManager);
        recentlySearchAdapter = new RecentlySearchAdapter(getContext());
        recentlySearchAdapter.setData(listRecentlySearch);
        rcvRecentlySearch.setAdapter(recentlySearchAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(rcvRecentlySearch.getContext(),
                layoutManager.getOrientation());
        rcvRecentlySearch.addItemDecoration(divider);

//        getImageButket(getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rcvAlbumSearch1.setLayoutManager(llm);
        butketAdapter1 = new ButketAdapter1(getContext());
        butketAdapter1.setData((ArrayList<Butket>) listButket);
        rcvAlbumSearch1.setAdapter(butketAdapter1);

        LinearLayoutManager llm1 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rcvPlaceSearch1.setLayoutManager(llm1);
        butketAdapter11 = new ButketAdapter1(getContext());
        butketAdapter11.setData((ArrayList<Butket>) listLocation);
        rcvPlaceSearch1.setAdapter(butketAdapter11);

        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rcvYearSearch1.setLayoutManager(llm2);
        butketAdapter12 = new ButketAdapter1(getContext());
        butketAdapter12.setData((ArrayList<Butket>) listYear);
        rcvYearSearch1.setAdapter(butketAdapter12);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvAlbumSearch.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter(getActivity());
        searchAdapter.setData(listButket);
        rcvAlbumSearch.setAdapter(searchAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvAlbumSearch.getContext(),
                linearLayoutManager.getOrientation());
        rcvAlbumSearch.addItemDecoration(dividerItemDecoration);


        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvYearSearch.setLayoutManager(linearLayoutManager1);
        searchAdapter1 = new SearchAdapter(getActivity());
        searchAdapter1.setData(listYear);
        rcvYearSearch.setAdapter(searchAdapter1);
        DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(rcvYearSearch.getContext(),
                linearLayoutManager1.getOrientation());
        rcvYearSearch.addItemDecoration(dividerItemDecoration1);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvPlaceSearch.setLayoutManager(linearLayoutManager2);
        searchAdapter2 = new SearchAdapter(getActivity());
        searchAdapter2.setData(listLocation);
        rcvPlaceSearch.setAdapter(searchAdapter2);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(rcvPlaceSearch.getContext(),
                linearLayoutManager2.getOrientation());
        rcvPlaceSearch.addItemDecoration(dividerItemDecoration2);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        rcvSuggestions.setLayoutManager(gridLayoutManager);
        suggestionsAdapter = new SuggestionsSearchAdapter(getContext());
        suggestionsAdapter.setData(listSearch);
        rcvSuggestions.setAdapter(suggestionsAdapter);

//        checkSizeAll();

        MyTask2 myTask2 = new MyTask2();
        myTask2.execute();

        cursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{"name", "address"},
                new int[]{android.R.layout.simple_list_item_1},
                0);
        srcView.setSuggestionsAdapter(cursorAdapter);

//        ImageView imageView = srcView.findViewById(R.id.search_close_btn);
//        imageView.setVisibility(View.GONE);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.size_icon_delete_text));
    }

    private void checkSizeAll() {
        if (listButket.size() < 4) {
            txtSeeAll1.setVisibility(View.GONE);
        }
        if (listYear.size() < 4) {
            txtSeeAll3.setVisibility(View.GONE);
        }
        if (listLocation.size() < 4) {
            txtSeeAll2.setVisibility(View.GONE);
        }

        checkSizeRcvAlbum(searchAdapter.getSize());
        checkSizeRcvPlace(searchAdapter2.getSize());
        checkSizeRcvMonth(searchAdapter1.getSize());

        checkSizeRcvAlbum1(butketAdapter1.getListSearch());
        checkSizeRcvPlace1(butketAdapter11.getListSearch());
        checkSizeRcvMonth1(butketAdapter12.getListSearch());
        checkSizeRcvRecentlySearch(listRecentlySearch.size());
    }

    private void checkSizeRcvRecentlySearch(int size) {
        if (size == 0) {
            layoutRecently.setVisibility(View.GONE);
        } else {
            layoutRecently.setVisibility(View.VISIBLE);
        }
    }

    private void checkSizeRcvAlbum1(int listSearch) {
        if (listSearch == 0) {
            layoutAlbumSeaarch1.setVisibility(View.GONE);
        } else {
            layoutAlbumSeaarch1.setVisibility(View.VISIBLE);
        }
    }

    private void checkSizeRcvPlace1(int listSearch) {
        if (listSearch == 0) {
            layoutPlaceSearch1.setVisibility(View.GONE);
        } else {
            layoutPlaceSearch1.setVisibility(View.VISIBLE);
        }
    }

    private void checkSizeRcvMonth1(int listSearch) {
        if (listSearch == 0) {
            layoutMonthSearch1.setVisibility(View.GONE);
        } else {
            layoutMonthSearch1.setVisibility(View.VISIBLE);
        }
    }

    private List<String> getListRecently() {
        Cursor data = database.getData("SELECT * FROM Search");
        List<String> list = new ArrayList<>();
        if (data != null) {
            while (data.moveToNext()) {
                String path = data.getString(1);
                list.add(path);
            }
        }
        Collections.reverse(list);
        return list;
    }

    private void getImageButket(Context context) throws IOException {
        ArrayList<Butket> butkets = new ArrayList<>();
        ArrayList<Butket> butkets1 = new ArrayList<>();
        ArrayList<Butket> butkets2 = new ArrayList<>();
        List<Item> listLocationLol = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.MediaColumns.DATE_TAKEN, MediaStore.Images.Media._ID};

        Uri uri1 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA, MediaStore.MediaColumns.DATE_TAKEN, MediaStore.Images.Media._ID, MediaStore.Images.Media.DURATION};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_TAKEN);
        ArrayList<String> listPath = new ArrayList<>();
        ArrayList<String> year = new ArrayList<>();
        ArrayList<String> location = new ArrayList<>();
        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                @SuppressLint("Range") String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                @SuppressLint("Range") long dateAdded = cursor.getLong(cursor.getColumnIndex(projection[2]));
                String date = String.valueOf(new SimpleDateFormat("MM/yyyy").format(dateAdded));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection[3]));
                file = new File(fisrtImage);
                //load data list album
                if (file.exists()) {
                    listSearch.add(new Butket(1,bucketPath, fisrtImage, 0, dateAdded, 0, id));
                    if (listPath.contains(bucketPath)) {
                        for (int i = 0; i < butkets.size(); i++) {
                            if (butkets.get(i).getName() != null && butkets.get(i).getName().equals(bucketPath)) {
                                int a = butkets.get(i).getTotalItem();
                                butkets.remove(i);
                                butkets.add(new Butket(1,bucketPath, fisrtImage, a + 1, dateAdded, 0, id));
                                break;
                            }
                        }
                    } else {
                        butkets.add(new Butket(1,bucketPath, fisrtImage, 1, dateAdded, 0, id));
                        listPath.add(bucketPath);
                    }
                }

                //load data list year
                if (file.exists()) {
                    listSearch.add(new Butket(1,date, fisrtImage, 0, dateAdded, 0, id));
                    if (year.contains(date)) {
                        for (int i = 0; i < butkets1.size(); i++) {
                            if (butkets1.get(i).getName() != null && butkets1.get(i).getName().equals(date)) {
                                int a = butkets1.get(i).getTotalItem();
                                butkets1.remove(i);
                                butkets1.add(new Butket(1,date, fisrtImage, a + 1, dateAdded, 0, id));
                                break;
                            }
                        }
                    } else {
                        butkets1.add(new Butket(1,date, fisrtImage, 1, dateAdded, 0, id));
                        year.add(date);
                    }
                }

                //load data list location
                if (file.exists()) {
                    ExifInterface exifInterface = new ExifInterface(file);
                    if (exifInterface.getLatLong() != null) {
//                        String locationString = String.valueOf(latLong[0])+"  "+String.valueOf(latLong[1]);
                        String locationString = getCompleteAddressString(exifInterface.getLatLong()[0], exifInterface.getLatLong()[1]);
                        listSearch.add(new Butket(1,locationString, fisrtImage, 0, dateAdded, 0, id));
                        if (location.contains(locationString)) {
                            for (int i = 0; i < butkets2.size(); i++) {
                                if (butkets2.get(i).getName() != null && butkets2.get(i).getName().equals(locationString)) {
                                    int a = butkets2.get(i).getTotalItem();
                                    butkets2.remove(i);
                                    butkets2.add(new Butket(1,locationString, fisrtImage, a + 1, dateAdded, 0, id));
                                    break;
                                }
                            }
                        } else {
                            butkets2.add(new Butket(1,locationString, fisrtImage, 1, dateAdded, 0, id));
                            location.add(locationString);
                        }
                        listLocationLol.add(new Item(fisrtImage, dateAdded, 0, false, id, locationString));
                    }
                }

            }
            cursor.close();
        }

        Cursor cursor1 = context.getContentResolver().query(uri1, projection1, null, null, null);
        if (cursor1 != null) {
            File file;
            while (cursor1.moveToNext()) {
                @SuppressLint("Range") String bucketPath = cursor1.getString(cursor1.getColumnIndex(projection1[0]));
                @SuppressLint("Range") String fisrtImage = cursor1.getString(cursor1.getColumnIndex(projection1[1]));
                @SuppressLint("Range") long dateAdded = cursor1.getLong(cursor1.getColumnIndex(projection1[2]));
                String date = String.valueOf(new SimpleDateFormat("MM/yyyy").format(dateAdded));
                @SuppressLint("Range") String id = cursor1.getString(cursor1.getColumnIndex(projection1[3]));
                @SuppressLint("Range") long duration = cursor1.getLong(cursor1.getColumnIndex(projection1[4]));

                //load data list album
                file = new File(fisrtImage);
                if (file.exists()) {
                    listSearch.add(new Butket(1,bucketPath, fisrtImage, 0, dateAdded, 0, id));
                    if (listPath.contains(bucketPath)) {
                        for (int i = 0; i < butkets.size(); i++) {
                            if (butkets.get(i).getName() != null && butkets.get(i).getName().equals(bucketPath)) {
                                if (butkets.get(i).getDateAdded() > dateAdded) {
                                    int a = butkets.get(i).getTotalItem();
                                    butkets.get(i).setTotalItem(a + 1);
                                    break;
                                } else {
                                    int a = butkets.get(i).getTotalItem();
                                    butkets.remove(i);
                                    butkets.add(new Butket(1,bucketPath, fisrtImage, a + 1, dateAdded, duration, id));
                                    break;
                                }
                            }
                        }
                    } else {
                        butkets.add(new Butket(1,bucketPath, fisrtImage, 1, dateAdded, duration, id));
                        listPath.add(bucketPath);
                    }
                }

                //load data list year
                if (file.exists()) {
                    listSearch.add(new Butket(1,date, fisrtImage, 0, dateAdded, 0, id));
                    if (year.contains(date)) {
                        for (int i = 0; i < butkets1.size(); i++) {
                            if (butkets1.get(i).getName() != null && butkets1.get(i).getName().equals(date)) {
                                if (butkets1.get(i).getDateAdded() > dateAdded) {
                                    int a = butkets1.get(i).getTotalItem();
                                    butkets1.get(i).setTotalItem(a + 1);
                                    break;
                                } else {
                                    int a = butkets1.get(i).getTotalItem();
                                    butkets1.remove(i);
                                    butkets1.add(new Butket(1,date, fisrtImage, a + 1, dateAdded, duration, id));
                                    break;
                                }
                            }
                        }
                    } else {
                        butkets1.add(new Butket(1,date, fisrtImage, 1, dateAdded, duration, id));
                        listPath.add(date);
                    }
                }

                //load data list location
                if (file.exists()) {
                    ExifInterface exifInterface = new ExifInterface(file);
                    if (exifInterface.getLatLong() != null) {
//                        String locationString = String.valueOf(latLong[0])+"  "+String.valueOf(latLong[1]);
                        String locationString = getCompleteAddressString(exifInterface.getLatLong()[0], exifInterface.getLatLong()[1]);
                        listSearch.add(new Butket(1,locationString, fisrtImage, 0, dateAdded, 0, id));
                        if (location.contains(locationString)) {
                            for (int i = 0; i < butkets2.size(); i++) {
                                if (butkets2.get(i).getName() != null && butkets2.get(i).getName().equals(locationString)) {
                                    if (butkets2.get(i).getDateAdded() > dateAdded) {
                                        int a = butkets2.get(i).getTotalItem();
                                        butkets2.get(i).setTotalItem(a + 1);
                                        break;
                                    } else {
                                        int a = butkets2.get(i).getTotalItem();
                                        butkets2.remove(i);
                                        butkets2.add(new Butket(1,locationString, fisrtImage, a + 1, dateAdded, duration, id));
                                        break;
                                    }
                                }
                            }
                        } else {
                            butkets2.add(new Butket(1,locationString, fisrtImage, 1, dateAdded, duration, id));
                            listPath.add(locationString);
                        }
                        listLocationLol.add(new Item(fisrtImage, dateAdded, duration, false, id, locationString));
                    }
                }
            }
            cursor.close();
        }
        Collections.sort(butkets, new Comparator<Butket>() {
            @Override
            public int compare(Butket o1, Butket o2) {
                if (o1.getDateAdded() > o2.getDateAdded()) {
                    return -1;
                } else if (o1.getDateAdded() < o2.getDateAdded()) {
                    return 1;
                }
                return 0;
            }
        });

        Collections.sort(butkets1, new Comparator<Butket>() {
            @Override
            public int compare(Butket o1, Butket o2) {
                if (o1.getDateAdded() > o2.getDateAdded()) {
                    return -1;
                } else if (o1.getDateAdded() < o2.getDateAdded()) {
                    return 1;
                }
                return 0;
            }
        });
        listButket = butkets;
        listYear = butkets1;
        listLocation = butkets2;
        listLocationSend = listLocationLol;
    }

    public ArrayList<Butket> reverse(ArrayList<Butket> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
//                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
//                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.w("My Current loction address", "Canont get Address!");
        }
        if (strAdd != "") {
            return strAdd.substring(strAdd.indexOf(",") + 2);
        }
        return null;
    }

    public void reloaddData() {
        MyTask myTask = new MyTask();
        myTask.execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... params) {
            try {
                getImageButket(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            searchAdapter.setData((ArrayList<Butket>) listButket);
            searchAdapter1.setData((ArrayList<Butket>) listYear);
            searchAdapter2.setData((ArrayList<Butket>) listLocation);
            super.onProgressUpdate(values);
        }
    }

    public boolean onBack() {
        if (!srcView.getQuery().toString().equals("") && onTouch == 0) {
            srcView.setQuery("", false);
            txtSearch.setVisibility(View.VISIBLE);
            txtCloseSearch.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    private void checkSizeRcvAlbum(int size) {
        if (size >= 0) {
            if (size < 4) {
                if (size == 0) {
                    layoutAlbumSeaarch.setVisibility(View.GONE);
                } else {
                    layoutAlbumSeaarch.setVisibility(View.VISIBLE);
                    if (txtSeeAll1.getVisibility() == View.VISIBLE) {
                        txtSeeAll1.setVisibility(View.GONE);
                    } else {
                        txtColapse1.setVisibility(View.GONE);
                    }
                }
            } else {
                layoutAlbumSeaarch.setVisibility(View.VISIBLE);
                if (checkSeeOrCollapse1 == 1) {
                    txtColapse1.setVisibility(View.VISIBLE);
                } else if (checkSeeOrCollapse1 == 0) {
                    txtSeeAll1.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void checkSizeRcvPlace(int size) {
        if (size >= 0) {
            if (size < 4) {
                if (size == 0) {
                    layoutPlaceSearch.setVisibility(View.GONE);
                } else {
                    layoutPlaceSearch.setVisibility(View.VISIBLE);
                    if (txtSeeAll2.getVisibility() == View.VISIBLE) {
                        txtSeeAll2.setVisibility(View.GONE);
                    } else {
                        txtColapse2.setVisibility(View.GONE);
                    }
                }
            } else {
                layoutPlaceSearch.setVisibility(View.VISIBLE);
                if (checkSeeOrCollapse2 == 1) {
                    txtColapse2.setVisibility(View.VISIBLE);
                } else if (checkSeeOrCollapse2 == 0) {
                    txtSeeAll2.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void checkSizeRcvMonth(int size) {
        if (size >= 0) {
            if (size < 4) {
                if (size == 0) {
                    layoutMonthSearch.setVisibility(View.GONE);
                } else {
                    layoutMonthSearch.setVisibility(View.VISIBLE);
                    if (txtSeeAll3.getVisibility() == View.VISIBLE) {
                        txtSeeAll3.setVisibility(View.GONE);
                    } else {
                        txtColapse3.setVisibility(View.GONE);
                    }
                }
            } else {
                layoutMonthSearch.setVisibility(View.VISIBLE);
                if (checkSeeOrCollapse3 == 1) {
                    txtColapse3.setVisibility(View.VISIBLE);
                } else if (checkSeeOrCollapse3 == 0) {
                    txtSeeAll3.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void setOnTouch() {
        onTouch = 0;
    }

    public int getTouch(){
        return onTouch;
    }

    class MyTask2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutProgressBar.setVisibility(View.VISIBLE);
            layoutTest1.setVisibility(View.GONE);
//            iSendDataListener.openSelect();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            try {
                getImageButket(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            layoutProgressBar.setVisibility(View.GONE);
            layoutTest1.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            butketAdapter1.setData((ArrayList<Butket>) listButket);
            rcvAlbumSearch1.setAdapter(butketAdapter1);

            butketAdapter11.setData((ArrayList<Butket>) listLocation);
            rcvPlaceSearch1.setAdapter(butketAdapter11);

            butketAdapter12.setData((ArrayList<Butket>) listYear);
            rcvYearSearch1.setAdapter(butketAdapter12);

            searchAdapter.setData(listButket);
            rcvAlbumSearch.setAdapter(searchAdapter);

            searchAdapter1.setData(listYear);
            rcvYearSearch.setAdapter(searchAdapter1);

            searchAdapter2.setData(listLocation);
            rcvPlaceSearch.setAdapter(searchAdapter2);

            suggestionsAdapter.setData(listSearch);
            rcvSuggestions.setAdapter(suggestionsAdapter);

            checkSizeAll();
        }
    }
}
