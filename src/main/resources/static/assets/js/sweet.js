const Toast = Swal.mixin({
    toast: true,
    position: 'top-right', // Vị trí hiển thị
    showConfirmButton: false,
    timer: 3000, // Thời gian hiển thị
    timerProgressBar: true,
    showCloseButton: true
});

// Hàm hiển thị thông báo thành công
function alertSuccess(message) {
    Toast.fire({
        icon: 'success',
        title: message
    });
}

// Hàm hiển thị thông báo lỗi
function alertError(message) {
    Toast.fire({
        icon: 'error',
        title: message
    });
}

// Hàm hiển thị thông báo cảnh báo
function alertWarning(message) {
    Toast.fire({
        icon: 'warning',
        title: message
    });
}

// Hàm hiển thị thông báo thông tin
function alertInfo(message) {
    Toast.fire({
        icon: 'info',
        title: message
    });
}

// Hàm xác nhận có callback khi người dùng nhấn OK
function alertConfirm(message, callback) {
    Swal.fire({
        title: "Xác nhận",
        text: message,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Đồng ý",
        cancelButtonText: "Hủy"
    }).then((result) => {
        if (result.isConfirmed && typeof callback === "function") {
            callback();
        }
    });
}