package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.PrintOrderAdapter;
import com.foodciti.foodcitipartener.adapters.callbacks.ListItemTouchHelperCallback;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.Sort;

public class EditPrintOrderDialog extends DialogFragment {
    private static final String TAG = "EditFlavourDialog";

    private EditPrintOrderDialog(){}
    public static EditPrintOrderDialog newInstance() {
        Bundle args = new Bundle();
        EditPrintOrderDialog printOrderDialog = new EditPrintOrderDialog();
        printOrderDialog.setArguments(args);
        printOrderDialog.setCancelable(true);
        return printOrderDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            parentId = getArguments().getLong(ARG_PARENT_ID);
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_editprintorder_layout, container, false);

        Realm realm = RealmManager.getLocalInstance();
        view.findViewById(R.id.close).setOnClickListener(v-> dismiss());

        RecyclerView printOrderRV = view.findViewById(R.id.printOrderRV);
        printOrderRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<MenuCategory> categories = new ArrayList<>(realm.where(MenuCategory.class).sort("printOrder", Sort.ASCENDING).findAll());
        PrintOrderAdapter printOrderAdapter = new PrintOrderAdapter(getActivity(), categories);
        printOrderRV.setAdapter(printOrderAdapter);
        ListItemTouchHelperCallback listItemTouchHelperCallback = new ListItemTouchHelperCallback(printOrderAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(listItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(printOrderRV);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.25);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.98);
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }
}
