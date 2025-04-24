document.addEventListener('DOMContentLoaded', function () {
    // Lấy tất cả các nút "Hủy" trong đơn hàng chờ xác nhận
    const cancelButtons = document.querySelectorAll('.cancel-order-btn');
    const cancelOrderModal = new bootstrap.Modal(document.getElementById('cancel-order-modal'));
    const confirmCancelBtn = document.getElementById('confirm-cancel-order');
    const cancelOrderIdInput = document.getElementById('cancel-order-id');
    const otherReasonRadio = document.getElementById('reason7');
    const otherReasonContainer = document.getElementById('other-reason-container');
    const otherReasonTextarea = document.getElementById('other-reason');

    // Hiển thị textarea khi chọn "Lý do khác"
    otherReasonRadio.addEventListener('change', function () {
        if (this.checked) {
            otherReasonContainer.classList.remove('d-none');
        } else {
            otherReasonContainer.classList.add('d-none');
        }
    });

    // Ẩn textarea khi chọn các lý do khác
    document.querySelectorAll('input[name="cancelReason"]').forEach(radio => {
        if (radio.id !== 'reason7') {
            radio.addEventListener('change', function () {
                if (this.checked) {
                    otherReasonContainer.classList.add('d-none');
                }
            });
        }
    });

    // Xử lý sự kiện click cho các nút "Hủy"
    cancelButtons.forEach(button => {
        button.addEventListener('click', function () {
            const orderId = this.getAttribute('data-order-id');
            cancelOrderIdInput.value = orderId;
            cancelOrderModal.show();
        });
    });

    // Xử lý sự kiện khi xác nhận hủy đơn hàng
    confirmCancelBtn.addEventListener('click', function () {
        const orderId = cancelOrderIdInput.value;
        const selectedReason = document.querySelector('input[name="cancelReason"]:checked').value;
        let reason = selectedReason;

        // Nếu chọn "Lý do khác", lấy nội dung từ textarea
        if (selectedReason === 'other') {
            const customReason = otherReasonTextarea.value.trim();
            if (customReason === '') {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Vui lòng nhập lý do hủy đơn hàng'
                });
                return;
            }
            reason = customReason;
        }

        // Gọi API hủy đơn hàng
        cancelOrder(orderId, reason);
    });

    // Hàm gọi API hủy đơn hàng
    function cancelOrder(orderId, reason) {
        // Hiển thị loading
        Swal.fire({
            title: 'Đang xử lý...',
            html: 'Vui lòng chờ trong giây lát',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // Gọi API hủy đơn hàng
        axios.post('/api/orders/cancel', {
            orderId: orderId,
            reason: reason
        })
            .then(response => {
                if (response.data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Thành công',
                        text: 'Đơn hàng đã được hủy thành công',
                        confirmButtonText: 'OK'
                    }).then(() => {
                        // Đóng modal và reload trang
                        cancelOrderModal.hide();
                        location.reload();
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Lỗi',
                        text: response.data.message || 'Có lỗi xảy ra khi hủy đơn hàng'
                    });
                }
            })
            .catch(error => {
                console.error('Error cancelling order:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: error.response?.data?.message || 'Có lỗi xảy ra khi hủy đơn hàng'
                });
            });
    }
});
