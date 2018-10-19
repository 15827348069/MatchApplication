package com.zbmf.newmatch.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pq
 * on 2018/10/17.
 * 推荐的圈子的老师的数据映射实体
 */

public class RecommendTeacherBean extends Erro implements Serializable {

//    {
//        "status":"ok", "result":{
//        "page":1, "perpage":20, "pages":1, "total":8, "groups":[{
//            "id":379781, "name":"金山农民", "nickname":"金山农民", "avatar":
//            "http:\/\/oss2.zbmf.com\/avatar\/379781-2736b85fe1057s.jpg", "is_close":0, "is_private":
//            0, "is_fans":1, "content":"", "follow_num":9170, "style":"永远追随热点，对热点的解读非常独到", "tags":
//            "四维度战法创始人", "is_followed":1
//        }}]}
//    }

    private String status;
    private RecommendResult result;

    public Boolean getStatus() {
        return status.equals("ok");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RecommendResult getResult() {
        return result;
    }

    public void setResult(RecommendResult result) {
        this.result = result;
    }

    public static class RecommendResult implements Serializable {

        private int page;
        private int perpage;
        private int pages;
        private int total;
        private List<Group> groups;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPerpage() {
            return perpage;
        }

        public void setPerpage(int perpage) {
            this.perpage = perpage;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }
    }
}