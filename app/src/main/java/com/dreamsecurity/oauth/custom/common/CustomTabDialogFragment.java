package com.dreamsecurity.oauth.custom.common;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.dreamsecurity.oauth.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 커스텀 탭 가능한패키지를 선택하는 다이얼로그 .
 */
public class CustomTabDialogFragment extends DialogFragment {

    public static final String ARG_PACKAGE = "packages";
    public static final String DIALOG_TAG = "CUSTOM_TAB_SELECTOR";

    private List<PackageInfo> packageInfos;

    private CustomTabAppAdaptor adaptor;
    private OnPackageSelectListener packageSelectListener;

    /**
     * 패키지 정보를 담아 리스트를 생성합니다
     *
     * @param packageList 패키저 정보들의 리스트입니다
     * @return A new instance of fragment CustomTabDialogFragment.
     */
    public static CustomTabDialogFragment newInstance(List<PackageInfo> packageList) {
        CustomTabDialogFragment fragment = new CustomTabDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PACKAGE, packageList.toArray(new PackageInfo[0]));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.getParcelableArray(ARG_PACKAGE) != null) {
            List<Parcelable> parcelablePackageInfoList = Arrays.asList(arguments.getParcelableArray(ARG_PACKAGE));
            packageInfos = new ArrayList<>();
            for (Parcelable p: parcelablePackageInfoList) {
                packageInfos.add((PackageInfo) p);
            }

            adaptor = new CustomTabAppAdaptor();
            setStyle(DialogFragment.STYLE_NO_TITLE
                    , androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), getTheme())
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancel(dialog);
                    }
                })
                .setAdapter(adaptor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        packageSelectListener.onPackageSelect(packageInfos.get(which));
                    }
                }).setTitle(R.string.use_application);

        return dialog.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        packageSelectListener.onPackageSelect(null);
    }

    public OnPackageSelectListener getPackageSelectListener() {
        return packageSelectListener;
    }

    public void setPackageSelectListener(OnPackageSelectListener packageSelectListener) {
        this.packageSelectListener = packageSelectListener;
    }

    /**
            * Listener will listen when user select app or close dialog
     */
    public interface OnPackageSelectListener {
        /**
         * This method invoke when user select application or close dialog
         *
         * @param packageInfo The package info that user select, if user close dialog this parameter will null
         */
        void onPackageSelect(PackageInfo packageInfo);
    }



    /**
     * 커스텀 탭 어플리케이션 패키지 리스트의 어뎁터
     */
    private class CustomTabAppAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return packageInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return packageInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout = (LinearLayout) convertView;
            if(convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) layoutInflater.inflate(R.layout.package_list_item, parent, false);
            }

            ImageView icon = (ImageView) layout.findViewById(R.id.package_icon);
            TextView name = (TextView) layout.findViewById(R.id.package_name);

            PackageInfo item = (PackageInfo) getItem(position);
            PackageManager pm = getActivity().getPackageManager();
            icon.setImageDrawable(item.applicationInfo.loadIcon(pm));
            name.setText(item.applicationInfo.loadLabel(pm));

            return layout;
        }
    }

}
