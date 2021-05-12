package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.util.Log;

import com.foodciti.foodcitipartener.gson.AddonJson;
import com.foodciti.foodcitipartener.gson.MenuCategoryJson;
import com.foodciti.foodcitipartener.gson.MenuItemJson;
import com.foodciti.foodcitipartener.gson.TableJson;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MenuBackupHelper extends BackupHelper {
    private static final String TAG = "MenuBackupHelper";
    private Activity context;

    public MenuBackupHelper(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<File> backup() {
        List<File> files = new ArrayList<>();
        List<MenuCategoryJson> menuCategories = new ArrayList<>();
        List<MenuItemJson> menuItemJsons = new ArrayList<>();
        List<AddonJson> addonJsons = new ArrayList<>();
        List<TableJson> tableJsons = new ArrayList<>();
        for (Table table : realm.where(Table.class).findAll()) {
            TableJson tableJson = new TableJson();
            tableJson.setId(table.getId());
            tableJson.setName(table.getName());
            tableJsons.add(tableJson);
        }
        files.add(serialize(tableJsons, "tables.json", false));

        for (MenuItem item : realm.where(MenuItem.class).notEqualTo("type", "category").findAll()) {
            MenuItemJson menuItemJson = new MenuItemJson();
            processMenuItems(menuItemJson, item);
            menuItemJsons.add(menuItemJson);
        }
        files.add(serialize(menuItemJsons, "menuitems.json", false));

        for (Addon addon : realm.where(Addon.class).findAll()) {
            AddonJson addonJson = new AddonJson();
            addonJson.setId(addon.id);
            addonJson.setColor(addon.color);
            addonJson.setMenuCategoryId(addon.menuCategory.id);
            addonJson.setName(addon.name);
            addonJson.setPrice(addon.price);
            addonJson.setNoAddon(addon.isNoAddon);
            addonJsons.add(addonJson);
        }
        files.add(serialize(addonJsons, "addons.json", false));

        for (MenuCategory category : realm.where(MenuCategory.class).findAll()) {
            Log.e(TAG, "------------creating menucat--------------");
            MenuCategoryJson menuCategoryJson = new MenuCategoryJson();
            menuCategoryJson.setId(category.id);
            menuCategoryJson.setColor(category.color);
            menuCategoryJson.setName(category.getName());
            menuCategoryJson.setMenuItems(new ArrayList<Long>());
            menuCategoryJson.setAddons(new ArrayList<Long>());
            for (MenuItem item : category.menuItems) {
                menuCategoryJson.getMenuItems().add(item.id);
            }
            for (Addon addon : category.addons) {
                Log.e(TAG, "-----------------menuitem id: " + addon.id);
                menuCategoryJson.getAddons().add(addon.id);
            }
            menuCategories.add(menuCategoryJson);
        }

        files.add(serialize(menuCategories, "categories.json", false));
        return files;
    }

    @Override
    public List<File> backup(boolean append) {
        List<File> files = new ArrayList<>();
        List<MenuCategoryJson> menuCategories = new ArrayList<>();
        List<MenuItemJson> menuItemJsons = new ArrayList<>();
        List<AddonJson> addonJsons = new ArrayList<>();
        List<TableJson> tableJsons = new ArrayList<>();
        List<MenuItem> menuItemList;
       /* final Type MENUITEM = new TypeToken<List<MenuItemJson>>() {
        }.getType();
        List<MenuItemJson> oldMenuItems = deserialize(MENUITEM, "menuitems.json");
        if(oldMenuItems==null)
            oldMenuItems=new ArrayList<>();
        if(oldMenuItems.size()>0) {
            MenuItemJson menuItemJson = oldMenuItems.get(oldMenuItems.size() - 1);
            menuItemList = realm.where(MenuItem.class).notEqualTo("type", "category")
                    .and().greaterThan("id", menuItemJson.getId()).findAll();
        }
        else*/
        menuItemList = realm.where(MenuItem.class).findAll();

        if (menuItemList != null) {
            Log.e(TAG, "------------menuItemList size: " + menuItemList.size());
//                Log.e(TAG, "**********will write on and after: " + menuItemList.get(0).name);
            for (MenuItem item : menuItemList) {
                MenuItemJson itemJson = new MenuItemJson();
                processMenuItems(itemJson, item);
//                    oldMenuItems.add(itemJson);
                menuItemJsons.add(itemJson);
            }
//                files.add(serialize(oldMenuItems, "menuitems.json", append));
            files.add(serialize(menuItemJsons, "menuitems.json", append));
        } else
            Log.e(TAG, "--------menuItemList already latest-----------------");

        List<Addon> addonList;
        /*final Type ADDON = new TypeToken<List<AddonJson>>() {
        }.getType();
        List<AddonJson> addonJsonList = deserialize(ADDON, "addons.json");
        if(addonJsonList==null)
            addonJsonList=new ArrayList<>();
        if(addonJsonList.size()>0) {
            AddonJson lastItem = addonJsonList.get(addonJsonList.size() - 1);
            addonList = realm.where(Addon.class).greaterThan("id", lastItem.getId()).findAll();
        }
        else*/
        addonList = realm.where(Addon.class).findAll();
        if (addonList != null) {
            for (Addon addon : addonList) {
                AddonJson addonJson = new AddonJson();
                addonJson.setId(addon.id);
                addonJson.setColor(addon.color);
                addonJson.setMenuCategoryId(addon.menuCategory.id);
                addonJson.setName(addon.name);
                addonJson.setPrice(addon.price);
                addonJson.setNoAddon(addon.isNoAddon);
//                    addonJsonList.add(addonJson);
                addonJsons.add(addonJson);
            }
//                files.add(serialize(addonJsonList, "addons.json", append));
            files.add(serialize(addonJsons, "addons.json", append));
        } else
            Log.e(TAG, "--------addonslist already latest-----------------");

        List<Table> tableList;
        tableList = realm.where(Table.class).findAll();
        if (tableList != null) {
            for (Table t : tableList) {
                TableJson tableJson = new TableJson();
                tableJson.setId(t.getId());
                tableJson.setName(t.getName());
                tableJsons.add(tableJson);
            }
            files.add(serialize(tableJsons, "tables.json", append));
        } else
            Log.e(TAG, "------------- tablelist already latest-----------");


        List<MenuCategory> categoryList;
        /*final Type MENUCATEGORY = new TypeToken<List<MenuCategoryJson>>() {
        }.getType();
        List<MenuCategoryJson> menuCategoryJsons = deserialize(MENUCATEGORY, "categories.json");
        if(menuCategoryJsons==null)
            menuCategoryJsons=new ArrayList<>();
        if(menuCategoryJsons.size()>0) {
            MenuCategoryJson lastCategory = menuCategoryJsons.get(menuCategoryJsons.size() - 1);
            categoryList = realm.where(MenuCategory.class).greaterThan("id", lastCategory.getId()).findAll();
        }
        else*/
        categoryList = realm.where(MenuCategory.class).findAll();
        if (categoryList != null) {
            for (MenuCategory category : categoryList) {
                Log.e(TAG, "------------creating menucat--------------");
                MenuCategoryJson menuCategoryJson = new MenuCategoryJson();
                menuCategoryJson.setId(category.id);
                menuCategoryJson.setColor(category.color);
                menuCategoryJson.setName(category.getName());
                menuCategoryJson.setMenuItems(new ArrayList<Long>());
                menuCategoryJson.setAddons(new ArrayList<Long>());
                for (MenuItem item : category.menuItems) {
                    menuCategoryJson.getMenuItems().add(item.id);
                }
                for (Addon addon : category.addons) {
                    Log.e(TAG, "-----------------menuitem id: " + addon.id);
                    menuCategoryJson.getAddons().add(addon.id);
                }
//                    menuCategoryJsons.add(menuCategoryJson);
                menuCategories.add(menuCategoryJson);
            }

//                files.add(serialize(menuCategoryJsons, "categories.json", append));
            files.add(serialize(menuCategories, "categories.json", append));
        } else
            Log.e(TAG, "--------categoryList already latest-----------------");
        return files;
    }

    public Boolean restore() {
        AtomicReference<Boolean> success = new AtomicReference<>(false);
        realm.executeTransaction(r -> {
            try {
                List<?> objects = new ArrayList<>();
                final Type ADDON = new TypeToken<List<AddonJson>>() {
                }.getType();
                final Type MENUITEM = new TypeToken<List<MenuItemJson>>() {
                }.getType();
                final Type MENUCATEGORY = new TypeToken<List<MenuCategoryJson>>() {
                }.getType();
                final Type TABLES = new TypeToken<List<TableJson>>() {
                }.getType();
                List<AddonJson> addonJsons = deserialize(ADDON, "addons.json");
                List<MenuItemJson> menuItemJsons = deserialize(MENUITEM, "menuitems.json");
                List<MenuCategoryJson> menuCategoryJsons = deserialize(MENUCATEGORY, "categories.json");
                List<TableJson> tableJsons = deserialize(TABLES, "tables.json");

                if (tableJsons != null) {
                    Log.e(TAG, "--------------------tables: " + tableJsons.size());

                    for (TableJson t : tableJsons) {
                        Table table = r.where(Table.class).equalTo("id", t.getId()).findFirst();
                        if (table != null) {
                            if (table.getLastUpdated() < t.getLastupdated()) {
                                table.setName(t.getName());
                            }
                            continue;
                        }
                        table = r.createObject(Table.class, t.getId());
                        table.setName(t.getName());
                        table.setLastUpdated(t.getLastupdated());
                    }
                }

                if (addonJsons != null) {
                    Log.e(TAG, "--------------------addons: " + addonJsons.size());
                    for (AddonJson a : addonJsons) {
                        Addon addon = r.where(Addon.class).equalTo("id", a.getId()).findFirst();
                        if (addon != null) {
                            if (addon.last_updated < a.getLast_updated()) {
                                addon.name = a.getName();
                                addon.price = a.getPrice();
                                addon.color = a.getColor();
                                addon.isNoAddon = a.isNoAddon();
                                addon.last_updated = a.getLast_updated();
                            }
                            continue;
                        }
                        addon = r.createObject(Addon.class, a.getId());
                        addon.name = a.getName();
                        addon.price = a.getPrice();
                        addon.color = a.getColor();
                        addon.isNoAddon = a.isNoAddon();
                        addon.last_updated = a.getLast_updated();
                    }
                }

                if (menuItemJsons != null) {
                    Log.e(TAG, "--------------------menuitems: " + menuItemJsons.size());
                    for (MenuItemJson m : menuItemJsons) {
                        MenuItem menuItem = r.where(MenuItem.class).equalTo("id", m.getId()).findFirst();
                        if (menuItem != null) {
                            if (menuItem.last_updated < m.getLast_updated()) {
                                processMenuItemJson(m, menuItem);
                            }
                            continue;
                        }
                        menuItem = r.createObject(MenuItem.class, m.getId());
                        processMenuItemJson(m, menuItem);
                    }
                }

                if (menuCategoryJsons != null) {
                    Log.e(TAG, "--------------------categories: " + menuCategoryJsons.size());
                }
                int counter = 0;
                for (MenuCategoryJson c : menuCategoryJsons) {
                    MenuCategory menuCategory = r.where(MenuCategory.class).equalTo("id", c.getId()).findFirst();
                    if (menuCategory != null) {
                        if (menuCategory.last_updated < c.getLast_updated()) {
                            menuCategory.setName(c.getName());
                            menuCategory.color = c.getColor();
                            menuCategory.last_updated = c.getLast_updated();
                            menuCategory.setItemposition(counter);
                            menuCategory.setPrintOrder(counter);
                            counter++;
                            for (Long addonId : c.getAddons()) {
                                menuCategory.addons.add(r.where(Addon.class).equalTo("id", addonId).findFirst());
                            }
                            for (Long itemId : c.getMenuItems()) {
                                MenuItem menuItem = r.where(MenuItem.class).equalTo("id", itemId).findFirst();
                                if (menuItem != null)
                                    menuCategory.menuItems.add(menuItem);
                            }

                            List<Long> itemIds = new ArrayList<>();
                            for (MenuItemJson itemJson : menuItemJsons) {
                                if (itemJson.getMenuCategoryId() == c.getId())
                                    getMenuItemIdForMenuCategory(itemJson, itemIds);
                            }

                            Log.e(TAG, "--------------------itemIds: " + itemIds);

                            if (!itemIds.isEmpty()) {
                                for (Long id : itemIds) {
                                    MenuItem m = r.where(MenuItem.class).equalTo("id", id).findFirst();
                                    m.menuCategory = menuCategory;
                                }
                            }

                            itemIds.clear();
                            for (AddonJson aj : addonJsons) {
                                if (aj.getMenuCategoryId() == c.getId()) {
                                    itemIds.add(aj.getId());
                                }
                            }
                            if (!itemIds.isEmpty()) {
                                for (Long id : itemIds) {
                                    Addon a = r.where(Addon.class).equalTo("id", id).findFirst();
                                    a.menuCategory = menuCategory;
                                }
                            }
                        }
                        continue;
                    } else {
                        menuCategory = r.createObject(MenuCategory.class, c.getId());
                        menuCategory.setName(c.getName());
                        menuCategory.color = c.getColor();
                        menuCategory.setItemposition(counter);
                        menuCategory.setPrintOrder(counter);
                        counter++;
                    }
                    for (Long addonId : c.getAddons()) {
                        menuCategory.addons.add(r.where(Addon.class).equalTo("id", addonId).findFirst());
                    }
                    for (Long itemId : c.getMenuItems()) {
                        MenuItem menuItem = r.where(MenuItem.class).equalTo("id", itemId).findFirst();
                        if (menuItem != null)
                            menuCategory.menuItems.add(menuItem);
                    }

                    List<Long> itemIds = new ArrayList<>();
                    for (MenuItemJson itemJson : menuItemJsons) {
                        if (itemJson.getMenuCategoryId() == c.getId())
                            getMenuItemIdForMenuCategory(itemJson, itemIds);
                    }

                    Log.e(TAG, "--------------------itemIds: " + itemIds);

                    if (!itemIds.isEmpty()) {
                        for (Long id : itemIds) {
                            MenuItem m = r.where(MenuItem.class).equalTo("id", id).findFirst();
                            m.menuCategory = menuCategory;
                        }
                    }

                    itemIds.clear();
                    for (AddonJson aj : addonJsons) {
                        if (aj.getMenuCategoryId() == c.getId()) {
                            itemIds.add(aj.getId());
                        }
                    }
                    if (!itemIds.isEmpty()) {
                        for (Long id : itemIds) {
                            Addon a = r.where(Addon.class).equalTo("id", id).findFirst();
                            a.menuCategory = menuCategory;
                        }
                    }
                }
                success.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return success.get();
    }

    private void processMenuItemJson(MenuItemJson menuItemJson, MenuItem menuItem) {
        menuItem.name = menuItemJson.getName();
        menuItem.type = menuItemJson.getType();
        menuItem.price = menuItemJson.getPrice();
        menuItem.deliveryPrice = menuItemJson.getDeliveryPrice();
        menuItem.collectionPrice = menuItemJson.getCollectionPrice();
        menuItem.itemPosition = menuItemJson.getItemPosition();
        menuItem.color = menuItemJson.getColor();
        menuItem.last_updated = menuItemJson.getLast_updated();

        if (menuItemJson.getFlavours().isEmpty())
            return;
        for (MenuItemJson itemJson : menuItemJson.getFlavours()) {
            MenuItem item = realm.where(MenuItem.class).equalTo("id", itemJson.getId()).findFirst();
            if (item != null) {
                if (item.last_updated < menuItemJson.getLast_updated()) {
                    item.parent = menuItem;
                    menuItem.flavours.add(item);
                    processMenuItemJson(itemJson, item);
                }
                continue;
            }
            item = realm.createObject(MenuItem.class, itemJson.getId());
            item.parent = menuItem;
            menuItem.flavours.add(item);
            processMenuItemJson(itemJson, item);
        }

    }

    private void processMenuItems(MenuItemJson menuItemJson, MenuItem menuItem) {
        menuItemJson.setId(menuItem.id);
        menuItemJson.setName(menuItem.name);
        menuItemJson.setType(menuItem.type);
        menuItemJson.setPrice(menuItem.price);
        menuItemJson.setDeliveryPrice(menuItem.deliveryPrice);
        menuItemJson.setCollectionPrice(menuItem.collectionPrice);
        menuItemJson.setItemPosition(menuItem.itemPosition);
        menuItemJson.setColor(menuItem.color);
        menuItemJson.setLast_updated(menuItem.last_updated);
        if (menuItem.menuCategory != null)
            menuItemJson.setMenuCategoryId(menuItem.menuCategory.id);
        else
            menuItemJson.setMenuCategoryId(-1l);

        if (menuItem.parent != null)
            menuItemJson.setParentId(menuItem.parent.id);
        else
            menuItemJson.setParentId(-1l);

        if (menuItem.flavours.isEmpty()) {
            return;
        }
        for (MenuItem item : menuItem.flavours) {
            MenuItemJson itemJson = new MenuItemJson();
            menuItemJson.getFlavours().add(itemJson);
            processMenuItems(itemJson, item);
        }
    }

    private void getMenuItemIdForMenuCategory(MenuItemJson menuItemJson, List<Long> longList) {
        longList.add(menuItemJson.getId());
        if (menuItemJson.getFlavours().isEmpty()) {
            return;
        }
        for (MenuItemJson m : menuItemJson.getFlavours())
            getMenuItemIdForMenuCategory(m, longList);
    }
}
