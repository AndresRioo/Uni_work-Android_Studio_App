package edu.ub.pis2324.projecte.presentation.utils;

import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationUtil {
    private static BottomNavigationView bottomNavigationView;

    // Constructor que recibe la referencia de BottomNavigationView
    public BottomNavigationUtil(BottomNavigationView bottomNavigationView) {
        BottomNavigationUtil.bottomNavigationView = bottomNavigationView;
    }

    // Método para cambiar el ítem seleccionado en la BottomNavigationView
    public static void setSelectedItem(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        } else {
            Toast.makeText(null, "BottomNavigationView is null", Toast.LENGTH_SHORT).show();
        }
    }
}
