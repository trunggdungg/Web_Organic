const blogTableBodyEl = document.getElementById('blog-list');

const renderBlogList = (blogs) => {
    let html = "";
    blogs.forEach(blog => {
        html += `
            <tr data-blog-id="${blog.id}">
                <td><input type="checkbox" class="form-check-input blog-select"></td>
                <td class="align-middle">
                    <img src="${blog.thumbnail}" alt="${blog.slug}" class="blog-image-preview" style="height: 100px;width: 100px">
                </td>
                <td class="align-middle">
                    <div class="fw-bold">
                        ${blog.title.length > 40 ? blog.title.substring(0, 40) + '...' : blog.title}
                    </div>
                </td>
                <td class="align-middle">${blog.category.name}</td>
                <td class="align-middle">
                    <span class="${blog.status ? 'status-active' : 'status-inactive'}">
                        ${blog.status ? 'Đã xuất bản' : 'Nháp'}
                    </span>
                </td>
                <td class="align-middle">${new Date(blog.createdAt).toLocaleDateString('vi-VN')}</td>
                <td class="align-middle">${new Date(blog.updatedAt).toLocaleDateString('vi-VN')}</td>
                <td class="align-middle">
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-warning" onclick="openModalUpdateBlog(${blog.id})" title="Sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteBlog(${blog.id})" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
    blogTableBodyEl.innerHTML = html;
}

const blogModalEl = new bootstrap.Modal(document.getElementById('blogModal'), {
    keyboard: false
});

const btnAddBlogEl = document.getElementById('btnAddBlog');
const blogTitleEl = document.getElementById('blogTitle');
const blogThumbnailEl = document.getElementById('thumbnailBlog');
const blogCategoryEl = document.getElementById('categoryBlog');
const blogStatusEl = document.getElementById('statusBlog');
const blogContentEl = document.getElementById('contentBlog');
const submitFormBlogEl = document.getElementById('formBlog');
const blogModalLabelEL = document.getElementById('blogModalLabel');
const descriptionBlogEl = document.getElementById('descriptionBlog');

let idUpdateBlog = null;
btnAddBlogEl.addEventListener('click', () => {
    blogModalEl.show();
    blogModalLabelEL.innerText = "Thêm bài viết mới";
    blogTitleEl.value = "";
    blogThumbnailEl.value = "";
    blogCategoryEl.value = "";
    blogStatusEl.value = "";
    descriptionBlogEl.value = "";
    blogContentEl.value = "";
    quill.setContents([]); // Xóa nội dung trong editor
    idUpdateBlog = null;
});
const openModalUpdateBlog = (id) => {
    idUpdateBlog = id;
    const blog = blogs.content.find(blog => blog.id === id);
    blogModalLabelEL.innerText = "Cập nhật bài viết";
    blogTitleEl.value = blog.title;
    blogCategoryEl.value = blog.category.id;
    descriptionBlogEl.value = blog.description;
    blogStatusEl.value = blog.status ? 'true' : 'false';
    // Cập nhật nội dung Quill
    quill.setContents(quill.clipboard.convert(blog.content));

    // Gán giá trị cho textarea nếu cần
    blogContentEl.value = blog.content;

    blogModalEl.show();
}
submitFormBlogEl.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (idUpdateBlog) {
        updateBlog();
    } else {
        createBlog();
    }
})

const createBlog = async () => {
    const formData = new FormData();
    formData.append('title', blogTitleEl.value);
    formData.append('thumbnail', blogThumbnailEl.files[0]);
    formData.append('categoryId', blogCategoryEl.value);
    formData.append('status', blogStatusEl.value);
    formData.append('description', descriptionBlogEl.value);
    formData.append('content', quill.root.innerHTML);
    for (let [key, value] of formData.entries()) {
        console.log(`${key}:`, value);
    }
    try{
        const response = await axios.post('/api/admin/blog/create',formData);
        if (response.status === 200) {
            alertSuccess("Thêm bài viết thành công");

            blogs.content.unshift(response.data);
            renderBlogList(blogs.content);
            blogModalEl.hide();
        }
    }catch (error) {
        console.error("Error creating blog:", error);
        alertError("Đã xảy ra lỗi khi tạo bài viết");
    }
}

const updateBlog = async () => {
    if (blogThumbnailEl.files.length === 0) {
        alertError("Vui lòng chọn ảnh thumbnail");
        return;
    }
    const formData = new FormData();

    formData.append('title', blogTitleEl.value);
    formData.append('thumbnail', blogThumbnailEl.files[0]);
    formData.append('categoryId', blogCategoryEl.value);
    formData.append('description', descriptionBlogEl.value);
    formData.append('status', blogStatusEl.value);
    formData.append('content', quill.root.innerHTML);

    try{
        const response = await axios.put(`/api/admin/blog/update/${idUpdateBlog}`,formData);
        if (response.status === 200) {
            alertSuccess("Cập nhật bài viết thành công");

            const index = blogs.content.findIndex(blog => blog.id === idUpdateBlog);
            blogs.content[index] = response.data;
            renderBlogList(blogs.content);
            blogModalEl.hide();
        }
    }catch (error) {
        console.error("Error updating blog:", error);
        alertError("Đã xảy ra lỗi khi cập nhật bài viết");
    }
}

const deleteBlog = async (id) => {
    alertConfirm("Bạn có chắc chắn muốn xóa bài viết này không?", async () => {
        try {
            const response = await axios.delete(`/api/admin/blog/delete/${id}`);
            if (response.status === 200) {
                alertSuccess("Xóa bài viết thành công");
                blogs.content = blogs.content.filter(blog => blog.id !== id);
                renderBlogList(blogs.content);
            }
        } catch (error) {
            console.error("Error deleting blog:", error);
            alertError("Đã xảy ra lỗi khi xóa bài viết");
        }
    })
}

renderBlogList(blogs.content);