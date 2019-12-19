//package com.shuvenduoffline.callrecoding;
//
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.salesmanagementezerx.datamodel.AssistanceUpdate;
//
//import java.text.SimpleDateFormat;
//import java.util.List;
//
//public class RecordingsAdapter extends RecyclerView.Adapter<UpdateAdapter.MyViewHolder> {
//
//    private List<AssistanceUpdate> updates;
//
//    public RecordingsAdapter(List<AssistanceUpdate> url) {
//        this.updates = url;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.single_update
//                        , parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.updatetxt.setText(updates.get(position).getRemaks());
//        holder.action.setText("Action : " + updates.get(position).getAction());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        holder.date.setText("Date : " + dateFormat.format(updates.get(position).getDate()));
//        holder.updateby.setText("By " + updates.get(position).getUpdateby());
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return updates.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView updatetxt;
//        public TextView action;
//        public TextView date;
//        private TextView updateby;
//
//        public MyViewHolder(View view) {
//            super(view);
//            updatetxt = view.findViewById(R.id.update_txt);
//            action = view.findViewById(R.id.update_action);
//            date = view.findViewById(R.id.update_date);
//            updateby = view.findViewById(R.id.update_by);
//        }
//    }
//}
