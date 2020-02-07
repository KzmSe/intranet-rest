package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Gallery;

import java.util.List;

public interface GalleryRepository {

    List<Gallery> findAllGalleries(int offset);

    int findCountOfAllGalleries();

}
