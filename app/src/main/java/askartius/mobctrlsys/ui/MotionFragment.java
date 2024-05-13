package askartius.mobctrlsys.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;
import askartius.mobctrlsys.api.EspCommunication;

public class MotionFragment extends Fragment {
    private MaterialButtonToggleGroup speedMultiplierSelector;
    private MaterialButton zIncreaseButton;
    private MaterialButton zDecreaseButton;
    private MaterialButton jogHomeButton;
    private MaterialTextView zPositionDisplay;
    private TextInputEditText targetZPositionInput;
    private EspComms espComms;

    private int speedMultiplier = 1;
    private float zPosition = 0.0F;
    private float zChangeStep = 0.1F;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Get espComms from ViewModel
        EspCommsViewModel viewModel = new ViewModelProvider(requireActivity()).get(EspCommsViewModel.class);
        espComms = viewModel.getSelectedEspComms().getValue();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motion, container, false);

        speedMultiplierSelector = view.findViewById(R.id.speed_multiplier_selector);
        zIncreaseButton = view.findViewById(R.id.z_increase_button);
        zDecreaseButton = view.findViewById(R.id.z_decrease_button);
        jogHomeButton = view.findViewById(R.id.jog_home_button);
        zPositionDisplay = view.findViewById(R.id.z_position_display);
        targetZPositionInput = view.findViewById(R.id.target_z_position_input);

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

        zIncreaseButton.setOnClickListener(v -> {
            espComms.sendTargetPosition(zPosition + zChangeStep * speedMultiplier, speedMultiplier);
            targetZPositionInput.setText(Float.toString(zPosition + zChangeStep * speedMultiplier));
        });

        zDecreaseButton.setOnClickListener(v -> {
            espComms.sendTargetPosition(zPosition - zChangeStep * speedMultiplier, speedMultiplier);
            targetZPositionInput.setText(Float.toString(zPosition - zChangeStep * speedMultiplier));
        });

        jogHomeButton.setOnClickListener(v -> {
            espComms.sendTargetPosition(0.0F, speedMultiplier);
            targetZPositionInput.setText(Float.toString(0.0F));
        });

        // Send target position and clear focus from the input field
        targetZPositionInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                targetZPositionInput.clearFocus();

                espComms.sendTargetPosition(Float.parseFloat(Objects.requireNonNull(targetZPositionInput.getText()).toString()), speedMultiplier); // Parsing to float to remove unnecessary characters
                Toast.makeText(getActivity(), "Jogging to " + Float.parseFloat(targetZPositionInput.getText().toString()), Toast.LENGTH_SHORT).show();

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(targetZPositionInput.getWindowToken(), 0);

                return true;
            }
            return false;
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