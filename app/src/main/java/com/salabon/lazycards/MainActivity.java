package com.salabon.lazycards;

import androidx.fragment.app.Fragment;

public class MainActivity extends SingleAbstractActivity {
    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }
}
