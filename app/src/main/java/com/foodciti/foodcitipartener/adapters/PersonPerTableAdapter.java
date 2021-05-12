package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;

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
            });
        }
    }

/*    public interface Callback {
        void onSelect(Integer no);
    }*/
}
