package com.itheima.heimagirl;

import java.util.List;

/**
 * Created by Leon on 2017/5/7.
 * 公司名称：黑马程序员
 */

public class ResultBean {
    public String error;
    public List<Result> results;

    public class Result {
        public String publishedAt;
        public String url;
    }
}
