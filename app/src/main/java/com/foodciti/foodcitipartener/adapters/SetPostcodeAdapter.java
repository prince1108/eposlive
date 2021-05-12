package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.gson.PostcodePattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetPostcodeAdapter extends RecyclerView.Adapter<SetPostcodeAdapter.Holder> {
    private Activity activity;
    private List<PostcodePattern> postcodePatterns;

    public SetPostcodeAdapter(Activity activity, List<PostcodePattern> postcodePatterns) {
        this.activity = activity;
        this.postcodePatterns = postcodePatterns;
        sortCollection(this.postcodePatterns);
    }

    public List<PostcodePattern> getPostcodePatterns() {
        return postcodePatterns;
    }

    public void setPostcodePatterns(List<PostcodePattern> postcodePatterns) {
        this.postcodePatterns = postcodePatterns;
        sortCollection(this.postcodePatterns);
    }

    public void setAllSelected(boolean selectAll) {
        if(postcodePatterns==null)
            return;
        for(PostcodePattern p: postcodePatterns) {
            p.setSelected(selectAll);
        }
        notifyDataSetChanged();
    }

    public List<PostcodePattern> getSelected() {
        List<PostcodePattern> list = new ArrayList<>();
        for(PostcodePattern p: postcodePatterns) {
            if(p.isSelected())
                list.add(p);
        }
        return list;
    }

    private void sortCollection(List<PostcodePattern> postcodePatterns) {
        Collections.sort(postcodePatterns, (o1, o2) -> {
            String str1 = o1.getStartString().substring(2);
            String str2 = o2.getStartString().substring(2);
            Integer i1 = Integer.parseInt(str1);
            Integer i2 = Integer.parseInt(str2);
            return i1.compareTo(i2);
        });
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_setpostcode_view, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final PostcodePattern postcodePattern = postcodePatterns.get(position);
        holder.postcode_pattern.setText(postcodePattern.getStartString());
        holder.city.setText(postcodePattern.getCity());
        holder.select.setChecked(postcodePattern.isSelected());
    }

    @Override
    public int getItemCount() {
        if(postcodePatterns==null)
            return 0;
        return postcodePatterns.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView postcode_pattern, city;
        CheckBox select;
        public Holder(View itemView) {
            super(itemView);
            postcode_pattern = itemView.findViewById(R.id.postcode_pattern);
            city = itemView.findViewById(R.id.city);
            select = itemView.findViewById(R.id.select);
            select.setOnClickListener(v->{
                final int pos = getAdapterPosition();
                final PostcodePattern postcodePattern = postcodePatterns.get(pos);
                Log.d("CHECKED", select.isChecked()+"");
                postcodePattern.setSelected(select.isChecked());
                notifyItemChanged(pos);
            });
        }
    }
}
