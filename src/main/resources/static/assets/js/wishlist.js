// lây danh sách yêu thích của header
function loadWishlist() {
    fetch("/api/wishlist")
        .then(response => response.json())
        .then(products => {
            const wishlistContainer = document.getElementById("wishlist-container");
            wishlistContainer.innerHTML = "";

            if (products.length === 0) {
                wishlistContainer.innerHTML = "<p class='text-gray-500'>Danh sách yêu thích trống.</p>";
                return;
            }

            products.forEach(product => {
                const productHTML = `
                    <li class="flex items-center space-x-3">
                        <a href="#" class="flex items-center space-x-3 flex-1 no-underline">
                            <img class="w-16 h-16 object-cover rounded-md" src="${product.imageUrl}" alt="${product.name}">
                            <span class="line-clamp-2 text-gray-700 font-medium hover:underline">${product.name}</span>
                        </a>

                        <button class="heart-button bg-white p-2 rounded-full shadow-md active" onclick="toggleHeart(this, ${product.id})">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-500" fill="currentColor" viewBox="0 0 24 24"
                                stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                            </svg>
                        </button>
                    </li>
                `;
                wishlistContainer.innerHTML += productHTML;
            });
        })
        .catch(error => console.error("Lỗi khi tải danh sách yêu thích:", error));
}

// Gọi `loadWishlist()` khi trang tải xong
document.addEventListener("DOMContentLoaded", loadWishlist);


//
// hàm click trái tim toggle
function toggleHeart(button, productId) {
    // Toggle trạng thái trước để có hiệu ứng nhanh
    button.classList.toggle("active");
    const svg = button.querySelector('svg');
    if (svg) {
        svg.setAttribute('fill', button.classList.contains('active') ? 'currentColor' : 'none');
    }

    // Gọi API cập nhật wishlist
    fetch(`/api/wishlist/toggle/${productId}`, {
        method: 'POST',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to update wishlist");
            }
            return response.json();
        })
        .then(() => {
            alertSuccess("Đã cập nhật danh sách yêu thích!");
            // Load lại danh sách yêu thích sau khi toggle
            loadWishlist();
        })
        .catch(error => {
            console.error("Error updating wishlist:", error);
            alertError("Vui lòng đăng nhập!");

            // Nếu lỗi, revert lại trạng thái
            button.classList.toggle("active");
            if (svg) {
                svg.setAttribute('fill', button.classList.contains('active') ? 'currentColor' : 'none');
            }
        });
}


// Gọi API để cập nhật trạng thái yêu thích
document.addEventListener("DOMContentLoaded", function () {
    // Fetch the user's wishlist
    fetch("/api/wishlist")
        .then(response => response.json())
        .then(wishlistProducts => {
            // Tạo một set chứa id của các sản phẩm trong wishlist
            const wishlistProductIds = new Set(wishlistProducts.map(product => product.id));

            // Thêm class active cho các nút heart của sản phẩm trong wishlist
            document.querySelectorAll('.heart-button').forEach(button => {
                const productId = button.getAttribute('data-product-id');

                // Nếu sản phẩm có trong wishlist thì thêm class active
                if (wishlistProductIds.has(parseInt(productId))) {
                    button.classList.add('active');
                    // Cập nhật màu cho icon heart
                    const svg = button.querySelector('svg');
                    if (svg) {
                        svg.setAttribute('fill', 'currentColor');
                    }
                }
            });
        })
        .catch(error => console.error("Error loading wishlist data:", error));
});