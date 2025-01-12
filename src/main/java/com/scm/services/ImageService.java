package com.scm.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {

    String uploadImage(MultipartFile contactImage, String filePublicId);

    String getURLFromPublicId(String publicId);
}
