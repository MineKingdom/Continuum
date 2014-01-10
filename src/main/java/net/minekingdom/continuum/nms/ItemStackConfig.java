package net.minekingdom.continuum.nms;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainerItem;
import me.dpohvar.powernbt.nbt.NBTTagCompound;
import me.dpohvar.powernbt.nbt.NBTTagDatable;
import me.dpohvar.powernbt.nbt.NBTTagList;

import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemStackConfig implements ConfigurationSerializable {

	private int type = 0;
    private int amount = 0;
    private short durability = 0;
	private NBTTagCompound	tag;

    @Utility
    protected ItemStackConfig() {}
    
    public ItemStackConfig(ItemStack itemstack) {
    	this.type = itemstack.getTypeId();
    	this.amount = itemstack.getAmount();
    	this.durability = itemstack.getDurability();
    	
    	try {
	    	NBTContainerItem nbt = new NBTContainerItem(itemstack);
	    	this.tag = nbt.getTag();
    	} catch (Throwable t) {
    	}
    }
    
    public ItemStackConfig(Material type, int amount, short damage, NBTTagCompound tag) {
		this.type = type.getId();
		this.amount = amount;
		this.durability = damage;
		this.tag = tag;
	}

	public Material getType() {
        Material material = Material.getMaterial(type);
        return material == null ? Material.AIR : material;
    }
	
	@Utility
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("type", getType().name());

        if (durability != 0) {
            result.put("damage", durability);
        }

        if (amount != 1) {
            result.put("amount", amount);
        }
        
        if (tag != null && tag.size() > 0) {
        	addTagRecur(tag, "nbt", result);
        }

        return result;
    }
	
	private void addTagRecur(NBTBase tag, String name, Map<String, Object> result) {
		if (tag instanceof NBTTagCompound) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, NBTBase> entry : ((NBTTagCompound) tag).asMap().entrySet()) {
				addTagRecur(entry.getValue(), entry.getKey(), map);
        	}
			result.put(name, map);
		} else if (tag instanceof NBTTagList) {
			List<Object> list = new LinkedList<Object>();
			for (NBTBase t : ((NBTTagList) tag).asList()) {
				addTagRecurList(t, list);
			}
		} else if (tag instanceof NBTTagDatable) {
			result.put(name, ((NBTTagDatable) tag).get());
		}
	}

    private void addTagRecurList(NBTBase tag, List<Object> result) {
    	if (tag instanceof NBTTagCompound) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, NBTBase> entry : ((NBTTagCompound) tag).asMap().entrySet()) {
				addTagRecur(entry.getValue(), entry.getKey(), map);
        	}
			result.add(map);
		} else if (tag instanceof NBTTagList) {
			List<Object> list = new LinkedList<Object>();
			for (NBTBase t : ((NBTTagList) tag).asList()) {
				addTagRecurList(t, list);
			}
		} else if (tag instanceof NBTTagDatable) {
			result.add(((NBTTagDatable) tag).get());
		}
	}
    
    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(getType(), amount, durability);
        
        if (stack.getType().equals(Material.AIR)) {
    		return stack;
    	}
        
        try {
        	stack = CraftItemStack.asCraftCopy(stack);
	    	if (tag != null && tag.size() > 0) {
	    		NBTContainerItem nbt = new NBTContainerItem(stack);
	    		nbt.setCustomTag(tag.clone());
	    	}
    	} catch (Throwable t) {
    		t.printStackTrace();
		}
        
        return new ItemStack(getType(), amount, durability);
    }
    
    public void setNBT(ItemStack itemstack) {
    	
    	try {
	    	if (tag != null && tag.size() > 0) {
	    		NBTContainerItem nbt = new NBTContainerItem(itemstack);
	    		nbt.setTag(tag);
	    	}
    	} catch (Throwable t) {
    		t.printStackTrace();
		}
    }

	public static ItemStackConfig deserialize(Map<String, Object> args) {
        Material type = Material.getMaterial((String) args.get("type"));
        short damage = 0;
        int amount = 1;

        if (args.containsKey("damage")) {
            damage = ((Number) args.get("damage")).shortValue();
        }

        if (args.containsKey("amount")) {
            amount = (Integer) args.get("amount");
        }
        
    	NBTTagCompound tag = new NBTTagCompound();
    	handleTagMap(args.get("nbt"), "tag", tag);
    	
        ItemStackConfig result = new ItemStackConfig(type, amount, damage, tag);

        return result;
    }
	
	private static void handleTagMap(Object o, String name, NBTTagCompound compound) {
		if (o instanceof Map) {
			NBTTagCompound c = new NBTTagCompound();
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
				handleTagMap(entry.getValue(), entry.getKey().toString(), c);
			}
			compound.set(name, c);
		} else if (o instanceof List) {
			NBTTagList c = new NBTTagList();
			for (Object entry : (List<?>) o) {
				handleTagList(entry, c);
			}
			compound.set(name, c);
		} else if (o != null) {
			try {
				compound.set(name, o);
			} catch (Exception ex) {
			}
		}
	}

	private static void handleTagList(Object o, NBTTagList list) {
		if (o instanceof Map) {
			NBTTagCompound c = new NBTTagCompound();
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
				handleTagMap(entry.getValue(), entry.getKey().toString(), c);
			}
			list.add(c);
		} else if (o instanceof List) {
			NBTTagList c = new NBTTagList();
			for (Object entry : (List<?>) o) {
				handleTagList(entry, c);
			}
			list.add(c);
		} else if (o != null) {
			try {
				list.add(o);
			} catch (Exception ex) {
			}
		}
	}
	
}