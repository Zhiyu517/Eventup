//package rpc;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebServlet(name = "ItemHistory")
//public class ItemHistory extends HttpServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//    }
//}
package rpc;

        import db.DBConnection;
        import db.DBConnectionFactory;
        import entity.Item;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import javax.servlet.ServletException;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Set;

//@WebServlet(name = "Jup", urlPatterns = "/history");
@WebServlet(name = "ItemHistory", urlPatterns = "/history")
public class ItemHistory extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSONObject input = RpcHelper.readJsonObject(request);
            String userId = input.getString("user_id");

            JSONArray array = input.getJSONArray("favorite");
            List<String> itemIds = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                itemIds.add(array.get(i).toString());
            }
            System.out.println(itemIds.toString());
            DBConnection conn = DBConnectionFactory.getConnection();
            conn.setFavoriteItems(userId, itemIds);
            conn.close();

            RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("user_id");
        JSONArray array = new JSONArray();
        DBConnection conn = DBConnectionFactory.getConnection();
        Set<Item> items = conn.getFavoriteItems(userId);

        for (Item item : items) {
            JSONObject obj = item.toJSONObject();
            array.put(obj);
            try {
                obj.append("favorite", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RpcHelper.writeJsonArray(response,array);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws SecurityException, IOException {
        try {
            //delete
//            {
//                'user_id':'11a1',
//                      'favorite' : [
//                      'item_id1',
//                      'item_id2'
//                  ]
//            }

            JSONObject input = RpcHelper.readJsonObject(request);
            String userId = input.getString("user_id");

            JSONArray array = input.getJSONArray("favorite");
            List<String> itemIds = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                itemIds.add(array.get(i).toString());
            }
            DBConnection conn = DBConnectionFactory.getConnection();
            conn.unsetFavoriteItems(userId, itemIds);
            conn.close();

            RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


