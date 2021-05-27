package com.javastaff.tracing.opentracinga;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

@RestController
public class AppAController {
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Tracer tracer;

    @RequestMapping("/test-tracing")
    public String endpoint1() {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    	HttpEntity<String> entity = new HttpEntity<>(headers);
    	String response=restTemplate.exchange("http://localhost:8081/service", HttpMethod.GET, entity, String.class).getBody();

        Span span = tracer.buildSpan("CustomSpan")
                        .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                        .start();
        Tags.COMPONENT.set(span, "AppAController");
        span.setTag("testtag", "test");
        span.finish();
    	
        return "Remote server said: "+response;
    }

    @RequestMapping("/test-tracing2")
    public String endpoint2() {
        endpoint1();

        Span span = tracer.buildSpan("loop").start();
        for (int i=0;i<5;i++) {
            endpoint1();
            restTemplate.exchange("http://localhost:5000", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class).getBody();
        }
        span.finish();
        return "yo";
    }
}