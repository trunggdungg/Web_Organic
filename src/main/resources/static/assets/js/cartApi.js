// xử lý nút tăng giảm số lượng sản phẩm
document.addEventListener("DOMContentLoaded", function() {
    calculateTotal();

    // Xử lý sự kiện nút giảm số lượng
    document.querySelectorAll('.decrease-btn').forEach(button => {
        button.addEventListener('click', function() {
            let productId = this.dataset.id;
            let quantityInput = document.querySelector(`.quantity-input[data-id='${productId}']`);

            let quantity = parseInt(quantityInput.value);
            if (quantity > 1) {
                quantity -= 1;
                quantityInput.value = quantity;
                updateRowTotal(quantityInput);
                calculateTotal();

                // Gọi API cập nhật số lượng
                updateQuantity(productId, quantity);
            }
        });
    });

    // Xử lý sự kiện nút tăng số lượng
    document.querySelectorAll('.increase-btn').forEach(button => {
        button.addEventListener('click', function() {
            let productId = this.dataset.id;
            let quantityInput = document.querySelector(`.quantity-input[data-id='${productId}']`);

            let quantity = parseInt(quantityInput.value);
            quantity += 1;
            quantityInput.value = quantity;
            updateRowTotal(quantityInput);
            calculateTotal();

            // Gọi API cập nhật số lượng
            updateQuantity(productId, quantity);
        });
    });

    // Xử lý sự kiện nhập số lượng trực tiếp
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            let productId = this.dataset.id;
            let quantity = parseInt(this.value);

            if (isNaN(quantity) || quantity < 1) {
                quantity = 1;
                this.value = 1;
            }

            updateRowTotal(this);
            calculateTotal();

            // Gọi API cập nhật số lượng
            updateQuantity(productId, quantity);
        });
    });
});


// Xử lý nút xóa sản phẩm
const deleteProduct = async (id) => {
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

    if (!result.isConfirmed) {
        console.log(result);
        return; // Người dùng bấm "Hủy" -> Dừng hàm
    }

    try {
        let res = await axios.delete(`/api/delete-cart-item/${id}`);
        if (res.status === 200) {
            alertSuccess('Xóa sản phẩm thành công');

            // Xóa sản phẩm khỏi giao diện ngay sau khi API thành công
            const row = document.getElementById(`cart-item-${id}`);
            if (row) {
                row.remove();
                calculateTotal(); // Cập nhật lại tổng giá
            }

            // Cập nhật số lượng sản phẩm trên header
            updateCartCount();
            updateCartHeader(); // Cập nhật giỏ hàng trên header
        }
    } catch (error) {
        console.log(error);
        alertError('Xóa sản phẩm thất bại');
    }
};


// Cập nhật thành tiền cho từng dòng
function updateRowTotal(inputElement) {
    const row = inputElement.closest('tr');
    const quantity = parseInt(inputElement.value) || 1;

    // Lấy đơn giá (cần xử lý chuỗi định dạng tiền tệ)
    const priceText = row.querySelector('td:nth-child(2)').textContent.trim();
    const price = parseFloat(priceText.replace(/[^\d]/g, '')) || 0;

    // Tính thành tiền
    const total = price * quantity;

    // Hiển thị thành tiền với định dạng tiền tệ
    row.querySelector('td:nth-child(4)').textContent = formatCurrency(total) + '₫';
}

// Tính tổng cộng tất cả sản phẩm
function calculateTotal() {
    let grandTotal = 0;
    document.querySelectorAll('#cart-itemsPage tbody tr').forEach(row => {
        const totalText = row.querySelector('td:nth-child(4)').textContent.trim();
        const rowTotal = parseFloat(totalText.replace(/[^\d]/g, '')) || 0;
        grandTotal += rowTotal;
    });

    // Hiển thị tổng cộng với định dạng tiền tệ
    document.getElementById('total-price').textContent = formatCurrency(grandTotal) + '₫';
}

// Hàm định dạng số thành chuỗi tiền tệ
function formatCurrency(value) {
    if (!value) return "0"; // Tránh lỗi nếu price bị null hoặc undefined
    let intPrice = Math.round(value); // Chuyển về số nguyên (bỏ phần thập phân)
    return intPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// Cập nhật số lượng sản phẩm trên header
function updateCartCount() {
    let totalItems = 0;
    document.querySelectorAll('#cart-itemsPage tbody tr').forEach(row => {
        const quantityInput = row.querySelector('input[type="text"]');
        totalItems += parseInt(quantityInput.value) || 0;
    });

    const cartCount = document.getElementById('cart-counts');
    if (cartCount) {
        cartCount.textContent = totalItems;
    }
}

// Cập nhật giỏ hàng trên header sau khi xóa
function updateCartHeader() {
    fetch("/api/cart-header")
        .then(response => response.json())
        .then(data => {
            localStorage.setItem('cartData', JSON.stringify(data));
            renderCartItems(data);
            updateSubtotal(data);
        })
        .catch(error => console.error("Lỗi khi cập nhật giỏ hàng:", error));
}

// gọi api tăng giảm số lượng sản phẩm
const updateQuantity = async (id, quantity) => {
    const data = {
        quantity: quantity
    }
    try {
        let res = await axios.patch(`/api/update-cart-item/${id}`, data);
        if (res.status === 200) {
            // alertSuccess('Cập nhật số lượng thành công');
            calculateTotal();
            updateCartCount();
            updateCartHeader();
        }
    } catch (error) {
        console.log(error);
        // alertError('Cập nhật số lượng thất bại');
    }
};



