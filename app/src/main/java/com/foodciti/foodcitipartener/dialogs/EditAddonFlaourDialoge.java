package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AddonAdapter;
import com.foodciti.foodcitipartener.adapters.EditableAddonAdapter;
import com.foodciti.foodcitipartener.adapters.EditableMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.callbacks.MyTouchListener;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;

public class EditAddonFlaourDialoge extends DialogFragment {
    private static final String TAG = "EditFlavourDialog";

    private static final String ARG_ID = "_id";
    private static final String ARG_ORDER_TYPE = "order_type";

    private long id;
    private String orderType;
    private Realm realm;

    private EditAddonFlaourDialoge(){}

    public static EditAddonFlaourDialoge newInstance(long parentId, String orderType) {
        EditAddonFlaourDialoge editFlavourDialog = new EditAddonFlaourDialoge();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, parentId);
        args.putString(ARG_ORDER_TYPE, orderType);
        editFlavourDialog.setArguments(args);
        editFlavourDialog.setCancelable(true);
        return editFlavourDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_ID);
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_subcat, container, false);
        realm = RealmManager.getLocalInstance();
        RealmResults<Addon> parent = realm.where(Addon.class).findAll();
//        AtomicReference<Addon> atomicReference = new AtomicReference<>(addon);
        final RecyclerView editFlavourRV = view.findViewById(R.id.editFlavoursRV);
        editFlavourRV.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        editFlavourRV.setHasFixedSize(true);

        EditableAddonAdapter editableMenuItemAdapter = new EditableAddonAdapter(getActivity(), parent, false,orderType);
        editFlavourRV.setAdapter(editableMenuItemAdapter);
        view.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        editFlavourRV.addOnItemTouchListener(new MyTouchListener(getActivity(),
                editFlavourRV,
                new MyTouchListener.OnTouchActionListener() {
                    @Override
                    public void onLeftSwipe(View view, int position) {
                    }

                    @Override
                    public void onRightSwipe(View view, int position) {
                    }

                    @Override
                    public void onClick(View view, int position) {
//                        EditAddonFlaourDialoge editFlavourDialog = EditAddonFlaourDialoge.newInstance(parent.flavours.get(position).id, orderType);
//                        FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
//                        editFlavourDialog.show(fragmentManager, null);
                        RealmResults<Addon> addon = realm.where(Addon.class).findAll();
                        EditableAddonAdapter editableMenuItemAdapter = new EditableAddonAdapter(getActivity(), addon, false,orderType);
                        editFlavourRV.setAdapter(editableMenuItemAdapter);
                    }
                }));


        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.98);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.98);
            dialog.getWindow().setLayout(width, height);
        }
    }
}
