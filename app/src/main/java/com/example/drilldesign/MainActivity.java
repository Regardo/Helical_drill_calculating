package com.example.drilldesign;

import android.content.Intent;
import android.os.Bundle;

import com.example.drilldesign.databinding.FragmentFirstBinding;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.drilldesign.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FragmentFirstBinding binding2;
    EditText holeDiameterEditText;
    CheckBox isFinishBox;
    Spinner workpieceMaterialSpinner ;
    Spinner machineSpinner;
    EditText toleranceEditText;
    EditText toleranceESEditText;
    EditText toleranceEIEditText;
     double holeDiameter;
     boolean isFinish;
     int workpieceMaterial;
     int machine;
     double tolerance;
     double toleranceES;
     double toleranceEI;
    Data data = new Data();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);



        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        holeDiameter = 20.0;
        isFinish = true;
        workpieceMaterial = 0;
        machine = 0;
        tolerance = 0.13;
        toleranceES = 0.13;
        toleranceEI = 0.0;


        //подключение всех елементов ввода
         holeDiameterEditText = findViewById(R.id.holeDiameter);
         isFinishBox = findViewById(R.id.finishingCheckbox);
         workpieceMaterialSpinner = findViewById(R.id.workpieceSpinner);
         machineSpinner = findViewById(R.id.machineSpinner);
         toleranceEditText = findViewById(R.id.editTolerance);
         toleranceESEditText = findViewById(R.id.editTolerance_es);
         toleranceEIEditText = findViewById(R.id.editTolerance_ei);



        Intent mainIntent = new Intent(MainActivity.this, FirstFragment.class);
        mainIntent.putExtra("hello", "hello World");




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            holeDiameterEditText.setText(null);
            isFinishBox.setChecked(true);
            workpieceMaterialSpinner.setSelection(0);
            machineSpinner.setSelection(0);
            toleranceEditText.setText(null);
            toleranceESEditText.setText(null);
            toleranceEIEditText.setText(null);
            return true;
        }
        if (id == R.id.action_about) {
            FragmentManager manager = getSupportFragmentManager();
            dialogFragmentAbout myDialogFragmentAbout = new dialogFragmentAbout();
            myDialogFragmentAbout.show(manager, "About");

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }




}
