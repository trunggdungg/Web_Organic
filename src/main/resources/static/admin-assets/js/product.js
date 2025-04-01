const productListEl = document.getElementById('product-list');

const renderProductList = (products) => {
    let html = "";
    products.forEach(product => {
        html += `
            <tr data-product-id="${product.id}">
                <td class="align-middle"><input type="checkbox" class="form-check-input product-select"></td>
                <td class="align-middle"><img src="${product.imageUrl}" alt="Product" class="product-image w-50 h-50"></td>
                <td class="align-middle">${product.name}</td>
                <td class="align-middle">${product.brand.nameBrand}</td>
                <td class="align-middle">${product.category.name}</td>
                <td class="align-middle">
                    ${product.description.length > 20 ? product.description.substring(0, 20) + '...' : product.description}
                </td>
                <td class="align-middle">
                    <span class="${product.status ? 'status-active' : 'status-inactive'}">
                        ${product.status ? 'Hoạt động' : 'Không hoạt động'}
                    </span>
                </td>
                <td class="align-middle"><span class="discount-badge ">${product.discount}%</span></td>
                <td class="align-middle"><span class="featured-badge ${product.isFeatured ? 'status-active' : 'status-inactive'}" >${product.isFeatured ? 'Có' : 'Không'}</span></td>
                <td class="align-middle">
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-info" onclick="openModalUpdateProduct(${product.id})" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteProductAdmin(${product.id})" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
    productListEl.innerHTML = html;
}

const modalProductObj = new bootstrap.Modal(document.getElementById('productModal'), {
    keyboard: false
});

const btnAddProductEl = document.getElementById('btn-add-modal');
const productModalLabelEl = document.getElementById('productModalLabel');
const nameProductEl = document.getElementById('nameProduct');
const brandProductEl = document.getElementById('brandProduct');
const imageUrlProductEl = document.getElementById('imageProduct');
const categoryProductEl = document.getElementById('categoryProduct');
const statusProductEl = document.getElementById('statusProduct');
const discountProductEl = document.getElementById('discountProduct');
const descriptionProductEl = document.getElementById('descriptionProduct');
const isFeaturedProductEl = document.getElementById('isFeatured');
const submitFormProductEl = document.getElementById('btn-save-product');
let idUpdateProduct = null;

btnAddProductEl.addEventListener('click', () => {
    productModalLabelEl.innerText = "Thêm sản phẩm mới";
    imageUrlProductEl.value = "";
    nameProductEl.value = "";
    brandProductEl.value = "";
    categoryProductEl.value = "";
    statusProductEl.value = "inactive";
    statusProductEl.disabled = true;
    discountProductEl.value = 0;
    descriptionProductEl.value = "";
    isFeaturedProductEl.checked = false;
    idUpdateProduct = null;
});

const openModalUpdateProduct = (id) => {
    idUpdateProduct = id;
    const product = products.content.find(product => product.id === id);
    nameProductEl.value = product.name;

    // Set the selected brand option
    const brandOption = Array.from(brandProductEl.options).find(option => option.text === product.brand.nameBrand);
    if (brandOption) {
        brandProductEl.value = brandOption.value;
    }

    // Set the selected category option
    const categoryOption = Array.from(categoryProductEl.options).find(option => option.text === product.category.name);
    if (categoryOption) {
        categoryProductEl.value = categoryOption.value;
    }

    statusProductEl.value = product.status ? 'active' : 'inactive';
    discountProductEl.value = product.discount;
    isFeaturedProductEl.checked = product.isFeatured;
    descriptionProductEl.value = product.description;
    productModalLabelEl.innerText = "Cập nhật sản phẩm";
    // Set the selected image
    const selectedImage = document.getElementById('selected-image');
    selectedImage.src = product.imageUrl;
    selectedImage.style.display = 'block';
    modalProductObj.show();
    idUpdateProduct = id;
};

submitFormProductEl.addEventListener('click', async (e) => {
    e.preventDefault();
    if (idUpdateProduct) {
        updateProduct();
    } else {
        createProduct();
    }
});
// xử lý chọn ảnh
document.addEventListener('DOMContentLoaded', function() {
    const imageInput = document.getElementById('imageProduct');
    const selectedImage = document.getElementById('selected-image');
    const btnShowUploadedImages = document.getElementById('btn-show-uploaded-images');
    const uploadedImagesContainer = document.getElementById('uploaded-images-container');
    const uploadedImagesModal = new bootstrap.Modal(document.getElementById('uploadedImagesModal'));

    // Xử lý khi chọn file
    imageInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        // imageUrlProductEl.value = "";
        if (file) {
            const reader = new FileReader();
            // console.log('File:', file);
            reader.onload = function(event) {
                // console.log('File URL:', event.target.result);
                selectedImage.src = event.target.result;
                selectedImage.style.display = 'block';
            }
            reader.readAsDataURL(file);
        }
    });

    // Nút hiển thị ảnh đã tải
    btnShowUploadedImages.addEventListener('click', function() {
        // Gọi API lấy danh sách ảnh đã tải
        fetch('/api/admin/product/uploaded-images')
            .then(response => response.json())
            .then(images => {
                // Xóa các ảnh cũ
                uploadedImagesContainer.innerHTML = '';

                // Thêm từng ảnh vào modal
                images.forEach(image => {
                    const col = document.createElement('div');
                    col.className = 'col-md-3 mb-3';

                    const img = document.createElement('img');
                    img.src = image.url;
                    img.className = 'img-fluid img-thumbnail cursor-pointer';

                    img.addEventListener('click', function() {
                        // Chọn ảnh
                        selectedImage.src = image.url;
                        console.log("image.url",image.url);
                        selectedImage.style.display = 'block';
                        imageUrlProductEl.value = "";
                        uploadedImagesModal.hide();
                    });

                    col.appendChild(img);
                    uploadedImagesContainer.appendChild(col);
                });

                // Hiển thị modal
                uploadedImagesModal.show();
            })
            .catch(error => {
                console.error('Lỗi tải ảnh:', error);
            });
    });

    // Nút xóa ảnh
    document.getElementById('btn-delete-image').addEventListener('click', function() {
        selectedImage.src = '';
        selectedImage.style.display = 'none';
        imageInput.value = ''; // Xóa giá trị input file
    });
});

async function uploadImageFromUrl(imageUrl, productId) {
    try {
        const response = await fetch(imageUrl);
        const blob = await response.blob();
        const file = new File([blob], "uploaded_image.jpg", { type: blob.type });

        const formData = new FormData();
        formData.append("file", file);

        const uploadResponse = await axios.post(`/api/admin/product/${productId}/upload-poster`, formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        });

        return uploadResponse.data.url;
    } catch (error) {
        console.error("Lỗi upload ảnh từ URL:", error);
        return null;
    }
}

const createProduct = async () => {
     const selectedImage = document.getElementById('selected-image');
        if (!selectedImage.src || selectedImage.src === window.location.href) {
            alertWarning("Vui lòng chọn ảnh sản phẩm");
            return;
        }
    if (nameProductEl.value.trim() === "") {
        alertWarning("Vui lòng nhập tên sản phẩm");
        return;
    }
    if (brandProductEl.value.trim() === "") {
        alertWarning("Vui lòng chọn thương hiệu");
        return;
    }
    if (categoryProductEl.value.trim() === "") {
        alertWarning("Vui lòng chọn danh mục");
        return;
    }
   if (descriptionProductEl.value.trim() === "") {
        alertWarning("Vui lòng nhập mô tả sản phẩm");
        return;
   }


    const request = {
        name: nameProductEl.value,
        brand: parseInt(brandProductEl.value),
        category: parseInt(categoryProductEl.value),
        status: statusProductEl.value === 'active',
        discount: parseInt(discountProductEl.value),
        description: descriptionProductEl.value,
        isFeatured: isFeaturedProductEl.checked
    };
console.log("request",request);
    try {
        // Tạo sản phẩm trước
        const productResponse = await axios.post('/api/admin/product/create', request);

        // Nếu tạo sản phẩm thành công
        if (productResponse.status === 200) {

            const createdProduct = productResponse.data;
            // Thêm thông tin brand và category đầy đủ
            createdProduct.brand = {
                id: parseInt(brandProductEl.value),
                nameBrand: brandProductEl.options[brandProductEl.selectedIndex].text
            };

            createdProduct.category = {
                id: parseInt(categoryProductEl.value),
                name: categoryProductEl.options[categoryProductEl.selectedIndex].text
            };
            // Kiểm tra và upload ảnh nếu có
            const imageInput = document.getElementById('imageProduct');
            const selectedImage = document.getElementById('selected-image');

            if (imageInput.files.length > 0) {
                // Nếu có file upload
                const formData = new FormData();
                formData.append('file', imageInput.files[0]);
            console.log("productId",createdProduct.id);
                try {
                    // Gọi API upload poster với ID sản phẩm vừa tạo
                    const uploadResponse = await axios.post(`/api/admin/product/${createdProduct.id}/upload-poster`,
                        formData,
                        {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        }
                    );

                    // Cập nhật URL ảnh cho sản phẩm
                    createdProduct.imageUrl = uploadResponse.data.url;
                } catch (uploadError) {
                    console.error('Lỗi upload ảnh:', uploadError);
                }
            } else if (selectedImage.src) {
                // Nếu là ảnh đã chọn từ danh sách
                const imageUrl = await uploadImageFromUrl(selectedImage.src, createdProduct.id);
                if (imageUrl) {
                    createdProduct.imageUrl = imageUrl;
                }
            }

            // Thêm sản phẩm vào danh sách
            alertSuccess("Thêm sản phẩm thành công");
            products.content.unshift(createdProduct);
            renderProductList(products.content);
            modalProductObj.hide();
        }
    } catch (error) {
        if (error.response.data.message ==="User not found") {
            alertWarning("Bạn chưa đăng nhập");
        }
        console.error('Lỗi tạo sản phẩm:', error);

    }
};


const updateProduct = async () => {
    const selectedImage = document.getElementById('selected-image');
    if (!selectedImage.src || selectedImage.src === window.location.href) {
        alertWarning("Vui lòng chọn ảnh sản phẩm");
        return;
    }
    if (nameProductEl.value.trim() === "") {
        alertWarning("Vui lòng nhập tên sản phẩm");
        return;
    }
    if (brandProductEl.value.trim() === "") {
        alertWarning("Vui lòng chọn thương hiệu");
        return;
    }
    if (categoryProductEl.value.trim() === "") {
        alertWarning("Vui lòng chọn danh mục");
        return;
    }
    if (descriptionProductEl.value.trim() === "") {
        alertWarning("Vui lòng nhập mô tả sản phẩm");
        return;
    }

    const request = {
        name: nameProductEl.value,
        brand: parseInt(brandProductEl.value),
        category: parseInt(categoryProductEl.value),
        status: statusProductEl.value === 'active',
        discount: parseInt(discountProductEl.value),
        description: descriptionProductEl.value,
        isFeatured: isFeaturedProductEl.checked
    };
    console.log("request", request);

    try {
        // Cập nhật sản phẩm
        const productResponse = await axios.put(`/api/admin/product/update/${idUpdateProduct}`, request);

        // Nếu cập nhật sản phẩm thành công
        if (productResponse.status === 200) {
            const updatedProduct = productResponse.data;
            // Thêm thông tin brand và category đầy đủ
            updatedProduct.brand = {
                id: parseInt(brandProductEl.value),
                nameBrand: brandProductEl.options[brandProductEl.selectedIndex].text
            };

            updatedProduct.category = {
                id: parseInt(categoryProductEl.value),
                name: categoryProductEl.options[categoryProductEl.selectedIndex].text
            };

            // Kiểm tra và upload ảnh nếu có
            const imageInput = document.getElementById('imageProduct');
            if (imageInput.files.length > 0) {
                // Nếu có file upload
                const formData = new FormData();
                formData.append('file', imageInput.files[0]);
                console.log("productId", updatedProduct.id);
                try {
                    // Gọi API upload poster với ID sản phẩm vừa cập nhật
                    const uploadResponse = await axios.post(`/api/admin/product/${updatedProduct.id}/upload-poster`,
                        formData,
                        {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        }
                    );

                    // Cập nhật URL ảnh cho sản phẩm
                    updatedProduct.imageUrl = uploadResponse.data.url;
                } catch (uploadError) {
                    console.error('Lỗi upload ảnh:', uploadError);
                }
            } else if (selectedImage.src) {
                // Nếu là ảnh đã chọn từ danh sách
                const imageUrl = await uploadImageFromUrl(selectedImage.src, updatedProduct.id);
                if (imageUrl) {
                    updatedProduct.imageUrl = imageUrl;
                }
            }

            // Cập nhật sản phẩm trong danh sách
            alertSuccess("Cập nhật sản phẩm thành công");
            const index = products.content.findIndex(product => product.id === idUpdateProduct);
            products.content[index] = updatedProduct;
            renderProductList(products.content);
            modalProductObj.hide();
        }
    } catch (error) {
        if (error.response.data.message ==="User not found") {
            alertWarning("Bạn chưa đăng nhập");
        }
        console.error('Lỗi cập nhật sản phẩm:', error);
    }
};

const deleteProductAdmin = (id) => {
    alertConfirm("Bạn có chắc chắn muốn xóa sản phẩm này?", async () => {
        try {
            const response = await axios.delete(`/api/admin/product/delete/${id}`);
            if (response.status === 200) {
                alertSuccess("Xóa sản phẩm thành công");
                products.content = products.content.filter(product => product.id !== id);
                renderProductList(products.content);
                modalProductObj.hide();
            }
        } catch (error) {
            if (error.response.data.message ==="User not found") {
                alertWarning("Bạn chưa đăng nhập");
            }
            console.error('Lỗi xóa sản phẩm:', error);
        }
    });
}


renderProductList(products.content);