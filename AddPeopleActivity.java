package hr.from.kovacevic.ivor.ssdatingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hr.from.kovacevic.ivor.ssdatingapp.db.AppDatabase;
import hr.from.kovacevic.ivor.ssdatingapp.db.entities.User;
import hr.from.kovacevic.ivor.ssdatingapp.model.PeopleModel;
import hr.from.kovacevic.ivor.ssdatingapp.model.Round;
import hr.from.kovacevic.ivor.ssdatingapp.model.RoundKeeper;

public class AddPeopleActivity extends AppCompatActivity {

    private static final String INPUT_NAME_TEXT = "input_name_text";
    private final String TAG = AddPeopleActivity.class.getSimpleName();
    private PeopleModel mPeopleModel;
    private LinearLayout llPeopleList;
    private Button btnStart;
    private Button btnRemoveAll;
    private ImageButton ibAddPerson;
    private EditText etNewPerson;


    private AsyncTask<Void, Void, Void> getPrepareRoundAndStartDatingTask() {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Round> rounds = mPeopleModel.getRounds();
                RoundKeeper.setRounds(rounds);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (RoundKeeper.getRounds() == null) {
                    Toast.makeText(AddPeopleActivity.this, R.string.add_people_before_start, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(AddPeopleActivity.this, DatingStartedActivity.class);
                    startActivity(intent);
                }

            }
        };
    }

    private AsyncTask<String, Void, User> getAddPersonTask() {
        return new AsyncTask<String, Void, User>() {
            @Override
            protected User doInBackground(String... strings) {
                return mPeopleModel.addUser(strings[0]);
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                Log.d(TAG, "Added: " + user.getName());
                updateUI();
            }
        };
    }

    private AsyncTask<Void, Void, Void> getRemoveAllTask() {
        return new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mPeopleModel.removeAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateUI();
            }
        };
    }

    private AsyncTask<Void, Void, List<String>> getUpdateUITask() {
        return new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... voids) {
                return PeopleModel.getUserNames();
            }

            @Override
            protected void onPostExecute(List<String> peopleList) {
                super.onPostExecute(peopleList);
                llPeopleList.removeAllViews();

                int num = peopleList.size();
                for (String person : peopleList) {
                    LinearLayout personView = createNewPersonView(num--, person);
                    llPeopleList.addView(personView, 0);
                }

            }
        };
    }

    private AsyncTask<String, Void, Void> getRemovePersonTask() {
        return new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                mPeopleModel.removeUser(strings[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                updateUI();
            }
        };
    }

    private AsyncTask<String, Void, Void> getUpdatePersonTask() {
        return new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                mPeopleModel.updateUserName(strings[0], strings[1]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                updateUI();
            }
        };
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_add_people);
        mPeopleModel = ViewModelProviders.of(this).get(PeopleModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWidgets();
        setupListeners();

        updateUI();
    }

    private void initWidgets() {
        llPeopleList = (LinearLayout) findViewById(R.id.list_people);
        ibAddPerson = (ImageButton) findViewById(R.id.add_person);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnRemoveAll = (Button) findViewById(R.id.btnRemoveAll);
        etNewPerson = (EditText) findViewById(R.id.new_person);


    }

    private void setupListeners() {
        ibAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPersonName = String.valueOf(etNewPerson.getText());
                if (newPersonName == null || "".equals(newPersonName)) {
                    Toast.makeText(AddPeopleActivity.this, R.string.enneter_name_before_add, Toast.LENGTH_LONG).show();
                } else {
                    etNewPerson.setText("");
                    getAddPersonTask().execute(newPersonName);
                }
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrepareRoundAndStartDatingTask().execute();
            }
        });


        btnRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog areYouSure = new AlertDialog.Builder(AddPeopleActivity.this)
                        .setTitle("Are you sure ou want to remove all people from the list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getRemoveAllTask().execute();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                areYouSure.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_people, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                showSettingsDialog();
                return true;

            case R.id.action_start:
                getPrepareRoundAndStartDatingTask().execute();
                return true;

            case R.id.action_about:
                showAboutDialog();
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        final Dialog aboutDialog = new Dialog(AddPeopleActivity.this);
        aboutDialog.setContentView(R.layout.about_dialog);
        TextView tvLauncherIconCredit = (TextView) aboutDialog.findViewById(R.id.launcher_icon_credit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvLauncherIconCredit.setText(Html.fromHtml(getString(R.string.about_html), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvLauncherIconCredit.setText(Html.fromHtml(getString(R.string.about_html)));
        }
        tvLauncherIconCredit.setMovementMethod(LinkMovementMethod.getInstance());

        Button btnCloseAbout = (Button) aboutDialog.findViewById(R.id.about_close);
        btnCloseAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutDialog.dismiss();
            }
        });

        aboutDialog.show();
    }

    private void showSettingsDialog() {

        final Dialog d = new Dialog(AddPeopleActivity.this);
        d.setTitle("Settings");
        d.setContentView(R.layout.settings_dialog);
        Button b1 = (Button) d.findViewById(R.id.btnSaveSettings);
        Button b2 = (Button) d.findViewById(R.id.btnCancelSettings);
        SharedPreferences sharedPref = AddPeopleActivity.this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Integer duration = sharedPref.getInt(getString(R.string.round_duration_key), AddPeopleActivity.this.getResources().getInteger(R.integer.default_round_duration));
        final EditText np = (EditText) d.findViewById(R.id.etRoundDuration);
        np.setText(String.valueOf(duration));

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = AddPeopleActivity.this.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.round_duration_key), Integer.valueOf(String.valueOf(np.getText())));
                editor.apply();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }


    // This callback is called only when there is a saved instance previously saved using
    // onSaveInstanceState(). We restore some state in onCreate() while we can optionally restore
    // other state here, possibly usable after onStart() has completed.
    // The savedInstanceState Bundle is same as the one used in onCreate().
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        etNewPerson.setText(savedInstanceState.getString(INPUT_NAME_TEXT));
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(INPUT_NAME_TEXT, String.valueOf(etNewPerson.getText()));
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private void updateUI() {
        Log.v(TAG, "updateUI");
        getUpdateUITask().execute();
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    private LinearLayout createNewPersonView(int num, final String person) {
        LinearLayout personView = (LinearLayout) LayoutInflater.from(AddPeopleActivity.this).inflate(
                R.layout.person, llPeopleList, false);
        TextView tvName = (TextView) personView.findViewById(R.id.person_name);
        ImageButton btnDelete = (ImageButton) personView.findViewById(R.id.delete_person);
        ImageButton btnEdit = (ImageButton) personView.findViewById(R.id.edit_person);
        tvName.setText(num + ". " + person);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewPerson.setText(person);
                getRemovePersonTask().execute(person);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText etUser = new EditText(AddPeopleActivity.this);
                etUser.setText(person);
                AlertDialog dialog = new AlertDialog.Builder(AddPeopleActivity.this)
                        .setTitle("Edit")
                        .setView(etUser)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newUserName = String.valueOf(etUser.getText());
                                getUpdatePersonTask().execute(person, newUserName);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
        return personView;
    }
}
