package az.gov.adra.controller;

import az.gov.adra.entity.Gallery;
import az.gov.adra.entity.News;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.service.interfaces.GalleryService;
import az.gov.adra.service.interfaces.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllNews() {
        List<News> news = newsService.findNewsByLastAddedTime();

        return GenericResponse.withSuccess(HttpStatus.OK, "list of news", news);
    }

}
