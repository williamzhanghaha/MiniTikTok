package com.bdac.zhcyc.minititok.UI;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bdac.zhcyc.minititok.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.bdac.zhcyc.minititok.Utilities.DatabaseUtils.loadItemsFromDatabase;

public class MeFragment extends Fragment implements ThumbsAdapter.OnThumbClickListener {

    public final static String TAG = "MeFragment";

    private RecyclerView recyclerView;
    private ThumbsAdapter adapter;
    private GridLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout personalInfoLayout;
    private AlertDialog alertDialog;
    private EditText userNameEditText;
    private EditText studentIdEditText;
    private SharedPreferences sharedPreferences;
    private static final String PACKAGE_NAME = "com.bdac.zhcyc.minitiktok";
    private TextView nameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        View diagView = inflater.inflate(R.layout.input_layout, (ViewGroup) getActivity().findViewById(R.id.input_layout));

        nameTextView = view.findViewById(R.id.textView_name);
        studentIdEditText = diagView.findViewById(R.id.et_student_id);
        userNameEditText = diagView.findViewById(R.id.et_user_name);
        personalInfoLayout = view.findViewById(R.id.personal_info_layout);
        personalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.input_info);
                if (diagView.getParent() != null) {
                    ((ViewGroup)diagView.getParent()).removeView(diagView);
                }
                builder.setView(diagView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO
                        if (userNameEditText.getText().toString().length() == 0 || studentIdEditText.getText().toString().length() == 0) {
                            Toast.makeText(getContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.sp_student_id), studentIdEditText.getText().toString());
                            editor.putString(getString(R.string.sp_user_name), userNameEditText.getText().toString());
                            editor.apply();
                            Log.d(TAG, "onClick: Changed");
                            refreshName();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertDialog = builder.create();
                alertDialog.show();

                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLACK);
                nbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLACK);
                pbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

            }
        });

        recyclerView = view.findViewById(R.id.rv_thumbs);
        adapter = new ThumbsAdapter();
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThumbsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshData();

        swipeRefreshLayout = view.findViewById(R.id.srl_thumbs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 :
                                recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        adapter.setOnThumbClickListener(this);

        refreshName();
        return view;
    }

    private void refreshName() {
        Log.d(TAG, "refreshName: " + sharedPreferences.getString(getString(R.string.sp_user_name), "zhcyc"));
        nameTextView.setText(sharedPreferences.getString(getString(R.string.sp_user_name), "zhcyc"));
    }

    public void refreshData () {
        adapter.setItems(loadItemsFromDatabase());
    }

    @Override
    public void onThumbClick(Bundle bundle, View view) {
        //TODO 跳转
        Intent intent = new Intent(getActivity(), MePlayerActivity.class);
        intent.putExtras(bundle);

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), view, MePlayerActivity.VIEW_NAME_MAIN);

        ActivityCompat.startActivity(getContext(), intent, activityOptionsCompat.toBundle());
    }
}
