package askartius.mobctrlsys.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class MotionFragment extends Fragment {
    private MaterialTextView zPositionDisplay;
    private EspComms espComms;

    private int speedMultiplier = 1;
    private float zPosition = 0.0F;
    private final float zStep = 0.1F;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Get espComms from ViewModel
        EspCommsViewModel viewModel = new ViewModelProvider(requireActivity()).get(EspCommsViewModel.class);
        espComms = viewModel.getSelectedEspComms().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motion, container, false);

        MaterialButtonToggleGroup speedMultiplierSelector = view.findViewById(R.id.speed_multiplier_selector);
        MaterialButton increaseZButton = view.findViewById(R.id.increase_z_button);
        MaterialButton decreaseZButton = view.findViewById(R.id.decrease_z_button);
        MaterialButton jogHomeButton = view.findViewById(R.id.jog_home_button);
        MaterialButton jogToButton = view.findViewById(R.id.jog_to_button);
        zPositionDisplay = view.findViewById(R.id.z_position_display);

        // Update displayed values if they have been changed before the view was created
        updatePosition(zPosition);

        // Select default speed multiplier
        speedMultiplierSelector.check(R.id.speed_x1);

        speedMultiplierSelector.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.speed_x1) {
                    speedMultiplier = 1;
                } else if (checkedId == R.id.speed_x10) {
                    speedMultiplier = 10;
                } else if (checkedId == R.id.speed_x100) {
                    speedMultiplier = 100;
                }
            }
        });

        increaseZButton.setOnClickListener(v -> espComms.sendTargetPosition(zPosition + zStep * speedMultiplier, speedMultiplier));

        decreaseZButton.setOnClickListener(v -> espComms.sendTargetPosition(zPosition - zStep * speedMultiplier, speedMultiplier));

        jogHomeButton.setOnClickListener(v -> espComms.sendTargetPosition(0.0F, speedMultiplier));

        jogToButton.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setPlaceholderText(String.format("%s", zPosition)); // Display current value as a reference

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Target position")
                    .setView(dialogView)
                    .setPositiveButton("Jog", (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), "Error: empty value", Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendTargetPosition(Float.parseFloat(String.valueOf(dataInput.getText())), speedMultiplier); // Parsing to float to remove unnecessary characters
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .show();
        });

        return view;
    }

    public void updatePosition(float zPosition) {
        this.zPosition = zPosition;
        if (zPositionDisplay != null) {
            zPositionDisplay.setText(String.format("%s", zPosition));
        }
    }
}