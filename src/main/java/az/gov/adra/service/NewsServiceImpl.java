package az.gov.adra.service;

import az.gov.adra.entity.News;
import az.gov.adra.repository.interfaces.NewsRepository;
import az.gov.adra.service.interfaces.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public List<News> findNewsByLastAddedTime() {
        return newsRepository.findNewsByLastAddedTime();
    }

}
