package com.example.web_organic;

import com.example.web_organic.entity.*;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.entity.Address;
import com.example.web_organic.entity.CartItem;
import com.example.web_organic.modal.Enum.Category_Type;
import com.example.web_organic.modal.Enum.Payment_Status_Enum;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.modal.Enum.User_Role;
import com.example.web_organic.repository.*;

import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootTest
class WebOrganicApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CartItemRepository cartRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderCanCellationRepository orderCanCellationRepository;
    @Test
    void createUser() {
        Faker faker = new Faker();
        Random random = new Random();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        for (int i = 0; i < 10; i++) {
            String name = faker.name().username();
            User user = User.builder()
                .username(name)
                .password(passwordEncoder.encode("123"))
                .fullName(name)
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .avatar(faker.avatar().image())
                .isActivated(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
//				if i % 2==0 then user has role ROLE_ADMIN else user has role ROLE_USER, ROLE_STAFF
                .role(User_Role.valueOf(
                    switch (random.nextInt(3)) {
                        case 0 -> "ADMIN";
                        case 1 -> "USER";
                        default -> "STAFF";
                    }
                ))
                .build();
            userRepository.save(user);
        }
    }


    @Test
    void createBlog() {
        Faker faker = new Faker();
        Slugify slugify = Slugify.builder().build();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String title = faker.book().title();
            String slug = slugify.slugify(title);
            String content = faker.lorem().paragraphs(3).toString();
            String description = faker.lorem().sentence();
            String image = faker.avatar().image();
            Boolean status = random.nextBoolean();
            String category = faker.book().genre();
            User user = userRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("User not found"));
            Blog blog = Blog.builder()
                .title(title)
                .content(content)
                .slug(slug)
                .description(description)
                .thumbnail(image)
                .status(status)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .publishedAt(LocalDateTime.now())
//                .category(category)
                .build();

            // Save blog
            blogRepository.save(blog);
        }
    }

    @Test
    void createAddress() {
        Faker faker = new Faker();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Address address = Address.builder()
                .fullName(faker.name().fullName())
                .phone(faker.phoneNumber().phoneNumber())
                .addressSelected(faker.address().fullAddress())
                .addressDetail(faker.address().secondaryAddress())
                .isDefault(random.nextBoolean())
                .user(userRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("User not found")))
                .build();
            addressRepository.save(address);
        }
    }

    @Test
    void createBrand() {
        Faker faker = new Faker();

        for (int i = 0; i < 8; i++) {
            String logo = faker.avatar().image();
            String name = faker.company().name();
            Brand brand = Brand.builder()
                .logo(logo)
                .nameBrand(name)
                .build();
            brandRepository.save(brand);

        }
    }

    @Test
    void createCategory() {
        Faker faker = new Faker();
        Slugify slugify = Slugify.builder().build();
        for (int i = 0; i < 10; i++) {
            String name = faker.commerce().department();
            String slug = slugify.slugify(name);
            Category category = Category.builder()
                .name(name)
                .slug(slug)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            categoryRepository.save(category);
        }
    }

    @Test
    void createProduct() {
        Faker faker = new Faker();
        Slugify slugify = Slugify.builder().build();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String name = faker.commerce().productName();
            String slug = slugify.slugify(name);
            String description = faker.lorem().sentence();
            String image = faker.avatar().image();
//            random discount from 1 to 25%
            Integer discount = random.nextInt(25) + 1;
            Boolean status = random.nextBoolean();
            Brand brand = brandRepository.findById(random.nextInt(8) + 1).orElseThrow(() -> new RuntimeException("Brand not found"));
            Category category = categoryRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("Category not found"));
            Product product = Product.builder()
                .name(name)
                .slug(slug)
                .imageUrl("https://placehold.co/200x200?text="+ name.substring(0, 1).toUpperCase())
                .description(description)
                .discount(discount)
                .status(status)
                .brand(brand)
                .category(category)
                .build();
            productRepository.save(product);
        }
    }

    @Test
    void createWishList() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            User user = userRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("Product not found"));
            WishList wishList = WishList.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
            wishListRepository.save(wishList);
        }
    }

    @Test
    void createProductImage() {
        Random random = new Random();
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {
            Product product = productRepository.findById(random.nextInt(11) + 10).orElseThrow(() -> new RuntimeException("Product not found"));
            ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageProduct("https://placehold.co/200x200?text=" + product.getName().substring(0, 1).toUpperCase())
                .altText(faker.lorem().sentence())
                .build();
            productImageRepository.save(productImage);
        }
    }


    @Test
    void createProductVariant() {
        Random random = new Random();
        Faker faker = new Faker();
        List<String> weightOptions = Arrays.asList("300g", "1kg");

        // Duyệt qua từng sản phẩm có ID từ 1 đến 10
        for (int productId = 11; productId <= 20; productId++) {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

            // Xác định có tạo cả hai phiên bản hay không (50% tạo cả 2, 50% chỉ tạo 1)
            boolean hasBothWeights = random.nextBoolean();

            // Nếu hasBothWeights = true, tạo cả 2 phiên bản
            // Nếu false, chỉ tạo 1 phiên bản ngẫu nhiên
            List<String> selectedWeights = hasBothWeights ? weightOptions
                : Collections.singletonList(weightOptions.get(random.nextInt(weightOptions.size())));

            for (String weight : selectedWeights) {
                ProductVariants productVariant = ProductVariants.builder()
                    .product(product)
                    .weight(weight)
                    .price(BigDecimal.valueOf(faker.number().randomDouble(2, 10000, 1000000)))
                    .stock(random.nextInt(100))
                    .isDefault(random.nextBoolean())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

                productVariantRepository.save(productVariant);
            }
        }
    }


    @Test
    void createFullOrderProcess() {
        Random random = new Random();

        // 1. Tạo cart items (nếu chưa có)
//        createCart();

        // 2. Lấy một user để tạo đơn hàng
        User user = userRepository.findById(7)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Tạo đơn hàng cho user
        Order createdOrder = createOrderForUser(user);

        // 4. Tạo order details từ cart items của user
        createOrderDetailsFromCart(user, createdOrder);

        // 5. Tùy chọn: Xóa các cart items của user sau khi đặt hàng
        // cartRepository.deleteAllByUser(user);
    }

    @Test
    void createCart() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            User user = userRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("User not found"));
            ProductVariants productVariant = productVariantRepository.findById(random.nextInt(16) + 1).orElseThrow(() -> new RuntimeException("ProductVariant not found"));
           CartItem cart = CartItem.builder()
                .user(user)
                .productVariant(productVariant)
                .quantity(random.nextInt(10) + 1)
               .createdAt(LocalDateTime.now())
                .build();
            cartRepository.save(cart);
        }
    }




    @Test
    Order createOrderForUser(User user) {
        Random random = new Random();

        // Lấy địa chỉ của user (giả sử có repository cho address)
         Address address = (Address) addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .orElseThrow(() -> new RuntimeException("Default address not found"));

        // Hoặc tạo thông tin địa chỉ ngẫu nhiên
//        String[] addressOptions = {"Hà Nội", "TP.HCM", "Đà Nẵng", "Hải Phòng", "Cần Thơ"};
//        String randomAddress = addressOptions[random.nextInt(addressOptions.length)];

        // Tạo đơn hàng mới
        Order order = Order.builder()
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .addressSelect(String.valueOf(address))
            .addressDetail("Số " + (random.nextInt(100) + 1) + ", Đường ABC")
            .productCost(BigDecimal.valueOf(0.0)) // Sẽ cập nhật sau khi thêm order details
            .shippingCost(BigDecimal.valueOf(30000.0)) // Phí ship cố định hoặc tính toán dựa trên địa chỉ
            .status(Status_Enum.PENDING) // Enum trạng thái đơn hàng
            .paymentMethod(random.nextBoolean() ? "COD" : "Banking")
            .paymentStatus(Payment_Status_Enum.PENDING) // Enum trạng thái thanh toán
            .total(BigDecimal.valueOf(0.0)) // Sẽ cập nhật sau
            .user(user)
            .orderDate(LocalDateTime.now())
            .build();

        return orderRepository.save(order);
    }

    @Test
    void createOrderDetailsFromCart(User user, Order order) {
        // Lấy tất cả cart items của user
        List<CartItem> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("User has no items in cart");
        }

        BigDecimal totalProductCost = BigDecimal.ZERO; // Dùng BigDecimal để chính xác hơn

        for (CartItem cartItem : cartItems) {
            ProductVariants productVariant = cartItem.getProductVariant();
            int quantity = cartItem.getQuantity();
            BigDecimal unitPrice = productVariant.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity)); // ✅ Sửa lỗi

            // Tạo order detail từ cart item
            OrderDetail orderDetail = OrderDetail.builder()
                .unitPrice(unitPrice)
                .quantity(quantity)
                .subTotal(subtotal)
                .order(order) // Liên kết với đơn hàng
                .productVariant(productVariant) // Liên kết với biến thể sản phẩm
                .build();

            orderDetailRepository.save(orderDetail);

            // Cộng dồn tổng giá sản phẩm
            totalProductCost = totalProductCost.add(subtotal);
        }

        // Cập nhật lại giá trong đơn hàng
        order.setProductCost(totalProductCost);
        order.setTotal(totalProductCost.add(order.getShippingCost())); // Dùng add thay vì "+"
        orderRepository.save(order);
    }


    @Test
    void createReview() {
        Random random = new Random();
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            User user = userRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(random.nextInt(10) + 1).orElseThrow(() -> new RuntimeException("Product not found"));
            String content = faker.lorem().paragraphs(3).toString();
            Integer rating = random.nextInt(5) + 1;
            Review review = Review.builder()
                .user(user)
                .product(product)
                .content(content)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .build();
            reviewRepository.save(review);
        }
    }

    @Test
    void createOrderCancellation() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            User user = userRepository.findById(7).orElseThrow(() -> new RuntimeException("User not found"));
            Order order = orderRepository.findById(3).orElseThrow(() -> new RuntimeException("Order not found"));
            String reason = "Lý do hủy đơn hàng: " + i;
            OrderCancellation orderCancellation = OrderCancellation.builder()
                .user(user)
                .order(order)
                .reason(reason)
                .cancelledAt(LocalDateTime.now())
                .build();
            orderCanCellationRepository.save(orderCancellation);
        }
    }

    @Test
    void test(){
//       List<Address>  address =  addressRepository.findByIsDefaultTrue();
//    System.out.println(address);
//        Random random = new Random();
//        User user = userRepository.findById(random.nextInt(10) + 1)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//        String address = addressRepository.findAddressSelectedByUserId(user.getId());
//        System.out.println(address);
        Random random = new Random();
        User user = userRepository.findById(random.nextInt(10) + 1)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = (Address) addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
            .orElseThrow(() -> new RuntimeException("Default address not found"));
        System.out.println(address);
    }

    @Test
    void loadProductCategory() {
        List<Product> products = productRepository.findByCategoryId(6, PageRequest.of(0, 10, Sort.by("createdAt").descending())).getContent();
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println(products);
    }

    @Test
    void loadPrVariant(){
    List<Product> products = productRepository.findAll();
        for (Product product : products) {
            Integer productId = product.getId();

            // Gọi repository để lấy variant mặc định
            ProductVariants defaultVariant = productVariantRepository.findByProductIdAndIsDefaultTrue(productId);

            // In ra kết quả kiểm tra
            System.out.println("Product ID: " + productId);
            if (defaultVariant != null) {
                System.out.println("  ➜ Default Variant Price: " + defaultVariant.getPrice());
            } else {
                System.out.println("  ⚠ Không có variant mặc định!");
            }
        }
    }

    @Test
    void loadPrCateSlug(){
        List<Product> products = productRepository.findByCategorySlug("music", PageRequest.of(0, 10, Sort.by("createdAt").descending())).getContent();
        for (Product product : products) {
            System.out.println(product);
        }
    }

    @Test
    void loadPrDissCountMax(){
        List<Product> products = productRepository.findTop15ByOrderByDiscountDesc();
        for (Product product : products) {
            System.out.println(product);
        }
    }

    @Test
    void loadBlogCate(){
        List<Category> categories = categoryRepository.findByTypeAndStatusTrue(Category_Type.BLOG);
        for (Category category : categories) {
            System.out.println(category);
        }
    }


}
