// Xử lý toggle chi tiết sản phẩm
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý toggle chi tiết sản phẩm
    const toggleButtons = document.querySelectorAll('.toggle-product-details');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Tìm hàng cha và lấy product-id
            const productRow = this.closest('.product-row');
            if (!productRow) return;

            const productId = productRow.getAttribute('data-product-id');
            const detailsRow = document.querySelector(`.product-details[data-product-id="${productId}"]`);

            if (detailsRow) {
                // Toggle visibility
                detailsRow.classList.toggle('show');
                productRow.classList.toggle('expanded');
            }
        });
    });
});


const imageListEl = document.getElementById('image-list');

const renderProductImages = (products, productImageMap) => {
    let html = "";
    products.forEach(product => {
        html += `
            <tr class="product-row" data-product-id="${product.id}">
                <td title="${product.name}" class="align-middle">
                    ${product.name.length > 50 ? product.name.substring(0, 50) + '...' : product.name}
                </td>
                <td class="align-middle">
                    <img src="${product.imageUrl}" alt="Hình chính" style="height: 100px; width: 100px; object-fit: cover;">
                </td>
                <td class="align-middle">
                    ${productImageMap[product.id] ? productImageMap[product.id].length + ' hình ảnh' : '0 hình ảnh'}
                </td>
                <td class="align-middle">
                    <button class="btn btn-sm btn-info toggle-product-details">
                        <i class="fas fa-eye"></i>
                    </button>
                </td>
            </tr>
            <tr class="product-details" data-product-id="${product.id}">
                <td colspan="4" class="align-middle">
                    <div class="product-images-container">
                        ${productImageMap[product.id] ? productImageMap[product.id].map(image => `
                            <div class="product-image-item" data-image-id="${image.id}">
                                <img src="${image.imageProduct}" alt="${image.altText || 'Hình ảnh sản phẩm'}">
                                <div class="image-actions">
                                    <button class="btn btn-sm btn-danger delete-image" onclick="deleteImage('${image.id}')">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        `).join('') : '<div class="text-center text-muted">Không có hình ảnh</div>'}
                    </div>
                </td>
            </tr>
        `;
    });
    imageListEl.innerHTML = html;
}


const formEl = document.getElementById("addImageForm");


const uploadAlert = document.getElementById("uploadAlert");

formEl.addEventListener("submit", async function (e) {
    e.preventDefault();

    const productId = document.getElementById("productSelect").value;
    const altText = document.getElementById("altText").value;
    const imageFiles = document.getElementById("imageFiles").files;

    if (!productId || imageFiles.length === 0) {
        alertWarning("Vui lòng chọn sản phẩm và ít nhất một hình ảnh.");
        return;
    }

    uploadAlert.className = "alert alert-info mt-2";
    uploadAlert.textContent = "Đang tải ảnh...";
    uploadAlert.style.display = "block";

    try {
        for (let file of imageFiles) {
            const formData = new FormData();
            formData.append("productId", productId);
            formData.append("altText", altText);
            formData.append("imageUrl", file);

            const res = await axios.post("/api/admin/productImage/upload", formData, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            });
            console.log("Tải ảnh thành công:", res.data);

            // Update productImageMap with the new image
            if (!productImageMap[productId]) {
                productImageMap[productId] = [];
            }
            productImageMap[productId].push(res.data);
        }

        uploadAlert.className = "alert alert-success mt-2";
        alertSuccess("Tải ảnh thành công.");
        renderProductImages(products.content, productImageMap);
        formEl.reset();
        imagePreviewEl.innerHTML = "";

    } catch (err) {
        console.error("Lỗi khi tải ảnh:", err);
        uploadAlert.className = "alert alert-danger mt-2";
        alertError("Đã xảy ra lỗi khi tải ảnh.");
        uploadAlert.textContent = "Đã xảy ra lỗi khi tải ảnh.";
    }

    setTimeout(() => {
        uploadAlert.style.display = "none";
    }, 3000); // Ẩn sau 3 giây
});

const deleteImage = async (id) => {
    alertConfirm("Bạn có chắc chắn muốn xóa hình ảnh này không?", async () => {
        try {
            const res = await axios.delete(`/api/admin/productImage/delete/${id}`);
            console.log("Xóa hình ảnh thành công:", res);
            alertSuccess("Xóa hình ảnh thành công.");
            setTimeout(() => {
                location.reload();
            }   , 1600);
        } catch (err) {
            console.error("Lỗi khi xóa hình ảnh:", err);
            alertError("Đã xảy ra lỗi khi xóa hình ảnh.");
        }
    });
}

renderProductImages(products.content, productImageMap);