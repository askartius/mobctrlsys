package askartius.mobctrlsys.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Timer;

import askartius.mobctrlsys.R;

public class MotionFragment extends Fragment {
    private MaterialButtonToggleGroup speedMultiplierSelector;
    private MaterialButton increaseZ;
    private MaterialButton decreaseZ;
    private MaterialButton jogHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_motion, container, false);

        speedMultiplierSelector = view.findViewById(R.id.speed_multiplier_selector);
        increaseZ = view.findViewById(R.id.increase_z);
        decreaseZ = view.findViewById(R.id.decrease_z);
        jogHome = view.findViewById(R.id.jog_home);

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

            }
        });

        return view;
    }
}