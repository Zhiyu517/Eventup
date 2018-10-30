package external;

import entity.Item;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TicketMasterAPI {
    private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
    private static final String DEFAULT_KEYWORD = ""; // no restriction
    private static final String API_KEY = "J567tjKSTnLBtverE4kcesSjnBo8LAMS";
//    "name": "laioffer",
    //    "id": "12345",
    //    "url": "www.laioffer.com",
    //    ...
    //    "_embedded": {
    //	    "venues": [
    //	        {
    //		        "address": {
    //		           "line1": "101 First St,",
    //		           "line2": "Suite 101",
    //		           "line3": "...",
    //		        },
    //		        "city": {
    //		        	"name": "San Francisco"
    //		        }
    //		        ...
    //	        },
    //	        ...
    //	    ]
    //    }

    private String getAddress(JSONObject event) throws JSONException {    //因为address 在embedded里边在
        if (!event.isNull("_embedded")) {
            JSONObject embedded = event.getJSONObject("_embedded");
            if (!embedded.isNull("venues")) {
                JSONArray venues = embedded.getJSONArray("venues");
                for (int i = 0; i < venues.length(); i++) {
                    JSONObject venue = venues.getJSONObject(i);
                    StringBuilder sb = new StringBuilder();
                    if (!venue.isNull("address")) {
                        JSONObject address = venue.getJSONObject("address");

                        if (!address.isNull("line1")) {
                            sb.append(address.getString("line1"));
                        }
                        if (!address.isNull("line2")) {
                            sb.append(" ");
                            sb.append(address.getString("line2"));
                        }
                        if (!address.isNull("line3")) {
                            sb.append(" ");
                            sb.append(address.getString("line3"));
                        }
                    }
                    if (!venue.isNull("city")) {
                        JSONObject city = venue.getJSONObject("city");

                        if (!city.isNull("name")) {
                            sb.append(" ");
                            sb.append(city.getString("name"));
                        }
                    }
                    return sb.toString();
                }
            }
        }
        return "";
    }


    // {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
    private String getImageUrl(JSONObject event) throws JSONException {
        if (!event.isNull("images")) {
            JSONArray images = event.getJSONArray("images");

            for (int i = 0; i < images.length(); ++i) {
                JSONObject image = images.getJSONObject(i);

                if (!image.isNull("url")) {
                    return image.getString("url");
                }
            }
        }

        return "";
    }

    // {"classifications" : [{"segment": {"name": "music"}}, ...]}
    private Set<String> getCategories(JSONObject event) throws JSONException {
        Set<String> categories = new HashSet<>();
        if (!event.isNull("classifications")) {
            JSONArray classifications = event.getJSONArray("classifications");
            for (int i = 0; i < classifications.length(); i++) {
                JSONObject classification = classifications.getJSONObject(i);
                if (!classification.isNull("segment")) {
                    JSONObject segment = classification.getJSONObject("segment");

                    if (!segment.isNull("name")) {
                        categories.add(segment.getString("name"));
                    }
                }
            }
        }
        return categories;
    }

    // Convert JSONArray to a list of item objects.
    private List<Item> getItemList(JSONArray events) throws JSONException {
        List<Item> itemList = new ArrayList<>();

        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);
            Item.ItemBuilder builder = new Item.ItemBuilder();

            if (!event.isNull("name")) {
                builder.setName(event.getString("name"));
            }

            if (!event.isNull("id")) {
                builder.setItemId(event.getString("id"));
            }

            if (!event.isNull("url")) {
                builder.setUrl(event.getString("url"));
            }
            if (!event.isNull("rating")) {
                builder.setRating(event.getDouble("rating"));
            }
            if (!event.isNull("distance")) {
                builder.setDistance(event.getDouble("distance"));
            }
            builder.setCategories(getCategories(event));
            builder.setAddress(getAddress(event));
            builder.setImageUrl(getImageUrl(event));

            itemList.add(builder.build());
        }


        return itemList;
    }





    public List<Item> search(double lat, double lan, String keyword) {                                   //与Ticketmaster 链接 返回一个json
        if (keyword == null) {
         keyword = DEFAULT_KEYWORD;
        }

        try {
            keyword = java.net.URLEncoder.encode(keyword, "UTF-8");  //把数据编程电脑可读的 变成8字节
        } catch (Exception e) {
            e.printStackTrace();
        }
        String geoHash = GeoHash.encodeGeohash(lat, lan, 8);
        String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
            int responseCode = connection.getResponseCode();    //200 404 etc
            System.out.println("33");
            System.out.println(connection);
            System.out.println(query);
            System.out.println(responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputline;
            StringBuilder response = new StringBuilder();
            while ((inputline = in.readLine()) != null) {
                response.append(inputline);
            }
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            if (obj.isNull("_embedded")) {     //master api 里边可以知道东西都在embedded 里它是一个container
                return new ArrayList<>();
            }
            JSONObject embedded = obj.getJSONObject("_embedded");
            JSONArray events = embedded.getJSONArray("events");
            return getItemList(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void queryAPI(double lat, double lon) {
        List<Item> events = search(lat, lon, null);
        try {
            for (int i = 0; i < events.size(); i++) {
                Item event = events.get(i);
                System.out.println("64");
                System.out.println(event.toJSONObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        TicketMasterAPI tmApi = new TicketMasterAPI();
        // Mountain View, CA
        // tmApi.queryAPI(37.38, -122.08);
        // London, UK
        // tmApi.queryAPI(51.503364, -0.12);
        // Houston, TX
        tmApi.queryAPI(29.682684, -95.295410);
    }


}