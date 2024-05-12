package askartius.mobctrlsys.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import askartius.mobctrlsys.R;
import askartius.mobctrlsys.api.EspCommunication;

public class MotionFragment extends Fragment {
    private MaterialButtonToggleGroup speedMultiplierSelector;
    private MaterialButton zIncreaseButton;
    private MaterialButton zDecreaseButton;
    private MaterialButton jogHomeButton;
    private TextInputEditText targetZPositionInput;
    private EspCommunication espCommunication;

    private int speedMultiplier = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EspCommunication) {
            espCommunication = (EspCommunication) context;
        } else {
            Log.e("API", "No EspCommunication");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motion, container, false);

        speedMultiplierSelector = view.findViewById(R.id.speed_multiplier_selector);
        zIncreaseButton = view.findViewById(R.id.z_increase_button);
        zDecreaseButton = view.findViewById(R.id.z_decrease_button);
        jogHomeButton = view.findViewById(R.id.jog_home_button);
        targetZPositionInput = view.findViewById(R.id.target_z_position_input);

        // Select default speed multiplier
        speedMultiplierSelector.check(R.id.speed_x1);

        speedMultiplierSelector.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.speed_x1) {
                        speedMultiplier = 1;
                    } else if (checkedId == R.id.speed_x10) {
                        speedMultiplier = 10;
                    } else if (checkedId == R.id.speed_x100) {
                        speedMultiplier = 100;
                    }
                }
            }
        });

        zIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add code to send data
                espCommunication.sendData("G0 Z");
            }
        });

        zDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add code to send data
                espCommunication.sendData("G0 Z");
            }
        });

        jogHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                espCommunication.sendData("G28");
            }
        });

        // Send target position and clear focus from the input field
        targetZPositionInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    targetZPositionInput.clearFocus();

                    // TODO: Add code to send data
                    espCommunication.sendData("G0 Z" + targetZPositionInput.getText());
                    Toast.makeText(getActivity(), "Jogging to " + targetZPositionInput.getText(), Toast.LENGTH_SHORT).show();

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(targetZPositionInput.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        return view;
    }
}