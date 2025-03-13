// Lưu giỏ hàng vào localStorage
document.addEventListener("DOMContentLoaded", function () {
    // Kiểm tra xem có dữ liệu trong localStorage không
    const cachedCart = localStorage.getItem('cartData');
    if (cachedCart) {
        const data = JSON.parse(cachedCart);
        renderCartItems(data);
        // updateSubtotal(data);
    }

    // Vẫn gọi API để cập nhật dữ liệu mới nhất
    fetch("/api/cart-header")
        .then(response => {
            if (!response.ok) {
                throw new Error("User not logged in or unable to fetch cart items.");
            }
            return response.json();
        })
        .then(data => {
            // Lưu vào localStorage
            localStorage.setItem('cartData', JSON.stringify(data));

            const cartContainer = document.getElementById("cart-items-container");
            const cartCount = document.getElementById("cart-count");
            const cartTotal = document.getElementById("cart-total");

            if (!data || data.length === 0) {
                cartContainer.innerHTML = "<p class='text-center text-gray-500 ' style='height: 50px;font-size: 30px'>Giỏ hàng trống.</p>";
                cartCount.textContent = "0";
                cartTotal.textContent = "0₫";
            } else {
                renderCartItems(data);
                updateSubtotal(data);
            }
        })
        .catch(error => {
            console.error("Lỗi khi tải giỏ hàng:", error);
        });
});

// Hiển thị danh sách sản phẩm trong giỏ hàng
function renderCartItems(cartItems) {
    const cartContainer = document.getElementById("cart-items-container");
    cartContainer.innerHTML = ""; // Xóa nội dung cũ

    cartItems.forEach(item => {
        const originalPrice = item.productVariant.price;
        const discount = item.productVariant.product.discount || 0; // Giảm giá (nếu có)
        const finalPrice = originalPrice - (originalPrice * discount / 100); // Giá sau giảm

        const itemElement = `
            <div class="p-4 border-b border-gray-200 flex items-center">
                <img alt="${item.productVariant.product.name}" class="w-16 h-16 object-cover rounded"
                     src="${item.productVariant.product.imageUrl}" />
                <div class="ml-4 flex-1">
                    <h3 class="text-sm font-semibold">${item.productVariant.product.name}</h3>
                    <p class="text-xs text-gray-500">x ${item.quantity} (${item.productVariant.weight})</p>
                    <p class="text-red-500 font-semibold">
                        ${formatCurrency(finalPrice)} ₫ 
                        ${discount > 0 ? `<span class="line-through text-gray-400">${formatCurrency(originalPrice)}₫</span>` : ""}
                        
                    </p>
                </div>
                <button class="text-gray-500 ml-4" onclick="removeItem(${item.id})">
                    <i class="fas fa-times"></i>
                </button>
            </div>`;
        cartContainer.innerHTML += itemElement;
    });
}

// Cập nhật tổng số lượng và tổng tiền
function updateSubtotal(cartItems) {
    let totalItems = 0;
    let totalPrice = 0;

    cartItems.forEach(item => {
        const originalPrice = item.productVariant.price;
        const discount = item.productVariant.product.discount || 0; // Lấy giảm giá nếu có
        const finalPrice = originalPrice - (originalPrice * (discount / 100)); // Giá sau giảm

        // totalItems += cartItems.length;
        totalPrice += item.quantity * finalPrice; // Cộng tổng tiền với giá đã giảm
    });


    document.getElementById("cart-count").textContent = cartItems.length;
    document.getElementById("cart-counts").textContent = cartItems.length;
    document.getElementById("cart-total").textContent = formatCurrency(totalPrice) + ' ₫';
}

// Hàm định dạng số thành chuỗi tiền tệ
function formatCurrency(value) {
    if (!value) return "0"; // Tránh lỗi nếu price bị null hoặc undefined
    let intPrice = Math.round(value); // Chuyển về số nguyên (bỏ phần thập phân)
    return intPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
// Xóa sản phẩm khỏi giỏ hàng
async function removeItem(itemId) {
    // Hiển thị hộp thoại xác nhận trước khi xóa
    const result = await Swal.fire({
        title: "Bạn có chắc chắn muốn xóa?",
        text: "Hành động này không thể hoàn tác!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Đồng ý",
        cancelButtonText: "Hủy"
    });

    // Nếu người dùng chọn "Hủy", dừng hàm
    if (!result.isConfirmed) {
        return;
    }

    console.log("Xóa sản phẩm:", itemId);
    console.log(`Gửi yêu cầu DELETE đến: /api/delete-cart-item/${itemId}`);

    try {
        let res = await axios.delete(`/api/delete-cart-item/${itemId}`);
        if (res.status === 200) {
            alertSuccess('Xóa sản phẩm thành công');

            // Cập nhật LocalStorage
            let cartData = JSON.parse(localStorage.getItem('cartData')) || [];
            cartData = cartData.filter(item => item.id !== itemId);
            localStorage.setItem('cartData', JSON.stringify(cartData));

            // Cập nhật giỏ hàng trên giao diện
            renderCartItems(cartData);
            updateSubtotal(cartData);

            // Cập nhật số lượng sản phẩm trên header
            document.getElementById("cart-count").textContent = cartData.length;
            document.getElementById("cart-counts").textContent = cartData.length;
        }
    } catch (error) {
        console.log(error);
        alertError('Xóa sản phẩm thất bại');
    }
}


//đang lỗi