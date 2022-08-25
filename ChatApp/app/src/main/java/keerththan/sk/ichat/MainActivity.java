package keerththan.sk.ichat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import keerththan.sk.ichat.Adapter.FragmentAdapter;
import keerththan.sk.ichat.Models.Users;
import keerththan.sk.ichat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    String globalUserLanguage ;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.taayout.setupWithViewPager(binding.viewPager);







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
               // Toast.makeText(this, "Setting is clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.groupChat:
                // Toast.makeText(this, "Group Chat is clicked", Toast.LENGTH_LONG).show();

                Intent intent1 = new Intent(MainActivity.this, GroupChatActivity.class);
                startActivity(intent1);


                break;
            case R.id.logout:
                //Toast.makeText(this, "Logout is clicked", Toast.LENGTH_LONG).show();
                mAuth.signOut();
                Intent intent2 = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent2);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}