package com.namefilterhopper.namefilterhopper;

import java.util.logging.Logger;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Nameable;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

public class NameFilterHopper extends JavaPlugin implements Listener {
    public Logger log = Bukkit.getLogger();

    @Override
    public void onEnable() {
        log.info("NameFilterHopper Started");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if(isNamedHopper(holder)){
            String hopperName = getHopperName(((Hopper)holder));
            ItemStack movedItem = e.getItem().getItemStack();
            String itemName = movedItem.getType().name().toLowerCase();
            if(mustPrevent(itemName, hopperName)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHopperPush(InventoryMoveItemEvent e) {
        Inventory sourceInventory = e.getSource();
        Inventory destinationInventory = e.getDestination();
        InventoryHolder destinationHolder = destinationInventory.getHolder();
        InventoryHolder sourceHolder = sourceInventory.getHolder();
        if(isNamedHopper(destinationHolder)){
            String hopperName = getHopperName(((Hopper)destinationHolder));
            ItemStack movedItem = e.getItem();
            String itemName = movedItem.getType().name().toLowerCase();
            checkAndMove(itemName, hopperName, sourceInventory, destinationInventory, e);
        }
        if(isNamedHopper(sourceHolder)){
            String hopperName = getHopperName(((Hopper)sourceHolder));
            ItemStack movedItem = e.getItem();
            String itemName = movedItem.getType().name().toLowerCase();
            checkAndMove(itemName, hopperName, sourceInventory, destinationInventory, e);
        }
    }

    private void checkAndMove(
        String itemName, 
        String hopperName,
        Inventory sourceInventory,
        Inventory destinationInventory,
        InventoryMoveItemEvent e
    ) {
        if(mustPrevent(itemName, hopperName)){
            e.setCancelled(true);
            tryOtherSlots(hopperName, sourceInventory, destinationInventory);
        }
    }

    private void tryOtherSlots(String hopperName, Inventory sourceInventory, Inventory destinationInventory){
        ItemStack[] allItems = sourceInventory.getContents();
        java.util.HashSet<String> checkedItems = new java.util.HashSet<>();
        for (ItemStack stack : allItems) {
            if (stack == null) {
                continue;
            }
            String itemNameLower = stack.getType().name().toLowerCase();
            if (!checkedItems.add(itemNameLower)) {
                continue;
            }
            if (!mustPrevent(itemNameLower, hopperName)) {
                try {
                    // Only create a new ItemStack if we are transferring
                    ItemStack itemStack = new ItemStack(stack);
                    itemStack.setAmount(1);
                    destinationInventory.addItem(itemStack);
                    int newAmount = stack.getAmount() - 1;
                    stack.setAmount(Math.max(newAmount, 0));
                    break;
                } catch (IllegalArgumentException e) {
                    // do nothing
                }
            }
        }
    }

    private boolean mustPrevent(String itemName, String hopperName) {
        if (hopperName == null || hopperName.isEmpty()) return false;
        if (hopperName.indexOf('|') == -1) {
            if (hasExclusionInName(hopperName)) {
                return mustPreventSingleItemName(itemName, hopperName);
            } else {
                return !mustAllowSingleItemName(itemName, hopperName);
            }
        }
        String[] filterList = hopperName.split("\\|");
        boolean canAllow = false;
        boolean hasAllowableList = hasAllowables(filterList);
        for (String filterName : filterList) {
            if (mustPreventSingleItemName(itemName, filterName)) {
                return true;
            }
            if (hasAllowableList && !canAllow) {
                canAllow = mustAllowSingleItemName(itemName, filterName);
            } else {
                canAllow = true;
            }
        }
        return !canAllow;
    }

    private boolean hasAllowables(String[] filterList) {
        for (String filterName : filterList) {
            if (filterName.length() >= 2 && filterName.charAt(1) == ':') {
                char prefix = filterName.charAt(0);
                if (prefix == 'c' || prefix == 's') {
                    return true;
                }
            } else if (!filterName.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasExclusionInName(String filterName) {
        if (filterName.length() >= 2 && filterName.charAt(1) == ':') {
            char prefix = filterName.charAt(0);
            if (prefix == 'x' || prefix == 'n') {
                return true;
            }
        }
        return false;
    }

    private boolean mustPreventSingleItemName(String itemName, String singleItemName) {
        if (singleItemName.length() >= 2 && singleItemName.charAt(1) == ':') {
            String filterName = singleItemName.substring(2);
            char prefix = singleItemName.charAt(0);
            //is not exact item
            if (prefix == 'x') {
                if (itemName.equals(filterName)) {
                    return true;
                }
            }
            //does not contain name
            else if (prefix == 'n') {
                if (itemName.contains(filterName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean mustAllowSingleItemName(String itemName, String singleItemName) {
        if (singleItemName.length() >= 2 && singleItemName.charAt(1) == ':') {
            String filterName = singleItemName.substring(2);
            char prefix = singleItemName.charAt(0);
            //starts with name
            if (prefix == 's') {
                if (itemName.startsWith(filterName)){
                    return true;
                }
            }
            //contains name     
            else if (prefix == 'c') {
                if (itemName.contains(filterName)){
                    return true;
                }
            }   
        }        
        //is exact item
        else if(itemName.equals(singleItemName)){
            return true;
        }
        return false;
    }

    private String getHopperName(Hopper hopper){
        String name = ((Nameable)hopper).getCustomName();
        return name == null ? "" : name.toLowerCase();
    }

    private boolean isNamedHopper(InventoryHolder holder){
        if (holder instanceof Hopper) {
            Nameable block = ((Hopper)holder);
            if(Objects.nonNull(block.getCustomName())){
                return true;
            }
        }
        return false;
    }

}