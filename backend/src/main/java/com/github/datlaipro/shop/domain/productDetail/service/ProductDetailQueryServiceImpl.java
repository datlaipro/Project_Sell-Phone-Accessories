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

        // Header row (không còn lấy p.description trong SQL; nếu vẫn còn, hãy sửa query
        // theo hướng alias từ product_specs)
        ProductDetailRow row = jpaProductQuery.findDetail(productId) // <-- đổi tên hàm
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId));

        // Ảnh: cover + non-cover (giới hạn)
        String cover = jpaProductQuery.findCoverUrl(productId); // có thể null
        var packed = jpaProductQuery.findNonCoverPacked(productId, limit); // List<String> "url||sort"

        // Map phần còn lại
        ProductDetailDto dto = ProductMapper.toDto(row, cover, packed);

        // Lấy mô tả từ bảng product_specs (ưu tiên attr='Description', fallback
        // 'Features')
        List<SpecRow> specs = productSpecRepository.findAllByProductId(productId);

        String description = null;
        String features = null;
        for (SpecRow s : specs) {
            if (description == null && "Description".equalsIgnoreCase(s.getAttr())) {
                description = s.getVal();
            }
            if (features == null && "Features".equalsIgnoreCase(s.getAttr())) {
                features = s.getVal();
            }
        }
        if (description == null)
            description = features;
        dto.setDescription(description);

        return dto;
    }
}
