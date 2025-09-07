package com.github.datlaipro.shop.domain.util;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtil {
//SlugUtil là “máy ép khuôn” chuỗi đầu vào về một định dạng an toàn, ổn định và dễ so khớp cho cả truy vấn DB lẫn URL.
  private SlugUtil() {}

  public static String toSlug(String input) {
    if (input == null) return null;
    String lower = input.trim().toLowerCase(Locale.ROOT);

    // Loại bỏ dấu tiếng Việt và ký tự unicode đặc biệt
    String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

    // Chỉ giữ a-z0-9 và thay phần còn lại thành '-'
    String slug = normalized.replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-+|-+$", "") // bỏ '-' đầu/cuối
        .replaceAll("-{2,}", "-"); // gộp nhiều '-' liên tiếp

    return slug;
  }
}
