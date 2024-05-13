package askartius.mobctrlsys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import askartius.mobctrlsys.api.EspComms;
import askartius.mobctrlsys.api.EspCommsViewModel;
import askartius.mobctrlsys.databinding.ActivityMainBinding;
import askartius.mobctrlsys.ui.PagerAdapter;

public class MainActivity extends AppCompatActivity {
    private final String ESP_IP = "192.168.3.22";
    private final int ESP_PORT = 80;
    private ActivityMainBinding binding;
    private EspComms espComms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        espComms = new EspComms(this);

        // Save espComms into ViewModel to access it from fragments
        EspCommsViewModel viewModel = new ViewModelProvider(this).get(EspCommsViewModel.class);
        viewModel.selectEspComms(espComms);

        binding.navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.terminal_screen) {
                binding.pager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.process_screen) {
                binding.pager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.motion_screen) {
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

        espComms.connectToEsp(ESP_IP, ESP_PORT);
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