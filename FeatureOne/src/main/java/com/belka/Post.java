package com.belka;

import lombok.Data;

@Data
public class Post {
    private long view_count;
    private long score;
    private long article_id;
    private String link;
    private String title;
}
