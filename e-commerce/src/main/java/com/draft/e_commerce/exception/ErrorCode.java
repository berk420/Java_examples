package com.draft.e_commerce.exception;

public enum ErrorCode {
    PRODUCT_NOT_FOUND("Bu ürün bulunmamaktadır."),
    PRODUCT_OUT_OF_STOCK("Ürün stokta yok."),
    PRODUCT_UPDATE_FAILED("Ürün güncellenemedi."),
    PRODUCT_DELETE_FAILED("Ürün silinemedi."),
    INVALID_PRODUCT_DATA("Geçersiz ürün verisi."),
    CUSTOMER_NOT_FOUND("Müşteri bulunamadı."),
    CART_NOT_FOUND("test"),
    CART_CREATION_FAILED("test"),
    PRODUCT_NOT_IN_CART("test"),
    CUSTOMER_CREATION_FAILED("test"),
    CUSTOMER_RETRIEVAL_FAILED("test"),
    ORDER_ENTRY_CREATION_FAILED("test"),
    ORDER_ENTRY_RETRIEVAL_FAILED("test"),
    ORDER_ALREADY_EXISTS("test"),
    ORDER_PLACEMENT_FAILED("test");
    

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
