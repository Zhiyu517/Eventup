package entity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class Item {
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getUrl() {
        return url;
    }

    public double getDistance() {
        return distance;
    }

    private String name;
    private double rating;
    private String address;
    private Set<String> categories;
    private String imageUrl;
    private String url;
    private double distance;

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("item_id", itemId);
            obj.put("name", name);
            obj.put("rating", rating);
            obj.put("address", address);
            obj.put("categories", new JSONArray(categories));
            obj.put("image_url", imageUrl);
            obj.put("url", url);
            obj.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (itemId == null) {
            if (other.itemId != null)
                return false;
        } else if (!itemId.equals(other.itemId))
            return false;
        return true;
    }


    public static class ItemBuilder {          //必须static 因为是inner class  否则下边调用他的时候不实例化就行能调用
        private String itemId;
        private String name;
        private double rating;

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setCategories(Set<String> categories) {
            this.categories = categories;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        private String address;
        private Set<String> categories;
        private String imageUrl;
        private String url;
        private double distance;

        public Item build() {
            return new Item(this);
        }
    }



    private Item(ItemBuilder builder) {        //private 的意思是只能通过itembuilder 创建   体现了封装
        this.itemId = builder.itemId;
        this.name = builder.name;
        this.rating = builder.rating;
        this.address = builder.address;
        this.categories = builder.categories;
        this.imageUrl = builder.imageUrl;
        this.url = builder.url;
        this.distance = builder.distance;
    }



}


