function viewOrderDetails(btn) {
    // Get the current row and its data-order-id
    const currentRow = btn.closest('tr');
    const orderId = currentRow.getAttribute('data-order-id');

    // Find the details row that matches this order
    const detailsRow = document.querySelector(`tr.order-details-row[data-order-id="${orderId}"]`);

    // Close all other open details first
    document.querySelectorAll('.order-details-row').forEach(row => {
        if (row !== detailsRow) {
            row.style.display = 'none';
            // Reset the eye icon for other rows
            const otherBtn = document.querySelector(`tr[data-order-id="${row.getAttribute('data-order-id')}"] .btn-info i`);
            if (otherBtn) {
                otherBtn.classList.remove('fa-eye-slash');
                otherBtn.classList.add('fa-eye');
            }
        }
    });

    // Toggle current details
    if (detailsRow.style.display === 'none' || detailsRow.style.display === '') {
        detailsRow.style.display = 'table-row';

        // Update button icon
        const icon = btn.querySelector('i');
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
        btn.title = "Ẩn chi tiết";
    } else {
        detailsRow.style.display = 'none';

        // Update button icon
        const icon = btn.querySelector('i');
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
        btn.title = "Chi tiết";
    }
}

// View Order Details
// function viewOrderDetails(btn) {
//     // Close all other open details first
//     document.querySelectorAll('.order-details.show').forEach(row => {
//         if (row !== btn.closest('tr').nextElementSibling) {
//             row.classList.remove('show');
//         }
//     });
//
//     // Toggle current details
//     const detailsRow = btn.closest('tr').nextElementSibling;
//     detailsRow.classList.toggle('show');
//
//     // Update button icon
//     const icon = btn.querySelector('i');
//     if (detailsRow.classList.contains('show')) {
//         icon.classList.remove('fa-eye');
//         icon.classList.add('fa-eye-slash');
//         btn.title = "Ẩn chi tiết";
//     } else {
//         icon.classList.remove('fa-eye-slash');
//         icon.classList.add('fa-eye');
//         btn.title = "Chi tiết";
//     }
// }



// Filter functions
function resetFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('statusFilter').value = '';
    document.getElementById('paymentFilter').value = '';
    applyFilters();
}

function applyFiltersOrder() {
    const search = document.getElementById('searchInput').value.toLowerCase();
    const status = document.getElementById('statusFilter').value;
    const payment = document.getElementById('paymentFilter').value;

    // Implement your filter logic here
    console.log('Applying filters:', { search, status, payment });
}

// Export function
function exportOrders() {
    console.log('Exporting orders to Excel...');
    // Implement your export logic here
}



// Sidebar submenu toggle
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.sidebar-section-header').forEach(header => {
        header.addEventListener('click', function() {
            this.classList.toggle('active');
            const submenu = this.nextElementSibling;
            submenu.classList.toggle('show');

        });
    });


    // Open Orders Management section by default
    const ordersSection = document.querySelector('.sidebar-section:first-child');
    if (ordersSection) {
        const header = ordersSection.querySelector('.sidebar-section-header');
        const submenu = ordersSection.querySelector('.sidebar-submenu');
        header.classList.add('active');
        submenu.classList.add('show');
    }
});



const renderOrders = (orders) => {
    const orderListEl = document.getElementById('order-list');
    if (!orderListEl) {
        return;
    }
    let html = "";
    orders.forEach(order => {
        html += `
        <tr data-order-id="${order.id}">
            <td>${order.fullName}</td>
            <td>
                ${order.addressDetail} <br/> ${order.addressSelect}
            </td>
            <td>${order.phone}</td>
            <td>${new Date(order.orderDate).toLocaleDateString('vi-VN', { day: '2-digit', month: 'short', year: 'numeric' })}</td>
            <td>${order.paymentMethod}</td>
            <td>
                <span class="payment-${order.paymentStatus.toLowerCase()}">
                    ${order.paymentStatus}
                </span>
            </td>
            <td>
                <span class="status-${order.status.toLowerCase()}">
                    ${order.status}
                </span>
            </td>
            <td>${order.total.toLocaleString('vi-VN')}₫</td>
            <td>
                <div class="d-flex gap-2">
                    <button class="btn btn-sm btn-info" onclick="viewOrderDetails(this)" title="Chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>

                    ${!["CANCELED", "COMPLETED", "RETURNED"].includes(order.status) ? `
                        ${order.status === "SHIPPED" ? `
                            <button class="btn btn-sm btn-success" onclick="updateStatus(this, true)" title="Khách đã nhận hàng">
                                Đã nhận
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="updateStatus(this, false)" title="Khách không nhận hàng">
                                Không nhận
                            </button>
                        ` : `
                            <button class="btn btn-sm btn-primary" onclick="updateStatus(this, true)" title="Xác nhận">
                                Xác nhận
                            </button>
                        `}
                    ` : ''}
                </div>
            </td>
        </tr>
        
         <tr class="order-details-row" style="display:none;" data-order-id="${order.id}">
            <td colspan="9">
                <div class="order-details-content">
                    <div class="order-details-header">
                        <h6>Chi tiết đơn hàng</h6>
                        <span class="text-muted">Mã đơn: ${order.id}</span>
                    </div>
                    <div class="product-list">
                        ${orderDetailsMap[order.id] ? orderDetailsMap[order.id].map(detail => `
                            <div class="product-item">
                                <div class="product-image">
                                    <img src="${detail.productVariant.product.imageUrl}" alt="Sản phẩm">
                                </div>
                                <div class="product-info">
                                    <p class="product-name">${detail.productVariant.product.name}</p>
                                    <p class="product-variant">Phân loại: ${detail.productVariant.weight}</p>
                                    <p class="product-quantity">Số lượng: ${detail.quantity}</p>
                                </div>
                                <div class="product-price">
                                    <p class="unit-price">Đơn giá: ${detail.unitPrice.toLocaleString('vi-VN')}₫</p>
                                    <p class="total-price">Thành tiền: ${detail.subTotal.toLocaleString('vi-VN')}₫</p>
                                </div>
                            </div>
                        `).join('') : ''}
                    </div>
                    <div class="order-summary">
                        <p>
                            <span>Tạm tính:</span>
                            <span>${order.productCost.toLocaleString('vi-VN')}₫</span>
                        </p>
                        <p>
                            <span>Phí vận chuyển:</span>
                            <span>${order.shippingCost.toLocaleString('vi-VN')}₫</span>
                        </p>
                        <p>
                            <span>Tổng cộng:</span>
                            <span>${order.total.toLocaleString('vi-VN')}₫</span>
                        </p>
                    </div>
                </div>
            </td>
        </tr>
        `;
    });

    orderListEl.innerHTML = html;
};


renderOrders(orders.content);