package com.mywork.recom;

public class ProductDTO {
    private String name;
    private String imageUrl;
    private String productLink;

    public ProductDTO(String name, String imageUrl, String productLink) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.productLink = productLink;
    }

    // getter 메서드들
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getProductLink() { return productLink; }
}
