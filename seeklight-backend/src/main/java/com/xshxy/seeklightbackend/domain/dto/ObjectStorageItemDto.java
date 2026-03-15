package com.xshxy.seeklightbackend.domain.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ObjectStorageItemDto {

    private String objectName;

    private Long size;

    private String eTag;

    private ZonedDateTime lastModified;
}
