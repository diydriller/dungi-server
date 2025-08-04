package com.dungi.apiserver.web;

import com.dungi.common.util.StringUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;

import java.io.IOException;

import static com.dungi.common.util.StringUtil.REQUEST_KEY;


@Component
public class LogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        MDC.put(REQUEST_KEY, StringUtil.randomString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        MDC.clear();
        Filter.super.destroy();
    }
}
