package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.ConnectAddonItemAdapter;
import com.foodciti.foodcitipartener.realm_entities.AddonCategory;
import com.foodciti.foodcitipartener.realm_entities.AddonItemCategory;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;

public class CreateAddonDialoge extends DialogFragment {

    private static final String TAG = "CreateAddonDialoge";
    Realm realm;
    private Button btnAddonItem,btnAddItemOption;
    private EditText nameEditTxt,itemNameEditTxt,itemPriceEditTxt;
    ListView itemList;
    AtomicReference<RealmResults<AddonCategory>> addonCategories;
    long categoryId=-1;
    boolean isComaplosr=false;
    CheckBox compalsorychek;
    private CreateAddonDialoge() {
    }

    public static CreateAddonDialoge newInstance(String baseUrl) {
        CreateAddonDialoge addonEditDialog = new CreateAddonDialoge();
        addonEditDialog.setCancelable(true);
        return addonEditDialog;
    }

    public static CreateAddonDialoge newInstance() {
        CreateAddonDialoge itemEditDialog = new CreateAddonDialoge();
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
        View view = inflater.inflate(R.layout.create_addon_dialoge, container, false);
        realm = RealmManager.getLocalInstance();
        btnAddonItem=view.findViewById(R.id.btnAddItem);
        compalsorychek=view.findViewById(R.id.compalsorychek);
        btnAddItemOption=view.findViewById(R.id.btnAddItemOption);
        nameEditTxt=(EditText)view.findViewById(R.id.nameEditTxt);
        itemNameEditTxt=(EditText)view.findViewById(R.id.itemNameEditTxt);
        itemPriceEditTxt=(EditText)view.findViewById(R.id.itemPriceEditTxt);
        itemList=(ListView) view.findViewById(R.id.itemlistView);
        Spinner dropdown = view.findViewById(R.id.spinner1);
        compalsorychek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isComaplosr=isChecked;
            }
        });
         addonCategories = new AtomicReference<>(realm.where(AddonCategory.class).findAll());
        if(addonCategories.get() ==null|| addonCategories.get().size()<=0){

        }else{
            String[] items = new String[addonCategories.get().size()];
            for(int i=0;i<items.length;i++){
                items[i]= addonCategories.get().get(i).getName();
            }
            ArrayAdapter<String> catadapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(catadapter);
        }

        btnAddonItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(r -> {
                    Number maxId = realm.where(AddonCategory.class).max("id");
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    AddonCategory addonCategory=realm.createObject(AddonCategory.class, nextId);
                    addonCategory.setName(String.valueOf(nameEditTxt.getText().toString()));
                    addonCategory.setIscompalsory(isComaplosr);
                    addonCategory.setItemposition(-1);
                    nameEditTxt.setText("");
                     addonCategories.set(realm.where(AddonCategory.class).findAll());
                    if(addonCategories.get() ==null|| addonCategories.get().size()<=0){
                        return;
                    }
                    String[] items = new String[addonCategories.get().size()];
                    for(int i=0;i<items.length;i++){
                        items[i]= addonCategories.get().get(i).getName();
                    }
                    ArrayAdapter<String> catadapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                    dropdown.setAdapter(catadapter);
                });

            }
        });
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId=addonCategories.get().get(position).id;
                realm.executeTransaction(r -> {
                    if(categoryId>0){
                        List<AddonItemCategory> addonItemCategories = realm.where(AddonItemCategory.class).equalTo("categoryid", categoryId).findAll();
                        if(addonItemCategories==null||addonItemCategories.size()<=0){
                            return;
                        }

                        ConnectAddonItemAdapter adapter = new ConnectAddonItemAdapter(addonItemCategories,getActivity());
                        // Assign adapter to ListView
                        itemList.setAdapter(adapter);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAddItemOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                realm.executeTransaction(r -> {
                    Number maxId = realm.where(AddonItemCategory.class).max("id");
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    AddonItemCategory addonCategory=realm.createObject(AddonItemCategory.class, nextId);
                    addonCategory.setName(String.valueOf(itemNameEditTxt.getText().toString()));
                    addonCategory.setPrice(itemPriceEditTxt.getText().toString());
                    addonCategory.setCategoryid(categoryId);
                    itemNameEditTxt.setText("");
                    itemPriceEditTxt.setText("");
                    RealmResults<AddonItemCategory> addonItemCategories = realm.where(AddonItemCategory.class).equalTo("categoryid", categoryId).findAll();
                    if(addonItemCategories==null||addonItemCategories.size()<=0){
                        return;
                    }
                    String[] items = new String[addonItemCategories.size()];
                    for(int i=0;i<items.length;i++){
                        items[i]=addonItemCategories.get(i).getName();
                    }

                    ArrayAdapter<String> catadapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
                    itemList.setAdapter(catadapter);
                });


            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void closeSoftKeyBoard() {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }
}
