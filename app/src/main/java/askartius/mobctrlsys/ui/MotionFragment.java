package askartius.mobctrlsys.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private MaterialButton increaseZ;
    private MaterialButton decreaseZ;
    private MaterialButton jogHome;
    private TextInputEditText targetZPositionInput;
    private EspCommunication espCommunication;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_motion, container, false);

        speedMultiplierSelector = view.findViewById(R.id.speed_multiplier_selector);
        increaseZ = view.findViewById(R.id.increase_z);
        decreaseZ = view.findViewById(R.id.decrease_z);
        jogHome = view.findViewById(R.id.jog_home);
        targetZPositionInput = view.findViewById(R.id.target_z_position_input);

        // Select default speed multiplier
        speedMultiplierSelector.check(R.id.speed_x1);

        increaseZ.setOnTouchListener(new View.OnTouchListener() {

            private Handler handler = new Handler();
            private Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("MOTION", "Down");
                    handler.postDelayed(this, 100);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.postDelayed(runnable, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(runnable);
                }

                return true;
            }
        });

        jogHome.setOnClickListener(new View.OnClickListener() {
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
                    espCommunication.sendData("G0 Z0");
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