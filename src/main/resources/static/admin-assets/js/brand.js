const brandsTableBodyEl = document.getElementById('brandsTableBody');

const renderBrandList = (brands) => {
    let html = "";
    brands.forEach(brand => {
        html += `
            <tr data-brand-id="${brand.id}">
                <td class="align-middle">
                    <img src="${brand.logo}" alt="Brand Logo" style="height: 100px;width: 100px" class="brand-logo-preview " />
                </td>
                <td class="align-middle">${brand.nameBrand}</td>
                <td class="align-middle">
                    <span class="${brand.status ? 'status-active' : 'status-inactive'}">
                        ${brand.status ? 'Hoạt động' : 'Không hoạt động'}
                    </span>
                </td>
                <td class="align-middle">
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-warning" onclick="openModalUpdateBrand(${brand.id})" title="Sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteBrand(${brand.id})" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
    brandsTableBodyEl.innerHTML = html;
}
const brandModalEl = new bootstrap.Modal(document.getElementById('addBrandModal'), {
    keyboard: false
});

const btnAddBrandEl = document.getElementById('btnAddBrand');
const brandNameEl = document.getElementById('nameBrand');
const brandLogoEl = document.getElementById('imageBrand');
const brandStatusEl = document.getElementById('statusBrand');
const submitFormBrandEl = document.getElementById('submitBrandForm');

let idUpdateBrand = null;
btnAddBrandEl.addEventListener('click', () => {
    brandModalEl.show();
    brandNameEl.value = "";
    brandLogoEl.value = "";
    brandStatusEl.value = "";
    idUpdateBrand = null;
});

const openModalUpdateBrand = (id) => {
    idUpdateBrand = id;
    const brand = brands.find(brand => brand.id === id);

    // Cập nhật tên thương hiệu
    brandNameEl.value = brand.nameBrand;

    // Hiển thị logo hiện tại trong modal
    const logoPreview = document.getElementById("logoPreview");
    if (brand.logo) {
        logoPreview.src = brand.logo;  // Đặt src của logo hiện tại
        logoPreview.classList.remove("d-none");  // Hiển thị logo
    } else {
        logoPreview.classList.add("d-none");  // Ẩn logo nếu không có
    }

    // Cập nhật trạng thái
    brandStatusEl.value = brand.status ? 'true' : 'false';

    // Hiển thị modal
    brandModalEl.show();
}

submitFormBrandEl.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (idUpdateBrand) {
        updateBrand();
    } else {
        createBrand();
    }
});

const createBrand = async () => {
    if (brandNameEl.value.trim() === "") {
        alertWarning("Vui lòng nhập tên thương hiệu");
        return;
    }
    if (brandLogoEl.value.trim() === "") {
        alertWarning("Vui lòng chọn logo thương hiệu");
        return;
    }
    if (brandStatusEl.value.trim() === "") {
        alertWarning("Vui lòng chọn trạng thái thương hiệu");
        return;
    }
    const formData = new FormData();
    formData.append('nameBrand', brandNameEl.value);
    formData.append('logo', brandLogoEl.files[0]);
    formData.append('status', brandStatusEl.value);

   try{
      const response = await axios.post('/api/admin/brand/create',formData);
          if (response.status === 200) {
              alertSuccess("Thêm thương hiệu thành công");

              brands.unshift(response.data);
              renderBrandList(brands);
              brandModalEl.hide();
          }
   }catch (error) {
       console.error("Error creating brand:", error);
       alertError("Đã xảy ra lỗi khi tạo thương hiệu");
   }

}

const updateBrand = async () => {
    if (brandNameEl.value.trim() === "") {
        alertWarning("Vui lòng nhập tên thương hiệu");
        return;
    }
    if (brandLogoEl.value.trim() === "") {
        alertWarning("Vui lòng chọn logo thương hiệu");
        return;
    }
    if (brandStatusEl.value.trim() === "") {
        alertWarning("Vui lòng chọn trạng thái thương hiệu");
        return;
    }
    const formData = new FormData();
    formData.append('nameBrand', brandNameEl.value);
    formData.append('logo', brandLogoEl.files[0]);
    formData.append('status', brandStatusEl.value);

   try{
      const response = await axios.put(`/api/admin/brand/${idUpdateBrand}/update`,formData);
          if (response.status === 200) {
              alertSuccess("Cập nhật thương hiệu thành công");

              const index = brands.findIndex(brand => brand.id === idUpdateBrand);
              brands[index] = response.data;
              renderBrandList(brands);
              brandModalEl.hide();
          }
   }catch (error) {
       console.error("Error updating brand:", error);
       alertError("Đã xảy ra lỗi khi cập nhật thương hiệu");
   }

}

const deleteBrand = async (id) => {
  alertConfirm("Bạn có chắc chắn muốn xóa thương hiệu này không?", async () => {
        try {
            const response = await axios.delete(`/api/admin/brand/${id}/delete`);
            if (response.status === 200) {
                alertSuccess("Xóa thương hiệu thành công");
                brands = brands.filter(brand => brand.id !== id);
                renderBrandList(brands);
            }
        } catch (error) {
            console.error("Error deleting brand:", error);
            alertError("Đã xảy ra lỗi khi xóa thương hiệu");
        }
  })
}

renderBrandList(brands);