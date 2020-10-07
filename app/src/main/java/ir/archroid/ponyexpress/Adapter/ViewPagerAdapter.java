package ir.archroid.ponyexpress.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import ir.archroid.ponyexpress.Fragment.ChatsFragment;
import ir.archroid.ponyexpress.Fragment.FriendsFragment;
import ir.archroid.ponyexpress.Fragment.RequestsFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
//            case 0:
//                RequestsFragment requestsFragment = new RequestsFragment();
//                return requestsFragment;
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
//            case 0:
//                return "REQUESTS";
            case 0:
                return "CHATS";
            case 1:
                return "FRIENDS";
            default:
                return "";
        }
    }
}
