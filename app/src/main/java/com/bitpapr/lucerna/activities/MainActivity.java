package com.bitpapr.lucerna.activities;

import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitpapr.lucerna.R;
import com.bitpapr.lucerna.fragments.PopularMoviesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ImageView mProfileImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = findViewById(R.id.navigation_view);
        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout_main);

        View navigationViewHeader = mNavigationView.getHeaderView(0);

        mProfileImageView = navigationViewHeader.findViewById(R.id.image_profile_picture);
        mNameTextView = navigationViewHeader.findViewById(R.id.text_name);
        mEmailTextView = navigationViewHeader.findViewById(R.id.text_email);

        loadUserData();
        setupNavigationDrawer();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            if (firebaseUser.getPhotoUrl() != null) {
                Picasso.with(this).load(firebaseUser.getPhotoUrl())
                        .into(mProfileImageView);
            } else {
                mProfileImageView.setImageResource(R.drawable.ic_default_profile_150dp);
            }

            mNameTextView.setText(firebaseUser.getDisplayName());
            mEmailTextView.setText(firebaseUser.getEmail());
        }
    }

    private void setupNavigationDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        setSupportActionBar(mToolbar);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavigationView.setNavigationItemSelectedListener((MenuItem menuItem) -> {
            selectNavigationItem(menuItem);
            return true;
        });

        MenuItem firstItem = mNavigationView.getMenu().getItem(0);
        if (firstItem != null) {
            selectNavigationItem(firstItem);
        }
    }

    private void selectNavigationItem(MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.action_popular_movies:
                fragment = new PopularMoviesFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_content, fragment)
                    .commit();

            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());

            mDrawerLayout.closeDrawers();
        }
    }
}
