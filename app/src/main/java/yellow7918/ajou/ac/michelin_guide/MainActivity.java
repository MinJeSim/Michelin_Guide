package yellow7918.ajou.ac.michelin_guide;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnRestaurantClickListener {

    private DrawerLayout drawerLayout;
    private RestaurantFragment fragment;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);

        fragment = new RestaurantFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();

        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
        toggle.syncState();

        int checkGrade = 0;
        CheckBox grade1 = findViewById(R.id.grade_1);
        CheckBox grade2 = findViewById(R.id.grade_2);
        CheckBox grade3 = findViewById(R.id.grade_3);
        CheckBox grade4 = findViewById(R.id.grade_4);
        CheckBox grade5 = findViewById(R.id.grade_5);
        CheckBox[] grades = {grade1, grade2, grade3, grade4, grade5};

        grade1.setOnClickListener(view -> {
            for (int i = 0; i < grades.length; i++)
                if (i != 0)
                    grades[i].setChecked(false);
        });

        grade2.setOnClickListener(view -> {
            for (int i = 0; i < grades.length; i++)
                if (i != 1)
                    grades[i].setChecked(false);
        });

        grade3.setOnClickListener(view -> {
            for (int i = 0; i < grades.length; i++)
                if (i != 2)
                    grades[i].setChecked(false);
        });

        grade4.setOnClickListener(view -> {
            for (int i = 0; i < grades.length; i++)
                if (i != 3)
                    grades[i].setChecked(false);
        });

        grade5.setOnClickListener(view -> {
            for (int i = 0; i < grades.length; i++)
                if (i != 4)
                    grades[i].setChecked(false);
        });


        Button confirm = findViewById(R.id.confirm);

        EditText location = findViewById(R.id.text_location);
        EditText category = findViewById(R.id.text_category);
        RangeBar money = findViewById(R.id.money);

        String[] spinnerList = {getString(R.string.string_simple_category1), getString(R.string.string_simple_category2), getString(R.string.string_simple_category3)};
        SpinnerAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList);
        Spinner spinner = findViewById(R.id.spinner_category);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        EditText editText = findViewById(R.id.text_search);

        ImageView searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> {
            String searchData = editText.getText().toString();
            if (searchData.length() <= 0) {
                Snackbar.make(view, getString(R.string.string_input_warning), Snackbar.LENGTH_SHORT).show();
                return;
            }

            String loc = "";
            String cat = "";
            String name = "";
            String type = ((String) spinner.getSelectedItem());

            //
            if (type.contains("요"))
                cat = searchData;
            else if (type.contains("식"))
                name = searchData;
            else if (type.contains("지"))
                loc = searchData;

            fragment.updateListBySimpleQuery(loc, cat, name);
            Toast.makeText(this, type + " / " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        confirm.setOnClickListener(view -> {
            String loc = location.getText().toString();
            String cat = category.getText().toString();
            String min = String.valueOf(Integer.parseInt(money.getLeftPinValue()) * 10000);
            String max = String.valueOf(Integer.parseInt(money.getRightPinValue()) * 10000);
            String grade = "";
            for (int i = 0; i < grades.length; i++) {
                if (grades[i].isChecked()) {
                    grade = String.valueOf(i + 1);
                    break;
                }

            }

            fragment.updateListByComplexQuery(loc, cat, min, max, grade);
//            Toast.makeText(this, loc + "/" + cat + "/" + min + "/" + max, Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reset) {
            fragment.updateListAll();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
                return;
            }
            if (pressedTime == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.string_close_warning), Toast.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    Toast.makeText(MainActivity.this, getString(R.string.string_close_warning), Toast.LENGTH_LONG).show();
                    pressedTime = 0;
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void onClickRestaurant(Restaurant restaurant) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, RestaurantDetailFragment.newInstance(restaurant)).addToBackStack(null).commit();
    }

    private void setLocale() {
        Context context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context = updateResources(this, "en");
        } else {
            context = updateResourcesLegacy(this, "en");
        }

        Resources resources = context.getResources();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
