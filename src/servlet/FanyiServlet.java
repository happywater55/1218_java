package servlet;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import redis.clients.jedis.Jedis;
import utils.MyRedis;
import utils.com.baidu.translate.demo.TransApi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@javax.servlet.annotation.WebServlet(urlPatterns = "/FanyiServlet")
public class FanyiServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String query = request.getParameter("query");
        System.out.println("from:"+from+"\n"+"to:"+to+"\n"+"query:"+query+"\n");

        boolean code=false;
        Jedis jedis = new Jedis();
        if (jedis.get(query+from+to)==null){
            code = true;
        }

        //发送请求，解析字符串
        if (code){

            TransApi transApi = new TransApi();
            String resultApi= transApi.getTransResult(query,from,to);
            boolean resultCode = (resultApi != null);
            if(resultCode == true){
                System.out.println("发送请求！");
//                System.out.println("from:"+from+"\n"+"to:"+to+"\n"+"query:"+query+"\n");
                Gson gson = new Gson();
                HashMap<String, Object> map = gson.fromJson(resultApi, HashMap.class);
                ArrayList arrayList = (ArrayList) map.get("trans_result");
                LinkedTreeMap linkedTreeMap = (LinkedTreeMap) arrayList.get(0);
                String resultDst = (String) linkedTreeMap.get("dst");

                Map<String,Object> mapRes = new HashMap<String, Object>();
                mapRes.put("code","1");
                mapRes.put("dst",resultDst);
                String jsonStr = new Gson().toJson(mapRes);

                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.print(jsonStr);
                out.flush();
                out.close();
                //redis里面没有，设置一个key_value
                MyRedis myRedis = new MyRedis(query+from+to,resultDst);

            }
            else{
                System.out.println("error!");
                Map<String,Object> mapRes = new HashMap<String, Object>();
                mapRes.put("code","0");
                mapRes.put("dst",null);
                String jsonStr = new Gson().toJson(mapRes);

                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.print(jsonStr);
                out.flush();
                out.close();
            }

        }

        else {
            //直接从redis里面取出来
            System.out.println("redis中取出");
            String resultDst = jedis.get(query+from+to);
            Map<String,Object> mapRes = new HashMap<String, Object>();
            mapRes.put("code","1");
            mapRes.put("dst",resultDst);
            String jsonStr = new Gson().toJson(mapRes);

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print(jsonStr);
            out.flush();
            out.close();

        }

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request, response);
    }
}
