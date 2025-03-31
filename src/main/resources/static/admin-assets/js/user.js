const userListEl = document.getElementById('user-list');
const renderUserList = (users) => {
    let html = "";
    users.forEach(user => {
        html += `
                <tr data-user-id="${user.id}">
                    <td><input type="checkbox" class="form-check-input user-select"></td>
                    <td>
                        <img src="${user.avatar}" class="rounded-circle" width="40" height="40" alt="Avatar">
                    </td>
                    <td>${user.fullName}</td>
                    <td>${user.email}</td>
                    <td>${user.password.length > 10 ? user.password.substring(0, 20) + '...' : user.password}</td>
                    <td>
                        ${user.role.toUpperCase() === 'ADMIN' ? 'Quản trị' :
                        user.role.toUpperCase() === 'USER' ? 'Người dùng' :
                            user.role.toUpperCase() === 'STAFF' ? 'Nhân viên' : 'Unknown'}
                    </td>
                    <td>
                        <span class="${user.isActivated ? 'status-active' : 'status-inactive'}">
                            ${user.isActivated ? 'Hoạt động' : 'Vô hiệu hóa'}
                        </span>
                    </td>
                    <td>2024-03-15</td>
                    <td>
                        <div class="d-flex gap-2">
                            <button class="btn btn-sm btn-info" onclick="openModalUpdateUser(${user.id})" title="Chỉnh sửa">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})" title="Xóa">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
    `;
    });
    userListEl.innerHTML = html;
}


const modalUserObj = new bootstrap.Modal(document.getElementById('new-user-modal'), {
    keyboard: false
});

const btnAddUserEl = document.getElementById('btn-add-user');
const userModalLabelEl = document.getElementById('userModalLabel');
console.log("userModalLabelEl",userModalLabelEl);
const nameUserEl = document.getElementById('nameUser');
const emailUserEl = document.getElementById('emailUser');
const roleUserEl = document.getElementById('roleUser');
const statusUserEl = document.getElementById('status');
const passwordUserEl = document.getElementById('password');
const confirmPasswordUserEl = document.getElementById('confirm-password');
const submitFormUserEl = document.getElementById('btn-submit');
// document.getElementById("btn-cancel").addEventListener("click", function (event) {
//     event.preventDefault(); // Ngăn chặn form bị submit
// });

let idUpdateUser = null;

btnAddUserEl.addEventListener('click', () => {
   userModalLabelEl.innerText = "Thêm người dùng mới";
    nameUserEl.value = "";
    emailUserEl.value = "";
    roleUserEl.value = "";
    statusUserEl.value = "";
    passwordUserEl.value = "";
    confirmPasswordUserEl.value = "";
    idUpdateUser = null;
})

const openModalUpdateUser = (id) => {
    idUpdateUser = id;
    const user = users.content.find(user => user.id === id);
    nameUserEl.value = user.fullName;
    emailUserEl.value = user.email;
    roleUserEl.value = user.role;
    passwordUserEl.value = "";
    confirmPasswordUserEl.value = "";
    statusUserEl.value = user.isActivated;
    userModalLabelEl.innerText = "Cập nhật người dùng";
    modalUserObj.show();
    idUpdateUser = id;
}

submitFormUserEl.addEventListener('click', async (e) => {
    e.preventDefault();
    if (idUpdateUser){
        updateUser();
    }else {
        createUser();
    }
})


