package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.util.Log;
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
import net.kdt.pojavlaunch.api.ModData;
import net.kdt.pojavlaunch.api.ModResult;
import net.kdt.pojavlaunch.api.Modrinth;
import net.kdt.pojavlaunch.modmanager.ModManager;

import java.io.IOException;
import java.util.ArrayList;

public class ModsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.lmaintab_mods, container, false);

        InstalledModAdapter installedModAdapter = new InstalledModAdapter();
        RecyclerView installedModsRecycler = view.findViewById(R.id.installedModsRecycler);
        installedModsRecycler.setHasFixedSize(true);
        installedModsRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        installedModsRecycler.setAdapter(installedModAdapter);

        ModAPIAdapter modAPIAdapter = new ModAPIAdapter(installedModAdapter);
        RecyclerView apiModsRecycler = view.findViewById(R.id.apiModsRecycler);
        apiModsRecycler.setHasFixedSize(true);
        apiModsRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        apiModsRecycler.setAdapter(modAPIAdapter);

        Modrinth.addProjectsToRecycler(modAPIAdapter, "1.18.1", 0, "sodium");
        return view;
    }

    public static class ModViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView icon;
        private final TextView title;
        private final TextView description;
        private final TextView compatLevel;
        private String slug;
        private InstalledModAdapter adapter;

        public ModViewHolder(View itemView) {
            super(itemView);
            //TextView view = itemView.findViewById(R.id.installedModsRecycler);
            icon = itemView.findViewById(R.id.installedModIcon);
            title = itemView.findViewById(R.id.installedModTitle);
            description = itemView.findViewById(R.id.installedModDescription);
            compatLevel = itemView.findViewById(R.id.compatLevel);
        }

        public ModViewHolder(View itemView, InstalledModAdapter adapter) {
            super(itemView);
            icon = itemView.findViewById(R.id.installedModIcon);
            icon.setOnClickListener(this);
            title = itemView.findViewById(R.id.installedModTitle);
            description = itemView.findViewById(R.id.installedModDescription);
            compatLevel = itemView.findViewById(R.id.compatLevel);
            this.adapter = adapter;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        @Override
        public void onClick(View view) {
            //Stop spamming of same mod
            if (ModManager.isDownloading(slug)) {
                return;
            }

            try {
                ModManager.addMod(adapter, "test", slug, "1.18.1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ModAPIAdapter extends RecyclerView.Adapter<ModViewHolder> {

        private final ArrayList<ModResult> mods = new ArrayList<>();
        private final InstalledModAdapter adapter;

        public ModAPIAdapter(InstalledModAdapter adapter) {
            this.adapter = adapter;
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
            return new ModViewHolder(view, adapter);
        }

        @Override
        public void onBindViewHolder(@NonNull ModViewHolder holder, int position) {
            if (mods.size() > position) {
                ModResult modResult = mods.get(position);
                holder.title.setText(modResult.getTitle());
                holder.description.setText(modResult.getDescription());
                holder.compatLevel.setText(ModManager.getModCompat(modResult.getSlug()));
                holder.setSlug(modResult.getSlug());

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

    public static class InstalledModAdapter extends RecyclerView.Adapter<ModViewHolder> {

        private ArrayList<ModData> mods = new ArrayList<>();

        public void addMod(ModData modData) {
            int posStart = mods.size();
            mods.add(modData);
            this.notifyItemRangeChanged(posStart, posStart + 1);
        }

        public void refreshList() {
            mods = ModManager.listMods("test");
            this.notifyDataSetChanged();
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
                ModData modData = mods.get(position);
                holder.title.setText(modData.getName());
                holder.description.setText(modData.getFilename());

                if (!modData.getIconUrl().isEmpty()) {
                    Picasso.get().load(modData.getIconUrl()).placeholder(R.drawable.ic_menu_no_news).into(holder.icon);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }
}
