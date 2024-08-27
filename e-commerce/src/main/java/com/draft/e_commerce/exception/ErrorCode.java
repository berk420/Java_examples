package com.draft.e_commerce.exception;

public enum ErrorCode {
    PRODUCT_NOT_FOUND               ("Bu ürün bulunmamaktadır."),
    PRODUCT_OUT_OF_STOCK            ("Ürün stokta yok."),
    PRODUCT_UPDATE_FAILED           ("Ürün güncellenemedi."),
    PRODUCT_DELETE_FAILED           ("Ürün silinemedi."),
    INVALID_PRODUCT_DATA            ("Geçersiz ürün verisi."),
    CUSTOMER_NOT_FOUND              ("Müşteri bulunamadı."),
    CART_NOT_FOUND                  ("CART_NOT_FOUND"),
    CART_CREATION_FAILED            ("CART_CREATION_FAILED"),
    PRODUCT_NOT_IN_CART             ("PRODUCT_NOT_IN_CART"),
    CUSTOMER_CREATION_FAILED        ("CUSTOMER_CREATION_FAILED"),
    CUSTOMER_RETRIEVAL_FAILED       ("CUSTOMER_RETRIEVAL_FAILED"),
    ORDER_ENTRY_CREATION_FAILED     ("ORDER_ENTRY_CREATION_FAILED"),
    ORDER_ENTRY_RETRIEVAL_FAILED    ("ORDER_ENTRY_RETRIEVAL_FAILED"),
    ORDER_ALREADY_EXISTS            ("ORDER_ALREADY_EXISTS"),
    ORDER_PLACEMENT_FAILED          ("ORDER_PLACEMENT_FAILED"),
    ORDER_NOT_FOUND                 ("ORDER_NOT_FOUND"),
    INTERNAL_SERVER_ERROR           ("INTERNAL_SERVER_ERROR"),
    CART_ALREADY_EXISTS             ("CART_ALREADY_EXISTS"),
    PRODUCT_NOT_FOUND_IN_CART       ("PRODUCT_NOT_FOUND_IN_CART"),
    INSUFFICIENT_STOCK              ("INSUFFICIENT_STOCK"),
    CART_ENTRY_DELETED              ("CART_ENTRY_DELETED"),
    PRODUCT_IN_USE                  ("PRODUCT_IN_USE");
    

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
