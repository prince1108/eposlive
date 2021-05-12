package com.foodciti.foodcitipartener.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.EditableAddonAdapter;
import com.foodciti.foodcitipartener.adapters.EditableCategoryAdapter;
import com.foodciti.foodcitipartener.adapters.EditableMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.EditableTableOrderAdapter;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.dialogs.AddonEditDialog;
import com.foodciti.foodcitipartener.dialogs.CategoryEditDialog;
import com.foodciti.foodcitipartener.dialogs.ColorDialog;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.EditPrintOrderDialog;
import com.foodciti.foodcitipartener.dialogs.ItemEditDialog;
import com.foodciti.foodcitipartener.dialogs.TableEditDialog;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.ItemColor;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmDBHelper;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.RecyclerViewItemSelectionAfterLayoutUpdate;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class EditMenu extends AppCompatActivity implements EditableMenuItemAdapter.Callback, EditableCategoryAdapter.MenuCategoryListener
        , EditableAddonAdapter.AddonClicklistener, EditableTableOrderAdapter.Callback {
    private RecyclerView tableRV, categoryRecycler, menuItemRecycler, commonItemRecycler, addonRecycler, noAddonRecycler;
    private EditableMenuItemAdapter commonItemAdapter, menuItemAdapter;
    private EditableTableOrderAdapter editableTableOrderAdapter;
    private EditableAddonAdapter addonAdapter, noAddonAdapter;
    private EditableCategoryAdapter menuCatAdapter;
    private List<MenuItem> menuItemList, commonItemList;
    private List<MenuCategory> cat_list;
    private List<Addon> addonList, noAddonList;
    private static final String TAG = "EditMenu";
    private Dialog modifyMenuItem;
    private Realm realm;
    private String orderType;
    private RealmDBHelper realmDBHelper;
    private Map<String, Integer> colorMap = new HashMap<>();
    private CommonMethods commonMethods;

    private int lastAddonColor = -1, lastMenuCategoryColor = -1, lastMenuItemColor = -1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TextView print_copy_counter, percent1, percent2, percent3, percent4, percentTableHeight;
    private SeekBar widthRatio, heightRatio, categoryWidthRatio, addonHeightRatio, tableHeightRatio;
    private LinearLayout itemAddonContainer, menuItemContainer, addonContainer, itemContainer;
    private FrameLayout tableContainer;

    private CounterBox addonColumnCounter, noAddonColumnCounter, menuItemColumnCounter, commonItemColumnCounter,
            categoryColCounter, tableColCounter;
    private CounterBox printCopyCounterDel,printCopyCounterCol,printCopyCounterTab;
    private CheckBox checkboxTextAfterOrder, textOrderCount;
    private RadioGroup menuIconSizes;

    @Override
    protected void onResume() {
        super.onResume();

        realm = RealmManager.getLocalInstance();
        realmDBHelper = new RealmDBHelper(this, realm);
        setUpRecyclerViews();

        RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).notEqualTo("name", Constants.MENUCATEGORY_COMMON).findAll();
        menuCatAdapter.setMenuCategories(menuCategories);
        menuCatAdapter.notifyDataSetChanged();

        /*commonItemList.clear();
        commonItemList.addAll(realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll());*/
        RealmResults<MenuItem> commonItems = realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll();
        commonItemAdapter.setItems(commonItems);
        commonItemAdapter.notifyDataSetChanged();

        RecyclerViewItemSelectionAfterLayoutUpdate.on(categoryRecycler);
    }


    @Override
    protected void onDestroy() {
//        RealmManager.closeRealmFor(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        orderType = getIntent().getStringExtra("ORDER_TYPE");
        Log.e(TAG, "------------------------order Type: " + orderType);

        commonMethods = new CommonMethods(this);
        initViews();
        colorMap = commonMethods.getColorMap();
    }

    private void initViews() {
        tableRV = findViewById(R.id.tableRV);
        int numColTable = sharedPreferences.getInt(Preferences.TABLE_NUM_COLUMNS, 4);
        RecyclerView.LayoutManager tableLM = new GridLayoutManager(this, numColTable, RecyclerView.VERTICAL, false);
        tableRV.setLayoutManager(tableLM);
        tableRV.setHasFixedSize(true);

        categoryRecycler = findViewById(R.id.category);
        int numColMenuCat = sharedPreferences.getInt(Preferences.CATEGORY_NUM_COLUMNS, 1);
        RecyclerView.LayoutManager categoryLayoutManager = new GridLayoutManager(this, numColMenuCat);
        categoryRecycler.setLayoutManager(categoryLayoutManager);

        menuItemRecycler = findViewById(R.id.category_data);
        int numColMenuItems = sharedPreferences.getInt(Preferences.MENUITEM_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager menuItemLayoutManager = new GridLayoutManager(this, numColMenuItems);
        menuItemRecycler.setLayoutManager(menuItemLayoutManager);

        commonItemRecycler = findViewById(R.id.commonItemList);
        int numColCommonItems = sharedPreferences.getInt(Preferences.COMMONITEM_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager commonItemLayoutManager = new GridLayoutManager(this, numColCommonItems);
        commonItemRecycler.setLayoutManager(commonItemLayoutManager);

        addonRecycler = findViewById(R.id.addon_list);
        int numColAddons = sharedPreferences.getInt(Preferences.ADDON_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager addonLayoutManager = new GridLayoutManager(this, numColAddons);
        addonRecycler.setLayoutManager(addonLayoutManager);

        noAddonRecycler = findViewById(R.id.noitem_list);
        int numColNoAddons = sharedPreferences.getInt(Preferences.NO_ADDON_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager noAddonLayoutManager = new GridLayoutManager(this, numColNoAddons);
        noAddonRecycler.setLayoutManager(noAddonLayoutManager);

        addonColumnCounter = findViewById(R.id.addonColumnCount);
        addonColumnCounter.setLowerLimit(1);
        addonColumnCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                Toast.makeText(EditMenu.this, "count: " + count, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "-----------count: " + count);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                addonRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.ADDON_NUM_COLUMNS, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                Toast.makeText(EditMenu.this, "count: " + count, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "-----------count: " + count);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                addonRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.ADDON_NUM_COLUMNS, count);
                editor.apply();
            }
        });

        noAddonColumnCounter = findViewById(R.id.noAddonColCounter);
        noAddonColumnCounter.setLowerLimit(1);
        noAddonColumnCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                noAddonRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.NO_ADDON_NUM_COLUMNS, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                noAddonRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.NO_ADDON_NUM_COLUMNS, count);
                editor.apply();
            }
        });

        menuItemColumnCounter = findViewById(R.id.menuItemColCounter);
        menuItemColumnCounter.setLowerLimit(1);
        menuItemColumnCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                menuItemRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.MENUITEM_NUM_COLUMNS, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                menuItemRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.MENUITEM_NUM_COLUMNS, count);
                editor.apply();
            }
        });

        commonItemColumnCounter = findViewById(R.id.commonItemColCounter);
        commonItemColumnCounter.setLowerLimit(1);
        commonItemColumnCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                commonItemRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.COMMONITEM_NUM_COLUMNS, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                commonItemRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.COMMONITEM_NUM_COLUMNS, count);
                editor.apply();
            }
        });

        categoryColCounter = findViewById(R.id.categoryColCounter);
        categoryColCounter.setLowerLimit(1);
        categoryColCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                categoryRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.CATEGORY_NUM_COLUMNS, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count);
                categoryRecycler.setLayoutManager(layoutManager);
                editor.putInt(Preferences.CATEGORY_NUM_COLUMNS, count);
                editor.apply();
            }
        });
        tableColCounter = findViewById(R.id.tableColCounter);
        tableColCounter.setLowerLimit(1);
        tableColCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count, RecyclerView.VERTICAL, false);
                tableRV.setLayoutManager(layoutManager);
                editor.putInt(Preferences.TABLE_NUM_COLUMNS, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), count, RecyclerView.VERTICAL, false);
                tableRV.setLayoutManager(layoutManager);
                editor.putInt(Preferences.TABLE_NUM_COLUMNS, count);
                editor.apply();
            }
        });

        printCopyCounterDel = findViewById(R.id.printCopyCounterDel);
        printCopyCounterDel.setLowerLimit(0);
        printCopyCounterCol= findViewById(R.id.printCopyCounterCol);
        printCopyCounterCol.setLowerLimit(0);
        printCopyCounterTab= findViewById(R.id.printCopyCounterTab);
        printCopyCounterTab.setLowerLimit(0);

        printCopyCounterDel.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_DEL, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_DEL, count);
                editor.apply();
            }
        });

        int counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_DEL, 1);
        printCopyCounterDel.setCount(counter);


        printCopyCounterCol.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_COL, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_COL, count);
                editor.apply();
            }
        });

        printCopyCounterCol.setCount(sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_COL, 1));

        printCopyCounterTab.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_TAB, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_TAB, count);
                editor.apply();
            }
        });

        printCopyCounterTab.setCount(sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_TAB, 1));


        int addonNumCol = sharedPreferences.getInt(Preferences.ADDON_NUM_COLUMNS, 3);
        addonColumnCounter.setCount(addonNumCol);
        int noaddonNumCol = sharedPreferences.getInt(Preferences.NO_ADDON_NUM_COLUMNS, 3);
        noAddonColumnCounter.setCount(noaddonNumCol);
        int menuItemNumCol = sharedPreferences.getInt(Preferences.MENUITEM_NUM_COLUMNS, 3);
        menuItemColumnCounter.setCount(menuItemNumCol);
        int commonItemNumCol = sharedPreferences.getInt(Preferences.COMMONITEM_NUM_COLUMNS, 3);
        commonItemColumnCounter.setCount(commonItemNumCol);
        int categoryNumCol = sharedPreferences.getInt(Preferences.CATEGORY_NUM_COLUMNS, 1);
        categoryColCounter.setCount(categoryNumCol);
        int numTableCol = sharedPreferences.getInt(Preferences.TABLE_NUM_COLUMNS, 4);
        tableColCounter.setCount(numTableCol);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tableContainer = findViewById(R.id.tableContainer);
        itemAddonContainer = findViewById(R.id.itemAddonContainer);
        menuItemContainer = findViewById(R.id.menuItemContainer);
        addonContainer = findViewById(R.id.addonContainer);
        itemContainer = findViewById(R.id.itemContainer);
        final float tableHeight = sharedPreferences.getFloat(Preferences.TABLE_HEIGHT, 0.25f);
        final float categoryWidth = sharedPreferences.getFloat(Preferences.CATEGORY_ITEMS_WIDTH_RATIO, 0.2f);
        final float itemWeight = sharedPreferences.getFloat(Preferences.ITEMS_ADDONS_WIDTH_RATIO, 0.6f);
        final float itemHeight = sharedPreferences.getFloat(Preferences.ITEMS_HEIGHT_RATIO, 0.75f);
        final float addonHeight = sharedPreferences.getFloat(Preferences.ADDON_HEIGHT_RATIO, 0.5f);

        LinearLayout.LayoutParams tableLp = (LinearLayout.LayoutParams) tableContainer.getLayoutParams();
        LinearLayout.LayoutParams itemAddonLp = (LinearLayout.LayoutParams) itemAddonContainer.getLayoutParams();
        LinearLayout.LayoutParams categoryLp = (LinearLayout.LayoutParams) categoryRecycler.getLayoutParams();
        LinearLayout.LayoutParams itemContainerLp = (LinearLayout.LayoutParams) itemContainer.getLayoutParams();
        LinearLayout.LayoutParams menuItemContainerLp = (LinearLayout.LayoutParams) menuItemContainer.getLayoutParams();
        LinearLayout.LayoutParams addonContainerLp = (LinearLayout.LayoutParams) addonContainer.getLayoutParams();
        LinearLayout.LayoutParams menuItemRecyclerLp = (LinearLayout.LayoutParams) menuItemRecycler.getLayoutParams();
        LinearLayout.LayoutParams commonItemRecyclerLp = (LinearLayout.LayoutParams) commonItemRecycler.getLayoutParams();
        LinearLayout.LayoutParams addonRecyclerLp = (LinearLayout.LayoutParams) addonRecycler.getLayoutParams();
        LinearLayout.LayoutParams noAddonRecyclerLp = (LinearLayout.LayoutParams) noAddonRecycler.getLayoutParams();

        tableLp.weight = tableHeight;
        itemAddonLp.weight = 1 - tableHeight;

        categoryLp.weight = categoryWidth;
        itemContainerLp.weight = 1 - categoryWidth;

        menuItemContainerLp.weight = itemWeight;
        addonContainerLp.weight = 1 - itemWeight;

        menuItemRecyclerLp.weight = itemHeight;
        commonItemRecyclerLp.weight = 1 - itemHeight;

        addonRecyclerLp.weight = addonHeight;
        noAddonRecyclerLp.weight = 1 - addonHeight;

        tableContainer.setLayoutParams(tableLp);
        itemAddonContainer.setLayoutParams(itemAddonLp);
        categoryRecycler.setLayoutParams(categoryLp);
        itemContainer.setLayoutParams(itemContainerLp);
        menuItemContainer.setLayoutParams(menuItemContainerLp);
        addonContainer.setLayoutParams(addonContainerLp);
        menuItemRecycler.setLayoutParams(menuItemRecyclerLp);
        commonItemRecycler.setLayoutParams(commonItemRecyclerLp);
        addonRecycler.setLayoutParams(addonRecyclerLp);
        noAddonRecycler.setLayoutParams(noAddonRecyclerLp);

        percent1 = findViewById(R.id.percent1);
        percent2 = findViewById(R.id.percent2);
        percent3 = findViewById(R.id.percent3);
        percent4 = findViewById(R.id.percent4);
        percentTableHeight = findViewById(R.id.percentTableHeight);

        tableHeightRatio = findViewById(R.id.table_height_ratio);
        tableHeightRatio.setMax(100);
        int percentTableH = (int) (tableHeight * 100);
        percentTableHeight.setText(percentTableH + "");
        tableHeightRatio.setProgress(percentTableH);
        tableHeightRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentTableHeight.setText(progress + "");
                float p = (float) progress;
                float weightTop = p / 100;
                float weightBottom = 1 - weightTop;

                Log.e(TAG, "------------weightleft: " + weightTop + "; weightRight: " + weightBottom);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) tableContainer.getLayoutParams();
                LinearLayout.LayoutParams lr = (LinearLayout.LayoutParams) itemAddonContainer.getLayoutParams();
                ll.weight = weightTop;
                lr.weight = weightBottom;
                tableContainer.setLayoutParams(ll);
                itemAddonContainer.setLayoutParams(lr);

                editor.putFloat(Preferences.TABLE_HEIGHT, weightTop);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        categoryWidthRatio = findViewById(R.id.category_width_ratio);
        categoryWidthRatio.setMax(100);
        Log.e(TAG, "---------------category weight: " + categoryWidth);
        int percent_1 = (int) (categoryWidth * 100);
        percent1.setText(percent_1 + "");
        categoryWidthRatio.setProgress(percent_1);
        categoryWidthRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent1.setText(progress + "");
                float p = (float) progress;
                float weightLeft = p / 100;
                float weightRight = 1 - weightLeft;

                Log.e(TAG, "------------weightleft: " + weightLeft + "; weightRight: " + weightRight);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) categoryRecycler.getLayoutParams();
                LinearLayout.LayoutParams lr = (LinearLayout.LayoutParams) itemContainer.getLayoutParams();
                ll.weight = weightLeft;
                lr.weight = weightRight;
                categoryRecycler.setLayoutParams(ll);
                itemContainer.setLayoutParams(lr);

                editor.putFloat(Preferences.CATEGORY_ITEMS_WIDTH_RATIO, weightLeft);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final float midPaneWeight = ((LinearLayout.LayoutParams) menuItemContainer.getLayoutParams()).weight;
        Log.e(TAG, "---------------midpane weight: " + midPaneWeight);
        widthRatio = findViewById(R.id.width_ratio);
        widthRatio.setMax(100);
        int percent_2 = (int) (midPaneWeight * 100);
        percent2.setText(percent_2 + "");
        widthRatio.setProgress(percent_2);
        widthRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent2.setText(progress + "");
                float p = (float) progress;
                float weightLeft = p / 100;
                float weightRight = 1 - weightLeft;

                Log.e(TAG, "------------weightleft: " + weightLeft + "; weightRight: " + weightRight);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) menuItemContainer.getLayoutParams();
                LinearLayout.LayoutParams lr = (LinearLayout.LayoutParams) addonContainer.getLayoutParams();
                ll.weight = weightLeft;
                lr.weight = weightRight;
                menuItemContainer.setLayoutParams(ll);
                addonContainer.setLayoutParams(lr);
                editor.putFloat(Preferences.ITEMS_ADDONS_WIDTH_RATIO, weightLeft);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        heightRatio = findViewById(R.id.height_ratio);
        heightRatio.setMax(100);
        LinearLayout.LayoutParams ml = (LinearLayout.LayoutParams) menuItemRecycler.getLayoutParams();
        final float heightWeight = ml.weight;
        int percent_3 = (int) (heightWeight * 100);
        percent3.setText(percent_3 + "");
        heightRatio.setProgress(percent_3);
        heightRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent3.setText(progress + "");
                float p = (float) progress;
                float weightTop = p / 100;
                float weightBottom = 1 - weightTop;

                Log.e(TAG, "------------weightleft: " + weightTop + "; weightRight: " + weightBottom);
                LinearLayout.LayoutParams lt = (LinearLayout.LayoutParams) menuItemRecycler.getLayoutParams();
                LinearLayout.LayoutParams lb = (LinearLayout.LayoutParams) commonItemRecycler.getLayoutParams();
                lt.weight = weightTop;
                lb.weight = weightBottom;
                menuItemRecycler.setLayoutParams(lt);
                commonItemRecycler.setLayoutParams(lb);
                editor.putFloat(Preferences.ITEMS_HEIGHT_RATIO, weightTop);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addonHeightRatio = findViewById(R.id.addon_height_ratio);
        addonHeightRatio.setMax(100);
        LinearLayout.LayoutParams al = (LinearLayout.LayoutParams) addonRecycler.getLayoutParams();
        final float addonheightWeight = al.weight;
        int percent_4 = (int) (addonheightWeight * 100);
        percent4.setText(percent_4 + "");
        addonHeightRatio.setProgress(percent_4);
        addonHeightRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent4.setText(progress + "");
                float p = (float) progress;
                float weightTop = p / 100;
                float weightBottom = 1 - weightTop;

                Log.e(TAG, "------------weightleft: " + weightTop + "; weightRight: " + weightBottom);
                LinearLayout.LayoutParams lt = (LinearLayout.LayoutParams) addonRecycler.getLayoutParams();
                LinearLayout.LayoutParams lb = (LinearLayout.LayoutParams) noAddonRecycler.getLayoutParams();
                lt.weight = weightTop;
                lb.weight = weightBottom;
                addonRecycler.setLayoutParams(lt);
                noAddonRecycler.setLayoutParams(lb);
                editor.putFloat(Preferences.ADDON_HEIGHT_RATIO, weightTop);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkboxTextAfterOrder = findViewById(R.id.checkboxTextAfterOrder);
        boolean checked = sharedPreferences.getBoolean(Preferences.SEND_TEXT_MESSAGE_AFTER_ORDER, false);
        checkboxTextAfterOrder.setChecked(checked);
        checkboxTextAfterOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(Preferences.SEND_TEXT_MESSAGE_AFTER_ORDER, isChecked);
                editor.apply();
            }
        });

        textOrderCount = findViewById(R.id.textOrderCount);
        boolean isChecked = sharedPreferences.getBoolean(Preferences.VENDOR_ORDER_COUNTER, false);
        textOrderCount.setChecked(isChecked);
        textOrderCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(Preferences.VENDOR_ORDER_COUNTER, isChecked);
                editor.apply();
            }
        });

        menuIconSizes = findViewById(R.id.menuIconSizes);

        menuIconSizes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.icon_option1:
                        editor.putInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
                        editor.apply();
                        menuCatAdapter.notifyDataSetChanged();
                        menuItemAdapter.notifyDataSetChanged();
                        addonAdapter.notifyDataSetChanged();
                        noAddonAdapter.notifyDataSetChanged();
                        break;
                    case R.id.icon_option2:
                        editor.putInt(Preferences.MENU_ICON_SIZE, Constants.ICON_MEDIUM);
                        editor.apply();
                        menuCatAdapter.notifyDataSetChanged();
                        menuItemAdapter.notifyDataSetChanged();
                        addonAdapter.notifyDataSetChanged();
                        noAddonAdapter.notifyDataSetChanged();
                        break;
                    case R.id.icon_option3:
                        editor.putInt(Preferences.MENU_ICON_SIZE, Constants.ICON_LARGE);
                        editor.apply();
                        menuCatAdapter.notifyDataSetChanged();
                        menuItemAdapter.notifyDataSetChanged();
                        addonAdapter.notifyDataSetChanged();
                        noAddonAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        Button showColor = findViewById(R.id.open_color);
        showColor.setOnClickListener(v -> {
            ColorDialog colorDialog = ColorDialog.newInstance();
            colorDialog.show(getSupportFragmentManager(), null);
        });

