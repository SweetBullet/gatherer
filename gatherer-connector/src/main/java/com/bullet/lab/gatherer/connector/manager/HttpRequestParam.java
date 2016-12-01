package com.bullet.lab.gatherer.connector.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by pudongxu on 16/12/1.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequestParam {
    private String method;
    private String path;
    private String body;
    private Map<String,String> params;
}
