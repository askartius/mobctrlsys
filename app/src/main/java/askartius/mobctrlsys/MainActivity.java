package askartius.mobctrlsys;

import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;
import askartius.mobctrlsys.databinding.ActivityMainBinding;
import askartius.mobctrlsys.ui.PagerAdapter;

public class MainActivity extends AppCompatActivity {
    private final String ESP_IP = "192.168.3.22";
    private ActivityMainBinding binding;
    private EspComms espComms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        startActivity(intent);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        espComms = new EspComms(this);

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

        espComms.connectToEsp(ESP_IP);
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