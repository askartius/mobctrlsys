package askartius.mobctrlsys.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButtonToggleGroup;

import askartius.mobctrlsys.R;

public class MotionFragment extends Fragment {
    private MaterialButtonToggleGroup speedMultiplierSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_motion, container, false);

        speedMultiplierSelector = view.findViewById(R.id.speed_multiplier_selector);
        speedMultiplierSelector.check(R.id.speed_x1);

        return view;
    }
}