package com.foodciti.foodcitipartener.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AddressAdapter;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPostCode#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPostCode extends Fragment implements AddressAdapter.AddressAdapterCAllback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView postalInfoRV;
    private EditText searchET;
    private Button search;
    private Realm realm;

    public EditPostCode() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditPostCode.
     */
    // TODO: Rename and change types and number of parameters
    public static EditPostCode newInstance() {
        EditPostCode fragment = new EditPostCode();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post_code, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View parent) {
        realm = RealmManager.getLocalInstance();
        postalInfoRV = parent.findViewById(R.id.postalInfoRV);
        postalInfoRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        AddressAdapter adapter = new AddressAdapter(getActivity(), realm.where(PostalInfo.class).findAll(), this);
        postalInfoRV.setAdapter(adapter);
        searchET = parent.findViewById(R.id.searchET);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    searchET.setTypeface(searchET.getTypeface(), Typeface.ITALIC);
                    List<PostalInfo> postalInfos = realm.where(PostalInfo.class).findAll();
                    AddressAdapter addressAdapter = (AddressAdapter) postalInfoRV.getAdapter();
                    addressAdapter.setPostalInfos(postalInfos);
                    addressAdapter.notifyDataSetChanged();
                } else {
                    String str = s.toString();
                    if (!str.equals(str.toUpperCase())) {
                        str = str.toUpperCase();
                        searchET.setText(str);
                        searchET.setSelection(searchET.length()); //fix reverse texting

                        List<PostalInfo> postalInfoList = realm.where(PostalInfo.class).equalTo("A_PostCode", searchET.getText().toString().trim()).findAll();
                        if(postalInfoList!=null) {
                            AddressAdapter addressAdapter = (AddressAdapter) postalInfoRV.getAdapter();
                            addressAdapter.setPostalInfos(postalInfoList);
                            addressAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClickAddress(int position) {
    }

    @Override
    public void onDeletePostalInfo(long id, int position) {
        Log.d("Manage postalinfo", "Position: "+position);
        AddressAdapter addressAdapter = (AddressAdapter)postalInfoRV.getAdapter();
        PostalInfo postalInfo = addressAdapter.getPostalInfos().get(position);
        CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
        customAlertDialog.setTitle("Delete PostalInfo");
        customAlertDialog.setMessage("Are you sure you want to delete this postalinfo?");
        customAlertDialog.setPositiveButton("Yes", dialog -> {
            dialog.dismiss();
            realm.executeTransaction(r->{
//                postalInfos.remove(postalInfo);
                postalInfo.deleteFromRealm();
                addressAdapter.notifyItemRemoved(position);
            });
        });
        customAlertDialog.setNegativeButton("No", dialog -> {
            dialog.dismiss();
        });

        customAlertDialog.show(getFragmentManager(), null);
    }
}
