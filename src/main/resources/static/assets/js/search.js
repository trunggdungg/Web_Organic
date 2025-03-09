document.getElementById("search-input").addEventListener("input", function () {
    let query = this.value.trim();
    let resultBox = document.getElementById("search-results");

    if (query.length === 0) {
        resultBox.innerHTML = "";
        resultBox.classList.add("hidden");
        return;
    }

    // Gửi AJAX request đến server
    fetch("/api/search-products?query=" + encodeURIComponent(query))
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                resultBox.innerHTML = data.map(product => `
                <div class="p-2 hover:bg-gray-100">
                    <a href="/product/${product.id}/${product.slug}" class="flex items-center space-x-3">
                        <img src="${product.image}" alt="${product.name}" class="w-12 h-12 rounded">
                        <div>
                            <p class="font-semibold">${product.name}</p>
                            <p class="text-red-600">${formatPriceCustom(product.price)}₫</p>
                        </div>
                    </a>
                </div>
            `).join("");
                resultBox.classList.remove("hidden");
            } else {
                resultBox.innerHTML = "<p class='p-2 text-gray-500'>Không tìm thấy sản phẩm</p>";
                resultBox.classList.remove("hidden");
            }
        });

// Hàm format số theo kiểu Thymeleaf: #numbers.formatDecimal(product.price, 0, 'COMMA', 0, 'POINT')
    function formatPriceCustom(price) {
        if (!price) return "0"; // Tránh lỗi nếu price bị null hoặc undefined
        let intPrice = Math.floor(price); // Chuyển về số nguyên (bỏ phần thập phân)
        return intPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }



});