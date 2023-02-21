package com.belka;

import lombok.Data;

import java.util.Collection;

@Data
public class ListPosts {
    private Collection<Post> posts;
    private long quota_max;
    private long quota_remaining;
}
