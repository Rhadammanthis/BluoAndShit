package mx.triolabs.pp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import mx.triolabs.pp.R;
import mx.triolabs.pp.objects.misc.Tips;
import mx.triolabs.pp.objects.tips.Tip;

/**
 * Created by hugomedina on 12/1/16.
 */

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {

    private List<Tip> dataSet;
    private Tips.Types type;
    private Context context;

    /**
     * Hashtable to get the icon resource int
     */
    private Hashtable<String, Integer> icon;

    /**
     * Hashtable to get the color resource int
     */
    private Hashtable<String, Integer> textColor;

    DateFormat dFDate = new SimpleDateFormat("dd - MMMM - yyyy");

    /**
     * Populates the a RecyclerView with a list of Tips
     * @param tipsList The dataset of Tips
     * @param type The type of tips in the list
     * @param context The context
     */
    public TipsAdapter(List<Tip> tipsList, Tips.Types type, Context context){
        this.dataSet = tipsList;
        this.type = type;
        this.context = context;

        icon = new Hashtable<String, Integer>();
        icon.put("1", R.drawable.a_alimentacion);
        icon.put("2", R.drawable.a_salud);
        icon.put("3", R.drawable.a_ejercicios);

        textColor = new Hashtable<String, Integer>();
        textColor.put("1", R.color.bluoOrange);
        textColor.put("2", R.color.blueButtonPrimary);
        textColor.put("3", R.color.bluoGreen);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tips, parent, false);
        TipsAdapter.ViewHolder vH = new TipsAdapter.ViewHolder(view);

        return vH;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvTitle.setText(dFDate.format(Long.parseLong(dataSet.get(position).getFecha())));
        holder.tvContent.setText(dataSet.get(position).getTexto());

        holder.ivThumbNail.setImageResource(icon.get(type.toString()));
        holder.tvTitle.setTextColor(context.getResources().getColor(textColor.get(type.toString())));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent;
        ImageView ivThumbNail;

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tips_tv_title);
            tvContent = (TextView) v.findViewById(R.id.tips_tv_content);
            ivThumbNail = (ImageView) v.findViewById(R.id.tips_iv_type);
        }
    }

}
