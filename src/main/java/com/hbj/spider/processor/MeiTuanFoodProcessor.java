package com.hbj.spider.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 */
public class MeiTuanFoodProcessor implements PageProcessor {

    public static final String URL_CITY_CHANGE = "http://www.meituan.com/changecity/";
    public static final String URL_MEI_SHI = "http://\\w+\\.meituan.com/meishi/";
    public static final String URL_LIST = "http://blog\\.sina\\.com\\.cn/s/articlelist_1487828712_0_\\d+\\.html";



    public static final String URL_POST = "http://blog\\.sina\\.com\\.cn/s/blog_\\w+\\.html";

    private Site site = Site
            .me()
            .setDomain("www.meituan.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    private List<String> getCityFoorPageUrl(String cityUrl){
        List<String> cityFoorPageUrlList = new ArrayList<String>();
        for(int i = 1; i <= 1; i++){
            String cityFoorPageUrl = null;
            if(CityUrl.AN_SHAN.equals(cityUrl)){
                cityFoorPageUrl = "http://as.meituan.com/meishi/api/poi/getPoiList?uuid=9d9acda591074349a0d8.1523785161.1.0.0&platform=1&partner=126&originUrl=http%3A%2F%2Fas.meituan.com%2Fmeishi%2F&riskLevel=1&optimusCode=1&cityName=%E9%9E%8D%E5%B1%B1&cateId=0&areaId=0&sort=&dinnerCountAttrId=&page="+i+"&userId=0";
            }
            cityFoorPageUrlList.add(cityFoorPageUrl);
        }

        return cityFoorPageUrlList;
    }

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(URL_CITY_CHANGE).match()) {
            //获取城市没事url
            List<String> mtUrlList = page.getHtml().xpath("//div[@class=\"alphabet-city-area\"]").links().regex("//\\w+\\.meituan.com").all();
            List<String> foodListUrl = new ArrayList<String>();
            for (String url : mtUrlList) {
                String foodUrl = "http:" + url + "/meishi/";
                foodListUrl.add(foodUrl);
                //获取城市美食分页地址
                List<String> cityFoorPageUrlList = getCityFoorPageUrl(url);
                page.addTargetRequests(cityFoorPageUrlList);
                break;
            }

            page.addTargetRequests(foodListUrl);
        }else{
            //System.out.println(page.getHtml().toString());
            //page.putField("title", page.getHtml().xpath("//div[@class='list']//rl[@class='list-ul']"));
        }

        //获取从接口获取到的商家列表
        if(page.getUrl().regex("http://\\w+\\.meituan.com/meishi/api/poi/getPoiList").match()){
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            //System.out.println(page.getJson().toString());
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");

            System.out.println(page.getJson().jsonPath("data").toString());
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ------------------------------------666666---------------------------------------------");
            System.out.println(page.getJson().jsonPath("data").get());
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ------------------------------------777777---------------------------------------------");

            JSONObject foodListObj = JSON.parseObject(page.getJson().jsonPath("data").get());
            JSONArray poiInfoList = foodListObj.getJSONArray("poiInfos");
            if(poiInfoList != null && !poiInfoList.isEmpty()){
                List<String> shopUrlList = new ArrayList<String>();
                List<String> merchantCommentUrlList = new ArrayList<String>();
                for(int index = 0; index < poiInfoList.size(); index++){
                    JSONObject poInfoObj = poiInfoList.getJSONObject(index);
                    long poiId = poInfoObj.getLongValue("poiId");
                    System.out.println("poiId = " + poiId);
                    System.out.println(poInfoObj.toString());

                    //商家详情
                    shopUrlList.add("http://www.meituan.com/meishi/"+poiId+"/");

                    //评论--50条
                    String getMerchantCommentUrl = "http://www.meituan.com/meishi/api/poi/getMerchantComment?uuid=9d9acda591074349a0d8.1523785161.1.0.0&platform=1&partner=126&originUrl=http%3A%2F%2Fwww.meituan.com%2Fmeishi%2F1483550%2F&riskLevel=1&optimusCode=1&id="+poiId+"&userId=&offset=0&pageSize=50&sortType=1";
                    merchantCommentUrlList.add(getMerchantCommentUrl);
                    break;
                }

                page.addTargetRequests(shopUrlList);
                page.addTargetRequests(merchantCommentUrlList);
            }
        }

        //商家详情
        if(page.getUrl().regex("http://www.meituan.com/meishi/\\d+\\/").match()){
            String shopName = page.getHtml().xpath("//div[@class='content']//div[@class='details clear']//div[@class='d-left']//div[@class='address']").get();
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println("page.getHtml = " + page.getHtml());
            System.out.println("shopName = " + shopName);
            String phoneNum = page.getHtml().xpath("//div[@class='address']/p").regex("电话").get();
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println("shopName = " + phoneNum);
        }

        //评论
        if(page.getUrl().regex("http://www.meituan.com/meishi/api/poi/getMerchantComment").match()){
            JSONObject commentObj = JSON.parseObject(page.getJson().jsonPath("data").get());
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println(" ---------------------------------------------------------------------------------");
            System.out.println("comment = " + commentObj.toString());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new MeiTuanFoodProcessor()).addUrl("http://www.meituan.com/changecity/")
                .run();
    }

    private class CityUrl{
        private static final String AN_SHAN = "//as.meituan.com";
    }
}
