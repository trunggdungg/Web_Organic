const productVariantListEl = document.getElementById('product-variant-list');

const renderProductVariantList = (allProducts, variantsByProductId) => {
    let html = "";
    allProducts.forEach(product => {
        html += `
            <tr class="product-row" data-product-id="${product.id}">
                <td class="align-middle">
                    <button class="btn btn-sm btn-link toggle-variants" onclick="toggleVariants(this)">
                        <i class="fas fa-chevron-right"></i>
                    </button>
                </td>
                <td class="align-middle">
                    <img src="${product.imageUrl}" alt="Sản phẩm" class="product-image" style="width: 110px;height: 110px">
                </td>
                <td class="align-middle">${product.name.length > 25 ? product.name.substring(0, 25) + '...' : product.name}</td>
                <td class="align-middle">${product.category.name}</td>
                <td class="align-middle">
                    <span class="badge bg-primary">
                        ${variantsByProductId[product.id] ? variantsByProductId[product.id].length + ' biến thể' : '0 biến thể'}
                    </span>
                </td>
                
            </tr>
        `;

        if (variantsByProductId[product.id]) {
            html += `
                <tr class="variants-row" style="display: none;">
                    <td colspan="6" class="p-0">
                        <div class="variants-container bg-light p-3">
                            <table class="table mb-0">
                                <thead>
                                    <tr>
                                        <th>Giá</th>
                                        <th>Số lượng</th>
                                        <th>Trọng lượng</th>
                                        <th>Mặc định</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
            `;

            variantsByProductId[product.id].forEach(variant => {
                html += `
                    <tr>
                      <td> ${formatCurrency(variant.price)}đ</td>
                        <td>${variant.stock}</td>
                        <td>${variant.weight}</td>
                        <td>
                            <span class="variant-badge ${variant.isDefault ? 'status-active' : 'status-inactive'}">
                                ${variant.isDefault ? 'Mặc định' : 'Không'}
                            </span>
                        </td>
                        <td>
                            <div class="d-flex gap-2">
                                <button class="btn btn-sm btn-info" onclick="openModalUpdateVariant(${variant.id})" title="Sửa">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="deleteVariant(${variant.id})" title="Xóa">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                `;
            });

            html += `
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            `;
        }
    });

    productVariantListEl.innerHTML = html;
};

const btnAddVariantEl = document.getElementById('btn-add-variant');
const variantModalLabelEl = document.getElementById('variantModalLabel');
const productSelectEL = document.getElementById('productSelect');
const priceVariantEl = document.getElementById('variantPrice');
const quantityVariantEl = document.getElementById('variantQuantity');
const weightVariantEl = document.getElementById('variantWeight');
const defaultVariantEl = document.getElementById('variantDefault');
const submitFormVariantEl = document.getElementById('btn-submit-variant');

const modalVariantObj = new bootstrap.Modal(document.getElementById('variantModal'), {
    keyboard: false
});
let idUpdateVariant = null;

btnAddVariantEl.addEventListener('click', () => {
    variantModalLabelEl.innerText = "Thêm biến thể mới";
    priceVariantEl.value = "";
    quantityVariantEl.value = "";
    weightVariantEl.value = "";
    defaultVariantEl.checked = false;
    idUpdateVariant = null;
});

const openModalUpdateVariant = (id) => {
    idUpdateVariant = id;
    // Tìm kiếm variant trong tất cả các sản phẩm
    let variant = null;
    for (const productId in variantsByProductId) {
        variant = variantsByProductId[productId].find(v => v.id === id);
        if (variant) break;
    }

    if (!variant) {
        console.error("Không tìm thấy biến thể với ID:", id);
        return;
    }
    console.log("variant",variant);
    priceVariantEl.value =formatCurrency(variant.price) ;
    quantityVariantEl.value = variant.stock;
    weightVariantEl.value = variant.weight;
    defaultVariantEl.checked = variant.isDefault;
    variantModalLabelEl.innerText = "Cập nhật biến thể";
    modalVariantObj.show();
    idUpdateVariant = id;
};

submitFormVariantEl.addEventListener('click', async (e) => {
    e.preventDefault();
    if (idUpdateVariant) {
        updateVariant();
    } else {
        createVariant();
    }
});

const createVariant = async (productId) => {
   if (productSelectEL.value === "") {
       alertWarning("Vui lòng chọn sản phẩm");
       return;
    }
    if (priceVariantEl.value.trim() === "") {
        alertWarning("Vui lòng nhập giá");
        return;
    }
    if (quantityVariantEl.value.trim() === "") {
        alertWarning("Vui lòng nhập số lượng");
        return;
    }
    if (weightVariantEl.value.trim() === "") {
        alertWarning("Vui lòng nhập trọng lượng");
        return;
    }

    const request = {
        productId:parseInt(productSelectEL.value),
        price: priceVariantEl.value.replaceAll(',', ''),
        stock:parseInt(quantityVariantEl.value),
        weight: weightVariantEl.value,
        isDefault: defaultVariantEl.checked
    }

    console.log("request",request);

    try {
        const response = await axios.post('/api/admin/product-variant/create', request);
        console.log("Response từ API:", response.data);

        if (response.status === 200) {
            alertSuccess("Tạo biến thể thành công");
            modalVariantObj.hide();

            const productId =parseInt(response.data.product.id); // Tránh lỗi undefined
            console.log("Product ID của biến thể:", productId);

            if (!variantsByProductId[productId]) {
                variantsByProductId[productId] = [];
            }
            variantsByProductId[productId].push(response.data);

            console.log("Cập nhật danh sách biến thể:", variantsByProductId);

            // 🔥 Fetch lại danh sách sản phẩm nếu cần
            const productIndex = allProducts.content.findIndex(p => p.id === productId);
            if (productIndex === -1) {
                console.error("Lỗi: Không tìm thấy sản phẩm với ID:", productId);
                return;
            }

            renderProductVariantList(allProducts.content, variantsByProductId);
        }
    } catch (error) {
        console.error("Lỗi khi tạo biến thể:", error);
    }

}

