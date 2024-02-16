package com.example.model;

import lombok.Builder;

@Builder // No haría falta
public record FileUploadResponse(String fileName, String downloadURI, long size) {

}
