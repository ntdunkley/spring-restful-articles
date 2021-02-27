package com.nathan.springrestfularticles.web.controllers;

import com.nathan.springrestfularticles.dao.entities.Article;
import com.nathan.springrestfularticles.dao.repositories.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


// Would normally Mock the DB calls but these tests are to replace manual CURL calls
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticleControllerTests {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleControllerTests.class);

    private static final String GET_BY_ID_LOCAL_PATH = "/article/getById?id=%s";
    private static final String CREATE_LOCAL_PATH = "/article/create";
    private static final String STATISTICS_LOCAL_PATH = "/article/statistics";

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin-password";

    private static final LocalDate SAD_DATE = LocalDate.parse("1997-06-26");
    private static final LocalDate HAPPY_DATE = LocalDate.parse("2016-09-22");

    @LocalServerPort
    private int port;

    @Autowired
    private ArticleController articleController;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        assertThat(articleController).isNotNull();
    }

    @Test
    public void getByIdShouldReturnArticleWithGivenId() {
        // Create 1 article
        String response = postRequestCreate(new Article("Sad", "J.K. Rowling", "Once upon a time", SAD_DATE), CREATE_LOCAL_PATH);
        StringBuilder expectedString = new StringBuilder()
                .append(response);
        assertThat(getRequest(String.format(GET_BY_ID_LOCAL_PATH, getIdFromPostResponse(response))).trim()).isEqualTo(expectedString.toString().trim());
    }

    @Test
    public void createWithLongTitleShouldFail() {
        String longTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        String response = postRequestCreate(new Article(longTitle, "J.K. Rowling", "Once upon a time", LocalDate.now()), CREATE_LOCAL_PATH);
        assertThat(response.trim()).isEqualTo("Title cannot exceed 100 characters\n".trim());
    }

    @Test
    public void statisticsWithoutAuthorisationShouldBeForbidden() {
        ResponseEntity<String> response = getRequestEntity(STATISTICS_LOCAL_PATH);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void statisticsShouldReturnArticleCountPublishedInLastWeekOnly() {
        String response = getRequest(STATISTICS_LOCAL_PATH, ADMIN_USERNAME, ADMIN_PASSWORD);
        assertThat(response.trim()).isEqualTo("0\n".trim());
        // Create 3 Articles, only 1 of them published in last week
        postRequestCreate(new Article("Sad", "J.K. Rowling", "Once upon a time", LocalDate.now().minusDays(1)), CREATE_LOCAL_PATH);
        postRequestCreate(new Article("Happy", "Derren Brown", "In the old days", HAPPY_DATE), CREATE_LOCAL_PATH);
        postRequestCreate(new Article("Code of Combat", "Michael Asher", "The SAS was formed during World War 2", LocalDate.now().plusDays(1)), CREATE_LOCAL_PATH);
        // Test statistics
        response = getRequest(STATISTICS_LOCAL_PATH, ADMIN_USERNAME, ADMIN_PASSWORD);
        assertThat(response.trim()).isEqualTo("1\n".trim());
    }

    private String getRequest(String localPath) {
        return restTemplate.getForObject("http://localhost:" + port + localPath, String.class);
    }

    private String getRequest(String localPath, String username, String password) {
        return restTemplate
                .withBasicAuth(username, password)
                .getForObject("http://localhost:" + port + localPath, String.class);
    }

    private ResponseEntity<String> getRequestEntity(String localPath) {
        return restTemplate.getForEntity("http://localhost:" + port + localPath, String.class);
    }

    private String postRequestCreate(Article article, String localPath) {
        HttpEntity<Article> request = new HttpEntity<>(article);
        return restTemplate.postForObject("http://localhost:" + port + localPath, request, String.class).replace("Created Article: ", "");
    }

    private String getIdFromPostResponse(String response) {
        return response.substring(1, response.indexOf(','));
    }
}
