package com.mondaybs.monday.Buyers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.mondaybs.monday.Model.ViewPagerModel;
import com.mondaybs.monday.R;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private List<ViewPagerModel> viewPagerModelList;
    private Context context;

    public ViewPagerAdapter(List<ViewPagerModel> viewPagerModelList, Context context) {
        this.viewPagerModelList = viewPagerModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_slider_image,container,false);
        container.addView(view);

        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageResource(viewPagerModelList.get(position).getBanner());
        return view ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout)object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return viewPagerModelList.size();
    }
}
