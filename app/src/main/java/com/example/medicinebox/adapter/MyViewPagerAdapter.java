package com.example.medicinebox.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.medicinebox.Users;
import com.example.medicinebox.fragments.AllUserFragment;
import com.example.medicinebox.fragments.BlockedUserFragment;
import com.google.firebase.firestore.auth.User;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull Users fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
            default:
                return new AllUserFragment();
            case 1:
                return new BlockedUserFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
