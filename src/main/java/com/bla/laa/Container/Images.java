package com.bla.laa.Container;

import com.bla.laa.Hash;

import java.awt.image.BufferedImage;

public class Images {
    private Integer imageCsddId = -1;
    private BufferedImage imageLarge = null;
    private String imageLargeHash = "";

    private BufferedImage imageSmall = null;
    private String imageSmallHash = "";

    public Images() {
    }

    public Images(Integer imageCssdId) {
        this.imageCsddId = imageCssdId;
    }

    public Images(Integer imageCsddId, BufferedImage imageLarge, BufferedImage imageSmall) {
        this.imageLarge = imageLarge;
        if (this.imageLargeHash.length() == 0)
            this.imageLargeHash = Hash.getHash(this.imageLarge);

        this.imageSmall = imageSmall;
        if (this.imageSmallHash.length() == 0)
            this.imageSmallHash = Hash.getHash(this.imageSmall);

        this.imageCsddId = imageCsddId;
    }

    public int getImageLargeHeight() {
        if (this.imageLarge != null)
            return this.imageLarge.getHeight();
        else
            return 0;
    }

    public int getImageSmallHeight() {
        if (this.imageSmall != null)
            return this.imageSmall.getHeight();
        else
            return 0;
    }

    public int getImageLargeWidth() {
        if (this.imageLarge != null)
            return this.imageLarge.getWidth();
        else
            return 0;
    }

    public int getImageSmallWidth() {
        if (this.imageSmall != null)
            return this.imageSmall.getWidth();
        else
            return 0;
    }

    public String getImageLargeHash() {
        return imageLargeHash;
    }

    public String getImageSmallHash() {
        return imageSmallHash;
    }

    public Integer getImageCsddId() {
        return imageCsddId;
    }

    public void setImageCsddId(Integer imageCsddId) {
        this.imageCsddId = imageCsddId;
    }

    public BufferedImage getImageLarge() {
        return imageLarge;
    }

    public void setImageLarge(BufferedImage imageLarge) {
        this.imageLarge = imageLarge;
    }

    public BufferedImage getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(BufferedImage imageSmall) {
        this.imageSmall = imageSmall;
    }

    public void setImageLargeHash(String imageLargeHash) {
        this.imageLargeHash = imageLargeHash;
    }

    public void setImageSmallHash(String imageSmallHash) {
        this.imageSmallHash = imageSmallHash;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Images");
        sb.append("{imageCsddId=").append(imageCsddId);
        sb.append(", imageLarge=").append(imageLarge);
        sb.append(", imageLargeHash='").append(imageLargeHash).append('\'');
        sb.append(", imageSmall=").append(imageSmall);
        sb.append(", imageSmallHash='").append(imageSmallHash).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Images images = (Images) o;

        //if (imageCsddId != null ? !imageCsddId.equals(images.imageCsddId) : images.imageCsddId != null) return false;
        if (imageLarge != null ? !imageLarge.equals(images.imageLarge) : images.imageLarge != null) return false;
        if (imageLargeHash != null ? !imageLargeHash.equals(images.imageLargeHash) : images.imageLargeHash != null)
            return false;
        if (imageSmall != null ? !imageSmall.equals(images.imageSmall) : images.imageSmall != null) return false;
        if (imageSmallHash != null ? !imageSmallHash.equals(images.imageSmallHash) : images.imageSmallHash != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = imageCsddId != null ? imageCsddId.hashCode() : 0;
        result = 31 * result + (imageLarge != null ? imageLarge.hashCode() : 0);
        result = 31 * result + (imageLargeHash != null ? imageLargeHash.hashCode() : 0);
        result = 31 * result + (imageSmall != null ? imageSmall.hashCode() : 0);
        result = 31 * result + (imageSmallHash != null ? imageSmallHash.hashCode() : 0);
        return result;
    }
}

