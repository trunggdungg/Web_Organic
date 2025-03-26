const addressListEl = document.getElementById('address-list');

const renderAddress = (addresses) => {
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
                 <a href="#" class="text-blue-500 mb-2">Xóa</a>
                 <button class="border border-gray-300 text-gray-700 px-4 py-2 rounded">Thiết lập mặc
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

let idUpdate = null;

const openModalUpdateAddress = (id) => {
    idUpdate = id;
    const address = addresses.find(address => address.id === id);
    console.log("address",address);
    modalAddressObj.show();
}

renderAddress(addresses);