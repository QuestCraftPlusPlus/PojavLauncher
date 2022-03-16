package net.kdt.pojavlaunch.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.PopupMenu;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.prefs.PerVersionConfigDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RefreshVersionListTask extends AsyncTask<Void, Void, ArrayList<String>>
{
    private BaseLauncherActivity mActivity;
    public RefreshVersionListTask(BaseLauncherActivity activity) {
        mActivity = activity;
    }
    
    @Override
    protected ArrayList<String> doInBackground(Void[] p1) {
        return filter(mActivity.mVersionList.versions);
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        super.onPostExecute(result);
        final PopupMenu popup = new PopupMenu(mActivity, mActivity.mVersionSelector);  
        popup.getMenuInflater().inflate(R.menu.menu_versionopt, popup.getMenu());  

        if (result != null && result.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            mActivity.mVersionSelector.setAdapter(adapter);
            mActivity.mVersionSelector.setSelection(selectAt(result.toArray(new String[0]), mActivity.mProfile.selectedVersion));
        }

        PerVersionConfigDialog dialog = new PerVersionConfigDialog(this.mActivity);
        mActivity.mVersionSelector.setOnLongClickListener((v)->dialog.openConfig(mActivity.mProfile.selectedVersion));
        mActivity.mVersionSelector.setOnItemSelectedListener(new OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                    mActivity.mProfile.selectedVersion = p1.getItemAtPosition(p3).toString();

                    PojavProfile.setCurrentProfile(mActivity, mActivity.mProfile);
                    if (PojavProfile.isFileType(mActivity)) {
                        try {
                            PojavProfile.setCurrentProfile(mActivity, mActivity.mProfile.save());
                        } catch (IOException e) {
                            Tools.showError(mActivity, e);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                    // TODO: Implement this method
                }
            });
        popup.setOnMenuItemClickListener(item -> true);
    }
    
    private ArrayList<String> filter(List<JMinecraftVersionList.Version> list1) {
        ArrayList<String> output = new ArrayList<>();
        for (JMinecraftVersionList.Version value1: list1) {
            if ((value1.type.equals("release") && LauncherPreferences.PREF_VERTYPE_RELEASE) ||
                (value1.type.equals("snapshot") && LauncherPreferences.PREF_VERTYPE_SNAPSHOT) ||
                (value1.type.equals("old_alpha") && LauncherPreferences.PREF_VERTYPE_OLDALPHA) ||
                (value1.type.equals("old_beta") && LauncherPreferences.PREF_VERTYPE_OLDBETA) ||
                (value1.type.equals("modified"))) {
                output.add(value1.name);
            }
        }
        return output;
    }
    
    private int selectAt(String[] strArr, String select) {
        int count = 0;
        for(String str : strArr){
            if (str.equals(select)) {
                return count;
            }
            count++;
        }
        return -1;
	}
}
