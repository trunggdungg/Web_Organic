
    document.addEventListener("DOMContentLoaded", function () {
    // Lấy form thêm địa chỉ mới
    const newAddressForm = document.getElementById("new-address-form");

    // Xử lý sự kiện submit form thêm địa chỉ mới
    if (newAddressForm) {
    newAddressForm.addEventListener("submit", function (event) {
    event.preventDefault();

    // Lấy giá trị từ các trường nhập liệu
    const fullName = document.getElementById("new-fullName").value.trim();
    const phone = document.getElementById("new-phone").value.trim();
    const provinceId = parseInt(document.getElementById("province").value);
    const districtId = parseInt(document.getElementById("district").value);
    const wardId = document.getElementById("ward").value;
    const addressDetail = document.getElementById("new-addressDetail").value.trim();
    const isDefault = document.getElementById("defaultAddress").checked;

    // Kiểm tra thông tin đầu vào
    if (!fullName || !phone || !provinceId || !districtId || !wardId || !addressDetail) {
    alertWarning("Vui lòng điền đầy đủ thông tin địa chỉ");
    return;
}

    // Lấy tên đầy đủ của tỉnh/quận/phường
    const provinceName = document.getElementById("province").options[document.getElementById("province").selectedIndex].text;
    const districtName = document.getElementById("district").options[document.getElementById("district").selectedIndex].text;
    const wardName = document.getElementById("ward").options[document.getElementById("ward").selectedIndex].text;

    // Tạo chuỗi địa chỉ đầy đủ
    const addressSelected = `${wardName}, ${districtName}, ${provinceName}`;

    // Tạo object dữ liệu để gửi đến API
    const addressData = {
    name: fullName,
    phone: phone,
    provinceId: provinceId,
    districtId: districtId,
    wardId: wardId,
    addressDetail: addressDetail,
    addressSelected: addressSelected,
    isDefault: isDefault
};

    // Gọi API tạo địa chỉ mới
    createNewAddress(addressData);
});
}

    // Hàm gọi API tạo địa chỉ mới
    function createNewAddress(addressData) {
    axios.post('/api/address/create', addressData)
    .then(function (response) {
    if (response.status === 200) {
    // Đóng modal
    document.getElementById("new-address-modal").classList.add("hidden");

    // Thêm địa chỉ mới vào danh sách
    const newAddress = response.data;
    addresses.push(newAddress);

    // Nếu là địa chỉ mặc định hoặc chưa có địa chỉ nào, cập nhật hiển thị
    if (newAddress.isDefault || addresses.length === 1) {
    updateDisplayedAddress(newAddress);
    calculateEstimatedDelivery(newAddress.districtId, newAddress.wardCode);
}

    // Cập nhật danh sách địa chỉ trong modal
    updateAddressListInModal();

    // Hiển thị thông báo thành công
    alertSuccess("Thêm địa chỉ mới thành công");
} else {
    alertWarning("Có lỗi xảy ra khi thêm địa chỉ mới");
}
})
    .catch(function (error) {
    console.error("Lỗi khi gọi API tạo địa chỉ:", error);
    alertError("Có lỗi xảy ra khi thêm địa chỉ mới");
});
}

    // Hàm cập nhật danh sách địa chỉ trong modal
    function updateAddressListInModal() {
    const addressListContainer = document.querySelector("#address-modal ul");
    if (!addressListContainer) return;

    // Xóa danh sách địa chỉ cũ
    addressListContainer.innerHTML = "";

    // Thêm các địa chỉ mới vào danh sách
    addresses.forEach(address => {
    const li = document.createElement("li");
    li.className = "flex items-start mb-4 p-2 border-b";
    li.innerHTML = `
                <!-- Radio chọn địa chỉ -->
                <input type="radio" name="selectedAddress" value="${address.id}" ${address.isDefault ? 'checked' : ''} class="mt-1 cursor-pointer" />

                <!-- Nội dung địa chỉ -->
                <div class="flex-grow ml-2 cursor-pointer" onclick="this.previousElementSibling.checked = true;">
                    <div class="flex items-center mb-2">
                        <span class="font-semibold text-lg">${address.fullName}</span>
                        <span class="ml-2 text-gray-600">(+84) ${address.phone}</span>
                    </div>
                    <p class="text-gray-700">${address.addressDetail}</p>
                    <span>${address.addressSelected}</span>
                    ${address.isDefault ? '<span class="text-red-500 mt-2 block">Mặc định</span>' : ''}
                </div>

                <!-- Nút cập nhật -->
                <button class="ml-4 text-blue-600 border border-blue-600 rounded px-2 py-1">Cập nhật</button>
            `;
    addressListContainer.appendChild(li);
});
}
});

    // Hàm cập nhật hiển thị địa chỉ
    function updateDisplayedAddress(address) {
    // Lấy container hiển thị địa chỉ
    const addressContainer = document.querySelector('.flex.items-start.justify-between.mt-2');

    // Nếu không có container, tạo mới
    if (!addressContainer) {
    const newAddressContainer = document.createElement('div');
    newAddressContainer.className = 'flex items-start justify-between mt-2';
    newAddressContainer.setAttribute('data-address-id', address.id);

    newAddressContainer.innerHTML = `
            <div class="ml-2 max-w-[70%] break-words addSelected">
                <span class="text-sm font-bold" id="fullName">${address.fullName}</span>
                <span class="font-bold">(+84)</span>
                <span class="font-bold" id="phone">${address.phone}</span>
                <p class="text-sm overflow-hidden break-words" id="addressDetail">
                   ${address.addressDetail}
                </p>
                <span id="addressSelect">${address.addressSelected}</span>
            </div>
            <div class="ml-auto flex items-center whitespace-nowrap flex-shrink-0">
                <button class="text-red-500 text-sm mr-2">Mặc định</button>
                <button id="change-address-btn" class="border border-gray-300 px-2 py-1 text-sm">
                    Thay đổi
                </button>
            </div>
        `;

    // Thêm container vào DOM
    const container = document.querySelector('.container.mx-auto.p-4');
    container.insertBefore(newAddressContainer, document.querySelector('hr.my-4'));

    // Thêm sự kiện cho nút thay đổi địa chỉ
    document.getElementById('change-address-btn').addEventListener('click', function() {
    document.getElementById('address-modal').classList.remove('hidden');
});
} else {
    // Cập nhật container hiện tại
    addressContainer.setAttribute('data-address-id', address.id);

    const addressContent = addressContainer.querySelector('.addSelected');
    addressContent.innerHTML = `
            <span class="text-sm font-bold" id="fullName">${address.fullName}</span>
            <span class="font-bold">(+84)</span>
            <span class="font-bold" id="phone">${address.phone}</span>
            <p class="text-sm overflow-hidden break-words" id="addressDetail">
               ${address.addressDetail}
            </p>
            <span id="addressSelect">${address.addressSelected}</span>
        `;
}

    // Ẩn thông báo "Bạn chưa có địa chỉ nhận hàng"
    const noAddressMessage = document.querySelector('.flex.items-center.mt-2');
    if (noAddressMessage) {
    noAddressMessage.style.display = 'none';
}
}
    // Hàm reset form thêm địa chỉ mới
    function resetNewAddressForm() {
    document.getElementById("new-fullName").value = "";
    document.getElementById("new-phone").value = "";
    document.getElementById("province").value = "";
    document.getElementById("district").innerHTML = '<option value="">Chọn Quận/Huyện</option>';
    document.getElementById("ward").innerHTML = '<option value="">Chọn Phường/Xã</option>';
    document.getElementById("new-addressDetail").value = "";
    document.getElementById("defaultAddress").checked = false;
}

    // Reset form
resetNewAddressForm();
