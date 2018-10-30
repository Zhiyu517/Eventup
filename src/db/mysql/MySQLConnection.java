package db.mysql;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MySQLConnection implements DBConnection {
    private Connection conn;
    public MySQLConnection() {                                  //一调用这个class 就打开链接
        try {
            Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
            conn = DriverManager.getConnection(MySQLDBUtil.URL);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void close() {                           //关闭连接
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setFavoriteItems(String userId, List<String> itemIds) {
        System.out.println("sss");
        if (conn == null) {
            return;
        }
        try {
            System.out.println("qqqq");
            String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            for (String itemId : itemIds) {
                stmt.setString(1,userId);
                stmt.setString(2,itemId);
                stmt.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void unsetFavoriteItems(String userId, List<String> itemIds) {
        if (conn == null) {
            return;
        }
        try {
            String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (String itemId : itemIds) {
                stmt.setString(1,userId);
                stmt.setString(2,itemId);
                stmt.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getFavoriteItemIds(String userId) {
        if (conn == null) {
            return new HashSet<>();
        }

        Set<String> favoriteItemIds = new HashSet<>();
        try {
            String sql = "SELECT item_id FROM history WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                favoriteItemIds.add(itemId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return favoriteItemIds;

    }

    @Override
    public Set<Item> getFavoriteItems(String userId) {
        if (conn == null) {
            return new HashSet<>();
        }
        Set<Item> favoriteItems = new HashSet<>();
        Set<String> itemIds = getFavoriteItemIds(userId);

        try {
            String sql = "SELECT * FROM items WHERE item_id = ?";   // * 表示所有的col 也可也改成 user_id 等等   where 后边的意思是 根据什么找
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (String itemId : itemIds) {
                stmt.setString(1, itemId);
                ResultSet rs = stmt.executeQuery();
                Item.ItemBuilder builder = new Item.ItemBuilder();
                while (rs.next()) {
                    builder.setItemId(rs.getString("item_id"));
                    builder.setName(rs.getString("name"));
                    builder.setAddress(rs.getString("address"));
                    builder.setImageUrl(rs.getString("image_url"));
                    builder.setUrl(rs.getString("url"));
                    builder.setCategories(getCategories("itemId"));
                    builder.setDistance(rs.getDouble("distance"));
                    builder.setRating(rs.getDouble("rating"));
                }
                favoriteItems.add(builder.build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    @Override
    public Set<String> getCategories(String itemId) {
        if (conn == null) {
            return new HashSet<>();
        }
        Set<String> categories = new HashSet<>();
        try {
            String sql = "SELECT category from categories WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }



    @Override
    public List<Item> searchItems(double lat, double lon, String term) {
        TicketMasterAPI tmAPI = new TicketMasterAPI();
        List<Item> items = tmAPI.search(lat, lon, term);
        for (Item item : items) {
            System.out.println("aasdasd");
            saveItem(item);
        }
        System.out.println("aasdasd");
        return items;
    }

    @Override
    public void saveItem(Item item) {
        System.out.println("xxx");
        if (conn == null) {
            System.out.println("not connection");
            return;
        }
        try {
            String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)"; //ignor 的意思是如果在的话就忽视 不加了  还有update可以覆盖
//            String sql = "INSERT IGNRO INTO items(sth, sth) VALUES(?,?)"
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getItemId());
            stmt.setString(2,item.getName());
            stmt.setDouble(3,item.getRating());
            stmt.setString(4,item.getAddress());
            stmt.setString(5,item.getImageUrl());
            stmt.setString(6,item.getUrl());
            stmt.setDouble(7,item.getDistance());
            stmt.execute();

            sql = "INSERT IGNORE INTO categories VALUES (?,?)";
            stmt = conn.prepareStatement(sql);
            for (String category : item.getCategories()) {
                stmt.setString(1, item.getItemId());
                stmt.setString(2, category);
                stmt.execute();
            }
            System.out.println("aaaaa");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFullname(String userId) {
        if (conn == null) {
            return null;
        }
        String name = "";
        try {
            String sql = "SELECT first_name, last_name from users WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = String.join(" ", rs.getString("first_name"), rs.getString("last_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    @Override
    public boolean verifyLogin(String userId, String password) {
        if (conn == null) {
            return false;
        }
        try {
            String sql = "SELECT user_id from users WHERE user_id = ? and password = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
