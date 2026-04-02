package com.example.eventlotterysystemapp.data.models;

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

    @Override
    public Fragment createFragment(int position) {
        String status = (position == 0) ? "waitlist" : (position == 1) ? "selected" : (position == 2) ? "cancelled" : "enrolled";
        // Pass the eventId and status to the fragment
        return ParticipantListFragment.newInstance(eventId, status);
    }

    @Override
    public int getItemCount() { return 4; }
}