package algorithm;

import java.util.*;
import java.util.Map.Entry;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


public class GeoRecommentation {
    public List<Item> recommendItems (String userId, double lat, double lon) {
        List<Item> recommednedItems = new ArrayList<>();
        DBConnection conn = DBConnectionFactory.getConnection();

        //step 1 get all favorited items
        Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);
        System.out.println("favor");
        System.out.println(favoriteItemIds.toString());
        //step 2 get all categories of favorited items, sort by count
        Map<String, Integer> allCategories = new HashMap<>();
        for (String itemId : favoriteItemIds) {
            Set<String> categories = conn.getCategories(itemId);
            System.out.println(categories.toString());
            System.out.println("qqqqq");
            for (String category : categories) {
                if (allCategories.containsKey(category)) {
                    allCategories.put(category, allCategories.get(category) + 1);
                } else {
                    allCategories.put(category,1);
                }
            }
        }
        List<Entry<String, Integer>> categoryList = new ArrayList<Entry<String, Integer>>(allCategories.entrySet());
        Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return Integer.compare(o2.getValue(), o1.getValue());
            }
        });
        //step 3. do search based on category, filter out favorited events, sort by distance
        Set<Item> visitedItems = new HashSet<>();
        for (Entry<String, Integer> category : categoryList) {
            List<Item> items = conn.searchItems(lat, lon, category.getKey());
            List<Item> filterItems = new ArrayList<>();
            for (Item item : items) {
                if (!favoriteItemIds.contains(item.getItemId()) && !visitedItems.contains(item)) {
                    filterItems.add(item);
                }
            }
            Collections.sort(filterItems, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return Double.compare(o1.getDistance(), o2.getDistance());
                }
            });
            visitedItems.addAll(items);
            recommednedItems.addAll(filterItems);
        }
        return recommednedItems;
    }


}
