package com.junts.ricardo.junts_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.junts.ricardo.junts_2.dummy.LocalConteudo;

/**
 * An activity representing a single Local detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link LocalListActivity}.
 */
public class LocalDetailActivity extends AppCompatActivity {
    protected static LocalConteudo.LocalItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        item = LocalConteudo.ITEM_MAP.get(getIntent().getStringExtra(LocalDetailFragment.ARG_ITEM_ID));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent(LocalDetailActivity.this, MapsActivity.class);
                intentMap.putExtra("latitude", item.latitude);
                intentMap.putExtra("longitude", item.longitude);

                if(MyLocationListener.localizacaoAtual == null) {
                    Toast.makeText(getApplicationContext(), "Aguarde, estamos verificando sua localizaçao", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(intentMap);
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(LocalDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(LocalDetailFragment.ARG_ITEM_ID));
            LocalDetailFragment fragment = new LocalDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.local_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, LocalListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
