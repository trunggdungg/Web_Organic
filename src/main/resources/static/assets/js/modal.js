// Get all navigation elements
const profileNav = document.getElementById('profile-nav');
const bankNav = document.getElementById('bank-nav');
const addressNav = document.getElementById('address-nav');
const passwordNav = document.getElementById('password-nav');
const ordersNav = document.getElementById('orders-nav');

// Get all content sections
const profileContent = document.getElementById('profile-content');
const bankContent = document.getElementById('bank-content');
const addressContent = document.getElementById('address-content');
const passwordContent = document.getElementById('password-content');
const ordersContent = document.getElementById('orders-content');

// Get all nav items
const navItems = [profileNav, bankNav, addressNav, passwordNav, ordersNav];

// Function to reset all nav items to default style
function resetNavStyles() {
    navItems.forEach(item => {
        item.classList.remove('text-red-500');
    });
}

// Function to hide all content sections
function hideAllContent() {
    profileContent.classList.add('hidden');
    bankContent.classList.add('hidden');
    addressContent.classList.add('hidden');
    passwordContent.classList.add('hidden');
    ordersContent.classList.add('hidden');
}

// Set up click handlers for navigation
profileNav.addEventListener('click', function() {
    resetNavStyles();
    profileNav.classList.add('text-red-500');
    hideAllContent();
    profileContent.classList.remove('hidden');
});

bankNav.addEventListener('click', function() {
    resetNavStyles();
    bankNav.classList.add('text-red-500');
    hideAllContent();
    bankContent.classList.remove('hidden');
});

addressNav.addEventListener('click', function() {
    resetNavStyles();
    addressNav.classList.add('text-red-500');
    hideAllContent();
    addressContent.classList.remove('hidden');
});

passwordNav.addEventListener('click', function() {
    resetNavStyles();
    passwordNav.classList.add('text-red-500');
    hideAllContent();
    passwordContent.classList.remove('hidden');
});

ordersNav.addEventListener('click', function() {
    resetNavStyles();
    ordersNav.classList.add('text-red-500');
    hideAllContent();
    ordersContent.classList.remove('hidden');
});

// Phone modal functionality
const phoneModal = document.getElementById('phoneModal');
const openModal = document.getElementById('openModal');
const closeModal = document.getElementById('closeModal');

openModal.addEventListener('click', function() {
    phoneModal.classList.remove('hidden');
});

closeModal.addEventListener('click', function() {
    phoneModal.classList.add('hidden');
});

// DOB modal functionality
const dobModal = document.getElementById('dobModal');
const openDobModal = document.getElementById('openDobModal');
const closeDobModal = document.getElementById('closeDobModal');

openDobModal.addEventListener('click', function() {
    dobModal.classList.remove('hidden');
});

closeDobModal.addEventListener('click', function() {
    dobModal.classList.add('hidden');
});

// New address modal functionality
const newAddressModal = document.getElementById('new-address-modal');
const addAddressBtn = document.getElementById('add-address-btn');
const backModalBtn = document.getElementById('back-modal-btn');

if (addAddressBtn) {
    addAddressBtn.addEventListener('click', function() {
        newAddressModal.classList.remove('hidden');
    });
}

if (backModalBtn) {
    backModalBtn.addEventListener('click', function() {
        newAddressModal.classList.add('hidden');
    });
}
