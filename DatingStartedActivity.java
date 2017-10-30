package hr.from.kovacevic.ivor.ssdatingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hr.from.kovacevic.ivor.ssdatingapp.model.Round;
import hr.from.kovacevic.ivor.ssdatingapp.model.RoundKeeper;

public class DatingStartedActivity extends AppCompatActivity {

    private FragmentStatePagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_started);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), RoundKeeper.getRounds());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dating_started, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_ROUND_TITLE = "round_title";
        private static final String ARG_TABLES = "round_tables";
        private TextView tvRoundCouples;
        private ImageButton btnPlay;
        private ImageButton btnPause;
        private ImageButton btnFwd10;
        private CountDownTimer countDownTimer;
        private int progress;
        private TextView tvTimer;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Round round) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_ROUND_TITLE, round.getName());
            args.putStringArrayList(ARG_TABLES, round.getTablesAsStringArrayList());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {

            String roundTitle = getArguments().getString(ARG_ROUND_TITLE);
            Log.i("ekovivo", roundTitle + " savedInstanceState: " + savedInstanceState);
            View rootView = initView(inflater, container);
            
            ArrayList<String> tables = getArguments().getStringArrayList(ARG_TABLES);
            String couples = "";
            for (String table : tables) {
                couples += table + "\n";
            }

            tvRoundCouples.setText(couples);


            SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Integer duration = sharedPref.getInt(getString(R.string.round_duration_key), this.getActivity().getResources().getInteger(R.integer.default_round_duration));


            setRemainingTime(duration);


            progress = duration;

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnPause.setEnabled(true);
                    btnPlay.setEnabled(false);
                    btnFwd10.setEnabled(false);
                    countDownTimer = new CountDownTimer(progress * 1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            progress = (int) (millisUntilFinished / 1000);
                            setRemainingTime(progress);
                            if (progress <= 0) {
                                this.onFinish();
                            }
                        }

                        public void onFinish() {
                            setRemainingTime(0);
                            tvTimer.setBackgroundResource(R.color.colorAccent);
                            Vibrator v = (Vibrator) PlaceholderFragment.this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            if (v.hasVibrator()) {
                                // Vibrate for 1000 milliseconds
                                v.vibrate(1000);
                            }
                        }
                    }.start();
                }
            });

            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    btnPause.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnFwd10.setEnabled(true);
                }
            });

            btnFwd10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progress -= 10;
                    if (progress < 0) progress = 1;
                    setRemainingTime(progress);
                }
            });

            return rootView;
        }

        private View initView(LayoutInflater inflater, final ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_dating_started, container, false);
            tvRoundCouples = (TextView) rootView.findViewById(R.id.round_couples);
            tvTimer = (TextView) rootView.findViewById(R.id.tvTimer);
            btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
            btnPause = (ImageButton) rootView.findViewById(R.id.btnPause);
            btnFwd10 = (ImageButton) rootView.findViewById(R.id.btnFwd10);
            btnPause.setEnabled(false);
            btnPlay.setEnabled(true);
            btnFwd10.setEnabled(true);

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            Log.i("ekovivo", "onSaveInstanceState");
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            Log.i("ekovivo", "onDestroyView");
        }

        public void setRemainingTime(Integer remainingSeconds) {
            Date d = new Date(remainingSeconds * 1000L);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            String time = df.format(d);
            tvTimer.setText(time);
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Round> rounds;

        public SectionsPagerAdapter(FragmentManager fm, List<Round> rounds) {
            super(fm);
            this.rounds = rounds;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(rounds.get(position));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return rounds.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return rounds.get(position).getName();
        }
    }
}
