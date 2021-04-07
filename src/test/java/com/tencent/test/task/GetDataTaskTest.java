package com.tencent.test.task;

import com.tencent.test.entity.CourseType;
import com.tencent.test.model.CourseModel;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
class GetDataTaskTest {

    private Logger logger = LoggerFactory.getLogger(GetDataTaskTest.class);

    private String baseUrl = "https://ke.qq.com/";

    @Test
    void getData() {
        logger.info("开始获取课程数据......");
        ThreadPoolExecutor fetchAddressPool =
                new ThreadPoolExecutor(50, 50, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

        try {
            long startTime = System.currentTimeMillis();
            List<String> firstTypeUrls = getFirstType(baseUrl);
            logger.info("一级分类总数：" + firstTypeUrls.size());
            List<String> secondTypeUrls = getSecondType(firstTypeUrls);
            logger.info("二级分类总数：" + secondTypeUrls.size());
            List<CourseType> courseTypeList = getThirdType(secondTypeUrls);
            logger.info("三级分类总数：" + courseTypeList.size());

            List<Future<List<CourseModel>>> futureList = new LinkedList<>();

            //获取三级分类下的课程
            courseTypeList.stream().forEach(courseType -> {
                Future<List<CourseModel>> future = fetchAddressPool.submit(() -> {
                    List<CourseModel> courseList = new LinkedList<>();
                    getCourseList(courseType.getUrl(), courseList);

                    logger.info("三级分类的名称:" + courseType.getTypeName() + "， 课程总数：" + courseList.size());
                    courseType.setCourseNum(courseList.size());
                    //设置课程分类名称
                    courseList.stream().forEach(course -> {
                        course.setCourseTypeName(courseType.getTypeName());
                    });
                    return courseList;
                });

                futureList.add(future);
            });

            List<CourseModel> allCourseList = new LinkedList<>();
            futureList.stream().forEach(future -> {
                try {
                    List<CourseModel> courseList = future.get();

                    allCourseList.addAll(courseList);
                } catch (Exception e) {
                    logger.error("获取分类课程失败：");
                    e.printStackTrace();
                }
            });

            logger.info("课程总数：" + allCourseList.size());
            long endTime = System.currentTimeMillis();
            logger.info("获取课程数据耗时：" + (endTime - startTime));

        }catch (Exception e){
            logger.error("拉取课程数据失败：");
            e.printStackTrace();
        }finally {
            fetchAddressPool.shutdown();
        }
    }

    @Test
    void getFirstType(){
        String cssQuery = ".mod-nav__link-nav-first-link";
        Elements elements = getHtmlData(baseUrl, cssQuery);

        if (elements != null && elements.size() > 0){
            elements.stream().forEach(e ->{
                logger.info("一级分类的名称：" + e.text() + ", 地址：" + e.attr("href"));
            });
        }
    }

    @Test
    void getSecondType(){
        String cssQuery = "dl.sort-menu.sort-menu1.clearfix > dd > a > h2";
        List<String> urlList = getFirstType(baseUrl);

        if (urlList.size() > 0){
            urlList.stream().forEach(url -> {
                Elements elements = getHtmlData(url, cssQuery);

                if (elements != null || elements.size() > 0){
                    elements.stream().forEach(e ->{
                        logger.info("二级分类的名称：" + e.text() + ", 地址：" + e.parent().attr("href"));
                    });
                }

            });
        }
    }

    @Test
    void getThirdType(){
        String cssQuery = "dl.sort-menu.sort-menu1.clearfix > dd:not(.dd-all.curr_all) > a";
        List<String> firstTypeUrls = getFirstType(baseUrl);
        List<String> secondTypeUrls = getSecondType(firstTypeUrls);

        if (secondTypeUrls.size() > 0){
            secondTypeUrls.stream().forEach(url -> {
                Elements elements = getHtmlData(url, cssQuery);

                if (elements != null || elements.size() > 0){
                    elements.stream().forEach(e ->{
                        logger.info("三级分类的名称：" + e.text() + ", 地址：" + e.attr("href"));
                    });
                }
            });
        }
    }

    //获取一级分类标签url
    private List<String> getFirstType(String baseUrl){
        List<String> urlList = new LinkedList<>();

        String cssQuery = ".mod-nav__link-nav-first-link";
        Elements elements = getHtmlData(baseUrl, cssQuery);

        if (elements != null && elements.size() > 0){
            elements.stream().forEach(e ->{
                logger.info("一级分类的名称：" + e.text() + ", 地址：" + e.attr("href"));
                urlList.add(e.attr("href"));
            });
        }

        return urlList;
    }

    //获取二级分类标签url
    private List<String> getSecondType(List<String> urlList){
        List<String> resultList = new LinkedList<>();
        String cssQuery = "dl.sort-menu.sort-menu1.clearfix > dd > a > h2";

        if (urlList.size() > 0){
            urlList.stream().forEach(url -> {
                Elements elements = getHtmlData(url, cssQuery);

                if (elements != null || elements.size() > 0){
                    elements.stream().forEach(e ->{
                        logger.info("二级分类的名称：" + e.text() + ", 地址：" + e.parent().attr("href"));
                        resultList.add(e.parent().attr("href"));
                    });
                }

            });
        }

        return resultList;
    }

    //获取三级标签
    private List<CourseType> getThirdType(List<String> urlList){
        List<CourseType> resultList = new LinkedList<>();
        String cssQuery = "dl.sort-menu.sort-menu1.clearfix > dd:not(.dd-all.curr_all) > a";

        if (urlList.size() > 0){
            urlList.stream().forEach(url -> {
                Elements elements = getHtmlData(url, cssQuery);

                if (elements != null || elements.size() > 0){
                    elements.stream().forEach(e ->{
                        CourseType courseType = new CourseType();
                        courseType.setTypeName(e.text());
                        courseType.setCreateTime(LocalDateTime.now());
                        courseType.setUrl(e.attr("href"));
//                        logger.info("三级分类的名称：" + e.text() + ", 地址：" + e.attr("href"));
                        resultList.add(courseType);
                    });
                }

            });
        }

        return resultList;
    }

    //获取三级标签下课程
    private void getCourseList(String  url, List<CourseModel> courseList){
        Connection connect = Jsoup.connect(url);

        String cssQuery = ".course-card-list > li";
        try{
            Document document = connect.userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").timeout(6000)
                    .ignoreContentType(true).get();

            Elements elements = document.select(cssQuery);
            if (elements != null || elements.size() > 0){
                elements.stream().forEach(e ->{
                    CourseModel course = new CourseModel();
                    course.setCourseTitle(e.select("h4 > a").get(0).text());
                    course.setUrl(e.select("h4 > a").get(0).attr("href"));
                    if (e.select("span.line-cell.item-price.custom-string").size() > 0){
                        course.setPrice(e.select("span.line-cell.item-price.custom-string").get(0).text());
                    }else {
                        course.setPrice("免费");
                    }
                    if(e.select("a.line-cell.item-source-link.js-agency-name ").size() > 0){
                        course.setTeacher(e.select("a.line-cell.item-source-link.js-agency-name ").get(0).attr("title"));
                    }else {
                        course.setTeacher("");
                    }

                    courseList.add(course);
                });
            }

            Elements elements1 = document.select("a.page-next-btn.icon-font.i-v-right");
            if (elements1.size() > 0){
                String url1 = elements1.get(0).attr("href");
                if (!"javascript:void(0);".equals(url1)){
                    getCourseList(url1, courseList);
                }
            }
        }catch (IOException e){
            logger.error("获取课程列表失败：");
            e.printStackTrace();
        }

    }

    //爬取html标签
    private Elements getHtmlData(String url, String cssQuery){
        Elements elements = null;
        //链接到目标地址
        try {
            Connection connect = Jsoup.connect(url);
            //设置useragent,设置超时时间，并以get请求方式请求服务器
            Document document = connect.userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").timeout(6000)
                    .ignoreContentType(true).get();
            //获取指定标签的数据
            elements = document.select(cssQuery);
        }catch (IOException e){
            logger.error("获取html数据失败：");
            e.printStackTrace();
        }

        return elements;
    }
}