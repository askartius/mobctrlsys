package askartius.mobctrlsys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;
import askartius.mobctrlsys.databinding.ActivityMainBinding;
import askartius.mobctrlsys.ui.PagerAdapter;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EspComms espComms;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // Prompt user to disable battery optimisation
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (!powerManager.isIgnoringBatteryOptimizations(getPackageName()) && sharedPreferences.getBoolean("battery optimization warning", true)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.battery_optimization)
                    .setMessage(R.string.battery_optimization_warning)
                    .setPositiveButton(R.string.turn_off, (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .setNeutralButton(R.string.dont_ask_again, (dialog, which) -> sharedPreferences.edit().putBoolean("battery optimization warning", false).apply())
                    .show();
        }

        // Warn user about his responsibilities
        if (sharedPreferences.getBoolean("responsibility warning", true)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.warning)
                    .setIcon(R.drawable.ic_alert_triangle)
                    .setMessage(R.string.responsibility_warning)
                    .setPositiveButton(R.string.i_understand, (dialog, which) -> sharedPreferences.edit().putBoolean("responsibility warning", false).apply())
                    .show();
        }

        espComms = new EspComms(this);
        getPreferences(Context.MODE_PRIVATE).edit().putInt("Test", 0).apply();

        // Save espComms into ViewModel to access it from fragments
        EspCommsViewModel viewModel = new ViewModelProvider(this).get(EspCommsViewModel.class);
        viewModel.selectEspComms(espComms);

        binding.navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.terminal) {
                binding.pager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.process) {
                binding.pager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.motion) {
                binding.pager.setCurrentItem(2);
            }
            return false;
        });

        binding.pager.setAdapter(new PagerAdapter(this));
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.navigationBar.getMenu().getItem(position).setChecked(true);
            }
        });
        binding.pager.setUserInputEnabled(false);

        espComms.connectToEsp(espComms.getDefaultEspIp());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        espComms.disconnectFromEsp();
    }

    public PagerAdapter getPagerAdapter() {
        return (PagerAdapter) binding.pager.getAdapter();
    }
}