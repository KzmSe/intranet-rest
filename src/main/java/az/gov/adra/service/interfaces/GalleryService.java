package az.gov.adra.service.interfaces;

import az.gov.adra.entity.Gallery;

import java.util.List;

public interface GalleryService {

    List<Gallery> findAllGalleries(int offset);

    int findCountOfAllGalleries();

}
