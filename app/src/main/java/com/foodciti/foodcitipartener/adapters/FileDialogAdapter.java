package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;

import java.io.File;

public class FileDialogAdapter extends RecyclerView.Adapter<FileDialogAdapter.FileViewHolder> {
    private static final String TAG = "FileDialogAdapter";
    private Context context;
    private File[] files;

    private boolean isFileClickable = true;
    private Callback callback;
    private static int currentFileIndex = -1;

    public File getCurrentSelection() {
        if (currentFileIndex == -1)
            return null;
        return files[currentFileIndex];
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public FileDialogAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }

    public FileDialogAdapter(Context context, File[] files, boolean isFileClickable) {
        this.context = context;
        this.files = files;
        this.isFileClickable = isFileClickable;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_view, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        final File file = files[position];
        if (file != null) {
            if (file.isDirectory())
                holder.icon.setImageResource(R.drawable.ic_folder_yellow_60dp);
            else
                holder.icon.setImageResource(R.drawable.ic_file_gray_60dp);
            holder.file.setText(file.getName());
            /*if (currentFileIndex == position)
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray_gradientStart));
            else
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent));*/
            if (!file.isDirectory() && !isFileClickable)
                holder.file.setAlpha(0.4f);
            else
                holder.file.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        if (files == null)
            return 0;
        return files.length;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView file;
        CardView cardView;

        public FileViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            file = itemView.findViewById(R.id.file);
            icon = itemView.findViewById(R.id.ic_folder);
            cardView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                File f = files[position];
//                setSelection(position);
                if (f.isDirectory())
                    callback.onClick(files[position]);
                else if (isFileClickable)
                    callback.onClick(f);
            });
        }
    }

    private void setSelection(int index) {
        int previous = currentFileIndex;
        currentFileIndex = index;
        notifyItemChanged(index);
        if (previous != -1)
            notifyItemChanged(previous);
    }

    public interface Callback {
        void onClick(File file);
    }
}
