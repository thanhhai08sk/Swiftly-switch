package org.de_studio.recentappswitcher.faqs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;

/**
 * Created by HaiNguyen on 2/24/17.
 */

public class FaqsAdapter extends RecyclerView.Adapter<FaqsAdapter.ViewHolder> {
    String[] titles;
    String[] texts;
    Context context;

    public FaqsAdapter(String[] titles, String[] texts, Context context) {
        this.titles = titles;
        this.texts = texts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_faq, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        holder.text.setText(texts[position]);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.text.setVisibility(holder.text.isShown()? View.GONE: View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView title;
        TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}

