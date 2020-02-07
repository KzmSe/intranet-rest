package az.gov.adra.service;

import az.gov.adra.entity.Gallery;
import az.gov.adra.repository.interfaces.GalleryRepository;
import az.gov.adra.service.interfaces.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryServiceImpl implements GalleryService {

    private GalleryRepository galleryRepository;

    @Autowired
    public GalleryServiceImpl(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @Override
    public List<Gallery> findAllGalleries(int offset) {
        return galleryRepository.findAllGalleries(offset);
    }

    @Override
    public int findCountOfAllGalleries() {
        return galleryRepository.findCountOfAllGalleries();
    }

}
