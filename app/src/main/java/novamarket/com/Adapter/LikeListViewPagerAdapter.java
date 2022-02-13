package novamarket.com.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import novamarket.com.Fragment.FragmentLikeList_market;
import novamarket.com.Fragment.FragmentLikeList_nova;
import novamarket.com.Fragment.FragmentSellList_done;
import novamarket.com.Fragment.FragmentSellList_now;

public class LikeListViewPagerAdapter extends FragmentPagerAdapter {
    public LikeListViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
          case  0:
            return FragmentLikeList_market.newInstance();

          case 1:
            return FragmentLikeList_nova.newInstance();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    //상단의 탭 레이아웃 인디케이터 쪽에 텍스트를 선언해주는곳

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case  0:
                return "중고거래";

            case 1:
                return "팀노바생활";

            default:
                return null;
        }
    }
}
