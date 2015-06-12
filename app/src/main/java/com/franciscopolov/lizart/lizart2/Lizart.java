package com.franciscopolov.lizart.lizart2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

//import de.madcyph3r.materialnavigationdrawer.R;
import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawer;
import de.madcyph3r.materialnavigationdrawer.head.MaterialHeadItem;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialMenu;
import de.madcyph3r.materialnavigationdrawer.menu.item.MaterialSection;
import de.madcyph3r.materialnavigationdrawer.tools.RoundedCornersDrawable;

/**
 * Created by Polo on 28/05/2015.
 */
public class Lizart extends MaterialNavigationDrawer {
    MaterialNavigationDrawer drawer = null;

    @Override
    public int headerType() {
        // set type. you get the available constant from MaterialNavigationDrawer class
        return MaterialNavigationDrawer.DRAWERHEADER_HEADITEMS;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        drawer = this;

        MaterialMenu menu = new MaterialMenu();


        // first section is loaded
        MaterialSection section1 = this.newSection("Inicio", this.getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp), new FragmentIndex(), false, menu);
        MaterialSection section2 = this.newSection("Subir fotografía", this.getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp), new FragmentIndex(), false, menu);
        MaterialSection section3 = this.newSection("Búsqueda de usuarios", this.getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp), new FragmentIndex(), false, menu);
        MaterialSection section4 = this.newSection("Configuración", this.getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp), new FragmentConfig(), true, menu);
        // create icon

        // create Head Item (Start index is section 2)

        MaterialHeadItem headItem = new MaterialHeadItem(this, "C HeadItem", "C Subtitle", R.drawable.ic_launcher, R.drawable.mat5, menu);
        // add head Item (menu will be loaded automatically)
        //MaterialHeadItem headItem1 = new MaterialHeadItem(this, "ITEM", "Subitem", R.drawable.ic_launcher, R.drawable.mat5, menu);
        this.addHeadItem(headItem);
    }
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            Toast.makeText(this, "Presione otra vez para salir de Lizart",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

}
