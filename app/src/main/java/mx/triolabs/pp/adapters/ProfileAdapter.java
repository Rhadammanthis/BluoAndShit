package mx.triolabs.pp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.List;

import mx.triolabs.pp.R;
import mx.triolabs.pp.objects.misc.Tips;
import mx.triolabs.pp.objects.patient.response.Medicamento;
import mx.triolabs.pp.objects.patient.response.Padecimiento;

/**
 * Created by hugomedina on 12/1/16.
 */

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ILLNESSES = 0;
    public static final int MEDS = 1;

    private List<Padecimiento> dataSet;
    private int type;
    private View.OnClickListener onClickListener;

    /**
     * Creates a default adapter to populate the historic data lists
     * @param padecimientoList The data to list
     * @param type Which list should be showed. Refer to final int's in ProfileAdapter
     * @param onClickListener This is fired when clicking on the Stats image view
     */
    public ProfileAdapter(List<Padecimiento> padecimientoList, int type, View.OnClickListener onClickListener){

        this.dataSet = padecimientoList;
        this.type = type;
        this.onClickListener = onClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType){
            case ILLNESSES:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_illnesses, parent, false);

                return new IllnessesViewHolder(view);
            case MEDS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_meds, parent, false);

                return new MedsViewHolder(view);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (type){
            case ILLNESSES:

                ((IllnessesViewHolder)holder).tvName.setText(dataSet.get(position).getNombre());
                ((IllnessesViewHolder)holder).tvRanges.setText(dataSet.get(position).getRango());

                ((IllnessesViewHolder)holder).ivStats.setOnClickListener(onClickListener);

                break;
            case MEDS:

                ((MedsViewHolder)holder).tvName.setText(dataSet.get(position).getMedicamentos().get(0).getNombre());
                ((MedsViewHolder)holder).tvDosage.setText(dataSet.get(position).getMedicamentos().get(0).getDosis());
                ((MedsViewHolder)holder).tvSymptom.setText(dataSet.get(position).getNombre());

                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public int getItemCount() {

        if(type == ILLNESSES)
            return dataSet.size();
        else {
            int items = 0;
            for(Padecimiento tempIll : dataSet){
                for(Medicamento tempMed : tempIll.getMedicamentos())
                    items++;
            }

            return items;
        }
    }

    class IllnessesViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvRanges;
        ImageView ivStats;

        public IllnessesViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.illnesses_tv_name);
            tvRanges = (TextView) v.findViewById(R.id.illnesses_tv_ranges);
            ivStats = (ImageView) v.findViewById(R.id.illnesses_iv_stats);
        }
    }

    class MedsViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvSymptom, tvDosage;

        public MedsViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.meds_tv_name);
            tvSymptom = (TextView) v.findViewById(R.id.meds_tv_symptom);
            tvDosage = (TextView) v.findViewById(R.id.meds_tv_dosage);
        }
    }

}
