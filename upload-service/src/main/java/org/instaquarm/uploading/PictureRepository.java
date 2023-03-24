package org.instaquarm.uploading;

import org.instaquarm.uploading.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PictureRepository extends CrudRepository<Picture,Long> {
}
