package askartius.mobctrlsys;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import askartius.mobctrlsys.ui.HomeFragment;
import askartius.mobctrlsys.ui.MotionFragment;

public class PagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
//        fragments.add(new HomeFragment());
        fragments.add(new MotionFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position < fragments.size()) {
            return fragments.get(position);
        } else {
            return fragments.get(0);
        }
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
