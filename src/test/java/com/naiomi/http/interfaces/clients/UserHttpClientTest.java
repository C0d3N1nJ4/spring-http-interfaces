package com.naiomi.http.interfaces.clients;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.naiomi.http.interfaces.model.User;

class UserHttpClientTest {

  private static final String BASE_URL = "https://example.test";

  private MockRestServiceServer server;
  private UserHttpClient userHttpClient;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();

    RestClient restClient = builder.baseUrl(BASE_URL).build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
    userHttpClient = factory.createClient(UserHttpClient.class);
  }

  @Test
  void shouldFetchAllUsers() {
    server
        .expect(requestTo(BASE_URL + "/users"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withSuccess(
                """
                                [
                                  {
                                    "id": 1,
                                    "name": "Alice",
                                    "username": "alice",
                                    "email": "alice@example.com",
                                    "address": {
                                      "street": "Street 123",
                                      "suite": "Suite 1",
                                      "city": "CityX",
                                      "zipcode": "12345",
                                      "geo": null
                                    },
                                    "phone": "123-456-7890",
                                    "website": "www.alice.com",
                                    "company": {
                                      "name": "Alice Inc.",
                                      "catchPhrase": "Innovating Alice",
                                      "bs": "Tech"
                                    }
                                  },
                                  {
                                    "id": 2,
                                    "name": "Bob",
                                    "username": "bob",
                                    "email": "bob@example.com",
                                    "address": {
                                      "street": "Street 123",
                                      "suite": "Suite 1",
                                      "city": "CityX",
                                      "zipcode": "12345",
                                      "geo": null
                                    },
                                    "phone": "123-456-7890",
                                    "website": "www.bob.com",
                                    "company": {
                                      "name": "Bob Inc.",
                                      "catchPhrase": "Innovating Bob",
                                      "bs": "Tech"
                                    }
                                  }
                                ]
                                """,
                MediaType.APPLICATION_JSON));

    List<User> users = userHttpClient.findAll();

    assertThat(users).hasSize(2).extracting(User::id).containsExactly(1, 2);
    assertThat(users.getFirst().name()).isEqualTo("Alice");

    server.verify();
  }

  @Test
  void shouldFetchUserById() {
    server
        .expect(requestTo(BASE_URL + "/users/7"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withSuccess(
                """
                                {
                                  "id": 7,
                                  "name": "Carol",
                                  "username": "carol",
                                  "email": "carol@example.com",
                                  "address": {
                                    "street": "Street 123",
                                    "suite": "Suite 1",
                                    "city": "CityX",
                                    "zipcode": "12345",
                                    "geo": null
                                  },
                                  "phone": "123-456-7890",
                                  "website": "www.carol.com",
                                  "company": {
                                    "name": "Carol Inc.",
                                    "catchPhrase": "Innovating Carol",
                                    "bs": "Tech"
                                  }
                                }
                                """,
                MediaType.APPLICATION_JSON));

    User oneUser = userHttpClient.findById(7);

    assertThat(oneUser.id()).isEqualTo(7);
    assertThat(oneUser.name()).isEqualTo("Carol");
    assertThat(oneUser.email()).isEqualTo("carol@example.com");

    server.verify();
  }
}
