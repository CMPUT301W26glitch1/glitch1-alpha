package com.example.eventlotterysystemapp.data.models;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventlotterysystemapp.ui.organizer.ParticipantListFragment;

/**
 * Adapter used to display seperate tabs for different types of participants in EventParticipantsActivity
 */
public class ParticipantPagerAdapter extends FragmentStateAdapter {
    private final String eventId;

    public ParticipantPagerAdapter(FragmentActivity fa, String eventId) {
        super(fa);
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return ParticipantListFragment.newInstance(eventId, "waitlist");
            case 1: return ParticipantListFragment.newInstance(eventId, "selected");
            case 2: return ParticipantListFragment.newInstance(eventId, "cancelled");
            case 3: return ParticipantListFragment.newInstance(eventId, "enrolled");
            case 4: return ParticipantListFragment.newInstance(eventId, "co-organizer");
            default: return ParticipantListFragment.newInstance(eventId, "waitlist");
        }
    }

    @Override
    public int getItemCount() { return 5; }
}