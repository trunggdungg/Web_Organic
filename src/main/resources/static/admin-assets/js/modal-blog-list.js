document.getElementById('selectAll').addEventListener('change', function() {
    const checkboxes = document.querySelectorAll('.blog-select');
    checkboxes.forEach(checkbox => checkbox.checked = this.checked);
    updateBulkActionsVisibility();
});

document.querySelectorAll('.blog-select').forEach(checkbox => {
    checkbox.addEventListener('change', updateBulkActionsVisibility);
});




// Reset Filters
function resetFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('categoryFilter').value = '';
    document.getElementById('statusFilter').value = '';
    applyFilters();
}

// Apply Filters
function applyFilters() {
    const search = document.getElementById('searchInput').value.toLowerCase();
    const category = document.getElementById('categoryFilter').value;
    const status = document.getElementById('statusFilter').value;

    // Implement your filter logic here
    console.log('Applying filters:', { search, category, status });
}

// Bulk Action
function bulkAction(action) {
    const selectedBlogs = Array.from(document.querySelectorAll('.blog-select:checked'))
        .map(checkbox => checkbox.closest('tr').querySelector('td:nth-child(2)').textContent);

    console.log(`Performing ${action} on blogs:`, selectedBlogs);
    // Implement your bulk action logic here
}

// Export Blogs
function exportBlogs() {
    console.log('Exporting blogs to Excel...');
    // Implement your export logic here
}

// Helper function for bulk actions visibility
function updateBulkActionsVisibility() {
    const checkedCount = document.querySelectorAll('.blog-select:checked').length;
    const bulkActions = document.querySelector('.bulk-actions');
    bulkActions.style.display = checkedCount > 0 ? 'block' : 'none';
}


// Quill Editor

const originalAddEventListener = Element.prototype.addEventListener;

Element.prototype.addEventListener = function (type, listener, options) {
    if (type === 'DOMNodeInserted') {
        return; // Bỏ qua sự kiện lỗi thời
    }
    return originalAddEventListener.call(this, type, listener, options);
};


var quill = new Quill('#editor-container', {
    theme: 'snow',
    placeholder: 'Viết nội dung blog tại đây...',
    modules: {
        toolbar: [
            [{ 'header': [1, 2, 3, 4, 5, 6, false] }],  // Tiêu đề H1 - H6
            ['bold', 'italic', 'underline', 'strike'],  // Định dạng chữ
            [{ 'color': [] }, { 'background': [] }],   // Màu sắc
            [{ 'list': 'ordered' }, { 'list': 'bullet' }],  // Danh sách số & chấm
            [{ 'indent': '-1' }, { 'indent': '+1' }],  // Thụt lề
            [{ 'align': [] }],  // Căn chỉnh văn bản
            ['blockquote', 'code-block'],  // Trích dẫn, mã code
            ['link', 'image', 'video', 'formula'],  // Chèn link, ảnh, video, công thức toán
            [{ 'font': [] }],  // Font chữ
            [{ 'size': ['small', false, 'large', 'huge'] }],  // Kích thước chữ
            ['clean']
        ]
    }
});




function previewBlog() {
    const content = quill.root.innerHTML;
    document.getElementById("previewContent").innerHTML = content;

    const modal = document.getElementById("previewModal");
    modal.style.display = "block";
    modal.classList.add("show");
    document.body.classList.add("modal-open");

    // Add backdrop
    const backdrop = document.createElement("div");
    backdrop.className = "modal-backdrop fade show";
    document.body.appendChild(backdrop);

    // Close modal when clicking outside
    modal.addEventListener("click", function(e) {
        if (e.target === modal) {
            closePreview();
        }
    });
}

function closePreview() {
    const modal = document.getElementById("previewModal");
    modal.style.display = "none";
    modal.classList.remove("show");
    document.body.classList.remove("modal-open");
    document.body.style.overflow = "";
    document.body.style.paddingRight = "";

    // Remove all backdrops
    const backdrops = document.querySelectorAll(".modal-backdrop");
    backdrops.forEach(backdrop => backdrop.remove());

    // Reset content
    document.getElementById("previewContent").innerHTML = "";
}
