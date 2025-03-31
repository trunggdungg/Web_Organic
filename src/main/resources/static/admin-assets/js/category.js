const categoryListEl = document.getElementById('category-list');
const renderCategoryList = (categories) => {
    let html = "";
    categories.forEach(category => {
        html += `
            <tr data-category-id="${category.id}">
                <td><input type="checkbox" class="form-check-input category-select"></td>
                <td>${category.id}</td>
                <td>${category.name}</td>
                <td>${category.slug}</td>
                <td>
                    ${category.type.toUpperCase() === 'PRODUCT' ? 'Sản phẩm' :
                    category.type.toUpperCase() === 'BLOG' ? 'Bài viết' : 'Unknown'}
                </td>
                <td>
                    <span class="${category.status ? 'status-active' : 'status-inactive'}">
                        ${category.status ? 'Hoạt động' : 'Vô hiệu hóa'}
                    </span>
                </td>
                <td>${new Date(category.createdAt).toLocaleDateString('vi-VN', { day: '2-digit', month: 'short', year: 'numeric' })}</td>
                <td>${new Date(category.updatedAt).toLocaleDateString('vi-VN', { day: '2-digit', month: 'short', year: 'numeric' })}</td>
                <td>
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-info" onclick="openModalUpdateCategory(${category.id})" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteCategory(${category.id})" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
    categoryListEl.innerHTML = html;
}


const modalCategoryObj = new bootstrap.Modal(document.getElementById('categoryModal'), {
    keyboard: false
});

const btnAddCategoryEl = document.getElementById('btn-add-modal');
const categoryModalLabelEl = document.getElementById('categoryModalLabel');
const nameCategoryEl = document.getElementById('nameCategory');
const typeCategoryEl = document.getElementById('typeCategory');
const statusCategoryEl = document.getElementById('statusCategory');
const submitFormCategoryEl = document.getElementById('btn-save-category');
let idUpdateCategory = null;

btnAddCategoryEl.addEventListener('click', () => {
    categoryModalLabelEl.innerText = "Thêm danh mục mới";
    nameCategoryEl.value = "";

    typeCategoryEl.value = "";
    statusCategoryEl.value = "";
    idUpdateCategory = null;
});

const openModalUpdateCategory = (id) => {
    idUpdateCategory = id;
    const category = categories.find(category => category.id === id);
    nameCategoryEl.value = category.name;

    typeCategoryEl.value = category.type;
    statusCategoryEl.value = category.status ? 'true' : 'false';
    categoryModalLabelEl.innerText = "Cập nhật danh mục";
    modalCategoryObj.show();
    idUpdateCategory = id;
};

submitFormCategoryEl.addEventListener('click', async (e) => {
    e.preventDefault();
    if (idUpdateCategory) {
        updateCategory();
    } else {
        createCategory();
    }
});

const createCategory = async () => {
    if (nameCategoryEl.value.trim() === "") {
        alertWarning("Vui lòng nhập tên danh mục");
        return;
    }
    if (typeCategoryEl.value.trim() === "") {
        alertWarning("Vui lòng chọn loại danh mục");
        return;
    }
    if (statusCategoryEl.value.trim() === "") {
        alertWarning("Vui lòng chọn trạng thái");
        return;
    }


    const request = {
        name: nameCategoryEl.value,

        type: typeCategoryEl.value,
        status: statusCategoryEl.value === 'true'
    };


    try {
        const response = await axios.post('/api/admin/category/create', request);
        if (response.status === 200) {
            alertSuccess("Thêm danh mục thành công");
            categories.unshift(response.data);
            renderCategoryList(categories);
            modalCategoryObj.hide();
        }
    } catch (error) {
        if (error.response) {
            console.log("Error response:", error.response);

            if (error.response.data.message === "User not logged in") {
                alertWarning("Bạn chưa đăng nhập!");
            } else if (error.response.data.message === "You do not have permission to perform this action") {
                alertWarning("Bạn không có quyền thực hiện chức năng này");
            } else if (error.response.data.message === "Category already exists") {
                alertWarning("Danh mục đã tồn tại");
            } else {
                alertWarning("Đã có lỗi xảy ra, vui lòng thử lại sau");
            }
        } else {
            alertWarning("Không thể kết nối đến server!");
        }
    }
};

const updateCategory = async () => {
    if (nameCategoryEl.value.trim() === "") {
        alertWarning("Vui lòng nhập tên danh mục");
        return;
    }
    if (typeCategoryEl.value.trim() === "") {
        alertWarning("Vui lòng chọn loại danh mục");
        return;
    }
    if (statusCategoryEl.value.trim() === "") {
        alertWarning("Vui lòng chọn trạng thái");
        return;
    }

    const request = {
        name: nameCategoryEl.value,

        type: typeCategoryEl.value,
        status: statusCategoryEl.value === 'active'
    };

    try {
        const response = await axios.put(`/api/admin/category/update/${idUpdateCategory}`, request);
        if (response.status === 200) {
            alertSuccess("Cập nhật danh mục thành công");
            const index = categories.findIndex(category => category.id === idUpdateCategory);
            categories[index] = response.data;
            renderCategoryList(categories);
            modalCategoryObj.hide();
        }
    } catch (error) {
        if (error.response) {
            console.log("Error response:", error.response);

            if (error.response.data.message === "User not logged in") {
                alertWarning("Bạn chưa đăng nhập!");
            } else if (error.response.data.message === "You do not have permission to perform this action") {
                alertWarning("Bạn không có quyền thực hiện chức năng này");
            } else if (error.response.data.message === "Category already exists") {
                alertWarning("Danh mục đã tồn tại");
            } else {
                alertWarning("Đã có lỗi xảy ra, vui lòng thử lại sau");
            }
        } else {
            alertWarning("Không thể kết nối đến server!");
        }
    }
};

const deleteCategory = async (id) => {
    alertConfirm("Bạn có chắc chắn muốn xóa người dùng này?", async () => {

        try {
            const response = await axios.delete(`/api/admin/category/delete/${id}`);
            if (response.status === 200) {
                alertSuccess("Xóa danh mục thành công");
                categories = categories.filter(category => category.id !== id);
                renderCategoryList(categories);
            }
        } catch (error) {
            if (error.response) {
                console.log("Error response:", error.response);

                if (error.response.data.message === "User not logged in") {
                    alertWarning("Bạn chưa đăng nhập!");
                } else if (error.response.data.message === "You do not have permission to perform this action") {
                    alertWarning("Bạn không có quyền thực hiện chức năng này");
                } else {
                    alertWarning("Đã có lỗi xảy ra, vui lòng thử lại sau");
                }
            } else {
                alertWarning("Không thể kết nối đến server!");
            }
        }
    });
}


renderCategoryList(categories);