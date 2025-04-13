package com.example.web_organic.controller;

import com.example.web_organic.entity.*;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.modal.response.TokenConfirmMessageResponse;
import com.example.web_organic.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;

@Controller
@RequiredArgsConstructor
public class WebController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderCanCellationService orderCanCellationService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/")
    public String getHomePage(@RequestParam(required = false,defaultValue = "1") int page,
        @RequestParam(required = false,defaultValue = "10") int pageSize, Model model) {

        Page<Product> products = productService.getProductPopular(page, 20);
        Page<Product> productCategory = productService.getProductByCategory(1, 20,"music");
        Page<Product> productReadyToEat = productService.getProductByCategory(1, 20,"ready-to-eat");
        Page<Product> productNonFood = productService.getProductByCategory(1, 20,"phi-thuc-pham");
        List<Product> productDiscountMax = productService.getProductDiscountMax();//        lây 20 san phảm discount lớn nhat
        Page<Blog> blogs = blogService.getBlogPopular(1, 6);// 3 blog
        // Thêm thông tin stock và giá cho mỗi sản phẩm
        Map<Integer, Integer> productStocks = new HashMap<>();
        Map<Integer, BigDecimal> productVariantPrices = new HashMap<>();

        // Hàm dùng chung để thêm dữ liệu vào productStocks & productVariantPrices
        Consumer<Product> addProductInfo = product -> {
            int productId = product.getId();
            productStocks.putIfAbsent(productId, productService.getProductStock(productId)); // Tránh ghi đè nếu đã có
            productVariantPrices.putIfAbsent(productId, productService.getProductVariantPrice(productId)); // Tránh ghi đè nếu đã có
        };

        // Lấy stock và giá cho sản phẩm phổ biến
        products.getContent().forEach(addProductInfo);
        // Lấy stock và giá cho sản phẩm theo danh mục
        productCategory.getContent().forEach(addProductInfo);
        productReadyToEat.getContent().forEach(addProductInfo);
        productNonFood.getContent().forEach(addProductInfo);
        productDiscountMax.forEach(addProductInfo);

        model.addAttribute("products", products); //sản phẩm phổ biến
        model.addAttribute("productStocks", productStocks); //số lượng sản phẩm
        model.addAttribute("productVariantPrices", productVariantPrices); //giá sản phẩm
        model.addAttribute("productCategory", productCategory); //sản phẩm theo category
        model.addAttribute("productReadyToEat", productReadyToEat); //sản phẩm ăn ngay
        model.addAttribute("productNonFood", productNonFood); //sản phẩm không phải thức ăn
        model.addAttribute("productDiscountMax", productDiscountMax); //sản phẩm có discount lớn nhất
        model.addAttribute("blogs", blogs); //blog

        return "/web/index";
    }

    @GetMapping("/product")
    public String getProductPage(@RequestParam(required = false, defaultValue = "1") int page,
                                 @RequestParam(required = false, defaultValue = "10") int pageSize,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false, defaultValue = "all") String sort,
                                 @RequestParam(required = false, defaultValue = "all") String brand,
                                 @RequestParam(required = false, defaultValue = "all") String price,
                                 Model model) {

        List<Brand> brands = brandService.getBrandByStatus();
        Map<Integer, Integer> productStocks = new HashMap<>();
        Map<Integer, BigDecimal> productVariantPrices = new HashMap<>();

        Category selectedCategory = null;
        if (type != null) {
            selectedCategory = categoryService.getCategoryBySlug(type);
        }

        // Lọc sản phẩm với Specification
        Page<Product> productPage = productService.getFilteredProducts(selectedCategory, sort, brand, price, page - 1, pageSize);

        // Lấy stock và giá của sản phẩm
        productPage.getContent().forEach(product -> {
            int productId = product.getId();
            productStocks.putIfAbsent(productId, productService.getProductStock(productId));
            productVariantPrices.putIfAbsent(productId, productService.getProductVariantPrice(productId));
        });

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productStocks", productStocks);
        model.addAttribute("productVariantPrices", productVariantPrices);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("brands", brands);
        model.addAttribute("selectedCategory", selectedCategory);

        return "/web/product";
    }


    @GetMapping("/blog")
    public String getBlogPage(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "9") int pageSize,
                              @RequestParam(required = false) String type, Model model) {
        Category selectedCategory = null;
        if (type != null) {
            selectedCategory = categoryService.getCategoryBySlug(type);
        }

        Page<Blog> blogPage;
        if (selectedCategory != null) {
            blogPage = blogService.getBlogsByCategory(selectedCategory, page - 1, pageSize);
        } else {
            blogPage = blogService.getAllBlogsAndStatus(page - 1, pageSize);
        }


//        String plainText = Jsoup.parse(blog.getContent()).text();
//        model.addAttribute("shortContent", plainText);
        model.addAttribute("blogs", blogPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        return "/web/blog";
    }

    @GetMapping("/blog/{id}/{slug}")
    public String getBlogDetailPage(@PathVariable Integer id, @PathVariable String slug, Model model) {
        Blog blog = blogService.getBlogDetail(id,slug);


        //lay tat ca blog
        List<Blog> allBlogs = blogService.getAllBlog();
        model.addAttribute("allBlogs", allBlogs);
        model.addAttribute("blog", blog);
        return "/web/blog-detail";
    }

    @GetMapping("/product/{id}/{slug}")
    public String getProductDetailPage(@PathVariable Integer id,@PathVariable String slug, Model model) {
        Product product = productService.getProductDetail(id,slug);
        List<ProductImage> productImages = productService.getProductImages(id);

        List<ProductVariants> productVariants = productService.getProductVariantsByProductId(id); // Lấy danh sách biến thể của sản phẩm

        List<Product> relatedProducts = productService.getProductsByCategory(product.getCategory().getId(), id);// Lấy danh sách sản phẩm cùng danh mục

        Map<Integer, Integer> productStocks = new HashMap<>();  // Thêm thông tin stock và giá cho mỗi sản phẩm
        Map<Integer, BigDecimal> productVariantPrices = new HashMap<>();

        Consumer<Product> addProductInfo = products -> { // Hàm dùng chung để thêm dữ liệu vào productStocks & productVariantPrices
            int productId = products.getId();
            productStocks.putIfAbsent(productId, productService.getProductStock(productId)); // Tránh ghi đè nếu đã có
            productVariantPrices.putIfAbsent(productId, productService.getProductVariantPrice(productId)); // Tránh ghi đè nếu đã có
        };
        addProductInfo.accept(product);
        relatedProducts.forEach(addProductInfo);

        Map<Integer, Integer> variantStocks = productService.getProductVariantStocks(id); // Lấy stock theo từng biến thể
        List<Review> reviews = productService.getProductReviews(id); // Lấy danh sách review của sản phẩm

        model.addAttribute("productStocks", productStocks); //số lượng sản phẩm
        model.addAttribute("variantStocks", variantStocks); //số lượng sản phẩm theo biến thể
        model.addAttribute("productVariants", productVariants); //danh sách biến thể
        model.addAttribute("relatedProducts", relatedProducts); //sản phẩm cùng danh mục
        model.addAttribute("productVariantPrices", productVariantPrices); //giá sản phẩm
        model.addAttribute("reviews", reviews); //danh sách review
        model.addAttribute("product", product);
        model.addAttribute("productImages", productImages);
        return "/web/product-detail";
    }

    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user != null) {
            return "redirect:/";
        }
        return "/web/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user != null) {
            return "redirect:/";
        }
        return "/web/signup";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            return "redirect:/login";
        }
        List<CartItem> cartItems = cartItemService.getCartItems(user);

        Map<Integer, CartItem> groupedItems = new HashMap<>();
        for (CartItem item : cartItems) {
            groupedItems.merge(item.getProductVariant().getId(), item, (existing, newItem) -> {
                existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                return existing;
            });
        }
        model.addAttribute("cartItems", groupedItems.values());
        return "/web/cart";
    }


    @GetMapping("/contact")
    public String getContactPage() {
        return "/web/contact";
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            return "redirect:/login";
        }

        // Lấy danh sách đơn hàng theo trạng thái
        List<Order> orderPending = orderService.getOrderByUserAndStatus(user, Status_Enum.PENDING);
        List<Order> orderProcessing = orderService.getOrderByUserAndStatus(user, Status_Enum.PROCESSING);
        List<Order> orderShipping = orderService.getOrderByUserAndStatus(user, Status_Enum.SHIPPED);
        List<Order> orderCompleted = orderService.getOrderByUserAndStatus(user, Status_Enum.COMPLETED);
        List<Order> orderCanceled = orderService.getOrderByUserAndStatus(user, Status_Enum.CANCELED);

        // Tạo Map để nhóm OrderCancellation theo OrderId
        Map<String, OrderCancellation> orderCancellationMap = new HashMap<>();

        List<OrderCancellation> orderCancellations = orderCanCellationService.getAllByUser(user);
        for (OrderCancellation oc : orderCancellations) {
            orderCancellationMap.put(oc.getOrder().getId(), oc);
        }
        // Tạo Map để nhóm OrderDetail theo OrderId
        Map<String, List<OrderDetail>> orderDetailsMap = new HashMap<>();

        // Nhóm OrderDetail cho từng danh sách đơn hàng
        List<Order> allOrders = new ArrayList<>();
        allOrders.addAll(orderPending);
        allOrders.addAll(orderProcessing);
        allOrders.addAll(orderShipping);
        allOrders.addAll(orderCompleted);
        allOrders.addAll(orderCanceled);

        for (Order order : allOrders) {
            List<OrderDetail> details = orderService.getOrderDetailsByOrderId(order.getId());
            orderDetailsMap.put(order.getId(), details);
        }

        //Lấy thông tin địa chỉ của user
        List<Address> addresses = addressService.getAddressByUserId(user.getId());
        // Gửi dữ liệu đến View
        model.addAttribute("orderPendings", orderPending);
        model.addAttribute("orderProcessings", orderProcessing);
        model.addAttribute("orderShippings", orderShipping);
        model.addAttribute("orderCompleteds", orderCompleted);
        model.addAttribute("orderCanceleds", orderCanceled);
        model.addAttribute("orderDetailsMap", orderDetailsMap); // Truyền Map vào Thymeleaf
        model.addAttribute("orderCancellationMap", orderCancellationMap); // Truyền Map vào Thymeleaf
        model.addAttribute("addresses", addresses);
        model.addAttribute("user", user);
        return "/web/info-user";
    }


    @GetMapping("/promotion")
    public String getPromotionPage(@RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "10") int pageSize,
                                   @RequestParam(required = false, defaultValue = "all") String sort,
                                   @RequestParam(required = false, defaultValue = "all") String brand,
                                   @RequestParam(required = false, defaultValue = "all") String price,
                                   Model model) {

        List<Brand> brands = brandService.getBrandByStatus();
        Map<Integer, Integer> productStocks = new HashMap<>();
        Map<Integer, BigDecimal> productVariantPrices = new HashMap<>();

        // Không có selectedCategory vì không lọc theo type
        Page<Product> productPage = productService.getPromotionProducts(sort, brand, price, page - 1, pageSize);

        productPage.getContent().forEach(product -> {
            int productId = product.getId();
            productStocks.putIfAbsent(productId, productService.getProductStock(productId));
            productVariantPrices.putIfAbsent(productId, productService.getProductVariantPrice(productId));
        });

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productStocks", productStocks);
        model.addAttribute("productVariantPrices", productVariantPrices);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("brands", brands);

        return "/web/promotion";
    }

    @GetMapping("/payment")
    public String getPaymentPage(Model model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            return "redirect:/login";
        }

        // Lấy thông tin địa chỉ của user
        List<Address> addresses = addressService.getAddressByUserId(user.getId());
        model.addAttribute("addresses", addresses);

        // Lấy thông tin giỏ hàng trực tiếp từ database
        List<CartItem> cartItems = cartItemService.getCartItems(user);

        // Tính tổng tiền
        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
        for (CartItem item : cartItems) {
            BigDecimal discount = item.getProductVariant().getProduct().getDiscount() != null ?
                item.getProductVariant().getPrice().multiply(BigDecimal.valueOf(item.getProductVariant().getProduct().getDiscount()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : // Làm tròn 2 chữ số thập phân khi tính giảm giá
                BigDecimal.valueOf(0);

            BigDecimal price = item.getProductVariant().getPrice().subtract(discount);
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Làm tròn lên số nguyên gần nhất
        totalAmount = totalAmount.setScale(0, RoundingMode.CEILING);

        // Truyền dữ liệu giỏ hàng và tổng tiền vào model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "/web/payment";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage() {
        return "/web/checkout";
    }

    @GetMapping("/xac-thuc-tai-khoan")
    public String confirmAccount(@RequestParam String token, Model model){
        TokenConfirmMessageResponse response = userService.verifyAccount(token);
        model.addAttribute("response",response);
        return "web/xac-thuc-tai-khoan";
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user != null) {
            return "redirect:/";
        }
        return "/web/forgetpw";
    }

    @GetMapping("/dat-lai-mat-khau")
    public String getResetPasswordPage(@RequestParam String token, Model model) {
        TokenConfirmMessageResponse response = userService.verifyResetPasword(token);
        model.addAttribute("response", response);
        model.addAttribute("token", token);
        return "/web/dat-lai-mat-khau";
    }

}