const updateVariant = async () => {
    if (priceVariantEl.value.trim() === "") {
        alertWarning("Vui lòng nhập giá");
        return;
    }
    if (quantityVariantEl.value.trim() === "") {
        alertWarning("Vui lòng nhập số lượng");
        return;
    }
    if (weightVariantEl.value.trim() === "") {
        alertWarning("Vui lòng nhập trọng lượng");
        return;
    }

    // Tìm productId của variant hiện tại
    let currentProductId = null;
    for (const productId in variantsByProductId) {
        const variant = variantsByProductId[productId].find(v => v.id === idUpdateVariant);
        if (variant) {
            currentProductId = parseInt(productId);
            break;
        }
    }

    if (!currentProductId) {
        alertWarning("Không tìm thấy sản phẩm của biến thể");
        return;
    }

    const request = {
        productId: currentProductId, // Sử dụng productId được tìm thấy
        price: priceVariantEl.value.replaceAll(',', ''),
        stock: parseInt(quantityVariantEl.value),
        weight: weightVariantEl.value,
        isDefault: defaultVariantEl.checked
    }

    console.log("request", request);
    try {
        const response = await axios.put(`/api/admin/product-variant/${idUpdateVariant}/update`, request);
        console.log("Kết quả response:", response);
        if (response.status === 200) {
            alertSuccess("Cập nhật biến thể thành công");

            const productId = response.data.product.id;
            console.log("Product ID của biến thể:", productId);

            const productIndex = allProducts.content.findIndex(p => p.id === productId);
            if (productIndex === -1) {
                console.error("Lỗi: Không tìm thấy sản phẩm với ID:", productId);
                return;
            }

            const variantIndex = variantsByProductId[productId].findIndex(v => v.id === idUpdateVariant);
            if (variantIndex === -1) {
                console.error("Lỗi: Không tìm thấy biến thể với ID:", idUpdateVariant);
                return;
            }

            // Cập nhật biến thể trong mảng
            variantsByProductId[productId][variantIndex] = response.data;
            renderProductVariantList(allProducts.content, variantsByProductId);
            modalVariantObj.hide();
        }
    } catch (error) {
        console.error("Lỗi khi cập nhật biến thể:", error);
        // Hiển thị thông báo lỗi chi tiết nếu có
        if (error.response && error.response.data) {
            alertWarning(error.response.data.message || "Có lỗi xảy ra khi cập nhật biến thể");
        } else {
            alertWarning("Có lỗi xảy ra khi cập nhật biến thể");
        }
    }
}

const deleteVariant = async (variantId) => {
    // Tìm productId của variant
    let productId = null;
    let variantToDelete = null;

    for (const pId in variantsByProductId) {
        const variant = variantsByProductId[pId].find(v => v.id === variantId);
        if (variant) {
            productId = pId;
            variantToDelete = variant;
            break;
        }
    }

    if (!productId || !variantToDelete) {
        alertWarning("Không tìm thấy biến thể");
        return;
    }

    // Kiểm tra số lượng biến thể
    const productVariants = variantsByProductId[productId];
    if (productVariants.length <= 1) {
        alertWarning("Không thể xóa. Sản phẩm phải có ít nhất một biến thể.");
        return;
    }

    alertConfirm("Bạn có chắc chắn muốn xóa biến thể này?", async () => {
        try {
            console.log("Đang xóa biến thể có ID:", variantId);
            console.log("Thuộc sản phẩm có ID:", productId);

            const response = await axios.delete(`/api/admin/product-variant/${variantId}/delete`);

            if (response.status === 200) {
                alertSuccess("Xóa biến thể thành công");

                // Xóa biến thể khỏi danh sách
                variantsByProductId[productId] = variantsByProductId[productId].filter(v => v.id !== variantId);

                // Cập nhật lại giao diện
                renderProductVariantList(allProducts.content, variantsByProductId);
            }
        } catch (error) {
            console.error("Chi tiết lỗi khi xóa biến thể:", {
                status: error.response?.status,
                data: error.response?.data,
                message: error.message,
                config: error.config
            });

            const errorMessage = error.response?.data?.message
                || error.response?.data?.error
                || "Có lỗi xảy ra khi xóa biến thể";

            alertWarning(errorMessage);
        }
    });
}

function formatCurrency(value) {
    if (!value) return "0"; // Tránh lỗi nếu price bị null hoặc undefined
    let intPrice = Math.round(value); // Chuyển về số nguyên (bỏ phần thập phân)
    return intPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

renderProductVariantList(allProducts.content, variantsByProductId);