//        showColor.setVisibility(View.GONE);
    }

    private void setUpRecyclerViews() {
        RealmResults<Table> tables = realm.where(Table.class).findAll();
        editableTableOrderAdapter = new EditableTableOrderAdapter(this, tables);
        tableRV.setAdapter(editableTableOrderAdapter);

        cat_list = new ArrayList<>();
        menuCatAdapter = new EditableCategoryAdapter(this, cat_list);
        categoryRecycler.setAdapter(menuCatAdapter);

        menuItemList = new ArrayList<>();
        menuItemAdapter = new EditableMenuItemAdapter(this, menuItemList, Constants.ITEM_TYPE_MENU, orderType);
        menuItemRecycler.setAdapter(menuItemAdapter);

        addonList = new ArrayList<>();
        addonAdapter = new EditableAddonAdapter(this, addonList, false, orderType);
        addonRecycler.setAdapter(addonAdapter);

        noAddonList = new ArrayList<>();
        noAddonAdapter = new EditableAddonAdapter(this, noAddonList, true, orderType);
        noAddonRecycler.setAdapter(noAddonAdapter);

        commonItemList = new ArrayList<>();
        commonItemAdapter = new EditableMenuItemAdapter(this, commonItemList, Constants.ITEM_TYPE_COMMON, orderType);
        commonItemRecycler.setAdapter(commonItemAdapter);

        int size = sharedPreferences.getInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
        switch (size) {
            case Constants.ICON_SMALL:
                ((RadioButton) findViewById(R.id.icon_option1)).setChecked(true);
                menuCatAdapter.notifyDataSetChanged();
                menuItemAdapter.notifyDataSetChanged();
                commonItemAdapter.notifyDataSetChanged();
                addonAdapter.notifyDataSetChanged();
                noAddonAdapter.notifyDataSetChanged();
                break;
            case Constants.ICON_MEDIUM:
                ((RadioButton) findViewById(R.id.icon_option2)).setChecked(true);
                menuCatAdapter.notifyDataSetChanged();
                menuItemAdapter.notifyDataSetChanged();
                commonItemAdapter.notifyDataSetChanged();
                addonAdapter.notifyDataSetChanged();
                noAddonAdapter.notifyDataSetChanged();
                break;
            case Constants.ICON_LARGE:
                ((RadioButton) findViewById(R.id.icon_option3)).setChecked(true);
                menuCatAdapter.notifyDataSetChanged();
                menuItemAdapter.notifyDataSetChanged();
                commonItemAdapter.notifyDataSetChanged();
                addonAdapter.notifyDataSetChanged();
                noAddonAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void callCategoryUpdateDialog(final int position, final MenuCategory category) {
 /*       final Dialog modifyCategory = new Dialog(this);
        modifyCategory.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modifyCategory.setContentView(R.layout.add_category_dialog);
        final TextView title = modifyCategory.findViewById(R.id.title);
        final EditText etCatName = modifyCategory.findViewById(R.id.etCatName);
        final Spinner color_spinner = modifyCategory.findViewById(R.id.color_spinner);
        Button btnAddCat = modifyCategory.findViewById(R.id.btnAddCat);
        View close = modifyCategory.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCategory.dismiss();
            }
        });*/
        CategoryEditDialog categoryEditDialog = CategoryEditDialog.newInstance();

        if (position == -1)
            categoryEditDialog.setTitle("Add Category");
        else {
            categoryEditDialog.setTitle("Update Category");
            categoryEditDialog.setName(category.getName());
        }

        categoryEditDialog.setCallback((dialog, nameET, colorId) -> {
            final ItemColor itemColor = realm.where(ItemColor.class).equalTo("id", colorId).findFirst();
           /* if(itemColor==null) {
                return;*/
            AtomicReference<MenuCategory> atomicReference = new AtomicReference<>(category);
            realm.executeTransaction(r -> {
                if (atomicReference.get() == null) {
                    Number maxId = r.where(MenuCategory.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key

                    atomicReference.set(r.createObject(MenuCategory.class, nextId));
                    String name = nameET.getText().toString().trim();
                    String capitalizedName = StringHelper.capitalizeEachWord(name);
                    atomicReference.get().setName(capitalizedName);
                    if (itemColor != null)
                        atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
//                    cat_list.add(atomicReference.get());
                    atomicReference.get().setItemposition(menuCatAdapter.getItemCount() - 1);
                    atomicReference.get().setPrintOrder(menuCatAdapter.getItemCount() - 1);
                    atomicReference.get().last_updated = new Date().getTime();
                } else {
                    String name = nameET.getText().toString().trim();
                    String capitalizedName = StringHelper.capitalizeEachWord(name);
                    atomicReference.get().setName(capitalizedName);
                    if (itemColor != null)
                        atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                    atomicReference.get().last_updated = new Date().getTime();
                }
                dialog.dismiss();
                if (position != -1)
                    menuCatAdapter.notifyItemChanged(position);
                else
                    menuCatAdapter.notifyItemInserted(menuCatAdapter.getItemCount() - 1);
            });

        });

        categoryEditDialog.show(getSupportFragmentManager(), null);
    }

    private void callTableEditDialog(final int position, final Table table) throws Exception {
      /*  Dialog modifyTable = new Dialog(this);
        modifyTable.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modifyTable.setContentView(R.layout.add_table_dialog_layout);
        final EditText tableNameET, colorET;
        final Button addTable;
        final TextView title = modifyTable.findViewById(R.id.title);
        View close = modifyTable.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyTable.dismiss();
            }
        });
        tableNameET = modifyTable.findViewById(R.id.table_name);
//        colorET = modifyTable.findViewById(R.id.color);
        addTable = modifyTable.findViewById(R.id.add_item);
        final StringBuilder price = new StringBuilder();*/

        TableEditDialog tableEditDialog = TableEditDialog.newInstance();

        if (position == -1) {
            tableEditDialog.setTitle("Add Table");
        } else {
            tableEditDialog.setTitle("Update Table");
            tableEditDialog.setName(table.getName());
//            colorET.setText(String.format("%.2f", table.getColor()));
        }

        tableEditDialog.setCallback((dialog, nameET) -> {
            realm.executeTransaction(r -> {
                if (table == null) {
                    Number maxId = r.where(Table.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key

                    Table newTable = r.createObject(Table.class, nextId);
                    String name = nameET.getText().toString().trim();
                    String capitalizedName = StringHelper.capitalizeEachWord(name);
                    newTable.setName(capitalizedName);
                    newTable.setLastUpdated(new Date().getTime());
                    editableTableOrderAdapter.notifyItemInserted(editableTableOrderAdapter.getItemCount() - 1);
//                    newTable.setColor(Integer.parseInt()colorET.getText().toString());
                } else {
                    String name = nameET.getText().toString().trim();
                    String capitalizedName = StringHelper.capitalizeEachWord(name);
                    table.setName(capitalizedName);
                    table.setLastUpdated(new Date().getTime());
                    editableTableOrderAdapter.notifyItemChanged(position);
                }
            });
            dialog.dismiss();
        });

        tableEditDialog.show(getSupportFragmentManager(), null);
    }

    private void callMenuItemEditDialog(EditableMenuItemAdapter menuItemAdapter, final int position, final MenuItem menuItem, final MenuItem parent, final String itemType) throws Exception {
        if (menuCatAdapter.categoryIndex == -1 && !itemType.equals(Constants.ITEM_TYPE_COMMON)) {
            Toast.makeText(this, "Select a category first", Toast.LENGTH_SHORT).show();
            return;
        }

        long itemId = (menuItem == null) ? -1 : menuItem.id;
        long parentId = (parent == null) ? -1 : parent.id;
        ItemEditDialog itemEditDialog = ItemEditDialog.newInstance(position, itemId, parentId, itemType, orderType);
        itemEditDialog.setCallback((dialog, pos, menuItemId, justCreated, isFlavour) -> {
            MenuItem newMenuItem = realm.where(MenuItem.class).equalTo("id", menuItemId).findFirst();
            if (justCreated) {
                if (itemType.equals(Constants.ITEM_TYPE_COMMON)) {
                    MenuCategory mc = realmDBHelper.getCategoryCommon();
                    newMenuItem.menuCategory = mc;
                    mc.menuItems.add(newMenuItem);
                    newMenuItem.itemPosition = commonItemAdapter.getItemCount() - 1;
                    commonItemAdapter.notifyItemInserted(commonItemAdapter.getItemCount() - 1);
                } else {
                    newMenuItem.itemPosition = menuItemAdapter.getItems().size() - 1;
//                    MenuCategory menuCategory = cat_list.get(menuCatAdapter.categoryIndex);
                    MenuCategory menuCategory = menuCatAdapter.getCurrentSelection();
                    newMenuItem.menuCategory = menuCategory;
                    if (!isFlavour) {
                        menuCategory.menuItems.add(newMenuItem);
                    }
                    menuItemAdapter.notifyItemInserted(menuItemAdapter.getItemCount() - 1);

                }
            } else {
                if (itemType.equals(Constants.ITEM_TYPE_COMMON))
                    commonItemAdapter.notifyItemChanged(position);
                else
                    menuItemAdapter.notifyItemChanged(position);
            }
            dialog.dismiss();
        });
        itemEditDialog.show(getSupportFragmentManager(), null);
    }


    private void callAddonGroupEditDialog(final int position, final Addon addon, final boolean noAddon) throws Exception {
        long parentId = -1;
        if (menuCatAdapter.categoryIndex == -1) {
            Toast.makeText(this, "Select a category first", Toast.LENGTH_SHORT).show();
            return;
        }

        AddonEditDialog addonEditDialog = AddonEditDialog.newInstance();
        if (addon != null && position != -1) {
            addonEditDialog.setName(addon.name);
            addonEditDialog.setPrice(addon.price + "");
            addonEditDialog.setTitle("Update Addon");

        } else {
            addonEditDialog.setTitle("New Addon");
        }
        if(addon!=null) {
            parentId = (addon.parent == null) ? -1 : addon.parent.id;
        }
        long finalParentId = parentId;
        addonEditDialog.setCallback((dialog, nameET, priceET, colorId) -> {
            realm.executeTransaction(r -> {
                final Addon parent = realm.where(Addon.class).equalTo("id", finalParentId).findFirst();
                final ItemColor itemColor = r.where(ItemColor.class).equalTo("id", colorId).findFirst();
                AtomicReference<Addon> atomicReference = new AtomicReference<>(addon);
                if (atomicReference.get() == null) {
                    Number maxId = r.where(Addon.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key
                    atomicReference.set(r.createObject(Addon.class, nextId));
                    MenuCategory menuCategory = menuCatAdapter.getCurrentSelection();
                    menuCategory.addons.add(atomicReference.get());
                    boolean isFlavour = false;
                    if (parent != null) {
                        parent.flavours.add(atomicReference.get());
                        isFlavour = true;
                    }
                    atomicReference.get().menuCategory = menuCategory;
                    atomicReference.get().last_updated = new Date().getTime();
                    if (noAddon) {
                        String name_ = nameET.getText().toString().trim();
                        String capitalizedName = StringHelper.capitalizeEachWord(name_);
                        atomicReference.get().name = capitalizedName;
                        atomicReference.get().price = 0;
                        atomicReference.get().isNoAddon = true;
                        noAddonList.add(atomicReference.get());
                        atomicReference.get().itemposition = noAddonList.size() - 1;
                        noAddonAdapter.notifyItemInserted(noAddonList.size() - 1);
                    } else {
                        String name = nameET.getText().toString().trim();
                        String capitalizedName = StringHelper.capitalizeEachWord(name);
                        atomicReference.get().name = capitalizedName;
                        double price = priceET.getText().toString().trim().isEmpty() ? 0 : Double.parseDouble(priceET.getText().toString());
                        atomicReference.get().price = price;
                        addonList.add(atomicReference.get());
                        atomicReference.get().parent = parent;
                        if(isFlavour){
                            atomicReference.get().parent.id=atomicReference.get().id;
                        }
                        atomicReference.get().itemposition = addonList.size() - 1;
                        addonAdapter.notifyItemInserted(addonList.size() - 1);
                    }
                    if (itemColor != null)
                        atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                } else {
                    String name = nameET.getText().toString().trim();
                    String capitalizedName = StringHelper.capitalizeEachWord(name);
                    atomicReference.get().name = capitalizedName;
                    atomicReference.get().last_updated = new Date().getTime();
                    if (noAddon)
                        noAddonAdapter.notifyItemChanged(position);
                    else {
                        double price = (priceET.getText().toString().trim().equals("")) ? 0 : Double.parseDouble(priceET.getText().toString().trim());
                        atomicReference.get().price = price;
                        addonAdapter.notifyItemChanged(position);
                    }
                    if (itemColor != null)
                        atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                }
                dialog.dismiss();
            });
        });
        addonEditDialog.show(getSupportFragmentManager(), null);
    }


    private void callAddonEditDialog(final int position, final Addon addon, final boolean noAddon) throws Exception {
        if (menuCatAdapter.categoryIndex == -1) {
            Toast.makeText(this, "Select a category first", Toast.LENGTH_SHORT).show();
            return;
        }
        AddonEditDialog addonEditDialog = AddonEditDialog.newInstance();
        if (addon != null && position != -1) {
            addonEditDialog.setName(addon.name);
            addonEditDialog.setPrice(addon.price + "");
            addonEditDialog.setTitle("Update Addon");
        } else {
            addonEditDialog.setTitle("New Addon");
        }

        addonEditDialog.setCallback((dialog, nameET, priceET, colorId) -> {
            realm.executeTransaction(r -> {
                final ItemColor itemColor = r.where(ItemColor.class).equalTo("id", colorId).findFirst();
                AtomicReference<Addon> atomicReference = new AtomicReference<>(addon);
                if (atomicReference.get() == null) {
                    Number maxId = r.where(Addon.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key
                    atomicReference.set(r.createObject(Addon.class, nextId));
                    MenuCategory menuCategory = menuCatAdapter.getCurrentSelection();
                    menuCategory.addons.add(atomicReference.get());


                    atomicReference.get().menuCategory = menuCategory;
                    atomicReference.get().last_updated = new Date().getTime();
                    if (noAddon) {
                        String name_ = nameET.getText().toString().trim();
                        String capitalizedName = StringHelper.capitalizeEachWord(name_);
                        atomicReference.get().name = capitalizedName;
                        atomicReference.get().price = 0;
                        atomicReference.get().isNoAddon = true;
                        noAddonList.add(atomicReference.get());
                        atomicReference.get().itemposition = noAddonList.size() - 1;
                        noAddonAdapter.notifyItemInserted(noAddonList.size() - 1);
                    } else {
                        String name = nameET.getText().toString().trim();
                        String capitalizedName = StringHelper.capitalizeEachWord(name);
                        atomicReference.get().name = capitalizedName;
                        double price = priceET.getText().toString().trim().isEmpty() ? 0 : Double.parseDouble(priceET.getText().toString());
                        atomicReference.get().price = price;
                        addonList.add(atomicReference.get());
                        atomicReference.get().itemposition = addonList.size() - 1;
                        addonAdapter.notifyItemInserted(addonList.size() - 1);
                    }
                    if (itemColor != null)
                        atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                } else {
                    String name = nameET.getText().toString().trim();
                    String capitalizedName = StringHelper.capitalizeEachWord(name);
                    atomicReference.get().name = capitalizedName;
                    atomicReference.get().last_updated = new Date().getTime();
                    if (noAddon)
                        noAddonAdapter.notifyItemChanged(position);
                    else {
                        double price = (priceET.getText().toString().trim().equals("")) ? 0 : Double.parseDouble(priceET.getText().toString().trim());
                        atomicReference.get().price = price;
                        addonAdapter.notifyItemChanged(position);
                    }
                    if (itemColor != null)
                        atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                }
                dialog.dismiss();
            });
        });
        addonEditDialog.show(getSupportFragmentManager(), null);
    }


    @Override
    public void onMenuItemEdit(EditableMenuItemAdapter menuItemAdapter, int position, MenuItem menuItem, String itemType) {
        try {
            callMenuItemEditDialog(menuItemAdapter, position, menuItem, null, itemType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMenuItem(EditableMenuItemAdapter menuItemAdapter, String itemType, MenuItem parent) {
        try {
            callMenuItemEditDialog(menuItemAdapter, -1, null, parent, itemType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMenu(EditableMenuItemAdapter editableMenuItemAdapter, final MenuItem menuItem, final int position) {
        CustomAlertDialog alertDialog = CustomAlertDialog.getInstance();
        alertDialog.setTitle("Delete Item");
        alertDialog.setMessage("Are you sure you want to delete this item?");
        alertDialog.setPositiveButton("Yes", dialog -> {
            dialog.dismiss();
            realm.executeTransaction(r -> {
                MenuCategory menuCategory = menuItem.menuCategory;
                EditableMenuItemAdapter menuItemAdapter = null;
                if (menuCategory != null) {
                    menuCategory.menuItems.remove(menuItem);
                    menuItemAdapter = editableMenuItemAdapter;
                } else {
                    menuItemAdapter = commonItemAdapter;
                }
                deleteMenuFlavours(menuItem);
                menuItem.deleteFromRealm();
                menuItemAdapter.notifyItemRemoved(position);
            });
        });
        alertDialog.setNegativeButton("No", dialog -> {
            dialog.dismiss();
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onMenuCatClick(int position, MenuCategory category) {
        RealmResults<MenuItem> menuItems = category.menuItems.where().notEqualTo("type", Constants.ITEM_TYPE_COMMON).sort("itemPosition", Sort.ASCENDING).findAll();
        menuItemAdapter.setItems(menuItems);
        menuItemAdapter.notifyDataSetChanged();

        addonList.clear();
        noAddonList.clear();
        for (Addon addon : category.addons) {
            if (addon.isNoAddon)
                noAddonList.add(addon);
            else
                addonList.add(addon);
        }

        Log.e(TAG, "-----------AddonList size: " + addonList.size() + "; NoAddonList size: " + noAddonList.size());
        addonAdapter.notifyDataSetChanged();
        noAddonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMenuCatLongClick(int position, MenuCategory category) {
        callCategoryUpdateDialog(position, category);
    }

    @Override
    public void addCategory() {
        callCategoryUpdateDialog(-1, null);
    }

    @Override
    public void deleteCategory(int position) {
        CustomAlertDialog alertDialog = CustomAlertDialog.getInstance();
        alertDialog.setTitle("Delete Item");
        alertDialog.setMessage("Are you sure you want to delete this item?");
        alertDialog.setPositiveButton("Yes", dialog -> {
            dialog.dismiss();
            deleteCategoryItem(position);
        });
        alertDialog.setNegativeButton("No", dialog -> {
            dialog.dismiss();
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClickEditPrintOrder() {
        EditPrintOrderDialog printOrderDialog = EditPrintOrderDialog.newInstance();
        printOrderDialog.show(getSupportFragmentManager(), null);
    }


    @Override
    public void onLongClickAddon(Addon addon, int position, boolean noAddon/*String itemType*/) {
        try {
            callAddonEditDialog(position, addon, noAddon);
//            callAddonEditFlavourDialog(position, addon, noAddon,itemType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNewAddon(boolean noAddon) {
        try {
            callAddonGroupEditDialog(-1, null, noAddon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCategoryItem(int position) {
        realm.executeTransaction(r -> {
            MenuCategory menuCategory = menuCatAdapter.getMenuCategories().get(position);
            menuCategory.deleteFromRealm();
        });

        menuCatAdapter.notifyDataSetChanged();
        RecyclerViewItemSelectionAfterLayoutUpdate.on(categoryRecycler);
    }

    private void deleteMenuFlavours(MenuItem menuItem) {
        if (menuItem.flavours.isEmpty())
            return;

        List<MenuItem> items = new ArrayList<>(menuItem.flavours);
        for (MenuItem m : items) {
            deleteMenuFlavours(m);
            menuItem.flavours.remove(m);
            m.deleteFromRealm();
        }
    }

    @Override
    public void onAddTable() {
        try {
            callTableEditDialog(-1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLongClickTable(int position, Table table) {
        try {
            callTableEditDialog(position, table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDelete(int position, Table table) {
        realm.executeTransaction(r -> {
            table.deleteFromRealm();
            editableTableOrderAdapter.notifyItemRemoved(position);
        });
    }

}
