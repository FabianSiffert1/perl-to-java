package com.sippy.wrapper.parent;

import com.sippy.wrapper.parent.database.DatabaseConnection;
import com.sippy.wrapper.parent.database.dao.TnbDao;
import com.sippy.wrapper.parent.request.JavaTestRequest;
import com.sippy.wrapper.parent.response.JavaTestResponse;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

@Stateless
public class WrappedMethods {

  private static final Logger LOGGER = LoggerFactory.getLogger(WrappedMethods.class);

  @EJB DatabaseConnection databaseConnection;

  @RpcMethod(name = "javaTest", description = "Check if everything works :)")
  public Map<String, Object> javaTest(JavaTestRequest request) {
    JavaTestResponse response = new JavaTestResponse();

    int count = databaseConnection.getAllTnbs().size();

    LOGGER.info("the count is: " + count);

    response.setId(request.getId());
    String tempFeeling = request.isTemperatureOver20Degree() ? "warm" : "cold";
    response.setOutput(
        String.format(
            "%s has a rather %s day. And he has %d tnbs", request.getName(), tempFeeling, count));

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");
    jsonResponse.put("faultString", "Method success");
    jsonResponse.put("something", response);

    return jsonResponse;
  }

  public List<JSONObject> getTnbList(JSONObject json){
    JSONObject params = json.optJSONObject("params");

    LOGGER.info("Fetching TNB list from the database");
     List<TnbDao> allTnbs = databaseConnection.getAllTnbs();

     //this feels wrong, but getTnb() returns string
    String number = params.getNumber("number").toString();


    final String[] dbTnb = new String[1];

    if(number != null && allTnbs != null){
      allTnbs.forEach(tnb -> {
        if (tnb.getTnb() == number){
        dbTnb[0] = tnb.getTnb();
        }
      });
    }

   final List<JSONObject> tnbList = new ArrayList<>();

  JSONObject deutscheTelekom = new JSONObject();

      try {
          deutscheTelekom.put("number",  "D001");
      } catch (JSONException e) {
          throw new RuntimeException(e);
      }

      try {
          deutscheTelekom.put("name", "Deutsche Telekom");
      } catch (JSONException e) {
          throw new RuntimeException(e);
      }

      if(dbTnb != null && dbTnb[0] == "D001") {
      tnbList.add(deutscheTelekom);
    }

    allTnbs.forEach( tnb ->
    {
      if(tnb.getTnb().matches("(D146|D218|D248)")){
        //next;
      };
      // push to tnbList
    });

    //sort tnbList

    return tnbList;
  }

}
