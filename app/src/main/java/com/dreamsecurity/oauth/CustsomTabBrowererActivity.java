package com.dreamsecurity.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustsomTabBrowererActivity extends Activity {

    String url = "https://paul.kinlan.me/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_custom_tab_browser);

        ActivityDesc activityDesc = createActivityDesc(R.string.title_activity_simple_chrome_tab,
                R.string.description_activity_simple_chrome_tab,
                SimpleCustomTabActivity.class);
        activityDescList.add(activityDesc);


        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*// open a Chrome Custom Tab
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();


        // Configure the color
        builder.setToolbarColor( 0x00ff00 );

        builder.setStartAnimations(this, android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        builder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);


        customTabsIntent.launchUrl(this, Uri.parse(url));*/


    }

    private static class ActivityDesc {
        String mTitle;
        String mDescription;
        Class<? extends Activity> mActivity;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        /* package */ TextView mTitleTextView;
        /* package */ TextView mDescriptionTextView;
        /* package */ int mPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mTitleTextView = (TextView)itemView.findViewById(R.id.tvTitle);
            this.mDescriptionTextView = (TextView)itemView.findViewById(R.id.tvDescription);
        }
    }

    private static class ActivityListAdapter extends RecyclerView.Adapter<ViewHolder>
            implements View.OnClickListener{
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private List<ActivityDesc> mActivityDescs;

        public ActivityListAdapter(Context context, List<ActivityDesc> activityDescs) {
            this.mActivityDescs = activityDescs;
            this.mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public void onClick(View v) {
            int position = ((ViewHolder)v.getTag()).mPosition;
            ActivityDesc activityDesc = mActivityDescs.get(position);
            Intent intent = new Intent(mContext, activityDesc.mActivity);
            mContext.startActivity(intent);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = mLayoutInflater.inflate(R.layout.item_example_description, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            v.setOnClickListener(this);
            v.setTag(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final ActivityDesc activityDesc = mActivityDescs.get(position);
            String title = activityDesc.mTitle;
            String description = activityDesc.mDescription;

            viewHolder.mTitleTextView.setText(title);
            viewHolder.mDescriptionTextView.setText(description);
            viewHolder.mPosition = position;
        }

        @Override
        public int getItemCount() {
            return mActivityDescs.size();
        }
    }
}
