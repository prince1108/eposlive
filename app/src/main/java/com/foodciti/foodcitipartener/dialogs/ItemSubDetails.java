package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.ItemSubAdapter;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.utils.Consts;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by quflip1 on 28-11-2017.
 */

public class ItemSubDetails extends DialogFragment implements ItemSubAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ItemSubAdapter orderAdapter;
    private List<SubItem> subItemList;


    public static ItemSubDetails newInstance(ArrayList<SubItem> subItem) {
        ItemSubDetails itemSubDetails = new ItemSubDetails();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Consts.SUB_ITEM, subItem);
        itemSubDetails.setArguments(bundle);
        return itemSubDetails;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_item_sub, container,
                false);

        subItemList = getArguments().getParcelableArrayList(Consts.SUB_ITEM);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        orderAdapter = new ItemSubAdapter(getActivity(), subItemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(orderAdapter);
        return rootView;
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
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow()
                    .setLayout((int) (getScreenWidth(getActivity()) * .35), (int) (getScreenWidth(getActivity()) * .5));
        }
    }

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }


    @Override
    public void onItemClick(SubItem item) {

    }
}
