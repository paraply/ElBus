package se.elbus.oaakee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Stack;

import se.elbus.oaakee.Fragments.DestinationFragment;
import se.elbus.oaakee.Fragments.FragmentSwitchCallbacks;
import se.elbus.oaakee.Fragments.HamburgerFragment;
import se.elbus.oaakee.Fragments.InfoFragment;
import se.elbus.oaakee.Fragments.PaymentFragment;
import se.elbus.oaakee.Fragments.SettingsFragment;
import se.elbus.oaakee.Fragments.TravelFragment;

public class MainActivity extends AppCompatActivity implements HamburgerFragment.NavigationDrawerCallbacks, FragmentSwitchCallbacks {

    private HamburgerFragment mHamburgerFragment;

    private ArrayList<Fragment> mFragments;
    private Stack<Fragment> mTravelFragments;

    private CharSequence mTitle;

    //For notifications etc.
    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragments = new ArrayList<>();
        mTravelFragments = new Stack<>();

        setTitle(getString(R.string.title_section_trip));

        /*
          Here is where we add the fragments in order.
         */
        mTravelFragments.push(new TravelFragment());

        mFragments.add(mTravelFragments.peek());
        mFragments.add(new PaymentFragment());
        mFragments.add(new SettingsFragment());

        changeFragment(mFragments.get(0));

        /*
        This will find and save the hamburger menu fragment.
         */
        mHamburgerFragment = (HamburgerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mHamburgerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * Called when an item in the navigation drawer is selected.
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position + 1) {
            case 1:
                mTitle = getString(R.string.title_section_trip);
                break;
            case 2:
                mTitle = getString(R.string.title_section_payment);
                break;
            case 3:
                mTitle = getString(R.string.title_section_settings);
                break;
        }

        if (mFragments != null) {
            Fragment newFragment = mFragments.get(position);
            if (newFragment != null) {
                changeFragment(newFragment);
            }
        }
    }

    /**
     * Method to call when changing the fragment in the main view without backstack.
     * @param f is the fragment to show.
     */
    private void changeFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.main_container, f)
                .commit();
    }

    /**
     * Method to call when changing the fragment in the main view with backstack.
     * @param f is the fragment to show.
     */
    private void changeFragmentWithBackstack(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.main_container, f)
                .addToBackStack(null)
                .commit();
    }

    /**
     * This is what changes the action bar when the app drawer is opened.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mHamburgerFragment.isDrawerOpen()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(true);
            }
            actionBar.setTitle(mTitle);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This is to move to the next fragment in the Travel menu.
     * @param args
     */
    @Override
    public void nextFragment(Bundle args) {
        Fragment newFragment;
        newFragment = getNextFragment();
        newFragment.setArguments(args);

        mTravelFragments.push(newFragment);
        mFragments.set(0, mTravelFragments.peek());

        changeFragmentWithBackstack(newFragment);
    }

    /**
     * This is called to get the next fragment to show in the Travel menu.
     */
    private Fragment getNextFragment() {
        switch (mTravelFragments.size()) {
            case 1:
                return new DestinationFragment();
            case 2:
                return new InfoFragment();
            default:
                throw new RuntimeException("You should not go to next fragment in the last one!");
        }
    }

    @Override
    public void onBackPressed() {
        mTravelFragments.pop();
        super.onBackPressed();
    }
}
