package com.scm.services.implementation;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helpers.AppContstants;
import com.scm.services.ImageService;

@Service
public class ImageServiceImplementation implements ImageService {

    private Cloudinary cloudinary;

    public ImageServiceImplementation(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    @Override
    public String uploadImage(MultipartFile contactImage, String filePublicId) {

        try {
            byte[] data = new byte[contactImage.getInputStream().available()];
            contactImage.getInputStream().read(data);
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "public_id", filePublicId
            ));
            
            return this.getURLFromPublicId(filePublicId);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String getURLFromPublicId(String publicId) {

        return cloudinary
            .url()
            .transformation(
                new Transformation<>()
                .width(AppContstants.CONTACT_IMAGE_WIDTH)
                .height(AppContstants.CONTACT_IMAGE_HEIGHT)
                .crop(AppContstants.CONTACT_IMAGE_CROP)
            )
            .generate(publicId);
    }
}