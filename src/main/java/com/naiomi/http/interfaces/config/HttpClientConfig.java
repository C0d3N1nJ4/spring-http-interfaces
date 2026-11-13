package com.naiomi.http.interfaces.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.naiomi.http.interfaces.clients.UserHttpClient;

@Configuration
public class HttpClientConfig {

  @Bean
  public UserHttpClient userHttpClient(@Value("${api.base.url}") String baseUrl) {

    RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();

    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    return factory.createClient(UserHttpClient.class);
  }
}
