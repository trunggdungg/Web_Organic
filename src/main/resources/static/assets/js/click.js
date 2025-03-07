// click chọn nút size trong product detail
document.addEventListener('DOMContentLoaded', function() {
    // Lấy tất cả các button kích thước
    const sizeButtons = document.querySelectorAll('.flex.space-x-2.mt-3 button');

    // Thêm sự kiện click cho mỗi button
    sizeButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Không cho phép chọn kích thước hết hàng
            if (this.classList.contains('out-of-stock')) {
                return; // Không thực hiện thao tác nếu hết hàng
            }

            // Đầu tiên, reset tất cả các button về trạng thái mặc định
            sizeButtons.forEach(btn => {
                if (!btn.classList.contains('out-of-stock')) {
                    btn.classList.remove('border-black', 'text-black');
                    btn.classList.add('border-gray-400', 'text-gray-800');
                }
            });

            // Sau đó, thêm class đặc biệt cho button được click
            this.classList.remove('border-gray-400', 'text-gray-800');
            this.classList.add('border-black', 'text-black');

            // Lưu kích thước đã chọn
            const selectedSize = this.textContent.trim();
            document.getElementById('selectedSizeInput').value = selectedSize;
        });
    });

    // Chọn button đầu tiên mặc định (nếu không hết hàng)
    const firstAvailableButton = Array.from(sizeButtons).find(btn => !btn.classList.contains('out-of-stock'));
    if (firstAvailableButton) {
        firstAvailableButton.click();
    }
});