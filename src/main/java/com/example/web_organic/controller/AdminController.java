package com.example.web_organic.controller;

import com.example.web_organic.entity.*;
import com.example.web_organic.modal.Enum.Category_Type;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private DashboardService dashboardService;
    @GetMapping("/home")
    public String home(Model model) {
    // Lấy thống kê từ service
        model.addAttribute("stats", dashboardService.getDashboardStats());
        // Thêm dữ liệu cho biểu đồ
        model.addAttribute("dailyRevenue", dashboardService.getDailyRevenue());
        model.addAttribute("monthlyRevenue", dashboardService.getMonthlyRevenue());
        model.addAttribute("orderStatusDistribution", dashboardService.getOrderStatusDistribution());
        model.addAttribute("recentOrders", dashboardService.getRecentOrders());
        model.addAttribute("recentUsers", dashboardService.getTop5Users());
        model.addAttribute("productCategoryDistribution", dashboardService.getProductCategoryDistribution());
        model.addAttribute("productHot", dashboardService.getHotProducts());
        model.addAttribute("stockAlert", dashboardService.getStockAlerts());
        model.addAttribute("customerDistributionByProvince", dashboardService.getCustomerDistributionByProvince());
        return "admin/home";
    }
    @GetMapping("/revenue-chart-data")
    @ResponseBody
    public Map<String, Object> getRevenueChartData(@RequestParam String range) {
        return dashboardService.getRevenueChartData(range);
    }

    @GetMapping("/custom-date-range-revenue")
    @ResponseBody
    public Map<String, Object> getCustomDateRangeRevenue(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return dashboardService.getCustomDateRangeRevenue(start, end);
    }


    @GetMapping("/order")
    public String order(@RequestParam(required = false, defaultValue = "1") int page,
                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                        @RequestParam(required = false) String type, Model model) {

        Page<Order> orders = Page.empty();
        if ("pending".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.PENDING);
        } else if ("processing".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.PROCESSING);
        } else if ("shipped".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.SHIPPED);
        } else if ("completed".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.COMPLETED);
        } else if ("canceled".equalsIgnoreCase(type)) {
            // Lấy toàn bộ danh sách canceled và returned
            List<Order> canceledOrders = orderService.getAllOrdersByStatus(Status_Enum.CANCELED);
            List<Order> returnedOrders = orderService.getAllOrdersByStatus(Status_Enum.RETURNED);

            // Gộp danh sách
            List<Order> mergedOrders = Stream.concat(
                canceledOrders.stream(),
                returnedOrders.stream()
            ).collect(Collectors.toList());

            // Tổng số đơn hàng sau gộp
            int total = mergedOrders.size();

            // Tính toán phân trang thủ công
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Order> pagedOrders = mergedOrders.subList(start, end);

            orders = new PageImpl<>(pagedOrders, PageRequest.of(page - 1, pageSize), total);
        }

        else {
            orders = orderService.getAllOrders(page, pageSize); // Lấy tất cả đơn hàng nếu không có type
        }

        Map<String, List<OrderDetail>> orderDetailsMap = orders.getContent().stream()
            .collect(Collectors.toMap(
                Order::getId,
                order -> orderService.getOrderDetailsByOrderId(order.getId())
            ));

        model.addAttribute("orders", orders);
        model.addAttribute("orderDetailsMap", orderDetailsMap);
        model.addAttribute("type", type); // Truyền vào model để highlight menu
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalOrders", orderService.countAll());
        model.addAttribute("pendingOrders", orderService.countByStatus("PENDING"));
        model.addAttribute("cancelledOrders", orderService.countByStatus("CANCELED"));


        return "admin/orders-list";
    }


    @GetMapping("/user-list")
    public String userList(@RequestParam(required = false,defaultValue = "1") int page,
                           @RequestParam(required = false,defaultValue = "10") int pageSize,
                           Model model) {
            Page<User> users = userService.getAllUsers(page, pageSize);
            model.addAttribute("users", users);
            model.addAttribute("currentUser",page);
            model.addAttribute("totalPages",users.getTotalPages());
        return "admin/user-list";
    }


    @GetMapping("/blog-list")
    public String blogList(@RequestParam(required = false,defaultValue = "1") int page,
                           @RequestParam(required = false,defaultValue = "5") int pageSize,
                           Model model) {
        Page<Blog> blogs = blogService.getAllBlogs(page, pageSize);
        List<Category> categories = categoryService.getCategoryByTypeAndStatus(Category_Type.BLOG);
        model.addAttribute("categories", categories);
        model.addAttribute("blogs", blogs);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",blogs.getTotalPages());
        return "admin/blog-list";
    }
    @GetMapping("/category")
    public String category(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/category";
    }

    @GetMapping("/product")
    public String product(@RequestParam(required = false,defaultValue = "1") int page,
                          @RequestParam(required = false,defaultValue = "10") int pageSize,
                          Model model) {
        Page<Product> products = productService.getAllProducts(page, pageSize);
        List<Category> categories = categoryService.getCategoryByTypeAndStatus(Category_Type.PRODUCT);
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "admin/product-list";
    }

    @GetMapping("/product-variant")
    public String productVariant(@RequestParam(required = false, defaultValue = "1") int page,
                                 @RequestParam(required = false, defaultValue = "10") int pageSize,
                                 Model model) {
        // Lấy tất cả sản phẩm (có phân trang)
        Page<Product> allProducts = productService.getAllProducts(page, pageSize);
        List<Product> products = productService.getAllProduct();

        // Lấy danh sách ID của các sản phẩm để truy vấn biến thể
        List<Integer> productIds = allProducts.getContent().stream()
            .map(Product::getId)
            .collect(Collectors.toList());

        // Lấy tất cả các biến thể của các sản phẩm này
        Map<Integer, List<ProductVariants>> variantsByProductId = productService.getVariantsByProductIds(productIds)
            .stream()
            .collect(Collectors.groupingBy(variant -> variant.getProduct().getId()));

        model.addAttribute("allProducts", allProducts);
        model.addAttribute("products", products);
        model.addAttribute("variantsByProductId", variantsByProductId);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", allProducts.getTotalPages());

        return "admin/product-variant";
    }

    @GetMapping("/product-image")
    public String productImage(@RequestParam(required = false, defaultValue = "1") int page,
                                 @RequestParam(required = false, defaultValue = "10") int pageSize,
                                 Model model) {
        Page<Product> products = productService.getAllProducts(page, pageSize);
        List<Product> allProducts = productService.getAllProduct();
        // Lấy tất cả productId trên trang hiện tại
        List<Integer> productIds = products.stream()
            .map(Product::getId)
            .toList();

        // Lấy danh sách hình ảnh theo các productId này
        List<ProductImage> allImages = productImageService.findByProductIds(productIds);

        // Gom nhóm hình ảnh theo productId
        Map<Integer, List<ProductImage>> productImageMap = allImages.stream()
            .collect(Collectors.groupingBy(img -> img.getProduct().getId()));
        model.addAttribute("allProducts", allProducts);
        model.addAttribute("products", products);
        model.addAttribute("productImageMap", productImageMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", products.getTotalPages());

        return "admin/product-image";
    }

    @GetMapping("/brand")
    public String brand(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);

        return "admin/brand";
    }

    @GetMapping("/chat-admin")
    public String chatAdmin() {

        return "admin/chat-admin";
    }
}
