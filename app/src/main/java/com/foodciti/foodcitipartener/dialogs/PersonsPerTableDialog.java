package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.compound_views.CounterBox;

public class PersonsPerTableDialog extends DialogFragment {

    private static final String TAG = "PersonsPerTableDialog";
    private Callback callback;
    private static OnDismisslistener onDismissListener;
    private TextView count1, count2, count3, count4, count5, count6, count7, count8, count9, submit;
    private CounterBox counterBox;

    private PersonsPerTableDialog(){}

    public static PersonsPerTableDialog getInstance() {
        PersonsPerTableDialog personsPerTableDialog = new PersonsPerTableDialog();
        personsPerTableDialog.setCancelable(true);
        return personsPerTableDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.persons_per_table, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            dismissDialog();
        });

        /*CounterBox personCounter=view.findViewById(R.id.personCounter);
        personCounter.setLowerLimit(1);
        Button submit=view.findViewById(R.id.submit);
        submit.setOnClickListener(v->{
            callback.onSubmit(personCounter.getCount());
            dismiss();
        });*/

        /*RecyclerView countRV=view.findViewById(R.id.countRV);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(), 3);
        countRV.setLayoutManager(layoutManager);
        countRV.setAdapter(new PersonPerTableAdapter(getContext(), new Integer[]{1,2,3,4,5,6,7,8,9}));
        countRV.setHasFixedSize(true);
        int pixel= (int)CommonMethods.convertDpToPx(getContext(), 2.0f);
        countRV.addItemDecoration(new GridItemDecoration(pixel));*/
        count1 = view.findViewById(R.id.count_1);
        count1.setOnClickListener(v -> {
            callback.onSubmit(1);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count2 = view.findViewById(R.id.count_2);
        count2.setOnClickListener(v -> {
            callback.onSubmit(2);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count3 = view.findViewById(R.id.count_3);
        count3.setOnClickListener(v -> {
            callback.onSubmit(3);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count4 = view.findViewById(R.id.count_4);
        count4.setOnClickListener(v -> {
            callback.onSubmit(4);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count5 = view.findViewById(R.id.count_5);
        count5.setOnClickListener(v -> {
            callback.onSubmit(5);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count6 = view.findViewById(R.id.count_6);
        count6.setOnClickListener(v -> {
            callback.onSubmit(6);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count7 = view.findViewById(R.id.count_7);
        count7.setOnClickListener(v -> {
            callback.onSubmit(7);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count8 = view.findViewById(R.id.count_8);
        count8.setOnClickListener(v -> {
            callback.onSubmit(8);
            PersonsPerTableDialog.this.dismissDialog();
        });
        count9 = view.findViewById(R.id.count_9);
        count9.setOnClickListener(v -> {
            callback.onSubmit(9);
            PersonsPerTableDialog.this.dismissDialog();
        });

        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            callback.onSubmit(counterBox.getCount());
            PersonsPerTableDialog.this.dismissDialog();
        });
        counterBox = view.findViewById(R.id.counterBox);
        counterBox.setLowerLimit(10);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            /*dialog.getWindow()
                    .setLayout((int) (getScreenWidth(getActivity()) * 0.99), (int) (getScreenWidth(getActivity()) * 0.99));*/
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.4);

            dialog.getWindow().setLayout(width, height);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            callback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PersonsPerTableDialog.Callback");
        }
    }*/

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setOnDismissListener(OnDismisslistener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void dismissDialog() {
        if (onDismissListener != null)
            onDismissListener.onDismiss();
        else
            Log.e(TAG, "-----------------dismisslistener is null");
        dismiss();
    }

    public interface Callback {
        void onSubmit(int count);
    }

    public interface OnDismisslistener {
        void onDismiss();
    }


    public class PersonPerTableAdapter extends RecyclerView.Adapter<PersonPerTableAdapter.PersonCountHolder> {
        private static final String TAG = "PersonPerTableAdapter";
        private Context context;
        private Integer[] countArray;
//    private Callback callback;

        public PersonPerTableAdapter(Context context, Integer[] countArray) {
            this.context = context;
            this.countArray = countArray;
       /* if(!(context instanceof Callback))
            throw new RuntimeException("context must implement PersonPerTableAdapter.Callback");
        callback=(Callback)context;*/
        }

        @NonNull
        @Override
        public PersonCountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_per_table_view, parent, false);
            return new PersonCountHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PersonCountHolder holder, int position) {
            holder.count.setText(countArray[position].toString());
        }

        @Override
        public int getItemCount() {
            if (countArray == null)
                return 0;
            return countArray.length;
        }

        public class PersonCountHolder extends RecyclerView.ViewHolder {
            TextView count;
            CardView cardView;

            public PersonCountHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView;
                count = itemView.findViewById(R.id.count);
                itemView.setOnClickListener(v -> {
//                callback.onSelect(countArray[getAdapterPosition()]);
                    callback.onSubmit(countArray[getAdapterPosition()]);
                    dismiss();
                });
            }
        }
    }
}