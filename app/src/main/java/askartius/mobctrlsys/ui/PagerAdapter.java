package askartius.mobctrlsys.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragments.add(new TerminalFragment());
        fragments.add(new ProcessFragment());
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

    public List<Fragment> getFragments() {
        return fragments;
    }
}
