const addressListEl = document.getElementById('address-list');

const renderAddress = (addresses) => {
    addresses.sort((a, b) => b.isDefault - a.isDefault);
    let html = "";
    addresses.forEach(address => {
        html +=`
       <div class="flex justify-between items-start mt-4" >
             <div>
                 <h3 class="text-lg font-semibold " id="name" > <span >${address.fullName}</span>   
               ${address.isDefault ? `<span class="inline-block bg-red-500 text-white px-2 py-1 text-sm rounded mt-2 ml-5" id="default">Mặc định</span>` : ''}
                    </h3>
                 <p class="text-gray-600">(+84) <span id="phone"> ${address.phone}</span></p>
                 <p class="text-gray-600" id="addressDT">${address.addressDetail}</p>
                 <p class="text-gray-600" id="addressSL">${address.addressSelected}g</p>

             </div>
             <div class="flex flex-col items-end">
                 <a href="#" class="text-blue-500 mb-2" onclick="openModalUpdateAddress(${address.id})">Cập nhật</a>
                 <a href="#" class="text-blue-500 mb-2" onclick="deleteAddress(${address.id})">Xóa</a>
                 <button class="border border-gray-300 text-gray-700 px-4 py-2 rounded" id="setIsDefault" onclick="setIsDefault(${address.id})">Thiết lập mặc
                                    định
                 </button>
             </div>
       </div>
        `
    })
    addressListEl.innerHTML = html;
}

const modalAddressObj = new bootstrap.Modal(document.getElementById('new-address-modal'), {
    keyboard: false
});

const nameEl = document.getElementById('nameAdd');
const phoneEl = document.getElementById('phoneAdd');
const addressDetailEl = document.getElementById('addDt');
const addressFormEL = document.getElementById('addressForm');
const provinceSelectEl = document.getElementById('province-select');
const districtSelectEl = document.getElementById('district-select');
const wardSelectEl = document.getElementById('ward-select');
const isDefaultEl = document.getElementById('defaultAddress');
const newAddressBtn = document.getElementById('add-address-btn');
const modalAddressLabelEl = document.getElementById('modalAddressLabel');
const btnSubmitAddressEl = document.getElementById('submit-new-address');
const setIsDefaultEl = document.getElementById('setIsDefault');
let idUpdate = null;

newAddressBtn.addEventListener('click', () => {
    nameEl.value = "";
    phoneEl.value = "";
    addressDetailEl.value = "";
    modalAddressLabelEl.innerText = "Thêm địa chỉ mới";
    isDefaultEl.checked = false;
    idUpdate = null;
})
const openModalUpdateAddress = (id) => {
    idUpdate = id;
    const address = addresses.find(address => address.id === id);
    nameEl.value = address.fullName;
    phoneEl.value = address.phone;
    addressDetailEl.value = address.addressDetail;
    isDefaultEl.checked = address.isDefault;
    console.log("address",address);
    modalAddressLabelEl.innerText = "Cập nhật địa chỉ";
    modalAddressObj.show();
    idUpdate = id;
}

addressFormEL.addEventListener('click', async (e) => {
    e.preventDefault();
    if (idUpdate){
        updateAddress();
    }else {
        createAddress();
    }
})

