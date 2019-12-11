package com.salabon.lazycards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MainActivity extends SingleAbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }
}
