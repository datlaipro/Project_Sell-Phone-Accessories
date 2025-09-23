package com.github.datlaipro.shop.domain.productDetail.service;

import com.github.datlaipro.shop.domain.productDetail.dto.ProductDetailDto;
import com.github.datlaipro.shop.domain.productDetail.mapper.ProductMapper;
import com.github.datlaipro.shop.domain.productDetail.repo.JpaProductQuery;
import com.github.datlaipro.shop.domain.productDetail.repo.ProductSpecDetailRepository;
import com.github.datlaipro.shop.domain.productDetail.repo.projection.ProductDetailRow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import com.github.datlaipro.shop.domain.productDetail.repo.projection.SpecRow;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductDetailQueryServiceImpl implements ProductDetailQueryService {

    private final JpaProductQuery jpaProductQuery;
    private final ProductSpecDetailRepository productSpecRepository; // <-- thêm repo đọc product_specs

    @Override
public ProductDetailDto getProductDetail(Long productId, Integer limitImages) {
    int limit = (limitImages == null || limitImages <= 0) ? 4 : Math.min(limitImages, 8);

    ProductDetailRow row = jpaProductQuery.findDetail(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId));

    String cover = jpaProductQuery.findCoverUrl(productId); // có thể null
    var packed   = jpaProductQuery.findNonCoverPacked(productId, limit); // List<String> "url||sort"

    // NOTE: đảm bảo ProductMapper.toDto(...) map đúng:
    // cover -> dto.coverImage
    // packed -> dto.mediaImages (List<ProductDetailImageDto> với url/sortOrder)
    ProductDetailDto dto = ProductMapper.toDto(row, cover, packed);

    // ===== Lấy specs từ bảng product_specs =====
    List<SpecRow> specs = productSpecRepository.findAllByProductId(productId);

    // Description & Features (ưu tiên Description, fallback Features)
    String description = null;
    String features    = null;

    // Build list SpecItem cho FE
    java.util.Map<String, String> firstByAttr = new java.util.LinkedHashMap<>(); // giữ bản ghi đầu theo attr
    for (SpecRow s : specs) {
        String attr = safeTrim(s.getAttr());
        String val  = safeTrim(s.getVal());
        if (attr == null || val == null || val.isEmpty()) continue;

        if ("description".equalsIgnoreCase(attr)) {
            if (description == null) description = val;
            continue;
        }
        if ("features".equalsIgnoreCase(attr)) {
            if (features == null) features = val;
            continue;
        }
        // các attr khác (Compatibility/Dimensions/Weight/Materials/…)
        // chỉ giữ bản đầu tiên
        firstByAttr.putIfAbsent(attr, val);
    }

    if (description == null) description = features;
    dto.setDescription(description);

    // Build danh sách SpecItem
    java.util.List<ProductDetailDto.SpecItem> items = new java.util.ArrayList<>();
    for (var e : firstByAttr.entrySet()) {
        items.add(new ProductDetailDto.SpecItem(e.getKey(), e.getValue()));
    }

    // Ưu tiên 4 mục mới lên đầu
    java.util.List<String> priority = java.util.Arrays.asList(
            "Compatibility", "Dimensions", "Weight", "Materials"
    );
    items.sort((a, b) -> {
        int ia = indexOrMax(priority, a.getAttr());
        int ib = indexOrMax(priority, b.getAttr());
        if (ia != ib) return Integer.compare(ia, ib);
        return a.getAttr().compareToIgnoreCase(b.getAttr());
    });

    dto.setSpecs(items);
    return dto;
}

private static String safeTrim(String s) {
    return s == null ? null : s.trim();
}

private static int indexOrMax(java.util.List<String> list, String key) {
    if (key == null) return Integer.MAX_VALUE;
    int i = -1;
    for (int idx = 0; idx < list.size(); idx++) {
        if (list.get(idx).equalsIgnoreCase(key)) { i = idx; break; }
    }
    return i < 0 ? Integer.MAX_VALUE : i;
}

}
