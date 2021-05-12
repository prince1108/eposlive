package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.BasicMenuActivity;
import com.foodciti.foodcitipartener.adapters.EditableAddonAdapter;
import com.foodciti.foodcitipartener.adapters.EditableMenuItemAdapter;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SessionManager;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;

public class EditFlavourDialog extends DialogFragment {
    private static final String TAG = "EditFlavourDialog";

    private static final String ARG_PARENT_ID = "parent_id";
    private static final String ARG_ORDER_TYPE = "order_type";

    private long parentId;
    private String orderType;
    private Realm realm;
    private int counterCloseSetting=-1;
    private EditFlavourDialog(){}

    public static EditFlavourDialog newInstance(long parentId, String orderType) {
        EditFlavourDialog editFlavourDialog = new EditFlavourDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_PARENT_ID, parentId);
        args.putString(ARG_ORDER_TYPE, orderType);
        editFlavourDialog.setArguments(args);
        editFlavourDialog.setCancelable(true);
        return editFlavourDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentId = getArguments().getLong(ARG_PARENT_ID);
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_subcat, container, false);
        realm = RealmManager.getLocalInstance();
        MenuItem parent = realm.where(MenuItem.class).equalTo("id", parentId).findFirst();
        AtomicReference<MenuItem> atomicReference = new AtomicReference<>(parent);

        final RecyclerView editFlavourRV = view.findViewById(R.id.editFlavoursRV);
        CounterBox closeCounterBtn= view.findViewById(R.id.counterClose);
        closeCounterBtn.setLowerLimit(0);

        editFlavourRV.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        editFlavourRV.setHasFixedSize(true);

        EditableMenuItemAdapter editableMenuItemAdapter = new EditableMenuItemAdapter(getActivity(), parent.flavours, parent, orderType);
        editFlavourRV.setAdapter(editableMenuItemAdapter);
        closeCounterBtn.setCount(editableMenuItemAdapter.getItemCount()-1);
        view.findViewById(R.id.close).setOnClickListener(v -> {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    atomicReference.get().closeCounter=counterCloseSetting;
                }
            });
            dismiss();
        });

        closeCounterBtn.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                counterCloseSetting=count;
            }

            @Override
            public void onDecrement(int count) {
                counterCloseSetting=count;
            }
        });
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
