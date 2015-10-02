package se.elbus.oaakee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import se.elbus.oaakee.Fragments.HamburgerFragment;
import se.elbus.oaakee.Fragments.TravelFragment;

public class MainActivity extends AppCompatActivity implements HamburgerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private HamburgerFragment mHamburgerFragment;

    private ArrayList<Fragment> mFragments;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragments = new ArrayList<>();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

        if (fragment == null) {
            fragment = new TravelFragment();
            mFragments.add(fragment);
            setTitle(getString(R.string.title_section1));

            /*
             This will replace the placeholder in activity_main
             with the default starting fragment, which is TravelFragment.
             */
            fm.beginTransaction()
                    .add(R.id.main_container, fragment)
                    .commit();

        }

        /*
          Here is where we add the fragments in order.
         */
        mFragments.add(new TravelFragment()); // TODO: Change this to fragment for "Stämpla"
        mFragments.add(new TravelFragment()); // TODO: Change this to fragment for "Konto"
        mFragments.add(new TravelFragment()); // TODO: Change this to fragment for "Inställningar"
        mFragments.add(new TravelFragment()); // TODO: Change this to fragment for "Historik"

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
        switch (position+1) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }

        if(mFragments != null){
            Fragment newFragment = mFragments.get(position);
            if(newFragment != null){
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.main_container, newFragment)
                        .commit();
            }
        }

    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mHamburgerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.hamburger, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
