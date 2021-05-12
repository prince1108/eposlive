/*
package com.foodciti.foodcitipartener.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.DriverListAdapter;
import com.foodciti.foodcitipartener.adapters.OrderhistoryListAdapter;
import com.foodciti.foodcitipartener.dialogs.OfferMessageDialog;
import com.foodciti.foodcitipartener.dialogs.OrderHistoryDetailsDialog;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SmsSender;

import io.realm.Realm;
import io.realm.RealmResults;

*/
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActiveOrdersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActiveOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 *//*

public class ActiveOrdersFragment extends Fragment implements OfferMessageDialog.smsSend,
        SmsSender.MessageStatusListener, OrderhistoryListAdapter.OnItemClickListener, DriverListAdapter.OnClickListener {
    private static final String TAG = "ActiveOrdersFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView closebtn;
    public static RealmResults<Order> orderHistoryList;
    private RecyclerView orderHistoryRV, driverViewRV;
    private OrderhistoryListAdapter mAdapter;
    private DriverListAdapter driverListAdapter;
    private Realm myRealm;
    private Order currentOrder;
    private SmsSender smsSender;

    private RealmResults<Driver> driverRealmResultsAsync = null;

    public ActiveOrdersFragment() {
        // Required empty public constructor
    }

    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActiveOrdersFragment.
     *//*

    // TODO: Rename and change types and number of parameters
    public static ActiveOrdersFragment newInstance(String param1, String param2) {
        ActiveOrdersFragment fragment = new ActiveOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActiveOrdersFragment newInstance() {
        ActiveOrdersFragment fragment = new ActiveOrdersFragment();
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
        return inflater.inflate(R.layout.fragment_active_orders, container, false);
    }

    @Override
    public void onDestroy() {
//        RealmManager.closeRealmFor(getActivity());
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myRealm = RealmManager.getLocalInstance();
        smsSender = new SmsSender(getContext(), this);
        orderHistoryRV = view.findViewById(R.id.orderHistoryRV);
        orderHistoryRV.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        orderHistoryRV.setLayoutManager(mLayoutManager);
        orderHistoryRV.setItemAnimator(new DefaultItemAnimator());
        driverViewRV = view.findViewById(R.id.driverViewRV);
        driverViewRV.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        driverViewRV.setLayoutManager(lm);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myRealm.where(Order.class).count() > 0) {
            orderHistoryList = myRealm.where(Order.class).equalTo("isPaid", false).findAll();
            mAdapter = new OrderhistoryListAdapter(getActivity(), orderHistoryList, this, myRealm);
            orderHistoryRV.setAdapter(mAdapter);
        } else {
            Toast.makeText(getContext(), "No Order History", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    */
/**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void sendMessage(final long[] userId, final String textMessage) {
        if (userId[0] != -1l) {
            final CustomerInfo customerInfo = myRealm.where(CustomerInfo.class).equalTo("id", userId[0]).findFirst();
            try {
                smsSender.sendSMS(customerInfo.getPhone().trim(), textMessage);
            } catch (Exception e) {
//                Crashlytics.logException(e);
            }
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    customerInfo.setMsgDeliveryStatus("Sending...");
                }
            });
        }
    }

    @Override
    public void onMessageDelivered(String number) {
        Log.e(TAG, "------message delivered to: " + number);
    }

    @Override
    public void onClick(long driverId) {
        Log.e(TAG, "------------Driver Id: " + driverId);
        final Driver newDriver = myRealm.where(Driver.class).equalTo("id", driverId).findFirst();
        myRealm.executeTransaction(realm -> {
            if (currentOrder != null) {
                Driver previousDriver = currentOrder.getDriver();
                currentOrder.setDriver(newDriver);
                driverListAdapter.notifyItemChanged(driverListAdapter.getDrivers().indexOf(newDriver));
                if (previousDriver != null) {
                    if (previousDriver.equals(newDriver)) {
                        currentOrder.setDriver(null);
                    }
                    driverListAdapter.notifyItemChanged(driverListAdapter.getDrivers().indexOf(previousDriver));
                }


                mAdapter.notifyItemChanged(mAdapter.getOrders().indexOf(currentOrder));
            }
        });
    }

    @Override
    public void clickedItem(long orderid) {
        OrderHistoryDetailsDialog addition = OrderHistoryDetailsDialog.getInstance(orderid);
        addition.setCancelable(false);
        addition.show(getActivity().getSupportFragmentManager(), null);
    }

    @Override
    public void showDriverList(long orderId) {
        Log.e(TAG, "------------current orderId: " + orderId);
        currentOrder = myRealm.where(Order.class).equalTo("id", orderId).findFirst();
        driverRealmResultsAsync = myRealm.where(Driver.class).equalTo("isAvailable", true).and().equalTo("enabled", true).findAllAsync();
        driverRealmResultsAsync.addChangeListener(rs -> {
            Log.e(TAG, "------result count: " + rs.size());
            driverListAdapter = new DriverListAdapter(getActivity(), rs, currentOrder, ActiveOrdersFragment.this);
            driverViewRV.setAdapter(driverListAdapter);
        });
    }

    @Override
    public void messageUser(String phoneNo) {
        OfferMessageDialog addition = OfferMessageDialog.getInstance(phoneNo, this);
        addition.show(getActivity().getSupportFragmentManager(), null);
    }

    @Override
    public void onOrderDelivered(long orderId) {
        driverViewRV.setAdapter(null);
    }

}
*/
