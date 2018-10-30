package rpc;

import algorithm.GeoRecommentation;
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
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "RecommendItem", urlPatterns = "/recommendation")
public class RecommendItem extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        PrintWriter out = response.getWriter();
//        JSONArray array = new JSONArray();
//
//        try {
//            array.put(new JSONObject().put("name", "abcd").put("address", "san francisco").put("time", "01/10/2017")); //json obj 可以连续put 因为返回值是本身
//            array.put(new JSONObject().put("name", "asww").put("address", "LA").put("time", "01/10/2ss17"));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        out.println(array);
//        out.close();
        String userId = request.getParameter("user_id");
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        GeoRecommentation recommentation = new GeoRecommentation();
        System.out.println("ss");
        List<Item> items = recommentation.recommendItems(userId, lat, lon);
        System.out.println(items.toString());
        JSONArray result = new JSONArray();
        try {
            for (Item item : items) {
                result.put(item.toJSONObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RpcHelper.writeJsonArray(response,result);
    }
}
// key must be a string

