package com.votify.helpers;

import com.votify.dto.InfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UtilHelper {
    @Value("${api.base_url}")
    private String baseUrl;

    public InfoDto buildPageableInfoDto(Page<?> responsePage, String endpoint) {
        return new InfoDto(
            responsePage.getTotalElements(),
            responsePage.getTotalPages(),
            responsePage.hasNext() ? baseUrl + "/" + endpoint + "?page=" + (responsePage.getNumber() + 1) : null,
            responsePage.hasPrevious() ? baseUrl + "/" + endpoint + "?page=" + (responsePage.getNumber() - 1) : null
        );
    }

    public String getEnumValues(Class<?> enumClass) {
        if (enumClass.isEnum()) {
            StringBuilder sb = new StringBuilder();
            for (Object constant : enumClass.getEnumConstants()) {
                sb.append(((Enum<?>) constant).name()).append(", ");
            }
            return !sb.isEmpty() ? sb.substring(0, sb.length() - 2) : "";
        } else {
            return "";
        }
    }
}
