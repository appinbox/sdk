package com.appinbox.sdk.msg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appinbox.sdk.R;
import com.appinbox.sdk.svc.ApiBuilder;
import com.appinbox.sdk.util.DateUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Messages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ListActivity extends AppCompatActivity {

    private static final List<Message> ITEMS = new ArrayList<>();
    private String appId;
    private String appKey;
    private String contact;
    private SwipeRefreshLayout pullToRefresh;
    private View errorText;
    private View recyclerView;
    private SimpleItemRecyclerViewAdapter adapter;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_message_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.message_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        pullToRefresh = findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(this::loadItems);

        errorText = findViewById(R.id.error_text);
        assert errorText != null;
        recyclerView = findViewById(R.id.message_list);
        assert recyclerView != null;

        adapter = new SimpleItemRecyclerViewAdapter(this, ITEMS, mTwoPane);
        ((RecyclerView)recyclerView).setAdapter(adapter);
        loadItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        appId = preferences.getString(getString(R.string.sp_app), "");
        appKey = preferences.getString(getString(R.string.sp_key), "");
        contact = preferences.getString(getString(R.string.sp_contact), "");
    }

    private void loadItems() {
        pullToRefresh.setRefreshing(true);
        ApiBuilder.getInstance().getMessages(appId, appKey, contact).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.body() != null) {
                    setupRecyclerView(response.body());
                } else {
                    showFailed();
                }
                pullToRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                showFailed();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void showFailed() {
        errorText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void setupRecyclerView(List<Message> items) {
        ITEMS.clear();
        ITEMS.addAll(items);

//        errorText.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.GONE);

        errorText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ListActivity mParentActivity;
        private final List<Message> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message item = (Message) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(DetailFragment.MSG_KEY, item);
                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.message_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailFragment.MSG_KEY, item);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ListActivity parent,
                                      List<Message> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.c_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).read ? "1" : "");
            holder.mContentView.setText(mValues.get(position).title + DateUtil.format(mValues.get(position).sentAt));
            holder.mDetailsView.setText(mValues.get(position).body);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mDetailsView;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.content);
                mDetailsView = view.findViewById(R.id.details);
            }
        }
    }
}