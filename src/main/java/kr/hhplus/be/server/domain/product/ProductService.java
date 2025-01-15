package kr.hhplus.be.server.domain.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.command.ProductCommand;
import kr.hhplus.be.server.domain.product.info.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product findProductWithLock(long id) {
        return productRepository.findProductWithLock(id).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
    }

    public PageImpl<ProductInfo> selectProductList(Pageable pageable) {
        return ProductInfo.toPaging(productRepository.selectProductPaging(pageable));
    }

    public List<ProductInfo> selectTopSellingProductList() {
        List<Product> products = productRepository.selectTopSellingProducts();
        return ProductInfo.toInfos(products);
    }

    public List<Product> getProducts(List<ProductCommand.Get> commands){
        List<Product> collect = commands.stream().map(
            (command) -> productRepository.findById(command.productId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."))
        ).collect(Collectors.toList());
        return collect;
    }

    public Long getAmountOfProducts(List<ProductCommand.GetAmount> commands){
        return commands.stream().mapToLong(
            (command) -> {
                Product product = productRepository.findById(command.productId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
                return product.getPrice() * command.quantity();
            }
        ).sum();
    }

    // 재고 차감시 lock 사용
    public void deduct(List<ProductCommand.Deduct> deducts ) {
        deducts.forEach(deduct -> {
            Product product = productRepository.findProductWithLock(deduct.productId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            product.decrementStock(deduct.quantity());
        });
    }
}