const createUser = async () => {
    if (nameUserEl.value.trim() === "") {
        alertWarning("Vui lòng nhập họ tên");
        return;
    }

    if (emailUserEl.value.trim() === "") {
        alertWarning("Vui lòng nhập email");
        return;
    }
    if (passwordUserEl.value.trim() === "") {
        alertWarning("Vui lòng nhập mật khẩu");
        return;
    }
    if (confirmPasswordUserEl.value.trim() === "") {
        alertWarning("Vui lòng xác nhận mật khẩu");
        return;
    }
    if (passwordUserEl.value.trim() !== confirmPasswordUserEl.value.trim()) {
        alertWarning("Mật khẩu không khớp");
        return;
    }

    const request = {
        fullName: nameUserEl.value,
        email: emailUserEl.value,
        role: roleUserEl.value,
        isActive: statusUserEl.value === 'true',
        password: passwordUserEl.value
    }
    console.log("user",request);

    try {
        const response = await axios.post('/api/admin/user/create', request);
        if (response.status === 200) {
            alertSuccess("Thêm người dùng thành công");
            users.content.unshift(response.data);
            renderUserList(users.content);
            modalUserObj.hide();

        }
    }catch (error) {
        console.log("Error response:", error.response); // Kiểm tra log chi tiết

        if (error.response) {
            // Kiểm tra mã lỗi
            if (error.response.status === 401) {
                alertWarning(error.response.data.message || "Bạn chưa đăng nhập");
            } else if (error.response.status === 500) {
                alertWarning(error.response.data.message || "Bạn không có quyền thực hiện chức năng này");
            } else if (error.response.status === 400) {
                alertWarning( "Email người dùng đã tồn tại");
            } else {
                alertWarning("Đã xảy ra lỗi, vui lòng thử lại!");
            }
        } else {
            alertWarning("Không thể kết nối đến server!");
        }
    }


}

const updateUser = async () => {
    if (nameUserEl.value.trim() === "") {
        alertWarning("Vui lòng nhập họ tên");
        return;
    }
    if (emailUserEl.value.trim() === "") {
        alertWarning("Vui lòng nhập email");
        return;
    }
    // if (passwordUserEl.value.trim() === "") {
    //     alertWarning("Vui lòng nhập mật khẩu");
    //     return;
    // }
    // if (confirmPasswordUserEl.value.trim() === "") {
    //     alertWarning("Vui lòng xác nhận mật khẩu");
    //     return;
    // }
    if (passwordUserEl.value.trim() !== confirmPasswordUserEl.value.trim()) {
        alertWarning("Mật khẩu không khớp");
        return;
    }
    const request = {
        fullName: nameUserEl.value,
        email: emailUserEl.value,
        role: roleUserEl.value,
        isActive: statusUserEl.value === 'true',
        password: passwordUserEl.value
    }
    console.log("user",request);

    try {
        const response =await axios.put(`/api/admin/user/update/${idUpdateUser}`,request);
        console.log("Kết quả response:", response);
        if (response.status === 200) {
            alertSuccess("Cập nhật người dùng thành công");
            const index = users.content.findIndex(user => user.id === idUpdateUser);
            users.content[index] = response.data;
            renderUserList(users.content);
            modalUserObj.hide();
        }
    }catch (e){
        if (e.response && e.response.status === 500) {
            alertWarning("Bạn không có quyền cập nhật người dùng");
        } else if (e.response && e.response.status === 400) {
            alertWarning("Email người dùng đã tồn tại");
        } else {
            alertWarning("Đã xảy ra lỗi, vui lòng thử lại!");
        }
        console.log("error", e);
    }
}


function deleteUser(id) {
    alertConfirm("Bạn có chắc chắn muốn xóa người dùng này?", async () => {
        try {
            const response = await axios.delete(`/api/admin/user/delete/${id}`);
            if (response.status === 200) {
                alertSuccess("Xóa người dùng thành công");
                users.content = users.content.filter(user => user.id !== id);
                renderUserList(users.content);
                modalUserObj.hide();
            }
        } catch (error) {
            if (error.response) {
                // Kiểm tra HTTP status từ server
                if (error.response.status === 401) {
                    alertError("Bạn chưa đăng nhập!");
                } else if (error.response.status === 500) {
                    alertError("Bạn không có quyền xóa người dùng!");
                } else {
                    alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
                }
            } else {
                // Nếu không có phản hồi từ server
                alertError("Không thể kết nối đến máy chủ!");
            }
            console.error("Lỗi khi xóa người dùng:", error);
        }
    });
}

renderUserList(users.content);