const createAddress = async () => {
    if (nameEl.value.trim() === "") {
        alertWarning("Vui lòng nhập họ tên");
        return;
    }
    if (phoneEl.value.trim() === "") {
        alertWarning("Vui lòng nhập số điện thoại");
    }
    if (addressDetailEl.value.trim() === "") {
        alertWarning("Vui lòng nhập địa chỉ");
        return;
    }
    if (provinceSelectEl.value === "0") {
        alertWarning("Vui lòng chọn tỉnh/thành phố");
        return;
    }
    if (districtSelectEl.value === "0") {
        alertWarning("Vui lòng chọn quận/huyện");
        return;
    }
    if (wardSelectEl.value === "0") {
        alertWarning("Vui lòng chọn phường/xã");
        return;
    }


    const request = {
        name: nameEl.value,
        phone: phoneEl.value,
        addressDetail: addressDetailEl.value,
        wardId: wardSelectEl.value,
        provinceId: parseInt(provinceSelectEl.value),
        districtId: parseInt(districtSelectEl.value),
        addressSelected: `${wardSelectEl.options[wardSelectEl.selectedIndex].textContent}, ${districtSelectEl.options[districtSelectEl.selectedIndex].textContent}, ${provinceSelectEl.options[provinceSelectEl.selectedIndex].textContent}`,
        isDefault: isDefaultEl.checked ? 1 : 0,
    }
    console.log("address",request);

    try {
        const response = await axios.post("/api/address/create", request);
        if (response.status === 200) {
            alertSuccess("Địa chỉ mới đã được thêm thành công");
            if (request.isDefault === 1) {
                addresses.forEach(addr => addr.isDefault = 0); // Đặt tất cả về false
            }

            addresses.unshift(response.data);
            renderAddress(addresses);
            modalAddressObj.hide();
        }
    }catch (error) {
        alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
        console.log("error",error);
    }

}

const updateAddress = async () => {
    if (nameEl.value.trim() === "") {
        alertWarning("Vui lòng nhập họ tên");
        return;
    }
    if (phoneEl.value.trim() === "") {
        alertWarning("Vui lòng nhập số điện thoại");
    }
    if (addressDetailEl.value.trim() === "") {
        alertWarning("Vui lòng nhập địa chỉ");
        return;
    }
    if (provinceSelectEl.value === "0") {
        alertWarning("Vui lòng chọn tỉnh/thành phố");
        return;
    }
    if (districtSelectEl.value === "0") {
        alertWarning("Vui lòng chọn quận/huyện");
        return;
    }
    if (wardSelectEl.value === "0") {
        alertWarning("Vui lòng chọn phường/xã");
        return;
    }

    const request = {
        name: nameEl.value,
        phone: phoneEl.value,
        addressDetail: addressDetailEl.value,
        wardId: wardSelectEl.value,
        provinceId: parseInt(provinceSelectEl.value),
        districtId: parseInt(districtSelectEl.value),
        addressSelected: `${wardSelectEl.options[wardSelectEl.selectedIndex].textContent}, ${districtSelectEl.options[districtSelectEl.selectedIndex].textContent}, ${provinceSelectEl.options[provinceSelectEl.selectedIndex].textContent}`,
        isDefault: isDefaultEl.checked ? 1 : 0,
    }
    console.log("address",request);

    try {
        const response = await axios.put(`/api/address/update/${idUpdate}`, request);
        if (response.status === 200) {
            alertSuccess("Địa chỉ đã được cập nhật thành công");
            addresses = addresses.map(addr => addr.id === idUpdate ? response.data : addr);
            renderAddress(addresses);
            modalAddressObj.hide();
        }
    }catch (error) {
        alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
        console.log("error",error);
    }
}

const deleteAddress = async (id) => {
    alertConfirm("Bạn có chắc chắn muốn xóa địa chỉ  này?", async () => {
        try {
            const response = await axios.delete(`/api/address/delete/${id}`);
            if (response.status === 200) {
                alertSuccess("Địa chỉ đã được xóa thành công");
                addresses = addresses.filter(addr => addr.id !== id);
                renderAddress(addresses);
            }
        }catch (error) {
            alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
            console.log("error",error);
        }
    });
}

const setIsDefault = async (id) => {
    console.log("id",id);
    alertConfirm("Bạn có chắc chắn muốn đặt địa chỉ này làm mặc định?", async () => {
        try {
            const response = await axios.put(`/api/address/setDefault/${id}`);
            if (response.status === 200) {
                alertSuccess("Địa chỉ đã được đặt làm mặc định");
                addresses.forEach(addr => addr.id === id ? addr.isDefault = 1 : addr.isDefault = 0);
                renderAddress(addresses);
            }
        }catch (error) {
            alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
            console.log("error",error);
        }
    });
}



renderAddress(addresses);