package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.api.ModResult;
import net.kdt.pojavlaunch.api.Modrinth;

import java.util.ArrayList;

public class ModsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.lmaintab_mods, container, false);

        ModAPIAdapter apiAdapter = new ModAPIAdapter();
        RecyclerView apiModsRecycler = view.findViewById(R.id.apiModsRecycler);
        apiModsRecycler.setHasFixedSize(true);
        apiModsRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        apiModsRecycler.setAdapter(apiAdapter);

        RecyclerView installedModsRecycler = view.findViewById(R.id.installedModsRecycler);
        installedModsRecycler.setHasFixedSize(true);
        installedModsRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Modrinth.addProjectsToRecycler(apiAdapter, "1.18.1", 0, "");
        return view;
    }

    public static class ModViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView icon;
        private final TextView title;
        private final TextView description;

        public ModViewHolder(View itemView) {
            super(itemView);
            TextView view = itemView.findViewById(R.id.installedModsRecycler);
            icon = itemView.findViewById(R.id.installedModIcon);
            title = itemView.findViewById(R.id.installedModTitle);
            description = itemView.findViewById(R.id.installedModDescription);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public static class ModAPIAdapter extends RecyclerView.Adapter<ModViewHolder> {

        private final ArrayList<ModResult> mods = new ArrayList<>();

        public ModAPIAdapter() {
        }

        public void addMods(Modrinth.ModrinthSearchResult result) {
            int posStart = mods.size();
            mods.addAll(result.getHits());
            this.notifyItemRangeChanged(posStart, mods.size());
        }

        @Override
        public int getItemViewType(final int position) {
            return R.layout.installed_mod_recycler_view;
        }

        @NonNull
        @Override
        public ModViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new ModViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ModViewHolder holder, int position) {
            if (mods.size() > position) {
                ModResult modResult = mods.get(position);
                holder.title.setText(modResult.getTitle());
                holder.description.setText(modResult.getDescription());

                if (!modResult.getIconUrl().isEmpty()) {
                    Picasso.get().load(modResult.getIconUrl()).placeholder(R.drawable.ic_menu_no_news).into(holder.icon);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }
}
