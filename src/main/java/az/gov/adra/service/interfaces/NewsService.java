package az.gov.adra.service.interfaces;

import az.gov.adra.entity.News;

import java.util.List;

public interface NewsService {

    List<News> findNewsByLastAddedTime();

}
