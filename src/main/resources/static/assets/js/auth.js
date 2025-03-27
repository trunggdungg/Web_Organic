const passWordCurrentEl = document.getElementById('passWordCurrent');
const passWordNewEl = document.getElementById('passWordNew');
const passWordConfirmEl = document.getElementById('passWordNewConfirm');
const btnSubmitEl = document.getElementById('btnConfirm');


btnSubmitEl.addEventListener('click', async () => {
    if (passWordCurrentEl.value.trim() === "") {
        alertWarning("Vui lòng nhập mật khẩu hiện tại");
        return;
    }
    if (passWordNewEl.value.trim() === "") {
        alertWarning("Vui lòng nhập mật khẩu mới");
        return;
    }
    if (passWordConfirmEl.value.trim() === "") {
        alertWarning("Vui lòng xác nhận mật khẩu mới");
        return;
    }
    if (passWordCurrentEl.value === passWordNewEl.value) {
        alertWarning("Mật khẩu mới không được trùng với mật khẩu cũ");
        return;
    }
    if (passWordNewEl.value !== passWordConfirmEl.value) {
        alertWarning("Mật khẩu mới không khớp");
        return;
    }

    const request = {
        passwordCurrent: passWordCurrentEl.value,
        passwordNew: passWordNewEl.value
    }
// console.log("request", request);
    try {
        const response = await axios.put("/api/auth/change-password", request);
        if (response.status === 200) {
            alertSuccess("Đổi mật khẩu thành công");
            passWordCurrentEl.value = "";
            passWordNewEl.value = "";
            passWordConfirmEl.value = "";
        }
    } catch (error) {
        alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
        console.log("error", error);
    }
});