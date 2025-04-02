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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/home")
    public String home() {
        return "admin/home";
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
            // Lấy danh sách đơn bị hủy và trả hàng
            Page<Order> canceledOrders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.CANCELED);
            Page<Order> returnedOrders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.RETURNED);

            // Gộp hai danh sách
            List<Order> mergedOrders = Stream.concat(
                canceledOrders.getContent().stream(),
                returnedOrders.getContent().stream()
            ).collect(Collectors.toList());

            // Tổng số phần tử để hỗ trợ phân trang
            long totalElements = canceledOrders.getTotalElements() + returnedOrders.getTotalElements();

            // Tạo Page mới với dữ liệu đã gộp
            orders = new PageImpl<>(mergedOrders, PageRequest.of(page, pageSize), totalElements);
        }
        else {
            orders = orderService.getAllOrders(page, pageSize); // Lấy tất cả đơn hàng nếu không có type
        }

        model.addAttribute("orders", orders);
        model.addAttribute("type", type); // Truyền vào model để highlight menu
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
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

    @GetMapping("/create-blog")
    public String createBlog() {
        return "admin/create-blog";
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


}
