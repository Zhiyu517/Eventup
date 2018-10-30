package rpc;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

@WebServlet(name = "Jup", urlPatterns = "/search")    //记得写后边的路径
public class SerachItem extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        PrintWriter out = response.getWriter();
//        String username = "";
//        String password = "";
//        if (request.getParameter("username") != null) {
//            username = request.getParameter("username");       //得到url中username = 后边的东西
//        }
//        if (request.getParameter("password") != null) {
//            password = request.getParameter("password");        ////password = 后边的东西
//        }
//        JSONObject obj = new JSONObject();                          //建立一个json的object
//            try {
//                obj.put("username", username);                      //将key 和 value 存入这个obj中  obj类似于map
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            out.println(obj);
//
//        JSONArray array = new JSONArray();                          //建立一个json array
//        try {
//            array.put(new JSONObject().put("username", username));  // 用 put方法 将obj存入array中
//            array.put(new JSONObject().put("password", password));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        out.println(array);
//        out.close();
//    }
//}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        PrintWriter out = response.getWriter();
//        String username = "";
//        String password = "";
//        if (request.getParameter("username") != null) {
//            username = request.getParameter("username");       //得到url中username = 后边的东西
//        }
//        if (request.getParameter("password") != null) {
//            password = request.getParameter("password");        ////password = 后边的东西
//        }
//        JSONObject obj = new JSONObject();                          //建立一个json的object
//        try {
//            obj.put("username", username);                      //将key 和 value 存入这个obj中  obj类似于map
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        out.println(obj);
//
//        JSONArray array = new JSONArray();                          //建立一个json array
//        try {
//            array.put(new JSONObject().put("username", username));  // 用 put方法 将obj存入array中
//            array.put(new JSONObject().put("password", password));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        out.println(array);
//        out.close();

//        response.setContentType("application/json");
        JSONArray array = new JSONArray();
        try {
            String userId = request.getParameter("user_id");
            double lat = Double.parseDouble(request.getParameter("lat"));
            double lon = Double.parseDouble(request.getParameter("lon"));
            String keyword = request.getParameter("term");

//            TicketMasterAPI tmAPI = new TicketMasterAPI();
//            List<Item> items = tmAPI.search(lat, lon, keyword);
            DBConnection connection = DBConnectionFactory.getConnection();
            List<Item> items = connection.searchItems(lat,lon,keyword);
            connection.close();
            Set<String> favorite = connection.getFavoriteItemIds(userId);
            for (Item item : items) {
                JSONObject obj = item.toJSONObject();
                obj.put("favorite", favorite.contains(item.getItemId()));
                array.put(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        RpcHelper.writeJsonArray(response,array);


    }
}