package com.foodciti.foodcitipartener.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.SetPostcodeAdapter;
import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.gson.PostcodePattern;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.utils.RealmManager;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectPostcodes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectPostcodes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "postcodes";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<PostalData> postalData;
    private ProgressBar progressBar;

    public SelectPostcodes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectPostcodes.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectPostcodes newInstance(ArrayList<PostalData> postalDataList) {
        SelectPostcodes fragment = new SelectPostcodes();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, postalDataList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postalData = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("POSTCODES", postalData.toString());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_postcodes, container, false);

        progressBar = view.findViewById(R.id.progress);
        RecyclerView recyclerView = view.findViewById(R.id.selectPostcodesRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*Flowable.just(postalData).flatMap(postalData1 -> {
            Map<String, String> stringMap = new HashMap<>(postalData.size());
            String patternStr = "([a-zA-Z]+)([0-9]+)([a-zA-Z]+)";
            Pattern pattern = Pattern.compile(patternStr);
            for(PostalData p: postalData) {
                Matcher matcher = pattern.matcher(p.getPostcode());

                while (matcher.find()) {
                    String group1 = matcher.group(1);
                    String group2 = matcher.group(2);
                    String group3 = matcher.group(3);
                    String location = (!p.getAddress().trim().isEmpty())?p.getAddress()+","+p.getCity():p.getCity();
                    String key = group1 + group2;
                    Log.d("KEY", key);
                    stringMap.put(key, location);
                }
            }
            return Flowable.just(stringMap);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new FlowableSubscriber<Map<String, String>>() {
            List<PostcodePattern> postcodePatterns = new ArrayList<>();
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Map<String, String> stringMap) {
                for(String s: stringMap.keySet()) {
                    Log.d("KEYYYYY", s);
                    PostcodePattern pcp = new PostcodePattern();
                    pcp.setStartString(s);
                    pcp.setCity(stringMap.get(s));
                    postcodePatterns.add(pcp);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                recyclerView.setAdapter(new SetPostcodeAdapter(getActivity(), postcodePatterns));
            }
        });*/

        progressBar.setVisibility(View.VISIBLE);
        new Thread(()->{
            Map<String, String> stringMap = new HashMap<>(postalData.size());
            String patternStr = "([a-zA-Z]+)([0-9]+)([a-zA-Z]+)";
            Pattern pattern = Pattern.compile(patternStr);
            for(PostalData p: postalData) {
                Matcher matcher = pattern.matcher(p.getPostcode());

                while (matcher.find()) {
                    String group1 = matcher.group(1);
                    String group2 = matcher.group(2);
                    String group3 = matcher.group(3);
                    String location = (!p.getAddress().trim().isEmpty() && !p.getAddress().equals(p.getCity()))?p.getAddress()+","+p.getCity():p.getCity();
                    String key = group1 + group2;
                    Log.d("KEY", key);
                    stringMap.put(key, location);
                }
            }

            List<PostcodePattern> postcodePatterns = new ArrayList<>();
            for(String s: stringMap.keySet()) {
                Log.d("KEYYYYY", s);
                PostcodePattern pcp = new PostcodePattern();
                pcp.setStartString(s);
                pcp.setCity(stringMap.get(s));
                postcodePatterns.add(pcp);

                getActivity().runOnUiThread(()->{
                    recyclerView.setAdapter(new SetPostcodeAdapter(getActivity(), postcodePatterns));
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();

        CheckBox select = view.findViewById(R.id.select);
        select.setOnClickListener(v->{
            SetPostcodeAdapter setPostcodeAdapter = (SetPostcodeAdapter)recyclerView.getAdapter();
            if(setPostcodeAdapter!=null)
                setPostcodeAdapter.setAllSelected(select.isChecked());
            else {
                select.setChecked(!select.isChecked());

            }
        });

        view.findViewById(R.id.setPostcodes).setOnClickListener(v->{
            SetPostcodeAdapter setPostcodeAdapter = (SetPostcodeAdapter)recyclerView.getAdapter();
            if(!setPostcodeAdapter.getSelected().isEmpty()) {
                new Thread(()->{
                    setPostcodes(setPostcodeAdapter.getSelected());
                    new Handler(Looper.getMainLooper()).post(()->{
                        Toast.makeText(getActivity(), "Postcodes set", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            } else {
                Toast.makeText(getActivity(), "You need to select atleast one", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setPostcodes(List<PostcodePattern> patterns) {
        Realm realm = RealmManager.getLocalInstance();
        realm.executeTransaction(r->{
            r.where(PostalInfo.class).findAll().deleteAllFromRealm();
            for(PostcodePattern pcp: patterns) {
                List<PostalData> postalDataList = new ArrayList<>();
                for (PostalData pd : postalData) {
                    if(pd.getPostcode().startsWith(pcp.getStartString()))
                        postalDataList.add(pd);
                }
                insertPostcode(postalDataList);
            }
        });
    }

    private void insertPostcode(List<PostalData> postalData) {
        Realm realm = RealmManager.getLocalInstance();
        for (PostalData p : postalData) {
//                    realm.copyToRealm(p);
            Number maxId = realm.where(PostalInfo.class).max("id");
            // If there are no rows, currentId is null, so the next id must be 1
            // If currentId is not null, increment it by 1
            long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            // User object created with the new Primary key
            PostalInfo postalInfo = realm.createObject(PostalInfo.class, nextId);
            postalInfo.setA_PostCode(p.getPostcode());
            postalInfo.setAddress(p.getAddress());
            postalInfo.setCity(p.getCity());
        }
    }
}
