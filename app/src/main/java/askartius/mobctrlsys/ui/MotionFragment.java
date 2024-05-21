package askartius.mobctrlsys.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
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

import java.util.Locale;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;

public class MotionFragment extends Fragment {
    private MaterialTextView zCoordinateDisplay;
    private EspComms espComms;

    private int speedMultiplier = 1;
    private float zCoordinate = 0.0F;
    private final float zStep = 0.025F;

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
        zCoordinateDisplay = view.findViewById(R.id.z_coordinate_display);

        // Update displayed values if they have been changed before the view was created
        updateCoordinates(zCoordinate);

        // Select default speed multiplier
        speedMultiplierSelector.check(R.id.speed_x1);

        speedMultiplierSelector.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.speed_x1) {
                    speedMultiplier = 1;
                } else if (checkedId == R.id.speed_x5) {
                    speedMultiplier = 5;
                } else if (checkedId == R.id.speed_x25) {
                    speedMultiplier = 25;
                }
            }
        });

        increaseZButton.setOnClickListener(v ->
                espComms.sendTargetCoordinates(zCoordinate + zStep, speedMultiplier)
        );

        increaseZButton.setOnLongClickListener(v -> {
            espComms.sendTargetCoordinates(zCoordinate + zStep * 10, speedMultiplier); // Long click - x10 movement
            return true;
        });

        decreaseZButton.setOnClickListener(v ->
                espComms.sendTargetCoordinates(zCoordinate - zStep, speedMultiplier)
        );

        decreaseZButton.setOnLongClickListener(v -> {
            espComms.sendTargetCoordinates(zCoordinate - zStep * 10, speedMultiplier); // Long click - x10 movement
            return true;
        });

        jogHomeButton.setOnClickListener(v ->
                espComms.sendTargetCoordinates(0.0F, speedMultiplier)
        );

        jogToButton.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText(getString(R.string.unit_millimeter));
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.jog_to)
                    .setView(dialogView)
                    .setPositiveButton(R.string.jog, (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), getString(R.string.error_empty_value), Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendTargetCoordinates(Float.parseFloat(String.valueOf(dataInput.getText())), speedMultiplier); // Parsing to float to remove unnecessary characters
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
        });

        // Duplicate of the "Jog to" button functionality, for now
        zCoordinateDisplay.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_data_input, null);
            TextInputLayout dataInputLayout = dialogView.findViewById(R.id.data_input_layout);
            TextInputEditText dataInput = dialogView.findViewById(R.id.data_input);

            dataInputLayout.setSuffixText(getString(R.string.unit_millimeter));
            dataInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.jog_to)
                    .setView(dialogView)
                    .setPositiveButton(R.string.jog, (dialog, which) -> {
                        if (String.valueOf(dataInput.getText()).isEmpty()) {
                            Toast.makeText(getActivity(), getString(R.string.error_empty_value), Toast.LENGTH_SHORT).show();
                        } else {
                            espComms.sendTargetCoordinates(Float.parseFloat(String.valueOf(dataInput.getText())), speedMultiplier); // Parsing to float to remove unnecessary characters
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
        });

        // Reset coordinate
        zCoordinateDisplay.setOnLongClickListener(v -> {
            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.reset_the_coordinate)
                    .setPositiveButton(R.string.yes, (dialog, which) -> espComms.resetCoordinate())
                    .setNegativeButton(R.string.no, (dialog, which) -> {
                    })
                    .show();
            return true;
        });

        return view;
    }

    public void updateCoordinates(float zCoordinate) {
        this.zCoordinate = zCoordinate;
        if (zCoordinateDisplay != null) {
            zCoordinateDisplay.setText(String.format(Locale.getDefault(), "%.3f", zCoordinate));
        }
    }
}