const stars = document.querySelectorAll(".rating .star");
const ratingValue = document.getElementById("rating-value");

let currentRating = 0;

stars.forEach((star) => {
    star.addEventListener("mouseover", () => {
        resetStars(); // Xóa trạng thái active của tất cả các sao
        const rating = parseInt(star.getAttribute("data-rating"));
        highlightStars(rating);
    });

    star.addEventListener("mouseout", () => {
        resetStars();
        highlightStars(currentRating); // Khi rời chuột, giữ trạng thái của đánh giá hiện tại
    });

    star.addEventListener("click", () => {
        currentRating = parseInt(star.getAttribute("data-rating"));
        ratingValue.textContent = `Bạn đã đánh giá ${currentRating} sao.`;
        highlightStars(currentRating);
    });
});

function highlightStars(rating) {
    stars.forEach((star) => {
        const starRating = parseInt(star.getAttribute("data-rating"));
        const icon = star.querySelector("i"); // Lấy thẻ <i> bên trong

        if (starRating <= rating) {
            icon.style.color = "#FFD700"; // Màu vàng khi chọn
        } else {
            icon.style.color = "#cad1dd"; // Màu xám nhạt khi chưa chọn
        }
    });
}
function resetStars() {
    stars.forEach((star) => {
        const icon = star.querySelector("i");
            icon.style.color = "#cad1dd"; // Màu xám nhạt khi chưa chọn
    });
}
const fetchCurrentUser = async () => {
    try {
        const response = await axios.get('/api/auth/current-user');
        sessionStorage.setItem("CURRENT_USER", JSON.stringify(response.data));
        // console.log("User from session:", response.data);
    } catch (error) {
        console.warn("Chưa đăng nhập hoặc session hết hạn!");
        sessionStorage.removeItem("CURRENT_USER");
    }
};

const reviewListEl = document.querySelector(".review-list");
const renderReview = (reviews) => {
    // console.log("reviews",reviews);
    let html = "";
    const currentUser =JSON.parse(sessionStorage.getItem("CURRENT_USER"));
    // console.log("currentUser",currentUser);
    reviews.forEach(review =>{
        const isCurrentUser = currentUser && currentUser.id === review.user.id;
        // console.log("isCurrentUser",isCurrentUser);
        html +=`
        <div class="rating-item d-flex position-relative" th:each="review : ${reviews}">
                <div class="rating-avatar position-absolute" style="top: 3px; left: 5px;">
                    <img src="${review.user.avatar}" alt="${review.user.fullName}">
                </div>
                <div class="rating-info ms-3" style="margin-left: 80px !important;">
                    <div class="d-flex">
                        <p class="rating-name" >${review.user.fullName}</p>
                    </div>

                    <div class="d-flex">
                        <p class="rating-time" >
                           ${formatDate(review.createdAt)}</p>
                     <p class="time ms-2" >${formatTime(review.createdAt)}</p>
                    </div>
                    <div class="rating-star">
                         ${[...Array(5)].map((_, i) =>
                            `<i class="${i < review.rating ? 'fa fa-star' : 'fa fa-star empty-star'}"></i>`
                        ).join("")}
                    </div>

                    <p class="rating-content">${review.content}</p>
                    <span class="show-more" onclick="toggleExpand(this.previousElementSibling)">Xem thêm</span>

                   <div  ${!isCurrentUser ? 'style="display:none;"' : ''}>
                        <button onclick="openModalUpdateReview(${review.id})"
                            class="text-primary border-0 bg-transparent text-decoration-underline me-1">Sửa
                        </button>
                        <button onclick="deleteReview(${review.id})"
                                class="text-danger border-0 bg-transparent text-decoration-underline me-1">Xóa
                        </button>
                    </div>
                </div>
            </div>
        `
    })
    reviewListEl.innerHTML = html;
}
const formatDate = dateString => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = ("0" + (date.getMonth() + 1)).slice(-2) ; // 09 -> 09 , 011 -> 11
    const day = ("0" + date.getDate()).slice(-2);
    return `${day}/${month}/${year}`;
}

const formatTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
};

