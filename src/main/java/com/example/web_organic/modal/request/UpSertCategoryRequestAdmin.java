package com.example.web_organic.modal.request;

import com.example.web_organic.modal.Enum.Category_Type;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpSertCategoryRequestAdmin {
    String name;
    Category_Type type;
    Boolean status;
}
