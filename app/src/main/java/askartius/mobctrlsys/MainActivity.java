package askartius.mobctrlsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.navigation.NavigationBarView;

import askartius.mobctrlsys.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    binding.pager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.navigation_graphs) {
                    binding.pager.setCurrentItem(1);
                } else if (item.getItemId() == R.id.navigation_process) {
                    binding.pager.setCurrentItem(2);
                } else if (item.getItemId() == R.id.navigation_motion) {
                    binding.pager.setCurrentItem(3);
                }
                return false;
            }
        });

        binding.pager.setAdapter(new PagerAdapter(this));
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.navigationBar.getMenu().getItem(position).setChecked(true);
            }
        });
    }
}