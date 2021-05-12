package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.RealmConnectAddonAdapter;
import com.foodciti.foodcitipartener.adapters.RealmConnectItemAdapter;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.AddonCategory;
import com.foodciti.foodcitipartener.realm_entities.AddonItemCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ConnectDialoge extends DialogFragment {
    private SessionManager sessionManager;
    private static final String TAG = "ConnectDialoge";
    private ProgressBar progressBar;
    Realm realm;
    private Button btnSubmit;
    List<MenuItem> menuitems=new ArrayList<>();
    long parentId=-1;
    private  int counter=-1;
    CheckBox complsoryCheck;
    protected  boolean isCompolsory=false;
    RealmList<MenuItem> realmItems;
    CounterBox counterBox;

    private ConnectDialoge() {
    }

    public static ConnectDialoge newInstance(String baseUrl) {
        ConnectDialoge addonEditDialog = new ConnectDialoge();
        addonEditDialog.setCancelable(true);
        return addonEditDialog;
    }

    public static ConnectDialoge newInstance() {
        ConnectDialoge itemEditDialog = new ConnectDialoge();
        return itemEditDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_dialoge, container, false);
        realm = RealmManager.getLocalInstance();
        menuitems=new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar1);
        btnSubmit=view.findViewById(R.id.btnSubmit);
        counterBox = view.findViewById(R.id.counterBox);
        counterBox.setLowerLimit(0);
        counterBox.setVisibility(View.GONE);
        complsoryCheck=view.findViewById(R.id.complsoryCheck);
        Spinner dropdown = view.findViewById(R.id.spinner1);

        RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).notEqualTo("name", Constants.MENUCATEGORY_COMMON).findAll();
        String[] items = new String[menuCategories.size()];
        for(int i=0;i<items.length;i++){
            items[i]=menuCategories.get(i).getName();
        }
        ArrayAdapter<String> catadapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(catadapter);

        ListView itemlistView = (ListView) view.findViewById(R.id.itemlistView);
        ListView adonlistView = (ListView) view.findViewById(R.id.addonlistView);
        closeSoftKeyBoard();

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realmItems=menuCategories.get(position).menuItems;
                RealmConnectItemAdapter adapter = new RealmConnectItemAdapter(realmItems, getActivity());
                itemlistView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//
        sessionManager = new SessionManager(getActivity());
        complsoryCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCompolsory=isChecked;
                if(isCompolsory){
                    counterBox.setVisibility(View.VISIBLE);
                }else{
                    counterBox.setVisibility(View.GONE);
                }
            }
        });
        counterBox.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                counter=count;
                Toast.makeText(getActivity(),""+counter,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDecrement(int count) {
                counter=count;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem parentItem = realm.where(MenuItem.class).equalTo("id", parentId).findFirst();
                realm.executeTransaction(r -> {
                    if (parentItem != null) {
                        for(int i=0;i<menuitems.size();i++){
                            menuitems.get(i).asAddon=true;
                            menuitems.get(i).parent=parentItem;
                            if(counter>0) {
                                menuitems.get(i).parent.closeCounter=counter;
                            }
                            parentItem.flavours.add(menuitems.get(i));
                        }

                    }
                    dismiss();
                });
            }
        });


        itemlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount() ; i ++)
                {
                    View v = parent.getChildAt(i);
                    RadioButton radio = (RadioButton) v.findViewById(R.id.radio1);
                    radio.setChecked(false);
                }

                RadioButton radio = (RadioButton) view.findViewById(R.id.radio1);
                radio.setChecked(true);

                parentId=realmItems.get(position).id;
            }
        });
        RealmResults<AddonItemCategory> realmAddons = realm.where(AddonItemCategory.class).findAll();
        RealmConnectAddonAdapter adapterAddon = new RealmConnectAddonAdapter(realmAddons, getActivity());
        adonlistView.setAdapter(adapterAddon);

        adonlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                View v = parent.getChildAt(position);
                CheckBox cb = (CheckBox)v.findViewById(R.id.checkBox);
                cb.setChecked(!cb.isChecked());
                Number maxId = realm.where(MenuItem.class).max("id");
                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                MenuItem m=new MenuItem();
                m.id=nextId;
                if(cb.isChecked())
                {
                    m.name=realmAddons.get(position).getName();
                    m.color=-1;
                    m.collectionPrice=Double.parseDouble(realmAddons.get(position).getPrice());
                    m.deliveryPrice=Double.parseDouble(realmAddons.get(position).getPrice());
                    m.last_updated=new Date().getTime();
                    m.printerSetting=1;
                    m.type=Constants.ITEM_TYPE_FLAVOUR;
                    menuitems.add(m);
                }else{
                    menuitems.remove(m);
                }


            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    private void closeSoftKeyBoard() {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }
}