const render = () => {
    $('#review-pagination').pagination({
        dataSource: reviews,
        pageSize: 5,
        callback: function(data, pagination) {
            renderReview(data);
        }
    })
}


const formReviewEl = document.getElementById("form-review");
const reviewContentEl = document.getElementById("review-content");
const modalReviewEl = document.getElementById('modalReview');
const titleModalReviewEl = document.querySelector('#modalReview .modal-title');
const btnSubmitReviewEl = document.getElementById("btn-submit");
const modalReviewObj = new bootstrap.Modal('#modalReview', {
    keyboard: false
});

let idUpdate = null;

modalReviewEl.addEventListener('hidden.bs.modal', event => {
    resetStars();
    currentRating = 0;
    ratingValue.innerHTML = "Vui lòng chọn đánh giá";
    reviewContentEl.value = "";
    titleModalReviewEl.innerHTML = "Tạo bình luận";
    btnSubmitReviewEl.innerHTML = "Tạo bình luận";
    idUpdate = null;
})

const openModalUpdateReview = (id) => {
    const review = reviews.find(review => review.id === id);
    currentRating = review.rating;
    highlightStars(currentRating);
    ratingValue.innerHTML = `Bạn đã đánh giá ${currentRating} sao.`;
    reviewContentEl.value = review.content;
    titleModalReviewEl.innerHTML = "Cập nhật bình luận";
    btnSubmitReviewEl.innerHTML = "Cập nhật bình luận";
    modalReviewObj.show();
    idUpdate = id;
};

formReviewEl.addEventListener("submit", (e) => {
    e.preventDefault();
    if (idUpdate) {
        updateReview();
    } else {
        createReview();
    }
})
// render();

const createReview = async () => {
    if (currentRating===0) {
        alertWarning("Vui lòng chọn số sao đánh giá");
        return;
    }
    if (reviewContentEl.value.trim() === "") {
        alertWarning("Vui lòng nhập nội dung đánh giá");
        return;
    }

    if (!sessionStorage.getItem("CURRENT_USER")) {
        alertWarning("Vui lòng đăng nhập để đánh giá");
        return;
    }

    const request ={
        rating: currentRating,
        content: reviewContentEl.value,
        productId: product.id
    }
    console.log("request",request);
    try {
        const response = await axios.post("/api/review/create", request);
        if (response.status === 200) {
            alertSuccess("Đánh giá của bạn đã được gửi thành công");
            reviews.unshift(response.data);
            render();
            modalReviewObj.hide();
        }
    }catch (error) {
        alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
        console.log("error",error);
    }
}

const updateReview =async () => {
    if (currentRating===0) {
        alertWarning("Vui lòng chọn số sao đánh giá");
        return;
    }
    if (reviewContentEl.value.trim() === "") {
        alertWarning("Vui lòng nhập nội dung đánh giá");
        return;
    }

    if (!sessionStorage.getItem("CURRENT_USER")) {
        alertWarning("Vui lòng đăng nhập để đánh giá");
        return;
    }

    const request ={
        rating: currentRating,
        content: reviewContentEl.value,

    }
    try {
        const response = await axios.put(`/api/review/update/${idUpdate}`, request);
        if (response.status === 200) {
            alertSuccess("Đánh giá của bạn đã được cập nhật thành công");
            const index = reviews.findIndex(review => review.id === idUpdate);
            reviews[index] = response.data;
            render();
            modalReviewObj.hide();
        }
    }catch (error) {
        alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
        console.log("error",error);
    }
}
const deleteReview = async (id) => {
    alertConfirm("Bạn có chắc chắn muốn xóa bình luận này?", async () => {
        try {
            const response = await axios.delete(`/api/review/delete/${id}`);
            if (response.status === 200) {
                alertSuccess("Xóa bình luận thành công");
                reviews = reviews.filter(review => review.id !== id);
                render();
            }
        } catch (error) {
            alertError("Đã có lỗi xảy ra, vui lòng thử lại sau");
            console.log("error", error);
        }
    });
};

fetchCurrentUser().then(() => {
    render();
});
render();
// Gọi hàm ngay khi trang load

