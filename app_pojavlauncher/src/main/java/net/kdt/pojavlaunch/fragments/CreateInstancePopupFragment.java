package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.kdt.mcgui.MineEditText;
import net.kdt.pojavlaunch.PojavLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modmanager.ModManager;

public class CreateInstancePopupFragment extends DialogFragment {

    private final PojavLauncherActivity activity;

    public CreateInstancePopupFragment(PojavLauncherActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_instance_popup, container, false);

        MineEditText text = view.findViewById(R.id.instance_name_input);
        Button createButton = view.findViewById(R.id.create_instance_button);
        Spinner mcVersionSelector = view.findViewById(R.id.mc_version_spinner);

        createButton.setOnClickListener(button -> {
            Editable input = text.getText();
            if (input != null) {
                ModManager.createInstance(activity, input.toString(), mcVersionSelector.getSelectedItem().toString());
                this.dismiss();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapter.addAll(Tools.getCompatibleVersions("releases"));
        mcVersionSelector.setAdapter(adapter);
        mcVersionSelector.setSelection(0);
        return view;
    }
}
