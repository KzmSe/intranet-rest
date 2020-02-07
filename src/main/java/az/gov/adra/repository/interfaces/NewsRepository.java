package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.News;

import java.util.List;

public interface NewsRepository {

    List<News> findNewsByLastAddedTime();

}
