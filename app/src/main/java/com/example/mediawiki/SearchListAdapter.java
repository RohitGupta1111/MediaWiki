package com.example.mediawiki;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import static java.lang.Math.min;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewHolder> {

    private ArrayList<SearchModel> searchList;
    private Context context;
    MainActivity.RecyclerViewClickListener clickListener;

    public SearchListAdapter (Context context, ArrayList<SearchModel> searchList, MainActivity.RecyclerViewClickListener clickListener) {
        this.context = context;
        this.searchList = searchList;
        this.clickListener = clickListener;
    }


    //make the searched String bold in searched results
    private SpannableStringBuilder processTitle(String textTitle) {
        final SpannableStringBuilder sb = new SpannableStringBuilder(textTitle);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        String queryText = Constants.getQueryText().toUpperCase();
        String title = textTitle.toUpperCase();
        if(queryText!=null) {
            int i =0;
            while(i < min(queryText.length(),title.length()) ) {
                if(queryText.charAt(i) == title.charAt(i)) {
                    i++;
                } else {
                    break;
                }

            }
            sb.setSpan(bss, 0, i, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        }
        return sb;
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_item,parent,false);
        return new SearchViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchModel model = searchList.get(position);
        SpannableStringBuilder sb = processTitle(model.getTitle());
        holder.titleView.setText(sb);
        holder.descriptionView.setText(model.getDescription());
        holder.searchImageView.setImageResource(R.drawable.default_image);
        if(model.searchImageUrl != null && !model.searchImageUrl.isEmpty()) {
            Glide.with(this.context)
                    .load(model.getSearchImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.searchImageView);
        }

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView,descriptionView;
        ImageView searchImageView;
        MainActivity.RecyclerViewClickListener clickListener;
        public SearchViewHolder(@NonNull View itemView, MainActivity.RecyclerViewClickListener clickListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
            descriptionView = itemView.findViewById(R.id.description);
            searchImageView = itemView.findViewById(R.id.search_image);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v,getAdapterPosition());
        }
    }
}
