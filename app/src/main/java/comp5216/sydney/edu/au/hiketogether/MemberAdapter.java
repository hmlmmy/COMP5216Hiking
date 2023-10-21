package comp5216.sydney.edu.au.hiketogether;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<String> memberNames;

    public MemberAdapter(List<String> memberNames) {
        this.memberNames = memberNames;
    }

    public void setData(List<String> newData) {
        this.memberNames = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberName = memberNames.get(position);
        holder.memberNameTextView.setText(memberName);
    }

    @Override
    public int getItemCount() {
        return memberNames.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberNameTextView;

        public MemberViewHolder(View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.memberNameTextView);
        }
    }
}
